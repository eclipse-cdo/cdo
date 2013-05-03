/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.admin;

import org.eclipse.emf.cdo.internal.admin.CDOAdminClientImpl;
import org.eclipse.emf.cdo.internal.admin.CDOAdminClientManagerImpl;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

/**
 * Various static methods that may help with CDO remote administration.
 *
 * @author Eike Stepper
 */
public final class CDOAdminClientUtil
{
  public static final long DEFAULT_TIMEOUT = ISignalProtocol.DEFAULT_TIMEOUT;

  private CDOAdminClientUtil()
  {
  }

  public static CDOAdminClient openAdmin(String url)
  {
    return openAdmin(url, DEFAULT_TIMEOUT);
  }

  public static CDOAdminClient openAdmin(String url, long timeout)
  {
    return openAdmin(url, timeout, IPluginContainer.INSTANCE);
  }

  public static CDOAdminClient openAdmin(String url, long timeout, IManagedContainer container)
  {
    return new CDOAdminClientImpl(url, timeout, container);
  }

  public static CDOAdminClientManager createAdminManager()
  {
    return createAdminManager(IPluginContainer.INSTANCE);
  }

  public static CDOAdminClientManager createAdminManager(IManagedContainer container)
  {
    return new CDOAdminClientManagerImpl(container);
  }
}
