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
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistryPopulator;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.internal.db.DBBrowserPage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.views.IViewDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutShowInActionProvider extends CommonActionProvider
{
  private ICommonViewerWorkbenchSite viewSite;

  @Override
  public void init(ICommonActionExtensionSite aConfig)
  {
    if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite)
    {
      viewSite = (ICommonViewerWorkbenchSite)aConfig.getViewSite();
    }
  }

  @Override
  public void fillContextMenu(IMenuManager menu)
  {
    if (viewSite == null)
    {
      return;
    }

    IStructuredSelection selection = (IStructuredSelection)getContext().getSelection();
    if (selection.size() != 1)
    {
      return;
    }

    Object selectedElement = selection.getFirstElement();
    addShowInActions(viewSite.getPage(), menu, selectedElement);
  }

  public static void addShowInActions(IWorkbenchPage page, IMenuManager menu, Object selectedElement)
  {
    List<IAction> actions = new ArrayList<IAction>();

    if (selectedElement instanceof CDORepository)
    {
      final CDORepository repository = (CDORepository)selectedElement;
      if (repository.isConnected())
      {
        addAction(actions, repository, new ShowInAction(page, CDOSessionsView.ID)
        {
          @Override
          public void run()
          {
            super.run();

            String description = repository.getURI();
            int lastSlash = description.lastIndexOf('/');
            description = description.substring(0, lastSlash) + "?repositoryName=" + repository.getName()
                + "&automaticPackageRegistry=true&repositoryID=" + repository.getID();

            CDOSession session = (CDOSession)IPluginContainer.INSTANCE.getElement(CDOSessionFactory.PRODUCT_GROUP,
                "cdo", repository.getConnectorType() + "://" + repository.getConnectorDescription()
                    + "?repositoryName=" + repository.getName() + "&repositoryID=" + repository.getID());
            if (session != null)
            {
              CDOPackageRegistryPopulator.populate(session.getPackageRegistry());
            }
          }
        });

        addAction(actions, repository.getSession(), new ShowInAction(page, "org.eclipse.team.ui.GenericHistoryView"));
      }
    }
    else if (selectedElement instanceof CDOBranch)
    {
      addAction(actions, selectedElement, new ShowInAction(page, "org.eclipse.team.ui.GenericHistoryView"));
    }
    else if (selectedElement instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)selectedElement;
      if (checkout.isOpen())
      {
        if (checkout instanceof OfflineCDOCheckout)
        {
          OfflineCDOCheckout offlineCheckout = (OfflineCDOCheckout)checkout;

          final InternalCDOWorkspace workspace = (InternalCDOWorkspace)offlineCheckout.getWorkspace();
          if (workspace != null)
          {
            actions.add(new Action("CDO Server Browser", OM.getImageDescriptor("icons/web.gif"))
            {
              @Override
              public void run()
              {
                IManagedContainer container = workspace.getContainer();
                container.registerFactory(new CDOServerBrowser.ContainerBased.Factory(container));
                container.registerFactory(new DBBrowserPage.Factory());

                CDOServerBrowser browser = (CDOServerBrowser)container.getElement(
                    CDOServerBrowser.ContainerBased.Factory.PRODUCT_GROUP,
                    CDOServerBrowser.ContainerBased.Factory.TYPE, "free-port");

                if (browser != null && browser.isActive())
                {
                  IOUtil.openSystemBrowser("http://localhost:" + browser.getPort());
                }
              }
            });
          }
        }

        addAction(actions, checkout.getView(), new ShowInAction(page, "org.eclipse.team.ui.GenericHistoryView"));
      }
    }
    else if (selectedElement instanceof EObject)
    {
      EObject eObject = (EObject)selectedElement;
      if (CDOExplorerUtil.getCheckout(eObject) != null)
      {
        addAction(actions, selectedElement, new ShowInAction(page, "org.eclipse.team.ui.GenericHistoryView"));
      }
    }

    if (!actions.isEmpty())
    {
      IMenuManager submenu = new MenuManager("Show In", ICommonMenuConstants.GROUP_OPEN_WITH);
      submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

      for (IAction action : actions)
      {
        submenu.add(action);
      }

      submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
      menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
    }
  }

  private static void addAction(List<IAction> actions, Object selectedElement, ShowInAction action)
  {
    action.selectionChanged(selectedElement);
    if (action.isEnabled())
    {
      actions.add(action);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class ShowInAction extends Action
  {
    public static final String ID = OM.BUNDLE_ID + ".ShowInAction"; //$NON-NLS-1$

    private final IWorkbenchPage page;

    private final IViewDescriptor viewDescriptor;

    private Object element;

    public ShowInAction(IWorkbenchPage page, String viewID)
    {
      setId(ID);
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
    public void run()
    {
      try
      {
        page.showView(viewDescriptor.getId());
      }
      catch (PartInitException ex)
      {
        OM.LOG.error(ex);
      }
    }
  }
}
