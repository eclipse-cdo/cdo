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

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.Collection;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IStoreAccessor extends IQueryHandler
{
  /**
   * Returns the store this accessor is associated with.
   */
  public IStore getStore();

  public void release();

  /**
   * Returns <code>true</code> if this accessor has been configured for read-only access to the back-end,
   * <code>false</code> otherwise.
   * 
   * @since 2.0
   */
  public boolean isReader();

  /**
   * Returns the session this accessor is associated with.
   * 
   * @since 2.0
   */
  public ISession getSession();

  /**
   * @since 2.0
   */
  public IStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature);

  /**
   * @since 2.0
   */
  public Collection<CDOPackageInfo> readPackageInfos();

  /**
   * Demand loads a given package proxy that has been created on startup of the repository.
   * <p>
   * It's left to the implementor's choice whether to load the {@link CDOPackage#getEcore() ecore xml} at this time
   * already. In case it is <b>not</b> loaded at this time {@link #readPackageEcore(CDOPackage) readPackageEcore()} is
   * called later on demand.
   * <p>
   * This method must only load the given package, <b>not</b> possible contained packages.
   * 
   * @see InternalCDOPackage
   * @since 2.0
   */
  public void readPackage(CDOPackage cdoPackage);

  /**
   * Loads the {@link CDOPackage#getEcore() ecore xml} of the given package.
   * 
   * @see InternalCDOPackage#setEcore(String)
   * @since 2.0
   */
  public void readPackageEcore(CDOPackage cdoPackage);

  /**
   * Returns an iterator that iterates over all objects in the store and makes their CDOIDs available for processing.
   * This method is supposed to be called very infrequently, for example during the recovery from a crash.
   * 
   * @since 2.0
   */
  public CloseableIterator<CDOID> readObjectIDs();

  /**
   * Reads the type of an object from the associated store and returns a class reference of it. This method is supposed
   * to be called very infrequently if the {@link IStore#hasEfficientTypeLookup()} returns <code>false</code>.
   * 
   * @since 2.0
   */
  public CDOClassRef readObjectType(CDOID id);

  /**
   * Reads a current revision (i.e. one with revised == 0) from the back-end. Returns <code>null</code> if the id is
   * invalid.
   * 
   * @since 2.0
   */
  public CDORevision readRevision(CDOID id, int referenceChunk);

  /**
   * Reads a revision with the given version from the back-end. This method will only be called by the framework if
   * {@link IRepository#isSupportingAudits()} is <code>true</code>. Returns <code>null</code> if the id is invalid.
   * 
   * @since 2.0
   */
  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version);

  /**
   * Reads a revision from the back-end that was valid at the given timeStamp. This method will only be called by the
   * framework if {@link IRepository#isSupportingAudits()} is <code>true</code>. Returns <code>null</code> if the id is
   * invalid.
   * 
   * @since 2.0
   */
  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp);

  /**
   * Returns the <code>CDOID</code> of the resource node with the given folderID and name if a resource with this
   * folderID and name exists in the store, <code>null</code> otherwise.
   * 
   * @since 2.0
   */
  public CDOID readResourceID(CDOID folderID, String name, long timeStamp);

  /**
   * @since 2.0
   */
  public CDORevision verifyRevision(CDORevision revision);

  /**
   * TODO Clarify the meaning of {@link #refreshRevisions()}
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
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface QueryResourcesContext
  {
    /**
     * The timeStamp of the view ({@link CDOCommonView#UNSPECIFIED_DATE} if the view is an
     * {@link CDOCommonView.Type#AUDIT audit} view.
     */
    public long getTimeStamp();

    public CDOID getFolderID();

    public String getName();

    public boolean exactMatch();

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

    /**
     * @author Eike Stepper
     * @since 2.0
     */
    public interface ExactMatch extends QueryResourcesContext
    {
      public CDOID getResourceID();
    }
  }

  /**
   * Returns the transaction this accessor is associated with if {@link #isReader()} returns <code>false</code>,
   * <code>null</code> otherwise.
   * 
   * @since 2.0
   */
  public ITransaction getTransaction();

  /**
   * Called before committing. An instance of this accessor represents an instance of a back-end transaction. Could be
   * called multiple times before commit it called. {@link IStoreAccessor#commit(OMMonitor)} or
   * {@link IStoreAccessor#rollback()} will be called after any numbers of
   * {@link IStoreAccessor#write(CommitContext, OMMonitor)}.
   * <p>
   * <b>Note</b>: {@link IStoreAccessor#write(CommitContext, OMMonitor)} and {@link IStoreAccessor#commit(OMMonitor)}
   * could be called from different threads.
   * 
   * @since 2.0
   */
  public void write(CommitContext context, OMMonitor monitor);

  /**
   * Flushes to the back-end and makes available the data for others.
   * <p>
   * <b>Note</b>: {@link IStoreAccessor#write(CommitContext, OMMonitor)} and {@link IStoreAccessor#commit(OMMonitor)}
   * could be called from different threads.
   * 
   * @since 2.0
   */
  public void commit(OMMonitor monitor);

  /**
   * <b>Note</b>: {@link IStoreAccessor#write(CommitContext, OMMonitor)} and {@link IStoreAccessor#rollback()} could be
   * called from different threads.
   * 
   * @since 2.0
   */
  public void rollback();

  /**
   * Represents the state of a single, logical commit operation which is driven through multiple calls to several
   * methods on the {@link IStoreAccessor} API. All these method calls get the same <code>CommitContext</code> instance
   * passed so that the implementor of the {@link IStoreAccessor} can track the state and progress of the commit
   * operation.
   * 
   * @author Eike Stepper
   * @since 2.0
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface CommitContext
  {
    /**
     * Returns the ID of the transactional view (<code>ITransaction</code>) which is the scope of the commit operation
     * represented by this <code>CommitContext</code>.
     */
    public int getTransactionID();

    /**
     * Returns the time stamp of this commit operation.
     */
    public long getTimeStamp();

    /**
     * Returns the temporary, transactional package manager associated with the commit operation represented by this
     * <code>CommitContext</code>. In addition to the packages registered with the session this package manager also
     * contains the new packages that are part of this commit operation.
     */
    public CDOPackageManager getPackageManager();

    /**
     * Returns an array of the new packages that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public CDOPackage[] getNewPackages();

    /**
     * Returns an array of the new objects that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public CDORevision[] getNewObjects();

    /**
     * Returns an array of the dirty objects that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public CDORevision[] getDirtyObjects();

    /**
     * Returns an array of the dirty object deltas that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public CDORevisionDelta[] getDirtyObjectDeltas();

    /**
     * Returns an array of the removed object that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     * 
     * @since 2.0
     */
    public CDOID[] getDetachedObjects();

    /**
     * Returns an unmodifiable map from all temporary IDs (meta or not) to their persistent counter parts. It is
     * initially populated with the mappings of all new meta objects.
     */
    public Map<CDOIDTemp, CDOID> getIDMappings();

    public void addIDMapping(CDOIDTemp oldID, CDOID newID);

    public void applyIDMappings(OMMonitor monitor);
  }
}
