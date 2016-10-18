/*
 * Copyright (c) 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
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
public class AnyTestManyTimesDB extends TestSuite implements IConstants
{
  private static final boolean SUPPORTING_AUDITS = false;

  private static final boolean SUPPORTING_BRANCHES = false;

  private static final boolean WITH_RANGES = false;

  private static final boolean COPY_ON_BRANCH = false;

  private static final IDGenerationLocation ID_GENERATION_LOCATION = IDGenerationLocation.STORE;

  private static final RepositoryConfig REPOSITORY_CONFIG = new H2Config(SUPPORTING_AUDITS, SUPPORTING_BRANCHES, WITH_RANGES, COPY_ON_BRANCH,
      ID_GENERATION_LOCATION);

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
    TestSuite suite = new TestSuite(AnyTestManyTimesDB.class)
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
