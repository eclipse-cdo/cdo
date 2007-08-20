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
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.Date;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends MappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalMappingStrategy.class);

  public HorizontalMappingStrategy()
  {
  }

  public String getType()
  {
    return "horizontal";
  }

  @Override
  protected IDBTable map(CDOClass cdoClass, Set<IDBTable> affectedTables)
  {
    if (cdoClass.isAbstract())
    {
      return null;
    }

    IDBTable table = addTable(cdoClass);
    initTable(table, true);
    return table;
  }

  @Override
  protected IDBField map(CDOClass cdoClass, CDOFeature cdoFeature, Set<IDBTable> affectedTables)
  {
    if (cdoClass.isAbstract())
    {
      return null;
    }

    DBClassInfo classInfo = (DBClassInfo)cdoClass.getServerInfo();
    IDBTable table = classInfo.getTable();
    return addField(cdoFeature, table);
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
    values[1] = ((DBClassInfo)revision.getCDOClass().getServerInfo()).getID();
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
