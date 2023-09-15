/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.providers;

import org.eclipse.emf.cdo.internal.ui.CDOAdapterFactoryContentProvider;
import org.eclipse.emf.cdo.internal.ui.CDOContentProvider;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.AdapterFactory;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Eike Stepper
 */
public class SystemContentProvider extends CDOContentProvider<ISystemDescriptor>
{
  public static final Comparator<Object> SYSTEM_COMPARATOR = Comparator.comparing(SystemContentProvider::name);

  private static final Object[] NO_CHILDREN = new Object[0];

  private final AdapterFactory adapterFactory;

  private final CDOAdapterFactoryContentProvider delegate;

  public SystemContentProvider(AdapterFactory adapterFactory)
  {
    this.adapterFactory = adapterFactory;
    delegate = new CDOAdapterFactoryContentProvider(adapterFactory);

  }

  @Override
  public void inputChanged(Viewer newViewer, Object oldInput, Object newInput)
  {
    super.inputChanged(newViewer, oldInput, newInput);
    delegate.inputChanged(newViewer, oldInput, newInput);
  }

  @Override
  public Object[] getElements(Object object)
  {
    return getChildren(object);
  }

  @Override
  public Object[] getChildren(Object object)
  {
    if (object == ISystemManager.INSTANCE)
    {
      ISystemDescriptor[] descriptors = ISystemManager.INSTANCE.getDescriptors();
      Object[] children = new Object[descriptors.length];

      for (int i = 0; i < descriptors.length; i++)
      {
        ISystemDescriptor descriptor = descriptors[i];
        System system = descriptor.getSystem();
        children[i] = system != null ? system : descriptor;
      }

      Arrays.sort(children, SYSTEM_COMPARATOR);
      return children;
    }

    if (object instanceof ISystemDescriptor)
    {
      return NO_CHILDREN;
    }

    Object[] children = super.getChildren(object);
    children = filteredChildren(children);

    if (object instanceof System)
    {
      Arrays.sort(children, Module.COMPARATOR);
    }
    else if (object instanceof Module)
    {
      Arrays.sort(children, Baseline.COMPARATOR);
    }
    else if (object instanceof Stream)
    {
      Arrays.sort(children, Baseline.COMPARATOR);
    }

    return children;
  }

  protected Object[] filteredChildren(Object[] children)
  {
    return children;
  }

  @Override
  public boolean hasChildren(Object object)
  {
    if (object == ISystemManager.INSTANCE)
    {
      return !ISystemManager.INSTANCE.isEmpty();
    }

    if (object instanceof ISystemDescriptor)
    {
      return false;
    }

    return super.hasChildren(object);
  }

  @Override
  public Object getParent(Object object)
  {
    if (object instanceof ISystemDescriptor || object instanceof System)
    {
      return ISystemManager.INSTANCE;
    }

    return super.getParent(object);
  }

  @Override
  protected Object adapt(Object target, Object type)
  {
    return adapterFactory.adapt(target, type);
  }

  @Override
  protected Object[] modifyChildren(Object parent, Object[] children)
  {
    return children;
  }

  @Override
  protected ITreeContentProvider getContentProvider(Object object)
  {
    return delegate;
  }

  @Override
  protected org.eclipse.emf.cdo.internal.ui.RunnableViewerRefresh getViewerRefresh()
  {
    return delegate.getViewerRefresh();
  }

  @Override
  protected boolean isContext(Object object)
  {
    return object instanceof ISystemDescriptor;
  }

  @Override
  protected org.eclipse.emf.cdo.internal.ui.CDOContentProvider.ContextState getContextState(ISystemDescriptor descriptor)
  {
    switch (descriptor.getState())
    {
    case Closing:
    case Closed:
      return ContextState.Closed;

    case Opening:
      return ContextState.Opening;

    case Open:
      return ContextState.Open;

    default:
      throw new IllegalStateException("Unexpected system state: " + descriptor);
    }
  }

  @Override
  protected void openContext(ISystemDescriptor descriptor)
  {
    descriptor.open();
  }

  @Override
  protected void closeContext(ISystemDescriptor descriptor)
  {
    descriptor.close();
  }

  @Override
  protected Object getRootObject(ISystemDescriptor descriptor)
  {
    return descriptor.getSystem();
  }

  private static String name(Object o)
  {
    if (o instanceof System)
    {
      return StringUtil.safe(((System)o).getName());
    }

    if (o instanceof ISystemDescriptor)
    {
      return StringUtil.safe(((ISystemDescriptor)o).getSystemName());
    }

    return StringUtil.EMPTY;
  }
}
