/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CustomDataIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CustomDataIndication.class);

  public CustomDataIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CUSTOM_DATA);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int receiverID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Read receiverID: " + receiverID); //$NON-NLS-1$
    }

    String type = in.readString();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Read type: " + type); //$NON-NLS-1$
    }

    byte[] data = in.readByteArray();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Read data: " + data); //$NON-NLS-1$
    }

    InternalSessionManager sessionManager = getRepository().getSessionManager();
    InternalSession receiver = sessionManager.getSession(receiverID);
    sessionManager.sendCustomData(getSession(), receiver, type, data);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
