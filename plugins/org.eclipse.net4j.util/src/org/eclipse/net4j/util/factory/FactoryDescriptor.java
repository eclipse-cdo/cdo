/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

import org.eclipse.net4j.internal.util.factory.PluginFactoryRegistry.IFactoryDescriptor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;

/**
 * A {@link IFactory factory} that delegates to an {@link IExtensionRegistry extension registry} contribution.
 * <p>
 * Example contribution:
 *
 * <pre>
 *    &lt;extension point="org.eclipse.net4j.util.factories"&gt;
 *       &lt;factory
 *             class="org.eclipse.net4j.util.concurrent.TimerLifecycle$DaemonFactory"
 *             productGroup="org.eclipse.net4j.util.timers"
 *             type="daemon"/&gt;
 *    &lt;/extension&gt;
 * </pre>
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @deprecated As of 3.19 no longer public API.
 */
@Deprecated
public class FactoryDescriptor implements IFactoryDescriptor
{
  public FactoryDescriptor(IConfigurationElement configurationElement)
  {
    throw new UnsupportedOperationException();
  }

  public IConfigurationElement getConfigurationElement()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IFactoryKey getKey()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IFactory createFactory()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object create(String description)
  {
    throw new UnsupportedOperationException();
  }
}
