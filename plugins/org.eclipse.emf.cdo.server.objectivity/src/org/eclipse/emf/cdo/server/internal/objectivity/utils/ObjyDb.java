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

import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyScope;
import org.eclipse.emf.cdo.server.internal.objectivity.db.OoCommitInfoHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.OoPropertyMapHandler;

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

  /***
   * Unitily functions..
   */

  protected static ooId createCommitInfoList(ObjyScope objyScope)
  {
    // TODO - this need refactoring...
    ooId commitInfoListId = OoCommitInfoHandler.create(objyScope.getScopeContOid());
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
    catch (Exception ex)
    {
      commitInfoListId = createCommitInfoList(objyScope);
    }
    return commitInfoListId;
  }

  protected static ooId createPropertyMap(ObjyScope objyScope)
  {
    // TODO - this need refactoring...
    ooId propertyMapId = OoPropertyMapHandler.create(objyScope.getScopeContOid());
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
    catch (Exception ex)
    {
      propertyMapId = createPropertyMap(objyScope);
    }
    return propertyMapId;
  }

}
