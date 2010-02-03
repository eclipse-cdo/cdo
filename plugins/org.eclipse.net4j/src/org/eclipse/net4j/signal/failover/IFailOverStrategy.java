/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal.failover;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 */
public interface IFailOverStrategy extends INotifier
{
  /**
   * @since 2.0
   */
  public void handleOpen(ISignalProtocol<?> protocol);

  /**
   * @since 2.0
   */
  public void handleFailOver(ISignalProtocol<?> protocol, Exception reason);
}
