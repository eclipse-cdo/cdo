/*
 * Copyright (c) 2008-2013, 2016-2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.concurrent.Callable;

/**
 * Provides server-side consumers with the {@link IStoreAccessor store accessor} that is valid in the context of a
 * specific {@link ISession session} during read operations or a specific {@link CommitContext commit context} during
 * write operations.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public final class StoreThreadLocal
{
  private static final boolean DEBUG_SESSION = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.StoreThreadLocal.DEBUG_SESSION");

  private static final ThreadLocal<InternalSession> SESSION = new ThreadLocal<>();

  private static final ThreadLocal<IStoreAccessor> ACCESSOR = new ThreadLocal<>();

  private static final ThreadLocal<IStoreAccessor.CommitContext> COMMIT_CONTEXT = new ThreadLocal<>();

  private StoreThreadLocal()
  {
  }

  /**
   * @since 4.11
   */
  public static Runnable wrap(ISession session, Runnable runnable)
  {
    return () -> {
      StoreThreadLocal.setSession((InternalSession)session);

      try
      {
        runnable.run();
      }
      finally
      {
        StoreThreadLocal.release();
      }
    };
  }

  /**
   * @since 4.11
   */
  public static <T> Callable<T> wrap(ISession session, Callable<T> callable)
  {
    return () -> {
      StoreThreadLocal.setSession((InternalSession)session);

      try
      {
        return callable.call();
      }
      finally
      {
        StoreThreadLocal.release();
      }
    };
  }

  /**
   * @since 3.0
   */
  public static void setSession(InternalSession session)
  {
    if (session == null)
    {
      REMOVE_SESSION();
    }
    else
    {
      SET_SESSION(session);
    }

    ACCESSOR.remove();
    COMMIT_CONTEXT.remove();
  }

  /**
   * Returns the session associated with the current thread.
   *
   * @return Never <code>null</code>.
   * @throws IllegalStateException
   *           if no session is associated with the current thread.
   * @since 3.0
   */
  public static InternalSession getSession() throws NoSessionRegisteredException
  {
    InternalSession session = SESSION.get();
    if (session == null)
    {
      throw new NoSessionRegisteredException();
    }

    return session;
  }

  /**
   * @since 4.2
   */
  public static boolean hasSession()
  {
    return SESSION.get() != null;
  }

  public static void setAccessor(IStoreAccessor accessor)
  {
    if (accessor == null)
    {
      ACCESSOR.remove();
      REMOVE_SESSION();
    }
    else
    {
      ACCESSOR.set(accessor);
      SET_SESSION(accessor.getSession());
    }
  }

  public static IStoreAccessor getAccessor() throws NoSessionRegisteredException
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

  /**
   * @since 4.7
   */
  public static boolean hasAccessor()
  {
    return ACCESSOR.get() != null;
  }

  public static void setCommitContext(IStoreAccessor.CommitContext commitContext)
  {
    if (commitContext == null)
    {
      COMMIT_CONTEXT.remove();
    }
    else
    {
      COMMIT_CONTEXT.set(commitContext);
    }
  }

  public static IStoreAccessor.CommitContext getCommitContext()
  {
    return COMMIT_CONTEXT.get();
  }

  /**
   * @since 4.7
   */
  public static boolean hasCommitContext()
  {
    return COMMIT_CONTEXT.get() != null;
  }

  /**
   * @since 4.2
   */
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
      remove();
    }
  }

  /**
   * @since 4.5
   */
  public static void remove()
  {
    ACCESSOR.remove();
    REMOVE_SESSION();
    COMMIT_CONTEXT.remove();
  }

  private static void SET_SESSION(InternalSession session)
  {
    if (session != null)
    {
      if (DEBUG_SESSION)
      {
        InternalSession oldSession = SESSION.get();
        if (oldSession != null)
        {
          throw new IllegalStateException("Session already registered: " + oldSession);
        }

        IOUtil.OUT().println("Set        " + session + " --> " + Thread.currentThread().getName());
      }

      SESSION.set(session);
    }
  }

  private static void REMOVE_SESSION()
  {
    if (DEBUG_SESSION)
    {
      InternalSession session = SESSION.get();
      if (session != null)
      {
        IOUtil.OUT().println("    Remove " + session + " --> " + Thread.currentThread().getName());
      }
    }

    SESSION.remove();
  }

  /**
   * An {@link IllegalStateException} that can be thrown from {@link StoreThreadLocal#getSession()}.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  public static final class NoSessionRegisteredException extends IllegalStateException
  {
    private static final long serialVersionUID = 1L;

    public NoSessionRegisteredException()
    {
      super("session == null");
    }
  }
}
