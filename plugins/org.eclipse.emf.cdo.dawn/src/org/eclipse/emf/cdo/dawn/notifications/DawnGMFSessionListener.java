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
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Martin Fluegge
 */
public class DawnGMFSessionListener implements IListener
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnGMFSessionListener.class);

  public void notifyEvent(IEvent event)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Starting SessionListener with Event: ", event); //$NON-NLS-1$
    }
    if (event instanceof CDOSessionInvalidationEvent)
    {

      CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
      if (TRACER.isEnabled())
      {
        TRACER.format("Detached Objects {0} ", e.getDetachedObjects()); //$NON-NLS-1$
        TRACER.format("New Objects {0} ", e.getNewObjects()); //$NON-NLS-1$
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Finished SessionListener with Event: ", event); //$NON-NLS-1$
      }
    }
  }
}
