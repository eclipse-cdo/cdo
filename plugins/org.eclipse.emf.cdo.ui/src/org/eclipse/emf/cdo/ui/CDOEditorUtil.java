/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 *    Christian W. Damus (CEA LIST) - bug 418452
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditorInputImpl;
import org.eclipse.emf.cdo.internal.ui.editor.CDOLobEditorInput;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Some utility methods to cope with CDOEditor and CDOEditorInput
 *
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public final class CDOEditorUtil
{
  /**
   * @since 4.1
   */
  public static final String EDITOR_ID = "org.eclipse.emf.cdo.ui.CDOEditor"; //$NON-NLS-1$

  /**
   * @since 4.4
   */
  public static final String TEXT_EDITOR_ID = "org.eclipse.ui.DefaultTextEditor";

  private static final IEditorRegistry EDITOR_REGISTRY = PlatformUI.getWorkbench().getEditorRegistry();

  private static final Map<String, String> EDITOR_MAPPINGS = initEditorMappings();

  private static final Map<CDOResourceLeaf, String> EDITOR_OVERRIDES = new WeakHashMap<>();

  private static String editorID = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.ui.editorID", EDITOR_ID);

  private CDOEditorUtil()
  {
  }

  /**
   * @since 4.1
   */
  public static String getEditorID()
  {
    return editorID;
  }

  /**
   * @since 4.1
   */
  public static void setEditorID(String editorID)
  {
    CDOEditorUtil.editorID = editorID;
  }

  /**
   * Returns an implementation of the CDOEditorInput interface.
   */
  public static CDOEditorInput createCDOEditorInput(CDOView view, String resourcePath, boolean viewOwned)
  {
    return new CDOEditorInputImpl(view, resourcePath, viewOwned);
  }

  /**
   * Creates a {@link CDOEditorInput} based on the given {@code input} that adapts to
   * the {@link IEditingDomainProvider} interface to provide a particular {@code editingDomain}.
   *
   * @param input an editor input to copy
   * @param editingDomain the editing domain to associate with the editor input
   *
   * @return the editing-domain-providing editor input
   *
   * @since 4.3
   */
  public static CDOEditorInput createCDOEditorInputWithEditingDomain(CDOEditorInput input, EditingDomain editingDomain)
  {
    return createCDOEditorInputWithEditingDomain(input.getView(), input.getResourcePath(), input.isViewOwned(), editingDomain);
  }

  /**
   * Creates a {@link CDOEditorInput} that adapts to the {@link IEditingDomainProvider} interface
   * to provide a particular {@code editingDomain}.
   *
   * @param view the CDO view of the editor input
   * @param resourcePath the path to the resource to edit
   * @param viewOwned whether the opened editor should assume ownership of the {@code view}
   * @param editingDomain the editing domain to associate with the editor input
   *
   * @return the editing-domain-providing editor input
   *
   * @since 4.3
   */
  public static CDOEditorInput createCDOEditorInputWithEditingDomain(CDOView view, String resourcePath, boolean viewOwned, EditingDomain editingDomain)
  {
    class CDOEditorInputWithEditingDomain extends CDOEditorInputImpl implements IEditingDomainProvider
    {
      private final EditingDomain editingDomain;

      CDOEditorInputWithEditingDomain(CDOView view, String resourcePath, boolean viewOwned, EditingDomain editingDomain)
      {
        super(view, resourcePath, viewOwned);
        this.editingDomain = editingDomain;
      }

      @Override
      public EditingDomain getEditingDomain()
      {
        return editingDomain;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> T getAdapter(Class<T> adapter)
      {
        if (adapter == IEditingDomainProvider.class)
        {
          return (T)this;
        }

        return super.getAdapter(adapter);
      }
    }

    return new CDOEditorInputWithEditingDomain(view, resourcePath, viewOwned, editingDomain);
  }

  /**
   * Opens the specified resource in CDOEditor
   *
   * @param page
   *          The page in which the editor will be opened
   * @param view
   *          the CDOView that will be used to access the resource
   * @param resourcePath
   *          absolute path to the resource in the repository
   */
  public static void openEditor(final IWorkbenchPage page, final CDOView view, final String resourcePath)
  {
    Display display = page.getWorkbenchWindow().getShell().getDisplay();
    UIUtil.asyncExec(display, () -> {
      try
      {
        IEditorReference[] references = findEditor(page, view, resourcePath);
        if (references.length != 0)
        {
          IEditorPart editor = references[0].getEditor(true);
          page.activate(editor);
        }
        else
        {
          IEditorInput input = createCDOEditorInput(view, resourcePath, false);
          page.openEditor(input, editorID);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    });
  }

  /**
   * Returns references to open instances of {@link CDOEditor} with given {@link CDOView}, resource path, and object ID.
   *
   * @param page
   *          The {@link IWorkbenchPage page} on which to search for open editors.
   * @param view
   *          The CDOView that the editors are filtered for, or <code>null</code> if view filtering is not applicable.
   * @param resourcePath
   *          The resource path that the editors are filtered for, or <code>null</code> if resource path filtering is not applicable.
   * @param objectID
   *          The object ID that the editors are filtered for, or <code>null</code> if object ID filtering is not applicable.
   * @since 4.8
   */
  public static IEditorReference[] findEditor(IWorkbenchPage page, CDOView view, String resourcePath, CDOID objectID)
  {
    List<IEditorReference> result = new ArrayList<>();

    for (IEditorReference editorReference : page.getEditorReferences())
    {
      try
      {
        if (!ObjectUtil.equals(editorReference.getId(), editorID))
        {
          continue;
        }

        IEditorInput editorInput = editorReference.getEditorInput();
        if (!(editorInput instanceof CDOEditorInput))
        {
          continue;
        }

        CDOEditorInput cdoInput = (CDOEditorInput)editorInput;
        if (view != null)
        {
          if (view != cdoInput.getView())
          {
            continue;
          }
        }

        if (resourcePath != null)
        {
          if (!ObjectUtil.equals(resourcePath, cdoInput.getResourcePath()))
          {
            continue;
          }
        }

        if (objectID != null)
        {
          if (!(cdoInput instanceof CDOEditorInput2))
          {
            continue;
          }

          CDOEditorInput2 cdoInput2 = (CDOEditorInput2)cdoInput;
          if (!ObjectUtil.equals(objectID, cdoInput2.getObjectID()))
          {
            continue;
          }
        }

        result.add(editorReference);
      }
      catch (PartInitException ex)
      {
        OM.LOG.error(ex);
      }
    }

    return result.toArray(new IEditorReference[result.size()]);
  }

  /**
   * Returns references to open instances of {@link CDOEditor} with given {@link CDOView}, resource path, and object ID.
   *
   * @param page
   *          The {@link IWorkbenchPage page} on which to search for open editors.
   * @param view
   *          The CDOView that the editors are filtered for, or <code>null</code> if view filtering is not applicable.
   * @param resourcePath
   *          The resource path that the editors are filtered for, or <code>null</code> if resource path filtering is not applicable.
   */
  public static IEditorReference[] findEditor(IWorkbenchPage page, CDOView view, String resourcePath)
  {
    return findEditor(page, view, resourcePath, null);
  }

  /**
   * @since 4.2
   */
  public static void populateMenu(IMenuManager manager, CDOResourceLeaf resource, IWorkbenchPage page)
  {
    String effectiveEditorID = getEffectiveEditorID(resource);
    if (effectiveEditorID != null)
    {
      manager.add(new OpenEditorAction(page, effectiveEditorID, resource, false));
    }

    String[] editorIDs = getAllEditorIDs(resource);
    if (editorIDs.length > 1 || editorIDs.length == 1 && !editorIDs[0].equals(effectiveEditorID))
    {
      MenuManager subMenu = new MenuManager("Open With");
      manager.add(subMenu);

      for (String id : editorIDs)
      {
        OpenEditorAction action = new OpenEditorAction(page, id, resource, true);
        subMenu.add(action);
        if (id.equals(effectiveEditorID))
        {
          action.setChecked(true);
        }
      }
    }
  }

  /**
   * @since 4.2
   */
  public static String getEffectiveEditorID(CDOResourceLeaf resource)
  {
    String id = EDITOR_OVERRIDES.get(resource);
    if (id != null)
    {
      return id;
    }

    id = computeEffectiveEditorID(resource);
    id = mapEditorID(id);

    return id;
  }

  /**
   * @since 4.2
   */
  public static String[] getAllEditorIDs(CDOResourceLeaf resource)
  {
    Set<String> editorIDs = new HashSet<>();
    if (resource instanceof CDOResource)
    {
      addMappedEditorID(editorIDs, EDITOR_ID);
    }

    addMappedEditorID(editorIDs, TEXT_EDITOR_ID);

    String name = resource.getName();
    for (IEditorDescriptor editorDescriptor : EDITOR_REGISTRY.getEditors(name))
    {
      addMappedEditorID(editorIDs, editorDescriptor.getId());
    }

    String[] array = editorIDs.toArray(new String[editorIDs.size()]);
    Arrays.sort(array);
    return array;
  }

  /**
   * @since 4.4
   */
  public static IEditorInput createEditorInput(String editorID, CDOResourceLeaf resource, boolean viewOwned, boolean lobCommitOnSave)
  {
    if (resource instanceof CDOResource)
    {
      if (EDITOR_ID.equals(editorID))
      {
        CDOView view = resource.cdoView();
        String path = resource.getPath();
        return createCDOEditorInput(view, path, lobCommitOnSave);
      }
    }

    return createLobEditorInput(resource, lobCommitOnSave);
  }

  /**
   * @since 4.9
   */
  public static IEditorInput createLobEditorInput(CDOResourceLeaf resource, boolean lobCommitOnSave)
  {
    return new CDOLobEditorInput(resource, lobCommitOnSave);
  }

  /**
   * Returns an implementation of the IEditorInput interface.
   *
   * @since 4.2
   */
  public static IEditorInput createEditorInput(String editorID, CDOResourceLeaf resource, boolean viewOwned)
  {
    return createEditorInput(editorID, resource, viewOwned, false);
  }

  /**
   * Returns an implementation of the IEditorInput interface.
   *
   * @since 4.2
   */
  public static IEditorInput createEditorInput(String editorID, CDOResourceLeaf resource)
  {
    return createEditorInput(editorID, resource, false);
  }

  /**
   * Opens the specified resource in CDOEditor
   *
   * @param page
   *          The page in which the editor will be opened
   * @since 4.2
   */
  public static void openEditor(IWorkbenchPage page, CDOResourceLeaf resource)
  {
    String editorID = CDOEditorUtil.getEffectiveEditorID(resource);
    if (editorID != null)
    {
      openEditor(page, editorID, resource);
    }
  }

  /**
   * Opens the specified resource in CDOEditor
   *
   * @param page
   *          The page in which the editor will be opened
   * @since 4.2
   */
  public static void openEditor(IWorkbenchPage page, String editorID, CDOResourceLeaf resource)
  {
    Display display = page.getWorkbenchWindow().getShell().getDisplay();
    UIUtil.asyncExec(display, () -> {
      try
      {
        IEditorInput editorInput = createEditorInput(editorID, resource);
        page.openEditor(editorInput, editorID);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    });
  }

  /**
   * Refreshes all editor's viewers that are using certain CDOView.
   *
   * @param page
   *          the IWorkbenchPage where CDOEditor is opened
   * @param view
   *          instance of CDOView our editors are using
   */
  public static void refreshEditors(IWorkbenchPage page, CDOView view)
  {
    IEditorReference[] references = findEditor(page, view, null);
    for (IEditorReference reference : references)
    {
      CDOEditor editor = (CDOEditor)reference.getEditor(false);
      if (editor != null)
      {
        editor.refreshViewer(null);
      }
    }
  }

  private static String computeEffectiveEditorID(CDOResourceLeaf resource)
  {
    if (resource instanceof CDOResource)
    {
      return EDITOR_ID;
    }

    String name = resource.getName();
    IEditorDescriptor editorDescriptor = EDITOR_REGISTRY.getDefaultEditor(name);
    if (editorDescriptor != null)
    {
      return editorDescriptor.getId();
    }

    return TEXT_EDITOR_ID;
  }

  private static void addMappedEditorID(Set<String> editorIDs, String id)
  {
    id = mapEditorID(id);
    if (id != null)
    {
      editorIDs.add(id);
    }
  }

  private static String mapEditorID(String id)
  {
    if (id != null && EDITOR_MAPPINGS.containsKey(id))
    {
      // The containsKey() check is necessary because the value can be null;
      id = EDITOR_MAPPINGS.get(id);
    }

    if (id != null && EDITOR_REGISTRY.findEditor(TEXT_EDITOR_ID) == null)
    {
      id = null;
    }

    return id;
  }

  private static Map<String, String> initEditorMappings()
  {
    Map<String, String> mappings = new HashMap<>();

    // JDT's PropertiesFileEditor hides our CDOLobStorage by setting AbstractTextEditor.fExplicitDocumentProvider,
    // so map it to our CDOPropertiesFileEditor, or to null to disable the editor if it's not available.
    mappings.put("org.eclipse.jdt.ui.PropertiesFileEditor", getCDOPropertiesFileEditorID());

    return mappings;
  }

  private static String getCDOPropertiesFileEditorID()
  {
    try
    {
      Class<?> c = CommonPlugin.loadClass("org.eclipse.emf.cdo.ui.jdt", "org.eclipse.emf.cdo.ui.jdt.CDOPropertiesFileEditor");
      Field field = ReflectUtil.getField(c, "ID");
      return (String)ReflectUtil.getValue(field, null);
    }
    catch (Throwable ex)
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class OpenEditorAction extends Action
  {
    private IWorkbenchPage page;

    private String editorID;

    private CDOResourceLeaf resource;

    private boolean overrideOnRun;

    public OpenEditorAction(IWorkbenchPage page, String editorID, CDOResourceLeaf resource, boolean overrideOnRun)
    {
      this.page = page;
      this.editorID = editorID;
      this.resource = resource;
      this.overrideOnRun = overrideOnRun;

      IEditorDescriptor editorDescriptor = EDITOR_REGISTRY.findEditor(editorID);
      String label = editorDescriptor.getLabel();
      if (overrideOnRun)
      {
        setText(label);
      }
      else
      {
        setText("Open With " + label);
      }

      setImageDescriptor(editorDescriptor.getImageDescriptor());
      setToolTipText("Open the " + label + " editor on this resource");
    }

    @Override
    public void run()
    {
      if (overrideOnRun)
      {
        EDITOR_OVERRIDES.put(resource, editorID);
      }

      openEditor(page, editorID, resource);
    }
  }
}
