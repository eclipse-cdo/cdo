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

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClass;

import org.eclipse.net4j.util.ImplementationError;

/**
 * @author Eike Stepper
 */
public final class ClassServerInfo extends ServerInfo
{
  private IClassMapping classMapping;

  private ClassServerInfo(int id)
  {
    super(id);
  }

  public static ClassServerInfo setDBID(CDOClass cdoClass, int id)
  {
    ClassServerInfo serverInfo = new ClassServerInfo(id);
    ((InternalCDOClass)cdoClass).setServerInfo(serverInfo);
    return serverInfo;
  }

  /*
   * Should only be called from MappingStrategy#getClassMapping(CDOClass).
   */
  public static IClassMapping getClassMapping(CDOClass cdoClass)
  {
    ClassServerInfo serverInfo = getServerInfo(cdoClass);
    return serverInfo == null ? null : serverInfo.classMapping;
  }

  public static void setClassMapping(CDOClass cdoClass, IClassMapping classMapping)
  {
    ClassServerInfo serverInfo = getServerInfo(cdoClass);
    if (serverInfo == null)
    {
      throw new ImplementationError("No serverInfo for class " + cdoClass);
    }

    serverInfo.classMapping = classMapping;
  }

  private static ClassServerInfo getServerInfo(CDOClass cdoClass)
  {
    ClassServerInfo serverInfo = (ClassServerInfo)cdoClass.getServerInfo();
    if (serverInfo == null)
    {
      if (cdoClass.isRoot())
      {
        serverInfo = setDBID(cdoClass, CDO_OBJECT_CLASS_DBID);
      }
      // else if (cdoClass.isResource())
      // {
      // CDOResourceClass c = (CDOResourceClass)cdoClass;
      // FeatureServerInfo.setDBID(c.getCDOContentsFeature(), CDO_CONTENTS_FEATURE_DBID);
      // serverInfo = setDBID(c, CDO_RESOURCE_CLASS_DBID);
      // }
      // else if (cdoClass.isResourceFolder())
      // {
      // CDOResourceFolderClass c = (CDOResourceFolderClass)cdoClass;
      // FeatureServerInfo.setDBID(c.getCDONodesFeature(), CDO_NODES_FEATURE_DBID);
      // serverInfo = setDBID(c, CDO_RESOURCE_FOLDER_CLASS_DBID);
      // }
      // else if (cdoClass.isResourceNode())
      // {
      // // Important to check the abstract class *after* the concrete ones!
      // CDOResourceNodeClass c = (CDOResourceNodeClass)cdoClass;
      // FeatureServerInfo.setDBID(c.getCDOFolderFeature(), CDO_FOLDER_FEATURE_DBID);
      // FeatureServerInfo.setDBID(c.getCDONameFeature(), CDO_NAME_FEATURE_DBID);
      // serverInfo = setDBID(cdoClass, CDO_RESOURCE_NODE_CLASS_DBID);
      // }
    }

    return serverInfo;
  }
}
