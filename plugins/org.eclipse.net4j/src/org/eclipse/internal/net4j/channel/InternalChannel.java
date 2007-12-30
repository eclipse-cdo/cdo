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
package org.eclipse.internal.net4j.channel;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface InternalChannel extends IChannel, ILifecycle.Introspection
{
  public void setChannelID(int channelID);

  public void setChannelIndex(short channelIndex);

  public ExecutorService getReceiveExecutor();

  public Queue<IBuffer> getSendQueue();

  public void handleBufferFromMultiplexer(IBuffer buffer);
}
