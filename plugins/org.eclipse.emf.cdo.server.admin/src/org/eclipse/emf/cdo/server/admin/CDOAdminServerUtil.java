/*
 * Copyright (c) 2012, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.om.OMBundle;

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

  /**
   * Registers Admin factories in a container while configuring a separate container from which the resulting Admin
   * infrastructure obtains repositories and other dependencies. The two containers may be the same or different.
   *
   * @param container
   *          the container that owns the registered Admin factories
   * @param repositoriesContainer
   *          the container from which Admin infrastructure obtains repositories and related dependencies
   */
  public static void prepareContainer(IManagedContainer container, IManagedContainer repositoriesContainer)
  {
    container.registerFactory(new CDOAdminServer.Factory(repositoriesContainer));
    container.registerFactory(new CDOAdminServerProtocol.Factory(repositoriesContainer));
  }

  /**
   * Manually registers the CDO Admin factories in a raw container, using it both as the factory owner and as the
   * repositories container.
   *
   * @param container
   *          the raw container to configure
   * @deprecated Automatically initialized containers discover the Admin factories through their declarative
   *             contributions. Use {@link OMBundle#prepareContainer(IManagedContainer)} only when intentionally
   *             preparing a raw container manually.
   */
  @Deprecated
  public static void prepareContainer(IManagedContainer container)
  {
    prepareContainer(container, container);
  }
}
