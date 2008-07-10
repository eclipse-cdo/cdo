/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    				 https://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, ChangeSubscriptionRequest.class);

  private int viewID;

  private List<CDOID> cdoIDs;

  private boolean subscribeMode;

  private boolean clear;

  public ChangeSubscriptionRequest(IChannel channel, int viewID, List<CDOID> cdoIDs, boolean subscribeMode,
      boolean clear)
  {
    super(channel);
    this.viewID = viewID;
    this.cdoIDs = cdoIDs;
    this.subscribeMode = subscribeMode;
    this.clear = clear;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled()) PROTOCOL.trace("View " + viewID + " subscribing to " + cdoIDs.size());

    out.writeInt(viewID);
    out.writeBoolean(clear);
    out.writeInt(subscribeMode ? cdoIDs.size() : -cdoIDs.size());
    for (CDOID id : cdoIDs)
      CDOIDUtil.write(out, id);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readBoolean();
  }
}
