/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 * @since 3.0
 */
@Deprecated
public class AsyncContentProvider implements ITreeContentProvider
{
  private static final Object[] NO_CHILDREN = {};

  private ITreeContentProvider delegate;

  private WeakHashMap<Object, LoadJob> loadJobs = new WeakHashMap<>();

  @Deprecated
  public AsyncContentProvider(ITreeContentProvider delegate)
  {
    this.delegate = delegate;
  }

  @Deprecated
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    delegate.inputChanged(viewer, oldInput, newInput);
  }

  @Deprecated
  @Override
  public void dispose()
  {
    delegate.dispose();
  }

  @Deprecated
  @Override
  public final Object[] getElements(Object inputElement)
  {
    return getChildren(inputElement);
  }

  @Deprecated
  @Override
  public Object[] getChildren(Object parentElement)
  {
    if (parentElement instanceof LoadJob)
    {
      return NO_CHILDREN;
    }

    if (parentElement instanceof CDOObject)
    {
      CDOObject object = (CDOObject)parentElement;
      if (object.cdoState() == CDOState.PROXY)
      {
        LoadJob loadJob = getLoadJob(parentElement);
        return loadJob.getChildren();
      }
    }

    return delegate.getChildren(parentElement);
  }

  @Deprecated
  @Override
  public boolean hasChildren(Object parentElement)
  {
    if (parentElement instanceof LoadJob)
    {
      return false;
    }

    if (parentElement instanceof CDOObject)
    {
      CDOObject object = (CDOObject)parentElement;
      if (object.cdoState() == CDOState.PROXY)
      {
        LoadJob loadJob = getLoadJob(parentElement);
        return loadJob.hasChildren();
      }
    }

    return delegate.hasChildren(parentElement);
  }

  @Deprecated
  @Override
  public Object getParent(Object element)
  {
    if (element instanceof LoadJob)
    {
      LoadJob loadJob = (LoadJob)element;
      return loadJob.getParent();
    }

    if (element instanceof CDOObject)
    {
      CDOObject object = (CDOObject)element;
      if (object.cdoState() == CDOState.PROXY)
      {
        LoadJob loadJob = getLoadJob(element);
        return loadJob.getParent();
      }
    }

    return delegate.getParent(element);
  }

  private synchronized LoadJob getLoadJob(Object parentElement)
  {
    LoadJob loadJob = loadJobs.get(parentElement);
    if (loadJob == null)
    {
      loadJob = new LoadJob(parentElement);
      loadJobs.put(parentElement, loadJob);
      new Thread(loadJob).start();
    }

    return loadJob;
  }

  /**
   * @author Eike Stepper
   */
  private final class LoadJob implements Runnable
  {
    private Object parent;

    private List<Object> children = new ArrayList<>();

    public LoadJob(Object parent)
    {
      this.parent = parent;
    }

    public Object getParent()
    {
      return parent;
    }

    public Object[] getChildren()
    {
      synchronized (children)
      {
        return children.toArray(new Object[children.size()]);
      }
    }

    public boolean hasChildren()
    {
      return false;
    }

    @Override
    public void run()
    {
    }

    @Override
    public String toString()
    {
      return Messages.getString("AsyncContentProvider_0"); //$NON-NLS-1$
    }
  }
}
