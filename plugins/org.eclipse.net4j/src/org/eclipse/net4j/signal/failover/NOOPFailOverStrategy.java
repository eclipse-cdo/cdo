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

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 */
public class NOOPFailOverStrategy implements IFailOverStrategy
{
  public static final NOOPFailOverStrategy INSTANCE = new NOOPFailOverStrategy();

  public NOOPFailOverStrategy()
  {
  }

  public <RESULT> RESULT send(RequestWithConfirmation<RESULT> request) throws Exception
  {
    return request.send();
  }

  public <RESULT> RESULT send(RequestWithConfirmation<RESULT> request, long timeout) throws Exception
  {
    return request.send(timeout);
  }

  public void addListener(IListener listener)
  {
  }

  public void removeListener(IListener listener)
  {
  }
}
