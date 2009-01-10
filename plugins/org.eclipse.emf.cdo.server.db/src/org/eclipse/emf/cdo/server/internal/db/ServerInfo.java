/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.model.CDOModelElement;

/**
 * @author Eike Stepper
 */
public abstract class ServerInfo
{
  private int dbID;

  public static final int CDO_CORE_PACKAGE_DBID = -1;

  public static final int CDO_RESOURCE_PACKAGE_DBID = -2;

  public static final int CDO_OBJECT_CLASS_DBID = -1;

  public static final int CDO_RESOURCE_CLASS_DBID = -2;

  public static final int CDO_RESOURCE_NODE_CLASS_DBID = -3;

  public static final int CDO_RESOURCE_FOLDER_CLASS_DBID = -4;

  public static final int CDO_FOLDER_FEATURE_DBID = -1;

  public static final int CDO_NAME_FEATURE_DBID = -2;

  public static final int CDO_NODES_FEATURE_DBID = -3;

  public static final int CDO_CONTENTS_FEATURE_DBID = -4;

  protected ServerInfo(int dbID)
  {
    this.dbID = dbID;
  }

  @Override
  public String toString()
  {
    return String.valueOf(dbID);
  }

  public static synchronized int getDBID(CDOModelElement modelElement)
  {
    ServerInfo serverInfo = (ServerInfo)modelElement.getServerInfo();
    return serverInfo.dbID;
  }
}
