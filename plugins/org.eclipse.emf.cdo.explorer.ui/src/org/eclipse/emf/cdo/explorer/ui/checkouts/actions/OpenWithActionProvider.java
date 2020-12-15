/*
 * Copyright (c) 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.explorer.CDOExplorerURIHandler;
import org.eclipse.emf.cdo.internal.ui.dialogs.EditObjectDialog;
import org.eclipse.emf.cdo.internal.ui.editor.CDOLobEditorInput;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorOpener;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.OMPlatform;
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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Eike Stepper
 */
public class OpenWithActionProvider extends CommonActionProvider
{
  private static final boolean OMIT_LOB_HANDLER_URI = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.explorer.ui.omitLobHandlerURI");

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
      CDOResourceLeaf resourceLeaf = getResourceLeaf(cdoObject);
      if (resourceLeaf instanceof CDOResource)
      {
        CDOResource resource = (CDOResource)resourceLeaf;
        URI uri = resource.getURI();

        CDOCheckout checkout = CDOExplorerUtil.getCheckout(cdoObject);
        if (checkout != null)
        {
          CDOEditorOpener[] editorOpeners = CDOEditorOpener.Registry.INSTANCE.getEditorOpeners(uri);
          if (editorOpeners.length != 0)
          {
            IMenuManager submenu = new MenuManager("Open With", ICommonMenuConstants.GROUP_OPEN_WITH);
            submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

            for (CDOEditorOpener editorOpener : editorOpeners)
            {
              OpenWithAction action = new OpenWithAction(viewSite.getPage(), cdoObject, editorOpener);
              submenu.add(action);
            }

            submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
            menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, submenu);
          }
        }
      }
    }
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
        else
        {
          // Pair<CDOResourceLeaf, String> key = Pair.create(resourceLeaf, editorOpenerID);
          //
          // synchronized (EDITORS)
          // {
          // Object editor = EDITORS.get(key);
          // if (editor != null)
          // {
          // if (editor != EDITOR_OPENING)
          // {
          // page.activate((IEditorPart)editor);
          // }
          //
          // return;
          // }
          //
          // EDITORS.put(key, EDITOR_OPENING);
          // }
          //
          // openEditor(page, cdoObject, resourceLeaf, editorOpenerID, key);
        }
      }
    }
    else if (resourceLeaf instanceof CDOFileResource)
    {
      String editorID = CDOEditorUtil.getEffectiveEditorID(resourceLeaf);
      if (editorID != null)
      {
        CDOView view = resourceLeaf.cdoView();
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(view);
        CDOTransaction tx = view.isHistorical() ? null : checkout != null ? checkout.openTransaction() : view.getSession().openTransaction(view.getBranch());
        CDOResourceLeaf txLeaf = tx == null ? resourceLeaf : tx.getObject(resourceLeaf);

        try
        {
          CDOLobEditorInput editorInput = (CDOLobEditorInput)CDOEditorUtil.createLobEditorInput(txLeaf, true);

          if (checkout != null && !OMIT_LOB_HANDLER_URI)
          {
            editorInput.setURI(CDOExplorerURIHandler.createURI(checkout, txLeaf));
          }

          IEditorPart editor = page.openEditor(editorInput, editorID);

          page.addPartListener(new IPartListener()
          {
            @Override
            public void partClosed(IWorkbenchPart part)
            {
              if (part == editor)
              {
                if (tx != null)
                {
                  tx.close();
                }

                editor.getSite().getPage().removePartListener(this);
              }
            }

            @Override
            public void partActivated(IWorkbenchPart part)
            {
              // Do nothing.
            }

            @Override
            public void partBroughtToTop(IWorkbenchPart part)
            {
              // Do nothing.
            }

            @Override
            public void partDeactivated(IWorkbenchPart part)
            {
              // Do nothing.
            }

            @Override
            public void partOpened(IWorkbenchPart part)
            {
              // Do nothing.
            }
          });
        }
        catch (PartInitException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  public static boolean editObject(Shell shell, ComposedAdapterFactory adapterFactory, EObject object)
  {
    boolean edited = false;

    if (!(object instanceof CDOResourceNode))
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

  // private static void registerEditor(IEditorPart editor, CDOView view, Pair<CDOResourceLeaf, String> key)
  // {
  // view.properties().put(WORKBENCH_PART_KEY, editor);
  //
  // synchronized (EDITORS)
  // {
  // EDITORS.put(key, editor);
  // }
  //
  // synchronized (VIEWS)
  // {
  // VIEWS.put(editor, Pair.create(view, key));
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // private static final class WindowListener implements IWindowListener
  // {
  // public void windowOpened(IWorkbenchWindow window)
  // {
  // window.addPageListener(PAGE_LISTENER);
  // }
  //
  // public void windowClosed(IWorkbenchWindow window)
  // {
  // window.removePageListener(PAGE_LISTENER);
  // }
  //
  // public void windowActivated(IWorkbenchWindow window)
  // {
  // // Do nothing
  // }
  //
  // public void windowDeactivated(IWorkbenchWindow window)
  // {
  // // Do nothing
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // private static final class PageListener implements IPageListener
  // {
  // public void pageOpened(IWorkbenchPage page)
  // {
  // page.addPartListener(PART_LISTENER);
  // }
  //
  // public void pageClosed(IWorkbenchPage page)
  // {
  // page.removePartListener(PART_LISTENER);
  // }
  //
  // public void pageActivated(IWorkbenchPage page)
  // {
  // // Do nothing
  // }
  // }
  //
  // /**
  // * @author Eike Stepper
  // */
  // private static final class PartListener implements IPartListener2
  // {
  // public void partOpened(IWorkbenchPartReference partRef)
  // {
  // }
  //
  // public void partClosed(IWorkbenchPartReference partRef)
  // {
  // IWorkbenchPart part = partRef.getPart(false);
  // if (part != null)
  // {
  // Pair<CDOView, Pair<CDOResourceLeaf, String>> pair;
  // synchronized (VIEWS)
  // {
  // pair = VIEWS.remove(part);
  // }
  //
  // if (pair != null)
  // {
  // CDOView view = pair.getElement1();
  // view.close();
  //
  // Pair<CDOResourceLeaf, String> key = pair.getElement2();
  // synchronized (EDITORS)
  // {
  // EDITORS.remove(key);
  // }
  // }
  // }
  // }
  //
  // public void partVisible(IWorkbenchPartReference partRef)
  // {
  // // Do nothing
  // }
  //
  // public void partHidden(IWorkbenchPartReference partRef)
  // {
  // // Do nothing
  // }
  //
  // public void partActivated(IWorkbenchPartReference partRef)
  // {
  // // Do nothing
  // }
  //
  // public void partDeactivated(IWorkbenchPartReference partRef)
  // {
  // // Do nothing
  // }
  //
  // public void partBroughtToTop(IWorkbenchPartReference partRef)
  // {
  // // Do nothing
  // }
  //
  // public void partInputChanged(IWorkbenchPartReference partRef)
  // {
  // // Do nothing
  // }
  // }

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

  // /**
  // * @author Eike Stepper
  // */
  // private static class OpenFileAction extends Action
  // {
  // public static final String ID = OM.BUNDLE_ID + ".OpenFileAction"; //$NON-NLS-1$
  //
  // private final IWorkbenchPage page;
  //
  // private EObject openableElement;
  //
  // private String editorID;
  //
  // public OpenFileAction(IWorkbenchPage page, EObject openableElement, String editorID)
  // {
  // setId(ID);
  //
  // this.page = page;
  // this.openableElement = openableElement;
  // this.editorID = editorID;
  //
  // IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(editorID);
  // setText(editorDescriptor.getLabel());
  // setImageDescriptor(editorDescriptor.getImageDescriptor());
  //
  // setToolTipText("Edit this resource");
  // }
  //
  // @Override
  // public void run()
  // {
  // openEditor(page, null, openableElement, editorID);
  // }
  // }
}
