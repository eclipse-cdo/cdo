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
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionIndication.class);

  private CDOID id;

  private Long timeStamp;

  public LoadRevisionIndication()
  {
    super(CDOProtocolConstants.SIGNAL_LOAD_REVISION);
  }

  @Override
  protected void accessStore(ExtendedDataInputStream in) throws IOException
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
    RevisionManager rm = getRevisionManager();
    CDORevisionImpl revision = timeStamp != null ? rm.getRevision(id, timeStamp) : rm.getRevision(id);
    revision.write(out, getSession());
  }
}
