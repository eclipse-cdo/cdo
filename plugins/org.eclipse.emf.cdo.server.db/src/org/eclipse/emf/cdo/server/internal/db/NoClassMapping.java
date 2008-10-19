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
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IDBStoreWriter;
import org.eclipse.emf.cdo.server.db.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.ddl.IDBTable;

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

  public void writeRevision(IDBStoreWriter storeWriter, CDORevision revision)
  {
  }

  public void detachObject(IDBStoreWriter storeWriter, CDOID id, long revised)
  {
  }

  public void readRevision(IDBStoreReader storeReader, CDORevision revision, int referenceChunk)
  {
  }

  public void readRevisionByTime(IDBStoreReader storeReader, CDORevision revision, long timeStamp, int referenceChunk)
  {
  }

  public void readRevisionByVersion(IDBStoreReader storeReader, CDORevision revision, int version, int referenceChunk)
  {
  }
}
