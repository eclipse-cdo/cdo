/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.InteractiveConflictHandlerSelector;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorOpener;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.transaction.CDOHandlingConflictResolver;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Eike Stepper
 */
public class CDOModelEditorOpener extends CDOEditorOpener.Default
{
  private static final boolean INTERACTIVE_CONFLICT_RESOLUTION = !"false"
      .equalsIgnoreCase(System.getProperty("cdo.interactive.conflict.resolution"));

  public CDOModelEditorOpener()
  {
  }

  @Override
  protected IEditorPart doOpenEditor(final IWorkbenchPage page, URI uri)
  {
    CDOCheckout checkout = CDOExplorerUtil.getCheckout(uri);
    if (checkout == null)
    {
      MessageDialog.openError(page.getWorkbenchWindow().getShell(), "Error",
          "The checkout for " + uri + " could not be found.");
      return null;
    }

    final CDOView view = checkout.openView();

    if (view instanceof CDOTransaction)
    {
      configureTransaction((CDOTransaction)view);
    }

    final IEditorPart editor = openEditor(page, view, CDOURIUtil.extractResourcePath(uri));
    page.addPartListener(new IPartListener()
    {
      public void partClosed(IWorkbenchPart part)
      {
        if (part == editor)
        {
          try
          {
            view.close();
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
          finally
          {
            page.removePartListener(this);
          }
        }
      }

      public void partOpened(IWorkbenchPart part)
      {
        // Do nothing.
      }

      public void partDeactivated(IWorkbenchPart part)
      {
        // Do nothing.
      }

      public void partBroughtToTop(IWorkbenchPart part)
      {
        // Do nothing.
      }

      public void partActivated(IWorkbenchPart part)
      {
        // Do nothing.
      }
    });

    return editor;
  }

  protected void configureTransaction(CDOTransaction transaction)
  {
    addConflictResolver(transaction);
  }

  private IEditorPart openEditor(IWorkbenchPage page, CDOView view, String resourcePath)
  {
    try
    {
      String editorID = CDOEditorUtil.getEditorID();

      IEditorReference[] references = CDOEditorUtil.findEditor(page, view, resourcePath);
      for (IEditorReference reference : references)
      {
        if (editorID.equals(reference.getId()))
        {
          IEditorPart editor = references[0].getEditor(true);
          page.activate(editor);
          return editor;
        }
      }

      IEditorInput input = CDOEditorUtil.createCDOEditorInput(view, resourcePath, false);
      return page.openEditor(input, editorID);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return null;
  }

  public static void addConflictResolver(CDOTransaction transaction)
  {
    if (INTERACTIVE_CONFLICT_RESOLUTION)
    {
      CDOHandlingConflictResolver conflictResolver = new CDOHandlingConflictResolver();
      conflictResolver.setConflictHandlerSelector(new InteractiveConflictHandlerSelector());

      transaction.options().addConflictResolver(conflictResolver);
    }
    else
    {
      transaction.options().addConflictResolver(new CDOMergingConflictResolver());
    }
  }
}
