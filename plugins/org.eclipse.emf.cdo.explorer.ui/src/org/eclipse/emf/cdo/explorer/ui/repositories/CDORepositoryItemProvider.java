/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.IDGeneration;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository.VersioningMode;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager.RepositoryConnectionEvent;
import org.eclipse.emf.cdo.explorer.ui.ViewerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDORepositoryItemProvider extends ContainerItemProvider<IContainer<Object>> implements ITableLabelProvider
{
  public static final Image REPOSITORY_IMAGE = SharedIcons.getImage(SharedIcons.OBJ_REPO);

  public static final Image BRANCH_IMAGE = SharedIcons.getImage(SharedIcons.OBJ_BRANCH);

  private final Image imageRepoDisconnected = new Image(UIUtil.getDisplay(), REPOSITORY_IMAGE, SWT.IMAGE_GRAY);

  private final Map<CDORepository, CDORepository> connectingRepositories = new ConcurrentHashMap<>();

  private final IListener repositoryManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof RepositoryConnectionEvent)
      {
        RepositoryConnectionEvent e = (RepositoryConnectionEvent)event;

        StructuredViewer viewer = getViewer();
        CDORepository repository = e.getRepository();

        if (!e.isConnected())
        {
          Node node = getNode(repository);
          if (node != null)
          {
            node.disposeChildren();

            if (viewer instanceof TreeViewer)
            {
              ViewerUtil.expand((TreeViewer)viewer, repository, false);
            }
          }
        }

        ViewerUtil.refresh(viewer, repository);
        updatePropertySheetPage(repository);
      }
      else if (event instanceof CDOExplorerManager.ElementsChangedEvent)
      {
        CDOExplorerManager.ElementsChangedEvent e = (CDOExplorerManager.ElementsChangedEvent)event;

        StructuredViewer viewer = getViewer();
        Object[] changedElements = e.getChangedElements();

        ViewerUtil.update(viewer, changedElements);
        updatePropertySheetPage(changedElements);
      }
    }

    private void updatePropertySheetPage(final Object element)
    {
      if (repositoriesView != null)
      {
        getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            IStructuredSelection selection = (IStructuredSelection)repositoriesView.getSelection();
            for (Object object : selection.toArray())
            {
              if (object == element)
              {
                repositoriesView.refreshPropertySheetPage();
                return;
              }
            }
          }
        });
      }
    }
  };

  private final CDORepositoriesView repositoriesView;

  private boolean grayOutDisconnectedRepositories = true;

  public CDORepositoryItemProvider()
  {
    this(null);
    grayOutDisconnectedRepositories = false;
  }

  public CDORepositoryItemProvider(CDORepositoriesView repositoriesView)
  {
    this.repositoriesView = repositoriesView;
  }

  @Override
  public void dispose()
  {
    imageRepoDisconnected.dispose();
    super.dispose();
  }

  public void connectRepository(CDORepository repository)
  {
    // Mark this repository as connecting.
    connectingRepositories.put(repository, repository);

    StructuredViewer viewer = getViewer();
    ViewerUtil.refresh(viewer, repository); // Trigger hasChildren().

    if (viewer instanceof TreeViewer)
    {
      ViewerUtil.expand((TreeViewer)viewer, repository, true); // Trigger getChildren().
    }
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      if (!repository.isConnected())
      {
        if (connectingRepositories.containsKey(repository))
        {
          // This must be the ContainerItemProvider.LazyElement.
          return true;
        }

        return false;
      }
    }

    return super.hasChildren(element);
  }

  @Override
  public Object[] getChildren(Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      if (!repository.isConnected())
      {
        if (!connectingRepositories.containsKey(repository))
        {
          return ViewerUtil.NO_CHILDREN;
        }
      }
    }
    else if (element instanceof CDORepositoryManager)
    {
      List<CDORepository> repositories = new ArrayList<>();

      Object[] children = super.getChildren(element);
      for (Object child : children)
      {
        if (child instanceof CDORepository)
        {
          repositories.add((CDORepository)child);
        }
      }

      Collections.sort(repositories);
      return repositories.toArray();
    }

    List<CDOTimeProvider> timeProviders = new ArrayList<>();
    List<Object> otherObjects = new ArrayList<>();

    Object[] children = super.getChildren(element);
    for (Object child : children)
    {
      if (child instanceof CDOTimeProvider)
      {
        timeProviders.add((CDOTimeProvider)child);
      }
      else
      {
        otherObjects.add(child);
      }
    }

    Collections.sort(timeProviders, CDOCommonUtil.TIME_COMPARATOR);
    otherObjects.addAll(0, timeProviders);
    return otherObjects.toArray();
  }

  @Override
  protected Object[] getContainerChildren(AbstractContainerNode containerNode, IContainer<?> container)
  {
    if (container instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)container;
      if (connectingRepositories.remove(repository) != null)
      {
        try
        {
          repository.connect();
        }
        catch (final Exception ex)
        {
          OM.LOG.error(ex);
          containerNode.disposeChildren();
          repository.disconnect();

          UIUtil.getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              try
              {
                MessageDialog.openError(getViewer().getControl().getShell(), "Connection Error", ex.getMessage());
              }
              catch (Exception ex)
              {
                OM.LOG.error(ex);
              }
            }
          });
        }
      }
    }

    return super.getContainerChildren(containerNode, container);
  }

  @Override
  public String getText(Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      return repository.getLabel();
    }

    if (element instanceof CDOBranch)
    {
      CDOBranch branch = (CDOBranch)element;
      return branch.getName();
    }

    return super.getText(element);
  }

  @Override
  public String getColumnText(Object element, int columnIndex)
  {
    switch (columnIndex)
    {
    case 0:
      return getText(element);

    case 1:
      if (element instanceof CDORepository)
      {
        CDORepository repository = (CDORepository)element;
        VersioningMode versioningMode = repository.getVersioningMode();
        return versioningMode == null ? "Unknown" : versioningMode.toString();
      }

      break;

    case 2:
      if (element instanceof CDORepository)
      {
        CDORepository repository = (CDORepository)element;
        IDGeneration idGeneration = repository.getIDGeneration();
        return idGeneration == null ? "Unknown" : idGeneration.toString();
      }
    }

    return "";
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)obj;
      if (!repository.isConnected() && grayOutDisconnectedRepositories)
      {
        return imageRepoDisconnected;
      }

      return REPOSITORY_IMAGE;
    }

    if (obj instanceof CDOBranch)
    {
      return BRANCH_IMAGE;
    }

    return super.getImage(obj);
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex)
  {
    if (columnIndex == 0)
    {
      return getImage(element);
    }

    return null;
  }

  @Override
  protected boolean isSlow(IContainer<Object> container)
  {
    return true;
  }

  @Override
  protected String getSlowText(IContainer<Object> container)
  {
    if ((IContainer<?>)container instanceof CDORepository)
    {
      return "Connecting...";
    }

    return "Loading...";
  }

  @Override
  protected void connectInput(IContainer<Object> input)
  {
    super.connectInput(input);
    input.addListener(repositoryManagerListener);
  }

  @Override
  protected void disconnectInput(IContainer<Object> input)
  {
    input.removeListener(repositoryManagerListener);
    super.disconnectInput(input);
  }
}
