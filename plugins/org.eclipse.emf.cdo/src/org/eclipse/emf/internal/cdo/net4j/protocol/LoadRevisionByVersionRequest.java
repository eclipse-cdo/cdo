/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByVersionRequest extends LoadRevisionRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionByVersionRequest.class);

  private int version;

  public LoadRevisionByVersionRequest(CDOClientProtocol protocol, CDOID id, int referenceChunk, int version)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION, Collections.singleton(id), referenceChunk);
    this.version = version;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    super.requesting(out);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing version: {0}", version);
    }

    out.writeInt(version);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}(ids={1}, referenceChunk={2}, version={3})", getClass().getSimpleName(), getIds(),
        getReferenceChunk(), version);
  }
}
