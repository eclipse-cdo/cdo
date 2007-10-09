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
package org.eclipse.net4j.internal.buddies;

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.Message;
import org.eclipse.net4j.buddies.internal.protocol.MessageNotification;
import org.eclipse.net4j.buddies.protocol.IMessage;
import org.eclipse.net4j.util.WrappedException;

import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class BuddyCollaboration extends Collaboration implements IBuddyCollaboration
{
  private IBuddySession session;

  public BuddyCollaboration()
  {
    super(createID());
  }

  public IBuddySession getSession()
  {
    return session;
  }

  public void sendMessage(IMessage message)
  {
    if (message instanceof Message)
    {
      ((Message)message).setSenderID(session.getSelf().getUserID());

    }
    try
    {
      new MessageNotification(session.getChannel(), message).send();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private static String createID()
  {
    return UUID.randomUUID().toString();
  }
}
