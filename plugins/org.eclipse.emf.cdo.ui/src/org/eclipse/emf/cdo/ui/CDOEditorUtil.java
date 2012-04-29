/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.CDOEditorInputImpl;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import java.util.ArrayList;
import java.util.List;

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

  private static String editorID = EDITOR_ID;

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
   * Returns an implementation of CDOEditorInput interface
   */
  public static CDOEditorInput createCDOEditorInput(CDOView view, String resourcePath, boolean viewOwned)
  {
    return new CDOEditorInputImpl(view, resourcePath, viewOwned);
  }

  /**
   * Opens the specified resource in CDOEditor
   *
   * @param page
   *          The page in which the editor will be open
   * @param view
   *          the CDOView that will be used to access the resource
   * @param resourcePath
   *          absolute path to the resource in the repository
   */
  public static void openEditor(final IWorkbenchPage page, final CDOView view, final String resourcePath)
  {
    Display display = page.getWorkbenchWindow().getShell().getDisplay();
    display.asyncExec(new Runnable()
    {
      public void run()
      {
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
            IEditorInput input = CDOEditorUtil.createCDOEditorInput(view, resourcePath, false);
            page.openEditor(input, CDOEditorUtil.getEditorID());
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    });
  }

  /**
   * Returns references to possibly opened instances of CDOEditor with certain CDOView and resource
   *
   * @param page
   *          The page where to search for opened editors
   * @param view
   *          The editors to find are using the specified CDOView
   * @param resourcePath
   *          The editors are editing the CDOResource specified with this path
   */
  public static IEditorReference[] findEditor(IWorkbenchPage page, CDOView view, String resourcePath)
  {
    List<IEditorReference> result = new ArrayList<IEditorReference>();
    IEditorReference[] editorReferences = page.getEditorReferences();
    for (IEditorReference editorReference : editorReferences)
    {
      try
      {
        if (ObjectUtil.equals(editorReference.getId(), CDOEditorUtil.getEditorID()))
        {
          IEditorInput editorInput = editorReference.getEditorInput();
          if (editorInput instanceof CDOEditorInput)
          {
            CDOEditorInput cdoInput = (CDOEditorInput)editorInput;
            if (cdoInput.getView() == view)
            {
              if (resourcePath == null || ObjectUtil.equals(cdoInput.getResourcePath(), resourcePath))
              {
                result.add(editorReference);
              }
            }
          }
        }
      }
      catch (PartInitException ex)
      {
        OM.LOG.error(ex);
      }
    }

    return result.toArray(new IEditorReference[result.size()]);
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
}
