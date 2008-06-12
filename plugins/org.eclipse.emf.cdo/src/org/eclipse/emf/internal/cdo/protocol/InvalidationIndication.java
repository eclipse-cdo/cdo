/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class InvalidationIndication extends Indication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, InvalidationIndication.class);

  public InvalidationIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_INVALIDATION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    long timeStamp = in.readLong();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read timeStamp: {0,date} {0,time}", timeStamp);
    }

    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} dirty IDs", size);
    }

    CDOSessionImpl session = getSession();
    Set<CDOIDAndVersion> dirtyOIDs = new HashSet<CDOIDAndVersion>();
    for (int i = 0; i < size; i++)
    {
      CDOIDAndVersion dirtyOID = CDOIDUtil.readIDAndVersion(in, session);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read dirty ID: {0}", dirtyOID);
      }

      dirtyOIDs.add(dirtyOID);
    }

    session.notifyInvalidation(timeStamp, dirtyOIDs, null);
  }

  protected CDOSessionImpl getSession()
  {
    return (CDOSessionImpl)getProtocol().getInfraStructure();
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }
}
