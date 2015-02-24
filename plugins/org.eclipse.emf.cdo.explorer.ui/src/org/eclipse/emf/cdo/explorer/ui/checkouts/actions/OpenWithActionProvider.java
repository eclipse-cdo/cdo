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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.EditObjectDialog;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class OpenWithActionProvider extends CommonActionProvider
{
  private static final Map<IEditorPart, Pair<CDOView, Pair<CDOResourceLeaf, String>>> VIEWS = new HashMap<IEditorPart, Pair<CDOView, Pair<CDOResourceLeaf, String>>>();

  private static final Map<Pair<CDOResourceLeaf, String>, Object> EDITORS = new HashMap<Pair<CDOResourceLeaf, String>, Object>();

  private static final Object EDITOR_OPENING = new Object();

  private static final IPartListener2 PART_LISTENER = new IPartListener2()
  {
    public void partOpened(IWorkbenchPartReference partRef)
    {
    }

    public void partClosed(IWorkbenchPartReference partRef)
    {
      IWorkbenchPart part = partRef.getPart(false);
      if (part != null)
      {
        Pair<CDOView, Pair<CDOResourceLeaf, String>> pair;
        synchronized (VIEWS)
        {
          pair = VIEWS.remove(part);
        }

        if (pair != null)
        {
          CDOView view = pair.getElement1();
          view.close();

          Pair<CDOResourceLeaf, String> key = pair.getElement2();
          synchronized (EDITORS)
          {
            EDITORS.remove(key);
          }
        }
      }
    }

    public void partVisible(IWorkbenchPartReference partRef)
    {
      // Do nothing
    }

    public void partHidden(IWorkbenchPartReference partRef)
    {
      // Do nothing
    }

    public void partActivated(IWorkbenchPartReference partRef)
    {
      // Do nothing
    }

    public void partDeactivated(IWorkbenchPartReference partRef)
    {
      // Do nothing
    }

    public void partBroughtToTop(IWorkbenchPartReference partRef)
    {
      // Do nothing
    }

    public void partInputChanged(IWorkbenchPartReference partRef)
    {
      // Do nothing
    }
  };

  private static final IPageListener PAGE_LISTENER = new IPageListener()
  {
    public void pageOpened(IWorkbenchPage page)
    {
      page.addPartListener(PART_LISTENER);
    }

    public void pageClosed(IWorkbenchPage page)
    {
      page.removePartListener(PART_LISTENER);
    }

    public void pageActivated(IWorkbenchPage page)
    {
      // Do nothing
    }
  };

  private static final IWindowListener WINDOW_LISTENER = new IWindowListener()
  {
    public void windowOpened(IWorkbenchWindow window)
    {
      window.addPageListener(PAGE_LISTENER);
    }

    public void windowClosed(IWorkbenchWindow window)
    {
      window.removePageListener(PAGE_LISTENER);
    }

    public void windowActivated(IWorkbenchWindow window)
    {
      // Do nothing
    }

    public void windowDeactivated(IWorkbenchWindow window)
    {
      // Do nothing
    }
  };

  static
  {
    IWorkbench workbench = UIUtil.getWorkbench();
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows())
    {
      window.addPageListener(PAGE_LISTENER);

      for (IWorkbenchPage page : window.getPages())
      {
        page.addPartListener(PART_LISTENER);
      }
    }

    workbench.addWindowListener(WINDOW_LISTENER);
  }

  private ICommonViewerWorkbenchSite viewSite;

  private OpenFileAction openFileAction;

  @Override
  public void init(ICommonActionExtensionSite aConfig)
  {
    if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite)
    {
      viewSite = (ICommonViewerWorkbenchSite)aConfig.getViewSite();
      openFileAction = new OpenFileAction(viewSite.getPage());
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

    openFileAction.selectionChanged(selectedElement);
    if (openFileAction.isEnabled())
    {
      actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openFileAction);
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

    openFileAction.selectionChanged(selectedElement);
    if (openFileAction.isEnabled())
    {
      menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openFileAction);
      addOpenWithMenu(menu, selectedElement);
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

  private void addOpenWithMenu(IMenuManager menu, Object selectedElement)
  {
    EObject openableElement = getOpenableElement(selectedElement);
    if (openableElement == null)
    {
      return;
    }

    CDOObject cdoObject = CDOUtil.getCDOObject(openableElement);
    CDOResourceLeaf resourceLeaf = getResourceLeaf(cdoObject);
    if (resourceLeaf == null)
    {
      return;
    }

    String[] editorIDs = CDOEditorUtil.getAllEditorIDs(resourceLeaf);
    if (editorIDs.length == 0)
    {
      return;
    }

    IMenuManager submenu = new MenuManager("Open With", ICommonMenuConstants.GROUP_OPEN_WITH);
    submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));

    for (String editorID : editorIDs)
    {
      OpenFileAction action = new OpenFileAction(viewSite.getPage(), resourceLeaf, editorID);
      submenu.add(action);
    }

    submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
    menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
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

  private static void openEditor(final IWorkbenchPage page, final CDOObject object, final CDOResourceLeaf resourceLeaf,
      final String editorID, final Pair<CDOResourceLeaf, String> key)
  {
    new Job("Open")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(resourceLeaf);
        final CDOView view = checkout.openView();

        CDOResourceLeaf contextualLeaf = view.getObject(resourceLeaf);
        final IEditorInput editorInput = CDOEditorUtil.createEditorInput(editorID, contextualLeaf, false, true);

        final IWorkbenchWindow workbenchWindow = page.getWorkbenchWindow();
        Display display = workbenchWindow.getShell().getDisplay();

        display.asyncExec(new Runnable()
        {
          public void run()
          {
            try
            {
              IEditorPart editor = page.openEditor(editorInput, editorID);
              if (editor != null)
              {
                synchronized (EDITORS)
                {
                  EDITORS.put(key, editor);
                }

                synchronized (VIEWS)
                {
                  VIEWS.put(editor, Pair.create(view, key));
                }
              }
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        });

        return Status.OK_STATUS;
      }
    }.schedule();
  }

  public static void openEditor(IWorkbenchPage page, ComposedAdapterFactory adapterFactory, EObject object,
      String editorID)
  {
    if (page == null)
    {
      page = UIUtil.getActiveWorkbenchPage();
    }

    if (object == null)
    {
      throw new IllegalArgumentException("object is null");
    }

    if (!(object instanceof CDOResourceNode))
    {
      if (adapterFactory == null)
      {
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
    if (resourceLeaf == null)
    {
      return;
    }

    if (editorID == null)
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
      if (checkout != null)
      {
        editorID = checkout.getEditorID(cdoObject.cdoID());
      }
    }

    if (editorID == null)
    {
      editorID = CDOEditorUtil.getEffectiveEditorID(resourceLeaf);
    }

    Pair<CDOResourceLeaf, String> key = Pair.create(resourceLeaf, editorID);

    synchronized (EDITORS)
    {
      Object editor = EDITORS.get(key);
      if (editor != null)
      {
        if (editor != EDITOR_OPENING)
        {
          page.activate((IEditorPart)editor);
        }

        return;
      }

      EDITORS.put(key, EDITOR_OPENING);
    }

    openEditor(page, cdoObject, resourceLeaf, editorID, key);
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

  /**
   * @author Eike Stepper
   */
  private static class OpenFileAction extends Action
  {
    public static final String ID = OM.BUNDLE_ID + ".OpenFileAction"; //$NON-NLS-1$

    private final IWorkbenchPage page;

    private EObject openableElement;

    private String editorID;

    public OpenFileAction(IWorkbenchPage page)
    {
      this(page, null, null);
    }

    public OpenFileAction(IWorkbenchPage page, EObject openableElement, String editorID)
    {
      setId(ID);

      this.page = page;
      this.openableElement = openableElement;
      this.editorID = editorID;

      if (editorID != null)
      {
        IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(editorID);
        setText(editorDescriptor.getLabel());
        setImageDescriptor(editorDescriptor.getImageDescriptor());
      }
      else
      {
        setText("Open");
      }

      setToolTipText("Edit this resource");
    }

    public void selectionChanged(Object selectedElement)
    {
      openableElement = getOpenableElement(selectedElement);
      setEnabled(openableElement != null);
    }

    @Override
    public void run()
    {
      openEditor(page, null, openableElement, editorID);
    }
  }
}
