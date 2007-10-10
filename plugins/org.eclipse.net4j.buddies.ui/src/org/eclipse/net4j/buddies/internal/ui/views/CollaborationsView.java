package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.buddies.ui.IBuddiesManager;
import org.eclipse.net4j.buddies.ui.IBuddiesManagerStateChangedEvent;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Tree;

public class CollaborationsView extends ContainerView implements IListener
{
  private static final int LIMIT = 10;

  private static final int PERCENT = 30;

  private static CollaborationsView INSTANCE;

  private IBuddySession session;

  private Sash sash;

  private Control leftControl;

  private Tree rightControl;

  public CollaborationsView()
  {
  }

  public static synchronized CollaborationsView getINSTANCE()
  {
    return INSTANCE;
  }

  @Override
  public synchronized void dispose()
  {
    INSTANCE = null;
    IBuddiesManager.INSTANCE.removeListener(this);
    session = null;
    super.dispose();
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof IBuddiesManagerStateChangedEvent)
    {
      queryBuddiesManager();
      updateState();
    }
    else if (event instanceof IBuddyStateChangedEvent)
    {
      if (session != null && event.getSource() == session.getSelf())
      {
        updateState();
      }
    }
  }

  @Override
  protected synchronized Control createUI(final Composite parent)
  {
    final FormLayout form = new FormLayout();
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(form);

    leftControl = super.createUI(composite);
    sash = new Sash(composite, SWT.VERTICAL);
    rightControl = new Tree(composite, SWT.NONE);

    FormData leftControlData = new FormData();
    leftControlData.left = new FormAttachment(0, 0);
    leftControlData.right = new FormAttachment(sash, 0);
    leftControlData.top = new FormAttachment(0, 0);
    leftControlData.bottom = new FormAttachment(100, 0);
    leftControl.setLayoutData(leftControlData);

    final FormData sashData = new FormData();
    sashData.left = new FormAttachment(PERCENT, 0);
    sashData.top = new FormAttachment(0, 0);
    sashData.bottom = new FormAttachment(100, 0);
    sash.setLayoutData(sashData);
    sash.addListener(SWT.Selection, new Listener()
    {
      public void handleEvent(Event e)
      {
        Rectangle sashRect = sash.getBounds();
        Rectangle shellRect = composite.getClientArea();
        int right = shellRect.width - sashRect.width - LIMIT;
        e.x = Math.max(Math.min(e.x, right), LIMIT);
        if (e.x != sashRect.x)
        {
          sashData.left = new FormAttachment(0, e.x);
          composite.layout();
        }
      }
    });

    FormData rightControlData = new FormData();
    rightControlData.left = new FormAttachment(sash, 0);
    rightControlData.right = new FormAttachment(100, 0);
    rightControlData.top = new FormAttachment(0, 0);
    rightControlData.bottom = new FormAttachment(100, 0);
    rightControl.setLayoutData(rightControlData);

    queryBuddiesManager();
    IBuddiesManager.INSTANCE.addListener(this);
    INSTANCE = this;
    updateState();
    return composite;
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return session != null ? session.getSelf() : ContainerUtil.emptyContainer();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new BuddiesItemProvider();
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    super.fillLocalPullDown(manager);
  }

  protected void queryBuddiesManager()
  {
    IBuddySession oldSession = session;
    session = IBuddiesManager.INSTANCE.getSession();
    if (oldSession != null && oldSession != session)
    {
      oldSession.removeListener(this);
      oldSession.getSelf().removeListener(this);
    }

    if (session != null && session != oldSession)
    {
      session.addListener(this);
      session.getSelf().addListener(this);
    }
  }

  protected void updateState()
  {
  }
}