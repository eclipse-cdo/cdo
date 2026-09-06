/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

/**
 * Creates H2 repository configurations and inherits generic and DB-specific capability handling from
 * {@link DBConfigFactory}.
 *
 * @author Eike Stepper
 */
public final class H2ConfigFactory extends DBConfigFactory<H2Config>
{
  public H2ConfigFactory()
  {
    super("H2");
  }

  @Override
  protected H2Config createConfig()
  {
    return new H2Config();
  }
}
