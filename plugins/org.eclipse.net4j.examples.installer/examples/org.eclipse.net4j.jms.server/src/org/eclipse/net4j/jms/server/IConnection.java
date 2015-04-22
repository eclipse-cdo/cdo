/*
 * Copyright (c) 2007, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server;

/**
 * @author Eike Stepper
 */
public interface IConnection
{
  public IServer getServer();

  public String getUserName();

  public ISession openSession(int sessionID);

  public ISession getSession(int sessionID);
}
