/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Transaction extends View implements ITransaction
{
  public Transaction(Session session, int viewID)
  {
    super(session, viewID);
  }

  @Override
  public Type getViewType()
  {
    return CDOCommonView.Type.TRANSACTION;
  }

  public int getTransactionID()
  {
    return getViewID();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Transaction[{0}]", getTransactionID());
  }

  /**
   * @since 2.0
   */
  public InternalCommitContext createCommitContext()
  {
    checkOpen();
    return new TransactionCommitContextImpl(this);
  }

  /**
   * For tests only.
   * 
   * @since 2.0
   */
  public InternalCommitContext testCreateCommitContext(final long timeStamp)
  {
    checkOpen();
    return new TransactionCommitContextImpl(this)
    {
      @Override
      protected long createTimeStamp()
      {
        return timeStamp;
      }
    };
  }

  private void checkOpen()
  {
    if (isClosed())
    {
      throw new IllegalStateException("View closed");
    }
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  public interface InternalCommitContext extends IStoreAccessor.CommitContext
  {
    public Transaction getTransaction();

    public void preCommit();

    public void write(OMMonitor monitor);

    public void commit(OMMonitor monitor);

    public void rollback(String message);

    public void postCommit(boolean success);

    public String getRollbackMessage();

    public List<CDOIDMetaRange> getMetaIDRanges();

    public void setNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits);

    public void setNewObjects(InternalCDORevision[] newObjects);

    public void setDirtyObjectDeltas(InternalCDORevisionDelta[] dirtyObjectDeltas);

    public void setDetachedObjects(CDOID[] detachedObjects);

    public boolean setAutoReleaseLocksEnabled(boolean on);

    public boolean isAutoReleaseLocksEnabled();
  }
}
