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
package org.eclipse.emf.cdo.server.admin;

import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServerProtocol;

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
    container.registerFactory(new CDOAdminServerProtocol.Factory(repositoriesContainer));
  }

  public static void prepareContainer(IManagedContainer container)
  {
    prepareContainer(container, container);
  }
}
