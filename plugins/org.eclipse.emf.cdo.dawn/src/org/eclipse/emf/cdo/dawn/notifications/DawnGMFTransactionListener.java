/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.dawn.synchronize.DawnConflictHelper;
import org.eclipse.emf.cdo.dawn.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnGMFTransactionListener extends BasicDawnListener
{

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnGMFTransactionListener.class);

  public DawnGMFTransactionListener()
  {
  }

  public DawnGMFTransactionListener(DiagramDocumentEditor editor)
  {
    super(editor);
  }

  @Override
  public void notifyEvent(IEvent event)
  {

    if (event instanceof CDOViewInvalidationEvent)
    {
      CDOViewInvalidationEvent e = (CDOViewInvalidationEvent)event;

      if (TRACER.isEnabled())
      {
        TRACER.format("Detached Objects {0} ", e.getDetachedObjects()); //$NON-NLS-1$
        TRACER.format("Dirty Objects {0} ", e.getDirtyObjects()); //$NON-NLS-1$
      }
      adjustDeletedEdges(e);

      for (CDOObject dirtyObject : e.getDirtyObjects())
      {
        handleObject(dirtyObject);
      }

      for (CDOObject detachedObject : e.getDetachedObjects())
      {
        handleObject(detachedObject);
      }

    }
    else if (event instanceof CDOTransactionConflictEvent)
    {
      CDOTransactionConflictEvent cdoTransactionConflictEvent = (CDOTransactionConflictEvent)event;

      CDOObject cdoObject = cdoTransactionConflictEvent.getConflictingObject();

      EObject element = CDOUtil.getEObject(cdoObject); // either semantic object or notational
      View view = DawnDiagramUpdater.findView(element);

      if (DawnConflictHelper.isConflicted(cdoObject))
      {
        DawnConflictHelper.handleConflictedView(cdoObject, view, editor);
        return;
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Unhandled Event: {0} ", event); //$NON-NLS-1$
      }
    }
  }

  private void handleObject(CDOObject dirtyObject)
  {
    EObject element = CDOUtil.getEObject(dirtyObject); // either sementic object or notational
    View view = DawnDiagramUpdater.findView(element);

    if (DawnConflictHelper.isConflicted(dirtyObject))
    {
      // DawnConflictHelper.handleConflictedView(dirtyObject, view, editor);
      return;
    }

    EditPart relatedEditPart = DawnDiagramUpdater.findEditPart(view, editor.getDiagramEditPart().getViewer());
    if (relatedEditPart != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Updating EditPart {0} ", relatedEditPart); //$NON-NLS-1$
      }
      DawnDiagramUpdater.refreshEditPart(relatedEditPart.getParent(), editor);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Updating DiagramEditPart {0} ", editor.getDiagramEditPart()); //$NON-NLS-1$
      }
      DawnDiagramUpdater.refreshEditPart(editor.getDiagramEditPart(), editor);
    }
  }
  // TODO: Clarify teh relevance of this method especially the synchronization of the edge and removed it if it is not
  // needed anymore
  // private void handleView(View view, CDOObject object)
  // {
  // if (view instanceof Node)
  // {
  // if (TRACER.isEnabled())
  // {
  //        TRACER.format("Handle Node: ", view); //$NON-NLS-1$
  // }
  //
  // Node node = (Node)view;
  // // final Node finalNode = (Node)view;
  // // editor.getEditingDomain().getCommandStack().execute(new RecordingCommand(editor.getEditingDomain())
  // // {
  // // public void doExecute()
  // // {
  // // finalNode.getTargetEdges().clear();
  // // finalNode.getSourceEdges().clear();
  // // }
  // // });
  //
  // // handleViewsEdge(node.getSourceEdges());
  // // handleViewsEdge(node.getTargetEdges());
  //
  // for (Object child : node.getChildren())
  // {
  // handleView((View)child, object);
  // }
  // }
  // else if (view instanceof Edge)
  // {
  // if (TRACER.isEnabled())
  // {
  //        TRACER.format("Handle Edge: ", view); //$NON-NLS-1$
  // }
  //
  // Edge edge = (Edge)view;
  // Node source = (Node)edge.getSource();
  // Node target = (Node)edge.getTarget();
  // DawnDiagramUpdater.createOrFindEditPartIfViewExists(source, editor).refresh();
  // DawnDiagramUpdater.createOrFindEditPartIfViewExists(target, editor).refresh();
  // DawnDiagramUpdater.createOrFindEditPartIfViewExists(edge, editor).refresh();
  // }
  // else if (view instanceof Diagram)
  // {
  // if (TRACER.isEnabled())
  // {
  //        TRACER.format("Handle Diagram: ", view); //$NON-NLS-1$
  // }
  // }
  //
  // EditPart editPart = DawnDiagramUpdater.createOrFindEditPartIfViewExists(view, editor);
  //
  // if (object.cdoConflict())
  // {
  // DawnAppearancer.setEdiPartConflicted(editPart, DawnAppearancer.TYPE_CONFLICT_LOCALLY_DELETED);
  // }
  // }

  // private void handleViewsEdge(List edges)
  // {
  // for (Object o : edges)
  // {
  // System.out.println("\t-targetedge: " + o);
  // }
  // }
}
