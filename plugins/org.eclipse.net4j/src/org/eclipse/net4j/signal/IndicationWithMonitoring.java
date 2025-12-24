/*
 * Copyright (c) 2008-2016, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.concurrent.ExecutorService;

/**
 * Represents the receiver side of a two-way {@link IndicationWithResponse signal} with additional support for remote progress monitoring.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class IndicationWithMonitoring extends IndicationWithResponse
{
  private OMMonitor monitor;

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  @Override
  protected final void indicating(ExtendedDataInputStream in) throws Exception
  {
    int monitorProgressSeconds = in.readInt();
    int monitorTimeoutSeconds = in.readInt();

    monitor = createMonitor(monitorProgressSeconds, monitorTimeoutSeconds);
    monitor.begin(OMMonitor.HUNDRED);

    indicating(in, monitor.fork(getIndicatingWorkPercent()));
  }

  @Override
  protected final void responding(ExtendedDataOutputStream out) throws Exception
  {
    responding(out, monitor.fork(getRespondingWorkPercent()));
  }

  protected abstract void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception;

  protected abstract void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception;

  protected int getIndicatingWorkPercent()
  {
    return 99;
  }

  /**
   * @since 4.13
   */
  protected final int getRespondingWorkPercent()
  {
    return (int)OMMonitor.HUNDRED - getIndicatingWorkPercent();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.13 not used anymore.
   */
  @Deprecated
  protected ExecutorService getMonitoringExecutorService()
  {
    return getProtocol().getExecutorService();
  }
}
