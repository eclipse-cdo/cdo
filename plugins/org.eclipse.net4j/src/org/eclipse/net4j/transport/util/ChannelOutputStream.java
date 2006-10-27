/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.transport.util;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;

/**
 * @author Eike Stepper
 */
public class ChannelOutputStream extends BufferOutputStream
{
  public ChannelOutputStream(Channel channel)
  {
    super(channel, channel.getChannelIndex());
  }

  public ChannelOutputStream(Channel channel, BufferProvider bufferProvider)
  {
    super(channel, bufferProvider, channel.getChannelIndex());
  }
}
