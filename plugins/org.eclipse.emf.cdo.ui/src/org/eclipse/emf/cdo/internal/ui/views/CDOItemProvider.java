/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.LabelUtil;
import org.eclipse.emf.cdo.internal.ui.ResourceHistory;
import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory.Entry;

import org.eclipse.net4j.ui.actions.LongRunningAction;
import org.eclipse.net4j.ui.views.ContainerItemProvider;
import org.eclipse.net4j.ui.views.IElementFilter;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOItemProvider extends ContainerItemProvider
{
  private IWorkbenchPage page;

  private Map<CDOView, CDOViewHistory> viewHistories = new HashMap();

  private IListener historyListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      CDOViewHistory history = (CDOViewHistory)event.getSource();
      CDOView view = history.getView();
      refreshElement(view, false);
    }
  };

  public CDOItemProvider(IWorkbenchPage page, IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
    this.page = page;
  }

  public CDOItemProvider(IWorkbenchPage page)
  {
    this(page, null);
  }

  @Override
  public Object[] getChildren(Object element)
  {
    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      CDOViewHistory history = viewHistories.get(view);
      if (history != null)
      {
        return history.getEntries();
      }

      return NO_CHILDREN;
    }

    return super.getChildren(element);
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      CDOViewHistory history = viewHistories.get(view);
      if (history != null)
      {
        return history.hasEntries();
      }

      return false;
    }

    return super.hasChildren(element);
  }

  @Override
  public Object getParent(Object element)
  {
    if (element instanceof CDOViewHistory.Entry)
    {
      return ((CDOViewHistory.Entry)element).getView();
    }

    return super.getParent(element);
  }

  @Override
  public String getText(Object obj)
  {
    String text = LabelUtil.getText(obj);
    if (text == null)
    {
      text = super.getText(obj);
    }

    return text;
  }

  @Override
  public Image getImage(Object obj)
  {
    Image image = LabelUtil.getImage(obj);
    if (image == null)
    {
      image = super.getImage(obj);
    }

    return image;
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);
    if (selection.size() == 1)
    {
      Object object = selection.getFirstElement();
      if (object instanceof CDOSession)
      {
        fillSession(manager, (CDOSession)object);
      }
      else if (object instanceof CDOView)
      {
        fillView(manager, (CDOView)object);
      }
      else if (object instanceof CDOViewHistory.Entry)
      {
        fillHistoryEntry(manager, (CDOViewHistory.Entry)object);
      }
    }
  }

  protected void fillSession(IMenuManager manager, CDOSession session)
  {
    manager.add(new OpenViewAction("Open Transaction", "Open a CDO transaction", session)
    {
      @Override
      protected CDOView createView()
      {
        return getSession().openView(new ResourceSetImpl());
      }
    });

    manager.add(new OpenViewAction("Open View", "Open a read-only CDO view", session)
    {
      @Override
      protected CDOView createView()
      {
        return getSession().openView(new ResourceSetImpl(), true);
      }
    });

    manager.add(new OpenViewAction("Open Historical View", "Open a historical CDO view", session)
    {
      @Override
      protected CDOView createView()
      {
        return getSession().openView(new ResourceSetImpl(), System.currentTimeMillis());
      }
    });
  }

  protected void fillView(IMenuManager manager, final CDOView view)
  {
    if (view.isReadWrite())
    {
      manager.add(new LongRunningAction(page, "Create Resource", "Create a CDO resource", null)
      {
        private String resourcePath;

        @Override
        protected void preRun(IWorkbenchPage page) throws Exception
        {
          int number = (int)(Math.random() * 10000000.0);
          InputDialog dialog = new InputDialog(page.getWorkbenchWindow().getShell(), "Create Resource",
              "Enter resource path:", "/res" + number, null);
          if (dialog.open() == InputDialog.OK)
          {
            resourcePath = dialog.getValue();
          }
          else
          {
            cancel();
          }
        }

        @Override
        protected void doRun(final IWorkbenchPage page, IProgressMonitor monitor) throws Exception
        {
          view.createResource(resourcePath);
          CDOEditor.open(page, view, resourcePath);
        }
      });
    }

    manager.add(new LongRunningAction(page, "Open", "Open a CDO editor", null)
    {
      @Override
      protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
      {
        CDOEditor.open(page, view, null);
      }
    });
  }

  protected void fillHistoryEntry(IMenuManager manager, Entry entry)
  {
  }

  // @Override
  // protected Display getDisplay()
  // {
  // Display display = getViewer().getControl().getDisplay();
  // if (display == null)
  // {
  // display = Display.getCurrent();
  // }
  //
  // if (display == null)
  // {
  // display = Display.getDefault();
  // }
  //
  // if (display == null)
  // {
  // throw new IllegalStateException("display == null");
  // }
  //
  // return display;
  // }

  @Override
  protected void elementAdded(Object element, Object parent)
  {
    super.elementAdded(element, parent);
    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      CDOViewHistory history = new CDOViewHistory(view);
      history.addListener(historyListener);
      viewHistories.put(view, history);
    }
  }

  @Override
  protected void elementRemoved(Object element, Object parent)
  {
    super.elementRemoved(element, parent);
    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      CDOViewHistory history = viewHistories.remove(view);
      history.removeListener(historyListener);
      history.dispose();
    }
  }

  protected Object[] getResources(CDOView view)
  {
    return ResourceHistory.INSTANCE.getEntries(view);
  }

  public static ImageDescriptor getOpenEditorImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR);
  }

  /**
   * @author Eike Stepper
   */
  private abstract class OpenViewAction extends LongRunningAction
  {
    private CDOSession session;

    public OpenViewAction(String text, String toolTipText, CDOSession session)
    {
      super(page, text, toolTipText, getOpenEditorImageDescriptor());
      this.session = session;
    }

    public CDOSession getSession()
    {
      return session;
    }

    @Override
    protected void doRun(final IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      // final Exception[] exception = new Exception[1];
      // final CDOView view =
      createView();
      // final IWorkbenchPart part = page.getActivePart();
      // getDisplay().asyncExec(new Runnable()
      // {
      // public void run()
      // {
      // try
      // {
      // if (part instanceof ISetSelectionTarget)
      // {
      // ((ISetSelectionTarget)part).selectReveal(new
      // StructuredSelection(view));
      // }
      //
      // CDOEditor.open(page, view, "/res/test");
      // }
      // catch (Exception ex)
      // {
      // exception[0] = ex;
      // }
      // }
      // });
      //
      // if (exception[0] != null)
      // {
      // throw exception[0];
      // }
    }

    protected abstract CDOView createView();
  }
}
