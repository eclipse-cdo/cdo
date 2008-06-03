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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class InitiateCollaborationRequest extends RequestWithConfirmation<Long>
{
  private Collection<IBuddy> buddies;

  public InitiateCollaborationRequest(IChannel channel, Collection<IBuddy> buddies)
  {
    super(channel);
    this.buddies = buddies;
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_INITIATE_COLLABORATION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    ProtocolUtil.writeBuddies(out, buddies);
  }

  @Override
  protected Long confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readLong();
  }
}
