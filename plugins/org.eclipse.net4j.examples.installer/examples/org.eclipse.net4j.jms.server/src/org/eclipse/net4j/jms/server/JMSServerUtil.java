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

import org.eclipse.net4j.jms.internal.server.protocol.JMSServerProtocolFactory;
import org.eclipse.net4j.jms.internal.server.protocol.admin.JMSAdminServerProtocolFactory;
import org.eclipse.net4j.jms.internal.server.store.NOOPStore;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class JMSServerUtil
{
  private JMSServerUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new JMSServerProtocolFactory());
    container.registerFactory(new JMSAdminServerProtocolFactory());
  }

  public static IStore createNOOPStore()
  {
    return new NOOPStore();
  }
}
