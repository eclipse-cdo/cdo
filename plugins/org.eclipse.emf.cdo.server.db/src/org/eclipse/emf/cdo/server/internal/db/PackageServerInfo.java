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

import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackage;

import org.eclipse.net4j.db.ddl.IDBSchema;

/**
 * @author Eike Stepper
 */
public final class PackageServerInfo extends ServerInfo
{
  private IDBSchema schema;

  private PackageServerInfo(int id)
  {
    super(id);
  }

  public static PackageServerInfo setDBID(CDOPackage cdoPackage, int id)
  {
    PackageServerInfo serverInfo = new PackageServerInfo(id);
    ((InternalCDOPackage)cdoPackage).setServerInfo(serverInfo);
    return serverInfo;
  }

  public static IDBSchema getSchema(CDOPackage cdoPackage)
  {
    return ((PackageServerInfo)cdoPackage.getServerInfo()).schema;
  }

  public static void setSchema(CDOPackage cdoPackage, IDBSchema schema)
  {
    ((PackageServerInfo)cdoPackage.getServerInfo()).schema = schema;
  }
}
