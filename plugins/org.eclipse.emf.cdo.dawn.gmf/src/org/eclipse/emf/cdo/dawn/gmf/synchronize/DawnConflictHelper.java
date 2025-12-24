/*
 * Copyright (c) 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *     Christian W. Damus (CEA) - Bug 404184: handle View that has no element
 */
package org.eclipse.emf.cdo.dawn.gmf.synchronize;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnConflictHelper
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnConflictHelper.class);

  public static boolean handleConflictedView(CDOObject cdoObject, View view, DiagramDocumentEditor editor)
  {
    boolean cdoConflict = cdoObject.cdoConflict();
    if (cdoConflict && view != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Object ({0}) is in state conflict!", cdoObject); //$NON-NLS-1$
      }
      EditPart editPart = DawnDiagramUpdater.createOrFindEditPartIfViewExists(view, editor);
      int typeConflictLocallyDeleted = evaluateConflictType(cdoObject);
      DawnAppearancer.setEditPartConflicted(editPart, typeConflictLocallyDeleted);
    }
    return cdoConflict;
  }

  private static int evaluateConflictType(CDOObject cdoObject)
  {
    if (cdoObject.cdoState() == CDOState.DIRTY)
    {
      return DawnAppearancer.TYPE_CONFLICT_REMOTELY_AND_LOCALLY_CHANGED;
    }
    return DawnAppearancer.TYPE_CONFLICT_REMOTELY_DELETED;
  }

  public static void rollback(final DiagramDocumentEditor editor)
  {
    CDOView view = ((IDawnEditor)editor).getView();

    if (view != null && view instanceof CDOTransaction)
    {
      ((CDOTransaction)view).rollback();
    }

    editor.getEditingDomain().getCommandStack().execute(new RecordingCommand(editor.getEditingDomain())
    {
      @Override
      public void doExecute()
      {
        DawnAppearancer.setEditPartDefaultAllChildren(editor.getDiagramEditPart());
        DawnDiagramUpdater.refreshEditPart(editor.getDiagramEditPart());
      }
    });
  }

  /**
   * TODO this method should decide whether the object is conflicted or not using special Policies
   *
   * @param object a non-{@code null} object
   * @return whether the object is conflicted
   */
  public static boolean isConflicted(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject.cdoConflict())
    {
      return true;
    }
    if (object instanceof View)
    {
      // if the view is not, itself, conflicted, maybe its semantic element is (if it has one)
      EObject element = ((View)object).getElement();
      if (element != null)
      {
        CDOObject cdoElement = CDOUtil.getCDOObject(element);
        return cdoElement.cdoConflict();
      }
    }
    return false;
  }
}
