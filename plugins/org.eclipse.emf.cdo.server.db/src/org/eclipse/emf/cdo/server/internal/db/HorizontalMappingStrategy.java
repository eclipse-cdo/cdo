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
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBTable;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends MappingStrategy
{
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
    CDOClassImpl cdoClass = revision.getCDOClass();
    ClassMapping classMapping = getClassMapping(cdoClass);
    Map<IDBTable, FeatureMapping[]> tables = classMapping.getTables();
    for (Entry<IDBTable, FeatureMapping[]> entry : tables.entrySet())
    {
      IDBTable table = entry.getKey();
      FeatureMapping[] featureMappings = entry.getValue();

      DBUtil.insertRow(connection, table);
    }
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
