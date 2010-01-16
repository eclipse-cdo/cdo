/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Christopher Albert - 254455: [DB] Support FeatureMaps bug 254455
 *    Victor Roldan Betancort - Bug 283998: [DB] Chunk reading for multiple chunks fails
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingFactory;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This abstract base class provides basic behavior needed for mapping many-valued attributes to tables.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class AbstractFeatureMapTableMapping extends BasicAbstractListTableMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractFeatureMapTableMapping.class);

  /**
   * The table of this mapping.
   */
  private IDBTable table;

  /**
   * The tags mapped to column names
   */
  private HashMap<Long, String> tagMap;

  /**
   * Column name Set
   */
  private List<String> columnNames;

  /**
   * The type mappings for the value fields.
   */
  private Map<Long, ITypeMapping> typeMappings;

  // --------- SQL strings - see initSqlStrings() -----------------
  private String sqlSelectChunksPrefix;

  private String sqlOrderByIndex;

  protected String sqlInsert;

  private String sqlGetListLastIndex;

  private List<DBType> dbTypes;

  public AbstractFeatureMapTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    super(mappingStrategy, eClass, feature);
    initDBTypes();
    initTable();
    initSqlStrings();
  }

  private void initDBTypes()
  {
    // TODO add annotation processing here ...
    dbTypes = new ArrayList<DBType>(TypeMappingFactory.getDefaultFeatureMapDBTypes());
  }

  private void initTable()
  {
    String tableName = getMappingStrategy().getTableName(getContainingClass(), getFeature());
    table = getMappingStrategy().getStore().getDBSchema().addTable(tableName);

    // add fields for keys (cdo_id, version, feature_id)
    FieldInfo[] fields = getKeyFields();
    IDBField[] dbFields = new IDBField[fields.length];

    for (int i = 0; i < fields.length; i++)
    {
      dbFields[i] = table.addField(fields[i].getName(), fields[i].getDbType());
    }

    // add field for list index
    IDBField idxField = table.addField(CDODBSchema.FEATUREMAP_IDX, DBType.INTEGER);

    // add field for FeatureMap tag (MetaID for Feature in CDO registry)
    IDBField tagField = table.addField(CDODBSchema.FEATUREMAP_TAG, DBType.INTEGER);

    tagMap = new HashMap<Long, String>();
    typeMappings = new HashMap<Long, ITypeMapping>();
    columnNames = new ArrayList<String>();

    // create columns for all DBTypes
    for (DBType type : getDBTypes())
    {
      String column = CDODBSchema.FEATUREMAP_VALUE + "_" + type.name();
      table.addField(column, type);
      columnNames.add(column);
    }

    table.addIndex(Type.NON_UNIQUE, dbFields);
    table.addIndex(Type.NON_UNIQUE, idxField);
    table.addIndex(Type.NON_UNIQUE, tagField);
  }

  protected abstract FieldInfo[] getKeyFields();

  protected abstract void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException;

  public Collection<IDBTable> getDBTables()
  {
    return Arrays.asList(table);
  }

  private void initSqlStrings()
  {
    String tableName = getTable().getName();
    FieldInfo[] fields = getKeyFields();

    // ---------------- SELECT to read chunks ----------------------------
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");

    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append(", ");

    Iterator<String> iter = columnNames.iterator();
    while (iter.hasNext())
    {
      builder.append(iter.next());
      if (iter.hasNext())
      {
        builder.append(", ");
      }
    }

    builder.append(" FROM ");
    builder.append(tableName);
    builder.append(" WHERE ");

    for (int i = 0; i < fields.length; i++)
    {
      builder.append(fields[i].getName());
      if (i + 1 < fields.length)
      {
        // more to come
        builder.append("= ? AND ");
      }
      else
      {
        // last one
        builder.append("= ? ");
      }
    }

    sqlSelectChunksPrefix = builder.toString();

    sqlOrderByIndex = " ORDER BY " + CDODBSchema.FEATUREMAP_IDX; //$NON-NLS-1$

    // ----------------- count list size --------------------------

    builder = new StringBuilder("SELECT max(");
    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(") FROM ");
    builder.append(tableName);
    builder.append(" WHERE ");

    for (int i = 0; i < fields.length; i++)
    {
      builder.append(fields[i].getName());
      if (i + 1 < fields.length)
      {
        // more to come
        builder.append("= ? AND ");
      }
      else
      {
        // last one
        builder.append("= ? ");
      }
    }

    sqlGetListLastIndex = builder.toString();

    // INSERT with dynamic field name
    // TODO: Better: universal INSERT-Statement, because of stmt caching!

    // ----------------- INSERT - prefix -----------------
    builder = new StringBuilder("INSERT INTO ");
    builder.append(tableName);
    builder.append(" ("); //$NON-NLS-1$
    for (int i = 0; i < fields.length; i++)
    {
      builder.append(fields[i].getName());
      builder.append(", "); //$NON-NLS-1$
    }

    for (int i = 0; i < columnNames.size(); i++)
    {
      builder.append(columnNames.get(i));
      builder.append(", "); //$NON-NLS-1$
    }

    builder.append(CDODBSchema.FEATUREMAP_IDX);
    builder.append(", "); //$NON-NLS-1$
    builder.append(CDODBSchema.FEATUREMAP_TAG);
    builder.append(") VALUES ("); //$NON-NLS-1$
    for (int i = 0; i < fields.length + columnNames.size(); i++)
    {
      builder.append("?, ");
    }

    builder.append("?, ?)");
    sqlInsert = builder.toString();
  }

  protected List<DBType> getDBTypes()
  {
    return dbTypes;
  }

  protected final IDBTable getTable()
  {
    return table;
  }

  protected final List<String> getColumnNames()
  {
    return columnNames;
  }

  protected final Map<Long, ITypeMapping> getTypeMappings()
  {
    return typeMappings;
  }

  protected final Map<Long, String> getTagMap()
  {
    return tagMap;
  }

  public void readValues(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    MoveableList<Object> list = revision.getList(getFeature());
    int listSize = -1;

    if (listChunk != CDORevision.UNCHUNKED)
    {
      listSize = getListLastIndex(accessor, revision);
      if (listSize == -1)
      {
        // list is empty - take shortcut
        return;
      }
      else
      {
        // subtract amount of items we are going to read now
        listSize -= listChunk;
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(), getFeature()
          .getName(), revision.getID(), revision.getVersion());
    }

    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      String sql = sqlSelectChunksPrefix + sqlOrderByIndex;
      pstmt = statementCache.getPreparedStatement(sql, ReuseProbability.HIGH);
      setKeyFields(pstmt, revision);

      if (listChunk != CDORevision.UNCHUNKED)
      {
        pstmt.setMaxRows(listChunk); // optimization - don't read unneeded rows.
      }

      resultSet = pstmt.executeQuery();
      while ((listChunk == CDORevision.UNCHUNKED || --listChunk >= 0) && resultSet.next())
      {
        Long tag = resultSet.getLong(1);
        Object value = getTypeMapping(tag).readValue(resultSet);

        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", list.size(), value);
        }

        list.add(CDORevisionUtil.createFeatureMapEntry(getFeatureByTag(tag), value));
      }

      while (listSize-- >= 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Adding UNINITIALIZED for index {0} ", list.size());
        }

        list.add(InternalCDOList.UNINITIALIZED);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(pstmt);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values done for feature {0}.{1} of {2}v{3}", getContainingClass().getName(),
          getFeature().getName(), revision.getID(), revision.getVersion());
    }
  }

  private void addFeature(Long tag)
  {
    EStructuralFeature modelFeature = getFeatureByTag(tag);

    TypeMapping typeMapping = (TypeMapping)getMappingStrategy().createValueMapping(modelFeature);
    String column = CDODBSchema.FEATUREMAP_VALUE + "_" + typeMapping.getDBType();

    tagMap.put(tag, column);
    typeMapping.setDBField(table, column);
    typeMappings.put(tag, typeMapping);
  }

  /**
   * Return the last (maximum) list index. (euals to size-1)
   *
   * @param accessor
   *          the accessor to use
   * @param revision
   *          the revision to which the feature list belongs
   * @return the last index or <code>-1</code> if the list is empty.
   */
  private int getListLastIndex(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      pstmt = statementCache.getPreparedStatement(sqlGetListLastIndex, ReuseProbability.HIGH);
      setKeyFields(pstmt, revision);

      resultSet = pstmt.executeQuery();
      if (!resultSet.next())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("No last index found -> list is empty. ");
        }

        return -1;
      }
      else
      {
        int result = resultSet.getInt(1);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Read list last index = " + result);
        }

        return result;
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values for feature {0}.{1} of {2}v{3}", getContainingClass().getName(),
          getFeature().getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
    }

    IPreparedStatementCache statementCache = chunkReader.getAccessor().getStatementCache();
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      StringBuilder builder = new StringBuilder(sqlSelectChunksPrefix);
      if (where != null)
      {
        builder.append(" AND "); //$NON-NLS-1$
        builder.append(where);
      }

      builder.append(sqlOrderByIndex);

      String sql = builder.toString();
      pstmt = statementCache.getPreparedStatement(sql, ReuseProbability.LOW);
      setKeyFields(pstmt, chunkReader.getRevision());

      resultSet = pstmt.executeQuery();

      Chunk chunk = null;
      int chunkSize = 0;
      int chunkIndex = 0;
      int indexInChunk = 0;

      while (resultSet.next())
      {
        Long tag = resultSet.getLong(1);
        Object value = getTypeMapping(tag).readValue(resultSet);

        if (chunk == null)
        {
          chunk = chunks.get(chunkIndex++);
          chunkSize = chunk.size();

          if (TRACER.isEnabled())
          {
            TRACER.format("Current chunk no. {0} is [start = {1}, size = {2}]", chunkIndex - 1, chunk.getStartIndex(),
                chunkSize);
          }
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for chunk index {0} from result set: {1}", indexInChunk, value);
        }

        chunk.add(indexInChunk++, CDORevisionUtil.createFeatureMapEntry(getFeatureByTag(tag), value));
        if (indexInChunk == chunkSize)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Chunk finished.");
          }

          chunk = null;
          indexInChunk = 0;
        }
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Reading list chunk values done for feature {0}.{1} of {2}v{3}", getContainingClass().getName(),
            getTagByFeature(getFeature()), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      statementCache.releasePreparedStatement(pstmt);
    }
  }

  public void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    CDOList values = revision.getList(getFeature());

    int idx = 0;
    for (Object element : values)
    {
      writeValue(accessor, revision, idx++, element);
    }
  }

  protected final void writeValue(IDBStoreAccessor accessor, CDORevision revision, int idx, Object value)
  {
    IPreparedStatementCache statementCache = accessor.getStatementCache();
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Writing value for feature {0}.{1} index {2} of {3}v{4} : {5}", getContainingClass().getName(), getTagByFeature(getFeature()), idx, revision.getID(), revision.getVersion(), value); //$NON-NLS-1$
    }

    try
    {
      FeatureMap.Entry entry = (FeatureMap.Entry)value;
      EStructuralFeature entryFeature = entry.getEStructuralFeature();
      Long tag = getTagByFeature(entryFeature);
      String column = getColumnName(tag);

      String sql = sqlInsert;
      stmt = statementCache.getPreparedStatement(sql, ReuseProbability.HIGH);
      setKeyFields(stmt, revision);
      int stmtIndex = getKeyFields().length + 1;

      for (int i = 0; i < columnNames.size(); i++)
      {
        if (columnNames.get(i).equals(column))
        {
          getTypeMapping(tag).setValue(stmt, stmtIndex++, entry.getValue());
        }
        else
        {
          stmt.setNull(stmtIndex++, getDBTypes().get(i).getCode());
        }
      }

      stmt.setInt(stmtIndex++, idx);
      stmt.setLong(stmtIndex++, tag);
      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      statementCache.releasePreparedStatement(stmt);
    }
  }

  /**
   * Get column name (lazy)
   *
   * @param tag
   *          The feature's MetaID in CDO
   * @return the column name where the values are stored
   */
  protected String getColumnName(Long tag)
  {
    String column = tagMap.get(tag);
    if (column == null)
    {
      addFeature(tag);
      column = tagMap.get(tag);
    }

    return column;
  }

  /**
   * Get type mapping (lazy)
   *
   * @param tag
   *          The feature's MetaID in CDO
   * @return the corresponding type mapping
   */
  protected ITypeMapping getTypeMapping(Long tag)
  {
    ITypeMapping typeMapping = typeMappings.get(tag);
    if (typeMapping == null)
    {
      addFeature(tag);
      typeMapping = typeMappings.get(tag);
    }

    return typeMapping;
  }

  /**
   * @param metaID
   * @return the column name where the values are stored
   */
  private EStructuralFeature getFeatureByTag(Long tag)
  {
    return (EStructuralFeature)getMappingStrategy().getStore().getMetaDataManager().getMetaInstance(tag);
  }

  /**
   * @param feature
   *          The EStructuralFeature
   * @return The feature's MetaID in CDO
   */
  protected Long getTagByFeature(EStructuralFeature feature)
  {
    return getMappingStrategy().getStore().getMetaDataManager().getMetaID(feature);
  }


  /**
   * Used by subclasses to indicate which fields should be in the table. I.e. just a pair of name and DBType ...
   *
   * @author Stefan Winkler
   */
  protected static class FieldInfo
  {
    private String name;

    private DBType dbType;

    public FieldInfo(String name, DBType dbType)
    {
      this.name = name;
      this.dbType = dbType;
    }

    public String getName()
    {
      return name;
    }

    public DBType getDbType()
    {
      return dbType;
    }
  }
}
