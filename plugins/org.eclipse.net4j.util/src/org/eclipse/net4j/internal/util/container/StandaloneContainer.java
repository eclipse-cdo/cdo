/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.util.container;

import org.eclipse.net4j.util.container.IManagedContainerInitializer;
import org.eclipse.net4j.util.container.ManagedContainer;

import java.util.ServiceLoader;

/**
 * A managed container for applications that are not running inside OSGi.
 * Standalone providers are discovered and invoked during explicit activation,
 * as part of standalone-container preparation before normal managed-container activation completes.
 *
 * @author Eike Stepper
 * @since 3.30
 */
@SuppressWarnings("deprecation")
public class StandaloneContainer extends ManagedContainer implements org.eclipse.net4j.util.container.IPluginContainer
{
  public StandaloneContainer()
  {
  }

  @Override
  protected String getTypeName()
  {
    return "StandaloneContainer"; //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    initializeProviders();
    super.doActivate();
  }

  private void initializeProviders()
  {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null)
    {
      classLoader = IManagedContainerInitializer.class.getClassLoader();
    }

    for (IManagedContainerInitializer initializer : ServiceLoader.load(IManagedContainerInitializer.class, classLoader))
    {
      initializer.initialize(this);
    }
  }
}
