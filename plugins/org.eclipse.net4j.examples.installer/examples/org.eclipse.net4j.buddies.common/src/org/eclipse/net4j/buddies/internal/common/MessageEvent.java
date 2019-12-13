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
package org.eclipse.net4j.buddies.internal.common;

import org.eclipse.net4j.buddies.common.IMessage;
import org.eclipse.net4j.buddies.common.IMessageEvent;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 */
public final class MessageEvent extends Event implements IMessageEvent
{
  private static final long serialVersionUID = 1L;

  private IMessage message;

  public MessageEvent(INotifier notifier, IMessage message)
  {
    super(notifier);
    this.message = message;
  }

  @Override
  public IMessage getMessage()
  {
    return message;
  }
}
