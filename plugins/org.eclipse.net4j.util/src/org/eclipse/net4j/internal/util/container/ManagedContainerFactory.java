/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.util.container;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

/**
 * Provides the central bootstrap hook for the global managed container.
 *
 * @author Eike Stepper
 */
public final class ManagedContainerFactory
{
  private ManagedContainerFactory()
  {
  }

  /**
   * Creates the global managed container for the current platform.
   * <p>
   * This method is intentionally separate from the general platform-specific container factory so that the
   * initialization of {@link IManagedContainer#INSTANCE} has a dedicated central hook.
   *
   * @return the global managed container
   */
  public static IManagedContainer createGlobalContainer()
  {
    return OMPlatform.INSTANCE.createManagedContainer();
  }
}
