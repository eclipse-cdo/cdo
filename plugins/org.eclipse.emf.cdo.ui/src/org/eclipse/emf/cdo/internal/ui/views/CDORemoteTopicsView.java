/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteTopic;
import org.eclipse.emf.cdo.ui.CDOTopicProvider;
import org.eclipse.emf.cdo.ui.CDOTopicProvider.Listener;
import org.eclipse.emf.cdo.ui.CDOTopicProvider.Topic;
import org.eclipse.emf.cdo.ui.UserInfo;
import org.eclipse.emf.cdo.ui.UserInfo.Manager;
import org.eclipse.emf.cdo.ui.UserInfo.Manager.UserChangedEvent;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.ui.shared.CollapseAllAction;
import org.eclipse.net4j.ui.shared.ExpandAllAction;
import org.eclipse.net4j.ui.shared.LinkWithEditorAction;
import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.ui.GlobalPartAdapter;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DelegatingStyledCellLabelProvider;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class CDORemoteTopicsView extends ViewPart implements ISelectionProvider, ISetSelectionTarget
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDORemoteTopicsView"; //$NON-NLS-1$

  private static final boolean LOCAL_USER_INFO_HIDE = //
      OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.ui.CDORemoteTopicsView.LOCAL_USER_INFO_HIDE"); //$NON-NLS-1$

  private static final boolean LOCAL_USER_INFO_DISABLE = //
      OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.ui.CDORemoteTopicsView.LOCAL_USER_INFO_DISABLE"); //$NON-NLS-1$

  private static final boolean EXPAND_SELECTION = //
      OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.ui.CDORemoteTopicsView.EXPAND_SELECTION"); //$NON-NLS-1$

  private Manager userInfoManager;

  private AutoCloseable userInfoListener;

  private final Map<Topic, CDORemoteTopic> remoteTopics = new HashMap<>();

  private final Map<CDORemoteTopic, RemoteTopicItem> remoteTopicItems = new HashMap<>();

  private GlobalPartAdapter workbenchListener = new GlobalPartAdapter(false)
  {
    @Override
    public void partOpened(IWorkbenchPartReference partRef)
    {
      CDOTopicProvider provider = getTopicProvider(partRef);
      if (provider != null)
      {
        addTopics(provider.getTopics());
        provider.addTopicListener(topicListener);
      }
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef)
    {
      CDOTopicProvider provider = getTopicProvider(partRef);
      if (provider != null)
      {
        provider.removeTopicListener(topicListener);
        removeTopics(provider.getTopics());
      }
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef)
    {
      if (linkWithEditor)
      {
        CDOTopicProvider provider = getTopicProvider(partRef);
        if (provider != null)
        {
          selectTopics(provider.getTopics());
        }
      }
    }
  };

  private final Listener topicListener = new Listener()
  {
    @Override
    public void topicAdded(CDOTopicProvider provider, Topic topic)
    {
      addTopics(topic);
    }

    @Override
    public void topicRemoved(CDOTopicProvider provider, Topic topic)
    {
      removeTopics(topic);
    }
  };

  private final AdapterFactory adapterFactory = new ComposedAdapterFactory();

  private final ItemProvider root = new ItemProvider(adapterFactory);

  private Shell shell;

  private TreeViewer viewer;

  private boolean linkWithEditor = OM.PREF_TOPICS_LINK_WITH_EDITOR.getValue();

  private boolean inEditorActivation;

  private ISelectionChangedListener selectionListener = event -> {
    if (!inEditorActivation)
    {
      ITreeSelection selection = (ITreeSelection)event.getSelection();
      IActionBars bars = getViewSite().getActionBars();
      selectionChanged(bars, selection);
    }
  };

  public CDORemoteTopicsView()
  {
  }

  public Shell getShell()
  {
    return shell;
  }

  public TreeViewer getViewer()
  {
    return viewer;
  }

  @Override
  public void setFocus()
  {
    viewer.getControl().setFocus();
  }

  @Override
  public void dispose()
  {
    workbenchListener.dispose();

    try
    {
      userInfoListener.close();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    super.dispose();
  }

  @Override
  public ISelection getSelection()
  {
    if (viewer != null)
    {
      return viewer.getSelection();
    }

    return StructuredSelection.EMPTY;
  }

  @Override
  public void setSelection(ISelection selection)
  {
    if (viewer != null)
    {
      viewer.setSelection(selection);
    }
  }

  @Override
  public void addSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.addSelectionChangedListener(listener);
    }
  }

  @Override
  public void removeSelectionChangedListener(ISelectionChangedListener listener)
  {
    if (viewer != null)
    {
      viewer.removeSelectionChangedListener(listener);
    }
  }

  @Override
  public void selectReveal(ISelection selection)
  {
    if (viewer != null)
    {
      viewer.setSelection(selection, true);
    }
  }

  @Override
  public final void createPartControl(Composite parent)
  {
    shell = parent.getShell();
    Composite composite = UIUtil.createGridComposite(parent, 1);

    Control control = createUI(composite);
    control.setLayoutData(UIUtil.createGridData());

    hookContextMenu();
    hookDoubleClick();
    contributeToActionBars();

    workbenchListener.register(true);
    getSite().setSelectionProvider(this);
  }

  protected Control createUI(Composite parent)
  {
    ILabelProvider labelProvider = new AdapterFactoryLabelProvider(adapterFactory);
    ((AdapterFactoryLabelProvider)labelProvider).setFireLabelUpdateNotifications(true);

    ILabelDecorator decorator = createLabelDecorator();
    if (decorator != null)
    {
      labelProvider = new DelegatingStyledCellLabelProvider(new DecoratingStyledLabelProvider(labelProvider, decorator));
    }

    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
    viewer.setLabelProvider(labelProvider);
    viewer.setComparator(new ViewerComparator());
    viewer.setInput(root);
    viewer.addSelectionChangedListener(selectionListener);

    userInfoManager = UserInfo.Manager.getInstance();
    userInfoListener = EventUtil.addListener(userInfoManager, UserChangedEvent.class, e -> {
      CDORemoteSession remoteSession = e.getRemoteSession();
      if (remoteSession != null)
      {
        for (Object topic : root.getChildren())
        {
          for (Object child : ((RemoteTopicItem)topic).getChildren())
          {
            RemoteMemberItem member = (RemoteMemberItem)child;
            if (member.getRemoteSession() == remoteSession)
            {
              UIUtil.asyncExec(() -> member.updateText());
            }
          }
        }
      }
    });

    return viewer.getControl();
  }

  protected ILabelDecorator createLabelDecorator()
  {
    return PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  protected void hookDoubleClick()
  {
    viewer.addDoubleClickListener(e -> doubleClicked(((ITreeSelection)viewer.getSelection()).getFirstElement()));
  }

  protected void hookContextMenu()
  {
    MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
    manager.setRemoveAllWhenShown(true);
    manager.addMenuListener(m -> fillContextMenu(m, (ITreeSelection)viewer.getSelection()));

    Menu menu = manager.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getViewSite().registerContextMenu(manager, viewer);
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

  protected void fillLocalToolBar(IToolBarManager manager)
  {
    if (!LOCAL_USER_INFO_HIDE)
    {
      UserInfo localUser = userInfoManager.getLocalUser();

      manager.add(new Action(localUser.getDisplayName(), IAction.AS_PUSH_BUTTON)
      {
        {
          setToolTipText(Messages.getString("CDORemoteTopicsView_0")); //$NON-NLS-1$
          setEnabled(!LOCAL_USER_INFO_DISABLE);
        }

        @Override
        public void run()
        {
          String displayName = localUser.getDisplayName();
          InputDialog dialog = new InputDialog(getShell(), getToolTipText(), Messages.getString("CDORemoteTopicsView_1"), displayName, null); //$NON-NLS-1$
          if (dialog.open() == InputDialog.OK)
          {
            String newDisplayName = dialog.getValue();
            userInfoManager.changeLocalUser(localUser.getFirstName(), localUser.getLastName(), newDisplayName);

            newDisplayName = localUser.getDisplayName(); // Could be different if it was originally empty.
            if (!Objects.equals(newDisplayName, displayName))
            {
              setText(newDisplayName);

              if (manager instanceof ToolBarManager)
              {
                ToolBar toolBar = ((ToolBarManager)manager).getControl();
                toolBar.getParent().pack(true);
              }
            }
          }
        }
      });
    }

    manager.add(new ExpandAllAction(viewer));
    manager.add(new CollapseAllAction(viewer));

    LinkWithEditorAction linkAction = new LinkWithEditorAction()
    {
      @Override
      protected void linkWithEditorChanged(boolean linkWithEditor)
      {
        CDORemoteTopicsView.this.linkWithEditor = linkWithEditor;
        OM.PREF_TOPICS_LINK_WITH_EDITOR.setValue(linkWithEditor);
      }
    };

    linkAction.setChecked(linkWithEditor);
    manager.add(linkAction);
  }

  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
  }

  protected void selectionChanged(IActionBars bars, ITreeSelection selection)
  {
    if (linkWithEditor)
    {
      Object element = selection.getFirstElement();
      if (element instanceof RemoteMemberItem)
      {
        element = ((RemoteMemberItem)element).getParent();
      }

      if (element instanceof RemoteTopicItem)
      {
        RemoteTopicItem remoteTopicItem = (RemoteTopicItem)element;
        Topic topic = remoteTopicItem.getTopic();

        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (!activatePart(topic, activePage.getEditorReferences()))
        {
          activatePart(topic, activePage.getViewReferences());
        }
      }
    }
  }

  protected void doubleClicked(Object object)
  {
    if (object instanceof ContainerItemProvider.ErrorElement)
    {
      try
      {
        UIUtil.getActiveWorkbenchPage().showView(UIUtil.ERROR_LOG_ID);
      }
      catch (PartInitException ex)
      {
        OM.LOG.error(ex);
      }
    }
    else if (object != null && viewer.isExpandable(object))
    {
      if (viewer.getExpandedState(object))
      {
        viewer.collapseToLevel(object, TreeViewer.ALL_LEVELS);
      }
      else
      {
        viewer.expandToLevel(object, 1);
      }
    }
  }

  /**
   * @since 3.1
   */
  protected void refreshPressed()
  {
    UIUtil.refreshViewer(viewer);
  }

  /**
   * @since 3.3
   */
  protected void collapseAllPressed()
  {
    getViewer().collapseAll();
  }

  protected void closeView()
  {
    UIUtil.syncExec(getDisplay(), () -> {
      getSite().getPage().hideView(CDORemoteTopicsView.this);
      dispose();
    });
  }

  protected Display getDisplay()
  {
    Display display = viewer.getControl().getDisplay();
    if (display == null)
    {
      display = UIUtil.getDisplay();
    }

    return display;
  }

  public Topic getTopic(CDORemoteTopic remoteTopic)
  {
    synchronized (remoteTopics)
    {
      RemoteTopicItem item = remoteTopicItems.get(remoteTopic);
      return item == null ? null : item.getTopic();
    }
  }

  protected void addTopics(Topic... topics)
  {
    synchronized (remoteTopics)
    {
      for (Topic topic : topics)
      {
        CDORemoteTopic remoteTopic = remoteTopics.computeIfAbsent(topic, k -> {
          CDOSession session = topic.getSession();
          String id = topic.getId();

          CDORemoteSessionManager remoteSessionManager = session.getRemoteSessionManager();
          return remoteSessionManager.subscribeTopic(id);
        });

        RemoteTopicItem item = remoteTopicItems.computeIfAbsent(remoteTopic, k -> {
          RemoteTopicItem newItem = new RemoteTopicItem(topic, remoteTopic);
          root.getChildren().add(newItem);
          return newItem;
        });

        item.reference();
      }
    }
  }

  protected void removeTopics(Topic... topics)
  {
    synchronized (remoteTopics)
    {
      for (Topic topic : topics)
      {
        CDORemoteTopic remoteTopic = remoteTopics.get(topic);
        if (remoteTopic != null)
        {
          RemoteTopicItem item = remoteTopicItems.get(remoteTopic);
          if (item.dereference() == 0)
          {
            remoteTopic.unsubscribe();

            remoteTopics.remove(topic);
            remoteTopicItems.remove(remoteTopic);
            root.getChildren().remove(item);
          }
        }
      }
    }
  }

  protected void selectTopics(Topic... topics)
  {
    List<RemoteTopicItem> items = new ArrayList<>();

    synchronized (remoteTopics)
    {
      for (Topic topic : topics)
      {
        CDORemoteTopic remoteTopic = remoteTopics.get(topic);
        if (remoteTopic != null)
        {
          RemoteTopicItem item = remoteTopicItems.get(remoteTopic);
          if (item != null)
          {
            items.add(item);
          }
        }
      }
    }

    try
    {
      inEditorActivation = true;

      IStructuredSelection selection = new StructuredSelection(items);
      viewer.setSelection(selection);

      if (EXPAND_SELECTION)
      {
        viewer.setExpandedElements(items.toArray());
      }
    }
    finally
    {
      inEditorActivation = false;
    }
  }

  private static CDOTopicProvider getTopicProvider(IWorkbenchPartReference partRef)
  {
    IWorkbenchPart part = partRef.getPart(true);
    return AdapterUtil.adapt(part, CDOTopicProvider.class);
  }

  private static boolean activatePart(Topic selectedTopic, IWorkbenchPartReference[] partRefs)
  {
    for (IWorkbenchPartReference partRef : partRefs)
    {
      CDOTopicProvider topicProvider = getTopicProvider(partRef);
      if (topicProvider != null)
      {
        for (Topic topic : topicProvider.getTopics())
        {
          if (Objects.equals(topic, selectedTopic))
          {
            partRef.getPage().activate(partRef.getPart(true));
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  private abstract class Item extends ItemProvider implements IAdaptable
  {
    protected Item()
    {
      super(CDORemoteTopicsView.this.adapterFactory);
    }

    /**
     * Work around EMF bug 578508.
     */
    @Override
    public void setText(Object object, String text)
    {
      this.text = text;

      fireNotifyChanged(new ItemProviderNotification(Notification.SET, null, text, Notification.NO_INDEX, false, false, true)
      {
        @Override
        public boolean isLabelUpdate()
        {
          return isLabelUpdate;
        }
      });
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteTopicItem extends Item implements IListener
  {
    private final Topic topic;

    private final CDORemoteTopic remoteTopic;

    private int refCount;

    public RemoteTopicItem(Topic topic, CDORemoteTopic remoteTopic)
    {
      this.topic = topic;
      this.remoteTopic = remoteTopic;

      setImage(topic.getImage());
      setText(topic.getText());

      List<RemoteMemberItem> children = new ArrayList<>();
      for (CDORemoteSession remoteSession : remoteTopic.getRemoteSessions())
      {
        RemoteMemberItem item = new RemoteMemberItem(remoteSession);
        children.add(item);
      }

      getChildren().addAll(children);
      remoteTopic.addListener(this);
    }

    public Topic getTopic()
    {
      return topic;
    }

    @Override
    public <T> T getAdapter(Class<T> type)
    {
      T adapter = AdapterUtil.adapt(this, type, false);
      if (adapter != null)
      {
        return adapter;
      }

      return topic.getAdapter(type);
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == Kind.DEACTIVATED)
        {
          dispose();
        }
      }
      else if (event instanceof IContainerEvent)
      {
        @SuppressWarnings("unchecked")
        IContainerEvent<CDORemoteSession> e = (IContainerEvent<CDORemoteSession>)event;

        UIUtil.asyncExec(() -> e.accept(new IContainerEventVisitor<>()
        {
          @Override
          public void added(CDORemoteSession remoteSession)
          {
            RemoteMemberItem item = new RemoteMemberItem(remoteSession);
            getChildren().add(item);
          }

          @Override
          public void removed(CDORemoteSession remoteSession)
          {
            for (Iterator<Object> it = getChildren().iterator(); it.hasNext();)
            {
              RemoteMemberItem item = (RemoteMemberItem)it.next();
              if (item.getRemoteSession() == remoteSession)
              {
                it.remove();
                break;
              }
            }
          }
        }));
      }
    }

    @Override
    public void dispose()
    {
      remoteTopic.removeListener(this);
    }

    private void reference()
    {
      ++refCount;
    }

    private int dereference()
    {
      return --refCount;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteMemberItem extends Item
  {
    private final CDORemoteSession remoteSession;

    public RemoteMemberItem(CDORemoteSession remoteSession)
    {
      this.remoteSession = remoteSession;

      setImage(SharedIcons.getImage(SharedIcons.OBJ_PERSON));
      updateText();
    }

    public void updateText()
    {
      UserInfo userInfo = userInfoManager.getRemoteUser(remoteSession);
      setText(userInfo.getDisplayName());
    }

    public CDORemoteSession getRemoteSession()
    {
      return remoteSession;
    }

    @Override
    public <T> T getAdapter(Class<T> type)
    {
      return AdapterUtil.adapt(this, type, false);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DecoratingStyledLabelProvider extends DecoratingLabelProvider implements IStyledLabelProvider
  {
    public DecoratingStyledLabelProvider(ILabelProvider provider, ILabelDecorator decorator)
    {
      super(provider, decorator);
    }

    @Override
    public StyledString getStyledText(Object element)
    {
      ILabelProvider provider = getLabelProvider();
      if (provider instanceof IStyledLabelProvider)
      {
        return ((IStyledLabelProvider)provider).getStyledText(element);
      }

      return new StyledString(getText(element));
    }
  }
}
