/*
 * Copyright (c) 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.actions;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Since Dawn 0.2 this class is deprecated and replaced with "org.eclipse.emf.cdo.dawn.handlers.SolveConflictHandler".
 * It will be removed soon.
 *
 * @author Martin Fluegge
 */
@Deprecated
public class HandleConflictsAction implements IObjectActionDelegate
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HandleConflictsAction.class);

  private Object selectedElement;

  @Deprecated
  public final static String ID = "org.eclipse.emf.cdo.dawn.actions.HandleConflictAction";

  @Deprecated
  @Override
  public void run(IAction action)
  {

    if (TRACER.isEnabled())
    {
      TRACER.format("Start solving conflicts for {0}", selectedElement); //$NON-NLS-1$
    }

    IEditorPart activeEditor = DawnEditorHelper.getActiveEditor();
    if (activeEditor instanceof IDawnEditor)
    {
      MessageDialog dialog = new MessageDialog(DawnEditorHelper.getActiveShell(), "Conflict", null,
          "There are conflicts in your diagram. Would you like to rollback your current transaction?", MessageDialog.QUESTION,
          new String[] { "yes", "no", "Cancel" }, 1);

      switch (dialog.open())
      {
      case 0: // yes
        // DawnConflictHelper.rollback((DiagramDocumentEditor)activeEditor);
        ((IDawnEditor)activeEditor).getDawnEditorSupport().rollback();
        break;
      case 1: // no
        break;
      default: // cancel
        break;
      }
    }
  }

  @Deprecated
  @Override
  public void selectionChanged(IAction action, ISelection selection)
  {
    selectedElement = null;
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection structuredSelection = (IStructuredSelection)selection;
      // if (structuredSelection.getFirstElement() instanceof EditPart)
      {
        selectedElement = structuredSelection.getFirstElement();
      }
    }
  }

  @Deprecated
  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }
}
