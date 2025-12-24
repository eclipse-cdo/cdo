/*
 * Copyright (c) 2009-2012, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.net4j;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.server.internal.net4j.protocol.CDOServerProtocolFactory;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * Various static methods that may help with the server-side setup to support Net4j-specific CDO {@link CDOCommonSession sessions}.
 *
 * @author Eike Stepper
 */
public final class CDONet4jServerUtil
{
  private CDONet4jServerUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container, IRepositoryProvider repositoryProvider)
  {
    prepareContainer(container);
    container.registerFactory(new CDOServerProtocolFactory(repositoryProvider));
  }

  public static void prepareContainer(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }

  /**
   * @since 4.5
   */
  public static FailoverMonitor getFailoverMonitor(IManagedContainer container, String type, String group)
  {
    return container.getElementOrNull(FailoverMonitor.PRODUCT_GROUP, type, group);
  }
}
