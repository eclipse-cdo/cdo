package org.eclipse.net4j.internal.ui.views;

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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class Net4jExplorerView extends ViewPart
{
  private static final String[] TAB_LABELS = { "Connectors", "Acceptors", "Factories", "Adapters" };

  private static final boolean[] WITH_TREE = { true, true, true, false };

  private static final ItemProvider[] ITEM_PROVIDERS = { new ConnectorsItemProvider(), new AcceptorsItemProvider(),
      new FactoriesItemProvider(), new AdaptersItemProvider() };

  private TabFolder tabFolder;

  private Control[] tabControls = new Control[TAB_LABELS.length];

  private StructuredViewer[] viewers = new StructuredViewer[TAB_LABELS.length];

  // private DrillDownAdapter drillDownAdapter;

  private Action addConnectorAction;

  private Action addAcceptorAction;

  private Action doubleClickAction;

  public Net4jExplorerView()
  {
  }

  public void createPartControl(Composite parent)
  {
    tabFolder = new TabFolder(parent, SWT.NULL);
    for (int i = 0; i < TAB_LABELS.length; i++)
    {
      tabControls[0] = createTabControl(tabFolder, i, TAB_LABELS[i]);
    }

    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
  }

  private Control createTabControl(TabFolder parent, int index, String label)
  {
    viewers[index] = createViewer(parent, index);
    Control control = viewers[index].getControl();
    control.setLayoutData(new GridData(GridData.FILL_BOTH));

    final TabItem factoryTab = new TabItem(tabFolder, SWT.NULL);
    factoryTab.setText(label);
    factoryTab.setControl(control);
    return control;
  }

  private StructuredViewer createViewer(Composite parent, int index)
  {
    int style = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
    StructuredViewer viewer = WITH_TREE[index] ? new TreeViewer(parent, style) : new TableViewer(parent, style);

    // drillDownAdapter = new DrillDownAdapter(viewer);
    viewer.setContentProvider(ITEM_PROVIDERS[index]);
    viewer.setLabelProvider(ITEM_PROVIDERS[index]);
    viewer.setSorter(new Net4jExplorerNameSorter());
    viewer.setInput(ContainerManager.INSTANCE.getContainer());
    return viewer;
  }

  private void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        Net4jExplorerView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(getCurrentViewer().getControl());
    getCurrentViewer().getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, getCurrentViewer());
  }

  private StructuredViewer getCurrentViewer()
  {
    int index = tabFolder.getSelectionIndex();
    return viewers[index];
  }

  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(addConnectorAction);
    manager.add(addAcceptorAction);
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
    manager.add(addConnectorAction);
    manager.add(addAcceptorAction);
    // manager.add(action2);
    // manager.add(new Separator());
    // drillDownAdapter.addNavigationActions(manager);
  }

  private void makeActions()
  {
    addConnectorAction = new Action()
    {
      public void run()
      {
        InputDialog dialog = new InputDialog(getCurrentViewer().getControl().getShell(), "Net4j Explorer",
            "Enter a connector description:", null, null);
        if (dialog.open() == InputDialog.OK)
        {
          String description = dialog.getValue();
          Object object = ContainerManager.INSTANCE.getContainer().getConnector(description);
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

    addAcceptorAction = new Action()
    {
      public void run()
      {
        InputDialog dialog = new InputDialog(getCurrentViewer().getControl().getShell(), "Net4j Explorer",
            "Enter an acceptor description:", null, null);
        if (dialog.open() == InputDialog.OK)
        {
          String description = dialog.getValue();
          Object object = ContainerManager.INSTANCE.getContainer().getAcceptor(description);
          if (object == null)
          {
            showMessage("Error while creating acceptor for description" + description);
          }
        }
      }
    };
    addAcceptorAction.setText("Add Acceptor");
    addAcceptorAction.setToolTipText("Add an acceptor");
    addAcceptorAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
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