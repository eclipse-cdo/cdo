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
package org.eclipse.net4j.jms;

import org.eclipse.net4j.internal.jms.protocol.JMSClientProtocolFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author Eike Stepper
 */
public final class JMSUtil
{
  private static IManagedContainer transportContainer;

  private JMSUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new JMSClientProtocolFactory());
  }

  public static Context createInitialContext() throws NamingException
  {
    if (transportContainer == null)
    {
      throw new IllegalStateException("transportContainer == null");
    }

    return new JMSInitialContext(transportContainer);
  }

  public static IManagedContainer getTransportContainer()
  {
    return transportContainer;
  }

  public static void setTransportContainer(IManagedContainer transportContainer)
  {
    JMSUtil.transportContainer = transportContainer;
  }
}
