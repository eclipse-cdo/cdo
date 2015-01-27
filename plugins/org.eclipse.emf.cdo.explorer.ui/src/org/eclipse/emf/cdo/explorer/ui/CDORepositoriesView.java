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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.explorer.CDORepositoryManager;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.CDORepositoryManagerImpl;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * @author Eike Stepper
 */
public class CDORepositoriesView extends ContainerView
{
  private static final int MINUTE = 60 * 1000;

  private final ActivityDetector activityDetector = new ActivityDetector();

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
    itemProvider = new CDORepositoryItemProvider();
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

    TreeViewer viewer = getViewer();
    viewer.addTreeListener(activityDetector);

    Tree tree = viewer.getTree();
    tree.addMouseListener(activityDetector);
    tree.addKeyListener(activityDetector);

    tree.getDisplay().timerExec(MINUTE, activityDetector);
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

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);

    if (selection.size() == 1)
    {
      Object element = selection.getFirstElement();
      manager.add(new ConnectAction(element));
    }
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (object instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)object;
      if (!repository.isConnected())
      {
        repository.connect();
        return;
      }
    }

    super.doubleClicked(object);
  }

  /**
   * @author Eike Stepper
   */
  private final class ActivityDetector implements ITreeViewerListener, MouseListener, KeyListener, Runnable
  {
    private long lastActivity;

    public ActivityDetector()
    {
      detect();
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
      detect();
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
      detect();
    }

    public void mouseDoubleClick(MouseEvent e)
    {
      detect();
    }

    public void mouseDown(MouseEvent e)
    {
      detect();
    }

    public void mouseUp(MouseEvent e)
    {
      detect();
    }

    public void keyPressed(KeyEvent e)
    {
      detect();
    }

    public void keyReleased(KeyEvent e)
    {
      detect();
    }

    private void detect()
    {
      lastActivity = System.currentTimeMillis();
    }

    public void run()
    {
      Tree tree = getViewer().getTree();
      if (tree.isDisposed())
      {
        return;
      }

      long now = System.currentTimeMillis();
      int wait = MINUTE;

      if (lastActivity <= now - MINUTE)
      {
        CDORepositoryManagerImpl repositoryManager = (CDORepositoryManagerImpl)getContainer();
        repositoryManager.disconnectUnusedRepositories();
      }
      else
      {
        wait = (int)(MINUTE - (now - lastActivity));
      }

      tree.getDisplay().timerExec(wait, this);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class NewRepositoryAction extends Action
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

  /**
   * @author Eike Stepper
   */
  private final class ConnectAction extends Action
  {
    private final Object element;

    public ConnectAction(Object element)
    {
      this.element = element;

      setText("Connect...");
      setToolTipText("Create an online checkout");
      setImageDescriptor(OM.getImageDescriptor("icons/add.gif"));
    }

    @Override
    public void run()
    {
      try
      {
        if (element instanceof CDORepository)
        {
          CDORepository repository = (CDORepository)element;
          CDOID rootID = repository.getSession().getRepositoryInfo().getRootResourceID();

          CDOCheckoutManager checkoutManager = CDOExplorerUtil.getCheckoutManager();
          checkoutManager.connect(repository.getLabel(), repository, CDOBranch.MAIN_BRANCH_NAME,
              CDOBranchPoint.UNSPECIFIED_DATE, false, rootID);
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
