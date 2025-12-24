/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class EventPrinter implements IListener
{
  private final PrintStream stream;

  public EventPrinter(PrintStream stream)
  {
    this.stream = stream;
  }

  public EventPrinter()
  {
    this(IOUtil.OUT());
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    stream.println(event);
  }
}
