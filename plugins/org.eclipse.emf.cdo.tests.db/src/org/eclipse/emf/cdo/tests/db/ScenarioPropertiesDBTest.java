/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.Scenario;

import junit.framework.TestCase;

/**
 * Verifies that the DB test bundle contributes repository factories to standalone ordinary JUnit launches.
 */
public class ScenarioPropertiesDBTest extends TestCase
{
  private static final String[] PROPERTIES = { IConstants.TEST_SCENARIO_PROPERTY, IConstants.TEST_REPOSITORY_PROPERTY,
      IConstants.TEST_SESSION_PROPERTY, IConstants.TEST_MODEL_PROPERTY };

  private static final DBConfigs DB_CONFIGS = new DBConfigs()
  {
  };

  public void testH2FactoryDescription()
  {
    assertNotNull(DB_CONFIGS);
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "H2:branches-ranges-client/JVM/NATIVE" }, () -> {
      IScenario scenario = Scenario.createFromProperties();
      DBConfig config = (DBConfig)scenario.getRepositoryConfig();
      assertTrue(config.supportingBranches());
      assertTrue(config.withRanges());
    });
  }

  private static void withProperties(String[] names, String[] values, Runnable test)
  {
    String[] oldValues = new String[PROPERTIES.length];

    for (int i = 0; i < PROPERTIES.length; i++)
    {
      oldValues[i] = System.getProperty(PROPERTIES[i]);
      System.clearProperty(PROPERTIES[i]);
    }

    try
    {
      for (int i = 0; i < names.length; i++)
      {
        System.setProperty(names[i], values[i]);
      }

      test.run();
    }
    finally
    {
      for (int i = 0; i < PROPERTIES.length; i++)
      {
        if (oldValues[i] == null)
        {
          System.clearProperty(PROPERTIES[i]);
        }
        else
        {
          System.setProperty(PROPERTIES[i], oldValues[i]);
        }
      }
    }
  }
}
