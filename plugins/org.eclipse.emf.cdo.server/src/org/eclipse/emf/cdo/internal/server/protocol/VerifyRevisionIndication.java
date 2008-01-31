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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDUtil;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class VerifyRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, VerifyRevisionIndication.class);

  protected long[] timeStamps;

  public VerifyRevisionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_VERIFY_REVISION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Reading {0} IDs and versions", size);

    RevisionManager revisionManager = getRevisionManager();
    timeStamps = new long[size];
    for (int i = 0; i < size; i++)
    {
      CDOID id = CDOIDUtil.read(in);
      int version = in.readInt();
      if (PROTOCOL.isEnabled()) PROTOCOL.format("Read ID and version: {0}v{1}", id, version);

      InternalCDORevision revision = revisionManager.getRevisionByVersion(id, 0, version);
      timeStamps[i] = revision.getRevised();
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    for (long revised : timeStamps)
    {
      out.writeLong(revised);
    }
  }
}
