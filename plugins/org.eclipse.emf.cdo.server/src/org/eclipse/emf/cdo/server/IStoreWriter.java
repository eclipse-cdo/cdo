/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IStoreWriter extends IStoreReader
{
  public IView getView();

  /**
   * Called before committing. An Instance of a storeWriter represents an instance of back-end Transaction. Could be called multiple times before commit it called. 
   * @since 2.0
   */
  public void write(CommitContext context);

  /**
   * It will flush to the backend and make available the data for others.
   * @since 2.0
   */
  public void commit();

  /**
   * @since 2.0
   */
  public void rollback();

  /**
   * Represents the state of a single, logical commit operation which is driven through multiple calls to several
   * methods on the {@link IStoreWriter} API. All these method calls get the same <code>CommitContext</code> instance
   * passed so that the implementor of the {@link IStoreWriter} can track the state and progress of the commit
   * operation.
   * 
   * @author Eike Stepper
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

    public void applyIDMappings();
  }
}
