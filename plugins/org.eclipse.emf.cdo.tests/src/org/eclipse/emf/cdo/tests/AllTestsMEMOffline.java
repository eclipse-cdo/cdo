/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsMEMOffline extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsMEMOffline().getTestSuite(AllConfigs.class.getName());
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    // super.initTestClasses(testClasses);

    // testClasses.add(OfflineRawTest.class);
    // testClasses.add(OfflineTest.class);
    // testClasses.add(OfflineDelayed2Test.class);
    testClasses.add(FailoverTest.class);
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM_OFFLINE, JVM, NATIVE);
  }
}
