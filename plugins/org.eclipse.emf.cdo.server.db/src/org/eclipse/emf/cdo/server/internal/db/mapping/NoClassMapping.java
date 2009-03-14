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
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.IReferenceMapping;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class NoClassMapping implements IClassMapping
{
  public static final IClassMapping INSTANCE = new NoClassMapping();

  private NoClassMapping()
  {
  }

  public IMappingStrategy getMappingStrategy()
  {
    return null;
  }

  public EClass getEClass()
  {
    return null;
  }

  public IDBTable getTable()
  {
    return null;
  }

  public Set<IDBTable> getAffectedTables()
  {
    return Collections.emptySet();
  }

  public boolean hasFullRevisionInfo()
  {
    return false;
  }

  public IFeatureMapping getFeatureMapping(EStructuralFeature feature)
  {
    return null;
  }

  public List<IAttributeMapping> getAttributeMappings()
  {
    return null;
  }

  public List<IReferenceMapping> getReferenceMappings()
  {
    return null;
  }

  public IAttributeMapping getAttributeMapping(EStructuralFeature feature)
  {
    return null;
  }

  public IReferenceMapping getReferenceMapping(EStructuralFeature feature)
  {
    return null;
  }

  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, OMMonitor monitor)
  {
  }

  public void detachObject(IDBStoreAccessor accessor, CDOID id, long revised, OMMonitor monitor)
  {
  }

  public boolean readRevision(IDBStoreAccessor accessor, InternalCDORevision revision, int referenceChunk)
  {
    return false;
  }

  public boolean readRevisionByTime(IDBStoreAccessor accessor, InternalCDORevision revision, long timeStamp,
      int referenceChunk)
  {
    return false;
  }

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, InternalCDORevision revision, int version,
      int referenceChunk)
  {
    return false;
  }

  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created,
      OMMonitor monitor)
  {
  }
}
