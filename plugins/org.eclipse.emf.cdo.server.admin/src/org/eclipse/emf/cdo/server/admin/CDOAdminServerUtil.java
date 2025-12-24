/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.admin;

import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServer;
import org.eclipse.emf.cdo.server.internal.admin.protocol.CDOAdminServerProtocol;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * Various static methods that may help with CDO remote administration.
 *
 * @author Eike Stepper
 */
public final class CDOAdminServerUtil
{
  private CDOAdminServerUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container, IManagedContainer repositoriesContainer)
  {
    container.registerFactory(new CDOAdminServer.Factory(repositoriesContainer));
    container.registerFactory(new CDOAdminServerProtocol.Factory(repositoriesContainer));
  }

  public static void prepareContainer(IManagedContainer container)
  {
    prepareContainer(container, container);
  }
}
