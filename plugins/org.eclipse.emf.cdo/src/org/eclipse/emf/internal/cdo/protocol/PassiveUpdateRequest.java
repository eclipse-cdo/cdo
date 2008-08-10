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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class PassiveUpdateRequest extends SyncRevisionRequest
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, PassiveUpdateRequest.class);

  private boolean passiveUpdateEnabled;

  public PassiveUpdateRequest(IChannel channel, CDOSessionImpl session, Map<CDOID, CDORevision> cdoRevisions,
      int referenceChunk, boolean passiveUpdateEnabled)
  {
    super(channel, session, cdoRevisions, referenceChunk);
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.trace("Turning " + (passiveUpdateEnabled ? "on" : "off") + " passive update");
    }

    super.requesting(out);
    out.writeBoolean(passiveUpdateEnabled);
  }
}
