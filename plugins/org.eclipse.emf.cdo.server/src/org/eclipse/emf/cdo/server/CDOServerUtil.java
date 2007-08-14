/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.server.RepositoryFactory;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocolFactory;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class CDOServerUtil
{
  private CDOServerUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container, IStoreProvider storeProvider,
      IRepositoryProvider repositoryProvider)
  {
    container.registerFactory(new RepositoryFactory(storeProvider));
    container.registerFactory(new CDOServerProtocolFactory(repositoryProvider));
  }
}
