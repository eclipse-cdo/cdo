/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.container;

import org.eclipse.net4j.util.container.IElementProcessor;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class PluginElementProcessorList extends Lifecycle
{
  private static final String ATTR_CLASS = "class";

  public static final String NAMESPACE = Net4j.BUNDLE_ID;

  public static final String EXT_POINT = "postProcessors";

  private List<IElementProcessor> processors = new ArrayList();

  private IRegistryChangeListener extensionRegistryListener = new IRegistryChangeListener()
  {
    public void registryChanged(IRegistryChangeEvent event)
    {
      IExtensionDelta[] deltas = event.getExtensionDeltas(NAMESPACE, EXT_POINT);
      for (IExtensionDelta delta : deltas)
      {
        // TODO Handle ExtensionDelta
        Net4j.LOG.warn("ExtensionDelta not handled: " + delta);
      }
    }
  };

  public PluginElementProcessorList()
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(NAMESPACE, EXT_POINT);
    for (IConfigurationElement element : elements)
    {
      IElementProcessor processor = (IElementProcessor)element.createExecutableExtension(ATTR_CLASS);
      processors.add(processor);
    }

    extensionRegistry.addRegistryChangeListener(extensionRegistryListener, NAMESPACE);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    extensionRegistry.removeRegistryChangeListener(extensionRegistryListener);
    processors.clear();
    super.doDeactivate();
  }
}
