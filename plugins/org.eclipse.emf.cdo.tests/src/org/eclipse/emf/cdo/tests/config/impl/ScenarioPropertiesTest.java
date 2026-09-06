/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig;

import junit.framework.TestCase;

/**
 * Tests the system-property based scenario selection without starting a repository or a session.
 */
public class ScenarioPropertiesTest extends TestCase
{
  private static final String[] PROPERTIES = { //
      IConstants.TEST_SCENARIO_PROPERTY, //
      IConstants.TEST_REPOSITORY_PROPERTY, //
      IConstants.TEST_SESSION_PROPERTY, //
      IConstants.TEST_MODEL_PROPERTY };

  public void testNoOverride()
  {
    withProperties(new String[0], new String[0], () -> assertNull(Scenario.createFromProperties()));
  }

  public void testCompleteScenario()
  {
    withProperties(new String[] { IConstants.TEST_SCENARIO_PROPERTY }, new String[] { "MEM/JVM/NATIVE" }, () -> {
      IScenario scenario = Scenario.createFromProperties();
      assertFalse(scenario.getRepositoryConfig().supportingAudits());
      assertFalse(scenario.getRepositoryConfig().supportingBranches());
      assertSame(IConstants.JVM, scenario.getSessionConfig());
      assertSame(IConstants.NATIVE, scenario.getModelConfig());
    });
  }

  public void testComponentTriple()
  {
    withProperties(new String[] { IConstants.TEST_REPOSITORY_PROPERTY, IConstants.TEST_SESSION_PROPERTY, IConstants.TEST_MODEL_PROPERTY },
        new String[] { "MEM:branches,clientIDs", "JVM", "NATIVE" }, () -> {
          IScenario scenario = Scenario.createFromProperties();
          assertTrue(scenario.getRepositoryConfig().supportingBranches());
          assertEquals(IDGenerationLocation.CLIENT, scenario.getRepositoryConfig().idGenerationLocation());
          assertSame(IConstants.JVM, scenario.getSessionConfig());
          assertSame(IConstants.NATIVE, scenario.getModelConfig());
        });
  }

  public void testMEMFactoryCapabilities()
  {
    MEMConfig config = new MEMConfigFactory().create(null);
    assertFalse(config.supportingAudits());
    assertFalse(config.supportingBranches());
    assertTrue(config.supportingChunks());
    assertTrue(config.supportingExtRefs());
    assertEquals(IDGenerationLocation.STORE, config.idGenerationLocation());
    assertEquals(ListOrdering.ORDERED, config.listOrdering());

    config = new MEMConfigFactory().create("");
    assertFalse(config.supportingAudits());
    assertTrue(config.supportingChunks());

    config = new MEMConfigFactory().create("audits");
    assertTrue(config.supportingAudits());
    config = new MEMConfigFactory().create("branches");
    assertTrue(config.supportingBranches());
    assertTrue(config.supportingAudits());
    config = new MEMConfigFactory().create("branches,clientIDs");
    assertEquals(IDGenerationLocation.CLIENT, config.idGenerationLocation());
  }

  public void testMEMFactoryBooleanCapabilities()
  {
    MEMConfig config = new MEMConfigFactory().create("audits=true,branches=true,chunks=false,extRefs=false,clientIDs=false,unorderedLists=false");
    assertTrue(config.supportingAudits());
    assertTrue(config.supportingBranches());
    assertFalse(config.supportingChunks());
    assertFalse(config.supportingExtRefs());
    assertEquals(IDGenerationLocation.STORE, config.idGenerationLocation());
    assertEquals(ListOrdering.ORDERED, config.listOrdering());

    config = new MEMConfigFactory().create("audits=false,branches=false,chunks=false,extRefs=false,clientIDs=false,unorderedLists=false");
    assertFalse(config.supportingAudits());
    assertFalse(config.supportingBranches());
    assertFalse(config.supportingChunks());
    assertFalse(config.supportingExtRefs());
    assertEquals(IDGenerationLocation.STORE, config.idGenerationLocation());
    assertEquals(ListOrdering.ORDERED, config.listOrdering());
  }

  public void testMEMFactoryOrderIndependence()
  {
    MEMConfig first = new MEMConfigFactory().create("branches,clientIDs,chunks=false,extRefs=false,unorderedLists");
    MEMConfig second = new MEMConfigFactory().create("unorderedLists=true,extRefs=false,chunks=false,clientIDs=true,branches=true");
    assertEquals(first.supportingAudits(), second.supportingAudits());
    assertEquals(first.supportingBranches(), second.supportingBranches());
    assertEquals(first.supportingChunks(), second.supportingChunks());
    assertEquals(first.supportingExtRefs(), second.supportingExtRefs());
    assertEquals(first.idGenerationLocation(), second.idGenerationLocation());
    assertEquals(first.listOrdering(), second.listOrdering());
  }

  public void testMEMFactoryValidation()
  {
    assertRejectedMEM("branches,audits=false");
    assertRejectedMEM("branches,branches");
    assertRejectedMEM("unknown");
    assertRejectedMEM("branches=maybe");
    assertRejectedMEM("branches,,audits");
    assertRejectedMEM("branches=audit");
  }

  private void assertRejectedMEM(String description)
  {
    try
    {
      new MEMConfigFactory().create(description);
      fail("ProductCreationException expected for " + description);
    }
    catch (RuntimeException expected)
    {
      // Expected.
    }
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
