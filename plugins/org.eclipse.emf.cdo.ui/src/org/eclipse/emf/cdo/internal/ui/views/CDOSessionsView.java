/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.actions.OpenSessionAction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CDOSessionsView extends ContainerView
{
  public final static String ID = "org.eclipse.emf.cdo.ui.CDOSessionsView"; //$NON-NLS-1$

  private OpenSessionAction openSessionAction;

  public CDOSessionsView()
  {
  }

  @Override
  protected Control createUI(Composite parent)
  {
    openSessionAction = new OpenSessionAction(getViewSite().getPage());
    return super.createUI(parent);
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new CDOItemProvider(getSite().getPage(), new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(openSessionAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (object instanceof CDOResource)
    {
      CDOResource resource = (CDOResource)object;
      CDOEditorUtil.openEditor(getSite().getPage(), resource.cdoView(), resource.getPath());
    }
    else
    {
      super.doubleClicked(object);
    }
  }
}
