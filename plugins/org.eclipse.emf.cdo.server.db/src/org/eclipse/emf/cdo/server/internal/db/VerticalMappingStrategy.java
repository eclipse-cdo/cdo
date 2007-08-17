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

import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class VerticalMappingStrategy extends MappingStrategy
{
  public VerticalMappingStrategy()
  {
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

  @Override
  protected IDBTable map(CDOClass cdoClass, Set<IDBTable> affectedTables)
  {
    if (cdoClass.isRoot())
    {
      return null;
    }

    IDBTable table = getSchema().addTable(cdoClass.getName());
    initTable(table, false);
    return table;
  }

  @Override
  protected IDBField map(CDOClass cdoClass, CDOFeature cdoFeature, Set<IDBTable> affectedTables)
  {
    if (cdoFeature.getContainingClass() != cdoClass)
    {
      return null;
    }

    DBClassInfo classInfo = (DBClassInfo)cdoClass.getServerInfo();
    IDBTable table = classInfo.getTable();
    return table.addField(cdoFeature.getName(), getDBType(cdoFeature.getType()));
  }
}
