/*
 * Copyright (c) 2011, 2012, 2015, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class EnableLockNotificationRequest extends CDOClientRequest<Boolean>
{
  private int viewID;

  private boolean on;

  public EnableLockNotificationRequest(CDOClientProtocol protocol, int viewID, boolean on)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_ENABLE_LOCK_NOTIFICATION);
    this.viewID = viewID;
    this.on = on;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(viewID);
    out.writeBoolean(on);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }

  @Override
  protected String getAdditionalInfo()
  {
    return "viewID=" + viewID + ", " + (on ? "ON" : "OFF");
  }
}
