/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import java.io.PrintStream;

/**
 * A {@link IListener listener} that dispatches throwable {@link ThrowableEvent events} to methods that can be
 * overridden by extenders.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public class ThrowableEventAdapter implements IListener
{
  public ThrowableEventAdapter()
  {
  }

  public final void notifyEvent(IEvent event)
  {
    if (event instanceof ThrowableEvent)
    {
      ThrowableEvent e = (ThrowableEvent)event;
      notifyLifecycleEvent(e);
    }
    else
    {
      notifyOtherEvent(event);
    }
  }

  protected void notifyLifecycleEvent(ThrowableEvent event)
  {
    onThrowable(event.getSource(), event.getThrowable());
  }

  protected void notifyOtherEvent(IEvent event)
  {
  }

  protected void onThrowable(INotifier source, Throwable t)
  {
  }

  /**
   * Prints the stack traces of throwable {@link ThrowableEvent events} to a {@link PrintStream}.
   *
   * @author Eike Stepper
   */
  public static class ToPrintStream extends ThrowableEventAdapter
  {
    public static final ToPrintStream CONSOLE = new ToPrintStream(System.out);

    private final PrintStream out;

    public ToPrintStream(PrintStream out)
    {
      this.out = out;
    }

    public final PrintStream getOut()
    {
      return out;
    }

    @Override
    protected void onThrowable(INotifier source, Throwable t)
    {
      t.printStackTrace(out);
    }
  }
}
