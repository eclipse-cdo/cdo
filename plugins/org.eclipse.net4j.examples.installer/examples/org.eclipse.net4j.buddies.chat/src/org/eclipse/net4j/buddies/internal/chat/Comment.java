/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.chat;

import org.eclipse.net4j.buddies.chat.IComment;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public class Comment implements IComment, Serializable
{
  private static final long serialVersionUID = 1L;

  private long receiveTime;

  private String senderID;

  private String text;

  public Comment(long receiveTime, String senderID, String text)
  {
    this.receiveTime = receiveTime;
    this.senderID = senderID;
    this.text = text;
  }

  @Override
  public long getReceiveTime()
  {
    return receiveTime;
  }

  @Override
  public String getSenderID()
  {
    return senderID;
  }

  @Override
  public String getText()
  {
    return text;
  }
}
