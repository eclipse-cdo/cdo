package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

public abstract class StructuredView extends ViewPart
{
  public StructuredView()
  {
  }

  public void setFocus()
  {
    StructuredViewer viewer = getCurrentViewer();
    if (viewer != null)
    {
      viewer.getControl().setFocus();
    }
  }

  public final void createPartControl(Composite parent)
  {
    doCreatePartControl(parent);
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
  }

  protected abstract StructuredViewer getCurrentViewer();

  protected abstract void doCreatePartControl(Composite parent);

  protected void fillLocalPullDown(IMenuManager manager)
  {
  }

  protected void fillLocalToolBar(IToolBarManager manager)
  {
  }

  protected void fillContextMenu(IMenuManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  protected void onDoubleClick(Object selectedElement)
  {
    StructuredViewer viewer = getCurrentViewer();
    if (viewer instanceof TreeViewer)
    {
      TreeViewer treeViewer = (TreeViewer)viewer;
      treeViewer.setExpandedState(selectedElement, !treeViewer.getExpandedState(selectedElement));
    }
  }

  protected void addContribution(IContributionManager manager, IContributionItem item)
  {
    if (manager != null && item != null)
    {
      manager.add(item);
    }
  }

  protected void addContribution(IContributionManager manager, IAction action)
  {
    if (manager != null && action != null)
    {
      manager.add(action);
    }
  }

  protected void showMessage(String message)
  {
    MessageDialog.openInformation(getShell(), getTitle(), message);
  }

  protected String showInputDialog(String message)
  {
    return showInputDialog(message, null);
  }

  protected String showInputDialog(String message, String defaultValue)
  {
    if (!message.endsWith(":"))
    {
      message += ":";
    }

    InputDialog dialog = new InputDialog(getShell(), getTitle(), message, defaultValue, null);
    if (dialog.open() == InputDialog.OK)
    {
      return dialog.getValue();
    }

    return null;
  }

  protected Shell getShell()
  {
    return getCurrentViewer().getControl().getShell();
  }

  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        StructuredView.this.fillContextMenu(manager);
      }
    });

    Menu menu = menuMgr.createContextMenu(getCurrentViewer().getControl());
    getCurrentViewer().getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, getCurrentViewer());
  }

  private void hookDoubleClickAction()
  {
    getCurrentViewer().addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        onDoubleClick(((IStructuredSelection)event.getSelection()).getFirstElement());
      }
    });
  }
}