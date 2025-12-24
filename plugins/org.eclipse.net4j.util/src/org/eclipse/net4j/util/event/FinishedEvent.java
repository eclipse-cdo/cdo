/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

/**
 * An {@link IEvent} that is fired after a method has called a handler multiple times.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public class FinishedEvent implements IEvent
{
  public static final IEvent INSTANCE = new FinishedEvent();

  private final Object result;

  public FinishedEvent(Object result)
  {
    this.result = result;
  }

  private FinishedEvent()
  {
    this(null);
  }

  public Object getResult()
  {
    return result;
  }

  @Override
  public INotifier getSource()
  {
    return null;
  }
}
