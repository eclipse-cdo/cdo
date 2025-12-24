/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public class MonitorProgressIndication extends Indication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, MonitorProgressIndication.class);

  public MonitorProgressIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_MONITOR_PROGRESS);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int correlationID = in.readInt();
    double totalWork = in.readDouble();
    double work = in.readDouble();
    if (TRACER.isEnabled())
    {
      TRACER.format("Progress of signal {0}: totalWork={1}, work={2}", correlationID, totalWork, work); //$NON-NLS-1$
    }

    getProtocol().handleMonitorProgress(correlationID, totalWork, work);
  }
}
