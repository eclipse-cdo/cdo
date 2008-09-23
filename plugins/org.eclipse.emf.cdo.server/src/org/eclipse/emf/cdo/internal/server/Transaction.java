/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContextImpl.TransactionPackageManager;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IStoreWriter.CommitContext;

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
    return CDOProtocolView.Type.TRANSACTION;
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
    return new TransactionCommitContextImpl(this);
  }

  /**
   * For tests only.
   * 
   * @since 2.0
   */
  public InternalCommitContext createCommitContext(final long timeStamp)
  {
    return new TransactionCommitContextImpl(this)
    {
      @Override
      protected long createTimeStamp()
      {
        return timeStamp;
      }
    };
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  public interface InternalCommitContext extends CommitContext
  {
    public Transaction getTransaction();

    public TransactionPackageManager getPackageManager();

    public void preCommit();

    public void write();

    public void commit();

    public void postCommit(boolean success);

    public String getRollbackMessage();

    public List<CDOIDMetaRange> getMetaIDRanges();

    public void setNewPackages(CDOPackage[] newPackages);

    public void setNewObjects(CDORevision[] newObjects);

    public void setDirtyObjectDeltas(CDORevisionDelta[] dirtyObjectDeltas);

    public void setDetachedObjects(CDOID[] detachedObjects);
  }
}
