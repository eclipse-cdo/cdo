/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 *    Christian W. Damus (CEA LIST) - bug 419805, bug 399306
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.internal.ui.actions.ChangePasswordAction;
import org.eclipse.emf.cdo.internal.ui.actions.CloseSessionAction;
import org.eclipse.emf.cdo.internal.ui.actions.CloseViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.CommitTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.CreateBranchAction;
import org.eclipse.emf.cdo.internal.ui.actions.DeleteBranchAction;
import org.eclipse.emf.cdo.internal.ui.actions.DisableViewDurabilityAction;
import org.eclipse.emf.cdo.internal.ui.actions.EnableViewDurabilityAction;
import org.eclipse.emf.cdo.internal.ui.actions.ExportResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.ImportResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.LoadResourceAction;
import org.eclipse.emf.cdo.internal.ui.actions.ManagePackagesAction;
import org.eclipse.emf.cdo.internal.ui.actions.MergeBranchPointAction;
import org.eclipse.emf.cdo.internal.ui.actions.MergeConflictsAction;
import org.eclipse.emf.cdo.internal.ui.actions.NewResourceNodeAction;
import org.eclipse.emf.cdo.internal.ui.actions.NewTopLevelResourceNodeAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenAuditAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenDurableViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenResourceEditorAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.OpenViewAction;
import org.eclipse.emf.cdo.internal.ui.actions.RegisterSinglePackageAction;
import org.eclipse.emf.cdo.internal.ui.actions.RollbackTransactionAction;
import org.eclipse.emf.cdo.internal.ui.actions.SwitchTargetAction;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionPermissionsChangedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;
import org.eclipse.emf.cdo.view.CDOViewSet;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements multiple functionality related with UI representation of basic CDO concepts on
 * {@link org.eclipse.jface.viewers.TreeViewer TreeViewer}-based editors and views.
 * <p>
 * For instance, behaves as {@link org.eclipse.jface.viewers.IContentProvider content} and
 * {@link org.eclipse.jface.viewers.ILabelProvider label} provider for concepts such as {@link CDOSession},
 * {@link CDOView}, {@link CDOResource} and {@link CDOResourceFolder}. It also providers common context menu action over
 * those elements.
 *
 * @author Eike Stepper
 * @see org.eclipse.jface.viewers.IContentProvider
 * @see org.eclipse.jface.viewers.ILabelProvider
 */
