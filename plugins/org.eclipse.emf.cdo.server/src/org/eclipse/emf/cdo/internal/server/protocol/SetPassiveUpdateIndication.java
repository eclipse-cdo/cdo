/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class SetPassiveUpdateIndication extends SyncRevisionsIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SetPassiveUpdateIndication.class);

  public SetPassiveUpdateIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    super.indicating(in);
    boolean passiveUpdateEnabled = in.readBoolean();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Turning " + (passiveUpdateEnabled ? "on" : "off") + " passive update");
    }

    getSession().setPassiveUpdateEnabled(passiveUpdateEnabled);
  }
}
