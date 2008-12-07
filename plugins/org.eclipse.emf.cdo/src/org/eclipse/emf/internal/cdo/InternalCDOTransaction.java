/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;

import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface InternalCDOTransaction extends CDOTransaction, InternalCDOView, CDOTransaction.Options
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
}
