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

import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.db.DBSchema;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy implements IMappingStrategy
{
  public static final int FULL_NUMBER_OF_FIELDS = 8;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MappingStrategy.class);

  private IDBStore store;

  private Map<String, String> properties;

  private IDBSchema schema;

  public MappingStrategy()
  {
  }

  public IDBStore getStore()
  {
    return store;
  }

  public void setStore(IDBStore store)
  {
    this.store = store;
  }

  public Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap();
    }

    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public IDBSchema getSchema()
  {
    if (schema == null)
    {
      schema = createSchema();
    }

    return schema;
  }

  public IMapping getMapping(CDOClass cdoClass)
  {
    IMapping mapping = ClassServerInfo.getMapping(cdoClass);
    if (mapping == NoMapping.INSTANCE)
    {
      return null;
    }

    if (mapping == null)
    {
      mapping = createMapping(cdoClass);
      ClassServerInfo.setMapping(cdoClass, mapping == null ? NoMapping.INSTANCE : mapping);
    }

    return mapping;
  }

  protected abstract IMapping createMapping(CDOClass cdoClass);

  @Override
  public String toString()
  {
    return getType();
  }

  protected String mangleTableName(String name, int attempt)
  {
    return store.getDBAdapter().mangleTableName(name, attempt);
  }

  protected String mangleFieldName(String name, int attempt)
  {
    return store.getDBAdapter().mangleFieldName(name, attempt);
  }

  protected IDBSchema createSchema()
  {
    String name = store.getRepository().getName();
    return new DBSchema(name);
  }

  protected IDBTable addReferenceTable(String tableName, boolean withFeature)
  {
    IDBTable table = addTable(tableName);
    if (withFeature)
    {
      table.addField("cdo_feature", DBType.INTEGER);
    }

    table.addField("cdo_idx", DBType.INTEGER);
    table.addField("cdo_source", DBType.BIGINT);
    table.addField("cdo_target", DBType.BIGINT);
    return table;
  }

  protected int initTable(IDBTable table, boolean full)
  {
    table.addField("cdo_id", DBType.BIGINT);
    if (full)
    {
      table.addField("cdo_class", DBType.INTEGER);
      table.addField("cdo_version", DBType.INTEGER);
      table.addField("cdo_created", DBType.TIMESTAMP);
      table.addField("cdo_revised", DBType.TIMESTAMP);
      table.addField("cdo_resource", DBType.BIGINT);
      table.addField("cdo_container", DBType.BIGINT);
      table.addField("cdo_feature", DBType.INTEGER);
      return FULL_NUMBER_OF_FIELDS;
    }

    return 1;
  }

  protected IDBTable addTable(CDOClass cdoClass)
  {
    return addTable(cdoClass.getName());
  }

  protected IDBTable addTable(String name)
  {
    for (int attempt = 0;; ++attempt)
    {
      String tableName = mangleTableName(name, attempt);

      try
      {
        return getSchema().addTable(tableName);
      }
      catch (DBException ignore)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("{0}. attempt to add table: {1} ({2})", attempt, tableName, ignore.getMessage());
        }
      }
    }
  }

  protected IDBField addField(CDOFeature cdoFeature, IDBTable table) throws DBException
  {
    for (int attempt = 0;; ++attempt)
    {
      String fieldName = mangleFieldName(cdoFeature.getName(), attempt);
      DBType fieldType = getDBType(cdoFeature.getType());

      try
      {
        return table.addField(fieldName, fieldType);
      }
      catch (DBException ignore)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("{0}. attempt to add field: {1} ({2})", attempt, fieldName, ignore.getMessage());
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
    else if (type == CDOType.LONG || type == CDOType.INTEGER_OBJECT)
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
}