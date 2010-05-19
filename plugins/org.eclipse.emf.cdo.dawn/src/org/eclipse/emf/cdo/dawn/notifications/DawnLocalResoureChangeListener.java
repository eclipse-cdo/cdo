/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.dawn.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.AbstractDocumentProvider;

/**
 * @author Martin Fluegge
 */
public class DawnLocalResoureChangeListener extends AbstractDawnResoureChangeListener// org.eclipse.emf.ecore.util.EContentAdapter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnLocalResoureChangeListener.class);

  @Override
  public void notifyChanged(Notification notification)
  {

    if (TRACER.isEnabled())
    {
      TRACER.format("DawnLocalResoureChangeListener fired with notification: ", notification); //$NON-NLS-1$
    }

    // super.notifyChanged(notification);
    // if (editor.getDocumentProvider() != null)
    // {
    // View view = DawnDiagramUpdater.findView((EObject)notification.getNotifier());
    // EditPart relatedEditPart = DawnDiagramUpdater.findEditPart(view, editor.getDiagramEditPart().getViewer());
    // if (relatedEditPart != null)
    // {
    // LOG.info("Updating EditPart " + relatedEditPart);
    //
    // DawnDiagramUpdater.refreshEditPart(relatedEditPart.getParent(), editor);
    // }
    //
    DawnDiagramUpdater.refreshEditPart(editor.getDiagramEditPart(), editor);
    ((AbstractDocumentProvider)editor.getDocumentProvider()).changed(editor.getEditorInput());
    // }
  }

}
