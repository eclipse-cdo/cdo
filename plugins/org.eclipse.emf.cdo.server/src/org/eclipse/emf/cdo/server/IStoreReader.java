/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832        
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.Collection;

/**
 * @see StoreThreadLocal#getStoreReader()
 * @author Eike Stepper
 */
public interface IStoreReader extends IStoreAccessor, IQueryHandler
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
  public CloseableIterator<CDOID> readObjectIDs();

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

  /**
   * TODO Clarify the meaning of {@link IStoreReader#refreshRevisions()}
   * 
   * @since 2.0
   */
  public void refreshRevisions();

  /**
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context);

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public interface QueryResourcesContext
  {
    public String getPathPrefix();

    /**
     * Returns the maximum number of results expected by the client or {@link CDOQueryInfo#UNLIMITED_RESULTS} for no
     * limitation.
     */
    public int getMaxResults();

    /**
     * Adds the CDOID of one resource to the results of the underlying query.
     * 
     * @return <code>true</code> to indicate that more results can be passed subsequently, <code>false</code> otherwise
     *         (i.e. maxResults has been reached or an asynchronous query has been canceled).
     */
    public boolean addResource(CDOID resourceID);
  }
}
