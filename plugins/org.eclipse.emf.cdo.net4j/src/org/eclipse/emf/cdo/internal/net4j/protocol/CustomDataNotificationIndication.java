/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CustomDataNotificationIndication extends CDOClientIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      CustomDataNotificationIndication.class);

  public CustomDataNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CUSTOM_DATA_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int senderID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Read senderID: " + senderID); //$NON-NLS-1$
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

    InternalCDORemoteSessionManager remoteSessionManager = getSession().getRemoteSessionManager();
    remoteSessionManager.handleRemoteSessionCustomData(senderID, type, data);
  }
}
