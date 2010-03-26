/*******************************************************************************
 * Copyright (c) 2009 - 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
/**
 * 
 * @author Martin Fluegge
 */
public class DawnDiagramUpdater
{

  public static void refreshEditPart(EditPart editPart)
  {
    refeshEditpartInternal(editPart);
  }

  public static void refreshEditPart(final EditPart editPart, DiagramDocumentEditor editor)
  {
    editor.getEditingDomain().getCommandStack().execute(new RecordingCommand(editor.getEditingDomain())
    {
      public void doExecute()
      {
        DawnDiagramUpdater.refreshEditPart(editPart);
      }
    });

  }

  public static void refreshEditCurrentSelected(TransactionalEditingDomain editingDomain)
  {
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {

      @Override
      protected void doExecute()
      {
        // ((ExamplediagramDocumentProvider)getDocumentProvider()).changed(getEditorInput());
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        if (selection instanceof IStructuredSelection)
        {
          IStructuredSelection structuredSelection = (IStructuredSelection)selection;
          if (structuredSelection.size() != 1)
          {
            return;
          }
          if (structuredSelection.getFirstElement() instanceof EditPart
              && ((EditPart)structuredSelection.getFirstElement()).getModel() instanceof View)
          {
            EObject modelElement = ((View)((EditPart)structuredSelection.getFirstElement()).getModel()).getElement();
            List editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(modelElement);
            for (Iterator it = editPolicies.iterator(); it.hasNext();)
            {
              CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy)it.next();
              nextEditPolicy.refresh();
            }

          }
        }
      }
    });
  }

  private static void refeshEditpartInternal(EditPart editPart)
  {
    if (editPart != null)
    {
      try
      {
        editPart.refresh();

        // EObject modelElement = ((View)(editPart).getModel()).getElement();
        // List editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(modelElement);
        // for (Iterator it = editPolicies.iterator(); it.hasNext();)
        // {
        // CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy)it.next();
        // nextEditPolicy.refresh();
        //        
        // }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      for (Object childEditPart : editPart.getChildren())
      {
        if (childEditPart instanceof EditPart)
        {
          refeshEditpartInternal((EditPart)childEditPart);
        }
      }

      if (editPart instanceof DiagramEditPart)
      {
        for (Object childEditPart : ((DiagramEditPart)editPart).getConnections())
        {
          if (childEditPart instanceof EditPart)
          {
            System.out.println("--connection---");
            refeshEditpartInternal((EditPart)childEditPart);
          }
        }
      }

    }
    else
    {
      System.err.println("EDITPART is null");
    }

  }

}
