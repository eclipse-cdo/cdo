/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.server.ILockingManager.DurableViewHandler;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;

/**
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_RESTARTABLE)
public class LockingManagerRestartRepositoryTest extends LockingManagerRestartSessionTest
{
  @Override
  protected void doBetweenSessionCloseAndOpen()
  {
    DurableViewHandler[] handlers = getRepository().getLockingManager().getDurableViewHandlers();
    restartRepository();

    for (DurableViewHandler handler : handlers)
    {
      getRepository().getLockingManager().addDurableViewHandler(handler);
    }
  }
}
