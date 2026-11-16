/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * Initializes the Eclipse Extension Registry for standalone CDO applications.
 * <p>
 * EMF's extension processor performs the general Eclipse Extension Registry bootstrap in Plain Java. Its work goes
 * beyond registering EMF generated packages and makes CDO extension points available to clients and servers. This
 * initialization is performed once per class loader through Java class initialization.
 * <p>
 * Under OSGi, the platform initializes and manages the extension registry.
 *
 * @author Eike Stepper
 */
public final class ExtensionRegistryBootstrap
{
  static
  {
    if (!OMPlatform.INSTANCE.isOSGiRunning())
    {
      EcorePlugin.ExtensionProcessor.process(null);
    }
  }

  private ExtensionRegistryBootstrap()
  {
  }

  /**
   * Ensures that the standalone extension registry bootstrap has run.
   * <p>
   * Invoking this method triggers class initialization. Subsequent invocations have no effect.
   */
  public static void init()
  {
    // Intentionally empty. Trigger class initialization.
  }
}
