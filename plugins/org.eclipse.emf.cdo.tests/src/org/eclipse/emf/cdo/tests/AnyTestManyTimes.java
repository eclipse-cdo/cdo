/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_416474_Test;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.Scenario;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AnyTestManyTimes extends TestSuite implements IConstants
{
  private static final RepositoryConfig REPOSITORY_CONFIG = MEM;

  private static final SessionConfig SESSION_CONFIG = JVM;

  private static final ModelConfig MODEL_CONFIG = NATIVE;

  private static final String METHOD = "testMatchesAnyStringAttribute";

  private static final int RUNS = 10000;

  private static ConfigTest createTest(final String displayName)
  {
    return new Bugzilla_416474_Test()
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
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite(AnyTestManyTimes.class)
    {
      @Override
      public void addTest(Test test)
      {
        if (test instanceof TestCase)
        {
          TestCase testCase = (TestCase)test;
          if ("warning".equals(testCase.getName()))
          {
            return;
          }
        }

        super.addTest(test);
      }
    };

    for (int run = 0; run < RUNS; run++)
    {
      ConfigTest test = createTest(METHOD + " [" + (run + 1) + "]");
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
