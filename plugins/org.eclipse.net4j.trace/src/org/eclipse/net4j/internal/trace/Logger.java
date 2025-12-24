/*
 * Copyright (c) 2018, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.trace;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.trace.Element;
import org.eclipse.net4j.trace.Element.BufferElement;
import org.eclipse.net4j.trace.Listener;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.om.log.RollingLog;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class Logger extends RollingLog implements Listener
{
  private static final Pattern INIT_PATTERN = Pattern.compile("([A-Za-z_$][A-Za-z\\d_$]+)\\[(\\d+)\\].init\\(\\)");

  public Logger(String logFile, long logSize, boolean append)
  {
    super(logFile, logSize, append);
  }

  @Override
  public void methodCalled(Element caller, String callingMethod, Element callee, String calledMethod)
  {
    log(caller + "." + callingMethod + "()\t" + callee + "." + calledMethod + "()");
  }

  @Override
  public void elementCreated(Element element)
  {
    log(element + ".init()");
  }

  @Override
  public void ownerChanged(BufferElement element, Element oldOwner, Element newOwner)
  {
    log("\t\t" + element + ".owner = " + newOwner);
  }

  @Override
  public void threadChanged(BufferElement element, Element oldThread, Element newThread)
  {
    String warning;
    if (oldThread != null && newThread != null)
    {
      warning = "  (was " + oldThread + ")";
    }
    else
    {
      warning = "";
    }

    log("\t\t" + element + ".thread = " + newThread + warning);
  }

  @Override
  public void stateChanged(BufferElement element, BufferState oldState, BufferState newState)
  {
    log("\t\t" + element + ".state = " + newState);
  }

  @Override
  public void positionChanged(BufferElement element, int oldPosition, int newPosition)
  {
    log("\t\t" + element + ".position = " + newPosition);
  }

  @Override
  public void limitChanged(BufferElement element, int oldLimit, int newLimit)
  {
    log("\t\t" + element + ".limit = " + newLimit);
  }

  @Override
  public void eosChanged(BufferElement element, boolean newEOS)
  {
    log("\t\t" + element + ".eos = " + newEOS);
  }

  @Override
  public void ccamChanged(BufferElement element, boolean newCCAM)
  {
    log("\t\t" + element + ".ccam = " + newCCAM);
  }

  @Override
  protected String getThreadInfo()
  {
    return Element.get(Thread.currentThread()).toString();
  }

  @Override
  protected void load(Properties properties)
  {
    ElementCounters.INSTANCE.load(properties);
  }

  @Override
  protected void save(Properties properties)
  {
    ElementCounters.INSTANCE.save(properties);
  }

  @Override
  protected void recovery(Properties properties, LogLine logLine)
  {
    String message = logLine.getMessage();
    Matcher matcher = INIT_PATTERN.matcher(message);
    if (matcher.matches())
    {
      String type = matcher.group(1);
      String id = matcher.group(2);
      properties.setProperty(type, id);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends Listener.Factory
  {
    public static final String TYPE = "logger";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    protected Logger create(Map<String, String> properties) throws ProductCreationException
    {
      String logFile = properties.get(DEFAULT_KEY);
      if (StringUtil.isEmpty(logFile))
      {
        logFile = "buffers";
      }

      String logSize = properties.get("size");
      if (StringUtil.isEmpty(logSize))
      {
        logSize = "100000000";
      }

      String append = properties.get("append"); //$NON-NLS-1$
      if (StringUtil.isEmpty(append))
      {
        append = StringUtil.TRUE;
      }

      return new Logger(logFile, Long.parseLong(logSize), Boolean.parseBoolean(append));
    }
  }
}
