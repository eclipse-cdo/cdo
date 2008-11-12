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

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.event.Notifier;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractFailOverStrategy extends Notifier implements IFailOverStrategy
{
  private long defaultTimeout = RequestWithConfirmation.NO_TIMEOUT;

  public AbstractFailOverStrategy()
  {
  }

  public long getDefaultTimeout()
  {
    return defaultTimeout;
  }

  public void setDefaultTimeout(long defaultTimeout)
  {
    this.defaultTimeout = defaultTimeout;
  }

  public <RESULT> RESULT send(RequestWithConfirmation<RESULT> request) throws Exception, RemoteException
  {
    return send(request, defaultTimeout);
  }
}
