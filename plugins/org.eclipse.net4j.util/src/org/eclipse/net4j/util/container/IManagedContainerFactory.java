/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.factory.IFactory;

/**
 * An extension of the {@link IFactory} protocol that provides an
 * awareness of the container that instantiated it, so that the
 * factory may reach back into that container for dependencies.
 *
 * @author Christian W. Damus (CEA)
 * @author Eike Stepper
 * @since 3.3
 */
public interface IManagedContainerFactory extends IFactory, ContainerAware
{
  /**
   * Obtains the container that I should use to get my dependencies.
   */
  public IManagedContainer getManagedContainer();
}
