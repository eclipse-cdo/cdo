/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.buddies.chat.IChat;
import org.eclipse.net4j.buddies.chat.IComment;
import org.eclipse.net4j.buddies.chat.ICommentEvent;
import org.eclipse.net4j.util.event.Event;

/**
 * @author Eike Stepper
 */
public class CommentEvent extends Event implements ICommentEvent
{
  private static final long serialVersionUID = 1L;

  private IComment comment;

  public CommentEvent(IChat chat, IComment comment)
  {
    super(chat);
    this.comment = comment;
  }

  /**
   * @since 3.0
   */
  @Override
  public IChat getSource()
  {
    return (IChat)super.getSource();
  }

  @Override
  public IComment getComment()
  {
    return comment;
  }
}
