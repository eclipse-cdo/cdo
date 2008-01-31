/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IObjectTypeCache;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends MappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HorizontalMappingStrategy.class);

  private IObjectTypeCache objectTypeCache;

  public HorizontalMappingStrategy()
  {
  }

  public String getType()
  {
    return "horizontal";
  }

  public IObjectTypeCache getObjectTypeCache()
  {
    return objectTypeCache;
  }

  public CDOClassRef readObjectType(IDBStoreReader storeReader, CDOID id)
  {
    return objectTypeCache.getObjectType(storeReader, id);
  }

  protected CDOClassRef readObjectTypeFromClassesWithObjectInfo(IDBStoreReader storeReader, CDOID id)
  {
    String prefix = "SELECT DISTINCT " + CDODBSchema.ATTRIBUTES_CLASS + " FROM ";
    String suffix = " WHERE " + CDODBSchema.ATTRIBUTES_ID + "=" + id;
    for (CDOClass cdoClass : getClassesWithObjectInfo())
    {
      IClassMapping mapping = getClassMapping(cdoClass);
      if (mapping != null)
      {
        IDBTable table = mapping.getTable();
        if (table != null)
        {
          String sql = prefix + table + suffix;
          if (TRACER.isEnabled()) TRACER.trace(sql);
          ResultSet resultSet = null;

          try
          {
            resultSet = storeReader.getStatement().executeQuery(sql);
            if (resultSet.next())
            {
              int classID = resultSet.getInt(1);
              return getClassRef(storeReader, classID);
            }
          }
          catch (SQLException ex)
          {
            throw new DBException(ex);
          }
          finally
          {
            DBUtil.close(resultSet);
          }
        }
      }
    }

    throw new DBException("No object with id " + id);
  }

  @Override
  protected IClassMapping createClassMapping(CDOClass cdoClass)
  {
    if (cdoClass.isAbstract())
    {
      return null;
    }

    return new HorizontalClassMapping(this, cdoClass);
  }

  @Override
  protected List<CDOClass> getClassesWithObjectInfo()
  {
    List<CDOClass> result = new ArrayList<CDOClass>();
    IPackageManager packageManager = getStore().getRepository().getPackageManager();
    for (CDOPackage cdoPackage : packageManager.getPackages())
    {
      for (CDOClass cdoClass : cdoPackage.getConcreteClasses())
      {
        if (!cdoClass.isRoot())
        {
          result.add(cdoClass);
        }
      }
    }

    return result;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    objectTypeCache = createObjectTypeCache(getStore());
    LifecycleUtil.activate(objectTypeCache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(objectTypeCache);
    objectTypeCache = null;
    super.doDeactivate();
  }

  protected IObjectTypeCache createObjectTypeCache(IDBStore store)
  {
    ObjectTypeCache cache = new ObjectTypeCache();
    cache.setMappingStrategy(this);
    return cache;
  }
}
