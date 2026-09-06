/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfigFactory;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Creates DB test configurations using the generic capability description syntax of
 * {@link org.eclipse.emf.cdo.tests.config.impl.RepositoryConfigFactory}.
 * <p>
 * This DB-specific extension recognizes {@code ranges}, {@code copyOnBranch}, and {@code inverseLists}. The first
 * requires effective auditing or branching, and {@code copyOnBranch=true} requires effective branching.
 * {@code inverseLists} has no additional parser-level dependency. Absent DB capabilities preserve the underlying
 * configuration defaults; explicit boolean values use the common syntax documented by the superclass.
 *
 * @param <C>
 *          the concrete DB configuration type
 * @author Eike Stepper
 */
abstract class DBConfigFactory<C extends DBConfig> extends RepositoryConfigFactory<C>
{
  private static final String RANGES = "ranges";

  private static final String COPY_ON_BRANCH = "copyOnBranch";

  private static final String INVERSE_LISTS = "inverseLists";

  protected DBConfigFactory(String type)
  {
    super(type);
  }

  @Override
  protected boolean isAdditionalCapability(String name)
  {
    return RANGES.equals(name) || COPY_ON_BRANCH.equals(name) || INVERSE_LISTS.equals(name);
  }

  @Override
  protected void validateAdditional(C config, Map<String, Boolean> options, String description)
  {
    boolean audits = getOption(config, options, "audits", config.supportingAudits());
    boolean branches = getOption(config, options, "branches", config.supportingBranches());
    boolean ranges = getOption(config, options, RANGES, config.withRanges());
    boolean copyOnBranch = getOption(config, options, COPY_ON_BRANCH, config.copyOnBranch());

    if (ranges && !audits && !branches)
    {
      throw productCreationException(description);
    }

    if (copyOnBranch && !branches)
    {
      throw productCreationException(description);
    }
  }

  @Override
  protected void applyAdditional(C config, Map<String, Boolean> options)
  {
    applyBoolean(options, RANGES, config::withRanges);
    applyBoolean(options, COPY_ON_BRANCH, config::copyOnBranch);
    applyBoolean(options, INVERSE_LISTS, config::inverseLists);
  }

  private void applyBoolean(Map<String, Boolean> options, String name, Consumer<Boolean> setter)
  {
    Boolean value = options.get(name);
    if (value != null)
    {
      setter.accept(value);
    }
  }
}
