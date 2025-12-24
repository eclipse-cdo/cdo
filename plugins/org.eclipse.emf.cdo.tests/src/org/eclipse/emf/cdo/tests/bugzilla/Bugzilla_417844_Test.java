/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.session.SessionUtil;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * Bug 417844 - InvalidationRunner can die if invalidations come too early.
 *
 * @author Eike Stepper
 */
public class Bugzilla_417844_Test extends AbstractCDOTest
{
  public void testInvalidationRunnerLifecycle() throws Exception
  {
    CDOSession session = openSession();
    final CDOTransaction transaction = session.openTransaction();
    transaction.createResource(getResourcePath("res1"));

    SessionUtil.setTestDelayInViewActivation(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          transaction.commit(); // Trigger invalidation of the view below
          ConcurrencyUtil.sleep(500L);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });

    try
    {
      CDOViewImpl view = (CDOViewImpl)session.openView();
      assertEquals(true, view.isActive());
      assertEquals(true, LifecycleUtil.isActive(view.getInvalidator()));
    }
    finally
    {
      SessionUtil.setTestDelayInViewActivation(null);
    }
  }
}
