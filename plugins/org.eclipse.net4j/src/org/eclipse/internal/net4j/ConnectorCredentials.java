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
package org.eclipse.internal.net4j;

import org.eclipse.net4j.IConnectorCredentials;

/**
 * @author Eike Stepper
 */
public class ConnectorCredentials implements IConnectorCredentials
{
  private String userID;

  public ConnectorCredentials(String userID)
  {
    this.userID = userID;
  }

  public String getUserID()
  {
    return userID;
  }
}
