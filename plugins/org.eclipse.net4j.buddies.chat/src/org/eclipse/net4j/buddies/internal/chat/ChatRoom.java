/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.chat;

import org.eclipse.net4j.buddies.chat.IChatRoom;
import org.eclipse.net4j.buddies.common.IMessage;

/**
 * @author Eike Stepper
 */
public class ChatRoom extends Chat implements IChatRoom
{
  public ChatRoom()
  {
  }

  @Override
  public void sendComment(String text)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handleMessage(IMessage message)
  {
    sendMessage(message);
  }
}
