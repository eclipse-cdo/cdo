/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms;

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

  public static Context createInitialContext() throws NamingException
  {
    if (transportContainer == null)
    {
      throw new IllegalStateException("transportContainer == null"); //$NON-NLS-1$
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
