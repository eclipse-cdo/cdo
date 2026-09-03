/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IScenario;

import junit.framework.TestCase;

/**
 * Tests the system-property based scenario selection without starting a repository or a session.
 */
public class ScenarioPropertiesTest extends TestCase
{
  private static final String[] PROPERTIES = { IConstants.TEST_SCENARIO_PROPERTY, IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY,
      IConstants.TEST_MODEL_PROPERTY };

  public void testNoOverride()
  {
    withProperties(new String[0], new String[0], () -> assertNull(Scenario.createFromProperties()));
  }

  public void testCompleteScenario()
  {
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "MEM/JVM/NATIVE" }, () -> {
      IScenario scenario = Scenario.createFromProperties();
      assertSame(IConstants.MEM, scenario.getRepositoryConfig());
      assertSame(IConstants.JVM, scenario.getSessionConfig());
      assertSame(IConstants.NATIVE, scenario.getModelConfig());
    });
  }

  public void testComponentTriple()
  {
    withProperties(new String[] { IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY, IConstants.TEST_MODEL_PROPERTY },
        new String[] { "MEM_BRANCHES_UUIDS", "JVM", "NATIVE" }, () -> {
          IScenario scenario = Scenario.createFromProperties();
          assertSame(IConstants.MEM_BRANCHES_UUIDS, scenario.getRepositoryConfig());
          assertSame(IConstants.JVM, scenario.getSessionConfig());
          assertSame(IConstants.NATIVE, scenario.getModelConfig());
        });
  }

  public void testMixedFormsRejected()
  {
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY, IConstants.TEST_MODEL_PROPERTY }, new String[] { "MEM/JVM/NATIVE", "NATIVE" },
        () -> assertRejected());
  }

  public void testIncompleteTripleRejected()
  {
    withProperties(new String[] { IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY }, new String[] { "MEM", "JVM" },
        () -> assertRejected());
  }

  public void testUnknownNamesRejected()
  {
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "UNKNOWN/JVM/NATIVE" }, () -> assertRejected());
    withProperties(new String[] { IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY, IConstants.TEST_MODEL_PROPERTY },
        new String[] { "unknown", "JVM", "NATIVE" }, () -> assertRejected());
    withProperties(new String[] { IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY, IConstants.TEST_MODEL_PROPERTY },
        new String[] { "MEM", "unknown", "NATIVE" }, () -> assertRejected());
    withProperties(new String[] { IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY, IConstants.TEST_MODEL_PROPERTY },
        new String[] { "MEM", "JVM", "unknown" }, () -> assertRejected());
  }

  public void testMalformedScenarioRejected()
  {
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "MEM/JVM" }, () -> assertRejected());
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "MEM\\sJVM/NATIVE" }, () -> assertRejected());
  }

  public void testEscapedConfigurationDescription()
  {
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "MEM:slash\\sinside/JVM/NATIVE" }, () -> {
      IScenario scenario = Scenario.createFromProperties();
      assertSame(IConstants.MEM, scenario.getRepositoryConfig());
    });

    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "MEM:colon\\\\sinside/JVM/NATIVE" }, () -> {
      IScenario scenario = Scenario.createFromProperties();
      assertSame(IConstants.MEM, scenario.getRepositoryConfig());
    });

    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "MEM:backslash\\\\\\\\inside/JVM/NATIVE" }, () -> {
      IScenario scenario = Scenario.createFromProperties();
      assertSame(IConstants.MEM, scenario.getRepositoryConfig());
    });
  }

  private void assertRejected()
  {
    try
    {
      Scenario.createFromProperties();
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // Expected.
    }
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
