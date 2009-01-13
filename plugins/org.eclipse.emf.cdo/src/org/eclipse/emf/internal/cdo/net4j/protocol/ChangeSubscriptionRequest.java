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
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      ChangeSubscriptionRequest.class);

  private int viewID;

  private List<CDOID> cdoIDs;

  /**
   * true - it will subscribe id's. <br>
   * false - it will unsubscribe id's.
   */
  private boolean subscribeMode;

  private boolean clear;

  public ChangeSubscriptionRequest(CDOClientProtocol protocol, int viewID, List<CDOID> cdoIDs, boolean subscribeMode,
      boolean clear)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION);
    this.viewID = viewID;
    this.cdoIDs = cdoIDs;
    this.subscribeMode = subscribeMode;
    this.clear = clear;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.trace("View " + viewID + " subscribing to " + cdoIDs.size());
    }

    out.writeInt(viewID);
    out.writeBoolean(clear);
    out.writeInt(subscribeMode ? cdoIDs.size() : -cdoIDs.size());
    for (CDOID id : cdoIDs)
    {
      out.writeCDOID(id);
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
