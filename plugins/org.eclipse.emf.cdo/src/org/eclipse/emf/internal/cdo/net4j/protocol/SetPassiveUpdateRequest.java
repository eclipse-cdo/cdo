/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class SetPassiveUpdateRequest extends SyncRevisionsRequest
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      SetPassiveUpdateRequest.class);

  private boolean passiveUpdateEnabled;

  public SetPassiveUpdateRequest(CDOClientProtocol protocol, Map<CDOID, CDORevision> revisions, int referenceChunk,
      boolean passiveUpdateEnabled)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE, revisions, referenceChunk);
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.trace("Turning " + (passiveUpdateEnabled ? "on" : "off") + " passive update");
    }

    super.requesting(out);
    out.writeBoolean(passiveUpdateEnabled);
  }
}
