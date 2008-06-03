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
package org.eclipse.net4j.signal.failover;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalActor;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.event.Notifier;

import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public abstract class FailOverStrategy extends Notifier implements IFailOverStrategy
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
    IConnector newConnector = getNewConnector(oldChannel);
    CheckUtil.checkNull(newConnector, "newConnector");

    IChannel newChannel = newConnector.openChannel(protocol);
    protocol.setChannel(newChannel);
    oldChannel.close();
    fireEvent(new FailOverEvent(oldChannel, newChannel, newConnector));
  }

  /**
   * Should be overridden to provide a fail-over <code>IConnector</code>. The oldChannel <i>can</i> be used as a hint.
   */
  protected abstract IConnector getNewConnector(IChannel oldChannel);

  /**
   * @author Eike Stepper
   */
  private final class FailOverEvent implements IFailOverEvent
  {
    private IChannel oldChannel;

    private IChannel newChannel;

    private IConnector newConnector;

    public FailOverEvent(IChannel oldChannel, IChannel newChannel, IConnector newConnector)
    {
      this.oldChannel = oldChannel;
      this.newChannel = newChannel;
      this.newConnector = newConnector;
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

    public IConnector getNewConnector()
    {
      return newConnector;
    }
  }
}
