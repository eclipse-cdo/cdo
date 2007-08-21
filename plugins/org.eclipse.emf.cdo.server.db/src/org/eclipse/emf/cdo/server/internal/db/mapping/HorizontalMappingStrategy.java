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
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.info.ServerInfo;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.Date;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends StandardMappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalMappingStrategy.class);

  public HorizontalMappingStrategy()
  {
  }

  public String getType()
  {
    return "horizontal";
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

    int i = 0;
    Object[] values = new Object[table.getFieldCount()];
    values[i++] = revision.getID().getValue();
    values[i++] = ServerInfo.getDBID(revision.getCDOClass());
    values[i++] = revision.getVersion();
    values[i++] = new Date(revision.getCreated());
    values[i++] = new Date(revision.getRevised());
    values[i++] = revision.getResourceID().getValue();
    values[i++] = revision.getContainerID().getValue();
    values[i++] = revision.getContainingFeatureID();

    for (CDOFeatureImpl feature : cdoClass.getAllFeatures())
    {
      Object value = revision.getValue(feature);
      if (value instanceof CDOID)
      {
        values[i++] = ((CDOID)value).getValue();
      }
      else
      {
        values[i++] = value;
      }
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
