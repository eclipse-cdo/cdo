/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.DeleteResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenSessionAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.internal.ui.transfer.RepositoryTransferDragListener;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transfer.ui.TransferDropAdapter;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOSessionsView extends ContainerView
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDOSessionsView"; //$NON-NLS-1$

  private static Map<String, ResourceOpener> resourceOpeners = Collections.synchronizedMap(new HashMap<String, ResourceOpener>());

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

    // CDOMergeDropAdapter.support(viewer);

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
      @Override
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    super.fillLocalToolBar(manager);
    manager.add(openSessionAction);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);

    manager.add(new Separator("group.new"));
    manager.add(new Separator("group.open"));
    manager.add(new GroupMarker("group.openWith"));
    manager.add(new Separator("group.edit"));

    List<CDOResourceNode> nodes = UIUtil.getElements(selection, CDOResourceNode.class);
    if (nodes != null && !nodes.isEmpty())
    {
      manager.add(new DeleteResourceAction(getShell(), nodes));
    }

    manager.add(new GroupMarker("group.new.branch"));
    manager.add(new Separator("group.port"));
    manager.add(new Separator("group.build"));
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    manager.add(new Separator("group.properties"));
  }

  @Override
  protected void doubleClicked(Object object)
  {
    IWorkbenchPage page = getSite().getPage();

    if (object instanceof CDOSession)
    {
      CDOSession session = (CDOSession)object;
      if (session.getViews().length == 0)
      {
        OpenTransactionAction.openTransaction(session);
        return;
      }
    }

    if (object instanceof CDOResourceLeaf)
    {
      CDOResourceLeaf resource = (CDOResourceLeaf)object;

      ResourceOpener opener = getResourceOpener(resource);
      if (opener != null)
      {
        opener.openResource(page, resource);
      }
      else
      {
        CDOEditorUtil.openEditor(page, resource);
      }

      return;
    }

    super.doubleClicked(object);
  }

  public static ResourceOpener getResourceOpener(CDOResourceLeaf resource)
  {
    String extension = new Path(resource.getName()).getFileExtension();
    return getResourceOpener(extension);
  }

  public static ResourceOpener getResourceOpener(String resourceExtension)
  {
    return resourceOpeners.get(resourceExtension);
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
