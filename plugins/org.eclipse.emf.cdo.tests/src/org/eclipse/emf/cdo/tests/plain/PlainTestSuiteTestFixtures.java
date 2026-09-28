/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

/**
 * Test-only classes used to verify routing without making them part of plain package discovery.
 *
 * @author Eike Stepper
 */
public final class PlainTestSuiteTestFixtures
{
  private PlainTestSuiteTestFixtures()
  {
  }

  /**
   * A plain test explicitly registered through a non-package suite path.
   *
   * @author Eike Stepper
   */
  public static class RegisteredOutsidePlainPackageTest extends PlainTest
  {
    public void testRunsUnderPlainScenario()
    {
    }
  }

  /**
   * An ordinary config test explicitly registered through a non-package suite path.
   *
   * @author Eike Stepper
   */
  public static class OrdinaryConfigTest extends ConfigTest
  {
    public void testRunsUnderRegularScenario()
    {
    }
  }

  /**
   * A plain test deliberately excluded from suite registration.
   *
   * @author Eike Stepper
   */
  public static class UnregisteredPlainTest extends PlainTest
  {
    public void testIsNotAutoRegistered()
    {
    }
  }
}
