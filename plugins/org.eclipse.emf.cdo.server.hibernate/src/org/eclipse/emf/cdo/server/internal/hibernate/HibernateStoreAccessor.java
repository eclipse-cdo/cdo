/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - specific hibernate functionality
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.internal.server.StoreAccessor;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStoreAccessor extends StoreAccessor implements IHibernateStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStoreAccessor.class);

  private Session hibernateSession;

  protected HibernateStoreAccessor(HibernateStore store, ISession session)
  {
    super(store, session);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName());
    }
  }

  protected HibernateStoreAccessor(HibernateStore store, IView view)
  {
    super(store, view);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName());
    }
  }

  protected Session createHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating hibernate session and setting it in threadlocal HibernateThreadContext");
    }

    final SessionFactory sessionFactory = getStore().getHibernateSessionFactory();
    final Session session = sessionFactory.openSession();
    HibernateThreadContext.setSession(session);
    return session;
  }

  @Override
  protected void doRelease()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Releasing hibernate session");
    }

    HibernateThreadContext.setSession(null);
    clearHibernateSession();
  }

  /** Clears the current hibernate session and sets a new one in the thread context */
  public void resetHibernateSession()
  {
    clearHibernateSession();
    getHibernateSession();
  }

  @Override
  public HibernateStore getStore()
  {
    return (HibernateStore)super.getStore();
  }

  public void clearHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Removing hibernate session");
    }

    if (hibernateSession != null && hibernateSession.isOpen())
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Closing hibernate session");
      }

      hibernateSession.close();
    }

    hibernateSession = null;
  }

  public Session getHibernateSession()
  {
    if (hibernateSession == null)
    {
      hibernateSession = createHibernateSession();
    }

    return hibernateSession;
  }
}
