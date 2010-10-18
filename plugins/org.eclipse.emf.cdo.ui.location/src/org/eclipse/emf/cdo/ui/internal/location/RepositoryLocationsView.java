/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.location;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RepositoryLocationsView extends ViewPart
{
  public static final String ID = "org.eclipse.emf.cdo.ui.location.RepositoryLocationsView";

  private TreeViewer viewer;

  private DrillDownAdapter drillDownAdapter;

  private Action newAction;

  private Action refreshAction;

  private Action doubleClickAction;

  public RepositoryLocationsView()
  {
  }

  @Override
  public void createPartControl(Composite parent)
  {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.setInput(getViewSite());

    drillDownAdapter = new DrillDownAdapter(viewer);

    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
  }

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  private void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        RepositoryLocationsView.this.fillContextMenu(manager);
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
    manager.add(newAction);
    manager.add(new Separator());
    manager.add(refreshAction);
  }

  private void fillContextMenu(IMenuManager manager)
  {
    manager.add(newAction);
    manager.add(refreshAction);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(newAction);
    manager.add(refreshAction);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
  }

  private void makeActions()
  {
    newAction = new Action()
    {
      @Override
      public void run()
      {
        showMessage("Action 1 executed");
      }
    };

    newAction.setText("Action 1");
    newAction.setToolTipText("Action 1 tooltip");
    newAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

    refreshAction = new Action()
    {
      @Override
      public void run()
      {
        showMessage("Action 2 executed");
      }
    };

    refreshAction.setText("Action 2");
    refreshAction.setToolTipText("Action 2 tooltip");
    refreshAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

    doubleClickAction = new Action()
    {
      @Override
      public void run()
      {
        ISelection selection = viewer.getSelection();
        Object obj = ((IStructuredSelection)selection).getFirstElement();
        showMessage("Double-click detected on " + obj.toString());
      }
    };
  }

  private void hookDoubleClickAction()
  {
    viewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        doubleClickAction.run();
      }
    });
  }

  private void showMessage(String message)
  {
    MessageDialog.openInformation(viewer.getControl().getShell(), "CDO Repositories", message);
  }

  /**
   * @author Eike Stepper
   */
  public static class TreeObject extends PlatformObject
  {
    private String name;

    private TreeParent parent;

    public TreeObject(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public void setParent(TreeParent parent)
    {
      this.parent = parent;
    }

    public TreeParent getParent()
    {
      return parent;
    }

    @Override
    public String toString()
    {
      return getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TreeParent extends TreeObject
  {
    private List<TreeObject> children = new ArrayList<TreeObject>();

    public TreeParent(String name)
    {
      super(name);
    }

    public void addChild(TreeObject child)
    {
      children.add(child);
      child.setParent(this);
    }

    public void removeChild(TreeObject child)
    {
      children.remove(child);
      child.setParent(null);
    }

    public TreeObject[] getChildren()
    {
      return children.toArray(new TreeObject[children.size()]);
    }

    public boolean hasChildren()
    {
      return children.size() > 0;
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ViewContentProvider implements ITreeContentProvider
  {
    private TreeParent invisibleRoot;

    public ViewContentProvider()
    {
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
      invisibleRoot = (TreeParent)newInput;
    }

    public void dispose()
    {
      invisibleRoot = null;
    }

    public Object[] getElements(Object parent)
    {
      return getChildren(parent);
    }

    public Object getParent(Object child)
    {
      if (child == invisibleRoot)
      {
        return null;
      }

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
  }

  /**
   * @author Eike Stepper
   */
  public class ViewLabelProvider extends LabelProvider
  {
    public ViewLabelProvider()
    {
    }

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
}
