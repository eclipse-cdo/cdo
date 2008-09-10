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
package org.eclipse.emf.cdo.server;

/**
 * @author Eike Stepper
 */
public final class StoreThreadLocal
{
  private static final ThreadLocal<ISession> SESSION = new InheritableThreadLocal<ISession>();

  private static final ThreadLocal<IStoreReader> STORE_READER = new InheritableThreadLocal<IStoreReader>();

  private StoreThreadLocal()
  {
  }

  public static void setSession(ISession session)
  {
    SESSION.set(session);
    STORE_READER.set(null);
  }

  /**
   *Returns the session associated with the current thread.
   * 
   * @return Never <code>null</code>.
   * @throws IllegalStateException
   *           if no session is associated with the current thread.
   */
  public static ISession getSession()
  {
    ISession session = SESSION.get();
    if (session == null)
    {
      throw new IllegalStateException("session == null");
    }

    return session;
  }

  public static void setStoreReader(IStoreReader storeReader)
  {
    SESSION.set(storeReader.getSession());
    STORE_READER.set(storeReader);
  }

  public static IStoreReader getStoreReader()
  {
    IStoreReader storeReader = STORE_READER.get();
    if (storeReader == null)
    {
      ISession session = getSession();
      IStore store = session.getSessionManager().getRepository().getStore();
      storeReader = store.getReader(session);
      STORE_READER.set(storeReader);
    }

    return storeReader;
  }

  public static void release()
  {
    IStoreReader storeReader = STORE_READER.get();
    if (storeReader != null)
    {
      storeReader.release();
      STORE_READER.set(null);
    }

    SESSION.set(null);
  }
}
