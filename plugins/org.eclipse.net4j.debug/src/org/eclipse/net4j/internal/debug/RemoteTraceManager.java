/*
 * Copyright (c) 2006-2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.debug;

import org.eclipse.net4j.internal.debug.views.RemoteTraceView;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.RemoteTraceServer;
import org.eclipse.net4j.util.om.trace.RemoteTraceServer.Event;
import org.eclipse.net4j.util.om.trace.RemoteTraceServer.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RemoteTraceManager extends Lifecycle implements Listener
{
  // @Singleton
  public static final RemoteTraceManager INSTANCE = new RemoteTraceManager();

  private RemoteTraceServer server;

  private List<Event> events = new ArrayList<>();

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

  @Override
  public void notifyRemoteTrace(Event event)
  {
    events.add(event);
    RemoteTraceView.notifyNewTrace();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    server = new RemoteTraceServer();
    server.addListener(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    server.removeListener(this);
    server.close();
    super.doDeactivate();
  }
}
