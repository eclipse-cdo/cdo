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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ViewsChangedRequest extends CDOClientRequest<Boolean>
{
  private int viewID;

  private byte kind;

  private long timeStamp;

  public ViewsChangedRequest(CDOClientProtocol protocol, int viewID, byte kind, long timeStamp)
  {
    super(protocol);
    this.viewID = viewID;
    this.kind = kind;
    this.timeStamp = timeStamp;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_VIEWS_CHANGED;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(viewID);
    out.writeByte(kind);
    if (kind == CDOProtocolConstants.VIEW_AUDIT)
    {
      out.writeLong(timeStamp);
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
