/*
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
package org.eclipse.emf.cdo.ui.ide;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreatedEvent;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.team.IRepositoryManager;
import org.eclipse.emf.cdo.team.IRepositoryProject;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.ui.CDOEventHandler;
import org.eclipse.emf.cdo.ui.ide.CommonNavigatorUtils.MessageType;
import org.eclipse.emf.cdo.ui.ide.Node.BranchNode;
import org.eclipse.emf.cdo.ui.ide.Node.PackagesNode;
import org.eclipse.emf.cdo.ui.ide.Node.ResourcesNode;
import org.eclipse.emf.cdo.ui.ide.Node.SessionsNode;
import org.eclipse.emf.cdo.ui.internal.ide.actions.RemoveResourceActionDelegate;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.StructuredContentProvider;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory.Descriptor.Registry;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link org.eclipse.jface.viewers.ITreeContentProvider ITreeContentProvider} implementation for the CDO Team
 * integration. Capable of providing containment information for an {@link org.eclipse.core.resources.IProject project}
 * synchronized with a repository. Understands containment information of the {@link org.eclipse.emf.cdo.ui.ide.Node
 * node} abstraction.
 * 
 * @author Eike Stepper
 */
public class RepositoryContentProvider extends StructuredContentProvider<IWorkspaceRoot> implements
    ITreeContentProvider
{
  private static final Object[] EMPTY = {};

  private ComposedAdapterFactory adapterFactory;

  private Map<IRepositoryProject, RepositoryInfo> infos = new HashMap<IRepositoryProject, RepositoryInfo>();

  private Map<IRepositoryProject, RepositoryCDOEventHandler> eventHandlers = new HashMap<IRepositoryProject, RepositoryCDOEventHandler>();

  private boolean sessionsNodeHidden;

  private boolean packagesNodeHidden;

  private boolean resourcesNodeHidden;

  private IListener repositoryManagerListener = new ContainerEventAdapter<IRepositoryProject>()
  {
    @Override
    protected void onAdded(IContainer<IRepositoryProject> container, IRepositoryProject element)
    {
      refreshViewer(element);
    }

    @Override
    protected void onRemoved(IContainer<IRepositoryProject> container, IRepositoryProject element)
    {
      refreshViewer(element);
    }

    private void refreshViewer(IRepositoryProject element)
    {
      getViewer().refresh(element.getProject());
    }
  };

  public RepositoryContentProvider()
  {
    adapterFactory = createAdapterFactory();
    IRepositoryManager.INSTANCE.addListener(repositoryManagerListener);
  }

  @Override
  public void dispose()
  {
    IRepositoryManager.INSTANCE.removeListener(repositoryManagerListener);
    adapterFactory.dispose();
    super.dispose();
  }

  public boolean isSessionsNodeHidden()
  {
    return sessionsNodeHidden;
  }

  public void setSessionsNodeHidden(boolean sessionNodesHidden)
  {
    sessionsNodeHidden = sessionNodesHidden;
  }

  public boolean isPackagesNodeHidden()
  {
    return packagesNodeHidden;
  }

  public void setPackagesNodeHidden(boolean packageNodesHidden)
  {
    packagesNodeHidden = packageNodesHidden;
  }

  public boolean isResourcesNodeHidden()
  {
    return resourcesNodeHidden;
  }

  public void setResourcesNodeHidden(boolean resourceNodesHidden)
  {
    resourcesNodeHidden = resourceNodesHidden;
  }

  public Object[] getChildren(Object parentElement)
  {
    try
    {
      return doGetChildren(parentElement);
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
      return CommonNavigatorUtils.createMessageProviderChild(Messages.getString("RepositoryContentProvider_0"), //$NON-NLS-1$
          MessageType.ERROR);
    }
  }

  private Object[] doGetChildren(Object parentElement)
  {
    if (parentElement instanceof IProject)
    {
      IProject project = (IProject)parentElement;
      IRepositoryProject repositoryProject = IRepositoryManager.INSTANCE.getElement(project);
      if (repositoryProject != null)
      {
        return getChildren(repositoryProject);
      }
    }

    if (parentElement instanceof Node)
    {
      Node node = (Node)parentElement;
      return node.getChildren();
    }

    if (parentElement instanceof Notifier)
    {
      Notifier notifier = (Notifier)parentElement;
      ITreeItemContentProvider adapter = (ITreeItemContentProvider)adapterFactory.adapt(notifier,
          ITreeItemContentProvider.class);
      if (adapter != null)
      {
        return adapter.getChildren(notifier).toArray();
      }
    }

    return EMPTY;
  }

  public Object[] getElements(Object parentElement)
  {
    return getChildren(parentElement);
  }

  public Object getParent(Object element)
  {
    if (element instanceof Node)
    {
      Node node = (Node)element;
      return node.getParent();
    }

    if (element instanceof Notifier)
    {
      Notifier notifier = (Notifier)element;
      ITreeItemContentProvider adapter = (ITreeItemContentProvider)adapterFactory.adapt(notifier,
          ITreeItemContentProvider.class);
      if (adapter != null)
      {
        return adapter.getParent(notifier);
      }
    }

    return null;
  }

  private Object[] getChildren(IRepositoryProject repositoryProject)
  {
    List<Object> children = new ArrayList<Object>();
    RepositoryInfo info = getRepositoryInfo(repositoryProject);

    children.add(info.getMainBranch());

    // First try virtual parent nodes
    if (!isPackagesNodeHidden())
    {
      children.add(info.getPackages());
    }

    if (!isResourcesNodeHidden())
    {
      children.add(info.getResources());
    }

    if (!isSessionsNodeHidden())
    {
      children.add(info.getSessions());
    }

    // Then try flattened sub nodes
    if (isPackagesNodeHidden())
    {
      addChildren(children, info.getPackages());
    }

    if (isResourcesNodeHidden())
    {
      addChildren(children, info.getResources());
    }

    if (isSessionsNodeHidden())
    {
      addChildren(children, info.getSessions());
    }

    return children.toArray(new Object[children.size()]);
  }

  private void addChildren(List<Object> result, Node node)
  {
    Object[] children = node.getChildren();
    for (Object child : children)
    {
      result.add(child);
    }
  }

  private RepositoryInfo getRepositoryInfo(IRepositoryProject repositoryProject)
  {
    RepositoryInfo info = infos.get(repositoryProject);
    if (info == null)
    {
      info = new RepositoryInfo(repositoryProject);
      infos.put(repositoryProject, info);

      wireUpViewerRefresher(repositoryProject, info);
      prepareViewerEventHandlers();

      // TODO Get rid of info mappings that are no longer needed (lifecycle or weakref)
    }

    return info;
  }

  private void prepareViewerEventHandlers()
  {
    // Mouse double-click
    getViewer().addDoubleClickListener(new MouseListener());

    // Keyboard actions
    getViewer().getControl().addKeyListener(new CDONavigatorKeyListener());
  }

  private void wireUpViewerRefresher(IRepositoryProject repositoryProject, RepositoryInfo info)
  {
    // Handles invalidated objects
    eventHandlers.put(repositoryProject, new RepositoryCDOEventHandler(info.getResources(),
        repositoryProject.getView(), (TreeViewer)getViewer()));

    repositoryProject.getView().getBranch().getBranchManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOBranchCreatedEvent)
        {
          refreshViewer(true);
        }
      }
    });

    repositoryProject.getView().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOViewInvalidationEvent || event instanceof CDOViewTargetChangedEvent)
        {
          refreshViewer(true);
        }
      }
    });
  }

  public static ComposedAdapterFactory createAdapterFactory()
  {
    Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
    return adapterFactory;
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class RepositoryCDOEventHandler extends CDOEventHandler
  {
    private Node resourcesNode;

    public RepositoryCDOEventHandler(Node resourcesNode, CDOView view, TreeViewer treeViewer)
    {
      super(view, treeViewer);
      this.resourcesNode = resourcesNode;
    }

    @Override
    protected void objectInvalidated(InternalCDOObject cdoObject)
    {
      if (CDOUtil.isLegacyObject(cdoObject))
      {
        CDOStateMachine.INSTANCE.read(cdoObject);
      }

      if (cdoObject instanceof CDOResource)
      {
        if (((CDOResource)cdoObject).isRoot())
        {
          refreshViewer(true);
          return;
        }
      }

      refreshElement(cdoObject, true);
    }

    @Override
    protected void viewInvalidated(Set<? extends CDOObject> dirtyObjects)
    {
      // Necessary when the parent of the dirtyObject is ResourcesNode
      // (since viewer.getInput() is IWorkspaceRoot)
      for (CDOObject cdoObject : dirtyObjects)
      {
        if (cdoObject instanceof CDOResource)
        {
          if (((CDOResource)cdoObject).isRoot())
          {
            refreshElement(resourcesNode, true);
            return;
          }
        }
      }

      super.viewInvalidated(dirtyObjects);
    }

    @Override
    protected void viewConflict(final CDOObject conflictingObject, boolean firstConflict)
    {
      refreshElement(conflictingObject, true);
    }

    @Override
    protected void viewClosed()
    {
      // TODO what should we do here? CDOObjects become disconnected, exceptions could arise everywhere
      // Temporary closing the project.

      try
      {
        // View gets on shutdown, but we shouldn't close the project
        if (PlatformUI.isWorkbenchRunning())
        {
          resourcesNode.getRepositoryProject().getProject().close(new NullProgressMonitor());
        }
      }
      catch (CoreException ex)
      {
        OM.LOG.error(ex);
      }
    }

    @Override
    protected void viewDirtyStateChanged()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RepositoryInfo
  {
    private BranchNode mainBranch;

    private PackagesNode packages;

    private ResourcesNode resources;

    private SessionsNode sessions;

    public RepositoryInfo(IRepositoryProject repositoryProject)
    {
      CDOBranch main = repositoryProject.getView().getSession().getBranchManager().getMainBranch();
      mainBranch = new BranchNode(repositoryProject, main);
      packages = new PackagesNode(repositoryProject);
      resources = new ResourcesNode(repositoryProject);
      sessions = new SessionsNode(repositoryProject);
    }

    public BranchNode getMainBranch()
    {
      return mainBranch;
    }

    public PackagesNode getPackages()
    {
      return packages;
    }

    public ResourcesNode getResources()
    {
      return resources;
    }

    public SessionsNode getSessions()
    {
      return sessions;
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  private static final class MouseListener implements IDoubleClickListener
  {
    public MouseListener()
    {
    }

    public void doubleClick(DoubleClickEvent event)
    {
      Object selection = UIUtil.getElement(event.getSelection());
      if (selection instanceof CDOResource)
      {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        CDOView view = ((CDOResource)selection).cdoView().getSession().openTransaction();

        try
        {
          CDOEditorInput editorInput = CDOEditorUtil.createCDOEditorInput(view, ((CDOResource)selection).getPath(),
              true);
          page.openEditor(editorInput, CDOEditor.EDITOR_ID);
        }
        catch (PartInitException ex)
        {
          OM.LOG.error(ex);
        }
      }
      if (selection instanceof IAdaptable)
      {
        Runnable runnable = (Runnable)((IAdaptable)selection).getAdapter(Runnable.class);
        if (runnable != null)
        {
          UIUtil.getDisplay().asyncExec(runnable);
        }
      }
    }
  }

  public boolean hasChildren(Object parentElement)
  {
    Object[] children = getChildren(parentElement);
    return children != null && children.length != 0;
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class CDONavigatorKeyListener extends KeyAdapter
  {
    public CDONavigatorKeyListener()
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
      if (e.keyCode == SWT.DEL)
      {
        RemoveResourceActionDelegate action = new RemoveResourceActionDelegate();
        action.selectionChanged(null, getViewer().getSelection());
        action.run(null);
      }
    }
  }
}
