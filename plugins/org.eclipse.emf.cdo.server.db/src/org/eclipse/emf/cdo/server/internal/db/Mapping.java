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

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Mapping implements IMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, Mapping.class);

  private MappingStrategy mappingStrategy;

  private CDOClass cdoClass;

  private IDBTable table;

  private Set<IDBTable> affectedTables = new HashSet<IDBTable>();

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
    table.addField(CDODBSchema.ATTRIBUTES_ID, DBType.BIGINT);
    table.addField(CDODBSchema.ATTRIBUTES_VERSION, DBType.INTEGER);
    if (full)
    {
      table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.INTEGER);
      table.addField(CDODBSchema.ATTRIBUTES_CREATED, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_REVISED, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_RESOURCE, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_CONTAINER, DBType.BIGINT);
      table.addField(CDODBSchema.ATTRIBUTES_FEATURE, DBType.INTEGER);
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
    return DBStore.getDBType(type);
  }

  protected IDBAdapter getDBAdapter()
  {
    IDBStore store = mappingStrategy.getStore();
    return store.getDBAdapter();
  }
}
