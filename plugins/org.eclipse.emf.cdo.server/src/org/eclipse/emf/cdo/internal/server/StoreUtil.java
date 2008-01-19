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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreReader;

/**
 * @author Eike Stepper
 */
public final class StoreUtil
{
  private static final ThreadLocal<IStoreReader> THREAD_LOCAL = new InheritableThreadLocal<IStoreReader>();

  private StoreUtil()
  {
  }

  public static IStore createNOOPStore()
  {
    return new NOOPStore();
  }

  public static IStore createMEMStore()
  {
    return new MEMStore();
  }

  public static void setReader(IStoreReader reader)
  {
    THREAD_LOCAL.set(reader);
  }

  public static IStoreReader getReader()
  {
    IStoreReader reader = THREAD_LOCAL.get();
    if (reader == null)
    {
      throw new IllegalStateException("reader == null");
    }

    return reader;
  }
}
