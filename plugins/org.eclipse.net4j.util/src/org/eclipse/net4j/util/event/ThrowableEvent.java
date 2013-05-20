/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

/**
 * An {@link IEvent} typically fired from {@link INotifier notifiers} that execute operations in the background.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public final class ThrowableEvent extends Event
{
  private static final long serialVersionUID = 1L;

  private final Throwable throwable;

  public ThrowableEvent(INotifier notifier, Throwable throwable)
  {
    super(notifier);
    this.throwable = throwable;
  }

  public Throwable getThrowable()
  {
    return throwable;
  }
}
