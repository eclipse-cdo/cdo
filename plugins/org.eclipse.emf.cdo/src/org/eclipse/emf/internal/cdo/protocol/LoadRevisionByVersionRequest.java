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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.internal.cdo.bundle.OM;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByVersionRequest extends LoadRevisionRequest
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionByVersionRequest.class);

  private int version;

  public LoadRevisionByVersionRequest(IChannel channel, CDOID id, int referenceChunk, int version)
  {
    super(channel, id, referenceChunk);
    this.version = version;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    super.requesting(out);
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing version: {0}", version);
    out.writeInt(version);
  }
}
