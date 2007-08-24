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
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ImplementationError;

import java.sql.Date;
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

  private IMappingStrategy mappingStrategy;

  private CDOClass cdoClass;

  private IDBTable table;

  private Set<IDBTable> affectedTables = new HashSet();

  public Mapping(IMappingStrategy mappingStrategy, CDOClass cdoClass)
  {
    this.mappingStrategy = mappingStrategy;
    this.cdoClass = cdoClass;
    table = addTable(cdoClass.getName());
  }

  public IMappingStrategy getMappingStrategy()
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

  protected void appendFullInfo(StringBuilder builder, CDORevisionImpl revision)
  {
    builder.append(ServerInfo.getDBID(revision.getCDOClass()));
    builder.append(", ");
    builder.append(revision.getVersion());
    builder.append(", ");
    builder.append(new Date(revision.getCreated()));
    builder.append(", ");
    builder.append(new Date(revision.getRevised()));
    builder.append(", ");
    builder.append(revision.getResourceID().getValue());
    builder.append(", ");
    builder.append(revision.getContainerID().getValue());
    builder.append(", ");
    builder.append(revision.getContainingFeatureID());
  }

  protected void executeSQL(IDBStoreAccessor storeAccessor, String sql) throws DBException
  {
    try
    {
      Statement statement = storeAccessor.getStatement();
      int count = statement.executeUpdate(sql);
      if (count != 1)
      {
        throw new DBException("Wrong update count: " + count);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected String mangleTableName(String name, int attempt)
  {
    return mappingStrategy.getStore().getDBAdapter().mangleTableName(name, attempt);
  }

  protected String mangleFieldName(String name, int attempt)
  {
    return mappingStrategy.getStore().getDBAdapter().mangleFieldName(name, attempt);
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

  protected IDBTable addTable(String name)
  {
    for (int attempt = 0;; ++attempt)
    {
      String tableName = mangleTableName(name, attempt);

      try
      {
        return mappingStrategy.getStore().getSchema().addTable(tableName);
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
    for (int attempt = 0;; ++attempt)
    {
      String fieldName = mangleFieldName(cdoFeature.getName(), attempt);
      DBType fieldType = getDBType(cdoFeature.getType());

      try
      {
        return table.addField(fieldName, fieldType);
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

  protected IDBField mapReference(CDOClass cdoClass, CDOFeature cdoFeature, ToMany mapping)
  {
    switch (mapping)
    {
    case PER_REFERENCE:
      return mapReferenceTable(cdoFeature, cdoClass.getName() + "_" + cdoFeature.getName() + "_refs", false);
    case PER_CLASS:
      return mapReferenceTable(cdoClass, cdoClass.getName() + "_refs", true);
    case PER_PACKAGE:
      CDOPackage cdoPackage = cdoClass.getContainingPackage();
      return mapReferenceTable(cdoPackage, cdoPackage.getName() + "_refs", true);
    case PER_REPOSITORY:
      IRepository repository = mappingStrategy.getStore().getRepository();
      return mapReferenceTable(repository, repository.getName() + "_refs", true);
    case LIKE_ATTRIBUTES:
      return mapReferenceSerialized(cdoClass, cdoFeature);
    default:
      throw new IllegalArgumentException("Invalid mapping: " + mapping);
    }
  }

  protected IDBField mapReferenceSerialized(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    // TODO Implement method HorizontalMappingStrategy.mapReferenceSerialized()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  protected IDBField mapReferenceTable(Object key, String tableName, boolean withFeature)
  {
    IDBTable table = referenceTables.get(key);
    if (table == null)
    {
      table = addReferenceTable(tableName, withFeature);
      referenceTables.put(key, table);
    }

    return table.getField(0);
  }
}
