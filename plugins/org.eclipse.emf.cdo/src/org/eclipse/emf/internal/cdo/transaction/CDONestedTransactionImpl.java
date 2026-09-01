/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.transaction.CDONestedTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionScope;
import org.eclipse.emf.cdo.transaction.CDOTransactionScopeClosedEvent;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.net4j.util.event.IEvent;

import java.util.concurrent.Callable;

/**
 * Transaction facade for exactly one open transaction scope.
 * <p>
 * This facade deliberately exposes ordinary view and transaction inspection operations of its containing root
 * transaction while making every repository commit operation fail. It therefore shares the root transaction's view,
 * object identities, object cache, resource set, dirty state, locks, and repository session.
 * <p>
 * A scope can only be accepted with {@link CDOTransactionScope#commit()} or rolled back with
 * {@link CDOTransactionScope#rollback()}; accepting a scope does not persist changes or manufacture repository
 * commit information.
 *
 * @author Eike Stepper
 * @since 4.30
 */
public final class CDONestedTransactionImpl extends DelegatingCDOTransactionImpl implements CDONestedTransaction
{
  private final CDOTransactionScope scope;

  public CDONestedTransactionImpl(CDOTransactionScope scope)
  {
    super(scope.getTransaction());
    this.scope = scope;
  }

  @Override
  public CDOTransactionScope getScope()
  {
    return scope;
  }

  @Override
  public CDOTransactionScope openScope()
  {
    return scope.openScope();
  }

  @Override
  protected synchronized void firstListenerAdded()
  {
    if (!isClosed())
    {
      super.firstListenerAdded();
    }
  }

  @Override
  protected void handleDelegateEvent(IEvent event)
  {
    super.handleDelegateEvent(event);

    if (event instanceof CDOTransactionScopeClosedEvent && ((CDOTransactionScopeClosedEvent)event).getScope() == scope)
    {
      detachDelegateListener();
    }
  }

  @Override
  public CDOCommitInfo commit() throws CommitException
  {
    throw unsupportedCommit();
  }

  @Override
  public CDOCommitInfo commit(IProgressMonitor monitor) throws CommitException
  {
    throw unsupportedCommit();
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

  @Override
  public void rollback()
  {
    scope.rollback();
  }

  @Override
  public CDOSavepoint setSavepoint()
  {
    throw new UnsupportedOperationException("A nested transaction cannot create a root transaction savepoint");
  }

  @Override
  public boolean isClosed()
  {
    return !scope.isOpen() || delegate.isClosed();
  }

  @Override
  public void close()
  {
    try
    {
      scope.close();
    }
    finally
    {
      detachDelegateListener();
    }
  }

  private UnsupportedOperationException unsupportedCommit()
  {
    return new UnsupportedOperationException("A nested transaction cannot commit to the repository; use getScope().commit()");
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
