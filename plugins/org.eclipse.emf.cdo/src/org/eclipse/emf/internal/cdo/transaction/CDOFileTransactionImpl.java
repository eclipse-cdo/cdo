/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOFileTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Internal file-backed transaction implementation.
 *
 * @author Eike Stepper
 * @since 4.31
 */
public final class CDOFileTransactionImpl extends DelegatingCDOTransactionImpl implements CDOFileTransaction
{
  private final File file;

  private boolean dirty;

  private final CDOTransactionHandler delegateTransactionHandler = new CDOTransactionHandler()
  {
    @Override
    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      setDirty(true);
    }

    @Override
    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
      setDirty(true);
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      setDirty(true);
    }

    @Override
    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
    }
  };

  private final IRegistry<String, Object> properties = new HashMapRegistry<>()
  {
    @Override
    public void setAutoCommit(boolean autoCommit)
    {
      throw new UnsupportedOperationException();
    }
  };

  public CDOFileTransactionImpl(CDOTransaction delegate, File file, boolean reconstructSavepoints) throws IOException
  {
    super(delegate);
    if (file.isDirectory())
    {
      throw new IllegalArgumentException("Not a file: " + file.getAbsolutePath());
    }

    this.file = file;
    boolean delegateWasDirty = delegate.isDirty();
    delegate.addTransactionHandler(delegateTransactionHandler);

    if (file.exists())
    {
      InputStream in = null;
      try
      {
        in = new FileInputStream(file);
        delegate.importChanges(in, reconstructSavepoints);
      }
      finally
      {
        IOUtil.close(in);
      }
    }

    dirty = delegateWasDirty;
  }

  @Override
  public File getFile()
  {
    return file;
  }

  @Override
  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  @Override
  public boolean isDirty()
  {
    return dirty || delegate.isDirty();
  }

  protected void setDirty(boolean dirty)
  {
    if (this.dirty != dirty)
    {
      this.dirty = dirty;

      if (dirty)
      {
        fireEvent(new CDOTransactionStartedEvent()
        {
          @Override
          public CDOView getSource()
          {
            return CDOFileTransactionImpl.this;
          }
        });
      }
      else
      {
        fireEvent(new CDOTransactionFinishedEvent()
        {
          @Override
          public CDOView getSource()
          {
            return CDOFileTransactionImpl.this;
          }

          @Override
          @Deprecated
          public Type getType()
          {
            return Type.COMMITTED;
          }

          @Override
          public Cause getCause()
          {
            return Cause.COMMITTED;
          }

          @Override
          public Map<CDOID, CDOID> getIDMappings()
          {
            return Collections.emptyMap();
          }
        });
      }
    }
  }

  @Override
  public CDOCommitInfo commit() throws CommitException
  {
    return commit(null);
  }

  @Override
  public CDOCommitInfo commit(IProgressMonitor monitor) throws CommitException
  {
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(file);
      delegate.exportChanges(out);
      setDirty(false);
      return null;
    }
    catch (Exception ex)
    {
      throw new CommitException("A problem occurred while exporting changes to " + file.getAbsolutePath(), ex);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  @Override
  public void rollback()
  {
    throw new UnsupportedOperationException("Rollback not supported for file transactions");
  }

  @Override
  public void push() throws CommitException
  {
    push(null);
  }

  @Override
  public void push(IProgressMonitor monitor) throws CommitException
  {
    delegate.commit(monitor);
    file.delete();
    setDirty(false);
  }

  @Override
  public void close()
  {
    delegate.removeTransactionHandler(delegateTransactionHandler);

    try
    {
      delegate.close();
    }
    finally
    {
      detachDelegateListener();
    }
  }

  @Override
  public <T> CommitResult<T> commit(Callable<T> callable, java.util.function.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception
  {
    throw unsupportedCommit();
  }

  @Override
  public <T> CommitResult<T> commit(Callable<T> callable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException, Exception
  {
    throw unsupportedCommit();
  }

  @Override
  public CDOCommitInfo commit(Runnable runnable, java.util.function.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException
  {
    throw unsupportedCommit();
  }

  @Override
  public CDOCommitInfo commit(Runnable runnable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    throw unsupportedCommit();
  }

  @Override
  public CDOCommitInfo commitAndClose(IProgressMonitor monitor, boolean keepOpenAfterCommitProblem) throws CommitException
  {
    throw unsupportedCommit();
  }

  private UnsupportedOperationException unsupportedCommit()
  {
    return new UnsupportedOperationException("File transactions export changes locally; use push() to commit to the repository");
  }

  @Override
  @Deprecated
  public <T> CommitResult<T> commit(Callable<T> callable, org.eclipse.net4j.util.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception
  {
    throw unsupportedCommit();
  }

  @Override
  @Deprecated
  public CDOCommitInfo commit(Runnable runnable, org.eclipse.net4j.util.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException
  {
    throw unsupportedCommit();
  }
}
