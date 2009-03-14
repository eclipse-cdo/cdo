/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IClassMapping
{
  public IMappingStrategy getMappingStrategy();

  public EClass getEClass();

  public IDBTable getTable();

  public Set<IDBTable> getAffectedTables();

  /**
   * @since 2.0
   */
  public boolean hasFullRevisionInfo();

  /**
   * @since 2.0
   */
  public IFeatureMapping getFeatureMapping(EStructuralFeature feature);

  public List<IAttributeMapping> getAttributeMappings();

  public List<IReferenceMapping> getReferenceMappings();

  public IAttributeMapping getAttributeMapping(EStructuralFeature feature);

  public IReferenceMapping getReferenceMapping(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, OMMonitor monitor);

  /**
   * @since 2.0
   */
  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created,
      OMMonitor monitor);

  /**
   * @since 2.0
   */
  public void detachObject(IDBStoreAccessor accessor, CDOID id, long revised, OMMonitor monitor);

  /**
   * @since 2.0
   * @return <code>true</code> if the revision has been loaded sucessfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int referenceChunk);

  /**
   * @since 2.0
   * @return <code>true</code> if the revision has been loaded sucessfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  public boolean readRevisionByTime(IDBStoreAccessor accessor, InternalCDORevision revision, long timeStamp,
      int referenceChunk);

  /**
   * @since 2.0
   * @return <code>true</code> if the revision has been loaded sucessfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int version,
      int referenceChunk);
}
