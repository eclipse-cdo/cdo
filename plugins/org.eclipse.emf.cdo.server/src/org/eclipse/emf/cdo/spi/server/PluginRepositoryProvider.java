/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public static final PluginRepositoryProvider INSTANCE = new PluginRepositoryProvider();

  private PluginRepositoryProvider()
  {
    super(IPluginContainer.INSTANCE);
  }
}
