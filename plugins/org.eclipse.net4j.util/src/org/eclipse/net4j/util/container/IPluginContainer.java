/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.internal.util.container.PluginContainer;

/**
 * A {@link IManagedContainer managed container} that is configured by the {@link org.eclipse.core.runtime.IExtensionRegistry extension registry}
 * .
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPluginContainer extends IManagedContainer
{
  public static final IPluginContainer INSTANCE = PluginContainer.getInstance();
}
