/*
 * Copyright (c) 2009-2013 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.offline.FailoverTest;
import org.eclipse.emf.cdo.tests.offline.OfflineDelayed2Test;
import org.eclipse.emf.cdo.tests.offline.OfflineLockReplicationTest;
import org.eclipse.emf.cdo.tests.offline.OfflineLockingTest;
import org.eclipse.emf.cdo.tests.offline.OfflineTest;

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
    return new AllTestsMEMOffline().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, MEM_OFFLINE, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.add(OfflineLockingTest.class);
    testClasses.add(OfflineLockReplicationTest.class);
    testClasses.add(OfflineTest.class);
    testClasses.add(OfflineDelayed2Test.class);
    testClasses.add(FailoverTest.class);

    // MEM does not support raw replication
    // testClasses.add(OfflineRawTest.class);
    // testClasses.add(OfflineLockRawReplicationTest.class);
  }
}
