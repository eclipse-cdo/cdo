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
package org.eclipse.net4j.http;

import org.eclipse.net4j.connector.IConnector;

/**
 * @author Eike Stepper
 */
public interface IHTTPConnector extends IConnector
{
  public static final int DEFAULT_POLL_INTERVAL = 5 * 1000;// 5 seconds

  public static final int UNKNOWN_MAX_IDLE_TIME = -1;

  public String getConnectorID();

  public int getMaxIdleTime();
}
