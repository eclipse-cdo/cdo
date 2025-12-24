/*
 * Copyright (c) 2009, 2011, 2012, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.net4j.util.container.IPluginContainer;

/**
 * @author Eike Stepper
 * @since 2.0
 * @deprecated As of 4.20 use {@link ContainerRepositoryProvider}.
 */
@Deprecated
public final class PluginRepositoryProvider extends ContainerRepositoryProvider
{
  @Deprecated
  public static final PluginRepositoryProvider INSTANCE = new PluginRepositoryProvider();

  private PluginRepositoryProvider()
  {
    super(IPluginContainer.INSTANCE);
  }
}
