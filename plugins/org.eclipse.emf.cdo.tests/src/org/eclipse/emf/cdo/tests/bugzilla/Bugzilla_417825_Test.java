/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.session.SessionUtil;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * Bug 417825 - Invalidator can die if CDOSession can not be activated within 100ms.
 *
 * @author Eike Stepper
 */
public class Bugzilla_417825_Test extends AbstractCDOTest
{
  public void testInvalidatorLifecycle() throws Exception
  {
    SessionUtil.setTestDelayInSessionActivation(new Runnable()
    {
      public void run()
      {
        // The session invalidator polls its queue every 100ms, so wait a little longer to see if it died
        ConcurrencyUtil.sleep(500L);
      }
    });

    try
    {
      CDOSessionImpl session2 = (CDOSessionImpl)openSession();
      assertEquals(true, session2.isActive());
      assertEquals(true, LifecycleUtil.isActive(session2.getInvalidator()));
    }
    finally
    {
      SessionUtil.setTestDelayInSessionActivation(null);
    }
  }
}
