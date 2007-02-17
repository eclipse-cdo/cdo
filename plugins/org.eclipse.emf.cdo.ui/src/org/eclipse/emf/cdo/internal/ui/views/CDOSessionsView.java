package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOConstants;
import org.eclipse.emf.cdo.container.CDOContainerAdapter;

import org.eclipse.net4j.container.Container;
import org.eclipse.net4j.container.ContainerManager;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class CDOSessionsView extends ViewPart
{
  private static final Container CONTAINER = ContainerManager.INSTANCE.getContainer();

  private static final CDOContainerAdapter CDO_ADAPTER = (CDOContainerAdapter)CONTAINER.getAdapter(CDOConstants.TYPE);

  private static final ItemProvider ITEM_PROVIDER = new CDOSessionsItemProvider();

  private StructuredViewer viewer;

  // private DrillDownAdapter drillDownAdapter;

  private Action openSessionAction;

  private Action addConnectorAction;

  private Action doubleClickAction;

  public CDOSessionsView()
  {
  }

  public void createPartControl(Composite parent)
  {
    viewer = new TreeViewer(parent, (SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
    viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

    // drillDownAdapter = new DrillDownAdapter(viewer);
    viewer.setContentProvider(ITEM_PROVIDER);
    viewer.setLabelProvider(ITEM_PROVIDER);
    viewer.setSorter(new CDOSessionsNameSorter());
    viewer.setInput(CDO_ADAPTER);

    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
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
        CDOSessionsView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(getCurrentViewer().getControl());
    getCurrentViewer().getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, getCurrentViewer());
  }

  private StructuredViewer getCurrentViewer()
  {
    return viewer;
  }

  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(openSessionAction);
    manager.add(addConnectorAction);
    // manager.add(new Separator());
    // manager.add(action2);
  }

  private void fillContextMenu(IMenuManager manager)
  {
    // manager.add(action2);
    // manager.add(new Separator());
    // drillDownAdapter.addNavigationActions(manager);

    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(openSessionAction);
    manager.add(addConnectorAction);
    // manager.add(action2);
    // manager.add(new Separator());
    // drillDownAdapter.addNavigationActions(manager);
  }

  private void makeActions()
  {
    openSessionAction = new Action()
    {
      public void run()
      {
        InputDialog dialog = new InputDialog(getCurrentViewer().getControl().getShell(), "CDO Sessions",
            "Enter a session description:", null, null);
        if (dialog.open() == InputDialog.OK)
        {
          String description = dialog.getValue();
          Object object = CDO_ADAPTER.getSession(description);
          if (object == null)
          {
            showMessage("Error while creating session for description" + description);
          }
        }
      }
    };
    openSessionAction.setText("Open Session");
    openSessionAction.setToolTipText("Open a CDO session");
    openSessionAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
        ISharedImages.IMG_TOOL_NEW_WIZARD));

    addConnectorAction = new Action()
    {
      public void run()
      {
        InputDialog dialog = new InputDialog(getCurrentViewer().getControl().getShell(), "Net4j Explorer",
            "Enter a connector description:", null, null);
        if (dialog.open() == InputDialog.OK)
        {
          String description = dialog.getValue();
          Object object = CONTAINER.getConnector(description);
          if (object == null)
          {
            showMessage("Error while creating connector for description" + description);
          }
        }
      }
    };
    addConnectorAction.setText("Add Connector");
    addConnectorAction.setToolTipText("Add a connector");
    addConnectorAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
        ISharedImages.IMG_TOOL_NEW_WIZARD));

    doubleClickAction = new Action()
    {
      public void run()
      {
        ISelection selection = getCurrentViewer().getSelection();
        Object obj = ((IStructuredSelection)selection).getFirstElement();
        showMessage("Double-click detected on " + obj.toString());
      }
    };
  }

  private void hookDoubleClickAction()
  {
    getCurrentViewer().addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        doubleClickAction.run();
      }
    });
  }

  private void showMessage(String message)
  {
    MessageDialog.openInformation(getCurrentViewer().getControl().getShell(), "Net4j Explorer", message);
  }

  public void setFocus()
  {
    getCurrentViewer().getControl().setFocus();
  }
}