/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOState;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.impl.CDOCallback;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

/**
 * @author Eike Stepper
 */
public class CDOCallbackImpl extends CDOAdapterImpl implements CDOCallback
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOCallbackImpl.class);

  public CDOCallbackImpl()
  {
  }

  public void beforeRead(EObjectImpl instance)
  {
    CDOStateMachine.INSTANCE.read(this);
  }

  public void beforeWrite(EObjectImpl instance)
  {
    CDOStateMachine.INSTANCE.write(this);
  }

  @Override
  public void notifyChanged(Notification msg)
  {
    // Do nothing
  }

  @Override
  public void cdoInternalSetState(CDOState state)
  {
    // TODO Move common logic to CDOObjectImpl (see CDOAdapterImpl)
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this);
      }

      this.state = state;
    }
    else
    {
      // TODO Detect duplicate setstate calls
    }
  }
}
