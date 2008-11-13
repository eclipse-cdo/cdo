/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal.failover;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.ISignalProtocol;

/**
 * @author Eike Stepper
 */
public class RetryFailOverStrategy extends NOOPFailOverStrategy
{
  public RetryFailOverStrategy(IConnector connector)
  {
    super(connector);
  }

  @Override
  public void handleFailOver(ISignalProtocol<?> protocol)
  {
    handleOpen(protocol);
  }
}
