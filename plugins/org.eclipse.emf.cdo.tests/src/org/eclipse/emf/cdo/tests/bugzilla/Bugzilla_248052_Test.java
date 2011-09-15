/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.bundle.OM;

import java.util.Map;

/**
 * 248052: CDO: UnsupportedOperationException in HibernateStoreAccessor
 * <p>
 * See bug 248052
 * 
 * @author Simon McDuff
 */
public class Bugzilla_248052_Test extends AbstractCDOTest
{
  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(IRepository.Props.SUPPORTING_AUDITS, "false");
    return testProperties;
  }

  @Override
  protected void doSetUp() throws Exception
  {
    try
    {
      super.doSetUp();
    }
    catch (IllegalStateException ex)
    {
      OM.LOG.info("Expected IllegalStateException", ex);
    }
  }

  public void testNoSupportingDeltas() throws Exception
  {
    // Possible failure already in doSetup()
  }
}
