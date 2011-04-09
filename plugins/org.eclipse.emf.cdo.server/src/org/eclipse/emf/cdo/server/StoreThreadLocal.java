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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class StoreThreadLocal
{
  private static final ThreadLocal<InternalSession> SESSION = new InheritableThreadLocal<InternalSession>();

  private static final ThreadLocal<IStoreAccessor> ACCESSOR = new InheritableThreadLocal<IStoreAccessor>();

  private static final ThreadLocal<IStoreAccessor.CommitContext> COMMIT_CONTEXT = new InheritableThreadLocal<IStoreAccessor.CommitContext>();

  private StoreThreadLocal()
  {
  }

  /**
   * @since 3.0
   */
  public static void setSession(InternalSession session)
  {
    SESSION.set(session);
    ACCESSOR.set(null);
  }

  /**
   * Returns the session associated with the current thread.
   * 
   * @return Never <code>null</code>.
   * @throws IllegalStateException
   *           if no session is associated with the current thread.
   * @since 3.0
   */
  public static InternalSession getSession()
  {
    InternalSession session = SESSION.get();
    if (session == null)
    {
      throw new IllegalStateException("session == null"); //$NON-NLS-1$
    }

    return session;
  }

  public static void setAccessor(IStoreAccessor accessor)
  {
    // IStoreAccessor old = ACCESSOR.get();
    // if (old != null && old != accessor)
    // {
    // throw new IllegalStateException("Attempt to overwrite accessor");
    // }

    SESSION.set(accessor == null ? null : accessor.getSession());
    ACCESSOR.set(accessor);
  }

  public static IStoreAccessor getAccessor()
  {
    IStoreAccessor accessor = ACCESSOR.get();
    if (accessor == null)
    {
      ISession session = getSession();
      IStore store = session.getManager().getRepository().getStore();
      accessor = store.getReader(session);
      ACCESSOR.set(accessor);
    }

    return accessor;
  }

  public static void setCommitContext(IStoreAccessor.CommitContext commitContext)
  {
    COMMIT_CONTEXT.set(commitContext);
  }

  public static IStoreAccessor.CommitContext getCommitContext()
  {
    return COMMIT_CONTEXT.get();
  }

  public static void release()
  {
    try
    {
      IStoreAccessor accessor = ACCESSOR.get();
      if (accessor != null)
      {
        if (LifecycleUtil.isActive(accessor))
        {
          accessor.release();
        }
      }
    }
    finally
    {
      ACCESSOR.set(null);
      SESSION.set(null);
      COMMIT_CONTEXT.set(null);
    }
  }
}
