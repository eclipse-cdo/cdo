/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IObjectTypeCache;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.ObjectTypeCache;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class HorizontalMappingStrategy extends MappingStrategy
{
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

  public void setObjectTypeCache(IObjectTypeCache objectTypeCache)
  {
    this.objectTypeCache = objectTypeCache;
  }

  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    return objectTypeCache.getObjectType(accessor, id);
  }

  /**
   * TODO Stefan: Is this method still needed?
   */
  @Deprecated
  protected final CDOClassifierRef readObjectTypeFromClassesWithObjectInfo(IDBStoreAccessor accessor, CDOID id)
  {
    // String prefix = "SELECT DISTINCT " + CDODBSchema.ATTRIBUTES_CLASS + " FROM ";
    // String suffix = " WHERE " + CDODBSchema.ATTRIBUTES_ID + "=" + id;
    // for (EClass eClass : getClassesWithObjectInfo())
    // {
    // IClassMapping mapping = getClassMapping(eClass);
    // if (mapping != null)
    // {
    // IDBTable table = mapping.getTable();
    // if (table != null)
    // {
    // String sql = prefix + table + suffix;
    // if (TRACER.isEnabled())
    // {
    // TRACER.trace(sql);
    // }
    //
    // ResultSet resultSet = null;
    //
    // try
    // {
    // resultSet = accessor.getJDBCDelegate().getStatement().executeQuery(sql);
    // if (resultSet.next())
    // {
    // int classID = resultSet.getInt(1);
    // return getClassifierRef(accessor, classID);
    // }
    // }
    // catch (SQLException ex)
    // {
    // throw new DBException(ex);
    // }
    // finally
    // {
    // DBUtil.close(resultSet);
    // }
    // }
    // }
    // }

    throw new DBException("No object with id " + id);
  }

  @Override
  protected IClassMapping createClassMapping(EClass eClass)
  {
    if (eClass.isAbstract() || eClass.isInterface())
    {
      return null;
    }

    return new HorizontalClassMapping(this, eClass);
  }

  @Override
  protected List<EClass> getClassesWithObjectInfo()
  {
    List<EClass> result = new ArrayList<EClass>();
    InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)getStore().getRepository()
        .getPackageRegistry();
    for (EPackage ePackage : packageRegistry.getEPackages())
    {
      for (EClass eClass : EMFUtil.getConcreteClasses(ePackage))
      {
        // if (!CDOModelUtil.isRoot(eClass))
        {
          result.add(eClass);
        }
      }
    }

    return result;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (objectTypeCache == null)
    {
      objectTypeCache = createObjectTypeCache(getStore());
      LifecycleUtil.activate(objectTypeCache);
    }
  }

  @Override
  protected String[] getResourceQueries(CDOID folderID, String name, boolean exactMatch)
  {
    String[] queries = new String[2];

    IClassMapping resourceFolderMapping = getClassMapping(EresourcePackage.eINSTANCE.getCDOResourceFolder());
    queries[0] = getResourceQuery(folderID, name, exactMatch, resourceFolderMapping);

    IClassMapping resourceMapping = getClassMapping(EresourcePackage.eINSTANCE.getCDOResource());
    queries[1] = getResourceQuery(folderID, name, exactMatch, resourceMapping);

    return queries;
  }

  protected String getResourceQuery(CDOID folderID, String name, boolean exactMatch, IClassMapping classMapping)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM ");
    builder.append(classMapping.getTable());
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append("=");
    builder.append(CDOIDUtil.getLong(folderID));
    if (exactMatch || name != null)
    {
      builder.append(" AND ");
      builder.append(classMapping.getAttributeMapping(EresourcePackage.eINSTANCE.getCDOResourceNode_Name()).getField());
      if (exactMatch)
      {
        if (name == null)
        {
          builder.append(" IS NULL");
        }
        else
        {
          builder.append("=\'");
          builder.append(name);
          builder.append("\'");
        }
      }
      else
      {
        // Here: name != null
        builder.append(" LIKE \'");
        builder.append(name);
        builder.append("%\'");
      }
    }

    String sql = builder.toString();
    return sql;
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
