/*
 * Copyright (c) 2007-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.internal.ui.actions.OpenSessionAction;
import org.eclipse.emf.cdo.internal.ui.transfer.RepositoryTransferDragListener;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transfer.ui.TransferDropAdapter;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOSessionsView extends ContainerView
{
  public final static String ID = "org.eclipse.emf.cdo.ui.CDOSessionsView"; //$NON-NLS-1$

  private static Map<String, ResourceOpener> resourceOpeners = Collections
      .synchronizedMap(new HashMap<String, ResourceOpener>());

  private OpenSessionAction openSessionAction;

  public CDOSessionsView()
  {
  }

  @Override
  protected Control createUI(Composite parent)
  {
    openSessionAction = new OpenSessionAction(getViewSite().getPage());
    Control control = super.createUI(parent);

    TreeViewer viewer = getViewer();
    RepositoryTransferDragListener.support(viewer);
    TransferDropAdapter.support(viewer);

    return control;
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
    IWorkbenchPage page = getSite().getPage();

    if (object instanceof CDOResourceLeaf)
    {
      CDOResourceLeaf resource = (CDOResourceLeaf)object;

      String name = resource.getName();
      String extension = new Path(name).getFileExtension();

      ResourceOpener opener = resourceOpeners.get(extension);
      if (opener != null)
      {
        opener.openResource(page, resource);
      }
      else
      {
        CDOEditorUtil.openEditor(page, resource);
      }
    }
    else
    {
      super.doubleClicked(object);
    }
  }

  public static ResourceOpener registerResourceOpener(String resourceExtension, ResourceOpener opener)
  {
    return resourceOpeners.put(resourceExtension, opener);
  }

  public static ResourceOpener unregisterResourceOpener(String resourceExtension)
  {
    return resourceOpeners.remove(resourceExtension);
  }

  /**
   * @author Eike Stepper
   */
  public interface ResourceOpener
  {
    public void openResource(IWorkbenchPage page, CDOResourceLeaf resource);
  }
}
