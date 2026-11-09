/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * Provides a store accessor for an embedded loader operation without changing an enclosing context.
 *
 * @author Eike Stepper
 */
public final class EmbeddedStoreContext implements AutoCloseable
{
  private final boolean hadSession;

  private final InternalSession session;

  private final boolean hadAccessor;

  private boolean ownsAccessor;

  private IStoreAccessor ownedAccessor;

  private final IStoreAccessor.CommitContext commitContext;

  private boolean closed;

  private EmbeddedStoreContext(InternalSession serverSession, IStore store)
  {
    hadSession = StoreThreadLocal.hasSession();
    session = hadSession ? StoreThreadLocal.getSession() : null;
    hadAccessor = StoreThreadLocal.hasAccessor();
    commitContext = StoreThreadLocal.getCommitContext();

    if (hadAccessor)
    {
      // The enclosing accessor, its session, and its commit context take precedence over the nested caller.
      return;
    }

    if (hadSession && serverSession != null && session != serverSession)
    {
      throw new IllegalStateException("The embedded server session conflicts with the enclosing store session context."); //$NON-NLS-1$
    }

    if (commitContext != null && !hadSession && serverSession != null)
    {
      throw new IllegalStateException("Cannot install an embedded server session over a commit context without its store session."); //$NON-NLS-1$
    }

    if (hadSession || serverSession != null)
    {
      if (!hadSession)
      {
        if (commitContext != null)
        {
          throw new IllegalStateException("Cannot install an embedded server session over a partial commit context."); //$NON-NLS-1$
        }

        StoreThreadLocal.setSession(serverSession);
      }

      try
      {
        IStoreAccessor newAccessor = StoreThreadLocal.getAccessor();
        if (newAccessor == null)
        {
          throw new IllegalStateException("The embedded store returned no reader accessor for its session."); //$NON-NLS-1$
        }

        ownsAccessor = true;
        ownedAccessor = newAccessor;
      }
      catch (RuntimeException | Error ex)
      {
        restoreAfterFailure(ex);
        throw ex;
      }

      return;
    }

    if (commitContext != null)
    {
      throw new IllegalStateException("Cannot install a sessionless accessor over a partial commit context."); //$NON-NLS-1$
    }

    try
    {
      IStoreAccessor newAccessor = store.getReader(null);
      if (newAccessor == null)
      {
        throw new IllegalStateException("The embedded store returned no sessionless reader accessor."); //$NON-NLS-1$
      }

      ownsAccessor = true;
      ownedAccessor = newAccessor;

      StoreThreadLocal.setAccessor(newAccessor);
    }
    catch (RuntimeException | Error ex)
    {
      restoreAfterFailure(ex);
      throw ex;
    }
  }

  private void restore()
  {
    if (hadSession)
    {
      StoreThreadLocal.setSession(session);
    }
    else
    {
      StoreThreadLocal.setSession(null);
    }

    if (commitContext != null)
    {
      StoreThreadLocal.setCommitContext(commitContext);
    }
  }

  private void restoreAfterFailure(Throwable failure)
  {
    if (ownsAccessor)
    {
      try
      {
        releaseOwnedAccessor();
      }
      catch (Throwable cleanupFailure)
      {
        failure.addSuppressed(cleanupFailure);
      }
    }

    try
    {
      restore();
    }
    catch (Throwable cleanupFailure)
    {
      failure.addSuppressed(cleanupFailure);
    }
  }

  private void releaseOwnedAccessor()
  {
    if (StoreThreadLocal.hasAccessor() && StoreThreadLocal.getAccessor() == ownedAccessor)
    {
      StoreThreadLocal.release();
      return;
    }

    boolean unexpectedAccessor = StoreThreadLocal.hasAccessor();
    if (unexpectedAccessor)
    {
      StoreThreadLocal.setAccessor(null);
    }
    else
    {
      StoreThreadLocal.setSession(null);
    }

    if (LifecycleUtil.isActive(ownedAccessor))
    {
      ownedAccessor.release();
    }

    if (unexpectedAccessor)
    {
      throw new IllegalStateException("The embedded store accessor context changed during a loader operation."); //$NON-NLS-1$
    }
  }

  @Override
  public void close()
  {
    if (closed)
    {
      return;
    }

    closed = true;

    if (!ownsAccessor)
    {
      return;
    }

    Throwable failure = null;

    try
    {
      releaseOwnedAccessor();
    }
    catch (RuntimeException | Error ex)
    {
      failure = ex;
    }

    try
    {
      restore();
    }
    catch (RuntimeException | Error ex)
    {
      if (failure == null)
      {
        failure = ex;
      }
      else
      {
        failure.addSuppressed(ex);
      }
    }

    if (failure instanceof RuntimeException)
    {
      throw (RuntimeException)failure;
    }

    if (failure instanceof Error)
    {
      throw (Error)failure;
    }
  }

  public static EmbeddedStoreContext install(InternalSession serverSession, IStore store)
  {
    return new EmbeddedStoreContext(serverSession, store);
  }
}
