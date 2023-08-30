/*
 * Copyright (c) 2015, 2016, 2019-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistryPopulator;
import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager.RepositoryConnectionEvent;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;
import org.eclipse.emf.cdo.internal.explorer.repositories.CDORepositoryImpl;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.internal.ui.views.CDOTimeMachineView;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.internal.db.DBBrowserPage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.internal.team.history.CDOHistoryAdapterFactory;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.MenuFiller;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.views.IViewDescriptor;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class ShowInActionProvider extends AbstractActionProvider<Object>
{
  public static final String TITLE = "Show In";

  public static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

  public static final String HISTORY_VIEW_ID = "org.eclipse.team.ui.GenericHistoryView";

  public static final String ID = "org.eclipse.emf.cdo.explorer.ui.checkouts.ShowInActions";

  private static final boolean PROPERTIES_SUPPORT_AVAILABLE = Support.UI_PROPERTIES.isAvailable();

  private static final boolean HISTORY_SUPPORT_AVAILABLE = Support.UI_HISTORY.isAvailable();

  private static final String DASHBOARD_KEY = CDOCheckoutDashboard.class.getName();

  public ShowInActionProvider()
  {
    super(Object.class, ID, TITLE, ICommonMenuConstants.GROUP_OPEN);
  }

  @Override
  public void fillActionBars(IActionBars actionBars)
  {
    super.fillActionBars(actionBars);

    int dashBoardHeight = OM.PREF_DASHBOARD_HEIGHT.getValue();
    if (dashBoardHeight >= 0)
    {
      // showDashboard(getViewer(), getViewSite().getPage());
    }
  }

  @Override
  protected boolean fillSubMenu(ICommonViewerWorkbenchSite viewSite, IMenuManager subMenu, Object selectedElement)
  {
    IWorkbenchPage page = viewSite.getPage();
    StructuredViewer viewer = getViewer();
    return fillMenu(page, viewer, subMenu, selectedElement);
  }

  public static boolean fillMenu(IWorkbenchPage page, StructuredViewer viewer, IMenuManager menu, Object selectedElement)
  {
    boolean filled = false;

    if (selectedElement instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)selectedElement;
      if (repository.isConnected())
      {
        if (repository.isLocal())
        {
          LocalCDORepository localRepository = (LocalCDORepository)repository;

          IManagedContainer container = localRepository.getContainer();
          menu.add(new ShowInServerBrowserAction(localRepository, container));
          filled = true;
        }

        filled |= addAction(menu, repository, new ShowInSessionsViewAction(page, repository, null));

        if (PROPERTIES_SUPPORT_AVAILABLE)
        {
          filled |= addAction(menu, repository, new ShowInViewAction(page, PROPERTIES_VIEW_ID));
        }

        if (HISTORY_SUPPORT_AVAILABLE)
        {
          filled |= addAction(menu, repository.getSession(), new ShowInHistoryAction(page, repository.getSession()));
        }
      }
      else
      {
        filled |= addAction(menu, repository, new ShowInSessionsViewAction(page, repository, null));
      }

      CDOCheckout[] checkouts = repository.getCheckouts();
      if (checkouts.length != 0)
      {
        filled |= addAction(menu, repository, new ShowInProjectExplorerAction(page, checkouts));
      }
    }

    if (selectedElement instanceof CDOBranch)
    {
      if (HISTORY_SUPPORT_AVAILABLE)
      {
        filled |= addAction(menu, selectedElement, new ShowInHistoryAction(page, selectedElement));
      }
    }

    if (selectedElement instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)selectedElement;
      if (checkout.isOpen())
      {
        // TODO
        // if (viewer != null)
        // {
        // menu.add(new ShowInDashboardAction(viewer, page));
        // filled = true;
        // }

        if (checkout.isOffline())
        {
          OfflineCDOCheckout offlineCheckout = (OfflineCDOCheckout)checkout;

          InternalCDOWorkspace workspace = offlineCheckout.getWorkspace();
          if (workspace != null)
          {
            IManagedContainer container = workspace.getContainer();
            menu.add(new ShowInServerBrowserAction(offlineCheckout, container));
            filled = true;
          }
        }
        else
        {
          filled |= addAction(menu, checkout, new ShowInSessionsViewAction(page, checkout.getRepository(), checkout));
        }

        if (checkout.isReadOnly())
        {
          filled |= addAction(menu, checkout, new ShowInViewAction(page, CDOTimeMachineView.ID));
        }

        if (PROPERTIES_SUPPORT_AVAILABLE)
        {
          filled |= addAction(menu, checkout, new ShowInViewAction(page, PROPERTIES_VIEW_ID));
        }

        if (HISTORY_SUPPORT_AVAILABLE)
        {
          filled |= addAction(menu, checkout.getView(), new ShowInHistoryAction(page, checkout.getView()));
        }
      }
    }

    if (selectedElement instanceof EObject)
    {
      EObject eObject = (EObject)selectedElement;
      if (CDOExplorerUtil.getCheckout(eObject) != null)
      {
        if (PROPERTIES_SUPPORT_AVAILABLE)
        {
          filled |= addAction(menu, eObject, new ShowInViewAction(page, PROPERTIES_VIEW_ID));
        }

        if (HISTORY_SUPPORT_AVAILABLE)
        {
          filled |= addAction(menu, eObject, new ShowInHistoryAction(page, eObject));
        }
      }
    }

    if (selectedElement instanceof AbstractElement)
    {
      AbstractElement element = (AbstractElement)selectedElement;
      menu.add(new ShowInSystemExplorerAction(element.getFolder()));
      filled = true;
    }

    menu.add(new Separator(ICommonMenuConstants.GROUP_ADDITIONS));

    boolean[] finalFilled = { filled };
    IPluginContainer.INSTANCE.forEachElement(MenuFiller.Factory.PRODUCT_GROUP, MenuFiller.class,
        filler -> finalFilled[0] |= filler.fillMenu(page, viewer, menu, selectedElement));

    return finalFilled[0];
  }

  private static boolean addAction(IMenuManager subMenu, Object selectedElement, ShowInViewAction action)
  {
    action.selectionChanged(selectedElement);
    if (action.isEnabled())
    {
      subMenu.add(action);
      return true;
    }

    return false;
  }

  public static void showDashboard(StructuredViewer viewer, ISelectionService selectionService)
  {
    CDOCheckoutDashboard[] dashboard = { (CDOCheckoutDashboard)viewer.getData(DASHBOARD_KEY) };
    if (dashboard[0] == null)
    {
      Control control = viewer.getControl();
      Object controlLayoutData = control.getLayoutData();

      Composite parent = control.getParent();
      Layout parentLayout = parent.getLayout();

      int[] minimumHeight = { 0 };

      GridLayout layout = new GridLayout(1, false);
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      layout.verticalSpacing = 0;

      parent.setLayout(layout);
      control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Sash sash = new Sash(parent, SWT.HORIZONTAL | SWT.SMOOTH);
      sash.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      sash.addListener(SWT.Selection, new Listener()
      {
        @Override
        public void handleEvent(Event e)
        {
          int dashBoardHeight = parent.getBounds().height - e.y;
          if (dashBoardHeight < minimumHeight[0])
          {
            e.doit = false;
          }

          dashBoardHeight = Math.max(dashBoardHeight, minimumHeight[0]);
          OM.PREF_DASHBOARD_HEIGHT.setValue(dashBoardHeight);

          GridData gridData = (GridData)dashboard[0].getLayoutData();
          gridData.heightHint = dashBoardHeight;

          parent.layout();
        }
      });

      GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);

      dashboard[0] = new CDOCheckoutDashboard(parent, selectionService);
      dashboard[0].setLayoutData(gridData);
      dashboard[0].addDisposeListener(new DisposeListener()
      {
        @Override
        public void widgetDisposed(DisposeEvent e)
        {
          viewer.setData(DASHBOARD_KEY, null);
          if (!control.isDisposed())
          {
            sash.dispose();

            control.setLayoutData(controlLayoutData);
            parent.setLayout(parentLayout);

            parent.getDisplay().asyncExec(new Runnable()
            {
              @Override
              public void run()
              {
                parent.layout();
              }
            });
          }
        }
      });

      viewer.setData(DASHBOARD_KEY, dashboard[0]);
      parent.layout();

      minimumHeight[0] = dashboard[0].getBounds().height;

      int dashBoardHeight = OM.PREF_DASHBOARD_HEIGHT.getValue();
      if (dashBoardHeight != 0)
      {
        gridData.heightHint = Math.abs(dashBoardHeight);
        parent.layout();
        OM.PREF_DASHBOARD_HEIGHT.setValue(gridData.heightHint);
      }
      else
      {
        OM.PREF_DASHBOARD_HEIGHT.setValue(minimumHeight[0]);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ShowInServerBrowserAction extends Action
  {
    private static final String PRODUCT_GROUP = CDOServerBrowser.ContainerBased.Factory.PRODUCT_GROUP;

    private static final String TYPE = CDOServerBrowser.ContainerBased.Factory.TYPE;

    private final CDOExplorerElement element;

    private final IManagedContainer container;

    public ShowInServerBrowserAction(CDOExplorerElement element, IManagedContainer container)
    {
      this.element = element;
      this.container = container;

      setText("CDO Server Browser");
      setImageDescriptor(OM.getImageDescriptor("icons/web.gif"));
      setToolTipText("Show this element in a CDO server browser");
    }

    @Override
    public void run()
    {
      container.registerFactory(new CDOServerBrowser.ContainerBased.Factory(container));
      container.registerFactory(new DBBrowserPage.Tables.Factory());
      container.registerFactory(new DBBrowserPage.Queries.Factory());

      String description = element.getType() + "-checkout-" + element.getID();

      int serverBrowserPort = ((AbstractElement)element).getServerBrowserPort();
      if (serverBrowserPort != 0)
      {
        description = Integer.toString(serverBrowserPort) + ":" + description;
      }

      CDOServerBrowser browser = (CDOServerBrowser)container.getElement(PRODUCT_GROUP, TYPE, description);

      if (browser != null && browser.isActive())
      {
        int port = browser.getPort();
        if (serverBrowserPort == 0)
        {
          ((AbstractElement)element).setServerBrowserPort(port);
        }

        IOUtil.openSystemBrowser("http://localhost:" + port);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("unused")
  private static final class ShowInDashboardAction extends Action
  {
    private final StructuredViewer viewer;

    private final ISelectionService selectionService;

    public ShowInDashboardAction(StructuredViewer viewer, ISelectionService selectionService)
    {
      this.viewer = viewer;
      this.selectionService = selectionService;

      setText("CDO Dashboard");
      setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR));
      setToolTipText("Show this element in the CDO dashboard");
    }

    @Override
    public void run()
    {
      showDashboard(viewer, selectionService);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ShowInViewAction extends Action
  {
    private final IWorkbenchPage page;

    private final IViewDescriptor viewDescriptor;

    private Object element;

    public ShowInViewAction(IWorkbenchPage page, String viewID)
    {
      this.page = page;

      viewDescriptor = PlatformUI.getWorkbench().getViewRegistry().find(viewID);
      if (viewDescriptor != null)
      {
        setText(viewDescriptor.getLabel());
        setImageDescriptor(viewDescriptor.getImageDescriptor());
        setToolTipText("Show this element in " + viewDescriptor.getLabel());
      }
    }

    public void selectionChanged(Object selectedElement)
    {
      element = selectedElement;
      setEnabled(viewDescriptor != null && element != null);
    }

    @Override
    public final void run()
    {
      try
      {
        IViewPart viewPart = page.showView(viewDescriptor.getId());
        run(viewPart);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    protected void run(IViewPart viewPart) throws Exception
    {
      // Do nothing.
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ShowInHistoryAction extends ShowInViewAction
  {
    private final Object selectedElement;

    public ShowInHistoryAction(IWorkbenchPage page, Object selectedElement)
    {
      super(page, HISTORY_VIEW_ID);
      this.selectedElement = selectedElement;
    }

    @Override
    protected void run(IViewPart viewPart) throws Exception
    {
      if (selectedElement != null && HISTORY_SUPPORT_AVAILABLE)
      {
        CDOHistoryAdapterFactory.load();
        ((IHistoryView)viewPart).showHistoryFor(selectedElement);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ShowInProjectExplorerAction extends ShowInViewAction
  {
    private final CDOCheckout[] checkouts;

    public ShowInProjectExplorerAction(IWorkbenchPage page, CDOCheckout[] checkouts)
    {
      super(page, CDOCheckoutContentProvider.PROJECT_EXPLORER_ID);
      this.checkouts = checkouts;
    }

    @Override
    protected void run(IViewPart viewPart) throws Exception
    {
      CDOCheckoutContentProvider checkoutContentProvider = CDOCheckoutContentProvider.getInstance(CDOCheckoutContentProvider.PROJECT_EXPLORER_ID);
      if (checkoutContentProvider != null)
      {
        checkoutContentProvider.selectObjects((Object[])checkouts);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ShowInSessionsViewAction extends ShowInViewAction
  {
    private final CDORepository repository;

    private final CDOCheckout checkout;

    public ShowInSessionsViewAction(IWorkbenchPage page, CDORepository repository, CDOCheckout checkout)
    {
      super(page, CDOSessionsView.ID);
      this.repository = repository;
      this.checkout = checkout;
    }

    @Override
    protected void run(IViewPart viewPart) throws Exception
    {
      Object select = show();
      if (select != null)
      {
        TreeViewer viewer = ((CDOSessionsView)viewPart).getViewer();
        viewer.getControl().getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              viewer.setSelection(new StructuredSelection(select), true);
            }
            catch (Exception ex)
            {
              //$FALL-THROUGH$
            }
          }
        });
      }
    }

    private Object show()
    {
      CDOSession session = getSession();
      if (session != null)
      {
        CDOPackageRegistryPopulator.populate(session.getPackageRegistry());

        if (checkout != null)
        {
          CDOView checkoutView = checkout.getView();
          if (!checkout.isReadOnly())
          {
            CDOBranch branch = checkoutView.getBranch();
            for (CDOTransaction transaction : session.getTransactions())
            {
              if (branch.getID() == transaction.getBranch().getID())
              {
                return transaction;
              }
            }

            CDOTransaction transaction = session.openTransaction(branch);
            CDOUtil.configureView(transaction);
            return transaction;
          }

          CDOBranchPoint branchPoint = CDOBranchUtil.copyBranchPoint(checkoutView);
          for (CDOView view : session.getViews())
          {
            if (!(view instanceof CDOTransaction) && branchPoint.equals(view))
            {
              return view;
            }
          }

          CDOView view = session.openView(checkoutView);
          CDOUtil.configureView(view);
          return view;
        }
      }

      return session;
    }

    private CDOSession getSession()
    {
      repository.connect();

      String factoryType = "cdo-explorer";
      String description = repository.getID();

      try
      {
        return (CDOSession)IPluginContainer.INSTANCE.getElement(CDOSessionFactory.PRODUCT_GROUP, factoryType, description);
      }
      catch (FactoryNotFoundException | ProductCreationException ex)
      {
        CDOSession session = ((CDORepositoryImpl)repository).openSession();

        IListener listener = new ContainerEventAdapter<Object>()
        {
          @Override
          protected void onRemoved(IContainer<Object> container, Object element)
          {
            if (element == repository)
            {
              dispose(session);
            }
          }

          @Override
          protected void notifyOtherEvent(IEvent event)
          {
            if (event instanceof RepositoryConnectionEvent)
            {
              RepositoryConnectionEvent e = (RepositoryConnectionEvent)event;
              if (e.getRepository() == repository)
              {
                dispose(session);
              }
            }
            else if (event instanceof LifecycleEvent)
            {
              LifecycleEvent e = (LifecycleEvent)event;
              if (e.getSource() == session && e.getKind() == LifecycleEvent.Kind.DEACTIVATED)
              {
                dispose(session);
              }
            }
          }

          private void dispose(CDOSession session)
          {
            session.removeListener(this);
            CDOExplorerUtil.getRepositoryManager().removeListener(this);
            LifecycleUtil.deactivate(session);
          }
        };

        session.addListener(listener);
        CDOExplorerUtil.getRepositoryManager().addListener(listener);

        IPluginContainer.INSTANCE.putElement(CDOSessionFactory.PRODUCT_GROUP, factoryType, description, session);

        return session;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ShowInSystemExplorerAction extends Action
  {
    private final File folder;

    public ShowInSystemExplorerAction(File folder)
    {
      this.folder = folder;

      setText("System Explorer");
      setImageDescriptor(OM.getImageDescriptor("icons/system_explorer.gif"));
      setToolTipText("Show the folder of this element in the system explorer");
    }

    @Override
    public void run()
    {
      IOUtil.openSystemBrowser(folder.toURI().toString());
    }
  }
}
