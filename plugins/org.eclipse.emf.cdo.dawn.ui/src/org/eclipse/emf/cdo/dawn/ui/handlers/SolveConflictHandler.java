/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ui.handlers;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.dawn.internal.ui.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;

/**
 * @author Martin Fluegge
 */
public class SolveConflictHandler extends AbstractHandler
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, SolveConflictHandler.class);

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    IEditorPart activeEditor = DawnEditorHelper.getActiveEditor();

    if (TRACER.isEnabled())
    {
      TRACER.format("Start solving conflicts for {0}", activeEditor); //$NON-NLS-1$
    }

    if (activeEditor instanceof IDawnEditor)
    {
      MessageDialog dialog = new MessageDialog(DawnEditorHelper.getActiveShell(), "Conflict", null,
          "There are conflicts in your diagram. Would you like to rollback your current transaction?", MessageDialog.QUESTION,
          new String[] { "yes", "no", "Cancel" }, 1);

      switch (dialog.open())
      {
      case 0: // yes
        ((IDawnEditor)activeEditor).getDawnEditorSupport().rollback();
        break;
      case 1: // no
        break;
      default: // cancel
        break;
      }
    }
    return null;
  }
}
