/*
 * Copyright (c) 2008-2013, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMOfflineConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;

/**
 * @author Eike Stepper
 */
public interface IConstants
{
  public static final RepositoryConfig MEM = new MEMConfig();

  public static final RepositoryConfig MEM_AUDITS = new MEMConfig().supportingAudits(true);

  public static final RepositoryConfig MEM_BRANCHES = new MEMConfig().supportingBranches(true);

  public static final RepositoryConfig MEM_BRANCHES_UUIDS = new MEMConfig().idGenerationLocation(IDGenerationLocation.CLIENT);

  public static final RepositoryConfig MEM_OFFLINE = new MEMOfflineConfig().idGenerationLocation(IDGenerationLocation.CLIENT);

  public static final RepositoryConfig MEM_EMBEDDED_BRANCHES = new MEMConfig.Embedded().supportingBranches(true);

  public static final SessionConfig EMBEDDED = Net4j.JVM.Embedded.INSTANCE;

  public static final SessionConfig JVM = Net4j.JVM.INSTANCE;

  public static final SessionConfig TCP = Net4j.TCP.INSTANCE;

  public static final SessionConfig SSL = Net4j.SSL.INSTANCE;

  public static final SessionConfig WS = Net4j.WS.INSTANCE;

  public static final ModelConfig NATIVE = ModelConfig.Native.INSTANCE;

  public static final ModelConfig LEGACY = ModelConfig.Legacy.INSTANCE;
}
