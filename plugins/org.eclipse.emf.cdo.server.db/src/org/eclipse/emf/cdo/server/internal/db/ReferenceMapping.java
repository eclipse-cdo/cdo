/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ReferenceMapping extends FeatureMapping implements IReferenceMapping
{
  private IDBTable table;

  private ToMany toMany;

  private boolean withFeature;

  public ReferenceMapping(ClassMapping classMapping, CDOFeature feature, ToMany toMany)
  {
    super(classMapping, feature);
    this.toMany = toMany;
    mapReference(classMapping.getCDOClass(), feature);
  }

  public IDBTable getTable()
  {
    return table;
  }

  public final void writeReference(IDBStoreAccessor accessor, CDORevision revision)
  {
    IJDBCDelegate jdbcDelegate = accessor.getJDBCDelegate();

    int idx = 0;
    for (Object element : ((InternalCDORevision)revision).getList(getFeature()))
    {
      jdbcDelegate.insertReference(revision, idx++, (CDOID)element, this);
    }
  }

  public void deleteReference(IDBStoreAccessor accessor, CDOID id)
  {
    accessor.getJDBCDelegate().deleteReferences(id, this);
  }

  public final void readReference(IDBStoreAccessor accessor, CDORevision revision, int referenceChunk)
  {
    accessor.getJDBCDelegate().selectRevisionReferences(revision, this, referenceChunk);
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    chunkReader.getAccessor().getJDBCDelegate().selectRevisionReferenceChunks(chunkReader, chunks, this, where);
  }

  protected void mapReference(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    MappingStrategy mappingStrategy = getClassMapping().getMappingStrategy();
    switch (toMany)
    {
    case PER_REFERENCE:
    {
      withFeature = false;
      String tableName = mappingStrategy.getReferenceTableName(cdoClass, cdoFeature);
      Object referenceMappingKey = getReferenceMappingKey(cdoFeature);
      table = mapReferenceTable(referenceMappingKey, tableName);
      break;
    }

    case PER_CLASS:
      withFeature = true;
      table = mapReferenceTable(cdoClass, mappingStrategy.getReferenceTableName(cdoClass));
      break;

    case PER_PACKAGE:
      withFeature = true;
      CDOPackage cdoPackage = cdoClass.getContainingPackage();
      table = mapReferenceTable(cdoPackage, mappingStrategy.getReferenceTableName(cdoPackage));
      break;

    case PER_REPOSITORY:
      withFeature = true;
      IRepository repository = mappingStrategy.getStore().getRepository();
      table = mapReferenceTable(repository, repository.getName() + "_refs");
      break;

    default:
      throw new IllegalArgumentException("Invalid mapping: " + toMany);
    }
  }

  protected Object getReferenceMappingKey(CDOFeature cdoFeature)
  {
    return getClassMapping().createReferenceMappingKey(cdoFeature);
  }

  protected IDBTable mapReferenceTable(Object key, String tableName)
  {
    Map<Object, IDBTable> referenceTables = getClassMapping().getMappingStrategy().getReferenceTables();
    IDBTable table = referenceTables.get(key);
    if (table == null)
    {
      table = addReferenceTable(tableName);
      referenceTables.put(key, table);
    }

    return table;
  }

  protected IDBTable addReferenceTable(String tableName)
  {
    IDBTable table = getClassMapping().addTable(tableName);
    if (withFeature)
    {
      table.addField(CDODBSchema.REFERENCES_FEATURE, DBType.INTEGER);
    }

    IDBField sourceField = table.addField(CDODBSchema.REFERENCES_SOURCE, DBType.BIGINT);
    IDBField versionField = table.addField(CDODBSchema.REFERENCES_VERSION, DBType.INTEGER);
    IDBField idxField = table.addField(CDODBSchema.REFERENCES_IDX, DBType.INTEGER);
    table.addField(CDODBSchema.REFERENCES_TARGET, DBType.BIGINT);

    table.addIndex(Type.NON_UNIQUE, sourceField, versionField);
    table.addIndex(Type.NON_UNIQUE, idxField);
    return table;
  }

  public boolean isWithFeature()
  {
    return withFeature;
  }
}
