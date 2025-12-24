/*
 * Copyright (c) 2015, 2016, 2020, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutLobEditorInput;
import org.eclipse.emf.cdo.internal.explorer.CDOExplorerURIHandler;
import org.eclipse.emf.cdo.internal.ui.dialogs.EditObjectDialog;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorOpener;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class OpenWithActionProvider extends CommonActionProvider
{
  private ICommonViewerWorkbenchSite viewSite;

  private OpenAction openAction;

  public OpenWithActionProvider()
  {
  }

  @Override
  public void init(ICommonActionExtensionSite aConfig)
  {
    if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite)
    {
      viewSite = (ICommonViewerWorkbenchSite)aConfig.getViewSite();
      openAction = new OpenAction(viewSite.getPage());
    }
  }

  @Override
  public void fillActionBars(IActionBars actionBars)
  {
    if (viewSite == null)
    {
      return;
    }

    Object selectedElement = getSelectedElement();

    openAction.selectionChanged(selectedElement);
    if (openAction.isEnabled())
    {
      actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
    }
  }

  @Override
  public void fillContextMenu(IMenuManager menu)
  {
    if (viewSite == null)
    {
      return;
    }

    Object selectedElement = getSelectedElement();

    openAction.selectionChanged(selectedElement);
    if (openAction.isEnabled())
    {
      menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openAction);
      addOpenWithMenu(menu, selectedElement);
    }
  }

  private void addOpenWithMenu(IMenuManager menu, Object selectedElement)
  {
    EObject openableElement = getOpenableElement(selectedElement);
    if (openableElement != null)
    {
      CDOObject cdoObject = CDOUtil.getCDOObject(openableElement);

      CDOCheckout checkout = CDOExplorerUtil.getCheckout(cdoObject);
      if (checkout != null)
      {
        CDOResourceLeaf resourceLeaf = getResourceLeaf(cdoObject);

        List<IAction> actions = collectOpenWithActions(resourceLeaf, cdoObject);
        if (!actions.isEmpty())
        {
          IMenuManager submenu = new MenuManager("Open With", ICommonMenuConstants.GROUP_OPEN_WITH);
          submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

          for (IAction action : actions)
          {
            if (action != null)
            {
              submenu.add(action);
            }
            else
            {
              submenu.add(new Separator());
            }
          }

          submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
          menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, submenu);
        }
      }
    }
  }

  private List<IAction> collectOpenWithActions(CDOResourceLeaf resourceLeaf, CDOObject cdoObject)
  {
    List<IAction> actions = new ArrayList<>();
    URI uri = resourceLeaf.getURI();

    if (resourceLeaf instanceof CDOResource)
    {
      CDOEditorOpener[] editorOpeners = CDOEditorOpener.Registry.INSTANCE.getEditorOpeners(uri);
      for (CDOEditorOpener editorOpener : editorOpeners)
      {
        actions.add(new OpenWithAction(viewSite.getPage(), cdoObject, editorOpener));
      }

      actions.add(null); // Produce a separator on the sub menu.
    }

    String[] editorIDs = CDOEditorUtil.getAllEditorIDs(resourceLeaf);
    for (String editorID : editorIDs)
    {
      if (Objects.equals(editorID, CDOEditorUtil.getEditorID()))
      {
        continue;
      }

      actions.add(new OpenWithRegisteredEditorAction(viewSite.getPage(), editorID, resourceLeaf));
    }

    return actions;
  }

  private Object getSelectedElement()
  {
    IStructuredSelection selection = (IStructuredSelection)getContext().getSelection();
    if (selection.size() == 1)
    {
      return selection.getFirstElement();
    }

    return null;
  }

  private static EObject getOpenableElement(Object element)
  {
    if (element instanceof EObject)
    {
      EObject eObject = (EObject)element;
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(eObject);
      if (checkout != null && !(eObject instanceof CDOResourceFolder))
      {
        return eObject;
      }
    }

    return null;
  }

  private static CDOResourceLeaf getResourceLeaf(CDOObject cdoObject)
  {
    if (cdoObject instanceof CDOResourceLeaf)
    {
      return (CDOResourceLeaf)cdoObject;
    }

    if (cdoObject instanceof CDOResourceFolder)
    {
      return null;
    }

    return cdoObject.cdoResource();
  }

  public static void openEditor(IWorkbenchPage page, ComposedAdapterFactory adapterFactory, EObject object, String editorOpenerID)
  {
    if (page == null)
    {
      page = UIUtil.getActiveWorkbenchPage();
    }

    if (object == null)
    {
      throw new IllegalArgumentException("object is null");
    }

    if (editorOpenerID == null && !(object instanceof CDOResourceNode))
    {
      if (adapterFactory == null)
      {
        // This must be an unwanted second call through the global open action registration.
        return;
      }

      Shell shell = page.getWorkbenchWindow().getShell();
      if (editObject(shell, adapterFactory, object))
      {
        return;
      }
    }

    CDOObject cdoObject = CDOUtil.getCDOObject(object);

    CDOResourceLeaf resourceLeaf = getResourceLeaf(cdoObject);
    if (resourceLeaf instanceof CDOResource)
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(cdoObject);
      if (checkout != null)
      {
        URI uri = resourceLeaf.getURI();

        if (!(object instanceof CDOResourceNode))
        {
          StringBuilder builder = new StringBuilder();
          CDOIDUtil.write(builder, cdoObject.cdoID());

          String fragment = builder.toString();
          uri = uri.appendFragment(fragment);
        }

        CDOEditorOpener[] editorOpeners = CDOEditorOpener.Registry.INSTANCE.getEditorOpeners(uri.trimFragment());
        String defaultEditorOpenerID = editorOpeners.length != 0 ? editorOpeners[0].getID() : null;

        CDOID objectID = cdoObject.cdoID();
        String lastEditorOpenerID = checkout.getEditorOpenerID(objectID);

        if (editorOpenerID == null)
        {
          editorOpenerID = lastEditorOpenerID;
        }

        if (editorOpenerID == null)
        {
          editorOpenerID = defaultEditorOpenerID;
        }

        if (editorOpenerID != null)
        {
          CDOEditorOpener editorOpener = CDOEditorOpener.Registry.INSTANCE.getEditorOpener(editorOpenerID);
          if (editorOpener != null)
          {
            if (!ObjectUtil.equals(editorOpenerID, lastEditorOpenerID))
            {
              checkout.setEditorOpenerID(objectID, editorOpenerID);
            }

            if (cdoObject instanceof CDOResourceNode)
            {
              uri = uri.trimFragment();
            }

            editorOpener.openEditor(page, uri);
          }
        }
      }
    }
    else if (resourceLeaf instanceof CDOFileResource)
    {
      String editorID = CDOEditorUtil.getEffectiveEditorID(resourceLeaf);
      if (editorID != null)
      {
        openFileEditor(page, editorID, resourceLeaf);
      }
    }
  }

  private static void openFileEditor(IWorkbenchPage page, String editorID, CDOResourceLeaf resourceLeaf)
  {
    URI uri = CDOExplorerURIHandler.createURI(resourceLeaf);

    try
    {
      CDOCheckoutLobEditorInput.openEditor(page, editorID, uri);
    }
    catch (PartInitException ex)
    {
      OM.LOG.error(ex);
    }
  }

  public static boolean editObject(Shell shell, ComposedAdapterFactory adapterFactory, EObject object)
  {
    boolean edited = false;

    if (Support.UI_FORMS.isAvailable() && !(object instanceof CDOResourceNode))
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
      if (checkout != null)
      {
        EditingDomain editingDomain = new AdapterFactoryEditingDomain(adapterFactory, new BasicCommandStack());
        ResourceSet resourceSet = editingDomain.getResourceSet();
        CDOTransaction transaction = checkout.openTransaction(resourceSet);

        try
        {
          EObject txObject = transaction.getObject(object);

          int result = new EditObjectDialog(shell, adapterFactory, txObject).open();
          edited = true;

          if (result == EditObjectDialog.OK)
          {
            transaction.commit();
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
        finally
        {
          transaction.close();
        }
      }
    }

    return edited;
  }

  /**
   * @author Eike Stepper
   */
  private static class OpenAction extends Action
  {
    public static final String ID = OM.BUNDLE_ID + ".OpenAction"; //$NON-NLS-1$

    private final IWorkbenchPage page;

    private EObject openableElement;

    public OpenAction(IWorkbenchPage page)
    {
      super("Open");
      setId(ID);
      setToolTipText("Edit this resource");
      this.page = page;
    }

    public void selectionChanged(Object selectedElement)
    {
      openableElement = getOpenableElement(selectedElement);
      setEnabled(openableElement != null);
    }

    @Override
    public void run()
    {
      openEditor(page, null, openableElement, null);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class OpenWithAction extends Action
  {
    public static final String ID = OM.BUNDLE_ID + ".OpenWithAction"; //$NON-NLS-1$

    private final IWorkbenchPage page;

    private EObject openableElement;

    private CDOEditorOpener editorOpener;

    public OpenWithAction(IWorkbenchPage page, EObject openableElement, CDOEditorOpener editorOpener)
    {
      setId(ID);
      setText(editorOpener.getName());
      setImageDescriptor(editorOpener.getIcon());
      setToolTipText("Edit this resource");

      this.page = page;
      this.openableElement = openableElement;
      this.editorOpener = editorOpener;
    }

    @Override
    public void run()
    {
      openEditor(page, null, openableElement, editorOpener.getID());
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class OpenWithRegisteredEditorAction extends Action
  {
    private static final IEditorRegistry EDITOR_REGISTRY = PlatformUI.getWorkbench().getEditorRegistry();

    private IWorkbenchPage page;

    private String editorID;

    private CDOResourceLeaf resourceLeaf;

    public OpenWithRegisteredEditorAction(IWorkbenchPage page, String editorID, CDOResourceLeaf resourceLeaf)
    {
      this.page = page;
      this.editorID = editorID;
      this.resourceLeaf = resourceLeaf;

      IEditorDescriptor editorDescriptor = EDITOR_REGISTRY.findEditor(editorID);
      setText(editorDescriptor.getLabel());
      setImageDescriptor(editorDescriptor.getImageDescriptor());
      setToolTipText("Edit this resource");
    }

    @Override
    public void run()
    {
      if (resourceLeaf instanceof CDOResource)
      {
        CDOEditorUtil.openEditor(page, editorID, resourceLeaf);
      }
      else
      {
        openFileEditor(page, editorID, resourceLeaf);
      }
    }
  }
}
