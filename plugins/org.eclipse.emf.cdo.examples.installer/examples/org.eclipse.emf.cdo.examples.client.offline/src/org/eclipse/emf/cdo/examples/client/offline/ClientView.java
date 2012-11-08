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
package org.eclipse.emf.cdo.examples.client.offline;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;

/**
 * @author Eike Stepper
 */
public class ClientView extends AbstractView<CDOSession>
{
  public static final String ID = "org.eclipse.emf.cdo.examples.client.offline.ClientView"; //$NON-NLS-1$

  private CDOItemProvider itemProvider;

  private TreeViewer treeViewer;

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
    treeViewer.setInput(session);

    hookDoubleClick();
    hookContextMenu();
  }

  protected void hookDoubleClick()
  {
    treeViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        ITreeSelection selection = (ITreeSelection)treeViewer.getSelection();
        Object object = selection.getFirstElement();
        if (object instanceof ContainerItemProvider.ErrorElement)
        {
          try
          {
            UIUtil.getActiveWorkbenchPage().showView(UIUtil.ERROR_LOG_ID);
          }
          catch (PartInitException ex)
          {
            ex.printStackTrace();
          }
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

  protected void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
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
}
