/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

import org.eclipse.net4j.db.IDBTable;

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

  public List<IAttributeMapping> getAttributeMappings();

  public List<IReferenceMapping> getReferenceMappings();

  public IAttributeMapping getAttributeMapping(CDOFeature feature);

  public IReferenceMapping getReferenceMapping(CDOFeature feature);

  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision);

  public void readRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, int referenceChunk);

  public void readRevisionByTime(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, long timeStamp,
      int referenceChunk);

  public void readRevisionByVersion(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, int version,
      int referenceChunk);
}
