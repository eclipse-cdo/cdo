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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.internal.util.event.Notifier;

import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public class FailOverStrategy extends Notifier implements IFailOverStrategy
{
  public FailOverStrategy()
  {
  }

  public <RESULT> RESULT send(RequestWithConfirmation<RESULT> request) throws Exception
  {
    return send(request, SignalActor.NO_TIMEOUT);
  }

  public <RESULT> RESULT send(RequestWithConfirmation<RESULT> request, long timeout) throws Exception
  {
    for (;;)
    {
      try
      {
        return request.send(timeout);
      }
      catch (TimeoutException exeption)
      {
        failOver(request.getProtocol());
      }
    }
  }

  protected void failOver(SignalProtocol protocol)
  {
    IChannel oldChannel = protocol.getChannel();
    IConnector connector = getNewConnector(oldChannel);
    if (connector == null)
    {
      throw new IllegalStateException("connector == null");
    }

    IChannel newChannel = connector.openChannel(protocol);
    protocol.setChannel(newChannel);
    oldChannel.close();
    fireEvent(new FailOverEvent(oldChannel, newChannel));
  }

  /**
   * Should be overridden to provide a fail-over <code>IConnector</code>. The oldChannel <i>can</i> be used as a
   * hint.
   */
  protected IConnector getNewConnector(IChannel oldChannel)
  {
    if (oldChannel == null)
    {
      throw new IllegalArgumentException("oldChannel == null");
    }

    return oldChannel.getConnector();
  }

  /**
   * @author Eike Stepper
   */
  private final class FailOverEvent implements IFailOverEvent
  {
    private IChannel oldChannel;

    private IChannel newChannel;

    public FailOverEvent(IChannel oldChannel, IChannel newChannel)
    {
      this.oldChannel = oldChannel;
      this.newChannel = newChannel;
    }

    public IFailOverStrategy getSource()
    {
      return FailOverStrategy.this;
    }

    public IChannel getOldChannel()
    {
      return oldChannel;
    }

    public IChannel getNewChannel()
    {
      return newChannel;
    }
  }
}
