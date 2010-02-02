/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.tests.config.impl.ContainerConfig;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

/**
 * @author Eike Stepper
 */
public interface IConstants
{
  public static final ContainerConfig COMBINED = ContainerConfig.Combined.INSTANCE;

  public static final ContainerConfig SEPARATED = ContainerConfig.Separated.INSTANCE;

  public static final RepositoryConfig.MEM MEM = RepositoryConfig.MEM.INSTANCE;

  public static final RepositoryConfig.MEMOffline MEM_OFFLINE = RepositoryConfig.MEMOffline.INSTANCE;

  public static final SessionConfig EMBEDDED = SessionConfig.Embedded.INSTANCE;

  public static final SessionConfig JVM = SessionConfig.JVM.INSTANCE;

  public static final SessionConfig TCP = SessionConfig.TCP.INSTANCE;

  public static final ModelConfig NATIVE = ModelConfig.Native.INSTANCE;

  public static final ModelConfig LEGACY = ModelConfig.Legacy.INSTANCE;
}
