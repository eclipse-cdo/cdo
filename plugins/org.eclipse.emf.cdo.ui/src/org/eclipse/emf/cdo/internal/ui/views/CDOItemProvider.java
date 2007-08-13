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

import org.eclipse.emf.cdo.CDOAudit;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionCommittedEvent;
import org.eclipse.emf.cdo.CDOTransactionDirtyEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewEvent;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.actions.CloseSessionAction;
import org.eclipse.emf.cdo.internal.ui.actions.CloseViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.CreateResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.LoadResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.ManagePackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenAuditAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenViewEditorAction;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory.Entry;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;
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
      if (event instanceof CDOTransactionDirtyEvent || event instanceof CDOTransactionCommittedEvent)
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

      return NO_ELEMENTS;
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
      return getSessionLabel((CDOSession)obj);
    }

    if (obj instanceof CDOView)
    {
      return getViewLabel((CDOView)obj);
    }

    if (obj instanceof CDOViewHistory.Entry)
    {
      return getHistroyEntryLabel((CDOViewHistory.Entry)obj);
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
      switch (view.getViewType())
      {
      case TRANSACTION:
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR);
      case READONLY:
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_READONLY);
      case AUDIT:
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_HISTORICAL);
      }
    }

    return super.getImage(obj);
  }

  public static String getSessionLabel(CDOSession session)
  {
    IConnector connector = session.getChannel().getConnector();
    String repositoryName = session.getRepositoryName();
    return "Session " + connector.getURL() + "/" + repositoryName + " [" + session.getSessionID() + "]";
  }

  public static String getViewLabel(CDOView view)
  {
    if (view instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)view;
      return MessageFormat.format("{0}Transaction [{1}]", transaction.isDirty() ? "*" : "", transaction.getViewID());
    }

    if (view instanceof CDOAudit)
    {
      CDOAudit audit = (CDOAudit)view;
      return MessageFormat.format("Audit [{0,date} {0,time}]", audit.getTimeStamp());
    }

    return MessageFormat.format("View [{0}]", view.getViewID());
  }

  public static String getHistroyEntryLabel(CDOViewHistory.Entry entry)
  {
    return (entry.getView().isDirty() ? "*" : "") + entry.getResourcePath();
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
    manager.add(new OpenAuditAction(page, session));
    manager.add(new Separator());
    manager.add(new ManagePackagesAction(page, session));
    manager.add(new Separator());
    manager.add(new CloseSessionAction(page, session));
  }

  protected void fillView(IMenuManager manager, CDOView view)
  {
    manager.add(new LoadResourceAction(page, view));
    if (view.getViewType() == CDOView.Type.TRANSACTION)
    {
      manager.add(new CreateResourceAction(page, view));
    }

    manager.add(new OpenViewEditorAction(page, view));
    manager.add(new Separator());
    manager.add(new CloseViewAction(page, view));
  }

  protected void fillHistoryEntry(IMenuManager manager, Entry entry)
  {
  }

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
}
