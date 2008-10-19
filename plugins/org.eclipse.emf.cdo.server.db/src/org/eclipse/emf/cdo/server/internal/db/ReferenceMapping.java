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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IDBStoreWriter;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ReferenceMapping extends FeatureMapping implements IReferenceMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, ReferenceMapping.class);

  private static final String SELECT_SUFFIX = " ORDER BY " + CDODBSchema.REFERENCES_IDX;

  private IDBTable table;

  private ToMany toMany;

  private boolean withFeature;

  private String insertPrefix;

  private String selectPrefix;

  public ReferenceMapping(ClassMapping classMapping, CDOFeature feature, ToMany toMany)
  {
    super(classMapping, feature);
    this.toMany = toMany;
    mapReference(classMapping.getCDOClass(), feature);

    int dbFeatureID = withFeature ? FeatureServerInfo.getDBID(getFeature()) : 0;
    insertPrefix = createInsertPrefix(dbFeatureID);
    selectPrefix = createSelectPrefix(dbFeatureID);
  }

  public IDBTable getTable()
  {
    return table;
  }

  public void writeReference(IDBStoreWriter storeWriter, CDORevision revision)
  {
    long source = CDOIDUtil.getLong(revision.getID());
    int version = revision.getVersion();

    int idx = 0;
    for (Object element : ((InternalCDORevision)revision).getList(getFeature()))
    {
      long target = CDOIDUtil.getLong((CDOID)element);
      StringBuilder builder = new StringBuilder(insertPrefix);
      builder.append(source);
      builder.append(", ");
      builder.append(version);
      builder.append(", ");
      builder.append(idx++);
      builder.append(", ");
      builder.append(target);
      builder.append(")");
      String sql = builder.toString();
      getClassMapping().sqlUpdate(storeWriter, sql);
    }
  }

  public void readReference(IDBStoreReader storeReader, CDORevision revision, int referenceChunk)
  {
    MoveableList<Object> list = ((InternalCDORevision)revision).getList(getFeature());
    CDOID source = revision.getID();
    int version = revision.getVersion();

    String sql = createSelect(source, version, null);
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    ResultSet resultSet = null;

    try
    {
      resultSet = storeReader.getStatement().executeQuery(sql);
      while (resultSet.next() && (referenceChunk == CDORevision.UNCHUNKED || --referenceChunk >= 0))
      {
        long target = resultSet.getLong(1);
        list.add(CDOIDUtil.createLong(target));
      }

      // TODO Optimize this?
      while (resultSet.next())
      {
        list.add(InternalCDORevision.UNINITIALIZED);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  public void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    IDBStoreAccessor storeAccessor = chunkReader.getStoreReader();
    CDOID source = chunkReader.getRevision().getID();
    int version = chunkReader.getRevision().getVersion();

    String sql = createSelect(source, version, where);
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    ResultSet resultSet = null;

    try
    {
      Chunk chunk = null;
      int chunkSize = 0;
      int chunkIndex = 0;
      int indexInChunk = 0;

      resultSet = storeAccessor.getStatement().executeQuery(sql);
      while (resultSet.next())
      {
        long target = resultSet.getLong(1);
        if (chunk == null)
        {
          chunk = chunks.get(chunkIndex++);
          chunkSize = chunk.size();
        }

        chunk.addID(indexInChunk++, CDOIDUtil.createLong(target));
        if (indexInChunk == chunkSize)
        {
          chunk = null;
          indexInChunk = 0;
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  protected String createInsertPrefix(int dbFeatureID)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");
    if (dbFeatureID != 0)
    {
      builder.append(FeatureServerInfo.getDBID(getFeature()));
      builder.append(", ");
    }

    return builder.toString();
  }

  protected String createSelectPrefix(int dbFeatureID)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(CDODBSchema.REFERENCES_TARGET);
    builder.append(" FROM ");
    builder.append(table);
    builder.append(" WHERE ");
    if (dbFeatureID != 0)
    {
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append("=");
      builder.append(dbFeatureID);
      builder.append(" AND ");
    }

    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append("=");
    return builder.toString();
  }

  protected String createSelect(CDOID source, int version, String where)
  {
    StringBuilder builder = new StringBuilder(selectPrefix);
    builder.append(CDOIDUtil.getLong(source));
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append("=");
    builder.append(version);
    if (where != null)
    {
      builder.append(where);
    }

    builder.append(SELECT_SUFFIX);
    return builder.toString();
  }

  protected void mapReference(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    MappingStrategy mappingStrategy = getClassMapping().getMappingStrategy();
    switch (toMany)
    {
    case PER_REFERENCE:
      withFeature = false;
      table = mapReferenceTable(cdoFeature, mappingStrategy.getTableName(cdoClass) + "_" + cdoFeature.getName()
          + "_refs");
      break;

    case PER_CLASS:
      withFeature = true;
      table = mapReferenceTable(cdoClass, mappingStrategy.getTableName(cdoClass) + "_refs");
      break;

    case PER_PACKAGE:
      withFeature = true;
      CDOPackage cdoPackage = cdoClass.getContainingPackage();
      table = mapReferenceTable(cdoPackage, mappingStrategy.getTableName(cdoPackage) + "_refs");
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

    table.addField(CDODBSchema.REFERENCES_SOURCE, DBType.BIGINT);
    table.addField(CDODBSchema.REFERENCES_VERSION, DBType.INTEGER);
    table.addField(CDODBSchema.REFERENCES_IDX, DBType.INTEGER);
    table.addField(CDODBSchema.REFERENCES_TARGET, DBType.BIGINT);
    return table;
  }

}
