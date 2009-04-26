/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444  
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.util.Collection;

/**
 * Basic interface for class mappings.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IClassMapping
{
  /**
   * Returns all DB tables which are used by this class and all its contained features.
   * 
   * @return a collection of all tables of this class and all its contained features.
   */
  public Collection<IDBTable> getDBTables();

  /**
   * Get the mapping of the many-valued feature.
   * 
   * @param feature
   *          the feature for which the mapping should be returned. <code>feature.isMany()</code> has to be
   *          <code>true</code>.
   * @return the list mapping corresponding to the feature.
   */
  public IListMapping getListMapping(EStructuralFeature feature);

  /**
   * Read the current version of a revision.
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @param revision
   *          the revision object into which the data should be read. The revision has to be have its ID set to the
   *          requested object's ID. The version is ignored, as the version parameter is used to determine the version
   *          to be read.
   * @param listChunk
   *          the chunk size to read attribute lists.
   * @return <code>true</code>, if the revision has been found and read correctly. <code>false</code> if the revision
   *         could not be found. In this case, the content of <code>revision</code> is undefined.
   */
  public boolean readRevision(IDBStoreAccessor dbStoreAccessor, InternalCDORevision revision, int listChunk);

  /**
   * Write the revision data to the database.
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @param revision
   *          the revision to write.
   * @param monitor
   *          the monitor to indicate progress.
   */
  public void writeRevision(IDBStoreAccessor dbStoreAccessor, InternalCDORevision revision, OMMonitor monitor);

  /**
   * Removes an object from the database. In the case of auditing support the object is just marked as revised, else it
   * it permanently deleted.
   * 
   * @param dbStoreAccessor
   *          the accessor to use.
   * @param id
   *          the ID of the object to remove.
   * @param revised
   *          the timeStamp when this object became detached.
   * @param monitor
   *          the monitor to indicate progress.
   */
  public void detachObject(IDBStoreAccessor dbStoreAccessor, CDOID id, long revised, OMMonitor monitor);

  /**
   * Create a prepared statement which returns all IDs of instances of the corresponding class.
   * 
   * @param accessor
   *          the accessor to use to create the statement
   * @return the prepared statement ready to be executed using <code>result.executeQuery()</code>.
   */
  public PreparedStatement createObjectIdStatement(IDBStoreAccessor accessor);

  /**
   * Create a prepared statement which returns all IDs of instances of the corresponding class.
   * 
   * @param accessor
   *          the accessor to use to create the statement
   * @param folderId
   *          the ID of the containing folder. <code>0</code> means none.
   * @param name
   *          the name of the resource node to look up
   * @param exactMatch
   *          if <code>true</code>, <code>name</code> must match exactly, otherwise all resource nodes starting with
   *          <code>name</code> are returned.
   * @param timeStamp
   *          a timestamp in the past if past versions should be looked up. In case of no audit support, this must be
   *          {@link CDORevision#UNSPECIFIED_DATE}.
   * @return the prepared statement ready to be executed using <code>result.executeQuery()</code>.
   * @throws ImplementationError
   *           if called on a mapping which does not map an <code>EClass instanceof CDOResourceNode</code>.
   */
  public PreparedStatement createResourceQueryStatement(IDBStoreAccessor accessor, CDOID folderId, String name,
      boolean exactMatch, long timeStamp);

}
