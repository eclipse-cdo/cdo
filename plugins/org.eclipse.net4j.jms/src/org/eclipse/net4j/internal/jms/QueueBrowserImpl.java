/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import javax.jms.Queue;
import javax.jms.QueueBrowser;

import java.util.Enumeration;

public class QueueBrowserImpl implements QueueBrowser
{
  private Queue queue;

  private String messageSelector;

  public QueueBrowserImpl(Queue queue, String messageSelector)
  {
    this.queue = queue;
    this.messageSelector = messageSelector;
  }

  public QueueBrowserImpl(Queue queue)
  {
    this(queue, null);
  }

  public Queue getQueue()
  {
    return queue;
  }

  public String getMessageSelector()
  {
    return messageSelector;
  }

  public void close()
  {
    throw new NotYetImplementedException();
  }

  public Enumeration<?> getEnumeration()
  {
    throw new NotYetImplementedException();
  }
}
