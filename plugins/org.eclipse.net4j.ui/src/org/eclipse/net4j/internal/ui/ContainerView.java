package org.eclipse.net4j.internal.ui;

import org.eclipse.net4j.internal.ui.bundle.SharedIcons;
import org.eclipse.net4j.transport.ITransportContainer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

public abstract class ContainerView extends ViewPart
{
  private ContainerItemProvider itemProvider;

  private TreeViewer viewer;

  private Action doubleClickAction = new Action()
  {
    public void run()
    {
      ISelection selection = viewer.getSelection();
      Object obj = ((IStructuredSelection)selection).getFirstElement();
      showMessage("Double-click detected on " + obj.toString());
    }
  };

  public ContainerView()
  {
  }

  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  public void createPartControl(Composite parent)
  {
    itemProvider = createContainerItemProvider();
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(itemProvider);
    viewer.setLabelProvider(itemProvider);
    viewer.setSorter(new ContainerNameSorter());
    viewer.setInput(getContainer());

    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
  }

  protected ContainerItemProvider createContainerItemProvider()
  {
    return new ContainerItemProvider(getRootElementFilter())
    {
      @Override
      public Image getImage(Object obj)
      {
        Image image = getElementImage(obj);
        if (image == null)
        {
          image = super.getImage(obj);
        }

        return image;
      }

      @Override
      public String getText(Object obj)
      {
        String text = getElementText(obj);
        if (text == null)
        {
          text = super.getText(obj);
        }

        return text;
      }
    };
  }

  protected String getElementText(Object element)
  {
    return null;
  }

  protected Image getElementImage(Object element)
  {
    return null;
  }

  protected IElementFilter getRootElementFilter()
  {
    return null;
  }

  protected abstract ITransportContainer getContainer();

  protected void hookContextMenu()
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager)
      {
        ContainerView.this.fillContextMenu(manager);
      }
    });

    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  protected void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected void fillLocalPullDown(IMenuManager manager)
  {
  }

  protected void fillContextMenu(IMenuManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  protected void fillLocalToolBar(IToolBarManager manager)
  {
  }

  protected void hookDoubleClickAction()
  {
    viewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        doubleClickAction.run();
      }
    });
  }

  protected void showMessage(String message)
  {
    MessageDialog.openInformation(viewer.getControl().getShell(), getTitle(), message);
  }

  public static ImageDescriptor getAddImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_ADD);
  }

  public static ImageDescriptor getDeleteImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_DELETE);
  }
}