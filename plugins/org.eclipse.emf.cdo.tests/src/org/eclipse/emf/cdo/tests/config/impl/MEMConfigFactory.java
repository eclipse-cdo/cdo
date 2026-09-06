/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig;

/**
 * Creates ordinary MEM repository configurations and inherits generic capability handling from
 * {@link RepositoryConfigFactory}.
 *
 * @author Eike Stepper
 */
public final class MEMConfigFactory extends RepositoryConfigFactory<MEMConfig>
{
  public MEMConfigFactory()
  {
    super("MEM");
  }

  @Override
  protected MEMConfig createConfig()
  {
    return new MEMConfig();
  }
}