public class CDOItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  private IPropertyListener editorRegistryListener;

  private final ElementListener elementListener = new ElementListener();

  private ResourceManager resourceManager;

  private IWorkbenchPage page;

  private boolean mergeMainBranchWithSession;

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
  public void dispose()
  {
    elementListener.dispose();

    if (editorRegistryListener != null)
    {
      PlatformUI.getWorkbench().getEditorRegistry().removePropertyListener(editorRegistryListener);
      resourceManager = null;
    }

    if (resourceManager != null)
    {
      resourceManager.dispose();
      resourceManager = null;
    }

    super.dispose();
  }

  /**
   * @since 4.4
   */
  public boolean useFullPath(Object object)
  {
    return false;
  }

  /**
   * @since 4.2
   */
  public boolean isMergeMainBranchWithSession()
  {
    return mergeMainBranchWithSession;
  }

  /**
   * @since 4.2
   */
  public void setMergeMainBranchWithSession(boolean mergeMainBranchWithSession)
  {
    if (this.mergeMainBranchWithSession != mergeMainBranchWithSession)
    {
      this.mergeMainBranchWithSession = mergeMainBranchWithSession;
      refreshViewer(true);
    }
  }

  @Override
  public Object[] getChildren(Object element)
  {
    if (element instanceof CDOBranchManager)
    {
      return new Object[] { ((CDOBranchManager)element).getMainBranch() };
    }

    if (element instanceof CDOBranch)
    {
      return getChildren((CDOBranch)element);
    }

    if (mergeMainBranchWithSession && element instanceof CDOSession)
    {
      return getChildren(((CDOSession)element).getBranchManager().getMainBranch());
    }

    if (element instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)element;
      if (folder.cdoPermission() == CDOPermission.NONE)
      {
        return NO_ELEMENTS;
      }

      return folder.getNodes().toArray();
    }

    return super.getChildren(element);
  }

  /**
   * @since 4.2
   */
  protected Object[] getChildren(CDOBranch branch)
  {
    CDOBranch[] branches = branch.getBranches();
    if (!mergeMainBranchWithSession)
    {
      return branch.getBranches();
    }

    Object[] views = getViews(branch);

    if (views.length == 0)
    {
      return branches;
    }

    if (branches.length == 0)
    {
      return views;
    }

    Object[] children = new Object[branches.length + views.length];
    System.arraycopy(branches, 0, children, 0, branches.length);
    System.arraycopy(views, 0, children, branches.length, views.length);
    return children;
  }

  private Object[] getViews(CDOBranch branch)
  {
    CDOSession session = CDOUtil.getSession(branch);
    if (session != null)
    {
      return session.getViews(branch);
    }

    return NO_ELEMENTS;
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (element instanceof CDOBranchManager)
    {
      return true; // Main branch always exists
    }

    if (element instanceof CDOBranch)
    {
      return hasChildren((CDOBranch)element);
    }

    if (element instanceof CDOSession)
    {
      if (mergeMainBranchWithSession)
      {
        return hasChildren(((CDOSession)element).getBranchManager().getMainBranch());
      }
    }

    if (element instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)element;
      if (folder.cdoPermission() == CDOPermission.NONE)
      {
        return false;
      }

      return !folder.getNodes().isEmpty();
    }

    return super.hasChildren(element);
  }

  /**
   * @since 4.2
   */
  protected boolean hasChildren(CDOBranch branch)
  {
    if (!branch.isEmpty())
    {
      return true;
    }

    Object[] views = getViews(branch);
    return views.length != 0;
  }

  @Override
  public Object getParent(Object element)
  {
    if (element instanceof CDOBranch)
    {
      CDOBranch branch = (CDOBranch)element;
      if (branch.isMainBranch())
      {
        if (mergeMainBranchWithSession)
        {
          return CDOUtil.getSession(branch);
        }

        return branch.getBranchManager();
      }

      return branch.getBase().getBranch();
    }

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

    if (element instanceof CDOView)
    {
      CDOView view = (CDOView)element;
      CDOBranch branch = view.getBranch();
      if (branch.isMainBranch() || !mergeMainBranchWithSession)
      {
        return view.getSession();
      }

      return branch;
    }

    return super.getParent(element);
  }

  @Override
  public String getText(Object obj)
  {
    if (obj instanceof CDOCommonView)
    {
      // Don't treat views as branch points (see below).
      return super.getText(obj);
    }

    if (obj instanceof CDOBranch)
    {
      if (useFullPath(obj))
      {
        return ((CDOBranch)obj).getPathName();
      }

      return ((CDOBranch)obj).getName();
    }

    if (obj instanceof CDOBranchPoint)
    {
      CDOBranchPoint branchPoint = (CDOBranchPoint)obj;
      String result = getText(branchPoint.getBranch());

      long timeStamp = branchPoint.getTimeStamp();
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        result += " [" + CDOCommonUtil.formatTimeStamp(timeStamp) + "]";
      }

      return result;
    }

    if (obj instanceof CDOResourceNode)
    {
      if (useFullPath(obj))
      {
        return ((CDOResourceNode)obj).getPath();
      }

      return ((CDOResourceNode)obj).getName();
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

    if (obj instanceof CDOViewSet)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_VIEW_SET);
    }

    if (obj instanceof CDOView)
    {
      CDOView view = (CDOView)obj;
      return getViewImage(view);
    }

    if (obj instanceof CDOCommitInfo)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_COMMIT);
    }

    if (obj instanceof CDOBranch)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_BRANCH);
    }

    if (obj instanceof CDOBranchPoint)
    {
      CDOBranchPoint branchPoint = (CDOBranchPoint)obj;
      if (branchPoint.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_BRANCH);
      }

      return SharedIcons.getImage(SharedIcons.OBJ_BRANCH_POINT);
    }

    if (obj instanceof CDOResourceFolder)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_RESOURCE_FOLDER);
    }

    if (obj instanceof CDOResourceLeaf)
    {
      String name = ((CDOResourceLeaf)obj).getName();
      IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(name);
      if (editorDescriptor != null && !CDOEditorUtil.TEXT_EDITOR_ID.equals(editorDescriptor.getId()))
      {
        Image image = getWorkbenchImage(name);
        if (image != null)
        {
          return image;
        }
      }

      if (obj instanceof CDOResource)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_RESOURCE);
      }

      if (obj instanceof CDOTextResource)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_TEXT_RESOURCE);
      }

      if (obj instanceof CDOBinaryResource)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_BINARY_RESOURCE);
      }
    }

    return super.getImage(obj);
  }

  /**
   * @since 4.2
   */
  protected Image getWorkbenchImage(String name)
  {
    IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();

    ImageDescriptor imageDescriptor = editorRegistry.getImageDescriptor(name);
    if (imageDescriptor != null)
    {
      if (editorRegistryListener == null)
      {
        editorRegistryListener = new EditorRegistryListener(this);
        editorRegistry.addPropertyListener(editorRegistryListener);
      }

      ResourceManager resourceManager = getResourceManager();
      return resourceManager.get(imageDescriptor);
    }

    return null;
  }

  /**
   * @since 4.2
   */
  protected ResourceManager getResourceManager()
  {
    if (resourceManager == null)
    {
      resourceManager = new LocalResourceManager(JFaceResources.getResources());
    }

    return resourceManager;
  }

  @Override
  public Color getForeground(Object obj)
  {
    if (obj instanceof CDOObject)
    {
      return CDOLabelProvider.getColor((CDOObject)obj);
    }

    if (obj instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)obj;
      if (transaction.hasConflict())
      {
        return CDOLabelProvider.getConflictColor();
      }
    }

    return super.getForeground(obj);
  }

  @Override
  public Font getFont(Object obj)
  {
    if (obj instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)obj;
      if (transaction.isDirty())
      {
        return getBoldFont();
      }
    }

    return super.getFont(obj);
  }

  /**
   * @since 4.2
   */
  @Override
  public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
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
      else if (object instanceof CDOBranch)
      {
        fillBranch(manager, (CDOBranch)object);
      }
      else if (object instanceof CDOResourceNode)
      {
        fillResourceNode(manager, (CDOResourceNode)object);
      }
    }

    manager.add(new Separator());
    super.fillContextMenu(manager, selection);
  }

  /**
   * @since 4.2
   */
  public void fillSession(IMenuManager manager, CDOSession session)
  {
    manager.add(new OpenTransactionAction(page, session));
    manager.add(new OpenViewAction(page, session));
    manager.add(new OpenAuditAction(page, session));
    manager.add(new OpenDurableViewAction(page, session));
    manager.add(new Separator());
    manager.add(new ManagePackagesAction(page, session));

    MenuManager generatedManager = new MenuManager(Messages.getString("CDOItemProvider.8")); //$NON-NLS-1$
    if (fillGenerated(generatedManager, session))
    {
      manager.add(generatedManager);
    }

    if (session.getRepositoryInfo().isSupportingBranches())
    {
      manager.add(new Separator());
      fillBranch(manager, session.getBranchManager().getMainBranch());
    }

    manager.add(new Separator());

    if (!StringUtil.isEmpty(session.getUserID()))
    {
      manager.add(new ChangePasswordAction(page, session));
    }

    manager.add(new CloseSessionAction(page, session));
  }

  /**
   * @since 4.2
   */
  public boolean fillGenerated(MenuManager manager, CDOSession session)
  {
    List<String> registeredURIs = new ArrayList<>(EPackage.Registry.INSTANCE.keySet());
    Collections.sort(registeredURIs, new Comparator<String>()
    {
      @Override
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
   * @since 4.2
   */
  public void fillView(IMenuManager manager, CDOView view)
  {
    if (!view.isReadOnly())
    {
      CDOResource rootResource = view.getRootResource();
      if (rootResource.cdoPermission() == CDOPermission.WRITE)
      {
        manager.add(new NewTopLevelResourceNodeAction(this, page, view, rootResource, NewTopLevelResourceNodeAction.Type.FOLDER));
        manager.add(new NewTopLevelResourceNodeAction(this, page, view, rootResource, NewTopLevelResourceNodeAction.Type.MODEL));
        manager.add(new NewTopLevelResourceNodeAction(this, page, view, rootResource, NewTopLevelResourceNodeAction.Type.TEXT));
        manager.add(new NewTopLevelResourceNodeAction(this, page, view, rootResource, NewTopLevelResourceNodeAction.Type.BINARY));
      }
    }

    manager.add(new Separator());
    manager.add(new LoadResourceAction(page, view));
    manager.add(new ExportResourceAction(page, view));

    if (!view.isReadOnly())
    {
      manager.add(new ImportResourceAction(page, view));
      manager.add(new Separator());
      manager.add(new MergeConflictsAction((CDOTransaction)view));
      manager.add(new MergeBranchPointAction(page, view));
      manager.add(new CommitTransactionAction(page, view));
      manager.add(new RollbackTransactionAction(page, view));
    }

    manager.add(new Separator());

    CDORepositoryInfo repositoryInfo = view.getSession().getRepositoryInfo();
    if (view.isReadOnly() && repositoryInfo.isSupportingAudits() || repositoryInfo.isSupportingBranches())
    {
      manager.add(new SwitchTargetAction(page, view));
    }

    if (view.getDurableLockingID() == null)
    {
      manager.add(new EnableViewDurabilityAction(page, view));
    }
    else
    {
      manager.add(new DisableViewDurabilityAction(page, view));
    }

    manager.add(new Separator());
    manager.add(new CloseViewAction(page, view));
  }

  /**
   * @since 4.2
   */
  public void fillBranch(IMenuManager manager, CDOBranch branch)
  {
    manager.add(new CreateBranchAction(page, branch.getHead()));

    if (!branch.isMainBranch() && !branch.isDeleted())
    {
      manager.add(new DeleteBranchAction(page, branch));
    }
  }

  /**
   * @since 4.4
   */
  public void fillResourceNode(IMenuManager manager, CDOResourceNode node)
  {
    if (node instanceof CDOResourceFolder)
    {
      fillResourceFolder(manager, (CDOResourceFolder)node);
    }
    else if (node instanceof CDOResourceLeaf)
    {
      fillResourceLeaf(manager, node);
    }
  }

  /**
   * @since 4.2
   */
  public void fillResourceFolder(IMenuManager manager, CDOResourceFolder folder)
  {
    if (!folder.cdoView().isReadOnly() && folder.cdoPermission() == CDOPermission.WRITE)
    {
      manager.add(new NewResourceNodeAction.Folder(page, folder));
      manager.add(new NewResourceNodeAction.Model(page, folder));
      manager.add(new NewResourceNodeAction.Binary(page, folder));
      manager.add(new NewResourceNodeAction.Text(page, folder));
    }
  }

  /**
   * @since 4.2
   */
  public void fillResourceLeaf(IMenuManager manager, Object object)
  {
    CDOEditorUtil.populateMenu(manager, (CDOResourceLeaf)object, page);

    if (object instanceof CDOResource)
    {
      fillResource(manager, (CDOResource)object);
    }
    else if (object instanceof CDOTextResource)
    {
      fillTextResource(manager, (CDOTextResource)object);
    }
    else if (object instanceof CDOBinaryResource)
    {
      fillBinaryResource(manager, (CDOBinaryResource)object);
    }
  }

  /**
   * @since 4.2
   */
  public void fillResource(IMenuManager manager, CDOResource resource)
  {
    manager.add(new OpenResourceEditorAction(page, resource));
  }

  /**
   * @since 4.2
   */
  public void fillTextResource(IMenuManager manager, CDOTextResource resource)
  {
    manager.add(new OpenResourceEditorAction(page, resource));
  }

  /**
   * @since 4.2
   */
  public void fillBinaryResource(IMenuManager manager, CDOBinaryResource resource)
  {
    manager.add(new OpenResourceEditorAction(page, resource));
  }

  @Override
  protected void elementAdded(Object element, Object parent)
  {
    super.elementAdded(element, parent);

    if (element instanceof CDOSession)
    {
      elementListener.attach((CDOSession)element);
    }
  }

  /**
   * @since 4.2
   */
  @Override
  public int compare(Viewer viewer, Object e1, Object e2)
  {
    if (e1 instanceof CDOResourceFolder)
    {
      if (e2 instanceof CDOResourceLeaf)
      {
        return -1;
      }
    }

    if (e1 instanceof CDOResourceLeaf)
    {
      if (e2 instanceof CDOResourceFolder)
      {
        return 1;
      }
    }

    return super.compare(viewer, e1, e2);
  }

  /**
   * @since 3.0
   */
  public static ImageDescriptor getViewImageDescriptor(CDOView view)
  {
    if (view == null)
    {
      return null;
    }

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
    if (view == null)
    {
      return null;
    }

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

  /**
   * A {@link IPropertyListener listener} on the platform's {@link IEditorRegistry editor registry} that fires {@link LabelProviderChangedEvent label events}
   * from the associated {@link #getItemProvider() item provider} when {@link CDOTransferElement element} labels need to be updated.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  protected static class EditorRegistryListener implements IPropertyListener
  {
    private final CDOItemProvider itemProvider;

    public EditorRegistryListener(CDOItemProvider itemProvider)
    {
      this.itemProvider = itemProvider;
    }

    public CDOItemProvider getItemProvider()
    {
      return itemProvider;
    }

    @Override
    public void propertyChanged(Object source, int propId)
    {
      if (propId == IEditorRegistry.PROP_CONTENTS)
      {
        itemProvider.fireLabelProviderChanged();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ElementListener implements IListener, CDOObjectHandler
  {
    private final Set<INotifier> notifiers = new HashSet<>();

    private boolean disposed;

    public ElementListener()
    {
    }

    public void attach(INotifier notifier)
    {
      synchronized (notifiers)
      {
        if (!disposed)
        {
          if (notifiers.add(notifier))
          {
            if (notifier instanceof CDOSession)
            {
              CDOSession session = (CDOSession)notifier;
              for (CDOView view : session.getViews())
              {
                attachView(view);
              }

              for (CDOView view : session.getTransactions())
              {
                attachView(view);
              }

              notifier.addListener(this);
            }
            else if (notifier instanceof CDOView)
            {
              CDOView view = (CDOView)notifier;
              attachView(view);
            }
          }
        }
      }
    }

    private void attachView(CDOView view)
    {
      view.addListener(this);
      view.addObjectHandler(this);
    }

    private void detach(INotifier notifier)
    {
      notifier.removeListener(this);
      if (notifier instanceof CDOView)
      {
        CDOView view = (CDOView)notifier;
        view.removeObjectHandler(this);
      }
    }

    public void dispose()
    {
      synchronized (notifiers)
      {
        disposed = true;
        for (INotifier notifier : notifiers)
        {
          detach(notifier);
        }

        notifiers.clear();
      }
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          ILifecycle notifier = e.getSource();

          synchronized (notifiers)
          {
            if (notifiers.remove(notifier))
            {
              notifier.removeListener(this);
            }
          }
        }
      }
      else if (event instanceof IContainerEvent<?>)
      {
        IContainerEvent<?> e = (IContainerEvent<?>)event;
        for (IContainerDelta<?> delta : e.getDeltas())
        {
          if (delta.getKind() == IContainerDelta.Kind.ADDED)
          {
            Object element = delta.getElement();
            if (element instanceof CDOView)
            {
              attach((CDOView)element);
            }
          }
        }
      }
      else if (event instanceof CDOSessionInvalidationEvent)
      {
        refreshViewer(true);
      }
      else if (event instanceof CDOSessionPermissionsChangedEvent)
      {
        refreshViewer(true);
      }
      else if (event instanceof CDOViewTargetChangedEvent)
      {
        refreshViewer(true);
      }
      else if (event instanceof CDOViewLocksChangedEvent)
      {
        CDOViewLocksChangedEvent e = (CDOViewLocksChangedEvent)event;
        EObject[] objects = e.getAffectedObjects();
        if (objects.length != 0)
        {
          updateLabels(objects);
        }
      }
    }

    @Override
    public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
    {
      updateLabels(object);
    }
  }
}
