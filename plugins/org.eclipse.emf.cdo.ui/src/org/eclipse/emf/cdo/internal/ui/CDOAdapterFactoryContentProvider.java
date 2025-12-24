/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;

import org.eclipse.jface.viewers.Viewer;

/**
 * @author Eike Stepper
 */
public class CDOAdapterFactoryContentProvider extends AdapterFactoryContentProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOAdapterFactoryContentProvider.class);

  public CDOAdapterFactoryContentProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    super.inputChanged(viewer, oldInput, newInput);

    if (viewer != null)
    {
      viewerRefresh = new RunnableViewerRefresh(viewer);
    }
    else
    {
      viewerRefresh = null;
    }
  }

  public RunnableViewerRefresh getViewerRefresh()
  {
    return (RunnableViewerRefresh)viewerRefresh;
  }

  @Override
  public boolean hasChildren(Object object)
  {
    try
    {
      return super.hasChildren(object);
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(ex);
      }

      return false;
    }
  }
}
