/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    				 https://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class PassiveUpdateIndication extends SyncRevisionIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, PassiveUpdateIndication.class);

  public PassiveUpdateIndication()
  {
    super();
  }
  
  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE;
  }
  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    super.indicating(in);
	  boolean passiveUpdateEnabled = in.readBoolean();
	  
    if (PROTOCOL.isEnabled()) PROTOCOL.trace("Turning " + (passiveUpdateEnabled ? "on" : "off") + " passive update");
	  
	  getSession().setPassiveUpdateEnabled(passiveUpdateEnabled);
	  	
  }
}
