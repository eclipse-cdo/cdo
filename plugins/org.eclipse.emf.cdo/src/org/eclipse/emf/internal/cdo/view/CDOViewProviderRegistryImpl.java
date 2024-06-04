/*
 * Copyright (c) 2009-2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Victor Roldan Betancort - initial API and implementation
 *   Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProvider.CDOViewProvider2;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.session.CDOViewContainerImpl;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.text.MessageFormat;
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

  private static final String EXT_POINT = "viewProviders"; //$NON-NLS-1$

  /**
   * Sort highest priority first.
   */
  private static final Comparator<CDOViewProvider> COMPARATOR = new Comparator<CDOViewProvider>()
  {
    @Override
    public int compare(CDOViewProvider vp1, CDOViewProvider vp2)
    {
      return -Integer.compare(vp1.getPriority(), vp2.getPriority());
    }
  };

  private final List<CDOViewProvider> viewProviders = new ArrayList<>();

  public CDOViewProviderRegistryImpl()
  {
    addViewProvider(PluginContainerViewProvider.INSTANCE);
  }

  @Override
  public CDOView provideView(URI uri, ResourceSet resourceSet)
  {
    if (uri == null)
    {
      return null;
    }

    CDOViewSet viewSet = null;

    for (CDOViewProvider viewProvider : getViewProviders(uri))
    {
      if (viewSet == null)
      {
        viewSet = CDOUtil.getViewSet(resourceSet);
      }

      InternalCDOView view = provideView(uri, resourceSet, viewSet, viewProvider);
      if (view != null)
      {
        CDOViewProvider provider = view.getProvider();
        if (provider != null)
        {
          if (provider != viewProvider)
          {
            throw new IllegalStateException("View providers don't match");
          }
        }
        else
        {
          view.setProvider(viewProvider);
        }

        return view;
      }
    }

    return null;
  }

  private InternalCDOView provideView(URI uri, ResourceSet resourceSet, CDOViewSet viewSet, CDOViewProvider viewProvider)
  {
    if (viewSet != null && viewProvider instanceof CDOViewProvider2)
    {
      URI viewURI;

      try
      {
        viewURI = ((CDOViewProvider2)viewProvider).getViewURI(uri);
      }
      catch (IllegalArgumentException ex)
      {
        throw new IllegalArgumentException(ex.getMessage() + ": " + uri, ex);
      }
      catch (Error ex)
      {
        throw ex;
      }

      try
      {
        InternalCDOView view = (InternalCDOView)viewSet.resolveView(viewURI);
        if (view != null)
        {
          return view;
        }
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
    }

    try
    {
      CDOViewContainerImpl.VIEW_PROVIDER.set(viewProvider);

      InternalCDOView view = (InternalCDOView)viewProvider.getView(uri, resourceSet);
      if (view != null)
      {
        return view;
      }
    }
    finally
    {
      CDOViewContainerImpl.VIEW_PROVIDER.remove();
    }

    return null;
  }

  @Override
  @Deprecated
  public Pair<CDOView, CDOViewProvider> provideViewWithInfo(URI uri, ResourceSet resourceSet)
  {
    CDOView view = provideView(uri, resourceSet);
    if (view != null)
    {
      return Pair.create(view, view.getProvider());
    }

    return null;
  }

  @Override
  public CDOViewProvider[] getViewProviders(URI uri)
  {
    List<CDOViewProvider> result = new ArrayList<>();

    synchronized (viewProviders)
    {
      for (CDOViewProvider viewProvider : viewProviders)
      {
        if (viewProvider.matchesRegex(uri))
        {
          result.add(viewProvider);
        }
      }
    }

    // Sort highest priority first
    Collections.sort(result, COMPARATOR);

    return result.toArray(new CDOViewProvider[result.size()]);
  }

  @Override
  public boolean hasViewProvider(CDOViewProvider viewProvider)
  {
    synchronized (viewProviders)
    {
      return viewProviders.contains(viewProvider);
    }
  }

  @Override
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

  @Override
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

  @Override
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
    private final IConfigurationElement element;

    private CDOViewProvider delegate;

    public CDOViewProviderDescriptor(IConfigurationElement element)
    {
      super(getRegex(element), getPriority(element));
      this.element = element;

      if (StringUtil.isEmpty(element.getAttribute("class"))) //$NON-NLS-1$
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOViewProviderRegistryImpl.4"), element)); //$NON-NLS-1$
      }
    }

    @Override
    public CDOView getView(URI uri, ResourceSet resourceSet)
    {
      CDOViewProvider viewProvider = getViewProvider();
      return viewProvider.getView(uri, resourceSet);
    }

    @Override
    public URI getResourceURI(CDOView view, String path)
    {
      CDOViewProvider viewProvider = getViewProvider();
      return viewProvider.getResourceURI(view, path);
    }

    @Override
    public URI getViewURI(URI uri)
    {
      CDOViewProvider viewProvider = getViewProvider();
      if (viewProvider instanceof CDOViewProvider2)
      {
        return ((CDOViewProvider2)viewProvider).getViewURI(uri);
      }

      return super.getViewURI(uri);
    }

    @Override
    public String getPath(URI uri)
    {
      CDOViewProvider viewProvider = getViewProvider();
      if (viewProvider instanceof CDOViewProvider2)
      {
        return ((CDOViewProvider2)viewProvider).getPath(uri);
      }

      return super.getPath(uri);
    }

    @Override
    protected String getDeclaredName()
    {
      return "CDOViewProviderDescriptor";
    }

    @Override
    protected String getActualName()
    {
      CDOViewProvider actualProvider = delegate;
      if (actualProvider != null)
      {
        return ReflectUtil.getSimpleClassName(actualProvider);
      }

      return element.getAttribute("class");
    }

    private CDOViewProvider getViewProvider()
    {
      synchronized (element)
      {
        if (delegate == null)
        {
          try
          {
            delegate = (CDOViewProvider)element.createExecutableExtension("class"); //$NON-NLS-1$
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }

        return delegate;
      }
    }

    private static String getRegex(IConfigurationElement element)
    {
      String value = element.getAttribute("regex");
      if (StringUtil.isEmpty(value))
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOViewProviderRegistryImpl.6"), element)); //$NON-NLS-1$
      }

      return value;
    }

    private static int getPriority(IConfigurationElement element)
    {
      try
      {
        String value = element.getAttribute("priority");
        return Integer.parseInt(value);
      }
      catch (Exception ex)
      {
        return DEFAULT_PRIORITY;
      }
    }
  }
}
