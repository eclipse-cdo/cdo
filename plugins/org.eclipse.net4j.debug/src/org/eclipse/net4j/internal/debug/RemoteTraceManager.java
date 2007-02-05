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
package org.eclipse.net4j.internal.debug;

import org.eclipse.net4j.internal.debug.views.RemoteTraceView;
import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;
import org.eclipse.net4j.util.lifecycle.Singleton;
import org.eclipse.net4j.util.om.trace.RemoteTraceServer;
import org.eclipse.net4j.util.om.trace.RemoteTraceServer.Event;
import org.eclipse.net4j.util.om.trace.RemoteTraceServer.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RemoteTraceManager extends AbstractLifecycle implements Listener
{
  @Singleton
  public static final RemoteTraceManager INSTANCE = new RemoteTraceManager();

  private RemoteTraceServer server;

  private List<Event> events = new ArrayList();

  public RemoteTraceManager()
  {
  }

  public Event[] getEvents()
  {
    return events.toArray(new Event[events.size()]);
  }

  public void clearEvents()
  {
    events.clear();
  }

  public void notifyRemoteTrace(Event event)
  {
    events.add(event);
    RemoteTraceView.notifyNewTrace();
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    server = new RemoteTraceServer();
    server.addListener(this);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    server.removeListener(this);
    server.close();
    super.onDeactivate();
  }
}
