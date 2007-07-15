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
import org.eclipse.emf.cdo.CDOViewCommittedEvent;
import org.eclipse.emf.cdo.CDOViewDirtyEvent;
import org.eclipse.emf.cdo.CDOViewEvent;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory.Entry;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.ui.actions.LongRunningAction;
import org.eclipse.net4j.ui.views.ContainerItemProvider;
import org.eclipse.net4j.ui.views.IElementFilter;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOItemProvider extends ContainerItemProvider
{
  private IWorkbenchPage page;

  private Map<CDOView, CDOViewHistory> viewHistories = new HashMap();

  private IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewDirtyEvent || event instanceof CDOViewCommittedEvent)
      {
        try
        {
          final CDOView view = ((CDOViewEvent)event).getView();
          getViewer().getControl().getDisplay().syncExec(new Runnable()
          {
            public void run()
            {
              try
              {
                fireLabelProviderChanged(view);
                CDOViewHistory history = viewHistories.get(view);
                if (history != null)
                {
                  Entry[] entries = history.getEntries();
                  if (entries != null && entries.length != 0)
                  {
                    fireLabelProviderChanged(entries);
                  }
                }
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
    }
  };

  private IListener historyListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewHistoryEvent)
      {
        CDOViewHistoryEvent e = (CDOViewHistoryEvent)event;
        CDOView view = e.getViewHistory().getView();
        refreshElement(view, false);

        Entry addedEntry = e.getAddedEntry();
        if (addedEntry != null)
        {
          revealElement(addedEntry);
        }
      }
    }
  };

  private static int lastResourceNumber = 0;

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
    if (obj instanceof CDOSession)
    {
      CDOSession session = (CDOSession)obj;
      IConnector connector = session.getChannel().getConnector();
      String repositoryName = session.getRepositoryName();
      return connector.getURL() + "/" + repositoryName + " [" + session.getSessionID() + "]";
    }

    if (obj instanceof CDOView)
    {
      CDOView view = (CDOView)obj;
      return (view.isDirty() ? "*" : "")
          + (view.isHistorical() ? new Date(view.getTimeStamp()).toString() : view.isReadOnly() ? "View"
              : "Transaction") + " [" + view.getID() + "]";
    }

    if (obj instanceof CDOViewHistory.Entry)
    {
      CDOViewHistory.Entry entry = (CDOViewHistory.Entry)obj;
      return (entry.getView().isDirty() ? "*" : "") + entry.getResourcePath();
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
    manager.add(new OpenTransactionAction(page, session));
    manager.add(new OpenViewAction(page, session));
    manager.add(new OpenHistoricalViewAction(page, session));
    manager.add(new Separator());
    manager.add(new CloseSessionAction(page, session));
  }

  protected void fillView(IMenuManager manager, CDOView view)
  {
    manager.add(new LoadResourceAction(page, view));
    if (view.isReadWrite())
    {
      manager.add(new CreateResourceAction(page, view));
    }

    manager.add(new ShowViewAction(page, view));
    manager.add(new Separator());
    manager.add(new CloseViewAction(page, view));
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
      view.addListener(viewListener);

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
      view.removeListener(viewListener);

      CDOViewHistory history = viewHistories.remove(view);
      history.removeListener(historyListener);
      history.dispose();
    }
  }

  public static ImageDescriptor getOpenEditorImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR);
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class SessionAction extends LongRunningAction
  {
    private CDOSession session;

    public SessionAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOSession session)
    {
      super(page, text, toolTipText, image);
      this.session = session;
    }

    public CDOSession getSession()
    {
      return session;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class OpenTransactionAction extends SessionAction
  {
    public OpenTransactionAction(IWorkbenchPage page, CDOSession session)
    {
      super(page, "Open Transaction", "Open a CDO transaction", getOpenEditorImageDescriptor(), session);
    }

    @Override
    protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      getSession().openView(new ResourceSetImpl());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class OpenViewAction extends SessionAction
  {
    public OpenViewAction(IWorkbenchPage page, CDOSession session)
    {
      super(page, "Open View", "Open a CDO view", getOpenEditorImageDescriptor(), session);
    }

    @Override
    protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      getSession().openView(new ResourceSetImpl(), true);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class OpenHistoricalViewAction extends SessionAction
  {
    public OpenHistoricalViewAction(IWorkbenchPage page, CDOSession session)
    {
      super(page, "Open Historical View", "Open a historical CDO view", getOpenEditorImageDescriptor(), session);
    }

    @Override
    protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      getSession().openView(new ResourceSetImpl(), System.currentTimeMillis());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CloseSessionAction extends SessionAction
  {
    public CloseSessionAction(IWorkbenchPage page, CDOSession session)
    {
      super(page, "Close", "Close the CDO session", null, session);
    }

    @Override
    protected void doRun(final IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      getSession().close();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class ViewAction extends LongRunningAction
  {
    private CDOView view;

    public ViewAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOView view)
    {
      super(page, text, toolTipText, image);
      this.view = view;
    }

    public CDOView getView()
    {
      return view;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LoadResourceAction extends ViewAction
  {
    private String resourcePath;

    public LoadResourceAction(IWorkbenchPage page, CDOView view)
    {
      super(page, "Load Resource", "Load a CDO resource", null, view);
    }

    @Override
    protected void preRun(IWorkbenchPage page) throws Exception
    {
      String uri = lastResourceNumber == 0 ? "" : "/res" + lastResourceNumber;
      InputDialog dialog = new InputDialog(page.getWorkbenchWindow().getShell(), "Load Resource",
          "Enter resource path:", uri, null);
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
      CDOEditor.open(page, getView(), resourcePath);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CreateResourceAction extends ViewAction
  {
    private String resourcePath;

    public CreateResourceAction(IWorkbenchPage page, CDOView view)
    {
      super(page, "Create Resource", "Create a CDO resource", null, view);
    }

    @Override
    protected void preRun(IWorkbenchPage page) throws Exception
    {
      InputDialog dialog = new InputDialog(page.getWorkbenchWindow().getShell(), "Create Resource",
          "Enter resource path:", "/res" + (lastResourceNumber + 1), null);
      if (dialog.open() == InputDialog.OK)
      {
        ++lastResourceNumber;
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
      getView().createResource(resourcePath);
      CDOEditor.open(page, getView(), resourcePath);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ShowViewAction extends ViewAction
  {
    public ShowViewAction(IWorkbenchPage page, CDOView view)
    {
      super(page, "Show Editor", "Show a CDO editor", null, view);
    }

    @Override
    protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      CDOEditor.open(page, getView(), null);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CloseViewAction extends ViewAction
  {
    public CloseViewAction(IWorkbenchPage page, CDOView view)
    {
      super(page, "Close", "Close the CDO view", null, view);
    }

    @Override
    protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      getView().close();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class EntryAction extends LongRunningAction
  {
    private CDOViewHistory.Entry entry;

    public EntryAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
        CDOViewHistory.Entry entry)
    {
      super(page, text, toolTipText, image);
      this.entry = entry;
    }

    public CDOViewHistory.Entry getEntry()
    {
      return entry;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ShowEntryAction extends EntryAction
  {
    public ShowEntryAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
        CDOViewHistory.Entry entry)
    {
      super(page, "Show Editor", "Show a CDO editor", null, entry);
    }

    @Override
    protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
    {
      CDOView view = getEntry().getView();
      String resourcePath = getEntry().getResourcePath();
      CDOEditor.open(page, view, resourcePath);
    }
  }
}
