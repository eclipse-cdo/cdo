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
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class VerticalMappingStrategy extends StandardMappingStrategy
{
  public VerticalMappingStrategy()
  {
    throw new UnsupportedOperationException();
  }

  public String getType()
  {
    return "vertical";
  }

  @Override
  protected IDBSchema createSchema()
  {
    IDBSchema schema = super.createSchema();
    IDBTable table = schema.addTable("CDO_REVISIONS");
    initTable(table, true);
    return schema;
  }

  // @Override
  // protected IDBTable mapClass(CDOClass cdoClass, Set<IDBTable>
  // affectedTables)
  // {
  // if (cdoClass.isRoot())
  // {
  // return null;
  // }
  //
  // IDBTable table = addTable(cdoClass);
  // initTable(table, false);
  // return table;
  // }

  // @Override
  // protected IDBField mapFeature(CDOClass cdoClass, CDOFeature cdoFeature,
  // Set<IDBTable> affectedTables)
  // {
  // if (cdoFeature.getContainingClass() != cdoClass)
  // {
  // return null;
  // }
  //
  // DBClassInfo classInfo = (DBClassInfo)cdoClass.getServerInfo();
  // IDBTable table = classInfo.getTable();
  // return addField(cdoFeature, table);
  // }

  public void writeRevision(Connection connection, CDORevisionImpl revision)
  {
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
