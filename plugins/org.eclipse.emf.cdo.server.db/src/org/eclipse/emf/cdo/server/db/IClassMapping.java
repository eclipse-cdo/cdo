/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IClassMapping
{
  public IMappingStrategy getMappingStrategy();

  public CDOClass getCDOClass();

  public IDBTable getTable();

  public Set<IDBTable> getAffectedTables();

  /**
   * @since 2.0
   */
  public boolean hasFullRevisionInfo();

  /**
   * @since 2.0
   */
  public IFeatureMapping getFeatureMapping(CDOFeature feature);

  public List<IAttributeMapping> getAttributeMappings();

  public List<IReferenceMapping> getReferenceMappings();

  public IAttributeMapping getAttributeMapping(CDOFeature feature);

  public IReferenceMapping getReferenceMapping(CDOFeature feature);

  /**
   * @since 2.0
   */
  public void writeRevision(IDBStoreAccessor accessor, CDORevision revision, OMMonitor monitor);

  /**
   * @since 2.0
   */
  public void detachObject(IDBStoreAccessor accessor, CDOID id, long revised, OMMonitor monitor);

  /**
   * @since 2.0
   * @return <code>true</code> if the revision has been loaded sucessfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  public boolean readRevision(IDBStoreAccessor accessor, CDORevision revision, int referenceChunk);

  /**
   * @since 2.0
   * @return <code>true</code> if the revision has been loaded sucessfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  public boolean readRevisionByTime(IDBStoreAccessor accessor, CDORevision revision, long timeStamp, int referenceChunk);

  /**
   * @since 2.0
   * @return <code>true</code> if the revision has been loaded sucessfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  public boolean readRevisionByVersion(IDBStoreAccessor accessor, CDORevision revision, int version, int referenceChunk);
}
