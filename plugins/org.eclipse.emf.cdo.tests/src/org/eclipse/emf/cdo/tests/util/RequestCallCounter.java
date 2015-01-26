/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.internal.cdo.session.DelegatingSessionProtocol;

import org.eclipse.net4j.signal.Signal;
import org.eclipse.net4j.signal.SignalScheduledEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link IListener} to count request sent to server through {@link CDOClientProtocol}.
 *
 * @author Esteban Dugueperoux
 */
public class RequestCallCounter implements IListener
{
  private Map<Short, Integer> nbRequestsCalls = new HashMap<Short, Integer>();

  public RequestCallCounter(CDOSession session)
  {

    InternalCDOSession internalCDOSession = (InternalCDOSession)session;
    CDOSessionProtocol sessionProtocol = internalCDOSession.getSessionProtocol();
    CDOClientProtocol cdoClientProtocol = null;
    if (sessionProtocol instanceof CDOClientProtocol)
    {
      cdoClientProtocol = (CDOClientProtocol)sessionProtocol;
    }
    else if (sessionProtocol instanceof DelegatingSessionProtocol)
    {
      DelegatingSessionProtocol delegatingSessionProtocol = (DelegatingSessionProtocol)sessionProtocol;
      CDOSessionProtocol delegate = delegatingSessionProtocol.getDelegate();
      if (delegate instanceof CDOClientProtocol)
      {
        cdoClientProtocol = (CDOClientProtocol)delegate;
      }
    }
    if (cdoClientProtocol != null)
    {
      cdoClientProtocol.addListener(this);
    }
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof SignalScheduledEvent)
    {
      @SuppressWarnings("unchecked")
      SignalScheduledEvent<Object> signalScheduledEvent = (SignalScheduledEvent<Object>)event;
      Signal signal = signalScheduledEvent.getSignal();
      short signalID = signal.getID();
      Integer nbRequestCalls = nbRequestsCalls.get(signalID);
      if (nbRequestCalls == null)
      {
        nbRequestCalls = 0;
      }
      nbRequestCalls++;
      nbRequestsCalls.put(signalID, nbRequestCalls);
    }
  }

  public Map<Short, Integer> getNBRequestsCalls()
  {
    return nbRequestsCalls;
  }

}
