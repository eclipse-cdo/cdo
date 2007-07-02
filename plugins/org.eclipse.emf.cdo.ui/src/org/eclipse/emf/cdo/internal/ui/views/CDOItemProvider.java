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
import org.eclipse.emf.cdo.internal.ui.ResourceHistory;
import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.ui.actions.LongRunningAction;
import org.eclipse.net4j.ui.views.ContainerItemProvider;
import org.eclipse.net4j.ui.views.IElementFilter;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class CDOItemProvider extends ContainerItemProvider
{
  private IWorkbenchPage page;

  public CDOItemProvider(IWorkbenchPage page)
  {
    this(page, null);
  }

  public CDOItemProvider(IWorkbenchPage page, IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
    this.page = page;
  }

  @Override
  public Object[] getChildren(Object element)
  {
    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      return getResources(view);
    }

    return super.getChildren(element);
  }

  @Override
  public String getText(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      CDOSession session = (CDOSession)obj;
      IConnector connector = session.getChannel().getConnector();
      String repositoryName = session.getRepositoryName();
      return connector.getURL() + "/" + repositoryName;
    }

    if (obj instanceof CDOView)
    {
      CDOView view = (CDOView)obj;
      return view.isHistorical() ? new Date(view.getTimeStamp()).toString() : view.isReadOnly() ? "View"
          : "Transaction";
    }

    return super.getText(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_SESSION);
    }

    if (obj instanceof CDOView)
    {
      CDOView view = (CDOView)obj;
      if (view.isHistorical())
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_HISTORICAL);
      }

      if (view.isReadOnly())
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_READONLY);
      }

      return SharedIcons.getImage(SharedIcons.OBJ_EDITOR);
    }

    return super.getImage(obj);
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
        CDOSession session = (CDOSession)object;
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
    }
  }

  public static ImageDescriptor getOpenEditorImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR);
  }

  @Override
  protected Display getDisplay()
  {
    Display display = getViewer().getControl().getDisplay();
    if (display == null)
    {
      display = Display.getCurrent();
    }

    if (display == null)
    {
      display = Display.getDefault();
    }

    if (display == null)
    {
      throw new IllegalStateException("display == null");
    }

    return display;
  }

  protected Object[] getResources(CDOView view)
  {
    return ResourceHistory.INSTANCE.getEntries(view);
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
