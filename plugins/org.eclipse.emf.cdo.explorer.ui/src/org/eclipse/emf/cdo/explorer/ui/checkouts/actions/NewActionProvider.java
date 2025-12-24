/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.NewBinaryResourceWizard;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.NewFolderWizard;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.NewResourceWizard;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.NewTextResourceWizard;
import org.eclipse.emf.cdo.explorer.ui.checkouts.wizards.NewWizard;
import org.eclipse.emf.cdo.internal.ui.actions.TransactionalBackgroundAction;
import org.eclipse.emf.cdo.internal.ui.dialogs.SelectClassDialog;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor.NewRootMenuPopulator;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.action.CreateChildAction;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.navigator.WizardActionGroup;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class NewActionProvider extends CommonActionProvider implements ISelectionChangedListener
{
  private static final String NEW_MENU_NAME = "common.new.menu"; //$NON-NLS-1$

  private ICommonActionExtensionSite extensionSite;

  private CDOCheckoutContentProvider contentProvider;

  private ActionFactory.IWorkbenchAction showDlgAction;

  private WizardActionGroup newWizardActionGroup;

  private IWorkbenchPage page;

  private TreeViewer viewer;

  private Object selectedObject;

  public NewActionProvider()
  {
  }

  @Override
  public void init(ICommonActionExtensionSite extensionSite)
  {
    this.extensionSite = extensionSite;
    ICommonViewerSite viewSite = extensionSite.getViewSite();
    if (viewSite instanceof ICommonViewerWorkbenchSite)
    {
      page = ((ICommonViewerWorkbenchSite)viewSite).getPage();
      IWorkbenchWindow window = page.getWorkbenchWindow();

      showDlgAction = ActionFactory.NEW.create(window);

      CDOCheckoutContentProvider contentProvider = getContentProvider();
      IWizardRegistry wrapperRegistry = new WizardRegistryWrapper(contentProvider);

      newWizardActionGroup = new WizardActionGroup(window, wrapperRegistry, WizardActionGroup.TYPE_NEW, extensionSite.getContentService());

      viewer = (TreeViewer)extensionSite.getStructuredViewer();
      viewer.addSelectionChangedListener(this);
      updateSelectedObject(viewer.getSelection());
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

    super.dispose();
  }

  @Override
  public void selectionChanged(SelectionChangedEvent event)
  {
    ISelection selection = event.getSelection();
    updateSelectedObject(selection);
  }

  private void updateSelectedObject(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (ssel.size() == 1)
      {
        selectedObject = ssel.getFirstElement();
        if (selectedObject instanceof CDOCheckout)
        {
          CDOCheckout checkout = (CDOCheckout)selectedObject;
          selectedObject = checkout.getRootObject();
        }

        return;
      }
    }

    selectedObject = null;
  }

  @Override
  public void fillContextMenu(IMenuManager menu)
  {
    if (viewer == null)
    {
      return;
    }

    CDOCheckout checkout = CDOExplorerUtil.getCheckout(selectedObject);
    if (checkout == null || checkout.isReadOnly())
    {
      return;
    }

    IMenuManager submenu = new MenuManager("&New", NEW_MENU_NAME);

    fillNewWizardActions(submenu);

    if (selectedObject instanceof CDOResource)
    {
      CDOResource resource = (CDOResource)selectedObject;
      if (!resource.isRoot())
      {
        fillNewRootActions(submenu, checkout, resource);

        submenu.add(new Separator());
        submenu.add(new NewRootOtherAction(resource, checkout));
        submenu.add(new Separator());
      }
    }
    else if (selectedObject instanceof CDOResourceNode)
    {
      // Do nothing. CDOResourceFolder contributions have already been added by newWizardActionGroup.
    }
    else if (selectedObject instanceof EObject)
    {
      fillNewChildActions(submenu, checkout, (EObject)selectedObject);
    }

    submenu.add(new Separator(ICommonMenuConstants.GROUP_ADDITIONS));

    // Append the submenu after the GROUP_NEW group.
    menu.insertAfter(ICommonMenuConstants.GROUP_NEW, submenu);
  }

  private void fillNewWizardActions(IMenuManager menu)
  {
    // Fill the menu from the commonWizard contributions.
    newWizardActionGroup.setContext(getContext());
    newWizardActionGroup.fillContextMenu(menu);

    IContributionItem newResourceFolderItem = null;
    IContributionItem newModelResourceItem = null;
    IContributionItem newBinaryFileItem = null;
    IContributionItem newTextFileItem = null;

    String firstID = null;
    IContributionItem[] newWizardItems = menu.getItems();
    for (IContributionItem newWizardItem : newWizardItems)
    {
      if (newWizardItem instanceof ActionContributionItem)
      {
        IAction action = ((ActionContributionItem)newWizardItem).getAction();
        if (action instanceof IPluginContribution)
        {
          IPluginContribution pluginContribution = (IPluginContribution)action;
          String id = pluginContribution.getLocalId();
          if (NewFolderWizard.ID.equals(id))
          {
            newResourceFolderItem = menu.remove(newWizardItem);
            continue;
          }

          if (NewResourceWizard.ID.equals(id))
          {
            newModelResourceItem = menu.remove(newWizardItem);
            continue;
          }

          if (NewBinaryResourceWizard.ID.equals(id))
          {
            newBinaryFileItem = menu.remove(newWizardItem);
            continue;
          }

          if (NewTextResourceWizard.ID.equals(id))
          {
            newTextFileItem = menu.remove(newWizardItem);
            continue;
          }
        }

        // Filter out the dubious "Ecore Diagram" action that appears everywhere.
        if ("Ecore Diagram".equals(action.getText()))
        {
          menu.remove(newWizardItem);
          continue;
        }
      }

      if (firstID == null)
      {
        firstID = newWizardItem.getId();
      }
    }

    if (firstID == null)
    {
      Separator group = new Separator("cdo-new-wizards");
      menu.add(group);
      firstID = group.getId();
    }

    if (newResourceFolderItem != null)
    {
      menu.insertBefore(firstID, newResourceFolderItem);
    }

    if (newModelResourceItem != null)
    {
      menu.insertBefore(firstID, newModelResourceItem);
    }

    if (newBinaryFileItem != null)
    {
      menu.insertBefore(firstID, newBinaryFileItem);
    }

    if (newTextFileItem != null)
    {
      menu.insertBefore(firstID, newTextFileItem);
    }
  }

  private void fillNewRootActions(IMenuManager menu, final CDOCheckout checkout, final CDOResource resource)
  {
    CDOPackageRegistry packageRegistry = resource.cdoView().getSession().getPackageRegistry();
    NewRootMenuPopulator populator = new NewRootMenuPopulator(packageRegistry)
    {
      @Override
      protected IAction createAction(EObject object)
      {
        ComposedAdapterFactory adapterFactory = getAdapterFactory(checkout);

        Object image = CDOEditor.getLabelImage(adapterFactory, object);
        ImageDescriptor imageDescriptor = ExtendedImageRegistry.getInstance().getImageDescriptor(image);

        return new NewRootAction(resource, checkout, object, imageDescriptor);
      }
    };

    populator.populateMenu(menu);
  }

  private void fillNewChildActions(IMenuManager menu, CDOCheckout checkout, EObject object)
  {
    ResourceSet resourceSet = checkout.getView().getResourceSet();

    ComposedAdapterFactory adapterFactory = getAdapterFactory(checkout);

    EditingDomain editingDomain = new AdapterFactoryEditingDomain(adapterFactory, new BasicCommandStack(), resourceSet);
    IStructuredSelection selection = new StructuredSelection(object);
    CDOObject cdoObject = CDOUtil.getCDOObject(object);

    Collection<?> childDescriptors = editingDomain.getNewChildDescriptors(object, null);
    for (Object childDescriptor : childDescriptors)
    {
      CreateChildAction delegate = new CreateChildAction(editingDomain, selection, childDescriptor);
      String text = delegate.getText();
      String toolTipText = delegate.getToolTipText();
      ImageDescriptor imageDescriptor = delegate.getImageDescriptor();

      NewChildAction action = new NewChildAction(text, toolTipText, imageDescriptor, checkout, cdoObject, childDescriptor);
      menu.add(action);
    }
  }

  private ComposedAdapterFactory getAdapterFactory(CDOCheckout checkout)
  {
    CDOCheckoutContentProvider contentProvider = getContentProvider();
    return contentProvider.getStateManager().getState(checkout).getAdapterFactory();
  }

  private CDOCheckoutContentProvider getContentProvider()
  {
    if (contentProvider == null)
    {
      String viewerID = extensionSite.getContentService().getViewerId();
      contentProvider = CDOCheckoutContentProvider.getInstance(viewerID);
    }

    return contentProvider;
  }

  /**
   * @author Eike Stepper
   */
  private static final class WizardRegistryWrapper implements IWizardRegistry
  {
    private static final IWizardRegistry DELEGATE = PlatformUI.getWorkbench().getNewWizardRegistry();

    private final CDOCheckoutContentProvider contentProvider;

    private WizardRegistryWrapper(CDOCheckoutContentProvider contentProvider)
    {
      this.contentProvider = contentProvider;
    }

    @Override
    public IWizardCategory getRootCategory()
    {
      return DELEGATE.getRootCategory();
    }

    @Override
    public IWizardDescriptor[] getPrimaryWizards()
    {
      return DELEGATE.getPrimaryWizards();
    }

    @Override
    public IWizardDescriptor findWizard(String id)
    {
      final IWizardDescriptor delegate = DELEGATE.findWizard(id);
      if (delegate != null)
      {
        return new WizardDescriptorWrapper(delegate, contentProvider);
      }

      return delegate;
    }

    @Override
    public IWizardCategory findCategory(String id)
    {
      return DELEGATE.findCategory(id);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class WizardDescriptorWrapper implements IWizardDescriptor
  {
    private final IWizardDescriptor delegate;

    private final CDOCheckoutContentProvider contentProvider;

    private WizardDescriptorWrapper(IWizardDescriptor delegate, CDOCheckoutContentProvider contentProvider)
    {
      this.delegate = delegate;
      this.contentProvider = contentProvider;
    }

    @Override
    public String getId()
    {
      return delegate.getId();
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
      return delegate.getImageDescriptor();
    }

    @Override
    public IStructuredSelection adaptedSelection(IStructuredSelection selection)
    {
      return delegate.adaptedSelection(selection);
    }

    @Override
    public String getLabel()
    {
      return delegate.getLabel();
    }

    @Override
    public <T> T getAdapter(Class<T> adapter)
    {
      return delegate.getAdapter(adapter);
    }

    @Override
    public String getDescription()
    {
      return delegate.getDescription();
    }

    @Override
    public String[] getTags()
    {
      return delegate.getTags();
    }

    @Override
    public IWorkbenchWizard createWizard() throws CoreException
    {
      IWorkbenchWizard wizard = delegate.createWizard();
      if (wizard instanceof NewWizard)
      {
        NewWizard newWizard = (NewWizard)wizard;
        newWizard.setContentProvider(contentProvider);
      }

      return wizard;
    }

    @Override
    public ImageDescriptor getDescriptionImage()
    {
      return delegate.getDescriptionImage();
    }

    @Override
    public String getHelpHref()
    {
      return delegate.getHelpHref();
    }

    @Override
    public IWizardCategory getCategory()
    {
      return delegate.getCategory();
    }

    @Override
    public boolean canFinishEarly()
    {
      return delegate.canFinishEarly();
    }

    @Override
    public boolean hasPages()
    {
      return delegate.hasPages();
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class AbstractNewAction extends TransactionalBackgroundAction
  {
    private CDOCheckout checkout;

    private EObject newObject;

    public AbstractNewAction(String text, String toolTipText, ImageDescriptor image, CDOCheckout checkout, CDOObject parent)
    {
      super(page, text, toolTipText, image, parent);
      this.checkout = checkout;
    }

    @Override
    protected CDOTransaction openTransaction(CDOObject AbstractNewAction)
    {
      if (checkout != null)
      {
        return checkout.openTransaction();
      }

      return null;
    }

    @Override
    protected final void doRun(CDOTransaction transaction, CDOObject parent, IProgressMonitor monitor) throws Exception
    {
      ISelection selection = new StructuredSelection(CDOUtil.getEObject(parent));
      newObject = doRun(transaction, parent, selection);
    }

    protected abstract EObject doRun(CDOTransaction transaction, CDOObject parent, ISelection selection);

    @Override
    protected void postRun(CDOView view, CDOObject parent)
    {
      if (newObject != null)
      {
        EObject object = view.getObject(newObject);
        if (object != null)
        {
          CDOCheckoutContentProvider contentProvider = getContentProvider();
          if (contentProvider != null)
          {
            contentProvider.selectObjects(object);
          }
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private class NewRootAction extends AbstractNewAction
  {
    private final EObject object;

    public NewRootAction(CDOResource resource, CDOCheckout checkout, EObject object, ImageDescriptor image)
    {
      super(object.eClass().getName(), null, image, checkout, resource);
      this.object = object;
    }

    @Override
    protected EObject doRun(CDOTransaction transaction, CDOObject resource, ISelection selection)
    {
      EList<EObject> contents = ((CDOResource)resource).getContents();
      contents.add(object);
      return object;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class NewRootOtherAction extends AbstractNewAction
  {
    private EObject object;

    public NewRootOtherAction(CDOResource resource, CDOCheckout checkout)
    {
      super("Other...", null, SharedIcons.getDescriptor(SharedIcons.OBJ_ECLASS), checkout, resource);
    }

    @Override
    protected void preRun() throws Exception
    {
      SelectClassDialog dialog = new SelectClassDialog(page, "New Root Object", "Select a package and a class for the new root object.");

      if (dialog.open() == SelectClassDialog.OK)
      {
        EClass eClass = dialog.getSelectedClass();
        object = EcoreUtil.create(eClass);
      }
      else
      {
        cancel();
      }
    }

    @Override
    protected EObject doRun(CDOTransaction transaction, CDOObject resource, ISelection selection)
    {
      EList<EObject> contents = ((CDOResource)resource).getContents();
      contents.add(object);
      return object;
    }
  }

  /**
   * @author Eike Stepper
   */
  private class NewChildAction extends AbstractNewAction
  {
    private final Object childDescriptor;

    public NewChildAction(String text, String toolTipText, ImageDescriptor image, CDOCheckout checkout, CDOObject parent, Object childDescriptor)
    {
      super(text, toolTipText, image, checkout, parent);
      this.childDescriptor = childDescriptor;
    }

    @Override
    protected EObject doRun(CDOTransaction transaction, CDOObject parent, ISelection selection)
    {
      ComposedAdapterFactory adapterFactory = CDOEditor.createAdapterFactory(true);

      try
      {
        BasicCommandStack commandStack = new BasicCommandStack();
        ResourceSet resourceSet = transaction.getResourceSet();

        EditingDomain editingDomain = new AdapterFactoryEditingDomain(adapterFactory, commandStack, resourceSet);

        CreateChildAction delegate = new CreateChildAction(editingDomain, selection, childDescriptor);
        delegate.run();

        if (childDescriptor instanceof CommandParameter)
        {
          CommandParameter parameter = (CommandParameter)childDescriptor;
          Object value = parameter.getValue();
          if (value instanceof EObject)
          {
            return (EObject)value;
          }
        }

        return null;
      }
      finally
      {
        adapterFactory.dispose();
      }
    }
  }
}
