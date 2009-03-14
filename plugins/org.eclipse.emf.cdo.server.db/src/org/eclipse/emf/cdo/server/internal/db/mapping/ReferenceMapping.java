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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.mapping.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.ToMany;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

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

  public ReferenceMapping(ClassMapping classMapping, EStructuralFeature feature, ToMany toMany)
  {
    super(classMapping, feature);
    this.toMany = toMany;
    mapReference(classMapping.getEClass(), feature);
  }

  public IDBTable getTable()
  {
    return table;
  }

  public boolean isWithFeature()
  {
    return withFeature;
  }

  protected void mapReference(EClass eClass, EStructuralFeature feature)
  {
    MappingStrategy mappingStrategy = getClassMapping().getMappingStrategy();
    switch (toMany)
    {
    case PER_REFERENCE:
    {
      withFeature = false;
      String tableName = mappingStrategy.getReferenceTableName(eClass, feature);
      Object referenceMappingKey = getReferenceMappingKey(feature);
      table = mapReferenceTable(referenceMappingKey, tableName);
      break;
    }

    case PER_CLASS:
      withFeature = true;
      table = mapReferenceTable(eClass, mappingStrategy.getReferenceTableName(eClass));
      break;

    case PER_PACKAGE:
      withFeature = true;
      EPackage ePackage = eClass.getEPackage();
      table = mapReferenceTable(ePackage, mappingStrategy.getReferenceTableName(ePackage));
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

  protected Object getReferenceMappingKey(EStructuralFeature feature)
  {
    return getClassMapping().createReferenceMappingKey(feature);
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
      table.addField(CDODBSchema.REFERENCES_FEATURE, DBType.BIGINT);
    }

    IDBField sourceField = table.addField(CDODBSchema.REFERENCES_SOURCE, DBType.BIGINT);
    IDBField versionField = table.addField(CDODBSchema.REFERENCES_VERSION, DBType.INTEGER);
    IDBField idxField = table.addField(CDODBSchema.REFERENCES_IDX, DBType.INTEGER);
    table.addField(CDODBSchema.REFERENCES_TARGET, DBType.BIGINT);

    table.addIndex(Type.NON_UNIQUE, sourceField, versionField);
    table.addIndex(Type.NON_UNIQUE, idxField);
    return table;
  }

  public final void writeReference(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    int idx = 0;
    for (Object element : (revision).getList(getFeature()))
    {
      writeReferenceEntry(accessor, revision.getID(), revision.getVersion(), idx++, (CDOID)element);
    }
  }

  public final void writeReferenceEntry(IDBStoreAccessor accessor, CDOID id, int version, int idx, CDOID targetId)
  {
    IJDBCDelegate jdbcDelegate = accessor.getJDBCDelegate();
    jdbcDelegate.insertReference(id, version, idx++, targetId, this);
  }

  public void insertReferenceEntry(IDBStoreAccessor accessor, CDOID id, int newVersion, int index, CDOID value)
  {
    accessor.getJDBCDelegate().insertReferenceRow(id, newVersion, index, value, this);
  }

  public void moveReferenceEntry(IDBStoreAccessor accessor, CDOID id, int newVersion, int oldPosition, int newPosition)
  {
    accessor.getJDBCDelegate().moveReferenceRow(id, newVersion, oldPosition, newPosition, this);
  }

  public void removeReferenceEntry(IDBStoreAccessor accessor, CDOID id, int index, int newVersion)
  {
    accessor.getJDBCDelegate().removeReferenceRow(id, index, newVersion, this);
  }

  public void updateReference(IDBStoreAccessor accessor, CDOID id, int newVersion, int index, CDOID value)
  {
    accessor.getJDBCDelegate().updateReference(id, newVersion, index, value, this);
  }

  public void updateReferenceVersion(IDBStoreAccessor accessor, CDOID id, int newVersion)
  {
    accessor.getJDBCDelegate().updateReferenceVersion(id, newVersion, this);
  }

  public void deleteReference(IDBStoreAccessor accessor, CDOID id)
  {
    accessor.getJDBCDelegate().deleteReferences(id, this);
  }

  public final void readReference(IDBStoreAccessor accessor, InternalCDORevision revision, int referenceChunk)
  {
    accessor.getJDBCDelegate().selectRevisionReferences(revision, this, referenceChunk);
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    chunkReader.getAccessor().getJDBCDelegate().selectRevisionReferenceChunks(chunkReader, chunks, this, where);
  }

}
