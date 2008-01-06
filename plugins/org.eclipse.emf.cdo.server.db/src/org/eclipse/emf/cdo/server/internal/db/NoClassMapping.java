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

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IDBStoreWriter;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.db.IDBTable;

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

  public void writeRevision(IDBStoreWriter storeWriter, CDORevisionImpl revision)
  {
  }

  public void readRevision(IDBStoreReader storeReader, CDORevisionImpl revision, int referenceChunk)
  {
  }

  public void readRevisionByTime(IDBStoreReader storeReader, CDORevisionImpl revision, long timeStamp,
      int referenceChunk)
  {
  }

  public void readRevisionByVersion(IDBStoreReader storeReader, CDORevisionImpl revision, int version,
      int referenceChunk)
  {
  }
}
