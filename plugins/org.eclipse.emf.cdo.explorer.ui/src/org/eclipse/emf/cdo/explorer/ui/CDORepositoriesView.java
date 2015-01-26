/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.explorer.CDORepositoryManager;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchActionConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDORepositoriesView extends ContainerView
{
  private final Set<CDORepository> expandedRepositories = new HashSet<CDORepository>();

  private CDORepositoryItemProvider itemProvider;

  private NewRepositoryAction newAction;

  public CDORepositoriesView()
  {
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return CDOExplorerUtil.getRepositoryManager();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    itemProvider = new CDORepositoryItemProvider()
    {
      @Override
      public boolean hasChildren(Object element)
      {
        if (element instanceof CDORepositoryManager)
        {
          CDORepositoryManager repositoryManager = (CDORepositoryManager)element;

          TreeViewer viewer = CDORepositoriesView.this.getViewer();
          for (CDORepository repository : repositoryManager.getRepositories())
          {
            if (expandedRepositories.remove(repository) && !viewer.getExpandedState(repository))
            {

            }
          }
        }

        return super.hasChildren(element);
      }
    };
    return itemProvider;
  }

  @Override
  protected Control createUI(Composite parent)
  {
    newAction = new NewRepositoryAction();
    return super.createUI(parent);
  }

  @Override
  protected void initViewer()
  {
    super.initViewer();
    getViewer().addTreeListener(new ITreeViewerListener()
    {
      public void treeExpanded(TreeExpansionEvent event)
      {
        Object element = event.getElement();
        if (element instanceof CDORepository)
        {
          CDORepository repository = (CDORepository)element;
          LifecycleUtil.activate(repository);
          itemProvider.superGetChildren(repository);
        }
      }

      public void treeCollapsed(TreeExpansionEvent event)
      {
        Object element = event.getElement();
        if (element instanceof CDORepository)
        {
          CDORepository repository = (CDORepository)element;
          LifecycleUtil.deactivate(repository);
        }
      }
    });
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(newAction);
    manager.add(getRefreshAction());
    super.fillLocalToolBar(manager);
  }

  /**
   * @author Eike Stepper
   */
  private class NewRepositoryAction extends Action
  {
    public NewRepositoryAction()
    {
      setText("New Repository");
      setToolTipText("Add a new repository");
      setImageDescriptor(OM.getImageDescriptor("icons/add.gif"));
    }

    @Override
    public void run()
    {
      try
      {
        NewRepositoryLocationDialog dialog = new NewRepositoryLocationDialog(getSite().getShell());
        if (dialog.open() == NewRepositoryLocationDialog.OK)
        {
          String connectorType = dialog.getConnectorType();
          String connectorDescription = dialog.getConnectorDescription();
          String repositoryName = dialog.getRepositoryName();

          CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
          repositoryManager.addRemoteRepository(repositoryName, repositoryName, connectorType, connectorDescription);
        }
      }
      catch (RuntimeException ex)
      {
        OM.LOG.error(ex);
        throw ex;
      }
    }
  }
}
