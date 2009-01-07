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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

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

  public CDOClass getCDOClass()
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

  public IFeatureMapping getFeatureMapping(CDOFeature feature)
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

  public IAttributeMapping getAttributeMapping(CDOFeature feature)
  {
    return null;
  }

  public IReferenceMapping getReferenceMapping(CDOFeature feature)
  {
    return null;
  }

  public void writeRevision(IDBStoreAccessor accessor, CDORevision revision, OMMonitor monitor)
  {
  }

  public void detachObject(IDBStoreAccessor accessor, CDOID id, long revised, OMMonitor monitor)
  {
  }

  public boolean readRevision(IDBStoreAccessor accessor, CDORevision revision, int referenceChunk)
  {
    return false;
  }

  public boolean readRevisionByTime(IDBStoreAccessor accessor, CDORevision revision, long timeStamp, int referenceChunk)
  {
    return false;
  }

  public boolean readRevisionByVersion(IDBStoreAccessor accessor, CDORevision revision, int version, int referenceChunk)
  {
    return false;
  }
}
