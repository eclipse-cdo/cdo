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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
    super.dispose();
  }

  @Override
  protected synchronized Control createUI(final Composite parent)
  {
    GridLayout grid = new GridLayout(3, false);
    grid.marginWidth = 0;
    grid.marginHeight = 0;
    grid.horizontalSpacing = 0;

    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(grid);

    leftControl = super.createUI(composite);
    leftControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    sash = new Sash(composite, SWT.VERTICAL);
    sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    sash.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        sash.setBounds(e.x, e.y, e.width, e.height);
        composite.layout(true);

        // if (event.detail == SWT.DRAG)
        // {
        // return;
        // }
        //
        // int shift = event.x - sash.getBounds().x;
        // GridData data = (GridData)rightControl.getLayoutData();
        // int newWidthHint = data.widthHint + shift;
        // if (newWidthHint < 20)
        // {
        // return;
        // }
        //
        // Point computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        // Point currentSize = getShell().getSize();
        //
        // // if the dialog wasn't of a custom size we know we can shrink
        // // it if necessary based on sash movement.
        // boolean customSize = !computedSize.equals(currentSize);
        // data.widthHint = newWidthHint;
        // composite.layout(true);
        //
        // // recompute based on new widget size
        // computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        //
        // // if the dialog was of a custom size then increase it only if
        // // necessary.
        // if (customSize)
        // {
        // computedSize.x = Math.max(computedSize.x, currentSize.x);
        // }
        //
        // computedSize.y = Math.max(computedSize.y, currentSize.y);
        // if (computedSize.equals(currentSize))
        // {
        // return;
        // }
        //
        // compo(computedSize.x, computedSize.y);
        // lastShellSize = getShell().getSize();
      }
    });

    rightControl = new Tree(composite, SWT.NONE);
    rightControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

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