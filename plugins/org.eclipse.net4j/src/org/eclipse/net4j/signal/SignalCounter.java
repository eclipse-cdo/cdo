/*
 * Copyright (c) 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides {@link Signal signal} execution counts  when
 * {@link SignalProtocol#addListener(IListener) attached} to a {@link ISignalProtocol signal protocol}.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public final class SignalCounter implements IListener
{
  private static final boolean fullyQualifiedNames = Boolean.getBoolean("org.eclipse.net4j.signal.SignalCounter.fullyQualifiedNames");

  private HashBag<Class<? extends Signal>> signals = new HashBag<Class<? extends Signal>>();

  private final ISignalProtocol<?> protocol;

  public SignalCounter()
  {
    protocol = null;
  }

  /**
   * @since 4.1
   */
  public SignalCounter(ISignalProtocol<?> protocol)
  {
    this.protocol = protocol;

    if (protocol != null)
    {
      protocol.addListener(this);
    }
  }

  /**
   * Get the number of different signal counted.
   *
   * @since 4.4
   */
  public int getCountForSignalTypes()
  {
    synchronized (signals)
    {
      return signals.size();
    }
  }

  public int getCountFor(Class<? extends Signal> signal)
  {
    synchronized (signals)
    {
      return signals.getCounterFor(signal);
    }
  }

  /**
   * @since 4.6
   */
  public int removeCountFor(Class<? extends Signal> signal)
  {
    synchronized (signals)
    {
      return signals.removeCounterFor(signal);
    }
  }

  public void clearCounts()
  {
    synchronized (signals)
    {
      signals.clear();
    }
  }

  /**
   * @since 4.8
   */
  public void dump(PrintStream out, boolean clearCountsWhenDone)
  {
    synchronized (signals)
    {
      Map<String, Class<? extends Signal>> signalTypes = new HashMap<String, Class<? extends Signal>>();

      for (Class<? extends Signal> signalType : signals)
      {
        String name = fullyQualifiedNames ? signalType.getName() : signalType.getSimpleName();
        signalTypes.put(name, signalType);
      }

      String[] names = signalTypes.keySet().toArray(new String[signalTypes.size()]);
      Arrays.sort(names);

      for (String name : names)
      {
        Class<? extends Signal> signalType = signalTypes.get(name);
        out.println(name + " = " + signals.getCounterFor(signalType));
      }

      if (clearCountsWhenDone)
      {
        clearCounts();
      }
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof SignalFinishedEvent)
    {
      synchronized (signals)
      {
        SignalFinishedEvent<?> e = (SignalFinishedEvent<?>)event;
        signals.add(e.getSignal().getClass());
      }
    }
  }

  /**
   * @since 4.6
   */
  public void dispose()
  {
    if (protocol != null)
    {
      protocol.removeListener(this);
    }
  }
}
