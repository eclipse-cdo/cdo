/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Victor Roldan Betancort - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.util.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOViewProvider;
import org.eclipse.emf.cdo.util.CDOViewProviderRegistry;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * When instanced in Eclipse, it's populated with contributions from the viewProvider Extension Point. A default
 * CDOViewProvider implementation is registered, regardless of the execution environment.
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 * @see CDOViewProvider
 */
public class CDOViewProviderRegistryImpl extends Container<CDOViewProvider> implements CDOViewProviderRegistry
{
  public static final CDOViewProviderRegistryImpl INSTANCE = new CDOViewProviderRegistryImpl();

  private static final String EXT_POINT = "viewProviders";

  private List<CDOViewProvider> viewProviders = new ArrayList<CDOViewProvider>();

  public CDOViewProviderRegistryImpl()
  {
    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      addViewProvider(new PluginContainerViewProvider());
    }
  }

  public CDOView provideView(URI uri, CDOViewSet viewSet)
  {
    if (uri == null)
    {
      return null;
    }

    if (viewSet != null)
    {
      try
      {
        String uuid = CDOURIUtil.extractRepositoryUUID(uri);
        CDOView view = viewSet.resolveView(uuid);
        if (view != null)
        {
          return view;
        }
      }
      catch (Exception ignore)
      {
        // Do nothing
      }
    }

    for (CDOViewProvider viewProvider : getViewProviders(uri))
    {
      CDOView view = viewProvider.getView(uri, viewSet);
      if (view != null)
      {
        return view;
      }
    }

    return null;
  }

  public CDOViewProvider[] getViewProviders(URI uri)
  {
    List<CDOViewProvider> result = new ArrayList<CDOViewProvider>();
    for (CDOViewProvider viewProvider : viewProviders)
    {
      if (viewProvider.matchesRegex(uri))
      {
        result.add(viewProvider);
      }
    }

    // Sort highest priority first
    Collections.sort(result, new Comparator<CDOViewProvider>()
    {
      public int compare(CDOViewProvider o1, CDOViewProvider o2)
      {
        return -new Integer(o1.getPriority()).compareTo(o2.getPriority());
      }
    });

    return result.toArray(new CDOViewProvider[result.size()]);
  }

  // public CDOViewProvider[] getViewProviders(URI uri, CDOViewSet viewSet)
  // {
  // List<CDOViewProvider> orderedProviders = new ArrayList<CDOViewProvider>();
  // for (CDOViewProvider viewProvider : viewProviders)
  // {
  // if (viewProvider.matchesRegex(uri))
  // {
  // for (int i = orderedProviders.size() - 1; i >= 0; i--)
  // {
  // if (viewProvider.getPriority() <= orderedProviders.get(i).getPriority())
  // {
  // orderedProviders.add(i + 1, viewProvider);
  // break;
  // }
  // }
  //
  // // if not inserted, it has highest priority
  // if (!orderedProviders.contains(viewProvider))
  // {
  // orderedProviders.add(0, viewProvider);
  // }
  // }
  // }
  //
  // return orderedProviders.toArray(new CDOViewProvider[orderedProviders.size()]);
  // }

  public void addViewProvider(CDOViewProvider viewProvider)
  {
    boolean added;
    synchronized (viewProviders)
    {
      added = !viewProviders.contains(viewProvider);
      if (added)
      {
        viewProviders.add(viewProvider);
      }
    }

    if (added)
    {
      fireElementAddedEvent(viewProvider);
    }
  }

  public void removeViewProvider(CDOViewProvider viewProvider)
  {
    boolean removed;
    synchronized (viewProviders)
    {
      removed = viewProviders.remove(viewProvider);
    }

    if (removed)
    {
      fireElementRemovedEvent(viewProvider);
    }
  }

  public CDOViewProvider[] getElements()
  {
    synchronized (viewProviders)
    {
      return viewProviders.toArray(new CDOViewProvider[viewProviders.size()]);
    }
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (viewProviders)
    {
      return viewProviders.isEmpty();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      try
      {
        readExtensions();
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
  }

  public void readExtensions()
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT);
    for (IConfigurationElement element : configurationElements)
    {
      try
      {
        CDOViewProviderDescriptor descriptor = new CDOViewProviderDescriptor(element);
        addViewProvider(descriptor);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CDOViewProviderDescriptor extends AbstractCDOViewProvider
  {
    private IConfigurationElement element;

    public CDOViewProviderDescriptor(IConfigurationElement element)
    {
      super(element.getAttribute("regex"), Integer.parseInt(element.getAttribute("priority")));
      this.element = element;

      if (StringUtil.isEmpty(element.getAttribute("class")))
      {
        throw new IllegalArgumentException("class not defined for extension " + element);
      }

      if (StringUtil.isEmpty(element.getAttribute("regex")))
      {
        throw new IllegalArgumentException("regex not defined for extension " + element);
      }
    }

    public CDOView getView(URI uri, CDOViewSet viewSet)
    {
      return getViewProvider().getView(uri, viewSet);
    }

    private CDOViewProvider getViewProvider()
    {
      try
      {
        return (CDOViewProvider)element.createExecutableExtension("class");
      }
      catch (CoreException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }
}
