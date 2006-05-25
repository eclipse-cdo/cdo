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
package org.eclipse.emf.cdo.example.server.internal;


import org.eclipse.net4j.util.thread.DeadlockDetector;

import org.eclipse.core.runtime.IPlatformRunnable;


public class ExampleServerApplication implements IPlatformRunnable
{
  public ExampleServerApplication()
  {
  }

  public Object run(Object args) throws Exception
  {
    System.out.println("HIT ANY KEY FOR SHUTDOWN!!!");

    while (System.in.available() == 0)
    {
      DeadlockDetector.sleep(100);
    }

    return null;
  }
}
