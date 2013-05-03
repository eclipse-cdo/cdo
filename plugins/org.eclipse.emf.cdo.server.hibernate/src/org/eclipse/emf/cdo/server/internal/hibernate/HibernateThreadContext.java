/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Martin Taal
 */
public class HibernateThreadContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateThreadContext.class);

  private static ThreadLocal<HibernateCommitContext> commitContext = new ThreadLocal<HibernateCommitContext>();

  public static HibernateStoreAccessor getCurrentStoreAccessor()
  {
    return (HibernateStoreAccessor)StoreThreadLocal.getAccessor();
  }

  public static HibernateCommitContext getCommitContext()
  {
    HibernateCommitContext result = commitContext.get();
    if (result == null)
    {
      throw new IllegalStateException("CommitContext not set"); //$NON-NLS-1$
    }

    return result;
  }

  public static boolean isCommitContextSet()
  {
    return commitContext.get() != null;
  }

  public static void setCommitContext(InternalCommitContext newCommitContext)
  {
    // in case of xa transactions then the commit context is set again
    // if (newCommitContext != null && commitContext.get() != null)
    // {
    // throw new IllegalStateException("CommitContext already set");
    // }

    if (TRACER.isEnabled())
    {
      if (newCommitContext == null)
      {
        TRACER.trace("Clearing commitcontext in threadlocal"); //$NON-NLS-1$
      }
      else
      {
        TRACER.trace("Setting commitcontext in threadlocal"); //$NON-NLS-1$
      }
    }

    // reset the context
    if (newCommitContext == null)
    {
      commitContext.set(null);
    }
    else
    {
      final HibernateCommitContext hcc = new HibernateCommitContext();
      hcc.setCommitContext(newCommitContext);
      commitContext.set(hcc);
    }
  }
}
