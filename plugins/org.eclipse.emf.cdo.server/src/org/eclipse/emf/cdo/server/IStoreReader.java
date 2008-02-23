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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.util.io.CloseableIterator;

import java.util.Collection;

/**
 * @see StoreUtil#getReader()
 * @author Eike Stepper
 */
public interface IStoreReader extends IStoreAccessor
{
  public ISession getSession();

  public IStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature);

  public Collection<CDOPackageInfo> readPackageInfos();

  /**
   * Demand loads a given package proxy that has been created on startup of the repository.
   */
  public void readPackage(CDOPackage cdoPackage);

  /**
   * Returns an iterator that iterates over all objects in the store and makes their ids available for processing. This
   * method is supposed to be called very infrequently, for example during the recovery from a crash.
   */
  public CloseableIterator<CDOID> readObjectIDs(boolean withTypes);

  /**
   * Reads the type of an object from the associated store and returns a class reference of it. This method is supposed
   * to be called very infrequently if the {@link IStore#hasEfficientTypeLookup()} returns <code>false</code>.
   */
  public CDOClassRef readObjectType(CDOID id);

  public CDORevision readRevision(CDOID id, int referenceChunk);

  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp);

  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version);

  /**
   * Returns the <code>CDOID</code> of the resource with the given path if a resource with this path exists in the
   * store, <code>null</code> otherwise.
   */
  public CDOID readResourceID(String path);

  /**
   * Returns the path of the resource with the given <code>CDOID</code> if a resource with this <code>CDOID</code>
   * exists in the store, <code>null</code> otherwise.
   */
  public String readResourcePath(CDOID id);

  public CDORevision verifyRevision(CDORevision revision);
}
