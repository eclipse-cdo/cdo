/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.bundle.CDOProtocol;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.RevisionManagerImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadObjectIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(CDOProtocol.DEBUG_PROTOCOL,
      LoadObjectIndication.class);

  private CDOID id;

  private Long timeStamp;

  public LoadObjectIndication()
  {
    super(CDOProtocolConstants.LOAD_OBJECT_SIGNAL);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    id = CDOIDImpl.read(in);
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read ID: {0}", id);
    }

    boolean historical = in.readBoolean();
    if (historical)
    {
      timeStamp = in.readLong();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read timeStamp: {0}", timeStamp);
      }
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    RevisionManagerImpl rm = getRevisionManager();
    CDORevisionImpl revision = timeStamp != null ? rm.getRevision(id, timeStamp) : rm.getRevision(id);
    revision.write(out, null);
  }
}
