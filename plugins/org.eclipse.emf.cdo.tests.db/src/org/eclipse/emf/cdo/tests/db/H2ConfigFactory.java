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

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * Creates the H2 repository configurations used by the DB test suites.
 *
 * @author Eike Stepper
 */
public final class H2ConfigFactory extends Factory
{
  public H2ConfigFactory()
  {
    super("org.eclipse.emf.cdo.tests.repositoryConfigs", "H2");
  }

  @Override
  public DBConfig create(String description) throws ProductCreationException
  {
    H2Config config = new H2Config();
    if (description == null || description.length() == 0 || "default".equals(description))
    {
      return config;
    }

    switch (description)
    {
    case "audit-ranges":
      return config.supportingAudits(true).withRanges(true);
    case "audit-ranges-client":
      return config.supportingAudits(true).withRanges(true).idGenerationLocation(IDGenerationLocation.CLIENT);
    case "branches-ranges":
      return config.supportingBranches(true).withRanges(true);
    case "branches-ranges-client":
      return config.supportingBranches(true).withRanges(true).idGenerationLocation(IDGenerationLocation.CLIENT);
    case "branches":
      return config.supportingBranches(true);
    case "branches-client":
      return config.supportingBranches(true).idGenerationLocation(IDGenerationLocation.CLIENT);
    case "client":
      return config.idGenerationLocation(IDGenerationLocation.CLIENT);
    default:
      throw productCreationException(description);
    }
  }
}
