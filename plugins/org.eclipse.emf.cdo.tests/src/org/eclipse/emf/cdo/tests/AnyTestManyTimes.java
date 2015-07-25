/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_316444_Test;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.Scenario;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AnyTestManyTimes extends TestSuite implements IConstants
{
  private static final RepositoryConfig REPOSITORY_CONFIG = MEM_AUDITS;

  private static final SessionConfig SESSION_CONFIG = JVM;

  private static final ModelConfig MODEL_CONFIG = NATIVE;

  private static final Class<? extends ConfigTest> CLASS = Bugzilla_316444_Test.class;

  private static final String METHOD = "testLockParentWithEAttributeChange";

  private static final int RUNS = 10000;

  public static Test suite()
  {
    TestSuite suite = new TestSuite(CLASS.getName());

    for (int run = 0; run < RUNS; run++)
    {
      final String displayName = METHOD + " [" + (run + 1) + "]";

      Bugzilla_316444_Test test = new Bugzilla_316444_Test()
      {
        @Override
        public String getName()
        {
          return displayName;
        }

        @Override
        protected String getTestMethodName()
        {
          return METHOD;
        }
      };

      test.setName(METHOD);
      test.setScenario(new Scenario(REPOSITORY_CONFIG, SESSION_CONFIG, MODEL_CONFIG)
      {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean alwaysCleanRepositories()
        {
          return true;
        }
      });

      suite.addTest(test);
    }

    return suite;
  }
}
