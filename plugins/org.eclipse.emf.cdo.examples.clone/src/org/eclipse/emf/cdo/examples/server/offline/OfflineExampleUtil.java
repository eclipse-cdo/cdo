/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.offline;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Martin Fluegge
 * @since 4.0
 */
public class OfflineExampleUtil
{
  public static final String MASTER_NAME = "repo1";

  public static final int MASTER_PORT = 2036;

  public static final String CLONE_NAME = "clone";

  public static final int CLONE_PORT = 2037;

  public static IManagedContainer createContainer()
  {
    IManagedContainer container = ContainerUtil.createContainer();

    Net4jUtil.prepareContainer(container); // Register Net4j factories
    TCPUtil.prepareContainer(container); // Register TCP factories

    CDONet4jUtil.prepareContainer(container); // Register CDO client factories
    CDONet4jServerUtil.prepareContainer(container); // Register CDO server factories

    container.registerFactory(new CDOServerBrowser.ContainerBased.Factory(container));
    CDODBUtil.prepareContainer(container); // Register DBBrowserPage.Factory

    container.activate();
    return container;
  }
}
