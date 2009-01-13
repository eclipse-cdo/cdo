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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

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
    super(protocol, CDOProtocolConstants.SIGNAL_VIEWS_CHANGED);
    this.viewID = viewID;
    this.kind = kind;
    this.timeStamp = timeStamp;
  }

  public ViewsChangedRequest(CDOClientProtocol protocol, int viewID)
  {
    this(protocol, viewID, CDOProtocolConstants.VIEW_CLOSED, CDOCommonView.UNSPECIFIED_DATE);
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
