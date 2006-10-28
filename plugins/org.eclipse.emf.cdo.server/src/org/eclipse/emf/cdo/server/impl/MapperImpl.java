/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.impl;


import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataOutput;

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.core.CDOResProtocol;
import org.eclipse.emf.cdo.core.ImplementationError;
import org.eclipse.emf.cdo.core.OIDEncoder;
import org.eclipse.emf.cdo.core.WrappedIOException;
import org.eclipse.emf.cdo.core.util.StringHelper;
import org.eclipse.emf.cdo.dbgen.ColumnType;
import org.eclipse.emf.cdo.dbgen.DBGenFactory;
import org.eclipse.emf.cdo.dbgen.Database;
import org.eclipse.emf.cdo.dbgen.IndexType;
import org.eclipse.emf.cdo.dbgen.SQLDialect;
import org.eclipse.emf.cdo.dbgen.Table;
import org.eclipse.emf.cdo.server.AttributeInfo;
import org.eclipse.emf.cdo.server.ClassInfo;
import org.eclipse.emf.cdo.server.ColumnConverter;
import org.eclipse.emf.cdo.server.DatabaseInconsistencyException;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.PackageInfo;
import org.eclipse.emf.cdo.server.PackageManager;
import org.eclipse.emf.cdo.server.ResourceInfo;
import org.eclipse.emf.cdo.server.ResourceManager;
import org.eclipse.emf.cdo.server.ResourceNotFoundException;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import java.io.IOException;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;


/**
 * @author Eike Stepper
 */
public class MapperImpl extends AbstractLifecycle implements Mapper, SQLConstants
{
  protected static final int OBJECT_NOT_FOUND_IN_DB = 0;

