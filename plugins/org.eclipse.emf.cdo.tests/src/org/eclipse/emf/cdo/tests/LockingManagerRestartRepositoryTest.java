/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
