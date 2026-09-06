/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.Scenario;

import junit.framework.TestCase;

/**
 * Verifies that the DB test bundle contributes repository factories to standalone ordinary JUnit launches.
 */
public class ScenarioPropertiesDBTest extends TestCase
{
  private static final String[] PROPERTIES = { IConstants.TEST_SCENARIO_PROPERTY, IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY,
      IConstants.TEST_MODEL_PROPERTY };

  public void testH2FactoryDescription()
  {
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "H2:branches,ranges,clientIDs/JVM/NATIVE" }, () -> {
      IScenario scenario = Scenario.createFromProperties();
      DBConfig config = (DBConfig)scenario.getRepositoryConfig();
      assertTrue(config.supportingBranches());
      assertTrue(config.withRanges());
      assertEquals(IDGenerationLocation.CLIENT, config.idGenerationLocation());
    });
  }

  public void testNullAndEmptyDescriptionUseDefaults()
  {
    assertDefault(new H2ConfigFactory().create(null));
    assertDefault(new H2ConfigFactory().create(""));
  }

  public void testOrderIndependence()
  {
    DBConfig first = new H2ConfigFactory().create("branches,ranges,clientIDs,copyOnBranch");
    DBConfig second = new H2ConfigFactory().create("copyOnBranch=true,clientIDs=true,ranges=true,branches=true");
    assertEquals(first.supportingAudits(), second.supportingAudits());
    assertEquals(first.supportingBranches(), second.supportingBranches());
    assertEquals(first.withRanges(), second.withRanges());
    assertEquals(first.copyOnBranch(), second.copyOnBranch());
    assertEquals(first.idGenerationLocation(), second.idGenerationLocation());
  }

  public void testValidAndInvalidCombinations()
  {
    assertTrue(new H2ConfigFactory().create("audits,ranges").withRanges());
    assertTrue(new H2ConfigFactory().create("branches,copyOnBranch").copyOnBranch());
    assertTrue(new H2ConfigFactory().create("inverseLists").inverseLists());

    assertRejected("branches,audits=false");
    assertRejected("ranges");
    assertRejected("copyOnBranch");
    assertRejected("inverseLists,inverseLists");
  }

  private void assertDefault(DBConfig config)
  {
    assertFalse(config.supportingAudits());
    assertFalse(config.supportingBranches());
    assertTrue(config.supportingChunks());
    assertTrue(config.supportingExtRefs());
    assertFalse(config.withRanges());
    assertFalse(config.copyOnBranch());
    assertFalse(config.inverseLists());
    assertEquals(IDGenerationLocation.STORE, config.idGenerationLocation());
    assertEquals(ListOrdering.ORDERED, config.listOrdering());
  }

  private void assertRejected(String description)
  {
    try
    {
      new H2ConfigFactory().create(description);
      fail("ProductCreationException expected for " + description);
    }
    catch (RuntimeException expected)
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
