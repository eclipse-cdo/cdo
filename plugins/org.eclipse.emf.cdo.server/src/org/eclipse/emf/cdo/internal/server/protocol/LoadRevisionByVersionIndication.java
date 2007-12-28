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

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByVersionIndication extends LoadRevisionIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL,
      LoadRevisionByVersionIndication.class);

  private int version;

  public LoadRevisionByVersionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    super.indicating(in);
    version = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read version: {0}", version);
  }

  @Override
  protected CDORevisionImpl getRevision(CDOID id)
  {
    return getRevisionManager().getRevisionByVersion(id, referenceChunk, version);
  }
}
