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
package org.eclipse.emf.cdo.server.internal.db.info;

import org.eclipse.emf.cdo.protocol.model.CDOClass;

import org.eclipse.net4j.db.IDBTable;

/**
 * @author Eike Stepper
 */
public final class ClassServerInfo extends ServerInfo
{
  private IDBTable table;

  public ClassServerInfo(int id)
  {
    super(id);
  }

  public static IDBTable getTable(CDOClass cdoClass)
  {
    return ((ClassServerInfo)cdoClass.getServerInfo()).table;
  }

  public static void setTable(CDOClass cdoClass, IDBTable table)
  {
    ((ClassServerInfo)cdoClass.getServerInfo()).table = table;
  }
}
