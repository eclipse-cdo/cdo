/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.UIUtil;

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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class MultiViewersView extends ViewPart implements ISetSelectionTarget
{
  private Shell shell;

  private StructuredViewer currentViewer;

  public MultiViewersView()
  {
  }

  public Shell getShell()
  {
    return shell;
  }

  public StructuredViewer getCurrentViewer()
  {
    return currentViewer;
  }

  public void setCurrentViewer(StructuredViewer viewer)
  {
    currentViewer = viewer;
    getSite().setSelectionProvider(currentViewer);
    hookContextMenu(currentViewer);
  }

  @Override
  public void setFocus()
  {
    StructuredViewer viewer = getCurrentViewer();
    if (viewer != null)
    {
      viewer.getControl().setFocus();
    }
  }

  public void refreshViewer(boolean updateLabels)
  {
    refreshElement(null, updateLabels);
  }

  public void refreshElement(final Object element, final boolean updateLabels)
  {
    try
    {
      final StructuredViewer viewer = getCurrentViewer();
      if (viewer != null)
      {
        getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              if (element != null)
              {
                viewer.refresh(element, updateLabels);
              }
              else
              {
                viewer.refresh(updateLabels);
              }
            }
            catch (RuntimeException ignore)
            {
            }
          }
        });
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public void updateLabels(final Object element)
  {
    try
    {
      final StructuredViewer viewer = getCurrentViewer();
      if (viewer != null)
      {
        getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              viewer.update(element, null);
            }
            catch (RuntimeException ignore)
            {
            }
          }
        });
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public void revealElement(final Object element)
  {
    try
    {
      final StructuredViewer viewer = getCurrentViewer();
      if (viewer != null)
      {
        getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              viewer.reveal(element);
            }
            catch (RuntimeException ignore)
            {
            }
          }
        });
      }
    }
    catch (RuntimeException ignore)
    {
    }
  }

  @Override
  public void selectReveal(ISelection selection)
  {
    StructuredViewer viewer = getCurrentViewer();
    if (viewer != null)
    {
      viewer.setSelection(selection, true);
    }
  }

  public void closeView()
  {
    try
    {
      getSite().getShell().getDisplay().syncExec(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            getSite().getPage().hideView(MultiViewersView.this);
            MultiViewersView.this.dispose();
          }
          catch (Exception ignore)
          {
          }
        }
      });
    }
    catch (Exception ignore)
    {
    }
  }

  @Override
  public final void createPartControl(Composite parent)
  {
    try
    {
      shell = parent.getShell();
      Composite composite = UIUtil.createGridComposite(parent, 1);

      Control control = createUI(composite);
      control.setLayoutData(UIUtil.createGridData());

      hookDoubleClick();
      contributeToActionBars();
    }
    catch (Error ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
  }

  protected abstract Control createUI(Composite parent);

  protected void doubleClicked(Object object)
  {
  }

  protected void fillContextMenu(IMenuManager manager, StructuredViewer viewer, IStructuredSelection selection)
  {
  }

  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  protected final void showMessage(String message)
  {
    showMessage(MessageType.INFORMATION, message);
  }

  protected final boolean showMessage(MessageType type, String message)
  {
    switch (type)
    {
    case INFORMATION:
      MessageDialog.openInformation(getShell(), getTitle(), message);
      return true;

    case ERROR:
      MessageDialog.openError(getShell(), getTitle(), message);
      return true;

    case WARNING:
      MessageDialog.openWarning(getShell(), getTitle(), message);
      return true;

    case CONFIRM:
      return MessageDialog.openConfirm(getShell(), getTitle(), message);

    case QUESTION:
      return MessageDialog.openQuestion(getShell(), getTitle(), message);

    default:
      return true;
    }
  }

  protected final Display getDisplay()
  {
    Display display = null;
    final StructuredViewer viewer = getCurrentViewer();
    if (viewer != null)
    {
      display = viewer.getControl().getDisplay();
    }

    if (display == null)
    {
      display = UIUtil.getDisplay();
    }

    return display;
  }

  private void hookDoubleClick()
  {
    final StructuredViewer viewer = getCurrentViewer();
    if (viewer != null)
    {
      viewer.addDoubleClickListener(new IDoubleClickListener()
      {
        @Override
        public void doubleClick(DoubleClickEvent event)
        {
          IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
          Object object = selection.getFirstElement();
          doubleClicked(object);
        }
      });
    }
  }

  private void hookContextMenu(final StructuredViewer viewer)
  {
    MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      @Override
      public void menuAboutToShow(IMenuManager manager)
      {
        final StructuredViewer viewer = getCurrentViewer();
        if (viewer != null)
        {
          IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
          fillContextMenu(manager, viewer, selection);
        }
      }
    });

    Control control = viewer.getControl();
    Menu menu = menuMgr.createContextMenu(control);
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }

  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  protected static enum MessageType
  {
    INFORMATION, ERROR, WARNING, CONFIRM, QUESTION
  }
}
