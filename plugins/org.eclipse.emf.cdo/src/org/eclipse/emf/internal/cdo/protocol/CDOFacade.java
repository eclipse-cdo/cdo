/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Provisional in preparation for 2.0.0
 * 
 * @author Eike Stepper
 */
public interface CDOFacade
{
  public OpenSessionResult openSession(String repositoryName, boolean legacySupportEnabled,
      RemoteInvalidationHandler remoteInvalidationHandler);

  public int loadLibraries(Collection<String> libraryNames, File cacheFolder);

  public boolean viewsChanged(int viewID, byte kind);

  public CDOID resourceID(String path);

  public String resourcePath(CDOID id);

  public void loadPackage(CDOPackage cdoPackage);

  public List<InternalCDORevision> loadRevision(Collection<CDOID> ids, int referenceChunk);

  public List<InternalCDORevision> loadRevisionByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp);

  public List<InternalCDORevision> loadRevisionByVersion();

  public CDOID loadChunk(InternalCDORevision revision, CDOFeature feature, int accessIndex, int fromIndex, int toIndex);

  public List<InternalCDORevision> verifyRevision(Collection<InternalCDORevision> revisions);

  public CDOClassRef[] queryObjectTypes(List<CDOID> ids);

  public CommitTransactionResult commitTransaction(CDOTransactionImpl transaction);

  public void closeSession(CDOSession session);

  /**
   * @author Eike Stepper
   */
  public interface RemoteInvalidationHandler
  {
    public void handleRemoteInvalidation();
  }
}
