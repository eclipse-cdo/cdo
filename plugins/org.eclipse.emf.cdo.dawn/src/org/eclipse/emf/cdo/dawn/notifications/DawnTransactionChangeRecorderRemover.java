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
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * @author Martin Fluegge
 */
public class DawnTransactionChangeRecorderRemover implements IListener
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnTransactionChangeRecorderRemover.class);

  private final DiagramDocumentEditor editor;

  public DawnTransactionChangeRecorderRemover(DiagramDocumentEditor editor)
  {
    this.editor = editor;

  }

  public void notifyEvent(IEvent event)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Starting DawnTransactionChangeRecorderRemover with Event: {0} ", event); //$NON-NLS-1$
    }

    if (event instanceof CDOViewInvalidationEvent)
    {
      CDOViewInvalidationEvent cdoViewInvalidationEvent = (CDOViewInvalidationEvent)event;

      if (editor.getDocumentProvider() != null)
      {

        for (CDOObject object : cdoViewInvalidationEvent.getDirtyObjects())
        {
          // TODO find a better solution maybe be writing an own TransactionalEditingDomain
          DawnNotificationUtil.removeTransactionChangeRecorder(object);
        }

        // for (Object node : editor.getDiagram().getChildren())
        // {
        // System.out.println(node + " " + ((EObject)node).eAdapters());
        // }
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Unhandled event:  {0} ", event); //$NON-NLS-1$
      }
    }
  }
}
