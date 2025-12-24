/*
 * Copyright (c) 2008-2013, 2016, 2017, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IRepositoryConfig extends IConfig, IRepositoryProvider
{
  public static final String REPOSITORY_NAME = "repo1";

  public static final String CAPABILITY_AUDITING = "repository.auditing";

  public static final String CAPABILITY_BRANCHING = "repository.branching";

  public static final String CAPABILITY_CHUNKING = "repository.chunking";

  public static final String CAPABILITY_EXTERNAL_REFS = "repository.external.refs";

  public static final String CAPABILITY_UNORDERED_LISTS = "repository.unordered.lists";

  public static final String CAPABILITY_UUIDS = "repository.uuids";

  public static final String CAPABILITY_OFFLINE = "repository.offline";

  public static final String CAPABILITY_RESTARTABLE = "repository.restartable";

  public IManagedContainer getServerContainer();

  public boolean hasServerContainer();

  public boolean isRestartable();

  public boolean supportingAudits();

  public boolean supportingBranches();

  public boolean supportingChunks();

  public boolean supportingExtRefs();

  public IDGenerationLocation idGenerationLocation();

  public ListOrdering listOrdering();

  public Map<String, String> getRepositoryProperties();

  public InternalRepository getRepository(String name, boolean activate);

  @Override
  public InternalRepository getRepository(String name);

  public void registerRepository(InternalRepository repository);

  public void setRestarting(boolean on);

  public IStore createStore(String repoName);

  /**
   * @author Eike Stepper
   */
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.TYPE, ElementType.METHOD })
  public @interface CallAddRepository
  {
  }

  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.TYPE, ElementType.METHOD })
  public @interface CountedTime
  {
  }
}
