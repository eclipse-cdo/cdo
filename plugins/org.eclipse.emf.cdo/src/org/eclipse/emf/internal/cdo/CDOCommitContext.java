/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

import java.util.List;
import java.util.Map;

/**
 * Provides a context for a commit operation.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOCommitContext
{
  public InternalCDOTransaction getTransaction();

  public void preCommit();

  public void postCommit(CommitTransactionResult result);

  public List<CDOPackage> getNewPackages();

  public Map<CDOID, CDOResource> getNewResources();

  public Map<CDOID, CDOObject> getNewObjects();

  public Map<CDOID, CDOObject> getDirtyObjects();

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas();

  public Map<CDOID, CDOObject> getDetachedObjects();
}
