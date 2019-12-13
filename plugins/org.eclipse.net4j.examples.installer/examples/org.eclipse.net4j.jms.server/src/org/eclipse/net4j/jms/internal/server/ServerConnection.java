/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server;

import org.eclipse.net4j.jms.internal.server.protocol.JMSServerProtocol;
import org.eclipse.net4j.jms.server.IConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ServerConnection implements IConnection
{
  private Server server;

  private String userName;

  private List<ServerSession> sessions = new ArrayList<ServerSession>(0);

  private JMSServerProtocol protocol;

  public ServerConnection(Server server, String userName)
  {
    this.server = server;
    this.userName = userName;
  }

  @Override
  public Server getServer()
  {
    return server;
  }

  @Override
  public String getUserName()
  {
    return userName;
  }

  public JMSServerProtocol getProtocol()
  {
    return protocol;
  }

  public void setProtocol(JMSServerProtocol protocol)
  {
    this.protocol = protocol;
  }

  @Override
  public ServerSession openSession(int sessionID)
  {
    ServerSession session = new ServerSession(this, sessionID);
    synchronized (sessions)
    {
      while (sessionID >= sessions.size())
      {
        sessions.add(null);
      }

      sessions.set(sessionID, session);
    }

    return session;
  }

  @Override
  public ServerSession getSession(int sessionID)
  {
    return sessions.get(sessionID);
  }

  public ServerSession[] getSessions()
  {
    List<ServerSession> result = new ArrayList<ServerSession>(sessions.size());
    synchronized (sessions)
    {
      for (ServerSession session : sessions)
      {
        if (session != null)
        {
          result.add(session);
        }
      }
    }

    return result.toArray(new ServerSession[result.size()]);
  }

  public void close()
  {
    protocol.getChannel().close();
  }

  protected boolean removeSession(ServerSession session)
  {
    synchronized (sessions)
    {
      int sessionID = session.getID();
      if (sessions.get(sessionID) == session)
      {
        sessions.set(sessionID, null);
        return true;
      }

      return false;
    }
  }
}
