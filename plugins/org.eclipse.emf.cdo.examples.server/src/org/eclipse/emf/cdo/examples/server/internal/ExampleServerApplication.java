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
package org.eclipse.emf.cdo.examples.server.internal;


import org.eclipse.net4j.spring.Container;
import org.eclipse.net4j.spring.ValidationException;
import org.eclipse.net4j.util.thread.DeadlockDetector;

import org.eclipse.emf.cdo.server.Mapper;

import org.eclipse.core.runtime.IPlatformRunnable;


public class ExampleServerApplication implements IPlatformRunnable
{
  private static final String ACTIVE_MSG = "Mapper is not active";

  public ExampleServerApplication()
  {
  }

  public Object run(Object args) throws Exception
  {
    validateContainer();
    System.out.println("HIT ENTER FOR SHUTDOWN!!!");

    while (System.in.available() == 0)
    {
      DeadlockDetector.sleep(100);
    }

    return null;
  }

  private void validateContainer()
  {
    Mapper mapper = getMapper();
    assertTrue(mapper.isActive(), ACTIVE_MSG);
  }

  private Mapper getMapper()
  {
    try
    {
      Container container = ExampleServerPlugin.getServerContainer();
      return (Mapper) container.getBean("mapper", Mapper.class);
    }
    catch (Exception ex)
    {
      throw new ValidationException(ACTIVE_MSG, ex);
    }
  }

  private void assertTrue(boolean condition, String msg)
  {
    if (!condition)
    {
      throw new ValidationException(msg);
    }
  }
}
