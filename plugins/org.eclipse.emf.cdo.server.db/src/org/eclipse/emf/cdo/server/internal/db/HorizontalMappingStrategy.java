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

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.ToManyReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends MappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalMappingStrategy.class);

  private Map<Object, IDBTable> referenceTables = new HashMap();

  public HorizontalMappingStrategy()
  {
  }

  public String getType()
  {
    return "horizontal";
  }

  @Override
  protected IDBTable mapClass(CDOClass cdoClass, Set<IDBTable> affectedTables)
  {
    return null;
  }

  @Override
  protected IDBField mapFeature(CDOClass cdoClass, CDOFeature cdoFeature, Set<IDBTable> affectedTables)
  {
    if (cdoClass.isAbstract())
    {
      return null;
    }

    if (cdoFeature.isReference())
    {
      if (cdoFeature.isMany())
      {
        return mapReferenceMany(cdoClass, cdoFeature, getToManyReferenceMapping());
      }
      else
      {
        switch (getToOneReferenceMapping())
        {
        case LIKE_ATTRIBUTES:
          return mapAttribute(cdoClass, cdoFeature);
        case LIKE_TO_MANY_REFERENCES:
          return mapReferenceMany(cdoClass, cdoFeature, getToManyReferenceMapping());
        default:
          throw new IllegalArgumentException("Invalid mapping: " + getToOneReferenceMapping());
        }
      }
    }
    else
    {
      if (cdoFeature.isMany())
      {
        throw new UnsupportedOperationException();
      }

      return mapAttribute(cdoClass, cdoFeature);
    }
  }

  protected IDBField mapAttribute(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    DBClassInfo classInfo = (DBClassInfo)cdoClass.getServerInfo();
    IDBTable table = classInfo.getTable();
    if (table == null)
    {
      table = addTable(cdoClass);
      initTable(table, true);
    }

    return addField(cdoFeature, table);
  }

  protected IDBField mapReferenceMany(CDOClass cdoClass, CDOFeature cdoFeature, ToManyReferenceMapping mapping)
  {
    switch (mapping)
    {
    case ONE_TABLE_PER_REFERENCE:
      return mapReference(cdoClass, cdoFeature);
    case ONE_TABLE_PER_CLASS:
      return mapReferencePerClass(cdoClass);
    case ONE_TABLE_PER_PACKAGE:
      return mapReferencePerPackage(cdoClass.getContainingPackage());
    case ONE_TABLE_PER_REPOSITORY:
      return mapReferencePerRepository(getStore().getRepository());
    case LIKE_ATTRIBUTES:
      return mapReferenceSerialized(cdoClass, cdoFeature);
    default:
      throw new IllegalArgumentException("Invalid mapping: " + mapping);
    }
  }

  protected IDBField mapReference(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    IDBTable table = referenceTables.get(cdoFeature);
    if (table == null)
    {
      table = addReferenceTable(cdoClass.getName() + "_" + cdoFeature.getName() + "_refs", false);
      referenceTables.put(cdoFeature, table);
    }

    return table.getField(0);
  }

  protected IDBField mapReferencePerClass(CDOClass cdoClass)
  {
    IDBTable table = referenceTables.get(cdoClass);
    if (table == null)
    {
      table = addReferenceTable(cdoClass.getName() + "_refs", true);
      referenceTables.put(cdoClass, table);
    }

    return table.getField(0);
  }

  protected IDBField mapReferencePerPackage(CDOPackage cdoPackage)
  {
    IDBTable table = referenceTables.get(cdoPackage);
    if (table == null)
    {
      table = addReferenceTable(cdoPackage.getName() + "_refs", true);
      referenceTables.put(cdoPackage, table);
    }

    return table.getField(0);
  }

  protected IDBField mapReferencePerRepository(IRepository repository)
  {
    IDBTable table = referenceTables.get(repository);
    if (table == null)
    {
      table = addReferenceTable(repository.getName() + "_refs", true);
      referenceTables.put(repository, table);
    }

    return table.getField(0);
  }

  protected IDBField mapReferenceSerialized(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    // TODO Implement method HorizontalMappingStrategy.mapReferenceSerialized()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  protected IDBTable addReferenceTable(String tableName, boolean withFeature)
  {
    IDBTable table = addTable(tableName);
    if (withFeature)
    {
      table.addField("cdo_feature", DBType.INTEGER);
    }

    table.addField("cdo_source", DBType.BIGINT);
    table.addField("cdo_target", DBType.BIGINT);
    table.addField("cdo_idx", DBType.INTEGER);
    return table;
  }

  public void writeRevision(Connection connection, CDORevisionImpl revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Inserting revision: {0}", revision);
    }

    CDOClassImpl cdoClass = revision.getCDOClass();
    ClassMapping classMapping = getClassMapping(cdoClass);
    Map<IDBTable, FeatureMapping[]> tables = classMapping.getTables();
    Entry<IDBTable, FeatureMapping[]> entry = tables.entrySet().iterator().next();
    IDBTable table = entry.getKey();

    Object[] values = new Object[table.getFieldCount()];
    values[0] = revision.getID().getValue();
    values[1] = DBInfo.getDBID(revision.getCDOClass());
    values[2] = revision.getVersion();
    values[3] = new Date(revision.getCreated());
    values[4] = new Date(revision.getRevised());
    values[5] = revision.getResourceID().getValue();
    values[6] = revision.getContainerID().getValue();
    values[7] = revision.getContainingFeatureID();

    int i = 8;
    for (CDOFeatureImpl feature : cdoClass.getAllFeatures())
    {
      Object value = revision.getValue(feature);
      values[i++] = value instanceof CDOID ? ((CDOID)value).getValue() : value;
    }

    DBUtil.insertRow(connection, table, values);
  }

  public CDORevision readRevision(Connection connection, CDOID id)
  {
    return null;
  }

  public CDORevision readRevision(Connection connection, CDOID id, long timeStamp)
  {
    return null;
  }

  public CDOID readResourceID(Connection connection, String path)
  {
    return null;
  }

  public String readResourcePath(Connection connection, CDOID id)
  {
    return null;
  }

  public CDOClassRef readObjectType(Connection connection, CDOID id)
  {
    return null;
  }
}
