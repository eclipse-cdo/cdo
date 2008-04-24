/***************************************************************************
 * Copyright (c) 2004 - 2008 Martin Taal and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.IStoreWriter.CommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.hibernate.Session;

/**
 * @author Martin Taal
 */
public class HibernateThreadContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateThreadContext.class);

  private static ThreadLocal<CommitContext> commitContext = new ThreadLocal<CommitContext>();

  private static ThreadLocal<Session> session = new ThreadLocal<Session>();

  public static Session getSession()
  {
    Session result = session.get();
    if (result == null)
    {
      throw new IllegalStateException("Session not set");
    }

    return result;
  }

  public static void setSession(Session newSession)
  {
    if (newSession != null && session.get() != null)
    {
      throw new IllegalStateException("Session already set");
    }

    if (TRACER.isEnabled())
    {
      if (newSession == null)
      {
        TRACER.trace("Clearing session in threadlocal");
      }
      else
      {
        TRACER.trace("Setting session in threadlocal");
      }
    }

    session.set(newSession);
  }

  public static CommitContext getCommitContext()
  {
    CommitContext result = commitContext.get();
    if (result == null)
    {
      throw new IllegalStateException("CommitContext not set");
    }

    return result;
  }

  public static boolean isCommitContextSet()
  {
    return commitContext.get() != null;
  }

  public static void setCommitContext(CommitContext newCommitContext)
  {
    if (newCommitContext != null && commitContext.get() != null)
    {
      throw new IllegalStateException("CommitContext already set");
    }

    if (TRACER.isEnabled())
    {
      if (newCommitContext == null)
      {
        TRACER.trace("Clearing commitcontext in threadlocal");
      }
      else
      {
        TRACER.trace("Setting commitcontext in threadlocal");
      }
    }

    commitContext.set(newCommitContext);
  }
}
