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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.AbstractDocumentProvider;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnResoureChangeListener extends AbstractDawnResoureChangeListener
{

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnResoureChangeListener.class);

  public DawnResoureChangeListener()
  {
    super();
  }

  public DawnResoureChangeListener(DiagramDocumentEditor editor)
  {
    super(editor);
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("DawnResoureChangeListener fired. {0} ", notification); //$NON-NLS-1$
    }

    if (notification.getOldValue() instanceof DawnResoureChangeListener)
    {
      // TODO handle this better
      if (TRACER.isEnabled())
      {
        TRACER.trace("No notification for ArrayDelegatingAdapterList"); //$NON-NLS-1$
      }
      return;
    }
    super.notifyChanged(notification);

    if (TRACER.isEnabled())
    {
      TRACER.format("Changing feature {0} ", notification.getFeature()); //$NON-NLS-1$
      TRACER.format("\t-OLD {0} ", notification.getOldValue()); //$NON-NLS-1$
      TRACER.format("\t-NEW {0} ", notification.getNewValue()); //$NON-NLS-1$
    }

    CDOObject cdoObject = CDOUtil.getCDOObject((EObject)notification.getNotifier());

    CDOView cdoView = cdoObject.cdoView();

    if (!(cdoView instanceof CDOTransaction))
    {
      // no update if we are running on a view
      return;
    }
    else if (cdoView instanceof CDOTransaction)
    {
      CDOTransaction transaction = (CDOTransaction)cdoView;
      if (transaction.isClosed())
      {
        // do not act on a close transaction
        return;
      }
    }

    EObject element = CDOUtil.getEObject(cdoObject); // either sementic object or notational

    View view = DawnDiagramUpdater.findViewByContainer(element);
    if (view == null)
    {
      view = DawnDiagramUpdater.findViewForModel(element, editor);
    }
    DawnConflictHelper.handleConflictedView(cdoObject, view, editor);

    EditPart relatedEditPart = DawnDiagramUpdater.findEditPart(view, editor.getDiagramEditPart().getViewer());
    if (relatedEditPart != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Updating EditPart {0} ", relatedEditPart); //$NON-NLS-1$
      }

      DawnDiagramUpdater.refreshEditPart(relatedEditPart.getParent(), editor);
      // if(view instanceof Edge)
      // {
      // Edge edge = (Edge)view;
      // EditPart source = DawnDiagramUpdater.findEditPart(edge.getSource(), editor.getDiagramEditPart().getViewer());
      // EditPart target = DawnDiagramUpdater.findEditPart(edge.getTarget(), editor.getDiagramEditPart().getViewer());
      // if(source!=null)
      // DawnDiagramUpdater.refreshEditPart(source, editor);
      // if(target!=null)
      // DawnDiagramUpdater.refreshEditPart(target, editor);
      // }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Updating DiagramEditPart {0} ", editor.getDiagramEditPart()); //$NON-NLS-1$
      }
      DawnDiagramUpdater.refreshEditPart(editor.getDiagramEditPart(), editor);
    }
    // set the editor dirty
    ((AbstractDocumentProvider)editor.getDocumentProvider()).changed(editor.getEditorInput());
  }
}
