/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.internal.ui.CDOAuthorCache;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.chat.ChatComposite;
import org.eclipse.net4j.util.ui.chat.ChatMessage;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;
import org.eclipse.net4j.util.ui.chat.ChatRenderer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.widgets.EntryControlAdvisor;

import org.eclipse.emf.common.util.URI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.FillLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionsView extends ContainerView.Default<CDORemoteSessionManager>
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDORemoteSessionsView"; //$NON-NLS-1$

  private static final String TYPE_CHAT_MESSAGE = "org.eclipse.emf.cdo.ui.ChatMessage"; //$NON-NLS-1$

  private static final String CHAT_RENDERER_TYPE = OMPlatform.INSTANCE.getProperty("CHAT_RENDERER_TYPE", "mylyn");

  private static final String CHAT_RENDERER_DESCRIPTION = OMPlatform.INSTANCE.getProperty("CHAT_RENDERER_DESCRIPTION", "Markdown");

  private static final String ENTRY_CONTROL_ADVISOR_TYPE = OMPlatform.INSTANCE.getProperty("ENTRY_CONTROL_ADVISOR_TYPE", "mylyn");

  private static final String ENTRY_CONTROL_ADVISOR_DESCRIPTION = OMPlatform.INSTANCE.getProperty("ENTRY_CONTROL_ADVISOR_DESCRIPTION", "Markdown");

  private static final AtomicInteger CHAT_MESSAGE_IDS = new AtomicInteger();

  private static final ChatMessage[] NO_MESSAGES = {};

  private final ISelectionListener selectionListener = new ISelectionListener()
  {
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
      if (part != CDORemoteSessionsView.this)
      {
        handleWindowSelection(selection);
      }
    }
  };

  private final IListener containerListener = new CDORemoteSessionManager.EventAdapter()
  {
    @Override
    protected void onLocalSubscriptionChanged(boolean subscribed)
    {
      getViewer().getControl().setEnabled(subscribed);
    }

    @Override
    protected void onSubscribed(CDORemoteSession remoteSession)
    {
      subscriptionChanged(remoteSession);
    }

    @Override
    protected void onUnsubscribed(CDORemoteSession remoteSession)
    {
      subscriptionChanged(remoteSession);
    }

    @Override
    protected void onMessageReceived(CDORemoteSession remoteSession, CDORemoteSessionMessage message)
    {
      if (TYPE_CHAT_MESSAGE.equals(message.getType()))
      {
        String content = new String(message.getData(), StandardCharsets.UTF_8);
        addMessage(remoteSession, remoteSession.getUserID(), content);
      }
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      setContainer(null);
    }

    private void subscriptionChanged(CDORemoteSession remoteSession)
    {
      if (showUnsubscribed)
      {
        refreshElement(remoteSession, true);
      }
      else
      {
        refreshUserViewer();
      }
    }
  };

  private final Map<CDORemoteSession, List<ChatMessage>> chatMessages = new HashMap<>();

  private final Set<CDORemoteSession> unreadChats = Collections.synchronizedSet(new HashSet<>());

  private CDOAuthorCache authorCache;

  private ChatComposite chatComposite;

  private CDORemoteSession selectedRemoteSession;

  private boolean showUnsubscribed = OM.PREF_COLLABORATION_SHOW_UNSUBSCRIBED.getValue();

  public CDORemoteSessionsView()
  {
  }

  @Override
  public void dispose()
  {
    getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);
    super.dispose();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new ContainerViewItemProvider(null)
    {
      @Override
      protected Node createNode(Node parent, Object element)
      {
        if (!showUnsubscribed && element instanceof CDORemoteSession)
        {
          CDORemoteSession remoteSession = (CDORemoteSession)element;
          if (!remoteSession.isSubscribed())
          {
            return null;
          }
        }

        return super.createNode(parent, element);
      }
    };
  }

  @Override
  protected Control createUI(Composite parent)
  {
    SashForm ui = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);

    // Left (remote sessions).
    super.createUI(ui);

    // Right (chat).
    Composite composite = new Composite(ui, SWT.NONE);
    composite.setLayout(FillLayoutFactory.fillDefaults().margins(5, 5).create());
    composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    chatComposite = createChatComposite(composite);

    // Handle window selection.
    ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
    handleWindowSelection(selectionService.getSelection());
    selectionService.addPostSelectionListener(selectionListener);

    updateChat();

    ui.setWeights(25, 75);
    return ui;
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    // Don't call super.
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(new ShowUnsubscribedAction());
    manager.add(new Separator());
    super.fillLocalPullDown(manager);
  }

  @Override
  protected IListener getContainerListener()
  {
    return containerListener;
  }

  @Override
  protected Image getElementImage(Object element)
  {
    if (element instanceof CDORemoteSession)
    {
      Image image = SharedIcons.getImage(SharedIcons.OBJ_PERSON);

      if (unreadChats.contains(element))
      {
        image = OM.getOverlayImage(image, URI.createPlatformPluginURI("/org.eclipse.emf.cdo.ui.shared/icons/full/ovr16/unread.gif", false), 8, 8);
      }

      return image;
    }

    return super.getElementImage(element);
  }

  @Override
  protected String getElementText(Object element)
  {
    if (element instanceof CDORemoteSession)
    {
      CDORemoteSession remoteSession = (CDORemoteSession)element;
      String userID = remoteSession.getUserID();

      if (authorCache != null)
      {
        Author author = authorCache.getAuthor(userID);
        if (author != null)
        {
          return author.getFullName();
        }
      }

      return userID;
    }

    return super.getElementText(element);
  }

  @Override
  protected Color getElementForeground(Object element)
  {
    if (element instanceof CDORemoteSession)
    {
      CDORemoteSession remoteSession = (CDORemoteSession)element;
      if (!remoteSession.isSubscribed())
      {
        return getDisplay().getSystemColor(SWT.COLOR_GRAY);
      }
    }

    return super.getElementForeground(element);
  }

  @Override
  protected void selectionChanged(IActionBars bars, ITreeSelection selection)
  {
    CDORemoteSession newRemoteSession = getSubscribedRemoteSession(selection);
    if (!Objects.equals(newRemoteSession, selectedRemoteSession))
    {
      selectedRemoteSession = newRemoteSession;
      updateChat();
    }
  }

  private ChatComposite createChatComposite(Composite parent)
  {
    Display display = parent.getDisplay();
    Color entryBackgroundColor = new Color(display, 241, 241, 241);
    parent.addDisposeListener(e -> entryBackgroundColor.dispose());

    ChatRenderer renderer = IPluginContainer.INSTANCE.getElementOrNull( //
        ChatRenderer.Factory.PRODUCT_GROUP, //
        CHAT_RENDERER_TYPE, //
        CHAT_RENDERER_DESCRIPTION);

    EntryControlAdvisor entryControlAdvisor = IPluginContainer.INSTANCE.getElementOrNull( //
        EntryControlAdvisor.Factory.PRODUCT_GROUP, //
        ENTRY_CONTROL_ADVISOR_TYPE, //
        ENTRY_CONTROL_ADVISOR_DESCRIPTION);

    ChatComposite.Config config = new ChatComposite.Config();
    config.setMessageProvider(this::computeMessages);
    config.setChatRenderer(renderer);
    config.setEntryBackgroundColor(entryBackgroundColor);
    config.setEntryControlAdvisor(entryControlAdvisor);
    config.setSendHandler(this::sendMessage);

    return new ChatComposite(parent, SWT.NONE, config);
  }

  private ChatMessage[] computeMessages()
  {
    synchronized (chatMessages)
    {
      List<ChatMessage> messages = chatMessages.get(selectedRemoteSession);
      if (ObjectUtil.isEmpty(messages))
      {
        return NO_MESSAGES;
      }

      return messages.toArray(new ChatMessage[messages.size()]);
    }
  }

  private void addMessage(CDORemoteSession remoteSession, String userID, String content)
  {
    ChatMessage chatMessage = new ChatMessage()
    {
      @Override
      public int getID()
      {
        return CHAT_MESSAGE_IDS.incrementAndGet();
      }

      @Override
      public Author getAuthor()
      {
        return authorCache.getAuthor(userID);
      }

      @Override
      public long getCreationTime()
      {
        return System.currentTimeMillis();
      }

      @Override
      public long getEditTime()
      {
        return getCreationTime();
      }

      @Override
      public String getContent()
      {
        return content;
      }

      @Override
      public ChatMessage getReplyTo()
      {
        return null;
      }
    };

    List<ChatMessage> list;
    synchronized (chatMessages)
    {
      list = chatMessages.computeIfAbsent(remoteSession, k -> new ArrayList<ChatMessage>());
    }

    list.add(chatMessage);

    if (remoteSession == selectedRemoteSession)
    {
      UIUtil.asyncExec(() -> chatComposite.refreshMessageBrowser());
    }
    else
    {
      unreadChats.add(remoteSession);
      UIUtil.asyncExec(() -> refreshElement(remoteSession, true));
    }
  }

  private void sendMessage(String message)
  {
    IStructuredSelection selection = (IStructuredSelection)getSelection();
    CDORemoteSession remoteSession = (CDORemoteSession)selection.getFirstElement();
    remoteSession.sendMessage(new CDORemoteSessionMessage(TYPE_CHAT_MESSAGE, message.getBytes(StandardCharsets.UTF_8)));

    addMessage(remoteSession, getLocalUserID(), message);
  }

  private void updateChat()
  {
    if (selectedRemoteSession != null && unreadChats.remove(selectedRemoteSession))
    {
      refreshElement(selectedRemoteSession, true);
    }

    chatComposite.refreshMessageBrowser();
    chatComposite.setVisible(selectedRemoteSession != null);
    chatComposite.setOwnUserID(getLocalUserID());
  }

  private String getLocalUserID()
  {
    CDORemoteSessionManager container = getContainer();
    if (container == null)
    {
      return null;
    }

    return container.getLocalSession().getUserID();
  }

  private void refreshUserViewer()
  {
    getItemProvider().clearNodesCache();
    refreshViewer(true);
  }

  private void handleWindowSelection(ISelection selection)
  {
    Object object = UIUtil.getElementIfOne(selection);
    CDOSession session = CDOUtil.getSession(object);
    if (session != null)
    {
      CDORemoteSessionManager oldContainer = getContainer();
      CDORemoteSessionManager newContainer = session.getRemoteSessionManager();
      if (newContainer != oldContainer)
      {
        authorCache = CDOAuthorCache.of(newContainer.getLocalSession());
        setContainer(newContainer);
      }
    }
  }

  private CDORemoteSession getSubscribedRemoteSession(IStructuredSelection selection)
  {
    Object element = selection.getFirstElement();
    if (element instanceof CDORemoteSession)
    {
      CDORemoteSession remoteSession = (CDORemoteSession)element;
      if (remoteSession.isSubscribed())
      {
        return remoteSession;
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private final class ShowUnsubscribedAction extends Action
  {
    public ShowUnsubscribedAction()
    {
      super("Show Unsubscribed Users", AS_CHECK_BOX);
      setChecked(showUnsubscribed);
    }

    @Override
    public void run()
    {
      showUnsubscribed = !showUnsubscribed;
      OM.PREF_COLLABORATION_SHOW_UNSUBSCRIBED.setValue(showUnsubscribed);
      refreshUserViewer();
    }
  }
}
