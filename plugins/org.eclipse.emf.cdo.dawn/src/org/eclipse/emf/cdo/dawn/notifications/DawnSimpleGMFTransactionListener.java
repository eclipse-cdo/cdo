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
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * @author Martin Fluegge
 */
public class DawnSimpleGMFTransactionListener extends BasicDawnListener
{

  public DawnSimpleGMFTransactionListener()
  {
  }

  public DawnSimpleGMFTransactionListener(DiagramDocumentEditor editor)
  {
    super(editor);
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOViewInvalidationEvent)
    {
      CDOViewInvalidationEvent cdoViewInvalidationEvent = (CDOViewInvalidationEvent)event;
      adjustDeletedEdges(cdoViewInvalidationEvent);
      if (editor.getDocumentProvider() != null)
      {
        DawnDiagramUpdater.refreshEditPart(editor.getDiagramEditPart(), editor);
      }
      // handleConflicts(cdoViewInvalidationEvent);
    }
  }
}
