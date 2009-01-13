/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;

import java.util.List;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface InternalCDOTransaction extends CDOTransaction, InternalCDOView
{
  public InternalCDOCommitContext createCommitContext();

  public void handleRollback(CDOSavepoint savepoint);

  public CDOSavepoint handleSetSavepoint();

  public CDOTransactionStrategy getTransactionStrategy();

  public void setTransactionStrategy(CDOTransactionStrategy transactionStrategy);

  /**
   * @return never <code>null</code>;
   */
  public CDOResourceFolder getOrCreateResourceFolder(List<String> names);

  public void detachObject(InternalCDOObject object);

  public CDOIDTemp getNextTemporaryID();

  public void registerNew(InternalCDOObject object);

  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta);

  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta);

  public void registerRevisionDelta(CDORevisionDelta revisionDelta);

  public void setConflict(InternalCDOObject object);

  public void handleConflicts(Set<CDOObject> conflicts);

  /**
   * Provides a context for a commit operation.
   * 
   * @author Simon McDuff
   */
  public interface InternalCDOCommitContext extends CDOCommitContext
  {
    public InternalCDOTransaction getTransaction();

    public void preCommit();

    public void postCommit(CommitTransactionResult result);
  }
}
