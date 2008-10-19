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
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;

import org.eclipse.net4j.util.ImplementationError;

/**
 * @author Eike Stepper
 */
public final class ClassServerInfo extends ServerInfo
{
  public static final int CDO_OBJECT_CLASS_DBID = -1;

  public static final int CDO_RESOURCE_CLASS_DBID = -2;

  public static final int CDO_RESOURCE_NODE_CLASS_DBID = -3;

  public static final int CDO_RESOURCE_FOLDER_CLASS_DBID = -4;

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

  protected static ClassServerInfo getServerInfo(CDOClass cdoClass)
  {
    ClassServerInfo serverInfo = (ClassServerInfo)cdoClass.getServerInfo();
    if (serverInfo == null)
    {
      if (cdoClass.isRoot())
      {
        serverInfo = setDBID(cdoClass, CDO_OBJECT_CLASS_DBID);
      }
      else if (cdoClass.isResource())
      {
        serverInfo = setDBID(cdoClass, CDO_RESOURCE_CLASS_DBID);
      }
      else if (cdoClass.isResourceFolder())
      {
        serverInfo = setDBID(cdoClass, CDO_RESOURCE_FOLDER_CLASS_DBID);
      }
      else if (cdoClass.isResourceNode())
      {
        // Important to check the abstract class *after* the concrete ones!
        serverInfo = setDBID(cdoClass, CDO_RESOURCE_NODE_CLASS_DBID);
      }
    }

    return serverInfo;
  }
}
