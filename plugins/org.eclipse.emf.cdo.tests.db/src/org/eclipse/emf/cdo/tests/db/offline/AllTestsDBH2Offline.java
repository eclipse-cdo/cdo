/*
 * Copyright (c) 2012, 2013, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.db.DBConfigs;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_319552_Test;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_325097_Test;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_326047_Test;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_328352_Test;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_329014_Test;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_376470_Test;
import org.eclipse.emf.cdo.tests.offline.Bugzilla_376566_Test;
import org.eclipse.emf.cdo.tests.offline.FailoverTest;
import org.eclipse.emf.cdo.tests.offline.OfflineDelayedTest;
import org.eclipse.emf.cdo.tests.offline.OfflineLockReplicationTest;
import org.eclipse.emf.cdo.tests.offline.OfflineLockingTest;
import org.eclipse.emf.cdo.tests.offline.OfflineTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBH2Offline extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBH2Offline().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, new H2OfflineConfig().idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.add(FailoverTest.class);
    testClasses.add(OfflineTest.class);
    testClasses.add(OfflineDelayedTest.class);
    testClasses.add(OfflineLockingTest.class);
    testClasses.add(OfflineLockReplicationTest.class);

    testClasses.add(Bugzilla_329014_Test.class);
    testClasses.add(Bugzilla_328352_Test.class);
    testClasses.add(Bugzilla_326047_Test.class);
    testClasses.add(Bugzilla_325097_Test.class);
    testClasses.add(Bugzilla_319552_Test.class);
    testClasses.add(Bugzilla_376470_Test.class);
    testClasses.add(Bugzilla_376566_Test.class);
    testClasses.add(Bugzilla_377727_Test.class);
  }
}
