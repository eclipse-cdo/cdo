/**
 *  Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This abstract base class provides basic behavior needed for mapping many-valued attributes to tables.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractListTableMapping implements IListMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractListTableMapping.class);

  /**
   * The feature for this mapping.
   */
  private EStructuralFeature feature;

  /**
   * The table of this mapping.
   */
  private IDBTable table;

  /**
   * The type mapping for the value field.
   */
  private ITypeMapping typeMapping;

  /**
   * The associated mapping strategy.
   */
  private IMappingStrategy mappingStrategy;

  // --------- SQL strings - see initSqlStrings() -----------------
  private String sqlSelectChunksPrefix;

  private String sqlOrderByIndex;

  private String sqlInsertEntry;

  private EClass containingClass;

  private String sqlGetListLastIndex;

  public AbstractListTableMapping(IMappingStrategy mappingStrategy, EClass eClass, EStructuralFeature feature)
  {
    this.mappingStrategy = mappingStrategy;
    this.feature = feature;
    containingClass = eClass;

    initTable();
    initSqlStrings();
  }

  private void initTable()
  {
    String tableName = mappingStrategy.getTableName(containingClass, feature);
    table = mappingStrategy.getStore().getDBSchema().addTable(tableName);

    // add fields for keys (cdo_id, version, feature_id)
    FieldInfo[] fields = getKeyFields();
    IDBField[] dbFields = new IDBField[fields.length];

    for (int i = 0; i < fields.length; i++)
    {
      dbFields[i] = table.addField(fields[i].getName(), fields[i].getDbType());
    }

    // add field for list index
    IDBField idxField = table.addField(CDODBSchema.LIST_IDX, DBType.INTEGER);

    // add field for value
    typeMapping = mappingStrategy.createValueMapping(feature);
    typeMapping.createDBField(table, CDODBSchema.LIST_VALUE);

    // add table indexes
    table.addIndex(Type.NON_UNIQUE, dbFields);
    table.addIndex(Type.NON_UNIQUE, idxField);
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
    builder.append(CDODBSchema.LIST_VALUE);
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

    sqlOrderByIndex = " ORDER BY " + CDODBSchema.LIST_IDX;

    // ----------------- count list size --------------------------

    builder = new StringBuilder("SELECT max(");
    builder.append(CDODBSchema.LIST_IDX);
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

    // ----------------- INSERT - reference entry -----------------
    builder = new StringBuilder("INSERT INTO ");
    builder.append(tableName);
    builder.append(" VALUES (");
    for (int i = 0; i < fields.length; i++)
    {
      builder.append("?, ");
    }

    builder.append(" ?, ?)");
    sqlInsertEntry = builder.toString();
  }

  public final EStructuralFeature getFeature()
  {
    return feature;
  }

  public final EClass getContainingClass()
  {
    return containingClass;
  }

  protected final IDBTable getTable()
  {
    return table;
  }

  protected final ITypeMapping getTypeMapping()
  {
    return typeMapping;
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
      TRACER.format("Reading list values for feature {0}.{1} of {2}v{3}", containingClass.getName(), feature.getName(),
          revision.getID(), revision.getVersion());
    }

    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      String sql = sqlSelectChunksPrefix + sqlOrderByIndex;

      pstmt = accessor.getStatementCache().getPreparedStatement(sql, ReuseProbability.HIGH);

      setKeyFields(pstmt, revision);

      // if (TRACER.isEnabled())
      // {
      // TRACER.trace(pstmt.toString());
      // }

      if (listChunk != CDORevision.UNCHUNKED)
      {
        pstmt.setMaxRows(listChunk); // optimization - don't read unneeded rows.
      }

      resultSet = pstmt.executeQuery();

      while ((listChunk == CDORevision.UNCHUNKED || --listChunk >= 0) && resultSet.next())
      {
        Object value = typeMapping.readValue(resultSet, 1);
        if (TRACER.isEnabled())
        {
          TRACER.format("Read value for index {0} from result set: {1}", list.size(), value);
        }

        list.add(value);
      }

      while (listSize-- >= 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Adding UNINITIALIZED for index {0} ", list.size());
        }

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
      accessor.getStatementCache().releasePreparedStatement(pstmt);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list values done for feature {0}.{1} of {2}v{3}", containingClass.getName(), feature
          .getName(), revision.getID(), revision.getVersion());
    }
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
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      pstmt = accessor.getStatementCache().getPreparedStatement(sqlGetListLastIndex, ReuseProbability.HIGH);

      setKeyFields(pstmt, revision);

      // if (TRACER.isEnabled())
      // {
      // TRACER.trace(pstmt.toString());
      // }

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
      accessor.getStatementCache().releasePreparedStatement(pstmt);
    }
  }

  public final void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String where)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading list chunk values for feature {0}.{1} of {2}v{3}", containingClass.getName(), feature
          .getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
    }

    PreparedStatement pstmt = null;
    ResultSet resultSet = null;

    try
    {
      StringBuilder builder = new StringBuilder(sqlSelectChunksPrefix);
      if (where != null)
      {
        builder.append(where);
      }

      builder.append(sqlOrderByIndex);

      String sql = builder.toString();
      pstmt = chunkReader.getAccessor().getStatementCache().getPreparedStatement(sql, ReuseProbability.LOW);
      setKeyFields(pstmt, chunkReader.getRevision());
      // if (TRACER.isEnabled())
      // {
      // TRACER.trace(pstmt.toString());
      // }

      resultSet = pstmt.executeQuery();

      Chunk chunk = null;
      int chunkSize = 0;
      int chunkIndex = 0;
      int indexInChunk = 0;

      while (resultSet.next())
      {
        Object value = typeMapping.readValue(resultSet, 1);

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

        chunk.add(indexInChunk++, value);
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
        TRACER.format("Reading list chunk values done for feature {0}.{1} of {2}v{3}", containingClass.getName(),
            feature.getName(), chunkReader.getRevision().getID(), chunkReader.getRevision().getVersion());
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      chunkReader.getAccessor().getStatementCache().releasePreparedStatement(pstmt);
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
    PreparedStatement stmt = null;

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing value for feature {0}.{1} index {2} of {3}v{4} : {5}", containingClass.getName(), feature
          .getName(), idx, revision.getID(), revision.getVersion(), value);
    }

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlInsertEntry, ReuseProbability.HIGH);

      setKeyFields(stmt, revision);
      int stmtIndex = getKeyFields().length + 1;
      stmt.setInt(stmtIndex++, idx);
      typeMapping.setValue(stmt, stmtIndex++, value);

      CDODBUtil.sqlUpdate(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
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
