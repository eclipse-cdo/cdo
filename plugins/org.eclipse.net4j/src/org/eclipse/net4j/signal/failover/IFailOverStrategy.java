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
import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 */
public interface IFailOverStrategy extends INotifier
{
  /**
   * @since 2.0
   */
  public long getDefaultTimeout();

  /**
   * @since 2.0
   */
  public void setDefaultTimeout(long defaultTimeout);

  public <RESULT> RESULT send(RequestWithConfirmation<RESULT> request) throws Exception, RemoteException;

  public <RESULT> RESULT send(RequestWithConfirmation<RESULT> request, long timeout) throws Exception, RemoteException;
}
