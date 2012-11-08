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
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

/**
 * Clone Repository doesn't retry to init root resource
 * <p>
 * See bug 376566.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore
public class Bugzilla_376567_Test extends AbstractSyncingTest
{
  @Override
  protected boolean isHinderInitialReplication()
  {
    return true;
  }

  public void testInitCloneWithoutMaster() throws Exception
  {
    // try
    // {
    // openSession();
    // fail("IllegalStateException expected");
    // }
    // catch (Throwable t)
    // {
    // if (t instanceof RemoteException)
    // {
    // t = ((RemoteException)t).getCause();
    // }
    //
    // String msg = ((IllegalStateException)t).getMessage();
    // assertEquals("Root resource has not been initialized in Repository[repo1]", msg);
    // }
  }
}