  protected static final long OBJECT_NOT_REFERENCED_IN_DB = 0;

  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_MAPPER,
      MapperImpl.class);

  protected ColumnConverter columnConverter;

  protected PackageManager packageManager;

  protected ResourceManager resourceManager;

  protected DataSource dataSource;

  protected JdbcTemplate jdbcTemplate;

  protected String sqlDialectName;

  protected OIDEncoder oidEncoder;

  private int nextPid;

  private int nextRID;

  private int nextCID;

  private transient SQLDialect cachedSqlDialect;

  /**
   * @return Returns the packageManager.
   */
  public PackageManager getPackageManager()
  {
    return packageManager;
  }

  /**
   * @param packageManager The packageManager to set.
   */
  public void setPackageManager(PackageManager packageManager)
  {
    this.packageManager = packageManager;
  }

  /**
   * @return Returns the columnConverter.
   */
  public ColumnConverter getColumnConverter()
  {
    return columnConverter;
  }

  /**
   * @param columnConverter The columnConverter to set.
   */
  public void setColumnConverter(ColumnConverter columnConverter)
  {
    this.columnConverter = columnConverter;
  }

  public ResourceManager getResourceManager()
  {
    return resourceManager;
  }

  public void setResourceManager(ResourceManager resourceManager)
  {
    this.resourceManager = resourceManager;
  }

  /**
   * @return Returns the dataSource.
   */
  public DataSource getDataSource()
  {
    return dataSource;
  }

  /**
   * @param dataSource The dataSource to set.
   */
  public void setDataSource(DataSource dataSource)
  {
    this.dataSource = dataSource;
  }

  /**
   * @return Returns the sqlDialect.
   */
  public SQLDialect getSqlDialect()
  {
    if (cachedSqlDialect == null)
    {
      cachedSqlDialect = DBGenFactory.eINSTANCE.createDialect(sqlDialectName);
    }

    return cachedSqlDialect;
  }

  public String getSqlDialectName()
  {
    return sqlDialectName;
  }

  /**
   * @param sqlDialectName The sqlDialectName to set.
   */
  public void setSqlDialectName(String sqlDialectName)
  {
    this.sqlDialectName = sqlDialectName;
  }

  /**
   * @return Returns the oidEncoder.
   */
  public OIDEncoder getOidEncoder() // Don't change case! Spring will be irritated
  {
    return oidEncoder;
  }

  /**
   * @param oidEncoder The oidEncoder to set.
   */
  public void setOidEncoder(OIDEncoder oidEncoder) // Don't change case! Spring will be irritated
  {
    this.oidEncoder = oidEncoder;
  }

  /**
   * @return Returns the jdbcTemplate.
   */
  public JdbcTemplate getJdbcTemplate()
  {
    return jdbcTemplate;
  }

  /**
   * @param jdbcTemplate The jdbcTemplate to set.
   */
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
  {
    this.jdbcTemplate = jdbcTemplate;
  }

  public int getNextPid()
  {
    return nextPid++;
  }

  public int getNextCID()
  {
    return nextCID++;
  }

  public int getNextRID()
  {
    return nextRID++;
  }

  public long getNextOID(int rid)
  {
    ResourceInfo resourceInfo = resourceManager.getResourceInfo(rid, this);
    if (resourceInfo == null)
    {
      throw new ResourceNotFoundException("Unknown RID: " + rid);
    }

    long nextOIDFragment = resourceInfo.getNextOIDFragment();
    return oidEncoder.getOID(rid, nextOIDFragment);
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (columnConverter == null)
    {
      throw new IllegalStateException("columnConverter == null");
    }

    if (packageManager == null)
    {
      throw new IllegalStateException("packageManager == null");
    }

    if (resourceManager == null)
    {
      throw new IllegalStateException("resourceManager == null");
    }

    if (dataSource == null)
    {
      throw new IllegalStateException("dataSource == null");
    }

    if (oidEncoder == null)
    {
      throw new IllegalStateException("oidEncoder == null");
    }

    if (jdbcTemplate == null)
    {
      throw new IllegalStateException("jdbcTemplate == null");
    }

    initTables();
    initPackages();

    nextPid = selectMaxPid() + 1;
    nextCID = selectMaxCID() + 1;
    nextRID = selectMaxRID() + 1;
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    columnConverter = null;
    dataSource = null;
    jdbcTemplate = null;
    oidEncoder = null;
    packageManager = null;
    resourceManager = null;
    sqlDialectName = null;
    cachedSqlDialect = null;
    super.onDeactivate();
  }

  protected void initTables()
  {
    Database database = DBGenFactory.eINSTANCE.createDatabase();

    //    Table systemTable = database.addTable(SYSTEM_TABLE);
    //    systemTable.addColumn(SYSTEM_SID_COLUMN, ColumnType.VARCHAR_LITERAL, 63);
    //    systemTable.addColumn(SYSTEM_STARTED_COLUMN, ColumnType.BOOLEAN_LITERAL);
    //    systemTable.addSimpleIndex(SYSTEM_SID_COLUMN, IndexType.PRIMARY_LITERAL);

    Table packageTable = database.addTable(PACKAGE_TABLE);
    packageTable.addColumn(PACKAGE_PID_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    packageTable.addColumn(PACKAGE_NAME_COLUMN, ColumnType.VARCHAR_LITERAL, 255, "NOT NULL");
    packageTable.addSimpleIndex(PACKAGE_PID_COLUMN, IndexType.PRIMARY_LITERAL);
    packageTable.addSimpleIndex(PACKAGE_NAME_COLUMN, IndexType.UNIQUE_LITERAL);

    Table classTable = database.addTable(CLASS_TABLE);
    classTable.addColumn(CLASS_CID_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    classTable.addColumn(CLASS_NAME_COLUMN, ColumnType.VARCHAR_LITERAL, 255, "NOT NULL");
    classTable.addColumn(CLASS_PARENTNAME_COLUMN, ColumnType.VARCHAR_LITERAL, 255);
    classTable.addColumn(CLASS_TABLENAME_COLUMN, ColumnType.VARCHAR_LITERAL, 127, "NOT NULL");
    classTable.addColumn(CLASS_PID_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    classTable.addSimpleIndex(CLASS_CID_COLUMN, IndexType.PRIMARY_LITERAL);
    classTable.addSimpleIndex(CLASS_NAME_COLUMN, IndexType.UNIQUE_LITERAL);
    classTable.addSimpleIndex(CLASS_PID_COLUMN, IndexType.NON_UNIQUE_LITERAL);

    Table columnTable = database.addTable(ATTRIBUTE_TABLE);
    columnTable.addColumn(ATTRIBUTE_NAME_COLUMN, ColumnType.VARCHAR_LITERAL, 127, "NOT NULL");
    columnTable.addColumn(ATTRIBUTE_FEATUREID_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    columnTable.addColumn(ATTRIBUTE_DATATYPE_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    columnTable.addColumn(ATTRIBUTE_COLUMNNAME_COLUMN, ColumnType.VARCHAR_LITERAL, 127, "NOT NULL");
    columnTable.addColumn(ATTRIBUTE_COLUMNTYPE_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    columnTable.addColumn(ATTRIBUTE_CID_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    columnTable.addSimpleIndex(ATTRIBUTE_CID_COLUMN, IndexType.NON_UNIQUE_LITERAL);

    Table objectTable = database.addTable(OBJECT_TABLE);
    objectTable.addColumn(OBJECT_OID_COLUMN, ColumnType.BIGINT_LITERAL, "NOT NULL");
    objectTable.addColumn(OBJECT_OCA_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    objectTable.addColumn(OBJECT_CID_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    objectTable.addSimpleIndex(OBJECT_OID_COLUMN, IndexType.PRIMARY_LITERAL);
    objectTable.addSimpleIndex(OBJECT_CID_COLUMN, IndexType.NON_UNIQUE_LITERAL);

    Table resourceTable = database.addTable(RESOURCE_TABLE);
    resourceTable.addColumn(RESOURCE_RID_COLUMN, ColumnType.INTEGER_LITERAL, "NOT NULL");
    resourceTable.addColumn(RESOURCE_PATH_COLUMN, ColumnType.VARCHAR_LITERAL, 255, "NOT NULL");
    resourceTable.addSimpleIndex(RESOURCE_RID_COLUMN, IndexType.PRIMARY_LITERAL);
    resourceTable.addSimpleIndex(RESOURCE_PATH_COLUMN, IndexType.UNIQUE_LITERAL);

    Table contentTable = database.addTable(CONTENT_TABLE);
    contentTable.addColumn(CONTENT_OID_COLUMN, ColumnType.BIGINT_LITERAL, "NOT NULL");
    contentTable.addSimpleIndex(CONTENT_OID_COLUMN, IndexType.PRIMARY_LITERAL);

    Table referenceTable = database.addTable(REFERENCE_TABLE);
    referenceTable.addColumn(REFERENCE_OID_COLUMN, ColumnType.BIGINT_LITERAL);
    referenceTable.addColumn(REFERENCE_FEATUREID_COLUMN, ColumnType.INTEGER_LITERAL);
    referenceTable.addColumn(REFERENCE_ORDINAL_COLUMN, ColumnType.BIGINT_LITERAL);
    referenceTable.addColumn(REFERENCE_TARGET_COLUMN, ColumnType.BIGINT_LITERAL, 0, "NOT NULL");
    referenceTable.addColumn(REFERENCE_CONTAINMENT_COLUMN, ColumnType.BOOLEAN_LITERAL);
    referenceTable.addSimpleIndex(REFERENCE_TARGET_COLUMN, IndexType.NON_UNIQUE_LITERAL);

    // TODO Check if this compound index generally makes preceding simple index superfluous
    referenceTable.addCompoundIndex(new String[] { REFERENCE_TARGET_COLUMN,
        REFERENCE_CONTAINMENT_COLUMN,}, IndexType.NON_UNIQUE_LITERAL);

    // This index can not be a real PK (UNIQUE), since during movement of allReferences
    // it temporarily holds duplicate entries!!!
    referenceTable.addCompoundIndex(new String[] { REFERENCE_OID_COLUMN,
        REFERENCE_FEATUREID_COLUMN, REFERENCE_ORDINAL_COLUMN}, IndexType.NON_UNIQUE_LITERAL);

    getSqlDialect().save(dataSource, database, false);
  }

  protected void initPackages()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, SELECT_PACKAGES);
    }

    jdbcTemplate.query(SELECT_PACKAGES, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        int pid = resultSet.getInt(1);
        String name = resultSet.getString(2);

        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Initializing package: pid=" + pid + ", name=" + name);
        }
        PackageInfo packageInfo = packageManager.addPackage(pid, name);
        initClasses(packageInfo);
      }
    });
  }

  protected void initClasses(final PackageInfo packageInfo)
  {
    // TODO This is NOT necessary!
    // Important to create a new template instance to handle nested select
    JdbcTemplate nestedTemplate = new JdbcTemplate(dataSource);

    Object[] args = { new Integer(packageInfo.getPid())};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_CLASSES, "?", args));
    }

    nestedTemplate.query(SELECT_CLASSES, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        int cid = resultSet.getInt(1);
        String name = resultSet.getString(2);
        String parentName = resultSet.getString(3);
        String tableName = resultSet.getString(4);

        if (parentName != null && parentName.length() == 0)
        {
          parentName = null;
        }

        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Initializing class: cid=" + cid + ", name=" + name + ", parentName="
              + parentName + ", tableName=" + tableName);
        }
        ClassInfo classInfo = packageInfo.addClass(cid, name, parentName, tableName);
        initAttributes(classInfo);

        if (cid > nextCID)
        {
          nextCID = cid;
        }
      }
    });

    ++nextCID;
  }

  protected void initAttributes(final ClassInfo classInfo)
  {
    // Important to create a new template instance to handle nested select
    JdbcTemplate nestedTemplate = new JdbcTemplate(dataSource);

    Object[] args = { new Integer(classInfo.getCID())};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_ATTRIBUTES, "?", args));
    }

    nestedTemplate.query(SELECT_ATTRIBUTES, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        String name = resultSet.getString(1);
        int featureId = resultSet.getInt(2);
        int dataType = resultSet.getInt(3);
        String columnName = resultSet.getString(4);
        int columnType = resultSet.getInt(5);

        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Initializing attribute: name=" + name + ", featureId=" + featureId
              + ", dataType=" + dataType + ", columnName=" + columnName + ", columnType="
              + columnType);
        }
        classInfo.addAttribute(name, featureId, dataType, columnName, columnType);
      }
    });
  }

  public void insertPackage(final PackageInfo packageInfo)
  {
    sql(INSERT_PACKAGE, new Object[] { packageInfo.getPid(), packageInfo.getName()});
  }

  public void insertClass(final ClassInfo classInfo)
  {
    String parentName = classInfo.getParentName() == null ? "" : classInfo.getParentName();
    sql(INSERT_CLASS, new Object[] { classInfo.getCID(), classInfo.getName(), parentName,
        classInfo.getTableName(), classInfo.getPackageInfo().getPid()});
  }

  public void insertAttribute(final AttributeInfo attributeInfo, final int cid)
  {
    sql(INSERT_ATTRIBUTE, new Object[] { attributeInfo.getName(), attributeInfo.getFeatureID(),
        attributeInfo.getDataType(), attributeInfo.getColumnName(), attributeInfo.getColumnType(),
        cid});
  }

  public ResourceInfo selectResourceInfo(String path)
  {
    Object[] args = { path};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_RID_OF_RESOURCE, "?", args));
    }

    final int[] rows = new int[1];
    final ResourceInfoImpl result = new ResourceInfoImpl();
    result.setPath(path);

    jdbcTemplate.query(SELECT_RID_OF_RESOURCE, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        result.setRID(resultSet.getInt(1));
        ++rows[0];
      }
    });

    if (rows[0] != 1)
    {
      return null;
    }

    long nextOIDFragment = selectMaxOIDFragment(result.getRID()) + 1;
    result.setNextOIDFragment(nextOIDFragment);

    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Selected " + result);
    }
    return result;
  }

  public ResourceInfo selectResourceInfo(int rid)
  {
    Object[] args = { new Integer(rid)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_PATH_OF_RESOURCE, "?", args));
    }

    final int[] rows = new int[1];
    final ResourceInfoImpl result = new ResourceInfoImpl();
    result.setRID(rid);

    jdbcTemplate.query(SELECT_PATH_OF_RESOURCE, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        result.setPath(resultSet.getString(1));
        ++rows[0];
      }
    });

    if (rows[0] != 1)
    {
      return null;
    }

    long nextOIDFragment = selectMaxOIDFragment(result.getRID()) + 1;
    result.setNextOIDFragment(nextOIDFragment);

    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Selected " + result);
    }
    return result;
  }

  protected long selectMaxOIDFragment(int rid)
  {
    Object[] args = ridBounds(rid);
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_MAX_OID_FRAGMENT, "?", args));
    }
    long oid = jdbcTemplate.queryForLong(SELECT_MAX_OID_FRAGMENT, args);
    return oidEncoder.getOIDFragment(oid);
  }

  private Object[] ridBounds(int rid)
  {
    long lowerBound = oidEncoder.getOID(rid, 1);
    long upperBound = oidEncoder.getOID(rid + 1, 1) - 1;
    return new Object[] { new Long(lowerBound), new Long(upperBound)};
  }

  private int selectMaxPid()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, SELECT_MAX_PID);
    }
    return jdbcTemplate.queryForInt(SELECT_MAX_PID);
  }

  private int selectMaxCID()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, SELECT_MAX_CID);
    }
    return jdbcTemplate.queryForInt(SELECT_MAX_CID);
  }

  private int selectMaxRID()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, SELECT_MAX_RID);
    }
    return jdbcTemplate.queryForInt(SELECT_MAX_RID);
  }

  public ResourceInfo createResource(String resourcePath)
  {
    int rid = getNextRID();
    sql(INSERT_RESOURCE, new Object[] { rid, resourcePath});
    return resourceManager.registerResourceInfo(resourcePath, rid, 1);
  }

  public int getCollectionCount(long oid, int feature)
  {
    Object[] args = { new Long(oid), new Integer(feature)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_COLLECTION_COUNT, "?", args));
    }

    return jdbcTemplate.queryForInt(SELECT_COLLECTION_COUNT, args);
  }

  public boolean lock(long oid, int oca)
  {
    Object[] args = { new Long(oid), new Integer(oca)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(DO_OPTIMISTIC_CONTROL, "?", args));
    }

    int changed = jdbcTemplate.update(DO_OPTIMISTIC_CONTROL, args);
    return changed == 1;
  }

  public void insertResource(int rid, String path)
  {
    sql(INSERT_RESOURCE, new Object[] { rid, path});
  }

  public void insertReference(long oid, int feature, int ordinal, long target, boolean containment)
  {
    sql(INSERT_REFERENCE, new Object[] { oid, feature, ordinal, target, containment});
  }

  public void removeReference(long oid, int feature, int ordinal)
  {
    sql(REMOVE_REFERENCE, new Object[] { oid, feature, ordinal});
  }

  public void moveReferenceAbsolute(long oid, int feature, int toIndex, int fromIndex)
  {
    sql(MOVE_REFERENCE_ABSOLUTE, new Object[] { toIndex, oid, feature, fromIndex});
  }

  public void moveReferencesRelative(long oid, int feature, int startIndex, int endIndex, int offset)
  {
    Object[] args = { new Integer(offset), new Long(oid), new Integer(feature),
        new Integer(startIndex), new Integer(endIndex)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(MOVE_REFERENCES_RELATIVE, "?", args));
    }

    // ignore number of affected rows
    jdbcTemplate.update(MOVE_REFERENCES_RELATIVE, args);
  }

  public void insertObject(long oid, int cid)
  {
    sql(INSERT_OBJECT, new Object[] { new Long(oid), new Integer(cid)});
  }

  protected void removeReferences(long oid)
  {
    Object[] args = { oid};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(REMOVE_REFERENCES, "?", args));
    }

    jdbcTemplate.update(REMOVE_REFERENCES, args);
  }

  public void removeObject(long oid, int cid)
  {
    ClassInfo classInfo = packageManager.getClassInfo(cid);
    while (classInfo != null)
    {
      removeUserSegment(oid, classInfo.getTableName());
      classInfo = classInfo.getParent();
    }

    removeSegment(oid);
    removeReferences(oid); // TODO optimize for objects with no refs
  }

  public void removeObject(long oid)
  {
    int cid = selectCIDOfObject(oid);
    removeObject(oid, cid);
  }

  protected int selectCIDOfObject(long oid)
  {
    Object[] args = { new Long(oid)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_CID_OF_OBJECT, "?", args));
    }

    try
    {
      return jdbcTemplate.queryForInt(SELECT_CID_OF_OBJECT, args);
    }
    catch (IncorrectResultSizeDataAccessException ex)
    {
      return OBJECT_NOT_FOUND_IN_DB;
    }
  }

  protected void removeSegment(long oid)
  {
    StringBuffer query = new StringBuffer("DELETE FROM ");
    query.append(OBJECT_TABLE);
    query.append(" WHERE ");
    query.append(OBJECT_OID_COLUMN);
    query.append("=");
    query.append(oid);

    sql(query.toString());
  }

  protected void removeUserSegment(long oid, String tableName)
  {
    StringBuffer query = new StringBuffer("DELETE FROM ");
    query.append(tableName);
    query.append(" WHERE ");
    query.append(USER_OID_COLUMN);
    query.append("=");
    query.append(oid);

    sql(query.toString());
  }

  public void insertContent(long oid)
  {
    sql(INSERT_CONTENT, new Object[] { oid});
  }

  public void removeContent(long oid)
  {
    sql(REMOVE_CONTENT, new Object[] { oid});
  }

  public void sql(String sql)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, sql);
    }

    int rows = jdbcTemplate.update(sql);

    if (rows != 1)
    {
      throw new DatabaseInconsistencyException(sql);
    }
  }

  public void sql(String sql, Object[] args)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(sql, "?", args));
    }

    int rows = jdbcTemplate.update(sql, args);

    if (rows != 1)
    {
      throw new DatabaseInconsistencyException(sql);
    }
  }

  public void sql(String sql, Object[] args, int[] types)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(sql, "?", args));
    }

    int rows = jdbcTemplate.update(sql, args, types);
    if (rows != 1)
    {
      throw new DatabaseInconsistencyException(sql);
    }
  }

  public void transmitContent(final ExtendedDataOutput out, ResourceInfo resourceInfo)
      throws IOException
  {
    if (resourceInfo != null)
    {
      Object[] args = ridBounds(resourceInfo.getRID());
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, StringHelper.replaceWildcards(TRANSMIT_CONTENT, "?", args));
      }

      try
      {
        jdbcTemplate.query(TRANSMIT_CONTENT, args, new RowCallbackHandler()
        {
          public void processRow(ResultSet resultSet) throws SQLException
          {
            try
            {
              long oid = resultSet.getLong(1);
              int oca = resultSet.getInt(2);
              int cid = resultSet.getInt(3);

              if (TRACER.isEnabled())
              {
                TRACER.trace(this, "Object: oid=" + oidEncoder.toString(oid) + ", oca=" + oca
                    + ", cid=" + cid);
              }

              out.writeLong(oid);
              out.writeInt(oca);
              out.writeInt(cid);

              ClassInfo classInfo = packageManager.getClassInfo(cid);
              if (classInfo == null)
              {
                throw new ImplementationError("Unknown cid " + cid);
              }

              transmitAttributes(out, oid, classInfo);
              transmitReferences(out, oid);
            }
            catch (IOException ex)
            {
              throw new WrappedIOException(ex);
            }
          }
        });
      }
      catch (WrappedIOException ex)
      {
        ex.reThrow();
      }
    }

    out.writeLong(0);
  }

  public void transmitObject(final ExtendedDataOutput out, final long oid) throws IOException
  {
    Object[] args = { new Long(oid)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(TRANSMIT_OBJECT, "?", args));
    }

    try
    {
      jdbcTemplate.query(TRANSMIT_OBJECT, args, new RowCallbackHandler()
      {
        public void processRow(ResultSet resultSet) throws SQLException
        {
          try
          {
            int oca = resultSet.getInt(1);
            int cid = resultSet.getInt(2);

            if (TRACER.isEnabled())
            {
              TRACER.trace(this, "Object: oid=" + oidEncoder.toString(oid) + ", oca=" + oca
                  + ", cid=" + cid);
            }

            out.writeLong(oid);
            out.writeInt(oca);
            out.writeInt(cid);

            ClassInfo classInfo = packageManager.getClassInfo(cid);
            if (classInfo == null)
            {
              throw new ImplementationError("Unknown cid " + cid);
            }

            transmitContainers(out, oid);
            transmitAttributes(out, oid, classInfo);
            transmitReferences(out, oid);
          }
          catch (IOException ex)
          {
            throw new WrappedIOException(ex);
          }
        }
      });
    }
    catch (WrappedIOException ex)
    {
      ex.reThrow();
    }

    out.writeLong(0);
  }

  public void transmitContainers(final ExtendedDataOutput out, long oid) throws IOException
  {
    class Container
    {
      public long oid;

      public int cid;

      public Container(long oid, int cid)
      {
        this.oid = oid;
        this.cid = cid;
      }
    }

    final List containers = new LinkedList();
    final long[] child = { oid};
    while (child[0] != 0)
    {
      Object[] args = { new Long(child[0]), Boolean.TRUE};
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, StringHelper.replaceWildcards(SELECT_CONTAINER_OF_OBJECT, "?", args));
      }

      child[0] = 0;
      jdbcTemplate.query(SELECT_CONTAINER_OF_OBJECT, args, new RowCallbackHandler()
      {
        public void processRow(ResultSet resultSet) throws SQLException
        {
          long oid = resultSet.getLong(1);
          int cid = resultSet.getInt(2);
          containers.add(0, new Container(oid, cid));
          child[0] = oid;
        }
      });
    }

    out.writeInt(containers.size());
    for (Iterator it = containers.iterator(); it.hasNext();)
    {
      Container container = (Container) it.next();
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, "Container: oid=" + oidEncoder.toString(container.oid) + ", cid="
            + container.cid);
      }

      out.writeLong(container.oid);
      out.writeInt(container.cid);
    }
  }

  public void transmitReferences(final ExtendedDataOutput out, long oid) throws IOException
  {
    Object[] args = { new Long(oid)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(TRANSMIT_REFERENCES, "?", args));
    }

    try
    {
      jdbcTemplate.query(TRANSMIT_REFERENCES, args, new RowCallbackHandler()
      {
        public void processRow(ResultSet resultSet) throws SQLException
        {
          try
          {
            int feature = resultSet.getInt(1);
            long target = resultSet.getLong(2);
            int cid = resultSet.getInt(3);
            if (TRACER.isEnabled())
            {
              TRACER.trace(this, "Reference: feature=" + feature + ", target="
                  + oidEncoder.toString(target) + ", cid=" + cid);
            }

            out.writeInt(feature);
            out.writeLong(target);
            out.writeInt(cid);
          }
          catch (IOException ex)
          {
            throw new WrappedIOException(ex);
          }
        }
      });
    }
    catch (WrappedIOException ex)
    {
      ex.reThrow();
    }

    out.writeInt(-1);
  }

  public void transmitAttributes(final ExtendedDataOutput out, long oid, ClassInfo classInfo)
      throws IOException
  {
    while (classInfo != null)
    {
      String columnNames = classInfo.getColumnNames();
      if (columnNames != null && columnNames.length() > 0)
      {
        final ClassInfo finalClassInfo = classInfo;
        String sql = "SELECT " + columnNames + " FROM " + classInfo.getTableName() + " WHERE "
            + USER_OID_COLUMN + "=?";

        Object[] args = { new Long(oid)};
        if (TRACER.isEnabled())
        {
          TRACER.trace(this, StringHelper.replaceWildcards(sql, "?", args));
        }

        try
        {
          jdbcTemplate.query(sql, args, new RowCallbackHandler()
          {
            public void processRow(ResultSet resultSet) throws SQLException
            {
              try
              {
                AttributeInfo[] attributeInfos = finalClassInfo.getAttributeInfos();
                for (int i = 0; i < attributeInfos.length; i++)
                {
                  AttributeInfo attributeInfo = attributeInfos[i];

                  Object value = resultSet.getObject(i + 1);
                  columnConverter.toChannel(out, attributeInfo.getDataType(), value);
                }
              }
              catch (IOException ex)
              {
                throw new WrappedIOException(ex);
              }
            }
          });
        }
        catch (WrappedIOException ex)
        {
          ex.reThrow();
        }
      }

      classInfo = classInfo.getParent();
    }
  }

  public void transmitAllResources(final ExtendedDataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Querying all resources");
      TRACER.trace(this, SELECT_ALL_RESOURCES);
    }

    try
    {
      jdbcTemplate.query(SELECT_ALL_RESOURCES, new RowCallbackHandler()
      {
        public void processRow(ResultSet resultSet) throws SQLException
        {
          try
          {
            int rid = resultSet.getInt(1);
            String path = resultSet.getString(2);
            out.writeInt(rid);
            out.writeString(path);
          }
          catch (IOException ex)
          {
            throw new WrappedIOException(ex);
          }
        }
      });
    }
    catch (WrappedIOException ex)
    {
      ex.reThrow();
    }

    out.writeInt(CDOResProtocol.NO_MORE_RESOURCES);
  }

  public void deleteResource(int rid)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Deleting resource: rid=" + rid);
    }

    sql(DELETE_RESOURCE, new Object[] { rid});

    Object[] args = ridBounds(rid);
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_ALL_OBJECTS_OF_RESOURCE, "?", args));
    }

    jdbcTemplate.query(SELECT_ALL_OBJECTS_OF_RESOURCE, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        long oid = resultSet.getLong(1);
        int cid = resultSet.getInt(2);

        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Deleting object: oid=" + oidEncoder.toString(oid) + ", cid=" + cid);
        }

        removeObject(oid, cid);
      }
    });
  }

  public Set<Long> removeStaleReferences()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Removing stale references");
    }

    final Set<Long> modifiedOIDs = new HashSet<Long>();
    jdbcTemplate.query(SELECT_ALL_STALE_REFERENCES, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        long oid = resultSet.getLong(1);
        int feature = resultSet.getInt(2);
        int ordinal = resultSet.getInt(3);

        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Reference: oid=" + oidEncoder.toString(oid) + ", feature=" + feature
              + ", ordinal=" + ordinal);
        }

        removeReference(oid, feature, ordinal);
        modifiedOIDs.add(oid);
      }
    });

    return modifiedOIDs;
  }

  public void transmitExtent(final ExtendedDataOutput out, final int context,
      final boolean exactMatch, int rid) throws IOException
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("SELECT ");
    buffer.append(OBJECT_OID_COLUMN);
    buffer.append(exactMatch ? "" : ", " + OBJECT_CID_COLUMN);
    buffer.append(" FROM ");
    buffer.append(OBJECT_TABLE);
    buffer.append(" WHERE ");
    buffer.append(OBJECT_CID_COLUMN);
    buffer.append(" IN (");
    buffer.append(context);
    if (!exactMatch)
    {
      ClassInfo classInfo = packageManager.getClassInfo(context);
      List<ClassInfo> subClasses = classInfo.getSubClasses();
      for (ClassInfo info : subClasses)
      {
        buffer.append(", ");
        buffer.append(info.getCID());
      }
    }

    buffer.append(")");
    if (rid != CDOProtocol.GLOBAL_EXTENT)
    {
      Object[] bounds = ridBounds(rid);
      buffer.append(" AND ");
      buffer.append(OBJECT_OID_COLUMN);
      buffer.append(" BETWEEN ");
      buffer.append(bounds[0]);
      buffer.append(" AND ");
      buffer.append(bounds[1]);
    }

    buffer.append(" ORDER BY ");
    buffer.append(OBJECT_OID_COLUMN);
    String sql = buffer.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, sql);
    }

    try
    {
      jdbcTemplate.query(sql, new RowCallbackHandler()
      {
        public void processRow(ResultSet resultSet) throws SQLException
        {
          try
          {
            long oid = resultSet.getLong(1);
            int cid = exactMatch ? context : resultSet.getInt(2);
            if (TRACER.isEnabled())
            {
              TRACER.trace(this, "Extent: oid=" + oidEncoder.toString(oid)
                  + (exactMatch ? "" : ", cid=" + cid));
            }

            out.writeLong(oid);
            if (!exactMatch)
            {
              out.writeInt(cid);
            }
          }
          catch (IOException ex)
          {
            throw new WrappedIOException(ex);
          }
        }
      });
    }
    catch (WrappedIOException ex)
    {
      ex.reThrow();
    }

    out.writeLong(CDOProtocol.NO_MORE_OBJECTS);
  }

  public void transmitXRefs(final ExtendedDataOutput out, final long oid, int rid)
      throws IOException
  {
    Object[] args = { new Long(oid)};
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, StringHelper.replaceWildcards(SELECT_XREFS_OF_OBJECT, "?", args));
    }

    try
    {
      jdbcTemplate.query(SELECT_XREFS_OF_OBJECT, args, new RowCallbackHandler()
      {
        public void processRow(ResultSet resultSet) throws SQLException
        {
          try
          {
            long referer = resultSet.getLong(1);
            int feature = resultSet.getInt(2);
            int cid = resultSet.getInt(3);
            if (TRACER.isEnabled())
            {
              TRACER.trace(this, "XRef: referer=" + oidEncoder.toString(referer) + ", feature="
                  + feature + ", cid=" + cid);
            }

            out.writeLong(referer);
            out.writeInt(feature);
            out.writeInt(cid);
          }
          catch (IOException ex)
          {
            throw new WrappedIOException(ex);
          }
        }
      });
    }
    catch (WrappedIOException ex)
    {
      ex.reThrow();
    }

    out.writeLong(CDOProtocol.NO_MORE_OBJECTS);
  }

  public void createAttributeTables(PackageInfo packageInfo)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Creating attribute tables");
    }

    Database database = DBGenFactory.eINSTANCE.createDatabase();
    ClassInfo[] classes = packageInfo.getClasses();

    for (int i = 0; i < classes.length; i++)
    {
      ClassInfo classInfo = classes[i];
      createAttributeTable(classInfo, database);
    }

    getSqlDialect().save(dataSource, database, false);
  }

  private void createAttributeTable(ClassInfo classInfo, Database database)
  {
    Table segmentTable = database.addTable(classInfo.getTableName());
    segmentTable.addColumn(USER_OID_COLUMN, ColumnType.BIGINT_LITERAL, "NOT NULL");

    AttributeInfo[] attributeInfos = classInfo.getAttributeInfos();
    for (int i = 0; i < attributeInfos.length; i++)
    {
      AttributeInfo attributeInfo = attributeInfos[i];

      String columnName = attributeInfo.getColumnName();
      ColumnType columnType = ColumnType.get(attributeInfo.getColumnType());
      segmentTable.addColumn(columnName, columnType);
    }

    segmentTable.addSimpleIndex(USER_OID_COLUMN, IndexType.PRIMARY_LITERAL);
  }
}
