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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Mapping implements IMapping
{
  public static final String FIELD_NAME_ID = "cdo_id";

  public static final String FIELD_NAME_VERSION = "cdo_version";

  public static final String FIELD_NAME_CLASS = "cdo_class";

  public static final String FIELD_NAME_CREATED = "cdo_created";

  public static final String FIELD_NAME_REVISED = "cdo_revised";

  public static final String FIELD_NAME_RESOURCE = "cdo_resource";

  public static final String FIELD_NAME_CONTAINER = "cdo_container";

  public static final String FIELD_NAME_FEATURE = "cdo_feature";

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, Mapping.class);

  private MappingStrategy mappingStrategy;

  private CDOClass cdoClass;

  private IDBTable table;

  private Set<IDBTable> affectedTables = new HashSet();

  public Mapping(MappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    this.mappingStrategy = mappingStrategy;
    this.cdoClass = cdoClass;
    table = addTable(cdoClass.getName());
  }

  public MappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public CDOClass getCDOClass()
  {
    return cdoClass;
  }

  public IDBTable getTable()
  {
    return table;
  }

  public Set<IDBTable> getAffectedTables()
  {
    return affectedTables;
  }

  protected void initTable(IDBTable table, boolean full)
  {
    table.addField(FIELD_NAME_ID, DBType.BIGINT);
    table.addField(FIELD_NAME_VERSION, DBType.INTEGER);
    if (full)
    {
      table.addField(FIELD_NAME_CLASS, DBType.INTEGER);
      table.addField(FIELD_NAME_CREATED, DBType.BIGINT);
      table.addField(FIELD_NAME_REVISED, DBType.BIGINT);
      table.addField(FIELD_NAME_RESOURCE, DBType.BIGINT);
      table.addField(FIELD_NAME_CONTAINER, DBType.BIGINT);
      table.addField(FIELD_NAME_FEATURE, DBType.INTEGER);
    }
  }

  protected void appendRevisionInfos(StringBuilder builder, CDORevisionImpl revision, boolean full)
  {
    builder.append(revision.getID().getValue());
    builder.append(", ");
    builder.append(revision.getVersion());
    if (full)
    {
      builder.append(", ");
      builder.append(ServerInfo.getDBID(revision.getCDOClass()));
      builder.append(", ");
      builder.append(revision.getCreated());
      builder.append(", ");
      builder.append(revision.getRevised());
      builder.append(", ");
      builder.append(revision.getResourceID().getValue());
      builder.append(", ");
      builder.append(revision.getContainerID().getValue());
      builder.append(", ");
      builder.append(revision.getContainingFeatureID());
    }
  }

  protected int sqlUpdate(IDBStoreAccessor storeAccessor, String sql) throws DBException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    try
    {
      Statement statement = storeAccessor.getStatement();
      return statement.executeUpdate(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected String mangleTableName(String name, int attempt)
  {
    return getDBAdapter().mangleTableName(name, attempt);
  }

  protected String mangleFieldName(String name, int attempt)
  {
    return getDBAdapter().mangleFieldName(name, attempt);
  }

  protected IDBTable addTable(String name)
  {
    for (int attempt = 0;; ++attempt)
    {
      String tableName = mangleTableName(name, attempt);

      try
      {
        IDBTable table = mappingStrategy.getStore().getSchema().addTable(tableName);
        affectedTables.add(table);
        return table;
      }
      catch (DBException ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("{0}. attempt to add table: {1} ({2})", attempt + 1, tableName, ex.getMessage());
        }
      }
    }
  }

  protected IDBField addField(CDOFeature cdoFeature, IDBTable table) throws DBException
  {
    DBType fieldType = getDBType(cdoFeature.getType());
    for (int attempt = 0;; ++attempt)
    {
      String fieldName = mangleFieldName(cdoFeature.getName(), attempt);

      try
      {
        IDBField field = table.addField(fieldName, fieldType);
        affectedTables.add(table);
        return field;
      }
      catch (DBException ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("{0}. attempt to add field: {1} ({2})", attempt + 1, fieldName, ex.getMessage());
        }
      }
    }
  }

  protected DBType getDBType(CDOType type)
  {
    if (type == CDOType.BOOLEAN || type == CDOType.BOOLEAN_OBJECT)
    {
      return DBType.BOOLEAN;
    }
    else if (type == CDOType.BYTE || type == CDOType.BYTE_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.CHAR || type == CDOType.CHARACTER_OBJECT)
    {
      return DBType.CHAR;
    }
    else if (type == CDOType.DATE)
    {
      return DBType.DATE;
    }
    else if (type == CDOType.DOUBLE || type == CDOType.DOUBLE_OBJECT)
    {
      return DBType.DOUBLE;
    }
    else if (type == CDOType.FLOAT || type == CDOType.FLOAT_OBJECT)
    {
      return DBType.FLOAT;
    }
    else if (type == CDOType.INT || type == CDOType.INTEGER_OBJECT)
    {
      return DBType.INTEGER;
    }
    else if (type == CDOType.LONG || type == CDOType.LONG_OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.SHORT || type == CDOType.SHORT_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.STRING)
    {
      return DBType.LONGVARCHAR;
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }

  protected IDBAdapter getDBAdapter()
  {
    IDBStore store = mappingStrategy.getStore();
    return store.getDBAdapter();
  }
}
