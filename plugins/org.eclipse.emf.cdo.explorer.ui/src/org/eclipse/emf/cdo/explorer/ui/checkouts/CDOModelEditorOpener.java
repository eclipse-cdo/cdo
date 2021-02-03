/*
 * Copyright (c) 2015, 2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.InteractiveConflictHandlerSelector;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorOpener;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;

import org.eclipse.emf.internal.cdo.transaction.CDOHandlingConflictResolver;

import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Eike Stepper
 */
public class CDOModelEditorOpener extends CDOEditorOpener.Default
{
  private static final boolean INTERACTIVE_CONFLICT_RESOLUTION = OMPlatform.INSTANCE.isProperty("cdo.interactive.conflict.resolution", true);

  public CDOModelEditorOpener()
  {
  }

  @Override
  protected IEditorPart doOpenEditor(IWorkbenchPage page, URI uri)
  {
    CDOModelEditorInput editorInput = new CDOModelEditorInput(uri);
    String editorID = CDOEditorUtil.getEditorID();

    try
    {
      return page.openEditor(editorInput, editorID);
    }
    catch (PartInitException ex)
    {
      OM.LOG.error(ex);
      return null;
    }
  }

  /**
   * @deprecated As of 4.6 no longer supported in favor of CDOModelEditorInput.configureTransaction().
   */
  @Deprecated
  protected void configureTransaction(CDOTransaction transaction)
  {
    addConflictResolver(transaction);
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
