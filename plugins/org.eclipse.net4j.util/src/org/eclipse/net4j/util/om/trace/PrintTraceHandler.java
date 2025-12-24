/*
 * Copyright (c) 2007-2009, 2011, 2012, 2016, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.trace;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.PrintStream;
import java.text.MessageFormat;

/**
 * A {@link OMTraceHandler trace handler} that appends {@link OMTraceHandlerEvent trace events}
 * to a {@link #getStream() print stream}.
 *
 * @author Eike Stepper
 */
public class PrintTraceHandler implements OMTraceHandler
{
  public static final PrintTraceHandler CONSOLE = new PrintTraceHandler();

  private PrintStream stream;

  private String pattern;

  private boolean shortContext;

  public PrintTraceHandler(PrintStream stream)
  {
    this.stream = stream;
  }

  protected PrintTraceHandler()
  {
    this(IOUtil.OUT());
  }

  /**
   * @since 3.2
   */
  public PrintStream getStream()
  {
    return stream;
  }

  public String getPattern()
  {
    return pattern;
  }

  /**
   * Pattern arguments:
   * <p>
   * <ul>
   * <li>{0} &rarr; String <b>tracerName</b>
   * <li>{1} &rarr; String <b>tracerShort</b>
   * <li>{2} &rarr; String <b>contextName</b>
   * <li>{3} &rarr; String <b>contextShort</b>
   * <li>{4} &rarr; long <b>timeStamp</b>
   * <li>{5} &rarr; String <b>message</b>
   * <li>{6} &rarr; String <b>threadName</b>
   * <li>{7} &rarr; long <b>threadID</b>
   * <li>{8} &rarr; int <b>threadPriority</b>
   * <li>{9} &rarr; Thread.State <b>threadState</b>
   * </ul>
   */
  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }

  public boolean isShortContext()
  {
    return shortContext;
  }

  public void setShortContext(boolean shortContext)
  {
    this.shortContext = shortContext;
  }

  @Override
  public void traced(OMTraceHandlerEvent event)
  {
    String line = pattern == null ? format(shortContext, event) : format(pattern, event);
    stream.println(line);
    if (event.getThrowable() != null)
    {
      IOUtil.print(event.getThrowable(), stream);
    }
  }

  public static String format(boolean shortContext, OMTraceHandlerEvent event)
  {
    Class<?> context = event.getContext();
    String contextName = shortContext ? context.getSimpleName() : context.getName();
    return Thread.currentThread().getName() + " [" + contextName + "] " + event.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Pattern arguments:
   * <p>
   * <ul>
   * <li>{0} &rarr; String <b>tracerName</b>
   * <li>{1} &rarr; String <b>tracerShort</b>
   * <li>{2} &rarr; String <b>contextName</b>
   * <li>{3} &rarr; String <b>contextShort</b>
   * <li>{4} &rarr; long <b>timeStamp</b>
   * <li>{5} &rarr; String <b>message</b>
   * <li>{6} &rarr; String <b>threadName</b>
   * <li>{7} &rarr; long <b>threadID</b>
   * <li>{8} &rarr; int <b>threadPriority</b>
   * <li>{9} &rarr; Thread.State <b>threadState</b>
   * </ul>
   */
  public static String format(String pattern, OMTraceHandlerEvent event)
  {
    final OMTracer tracer = event.getTracer();
    final String tracerName = tracer.getFullName();
    final String tracerShort = tracer.getName();

    final Class<?> context = event.getContext();
    final String contextName = context.getName();
    final String contextShort = context.getName();

    final long timeStamp = event.getTimeStamp();
    final String message = event.getMessage();

    final Thread thread = Thread.currentThread();
    final String threadName = thread.getName();
    final long threadID = thread.getId();
    final int threadPriority = thread.getPriority();
    final Thread.State threadState = thread.getState();

    return MessageFormat.format(pattern, tracerName, tracerShort, contextName, contextShort, timeStamp, message, threadName, threadID, threadPriority,
        threadState);
  }
}
