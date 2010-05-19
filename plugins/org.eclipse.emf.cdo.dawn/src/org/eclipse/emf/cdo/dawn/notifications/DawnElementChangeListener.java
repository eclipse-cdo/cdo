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

import org.eclipse.emf.cdo.dawn.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.AbstractDocumentProvider;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * @author Martin Fluegge
 */
public class DawnElementChangeListener extends org.eclipse.emf.ecore.util.EContentAdapter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnElementChangeListener.class);

  private final DiagramDocumentEditor editor;

  public DawnElementChangeListener(DiagramDocumentEditor editor)
  {
    this.editor = editor;
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    super.notifyChanged(notification);
    if (TRACER.isEnabled())
    {
      TRACER.format("Changes on element {0} | on target  {1}", notification, target); //$NON-NLS-1$
    }

    DawnDiagramUpdater.refreshEditPart(editor.getDiagramEditPart());
    ((AbstractDocumentProvider)editor.getDocumentProvider()).changed(editor.getEditorInput());
  }
}
