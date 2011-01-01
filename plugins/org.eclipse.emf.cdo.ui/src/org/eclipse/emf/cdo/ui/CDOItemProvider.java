/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.ChangeViewTargetAction;
import org.eclipse.emf.cdo.internal.ui.actions.CloseSessionAction;
import org.eclipse.emf.cdo.internal.ui.actions.CloseViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.CommitTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.CreateResourceNodeAction;
import org.eclipse.emf.cdo.internal.ui.actions.ExportResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.ImportResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.LoadResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.ManagePackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenAuditAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenResourceEditorAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenViewEditorAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterFilesystemPackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterSinglePackageAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterWorkspacePackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.ReloadViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.RollbackTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.ToggleLegacyModeDefaultAction;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  private IWorkbenchPage page;

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
      return ((CDOView)element).getRootResource().getContents().toArray();
    }

    if (element instanceof CDOResourceFolder)
    {
      return ((CDOResourceFolder)element).getNodes().toArray();
    }

    return super.getChildren(element);
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (element instanceof CDOView)
    {
      return ((CDOView)element).getRootResource().getContents().size() > 0;
    }

    if (element instanceof CDOResourceFolder)
    {
      return ((CDOResourceFolder)element).getNodes().size() > 0;
    }

    return super.hasChildren(element);
  }

  @Override
  public Object getParent(Object element)
  {
    if (element instanceof CDOResourceNode)
    {
      CDOResourceNode node = (CDOResourceNode)element;
      CDOResourceNode parent = (CDOResourceNode)node.eContainer();
      if (parent == null || parent.isRoot())
      {
        return node.cdoView();
      }

      return parent;
    }

    return super.getParent(element);
  }

  @Override
  public String getText(Object obj)
  {
    if (obj instanceof CDOResourceNode)
    {
      return ((CDOResourceNode)obj).getName();
    }

    if (obj instanceof CDOSession)
    {
      return getSessionLabel((CDOSession)obj);
    }

    if (obj instanceof CDOView)
    {
      return getViewLabel((CDOView)obj);
    }

    return super.getText(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      CDOSession session = (CDOSession)obj;
      State state = session.getRepositoryInfo().getState();
      switch (state)
      {
      case ONLINE:
        return SharedIcons.getImage(SharedIcons.OBJ_SESSION);
      case SYNCING:
        return SharedIcons.getImage(SharedIcons.OBJ_SESSION_SYNCING);
      case OFFLINE:
        return SharedIcons.getImage(SharedIcons.OBJ_SESSION_OFFLINE);
      }
    }

    if (obj instanceof CDOView)
    {
      CDOView view = (CDOView)obj;
      return getViewImage(view);
    }

    if (obj instanceof CDOResourceFolder)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_RESOURCE_FOLDER);
    }

    if (obj instanceof CDOResource)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_RESOURCE);
    }

    return super.getImage(obj);
  }

  /**
   * @since 2.0
   */
  public static String getSessionLabel(CDOSession session)
  {
    return MessageFormat.format(
        Messages.getString("CDOItemProvider.0"), session.getRepositoryInfo().getName(), session.getSessionID()); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public static String getViewLabel(CDOView view)
  {
    if (view instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)view;
      return MessageFormat.format(
          Messages.getString("CDOItemProvider.3"), transaction.isDirty() ? "*" : "", transaction.getViewID()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    long timeStamp = view.getTimeStamp();
    if (timeStamp != CDOView.UNSPECIFIED_DATE)
    {
      return MessageFormat.format(Messages.getString("CDOItemProvider.6"), timeStamp); //$NON-NLS-1$
    }

    return MessageFormat.format(Messages.getString("CDOItemProvider.7"), view.getViewID()); //$NON-NLS-1$
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
      else if (object instanceof CDOResource)
      {
        fillResource(manager, (CDOResource)object);
      }
      else if (object instanceof CDOResourceFolder)
      {
        fillResourceFolder(manager, (CDOResourceFolder)object);
      }
    }
  }

  /**
   * @since 3.0
   */
  protected void fillResourceFolder(IMenuManager manager, CDOResourceFolder folder)
  {
  }

  /**
   * @since 3.0
   */
  protected void fillResource(IMenuManager manager, CDOResource resource)
  {
    manager.add(new OpenResourceEditorAction(page, resource));
  }

  /**
   * @since 2.0
   */
  protected void fillSession(IMenuManager manager, CDOSession session)
  {
    manager.add(new OpenTransactionAction(page, session));
    manager.add(new OpenViewAction(page, session));
    manager.add(new OpenAuditAction(page, session));
    manager.add(new Separator());
    manager.add(new ManagePackagesAction(page, session));

    MenuManager generatedManager = new MenuManager(Messages.getString("CDOItemProvider.8")); //$NON-NLS-1$
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

    manager.add(new ToggleLegacyModeDefaultAction(session));
    manager.add(new Separator());
    manager.add(new CloseSessionAction(page, session));
  }

  /**
   * @since 2.0
   */
  protected boolean fillGenerated(MenuManager manager, CDOSession session)
  {
    List<String> registeredURIs = new ArrayList<String>(EPackage.Registry.INSTANCE.keySet());
    Collections.sort(registeredURIs, new Comparator<String>()
    {
      public int compare(String o1, String o2)
      {
        return o1.compareTo(o2);
      }
    });

    boolean added = false;
    CDOPackageRegistry packageRegistry = session.getPackageRegistry();
    for (String packageURI : registeredURIs)
    {
      if (!packageRegistry.containsKey(packageURI))
      {
        Type type = CDOPackageTypeRegistry.INSTANCE.lookup(packageURI);
        if (type == Type.NATIVE)
        {
          EPackage ePackage = packageRegistry.getEPackage(packageURI);
          if (ePackage == null)
          {
            ePackage = EPackage.Registry.INSTANCE.getEPackage(packageURI);
          }

          if (ePackage != null)
          {
            manager.add(new RegisterSinglePackageAction(page, session, packageURI));
            added = true;
          }
        }
      }
    }

    return added;
  }

  /**
   * @since 2.0
   */
  protected void fillView(IMenuManager manager, CDOView view)
  {
    manager.add(new OpenViewEditorAction(page, view));
    manager.add(new LoadResourceAction(page, view));
    manager.add(new ExportResourceAction(page, view));
    manager.add(new ChangeViewTargetAction(page, view));
    manager.add(new Separator());
    if (!view.isReadOnly())
    {
      {
        CDOResource rootResource = view.getRootResource();
        manager.add(new CreateResourceNodeAction(this, page, view, rootResource, false));
        manager.add(new CreateResourceNodeAction(this, page, view, rootResource, true));
      }

      manager.add(new ImportResourceAction(page, view));
      manager.add(new CommitTransactionAction(page, view));
      manager.add(new RollbackTransactionAction(page, view));
    }

    manager.add(new Separator());
    manager.add(new ReloadViewAction(page, view));
    manager.add(new Separator());
    manager.add(new CloseViewAction(page, view));
  }

  @Override
  protected void elementAdded(Object element, Object parent)
  {
    super.elementAdded(element, parent);

    if (element instanceof CDOSession)
    {
      ((CDOSession)element).addListener(new IListener()
      {
        public void notifyEvent(IEvent event)
        {
          if (event instanceof CDOSessionInvalidationEvent)
          {
            refreshViewer(true);
          }
        }
      });
    }

    if (element instanceof CDOView)
    {
      ((CDOView)element).addListener(new IListener()
      {
        public void notifyEvent(IEvent event)
        {
          if (event instanceof CDOViewTargetChangedEvent)
          {
            refreshViewer(true);
          }
        }
      });
    }
  }

  /**
   * @since 3.0
   */
  public static ImageDescriptor getViewImageDescriptor(CDOView view)
  {
    if (view.isReadOnly())
    {
      if (view.getTimeStamp() != CDOView.UNSPECIFIED_DATE)
      {
        return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_HISTORICAL);
      }

      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_READONLY);
    }

    return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR);
  }

  /**
   * @since 3.0
   */
  public static Image getViewImage(CDOView view)
  {
    if (view.isReadOnly())
    {
      if (view.getTimeStamp() != CDOView.UNSPECIFIED_DATE)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_HISTORICAL);
      }

      return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_READONLY);
    }

    return SharedIcons.getImage(SharedIcons.OBJ_EDITOR);
  }
}
