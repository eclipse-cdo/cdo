/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.factory.IFactory;

/**
 * An extension of the {@link IFactory} protocol that provides an
 * awareness of the container that instantiated it, so that the
 * factory may reach back into that container for dependencies.
 *
 * @since 3.3
 */
public interface IManagedContainerFactory extends IFactory
{
  /**
   * Obtains the container that I should use to get my dependencies.
   */
  public IManagedContainer getManagedContainer();

  /**
   * Assigns the container that I should use to get my dependencies.
   *
   * @param container the container in which I am created/registered
   */
  public void setManagedContainer(IManagedContainer container);
}
