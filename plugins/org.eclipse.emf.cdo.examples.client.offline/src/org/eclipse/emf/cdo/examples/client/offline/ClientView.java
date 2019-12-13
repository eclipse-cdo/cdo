/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.ExampleResourceManager;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Eike Stepper
 */
public class ClientView extends AbstractView<CDOSession>
{
  public static final String ID = "org.eclipse.emf.cdo.examples.client.offline.ClientView"; //$NON-NLS-1$

  private CDOItemProvider itemProvider;

  private TreeViewer treeViewer;

  private CommitAction commitAction = new CommitAction();

  private MergeAction mergeAction = new MergeAction();

  public ClientView()
  {
    super(CDOSession.class);
  }

  @Override
  protected void createPane(Composite parent, CDOSession session)
  {
    itemProvider = new CDOItemProvider(getSite().getPage())
    {
      @Override
      protected void handleElementEvent(final IEvent event)
      {
        addEvent(event);
      }
    };

    treeViewer = new TreeViewer(parent, SWT.BORDER);
    treeViewer.setLabelProvider(itemProvider);
    treeViewer.setContentProvider(itemProvider);
    treeViewer.setComparator(itemProvider);
    treeViewer.setInput(session);

    hookDoubleClick();
    hookContextMenu();
    updateEnablement();

    IRepository repository = Application.NODE.getObject(IRepository.class);
    repository.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        updateEnablement();
      }
    });

    final IListener transactionListener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        updateEnablement();
      }
    };

    CDOTransaction transaction = Application.NODE.getObject(CDOTransaction.class);
    if (transaction != null)
    {
      transaction.addListener(transactionListener);
    }
    else
    {
      session.addListener(new ContainerEventAdapter<CDOView>()
      {
        @Override
        protected void onAdded(IContainer<CDOView> container, CDOView view)
        {
          updateEnablement();
          view.addListener(transactionListener);
        }
      });
    }
  }

  protected void hookDoubleClick()
  {
    treeViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        ITreeSelection selection = (ITreeSelection)treeViewer.getSelection();
        Object object = selection.getFirstElement();
        if (object instanceof CDOResourceLeaf)
        {
          CDOResourceLeaf resource = (CDOResourceLeaf)object;
          CDOEditorUtil.openEditor(getSite().getPage(), resource);
        }
        else if (object != null && treeViewer.isExpandable(object))
        {
          if (treeViewer.getExpandedState(object))
          {
            treeViewer.collapseToLevel(object, TreeViewer.ALL_LEVELS);
          }
          else
          {
            treeViewer.expandToLevel(object, 1);
          }
        }
      }
    });
  }

  @Override
  protected void initializeToolBar(IToolBarManager toolbarManager)
  {
    super.initializeToolBar(toolbarManager);
    toolbarManager.add(commitAction);
    toolbarManager.add(mergeAction);
  }

  protected void updateEnablement()
  {
    CDOTransaction transaction = Application.NODE.getObject(CDOTransaction.class);
    commitAction.setEnabled(transaction != null && transaction.isDirty());
    mergeAction
        .setEnabled(transaction != null && transaction.getBranch().isLocal() && Application.NODE.getObject(IRepository.class).getState() == State.ONLINE);
  }

  protected void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        ITreeSelection selection = (ITreeSelection)treeViewer.getSelection();
        fillContextMenu(manager, selection);
      }
    });

    Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
    treeViewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, treeViewer);
  }

  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    itemProvider.fillContextMenu(manager, selection);
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  @Override
  public void setFocus()
  {
    treeViewer.getTree().setFocus();
  }

  @Override
  public void dispose()
  {
    itemProvider.dispose();
    super.dispose();
  }

  /**
   * @author Eike Stepper
   */
  public static class CommitAction extends Action
  {
    public CommitAction()
    {
      super("Commit", ExampleResourceManager.getPluginImageDescriptor(Application.PLUGIN_ID, "icons/Commit.gif"));
    }

    @Override
    public void run()
    {
      try
      {
        PlatformUI.getWorkbench().getProgressService().run(true, true, new IRunnableWithProgress()
        {
          @Override
          public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
          {
            CDOTransaction transaction = Application.NODE.getObject(CDOTransaction.class);

            try
            {
              transaction.commit(monitor);
            }
            catch (CommitException ex)
            {
              ex.printStackTrace();
              transaction.rollback();
            }
          }
        });
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class MergeAction extends Action
  {
    public MergeAction()
    {
      super("Merge", ExampleResourceManager.getPluginImageDescriptor(Application.PLUGIN_ID, "icons/Merge.gif"));
    }

    @Override
    public void run()
    {
      CDOTransaction transaction = Application.NODE.getObject(CDOTransaction.class);

      CDOBranch offlineBranch = transaction.getBranch();
      CDOBranch baseBranch = offlineBranch.getBase().getBranch();

      transaction.setBranch(baseBranch);
      transaction.merge(offlineBranch.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    }
  }
}
