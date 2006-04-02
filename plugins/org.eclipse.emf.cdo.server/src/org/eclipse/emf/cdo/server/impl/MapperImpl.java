/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.cdo.server.impl;


import org.eclipse.net4j.core.Channel;
import org.eclipse.net4j.spring.ValidationException;
import org.eclipse.net4j.spring.impl.ServiceImpl;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.StringHelper;

import org.eclipse.emf.cdo.core.CDOResProtocol;
import org.eclipse.emf.cdo.core.OIDEncoder;
import org.eclipse.emf.cdo.dbgen.ColumnType;
import org.eclipse.emf.cdo.dbgen.DBGenFactory;
import org.eclipse.emf.cdo.dbgen.Database;
import org.eclipse.emf.cdo.dbgen.IndexType;
import org.eclipse.emf.cdo.dbgen.SQLDialect;
import org.eclipse.emf.cdo.dbgen.Table;
import org.eclipse.emf.cdo.dbgen.internal.DBGenActivator;
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

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;


public class MapperImpl extends ServiceImpl implements Mapper, SQLConstants
{
  protected static final int OBJECT_NOT_FOUND_IN_DB = 0;

  protected static final long OBJECT_NOT_REFERENCED_IN_DB = 0;

  protected ColumnConverter columnConverter;

  protected PackageManager packageManager;

  protected ResourceManager resourceManager;

  protected DataSource dataSource;

  protected JdbcTemplate jdbcTemplate;

  protected String sqlDialectName;

  protected OIDEncoder oIDEncoder;

  private int nextPid;

  private int nextRid;

