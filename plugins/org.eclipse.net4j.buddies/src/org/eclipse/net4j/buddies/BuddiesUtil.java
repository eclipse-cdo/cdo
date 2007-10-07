/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.buddies.protocol.BuddiesProtocolConstants;
import org.eclipse.net4j.internal.buddies.protocol.OpenSessionRequest;
import org.eclipse.net4j.util.WrappedException;

/**
 * @author Eike Stepper
 */
public final class BuddiesUtil
{
  private BuddiesUtil()
  {
  }

  public static IBuddySession openSession(IConnector connector, String userID, String password)
  {
    try
    {
      IChannel channel = connector.openChannel(BuddiesProtocolConstants.PROTOCOL_NAME, null);
      OpenSessionRequest request = new OpenSessionRequest(channel, userID, password);
      return request.send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
