/*******************************************************************************
 * Copyright (c) 2009 - 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Martin Fluegge
 */
public class DawnConflictView extends ViewPart implements Observer
{
  private TreeViewer viewer;

  private DrillDownAdapter drillDownAdapter;

  private Action action1;

  class ViewContentProvider implements ITreeContentProvider
  {
    private TreeParent invisibleRoot;

    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
      initialize();
    }

    public void dispose()
    {
    }

    public Object[] getElements(Object parent)
    {
      if (parent.equals(getViewSite()))
      {
        if (invisibleRoot == null)
        {
          initialize();
        }
        return getChildren(invisibleRoot);
      }
      return getChildren(parent);
    }

    public Object getParent(Object child)
    {
      if (child instanceof TreeObject)
      {
        return ((TreeObject)child).getParent();
      }
      return null;
    }

    public Object[] getChildren(Object parent)
    {
      if (parent instanceof TreeParent)
      {
        return ((TreeParent)parent).getChildren();
      }
      return new Object[0];
    }

    public boolean hasChildren(Object parent)
    {
      if (parent instanceof TreeParent)
      {
        return ((TreeParent)parent).hasChildren();
      }
      return false;
    }

    /*
     * We will set up a dummy model to initialize tree heararchy. In a real code, you will connect to a real model and
     * expose its hierarchy.
     */
    private void initialize()
    {

      // IDawnDiagramEditor editor = (IDawnDiagramEditor)DawnEditorHelper.getActiveEditor();
      // Watcher watcher = editor.getWatcher();

      // TreeParent root = new TreeParent("Locally Deletion Conflict", null);
      // for (String key : watcher.getResourceSynchronizer().getDeletedLocallyConflicts().keySet())
      // {
      // ChangeObject changeObject = watcher.getResourceSynchronizer().getDeletedLocallyConflicts().get(key);
      // View view = (View) changeObject.getServerObject();
      // EObject element = view.getElement();
      // TreeObject to;
      // if(element !=null)
      // {
      // to = new TreeObject(view.eClass().getName() + "(" + element.getClass().getName() + ")", view);
      // }
      // else
      // {
      // to = new TreeObject(view.eClass().getName() + "(no element)", view);
      // }
      // root.addChild(to);
      // }
      //
      // TreeParent root2 = new TreeParent("Remotely Deletion Conflict", null);
      // for (String key : watcher.getResourceSynchronizer().getDeletedRemotelyConflicts().keySet())
      // {
      // ChangeObject changeObject = watcher.getResourceSynchronizer().getDeletedRemotelyConflicts().get(key);
      // View view = (View) changeObject.getLastObject();
      // EObject element = view.getElement();
      // TreeObject to = new TreeObject(view.eClass().getName() + "(" + element.getClass().getName() + ")", view);
      // root2.addChild(to);
      // }
      // TreeParent root3 = new TreeParent("Locally And Remotely Changed Conflict", null);
      //
      // for (String key : watcher.getResourceSynchronizer().getChangedLocalyAndRemotellyConflicts().keySet())
      // {
      // ChangeObject changeObject = watcher.getResourceSynchronizer().getChangedLocalyAndRemotellyConflicts().get(key);
      // View view = (View) changeObject.getLastObject();
      // EObject element = view.getElement();
      // TreeObject to = new TreeObject(view.eClass().getName() + "(" + element.getClass().getName() + ")", view);
      // root3.addChild(to);
      // }

      // invisibleRoot = new TreeParent("", null);
      // invisibleRoot.addChild(root);
      // invisibleRoot.addChild(root2);
      // invisibleRoot.addChild(root3);
    }
  }

  class ViewLabelProvider extends LabelProvider
  {

    @Override
    public String getText(Object obj)
    {
      return obj.toString();
    }

    @Override
    public Image getImage(Object obj)
    {
      String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
      if (obj instanceof TreeParent)
      {
        imageKey = ISharedImages.IMG_OBJ_FOLDER;
      }
      return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
    }
  }

  class NameSorter extends ViewerSorter
  {
  }

  /**
   * The constructor.
   */
  public DawnConflictView()
  {
    // IDawnDiagramEditor editor = (IDawnDiagramEditor)DawnEditorHelper.getActiveEditor();
    // Watcher watcher = editor.getWatcher();
    // watcher.getResourceSynchronizer().addObserver(this);
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(Composite parent)
  {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

    viewer.getTree().setHeaderVisible(true);
    viewer.getTree().setLinesVisible(true);
    TreeColumn col1 = new TreeColumn(viewer.getTree(), SWT.NONE);
    col1.setText("Conflict");
    col1.setWidth(200);
    TreeColumn col2 = new TreeColumn(viewer.getTree(), SWT.NONE);
    col2.setText("Description");
    col1.setWidth(500);

    drillDownAdapter = new DrillDownAdapter(viewer);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.setSorter(new NameSorter());
    viewer.setInput(getViewSite());

    // TODO generation
    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
        "de.mf.eclipse.plugins.dawn.diagram.communication.viewer");
    makeActions();
    hookContextMenu();
    // hookDoubleClickAction();
    contributeToActionBars();
  }

  private void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        DawnConflictView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(action1);
    manager.add(new Separator());
  }

  private void fillContextMenu(IMenuManager manager)
  {
    manager.add(action1);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(action1);
    // manager.add(action2);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
  }

  private void makeActions()
  {
    // action1 = new SolveConflictTreeViewAction(viewer);
    //
    // action1.setText("solve conflict");
    // action1.setToolTipText("solvesw the specific confilt for the object");
    // action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    //
    //
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  public void update(Observable o, Object arg)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        viewer.getContentProvider().inputChanged(null, null, null);
        viewer.refresh();

      }
    });
  }
}
