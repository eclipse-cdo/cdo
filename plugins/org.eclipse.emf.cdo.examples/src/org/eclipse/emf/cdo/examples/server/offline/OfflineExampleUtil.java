/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
