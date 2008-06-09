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

import org.eclipse.emf.cdo.common.CDOProtocolConstants;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ViewsChangedRequest extends CDOClientRequest<Boolean> // Request
{
  private int viewID;

  private byte kind;

  public ViewsChangedRequest(IChannel channel, int viewID, byte kind)
  {
    super(channel);
    this.viewID = viewID;
    this.kind = kind;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_VIEWS_CHANGED;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(viewID);
    out.writeByte(kind);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readBoolean();
  }
}
