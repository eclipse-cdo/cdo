/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server;

import org.eclipse.net4j.jms.internal.server.Server;

/**
 * @author Eike Stepper
 */
public interface IServer
{
  public static final IServer INSTANCE = Server.INSTANCE;

  public IDestination createDestination(String name, IDestination.Type type);

  public IConnection logon(String userName, String password);
}
