/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;
import org.eclipse.emf.cdo.internal.explorer.repositories.LocalCDORepository;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.internal.db.DBBrowserPage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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

  private static final String HISTORY_VIEW_ID = "org.eclipse.team.ui.GenericHistoryView";

  public ShowInActionProvider()
  {
    super(Object.class, ICommonMenuConstants.GROUP_OPEN_WITH, TITLE, ICommonMenuConstants.GROUP_OPEN_WITH);
  }

  @Override
  protected boolean fillSubMenu(ICommonViewerWorkbenchSite viewSite, IMenuManager subMenu, Object selectedElement)
  {
    IWorkbenchPage page = viewSite.getPage();
    return fillMenu(page, subMenu, selectedElement);
  }

  public static boolean fillMenu(IWorkbenchPage page, IMenuManager menu, Object selectedElement)
  {
    boolean filled = false;

    if (selectedElement instanceof CDORepository)
    {
      final CDORepository repository = (CDORepository)selectedElement;
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
        filled |= addAction(menu, repository.getSession(), new ShowInViewAction(page, HISTORY_VIEW_ID));
      }
    }

    if (selectedElement instanceof CDOBranch)
    {
      filled |= addAction(menu, selectedElement, new ShowInViewAction(page, HISTORY_VIEW_ID));
    }

    if (selectedElement instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)selectedElement;
      if (checkout.isOpen())
      {
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

        filled |= addAction(menu, checkout.getView(), new ShowInViewAction(page, HISTORY_VIEW_ID));
      }
    }

    if (selectedElement instanceof EObject)
    {
      EObject eObject = (EObject)selectedElement;
      if (CDOExplorerUtil.getCheckout(eObject) != null)
      {
        filled |= addAction(menu, selectedElement, new ShowInViewAction(page, HISTORY_VIEW_ID));
      }
    }

    if (selectedElement instanceof AbstractElement)
    {
      AbstractElement element = (AbstractElement)selectedElement;
      menu.add(new ShowInSystemExplorerAction(element.getFolder()));
      filled = true;
    }

    return filled;
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

  /**
   * @author Eike Stepper
   */
  private static final class ShowInServerBrowserAction extends Action
  {
    private static final String ID = OM.BUNDLE_ID + ".ShowInServerBrowserAction"; //$NON-NLS-1$

    private static final String PRODUCT_GROUP = CDOServerBrowser.ContainerBased.Factory.PRODUCT_GROUP;

    private static final String TYPE = CDOServerBrowser.ContainerBased.Factory.TYPE;

    private final CDOExplorerElement element;

    private final IManagedContainer container;

    private ShowInServerBrowserAction(CDOExplorerElement element, IManagedContainer container)
    {
      this.element = element;
      this.container = container;

      setId(ID);
      setText("CDO Server Browser");
      setImageDescriptor(OM.getImageDescriptor("icons/web.gif"));
      setToolTipText("Show this element in a CDO server browser");
    }

    @Override
    public void run()
    {
      container.registerFactory(new CDOServerBrowser.ContainerBased.Factory(container));
      container.registerFactory(new DBBrowserPage.Factory());

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
  private static class ShowInViewAction extends Action
  {
    private static final String ID_PREFIX = OM.BUNDLE_ID + ".ShowInViewAction"; //$NON-NLS-1$

    private final IWorkbenchPage page;

    private final IViewDescriptor viewDescriptor;

    private Object element;

    public ShowInViewAction(IWorkbenchPage page, String viewID)
    {
      setId(ID_PREFIX + "." + viewID);
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
  private static final class ShowInSessionsViewAction extends ShowInViewAction
  {
    private final CDORepository repository;

    private final CDOCheckout checkout;

    private ShowInSessionsViewAction(IWorkbenchPage page, CDORepository repository, CDOCheckout checkout)
    {
      super(page, CDOSessionsView.ID);
      this.repository = repository;
      this.checkout = checkout;
    }

    @Override
    protected void run(IViewPart viewPart) throws Exception
    {
      final Object select = show();
      if (select != null)
      {
        final TreeViewer viewer = ((CDOSessionsView)viewPart).getViewer();
        viewer.getControl().getDisplay().asyncExec(new Runnable()
        {
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
              if (branch.equals(transaction.getBranch()))
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
      String description = repository.getURI();
      int lastSlash = description.lastIndexOf('/');
      description = description.substring(0, lastSlash) + "?repositoryName=" + repository.getName()
          + "&automaticPackageRegistry=true&repositoryID=" + repository.getID();

      return (CDOSession)IPluginContainer.INSTANCE.getElement(CDOSessionFactory.PRODUCT_GROUP, "cdo",
          repository.getConnectorType() + "://" + repository.getConnectorDescription() + "?repositoryName="
              + repository.getName() + "&repositoryID=" + repository.getID());
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ShowInSystemExplorerAction extends Action
  {
    private static final String ID = OM.BUNDLE_ID + ".ShowInSystemExplorerAction"; //$NON-NLS-1$

    private final File folder;

    private ShowInSystemExplorerAction(File folder)
    {
      this.folder = folder;

      setId(ID);
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
