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

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Creates repository test configurations from comma-separated boolean capability descriptions.
 * <p>
 * The description grammar is:
 *
 * <pre>
 * description ::= option ("," option)*
 * option ::= capability | capability "=" boolean
 * boolean ::= true | false
 * </pre>
 *
 * A capability without a value is shorthand for {@code capability=true}; explicit {@code =true} and
 * {@code =false} forms are always accepted. An absent capability preserves the underlying configuration default.
 * The complete description is parsed before it is validated or applied, so option order is semantically irrelevant.
 * Duplicate names, unknown names, empty names or options, and malformed boolean values are rejected.
 * <p>
 * The generic capabilities are {@code audits}, {@code branches}, {@code chunks}, {@code extRefs}, {@code clientIDs},
 * and {@code unorderedLists}. {@code clientIDs=true} selects client-side IDs and {@code clientIDs=false} selects
 * store-side IDs, as represented by
 * {@link org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation}. Similarly,
 * {@code unorderedLists=true} selects unordered lists and {@code unorderedLists=false} selects ordered lists, as
 * represented by {@link org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering}. These are intentionally
 * boolean capabilities even though the underlying Java API uses enums.
 * <p>
 * Branching implies auditing through {@link RepositoryConfig#supportingBranches(boolean)}. Consequently,
 * {@code branches=true,audits=false} is contradictory and rejected. In particular, the absence of {@code chunks} or
 * {@code extRefs} preserves their underlying default of {@code true}; it is not equivalent to explicitly applying
 * {@code false}.
 * <p>
 * Generic capabilities are parsed, validated, and applied here. Subclasses can add capabilities by overriding
 * {@link #isAdditionalCapability(String)}, {@link #validateAdditional(RepositoryConfig, Map, String)}, and
 * {@link #applyAdditional(RepositoryConfig, Map)}.
 *
 * @param <C>
 *          the concrete repository configuration type
 * @author Eike Stepper
 */
public abstract class RepositoryConfigFactory<C extends RepositoryConfig> extends Factory
{
  private static final String AUDITS = "audits";

  private static final String BRANCHES = "branches";

  private static final String CHUNKS = "chunks";

  private static final String EXT_REFS = "extRefs";

  private static final String CLIENT_IDS = "clientIDs";

  private static final String UNORDERED_LISTS = "unorderedLists";

  protected RepositoryConfigFactory(String type)
  {
    super(IConstants.REPOSITORY_CONFIGS, type);
  }

  @Override
  public final C create(String description) throws ProductCreationException
  {
    C config = createConfig();
    if (description == null || description.trim().length() == 0)
    {
      return config;
    }

    Map<String, Boolean> options = parse(description);
    validate(config, options, description);
    apply(config, options);
    return config;
  }

  /**
   * Creates the unmodified configuration whose defaults are preserved when capabilities are absent.
   *
   * @return a new configuration
   */
  protected abstract C createConfig();

  /**
   * Determines whether a name is supplied by a subclass.
   *
   * @param name
   *          the normalized option name
   * @return {@code true} if the subclass recognizes the name
   */
  protected boolean isAdditionalCapability(String name)
  {
    return false;
  }

  /**
   * Validates subclass-specific options after all options have been normalized.
   *
   * @param config
   *          the unmodified configuration, for its underlying defaults
   * @param options
   *          the normalized options
   * @param description
   *          the original description for error reporting
   */
  protected void validateAdditional(C config, Map<String, Boolean> options, String description)
  {
    // Do nothing.
  }

  /**
   * Applies subclass-specific options after generic validation has succeeded.
   *
   * @param config
   *          the configuration to modify
   * @param options
   *          the normalized options
   */
  protected void applyAdditional(C config, Map<String, Boolean> options)
  {
    // Do nothing.
  }

  /**
   * Returns an option value or its supplied default when the option is absent.
   *
   * @param config
   *          the configuration supplying the default
   * @param options
   *          the normalized options
   * @param name
   *          the option name
   * @param defaultValue
   *          the fallback value
   * @return the effective option value
   */
  protected final boolean getOption(C config, Map<String, Boolean> options, String name, boolean defaultValue)
  {
    Boolean value = options.get(name);
    return value == null ? defaultValue : value.booleanValue();
  }

  private Map<String, Boolean> parse(String description)
  {
    Map<String, Boolean> options = new HashMap<>();
    String[] tokens = description.split(",", -1);

    for (String token : tokens)
    {
      String option = token.trim();
      if (option.length() == 0)
      {
        throw productCreationException(description);
      }

      int equals = option.indexOf('=');
      String name = equals < 0 ? option : option.substring(0, equals).trim();
      if (!isCapability(name) || options.containsKey(name))
      {
        throw productCreationException(description);
      }

      boolean value = true;
      if (equals >= 0)
      {
        if (option.indexOf('=', equals + 1) >= 0)
        {
          throw productCreationException(description);
        }

        String valueText = option.substring(equals + 1).trim();
        if ("true".equals(valueText))
        {
          value = true;
        }
        else if ("false".equals(valueText))
        {
          value = false;
        }
        else
        {
          throw productCreationException(description);
        }
      }

      options.put(name, value);
    }

    return options;
  }

  private boolean isCapability(String name)
  {
    return AUDITS.equals(name) || BRANCHES.equals(name) || CHUNKS.equals(name) || EXT_REFS.equals(name) || CLIENT_IDS.equals(name)
        || UNORDERED_LISTS.equals(name) || isAdditionalCapability(name);
  }

  private void validate(C config, Map<String, Boolean> options, String description)
  {
    boolean branches = getOption(config, options, BRANCHES, config.supportingBranches());
    if (branches && Boolean.FALSE.equals(options.get(AUDITS)))
    {
      throw productCreationException(description);
    }

    validateAdditional(config, options, description);
  }

  private void apply(C config, Map<String, Boolean> options)
  {
    applyBoolean(options, BRANCHES, config::supportingBranches);
    applyBoolean(options, AUDITS, config::supportingAudits);
    applyBoolean(options, CHUNKS, config::supportingChunks);
    applyBoolean(options, EXT_REFS, config::supportingExtRefs);
    applyBoolean(options, CLIENT_IDS, value -> config.idGenerationLocation(value ? IDGenerationLocation.CLIENT : IDGenerationLocation.STORE));
    applyBoolean(options, UNORDERED_LISTS, value -> config.listOrdering(value ? ListOrdering.UNORDERED : ListOrdering.ORDERED));
    applyAdditional(config, options);
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