  private int nextCid;

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
    doSet("packageManager", packageManager);
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
    doSet("columnConverter", columnConverter);
  }

  public ResourceManager getResourceManager()
  {
    return resourceManager;
  }

  public void setResourceManager(ResourceManager resourceManager)
  {
    doSet("resourceManager", resourceManager);
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
    doSet("dataSource", dataSource);
  }

  /**
   * @return Returns the sqlDialect.
   */
  public SQLDialect getSqlDialect()
  {
    if (cachedSqlDialect == null)
    {
      cachedSqlDialect = DBGenActivator.INSTANCE.createDialect(sqlDialectName);
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
    doSet("sqlDialectName", sqlDialectName);
  }

  /**
   * @return Returns the oIDEncoder.
   */
  public OIDEncoder getOidEncoder()
  {
    return oIDEncoder;
  }

  /**
   * @param oIDEncoder The oIDEncoder to set.
   */
  public void setOidEncoder(OIDEncoder oIDEncoder)
  {
    doSet("oIDEncoder", oIDEncoder);
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
    doSet("jdbcTemplate", jdbcTemplate);
  }

  public int getNextPid()
  {
    return nextPid++;
  }

  public int getNextCid()
  {
    return nextCid++;
  }

  public int getNextRid()
  {
    return nextRid++;
  }

  public long getNextOid(int rid)
  {
    ResourceInfo resourceInfo = resourceManager.getResourceInfo(rid, this);
    if (resourceInfo == null) throw new ResourceNotFoundException("Unknown RID: " + rid);

    long nextOidFragment = resourceInfo.getNextOIDFragment();
    return oIDEncoder.getOID(rid, nextOidFragment);
  }

  protected void validate() throws ValidationException
  {
    super.validate();
    assertNotNull("columnConverter");
    assertNotNull("packageManager");
    assertNotNull("resourceManager");
    assertNotNull("dataSource");
    assertNotNull("oIDEncoder");
    assertNotNull("jdbcTemplate");

    initTables();
    initPackages();

    nextPid = selectMaxPid() + 1;
    nextCid = selectMaxCid() + 1;
    nextRid = selectMaxRid() + 1;
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
    referenceTable.addColumn(REFERENCE_CONTENT_COLUMN, ColumnType.BOOLEAN_LITERAL);
    referenceTable.addSimpleIndex(REFERENCE_TARGET_COLUMN, IndexType.NON_UNIQUE_LITERAL);

    // TODO Check if this compound index generally makes preceding simple index superfluous
    referenceTable.addCompoundIndex(new String[] { REFERENCE_TARGET_COLUMN,
        REFERENCE_CONTENT_COLUMN,}, IndexType.NON_UNIQUE_LITERAL);

    // This index can not be a real PK (UNIQUE), since during movement of allReferences
    // it temporarily holds duplicate entries!!!
    referenceTable.addCompoundIndex(new String[] { REFERENCE_OID_COLUMN,
        REFERENCE_FEATUREID_COLUMN, REFERENCE_ORDINAL_COLUMN}, IndexType.NON_UNIQUE_LITERAL);

    getSqlDialect().save(dataSource, database, false);
  }

  protected void initPackages()
  {
    if (isDebugEnabled()) debug(SELECT_PACKAGES);
    jdbcTemplate.query(SELECT_PACKAGES, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        int pid = resultSet.getInt(1);
        String name = resultSet.getString(2);

        if (isDebugEnabled()) debug("Initializing package: pid=" + pid + ", name=" + name);
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
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(SELECT_CLASSES, "?", args));

    nestedTemplate.query(SELECT_CLASSES, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        int cid = resultSet.getInt(1);
        String name = resultSet.getString(2);
        String parentName = resultSet.getString(3);
        String tableName = resultSet.getString(4);

        if (isDebugEnabled())
          debug("Initializing class: cid=" + cid + ", name=" + name + ", parentName=" + parentName
              + ", tableName=" + tableName);
        ClassInfo classInfo = packageInfo.addClass(cid, name, parentName, tableName);
        initAttributes(classInfo);

        if (cid > nextCid)
        {
          nextCid = cid;
        }
      }
    });

    ++nextCid;
  }

  protected void initAttributes(final ClassInfo classInfo)
  {
    // Important to create a new template instance to handle nested select
    JdbcTemplate nestedTemplate = new JdbcTemplate(dataSource);

    Object[] args = { new Integer(classInfo.getCid())};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(SELECT_ATTRIBUTES, "?", args));

    nestedTemplate.query(SELECT_ATTRIBUTES, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        String name = resultSet.getString(1);
        int featureId = resultSet.getInt(2);
        int dataType = resultSet.getInt(3);
        String columnName = resultSet.getString(4);
        int columnType = resultSet.getInt(5);

        if (isDebugEnabled())
          debug("Initializing attribute: name=" + name + ", featureId=" + featureId + ", dataType="
              + dataType + ", columnName=" + columnName + ", columnType=" + columnType);
        classInfo.addAttribute(name, featureId, dataType, columnName, columnType);
      }
    });
  }

  public void insertPackage(final PackageInfo packageInfo)
  {
    sql(INSERT_PACKAGE, new Object[] { new Integer(packageInfo.getPid()), packageInfo.getName()});
  }

  public void insertClass(final ClassInfo classInfo)
  {
    sql(INSERT_CLASS, new Object[] { new Integer(classInfo.getCid()), classInfo.getName(),
        classInfo.getParentName(), classInfo.getTableName(),
        new Integer(classInfo.getPackageInfo().getPid())});
  }

  public void insertAttribute(final AttributeInfo attributeInfo, final int cid)
  {
    sql(INSERT_ATTRIBUTE,
        new Object[] { attributeInfo.getName(), new Integer(attributeInfo.getFeatureID()),
            new Integer(attributeInfo.getDataType()), attributeInfo.getColumnName(),
            new Integer(attributeInfo.getColumnType()), new Integer(cid)});
  }

  public ResourceInfo selectResourceInfo(String path)
  {
    Object[] args = { path};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(SELECT_RID_OF_RESOURCE, "?", args));

    final int[] rows = new int[1];
    final ResourceInfoImpl result = new ResourceInfoImpl();
    result.setPath(path);

    jdbcTemplate.query(SELECT_RID_OF_RESOURCE, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        result.setRid(resultSet.getInt(1));
        ++rows[0];
      }
    });

    if (rows[0] != 1)
    {
      return null;
    }

    long nextOidFragment = selectMaxOidFragment(result.getRid()) + 1;
    result.setNextOIDFragment(nextOidFragment);

    if (isDebugEnabled()) debug("Selected " + result);
    return result;
  }

  public ResourceInfo selectResourceInfo(int rid)
  {
    Object[] args = { new Integer(rid)};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(SELECT_PATH_OF_RESOURCE, "?", args));

    final int[] rows = new int[1];
    final ResourceInfoImpl result = new ResourceInfoImpl();
    result.setRid(rid);

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

    long nextOidFragment = selectMaxOidFragment(result.getRid()) + 1;
    result.setNextOIDFragment(nextOidFragment);

    if (isDebugEnabled()) debug("Selected " + result);
    return result;
  }

  protected int selectMaxOidFragment(int rid)
  {
    Object[] args = ridBounds(rid);
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(SELECT_MAX_OID_FRAGMENT, "?", args));
    long oid = jdbcTemplate.queryForLong(SELECT_MAX_OID_FRAGMENT, args);
    return (int) (oid & 0xFFFFFFFFL); // TODO Without OIDEncoder???
  }

  private Object[] ridBounds(int rid)
  {
    long lowerBound = oIDEncoder.getOID(rid, 1);
    long upperBound = oIDEncoder.getOID(rid + 1, 1) - 1;
    return new Object[] { new Long(lowerBound), new Long(upperBound)};
  }

  private int selectMaxPid()
  {
    if (isDebugEnabled()) debug(SELECT_MAX_PID);
    return jdbcTemplate.queryForInt(SELECT_MAX_PID);
  }

  private int selectMaxCid()
  {
    if (isDebugEnabled()) debug(SELECT_MAX_CID);
    return jdbcTemplate.queryForInt(SELECT_MAX_CID);
  }

  private int selectMaxRid()
  {
    if (isDebugEnabled()) debug(SELECT_MAX_RID);
    return jdbcTemplate.queryForInt(SELECT_MAX_RID);
  }

  public ResourceInfo createResource(String resourcePath)
  {
    int rid = getNextRid();
    sql(INSERT_RESOURCE, new Object[] { new Integer(rid), resourcePath});
    return resourceManager.registerResourceInfo(resourcePath, rid, 1);
  }

  public int getCollectionCount(long oid, int feature)
  {
    Object[] args = { new Long(oid), new Integer(feature)};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(SELECT_COLLECTION_COUNT, "?", args));

    return jdbcTemplate.queryForInt(SELECT_COLLECTION_COUNT, args);
  }

  public boolean lock(long oid, int oca)
  {
    Object[] args = { new Long(oid), new Integer(oca)};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(DO_OPTIMISTIC_CONTROL, "?", args));

    int changed = jdbcTemplate.update(DO_OPTIMISTIC_CONTROL, args);
    return changed == 1;
  }

  public void insertResource(int rid, String path)
  {
    sql(INSERT_RESOURCE, new Object[] { new Integer(rid), path});
  }

  public void insertReference(long oid, int feature, int ordinal, long target, boolean content)
  {
    sql(INSERT_REFERENCE, new Object[] { new Long(oid), new Integer(feature), new Integer(ordinal),
        new Long(target), new Boolean(content)});
  }

  public void removeReference(long oid, int feature, int ordinal)
  {
    sql(REMOVE_REFERENCE, new Object[] { new Long(oid), new Integer(feature), new Integer(ordinal)});
  }

  public void moveReferenceAbsolute(long oid, int feature, int toIndex, int fromIndex)
  {
    sql(MOVE_REFERENCE_ABSOLUTE, new Object[] { new Integer(toIndex), new Long(oid),
        new Integer(feature), new Integer(fromIndex)});
  }

  public void moveReferencesRelative(long oid, int feature, int startIndex, int endIndex, int offset)
  {
    Object[] args = { new Integer(offset), new Long(oid), new Integer(feature),
        new Integer(startIndex), new Integer(endIndex)};
    if (isDebugEnabled())
      debug(StringHelper.replaceWildcards(MOVE_REFERENCES_RELATIVE, "?", args));

    // ignore number of affected rows
    jdbcTemplate.update(MOVE_REFERENCES_RELATIVE, args);
  }

  public void insertObject(long oid, int cid)
  {
    sql(INSERT_OBJECT, new Object[] { new Long(oid), new Integer(cid)});
  }

  protected void removeReferences(long oid)
  {
    Object[] args = { new Long(oid)};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(REMOVE_REFERENCES, "?", args));

    jdbcTemplate.update(REMOVE_REFERENCES, args);
  }

  public void removeObject(long oid)
  {
    removeReferences(oid); // TODO optimize for objects with no refs

    int cid = selectCidOfObject(oid);
    ClassInfo classInfo = packageManager.getClassInfo(cid);

    while (classInfo != null)
    {
      removeSegment(oid, classInfo.getTableName());
      classInfo = classInfo.getParent();
    }

    removeSegment(oid, OBJECT_TABLE);
  }

  protected int selectCidOfObject(long oid)
  {
    Object[] args = { new Long(oid)};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(SELECT_CID_OF_OBJECT, "?", args));

    try
    {
      return jdbcTemplate.queryForInt(SELECT_CID_OF_OBJECT, args);
    }
    catch (IncorrectResultSizeDataAccessException ex)
    {
      return OBJECT_NOT_FOUND_IN_DB;
    }
  }

  protected void removeSegment(long oid, String tableName)
  {
    StringBuffer query = new StringBuffer("DELETE FROM ");
    query.append(tableName);
    query.append(" WHERE ");
    query.append(OBJECT_OID_COLUMN);
    query.append("=");
    query.append(oid);

    sql(query.toString());
  }

  public void insertContent(long oid)
  {
    sql(INSERT_CONTENT, new Object[] { new Long(oid)});
  }

  public void removeContent(long oid)
  {
    sql(REMOVE_CONTENT, new Object[] { new Long(oid)});
  }

  public void sql(String sql)
  {
    if (isDebugEnabled()) debug(sql);

    int rows = jdbcTemplate.update(sql);

    if (rows != 1)
    {
      throw new DatabaseInconsistencyException(sql);
    }
  }

  public void sql(String sql, Object[] args)
  {
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(sql, "?", args));

    int rows = jdbcTemplate.update(sql, args);

    if (rows != 1)
    {
      throw new DatabaseInconsistencyException(sql);
    }
  }

  public void transmitContent(final Channel channel, ResourceInfo resourceInfo)
  {
    if (resourceInfo != null)
    {
      Object[] args = ridBounds(resourceInfo.getRid());
      if (isDebugEnabled()) debug(StringHelper.replaceWildcards(TRANSMIT_CONTENT, "?", args));

      jdbcTemplate.query(TRANSMIT_CONTENT, args, new RowCallbackHandler()
      {
        public void processRow(ResultSet resultSet) throws SQLException
        {
          long oid = resultSet.getLong(1);
          int oca = resultSet.getInt(2);
          int cid = resultSet.getInt(3);

          if (isDebugEnabled())
            debug("Object: oid=" + oIDEncoder.toString(oid) + ", oca=" + oca + ", cid=" + cid);

          channel.transmitLong(oid);
          channel.transmitInt(oca);
          channel.transmitInt(cid);

          ClassInfo classInfo = packageManager.getClassInfo(cid);
          if (classInfo == null) throw new ImplementationError("Unknown cid " + cid);

          transmitAttributes(channel, oid, classInfo);
          transmitReferences(channel, oid);
        }
      });
    }

    channel.transmitLong(0);
  }

  public void transmitObject(final Channel channel, final long oid)
  {
    Object[] args = { new Long(oid)};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(TRANSMIT_OBJECT, "?", args));

    jdbcTemplate.query(TRANSMIT_OBJECT, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        int oca = resultSet.getInt(1);
        int cid = resultSet.getInt(2);

        if (isDebugEnabled())
          debug("Object: oid=" + oIDEncoder.toString(oid) + ", oca=" + oca + ", cid=" + cid);

        channel.transmitLong(oid);
        channel.transmitInt(oca);
        channel.transmitInt(cid);

        ClassInfo classInfo = packageManager.getClassInfo(cid);
        if (classInfo == null) throw new ImplementationError("Unknown cid " + cid);

        transmitContainers(channel, oid);
        transmitAttributes(channel, oid, classInfo);
        transmitReferences(channel, oid);
      }
    });

    channel.transmitLong(0);
  }

  public void transmitContainers(final Channel channel, long oid)
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
    ;

    final List containers = new LinkedList();
    final long[] child = { oid};

    while (child[0] != 0)
    {
      Object[] args = { new Long(child[0]), Boolean.TRUE};
      if (isDebugEnabled())
        debug(StringHelper.replaceWildcards(SELECT_CONTAINER_OF_OBJECT, "?", args));

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

    channel.transmitInt(containers.size());
    for (Iterator it = containers.iterator(); it.hasNext();)
    {
      Container container = (Container) it.next();
      if (isDebugEnabled())
        debug("Container: oid=" + oIDEncoder.toString(container.oid) + ", cid=" + container.cid);

      channel.transmitLong(container.oid);
      channel.transmitInt(container.cid);
    }
  }

  public void transmitReferences(final Channel channel, long oid)
  {
    Object[] args = { new Long(oid)};
    if (isDebugEnabled()) debug(StringHelper.replaceWildcards(TRANSMIT_REFERENCES, "?", args));
    jdbcTemplate.query(TRANSMIT_REFERENCES, args, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        int feature = resultSet.getInt(1);
        long target = resultSet.getLong(2);
        int cid = resultSet.getInt(3);

        if (isDebugEnabled())
          debug("Reference: feature=" + feature + ", target=" + oIDEncoder.toString(target)
              + ", cid=" + cid);

        channel.transmitInt(feature);
        channel.transmitLong(target);
        channel.transmitInt(cid);
      }
    });

    channel.transmitInt(-1);
  }

  public void transmitAttributes(final Channel channel, long oid, ClassInfo classInfo)
  {
    while (classInfo != null)
    {
      String columnNames = classInfo.getColumnNames();

      if (columnNames != null && columnNames.length() > 0)
      {
        final ClassInfo finalClassInfo = classInfo;
        String sql = "SELECT " + columnNames + " FROM " + classInfo.getTableName() + " WHERE "
            + OBJECT_OID_COLUMN + "=?";

        Object[] args = { new Long(oid)};
        if (isDebugEnabled()) debug(StringHelper.replaceWildcards(sql, "?", args));

        jdbcTemplate.query(sql, args, new RowCallbackHandler()
        {
          public void processRow(ResultSet resultSet) throws SQLException
          {
            AttributeInfo[] attributeInfos = finalClassInfo.getAttributeInfos();
            for (int i = 0; i < attributeInfos.length; i++)
            {
              AttributeInfo attributeInfo = attributeInfos[i];

              Object value = resultSet.getObject(i + 1);
              columnConverter.toChannel(channel, attributeInfo.getDataType(), value);
            }
          }
        });
      }

      classInfo = classInfo.getParent();
    }
  }

  public void transmitAllResources(final Channel channel)
  {
    if (isDebugEnabled()) debug("Querying all resources");
    if (isDebugEnabled()) debug(SELECT_ALL_RESOURCES);

    jdbcTemplate.query(SELECT_ALL_RESOURCES, new RowCallbackHandler()
    {
      public void processRow(ResultSet resultSet) throws SQLException
      {
        int rid = resultSet.getInt(1);
        String path = resultSet.getString(2);

        channel.transmitInt(rid);
        channel.transmitString(path);
      }
    });

    channel.transmitInt(CDOResProtocol.NO_MORE_RESOURCES);
  }

  public void createAttributeTables(PackageInfo packageInfo)
  {
    if (isDebugEnabled()) debug("Creating attribute tables");

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
    segmentTable.addColumn(OBJECT_OID_COLUMN, ColumnType.BIGINT_LITERAL, "NOT NULL");

    AttributeInfo[] attributeInfos = classInfo.getAttributeInfos();
    for (int i = 0; i < attributeInfos.length; i++)
    {
      AttributeInfo attributeInfo = attributeInfos[i];

      String columnName = attributeInfo.getColumnName();
      ColumnType columnType = ColumnType.get(attributeInfo.getColumnType());
      segmentTable.addColumn(columnName, columnType);
    }

    segmentTable.addSimpleIndex(OBJECT_OID_COLUMN, IndexType.PRIMARY_LITERAL);
  }
}