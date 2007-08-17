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

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.db.IDBStore;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy implements IMappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MappingStrategy.class);

  private IDBStore store;

  private Properties properties;

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

  public Properties getProperties()
  {
    return properties;
  }

  public void setProperties(Properties properties)
  {
    this.properties = properties;
  }

  public IDBSchema getSchema()
  {
    return schema;
  }

  @Override
  public String toString()
  {
    return getType();
  }

  public Set<IDBTable> map(CDOPackageImpl[] cdoPackages)
  {
    // Lazily create the schema
    if (schema == null)
    {
      schema = createSchema();
    }

    // Prepare data structures
    Set<IDBTable> affectedTables = new HashSet();
    List<CDOClass> cdoClasses = new ArrayList();

    // Map all packages before classes are mapped
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      ((DBPackageInfo)cdoPackage.getServerInfo()).setSchema(schema);
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapped package: {0} --> {1}", cdoPackage, schema);
      }
    }

    // Map all classes before features are mapped
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapping classes of package {0}", cdoPackage);
      }

      for (CDOClass cdoClass : cdoPackage.getClasses())
      {
        cdoClasses.add(cdoClass);
        IDBTable table = map(cdoClass, affectedTables);
        if (table != null)
        {
          ((DBClassInfo)cdoClass.getServerInfo()).setTable(table);
          affectedTables.add(table);
          if (TRACER.isEnabled())
          {
            TRACER.format("Mapped class: {0} --> {1}", cdoClass, table);
          }
        }
      }
    }

    // Map all features
    for (CDOClass cdoClass : cdoClasses)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Mapping features of class {0}", cdoClass);
      }

      for (CDOFeature cdoFeature : cdoClass.getAllFeatures())
      {
        IDBField field = map(cdoClass, cdoFeature, affectedTables);
        if (field != null)
        {
          ((DBFeatureInfo)cdoFeature.getServerInfo()).addField(cdoClass, field);
          affectedTables.add(field.getTable());
          if (TRACER.isEnabled())
          {
            TRACER.format("Mapped feature: {0} --> {1}", cdoFeature, field);
          }
        }
      }
    }

    return affectedTables;
  }

  /**
   * @param affectedTables
   *          Can be used to indicate the creation or modification of additional
   *          tables. There is no need to add the returned table to this set of
   *          affected tables. The caller takes care of that.
   */
  protected abstract IDBTable map(CDOClass cdoClass, Set<IDBTable> affectedTables);

  /**
   * @param affectedTables
   *          Can be used to indicate the creation or modification of additional
   *          tables. There is no need to add the table of the returned field to
   *          this set of affected tables. The caller takes care of that.
   */
  protected abstract IDBField map(CDOClass cdoClass, CDOFeature cdoFeature, Set<IDBTable> affectedTables);

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

  protected void initTable(IDBTable table, boolean full)
  {
    table.addField("cdo_id", DBType.BIGINT);
    if (full)
    {
      table.addField("cdo_class", DBType.INTEGER);
    }
  }

  protected IDBTable addTable(CDOClass cdoClass)
  {
    for (int attempt = 0;; ++attempt)
    {
      try
      {
        String tableName = mangleTableName(cdoClass.getName(), attempt);
        return getSchema().addTable(tableName);
      }
      catch (DBException ignore)
      {
      }
    }
  }

  protected IDBField addField(CDOFeature cdoFeature, IDBTable table) throws DBException
  {
    for (int attempt = 0;; ++attempt)
    {
      try
      {
        String fieldName = mangleFieldName(cdoFeature.getName(), attempt);
        DBType fieldType = getDBType(cdoFeature.getType());
        return table.addField(fieldName, fieldType);
      }
      catch (DBException ignore)
      {
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