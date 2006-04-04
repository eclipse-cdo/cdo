/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.util;


import org.eclipse.net4j.spring.InactiveException;
import org.eclipse.net4j.spring.Service;
import org.eclipse.net4j.spring.impl.ServiceImpl;

import junit.framework.Assert;


public abstract class ServiceInvoker
{
  private static final byte[] STATES = { ServiceImpl.ServiceStateMachine.STATE_STOPPING,
      ServiceImpl.ServiceStateMachine.STATE_STOPPING,
      ServiceImpl.ServiceStateMachine.STATE_INCONSISTENT,
      ServiceImpl.ServiceStateMachine.STATE_ACTIVE, ServiceImpl.ServiceStateMachine.STATE_DISPOSED};

  private static final int OK_INDEX = 2;

  public ServiceInvoker(Service bean)
  {
    for (int i = 0; i < STATES.length; i++)
    {
      byte state = STATES[i];
      bean.testSetState(state);
      Throwable throwable = null;

      try
      {
        invokeService(bean);
      }
      catch (Throwable t)
      {
        throwable = t;
      }

      if (i == OK_INDEX)
      {
        Assert.assertFalse("No InactiveException is thrown", throwable != null
            && throwable instanceof InactiveException);
      }
      else
      {
        Assert.assertNotNull("An exception is thrown", throwable);
        Assert.assertTrue("An InactiveException is thrown", throwable instanceof InactiveException);
      }
    }
  }

  protected abstract void invokeService(Service bean) throws Exception;
}
