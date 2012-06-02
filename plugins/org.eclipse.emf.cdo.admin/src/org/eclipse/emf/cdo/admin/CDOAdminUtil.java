/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.internal.admin.CDOAdminClient;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.ISignalProtocol;

/**
 * Various static methods that may help with CDO remote administration.
 *
 * @author Eike Stepper
 */
public final class CDOAdminUtil
{
  public static final long DEFAULT_TIMEOUT = ISignalProtocol.DEFAULT_TIMEOUT;

  private CDOAdminUtil()
  {
  }

  public static CDOAdmin openAdmin(IConnector connector)
  {
    return openAdmin(connector, DEFAULT_TIMEOUT);
  }

  public static CDOAdmin openAdmin(IConnector connector, long timeout)
  {
    return new CDOAdminClient(connector, timeout);
  }
}
