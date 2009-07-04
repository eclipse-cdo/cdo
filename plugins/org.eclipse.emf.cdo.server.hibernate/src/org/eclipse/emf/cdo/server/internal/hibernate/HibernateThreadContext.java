/**
 * Copyright (c) 2004 - 2009 Martin Taal and others. and others
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

import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Martin Taal
 */
public class HibernateThreadContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateThreadContext.class);

  private static ThreadLocal<HibernateCommitContext> commitContext = new ThreadLocal<HibernateCommitContext>();

  private static ThreadLocal<HibernateStoreAccessor> accessor = new ThreadLocal<HibernateStoreAccessor>();

  public static HibernateStoreAccessor getCurrentStoreAccessor()
  {
    return accessor.get();
  }

  public static void setCurrentStoreAccessor(HibernateStoreAccessor storeAccessor)
  {
    accessor.set(storeAccessor);
  }

  public static HibernateCommitContext getCommitContext()
  {
    HibernateCommitContext result = commitContext.get();
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
