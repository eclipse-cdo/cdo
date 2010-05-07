/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.utils;

import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyCommitInfoHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyPropertyMapHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyScope;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranchManager;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyResourceList;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import com.objy.db.ObjyRuntimeException;
import com.objy.db.app.ooId;

public class ObjyDb
{

  static public final String CONFIGDB_NAME = "ConfigDb";

  static public final String RESOURCELIST_NAME = "ResourceList";

  static public final String RESOURCELIST_CONT_NAME = "ResourceListCont";

  static public final String PACKAGESTORE_CONT_NAME = "PackageCont";

  static public final String COMMITINFOSET_CONT_NAME = "CommitInfoCont";

  static public final String COMMITINFOSET_NAME = "CommitInfoSet";

  static public final String PROPERTYMAP_NAME = "PropertyMap";

  static public final String PROPERTYMAP_CONT_NAME = "PropertyCont";

  static public final String OBJYSTOREINFO_NAME = "ObjyStoreInfo";

  public static final String DEFAULT_CONT_NAME = "_ooDefaultContObj"; // this is objy default cont name.

  static public final String BRANCHMANAGER_NAME = "BranchManager";

  static public final String BRANCHING_CONT_NAME = "BranchingCont";

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyDb.class);

  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjyDb.class);

  /***
   * Unitily functions..
   */

  /***
   * This function will return the resourceList after creation.
   */
  public static ObjyObject getOrCreateResourceList()
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("getOrCreateResourceList()"); //$NON-NLS-1$
    }
    ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.RESOURCELIST_CONT_NAME);
    ObjyObject objyObject = null;
    try
    {
      objyObject = objyScope.lookupObjyObject(ObjyDb.RESOURCELIST_NAME);
    }
    catch (ObjyRuntimeException ex)
    {
      // we need to create the resource.
      objyObject = createResourceList(objyScope);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return objyObject;
  }

  protected static ObjyObject createResourceList(ObjyScope objyScope)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("createResourceList()"); //$NON-NLS-1$
    }
    // TODO - this need refactoring...
    ObjyObject resourceList = ObjyResourceList.create(objyScope.getScopeContOid());
    objyScope.nameObj(ObjyDb.RESOURCELIST_NAME, resourceList);
    return resourceList;
  }

  protected static ooId createCommitInfoList(ObjyScope objyScope)
  {
    // TODO - this need refactoring...
    ooId commitInfoListId = ObjyCommitInfoHandler.create(objyScope.getScopeContOid());
    objyScope.nameObj(ObjyDb.COMMITINFOSET_NAME, commitInfoListId);
    return commitInfoListId;
  }

  public static ooId getOrCreateCommitInfoList(String repositoryName)
  {
    ObjyScope objyScope = new ObjyScope(repositoryName, ObjyDb.COMMITINFOSET_CONT_NAME);
    ooId commitInfoListId = null;
    try
    {
      commitInfoListId = objyScope.lookupObjectOid(ObjyDb.COMMITINFOSET_NAME);
    }
    catch (ObjyRuntimeException ex)
    {
      commitInfoListId = createCommitInfoList(objyScope);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return commitInfoListId;
  }

  protected static ooId createPropertyMap(ObjyScope objyScope)
  {
    // TODO - this need refactoring...
    ooId propertyMapId = ObjyPropertyMapHandler.create(objyScope.getScopeContOid());
    objyScope.nameObj(ObjyDb.PROPERTYMAP_NAME, propertyMapId);
    return propertyMapId;
  }

  public static ooId getOrCreatePropertyMap()
  {
    ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.PROPERTYMAP_CONT_NAME);
    ooId propertyMapId = null;
    try
    {
      propertyMapId = objyScope.lookupObjectOid(ObjyDb.PROPERTYMAP_NAME);
    }
    catch (ObjyRuntimeException ex)
    {
      propertyMapId = createPropertyMap(objyScope);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return propertyMapId;
  }

  protected static ObjyBranchManager createBranchManager(ObjyScope objyScope)
  {
    ObjyBranchManager objyBranchManager = ObjyBranchManager.create(objyScope.getScopeContOid());
    objyScope.nameObj(ObjyDb.BRANCHMANAGER_NAME, objyBranchManager.getOid());
    return objyBranchManager;
  }

  public static ObjyBranchManager getOrCreateBranchManager(String repositoryName)
  {
    ObjyScope objyScope = new ObjyScope(repositoryName, ObjyDb.BRANCHING_CONT_NAME);
    ObjyBranchManager objyBranchManager = null;
    try
    {
      objyBranchManager = (ObjyBranchManager)objyScope.lookupObject(ObjyDb.BRANCHMANAGER_NAME);
    }
    catch (ObjyRuntimeException ex)
    {
      objyBranchManager = createBranchManager(objyScope);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return objyBranchManager;
  }

}
