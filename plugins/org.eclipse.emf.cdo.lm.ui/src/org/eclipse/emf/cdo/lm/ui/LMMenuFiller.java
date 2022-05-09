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
package org.eclipse.emf.cdo.lm.ui;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.checkouts.actions.ShowInActionProvider;
import org.eclipse.emf.cdo.explorer.ui.repositories.CDORepositoriesView;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.ui.views.AssembliesView;
import org.eclipse.emf.cdo.lm.ui.views.SystemsView;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.MenuFiller;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class LMMenuFiller implements MenuFiller
{
  public LMMenuFiller()
  {
  }

  @Override
  public boolean fillMenu(IWorkbenchPage page, StructuredViewer viewer, IMenuManager menu, Object selectedElement)
  {
    if (selectedElement instanceof CDOCheckout //
        && ShowInActionProvider.ID.equals(menu.getId()))
    {
      CDOCheckout checkout = (CDOCheckout)selectedElement;

      IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(checkout);
      if (descriptor != null)
      {
        menu.add(new ShowInActionProvider.ShowInViewAction(page, SystemsView.ID)
        {
          @Override
          protected void run(IViewPart viewPart) throws Exception
          {
            ((SystemsView)viewPart).selectReveal(new StructuredSelection(descriptor.getBaseline()));
          }
        });

        menu.add(new ShowInActionProvider.ShowInViewAction(page, AssembliesView.ID)
        {
          @Override
          protected void run(IViewPart viewPart) throws Exception
          {
            ((AssembliesView)viewPart).selectReveal(new StructuredSelection(descriptor));
          }
        });

        return true;
      }
    }
    else if (selectedElement instanceof CDORepository //
        && CDORepositoriesView.SHOW_IN_MENU_ID.equals(menu.getId()))
    {
      CDORepository repository = (CDORepository)selectedElement;

      ISystemDescriptor descriptor = ISystemManager.INSTANCE.getDescriptor(repository);
      if (descriptor != null)
      {
        menu.add(new ShowInActionProvider.ShowInViewAction(page, SystemsView.ID)
        {
          @Override
          protected void run(IViewPart viewPart) throws Exception
          {
            Object[] elements = { descriptor, descriptor.getSystem() };
            ((SystemsView)viewPart).selectReveal(new StructuredSelection(elements));
          }
        });

        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends MenuFiller.Factory
  {
    public static final String TYPE = "lm";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public MenuFiller create(String description) throws ProductCreationException
    {
      return new LMMenuFiller();
    }
  }
}
