/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
