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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.actions.TransactionalBackgroundAction;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor.NewRootMenuPopulator;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.navigator.WizardActionGroup;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutNewActionProvider extends CommonActionProvider implements ISelectionChangedListener
{
  private static final String NEW_MENU_NAME = "common.new.menu"; //$NON-NLS-1$

  private final ComposedAdapterFactory adapterFactory;

  private ActionFactory.IWorkbenchAction showDlgAction;

  private WizardActionGroup newWizardActionGroup;

  private IWorkbenchPage page;

  private StructuredViewer viewer;

  private Object object;

  public CDOCheckoutNewActionProvider()
  {
    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
  }

  @Override
  public void init(ICommonActionExtensionSite extensionSite)
  {
    ICommonViewerSite viewSite = extensionSite.getViewSite();
    if (viewSite instanceof ICommonViewerWorkbenchSite)
    {
      page = ((ICommonViewerWorkbenchSite)viewSite).getPage();
      IWorkbenchWindow window = page.getWorkbenchWindow();

      showDlgAction = ActionFactory.NEW.create(window);

      newWizardActionGroup = new WizardActionGroup(window, PlatformUI.getWorkbench().getNewWizardRegistry(),
          WizardActionGroup.TYPE_NEW, extensionSite.getContentService());

      viewer = extensionSite.getStructuredViewer();
      viewer.addSelectionChangedListener(this);
    }
  }

  @Override
  public void dispose()
  {
    if (viewer != null)
    {
      viewer.removeSelectionChangedListener(this);
    }

    if (showDlgAction != null)
    {
      showDlgAction.dispose();
      showDlgAction = null;
    }

    adapterFactory.dispose();
    super.dispose();
  }

  public void selectionChanged(SelectionChangedEvent event)
  {
    ISelection selection = event.getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (ssel.size() == 1)
      {
        object = ssel.getFirstElement();
        return;
      }
    }

    object = null;
  }

  @Override
  public void fillContextMenu(IMenuManager menu)
  {
    IMenuManager submenu = new MenuManager("&New", NEW_MENU_NAME);
    if (viewer == null)
    {
      return;
    }

    // Fill the menu from the commonWizard contributions.
    newWizardActionGroup.setContext(getContext());
    newWizardActionGroup.fillContextMenu(submenu);

    if (object instanceof CDOResource)
    {
      final CDOResource resource = (CDOResource)object;

      CDOPackageRegistry packageRegistry = resource.cdoView().getSession().getPackageRegistry();
      NewRootMenuPopulator populator = new NewRootMenuPopulator(packageRegistry)
      {
        @Override
        protected IAction createAction(EObject object)
        {
          return new CreateRootAction(resource, object);
        }
      };

      populator.populateMenu(submenu);
    }

    submenu.add(new Separator(ICommonMenuConstants.GROUP_ADDITIONS));

    // Add other...
    submenu.add(new Separator());
    submenu.add(showDlgAction);

    // Append the submenu after the GROUP_NEW group.
    menu.insertAfter(ICommonMenuConstants.GROUP_NEW, submenu);
  }

  /**
   * @author Eike Stepper
   */
  private class CreateRootAction extends TransactionalBackgroundAction
  {
    protected EObject object;

    protected CreateRootAction(CDOResource resource, EObject object)
    {
      super(page, object.eClass().getName(), null, ExtendedImageRegistry.getInstance().getImageDescriptor(
          CDOEditor.getLabelImage(adapterFactory, object)), resource);
      this.object = object;
    }

    @Override
    protected void doRun(CDOTransaction transaction, CDOObject resource, IProgressMonitor progressMonitor)
        throws Exception
    {
      EList<EObject> contents = ((CDOResource)resource).getContents();
      contents.add(object);
    }
  }
}
