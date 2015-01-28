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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.explorer.CDORepositoryManager.RepositoryConnectionEvent;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class CDORepositoryItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  private static final Image IMAGE_REPO = SharedIcons.getImage(SharedIcons.OBJ_REPO);

  private static final Image IMAGE_BRANCH = SharedIcons.getImage(SharedIcons.OBJ_BRANCH);

  private static final Color COLOR_DRAK_GRAY = UIUtil.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

  private final Image imageRepoDisconnected = new Image(UIUtil.getDisplay(), IMAGE_REPO, SWT.IMAGE_GRAY);

  private final IListener repositoryManagerListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof RepositoryConnectionEvent)
      {
        RepositoryConnectionEvent e = (RepositoryConnectionEvent)event;
        CDORepository repository = e.getRepository();
        TreeViewer viewer = getViewer();

        if (!e.isConnected())
        {
          ViewerUtil.expand(viewer, repository, false);
        }

        ViewerUtil.refresh(viewer, repository);

        // if (e.isConnected())
        // {
        // ViewerUtil.setExpandedState(viewer, repository, true);
        // }
      }
    }
  };

  public CDORepositoryItemProvider()
  {
  }

  @Override
  public void dispose()
  {
    imageRepoDisconnected.dispose();
    super.dispose();
  }

  @Override
  public TreeViewer getViewer()
  {
    return (TreeViewer)super.getViewer();
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      if (!repository.isConnected())
      {
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
        return ViewerUtil.NO_CHILDREN;
      }
    }

    return super.getChildren(element);
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
  public Image getImage(Object obj)
  {
    if (obj instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)obj;
      if (!repository.isConnected())
      {
        return imageRepoDisconnected;
      }

      return IMAGE_REPO;
    }

    if (obj instanceof CDOBranch)
    {
      return IMAGE_BRANCH;
    }

    return super.getImage(obj);
  }

  @Override
  public Color getForeground(Object obj)
  {
    if (obj instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)obj;
      if (!repository.isConnected())
      {
        return COLOR_DRAK_GRAY;
      }
    }

    return super.getForeground(obj);
  }

  @Override
  public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);
    if (selection.size() == 1)
    {
      Object obj = selection.getFirstElement();
      if (obj instanceof CDORepository)
      {
        manager.add(new DisconnectAction((CDORepository)obj));
      }
    }
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

  // @Override
  // protected void handleInactiveElement(Iterator<org.eclipse.net4j.util.ui.views.ContainerItemProvider.Node> it,
  // org.eclipse.net4j.util.ui.views.ContainerItemProvider.Node child)
  // {
  // // Do nothing.
  // }

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

  /**
   * @author Eike Stepper
   */
  public static class DisconnectAction extends LongRunningAction
  {
    private CDORepository repository;

    public DisconnectAction(CDORepository repository)
    {
      super("Disconnect");
      this.repository = repository;
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      repository.disconnect();
    }
  }
}
