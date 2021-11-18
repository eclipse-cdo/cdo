/*
 * Copyright (c) 2009-2012, 2015-2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - bug 230832
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionRequest extends CDOClientRequest<Boolean>
{
  private int viewID;

  private List<CDOID> ids;

  /**
   * true - it will subscribe id's. <br>
   * false - it will unsubscribe id's.
   */
  private boolean subscribeMode;

  private boolean clear;

  public ChangeSubscriptionRequest(CDOClientProtocol protocol, int viewID, List<CDOID> ids, boolean subscribeMode, boolean clear)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION);
    this.viewID = viewID;
    this.ids = ids;
    this.subscribeMode = subscribeMode;
    this.clear = clear;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(viewID);
    out.writeBoolean(clear);
    out.writeXInt(subscribeMode ? ids.size() : -ids.size());
    for (CDOID id : ids)
    {
      out.writeCDOID(id);
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }

  @Override
  protected String getAdditionalInfo()
  {
    return "viewID=" + viewID + ", ids=" + ids;
  }
}
