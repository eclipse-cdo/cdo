/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOAudit;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewEvent;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.actions.CloseSessionAction;
import org.eclipse.emf.cdo.internal.ui.actions.CloseViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.CommitTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.CreateResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.ImportResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.LoadResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.ManagePackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenAuditAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenViewEditorAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterFilesystemPackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterSinglePackageAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterWorkspacePackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.ReloadViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.RollbackTransactionAction;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory.Entry;
import org.eclipse.emf.cdo.util.CDOPackageType;
import org.eclipse.emf.cdo.util.CDOPackageTypeRegistry;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  private IWorkbenchPage page;

  private Map<CDOView, CDOViewHistory> viewHistories = new HashMap<CDOView, CDOViewHistory>();

  private IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOTransactionStartedEvent || event instanceof CDOTransactionFinishedEvent)
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
    IConnector connector = session.getConnector();
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

    MenuManager generatedManager = new MenuManager("Register Generated Package");
    if (fillGenerated(generatedManager, session))
    {
      manager.add(generatedManager);
    }

    IAction a1 = new RegisterWorkspacePackagesAction(page, session);
    a1.setText(a1.getText() + SafeAction.INTERACTIVE);
    manager.add(a1);

    RegisterFilesystemPackagesAction a2 = new RegisterFilesystemPackagesAction(page, session);
    a2.setText(a2.getText() + SafeAction.INTERACTIVE);
    manager.add(a2);

    manager.add(new Separator());
    manager.add(new CloseSessionAction(page, session));
  }

  protected boolean fillGenerated(MenuManager manager, CDOSession session)
  {
    Set<Map.Entry<String, CDOPackageType>> entrySet = CDOPackageTypeRegistry.INSTANCE.entrySet();
    List<Map.Entry<String, CDOPackageType>> entryList = new ArrayList<Map.Entry<String, CDOPackageType>>(entrySet);
    Collections.sort(entryList, new Comparator<Map.Entry<String, CDOPackageType>>()
    {
      public int compare(Map.Entry<String, CDOPackageType> e1, Map.Entry<String, CDOPackageType> e2)
      {
        return e1.getKey().compareTo(e2.getKey());
      }
    });

    Set<String> registeredURIs = new HashSet<String>(session.getPackageRegistry().keySet());
    boolean added = false;
    for (Map.Entry<String, CDOPackageType> entry : entryList)
    {
      String packageURI = entry.getKey();
      if (!registeredURIs.contains(packageURI))
      {
        manager.add(new RegisterSinglePackageAction(page, session, packageURI, entry.getValue()));
        added = true;
      }
    }

    return added;
  }

  protected void fillView(IMenuManager manager, CDOView view)
  {
    manager.add(new OpenViewEditorAction(page, view));
    manager.add(new LoadResourceAction(page, view));
    manager.add(new Separator());
    if (view.getViewType() == CDOView.Type.TRANSACTION)
    {
      manager.add(new CreateResourceAction(page, view));
      manager.add(new ImportResourceAction(page, view));
      manager.add(new CommitTransactionAction(page, view));
      manager.add(new RollbackTransactionAction(page, view));
    }

    manager.add(new Separator());
    manager.add(new ReloadViewAction(page, view));
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
