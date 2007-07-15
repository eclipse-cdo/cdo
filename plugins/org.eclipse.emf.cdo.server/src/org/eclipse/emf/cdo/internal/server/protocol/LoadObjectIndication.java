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
import org.eclipse.emf.cdo.internal.protocol.bundle.OM;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadObjectIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadObjectIndication.class);

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
    final CDORevisionImpl[] revision = new CDORevisionImpl[1];
    transact(new Runnable()
    {
      public void run()
      {
        RevisionManager rm = getRevisionManager();
        revision[0] = timeStamp != null ? rm.getRevision(id, timeStamp) : rm.getRevision(id);
      }
    });

    revision[0].write(out, null);
  }
}
