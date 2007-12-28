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
package org.eclipse.net4j.jms.admin;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.jms.internal.admin.JMSAdmin;
import org.eclipse.net4j.jms.internal.admin.protocol.JMSAdminProtocolFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class JMSAdminUtil
{
  private JMSAdminUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new JMSAdminProtocolFactory());
  }

  public static IJMSAdmin createAdmin(IConnector connector)
  {
    return new JMSAdmin(connector);
  }
}
