/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;

/**
 * An {@link IEvent event} fired from a {@link ISignalProtocol signal protocol} when
 * a {@link #getSignal() signal} has been scheduled for local execution.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 */
public class SignalScheduledEvent<INFRA_STRUCTURE> extends Event
{
  private static final long serialVersionUID = 1L;

  private Signal signal;

  SignalScheduledEvent(ISignalProtocol<INFRA_STRUCTURE> source, Signal signal)
  {
    super(source);
    this.signal = signal;
  }

  @Override
  public ISignalProtocol<INFRA_STRUCTURE> getSource()
  {
    @SuppressWarnings("unchecked")
    ISignalProtocol<INFRA_STRUCTURE> source = (ISignalProtocol<INFRA_STRUCTURE>)super.getSource();
    return source;
  }

  public Signal getSignal()
  {
    return signal;
  }

  @Override
  protected String formatAdditionalParameters()
  {
    return "signal=" + signal.getClass().getSimpleName();
  }
}
