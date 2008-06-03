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

import org.eclipse.emf.cdo.common.model.CDOModelElement;

/**
 * @author Eike Stepper
 */
public abstract class ServerInfo
{
  private int dbID;

  protected ServerInfo(int dbID)
  {
    this.dbID = dbID;
  }

  @Override
  public String toString()
  {
    return String.valueOf(dbID);
  }

  public static int getDBID(CDOModelElement modelElement)
  {
    return ((ServerInfo)modelElement.getServerInfo()).dbID;
  }
}
