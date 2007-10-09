package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Tree;

public class CollaborationsView extends ContainerView implements IListener
{
  private static CollaborationsView INSTANCE;

  private Sash sash;

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
    super.dispose();
  }

  @Override
  protected synchronized Control createUI(Composite parent)
  {
    GridLayout grid = new GridLayout(3, false);
    grid.marginWidth = 0;
    grid.marginHeight = 0;
    grid.horizontalSpacing = 0;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(grid);

    Control control = super.createUI(composite);
    control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    sash = new Sash(composite, SWT.VERTICAL);
    sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));

    Tree pane = new Tree(composite, SWT.NONE);
    pane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    INSTANCE = this;
    return composite;
  }

  public void notifyEvent(IEvent event)
  {
  }

  @Override
  protected IContainer<?> getContainer()
  {
    BuddiesView buddiesView = BuddiesView.getINSTANCE();
    IBuddySession session = buddiesView == null ? null : buddiesView.getSession();
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

  protected void updateState()
  {
  }
}