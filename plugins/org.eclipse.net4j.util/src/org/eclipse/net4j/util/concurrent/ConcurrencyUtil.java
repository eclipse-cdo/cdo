/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.WrappedException;

/**
 * @author Eike Stepper
 */
public final class ConcurrencyUtil
{
  private ConcurrencyUtil()
  {
  }

  public static void sleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void sleep(long millis, int nanos)
  {
    try
    {
      Thread.sleep(millis, nanos);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
