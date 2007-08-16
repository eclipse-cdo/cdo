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
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.db.DBSchema;
import org.eclipse.net4j.util.ImplementationError;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy implements IMappingStrategy
{
  private IStore store;

  private Properties properties;

  private IDBSchema schema;

  public MappingStrategy()
  {
  }

  public IStore getStore()
  {
    return store;
  }

  public void setStore(IStore store)
  {
    this.store = store;
    schema = createSchema();
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

  public IDBTable[] map(CDOPackage cdoPackage)
  {
    Set<IDBTable> affectedTables = new HashSet();
    ((DBPackageInfo)cdoPackage.getServerInfo()).setSchema(schema);
    for (CDOClass cdoClass : cdoPackage.getClasses())
    {
      IDBTable table = map(schema, cdoClass, affectedTables);
      if (table != null)
      {
        ((DBClassInfo)cdoClass.getServerInfo()).setTable(table);
        affectedTables.add(table);
      }

      for (CDOFeature cdoFeature : cdoClass.getAllFeatures())
      {
        IDBField field = map(schema, cdoClass, cdoFeature, affectedTables);
        if (table != null)
        {
          ((DBFeatureInfo)cdoFeature.getServerInfo()).setField(field);
          affectedTables.add(field.getTable());
        }
      }
    }

    return affectedTables.toArray(new IDBTable[affectedTables.size()]);
  }

  /**
   * @param affectedTables
   *          Can be used to indicate the creation or modification of additional
   *          tables. There is no need to add the returned table to this set of
   *          affected tables. The caller takes care of that.
   */
  protected abstract IDBTable map(IDBSchema schema, CDOClass cdoClass, Set<IDBTable> affectedTables);

  /**
   * @param affectedTables
   *          Can be used to indicate the creation or modification of additional
   *          tables. There is no need to add the table of the returned field to
   *          this set of affected tables. The caller takes care of that.
   */
  protected abstract IDBField map(IDBSchema schema, CDOClass cdoClass, CDOFeature cdoFeature,
      Set<IDBTable> affectedTables);

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

    throw new ImplementationError("Unrecognized type: " + type);
  }
}