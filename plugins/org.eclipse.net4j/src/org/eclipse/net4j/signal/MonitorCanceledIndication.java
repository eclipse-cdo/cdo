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
public class MonitorCanceledIndication extends Indication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, MonitorCanceledIndication.class);

  public MonitorCanceledIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_MONITOR_CANCELED);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int correlationID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Canceling monitor of signal {0}", correlationID); //$NON-NLS-1$
    }

    getProtocol().handleMonitorCanceled(correlationID);
  }
}
