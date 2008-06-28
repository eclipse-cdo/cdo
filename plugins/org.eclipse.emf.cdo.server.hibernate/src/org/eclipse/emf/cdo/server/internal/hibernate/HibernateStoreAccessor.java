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
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

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

  private boolean errorOccured = false;

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

  @Override
  protected void doRelease()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Committing/rollback and closing hibernate session");
    }

    // this try/catch can disappear when in transaction commit the release
    // of the accessor is done after the
    try
    {
      // ugly cast
      StoreUtil.setReader((IStoreReader)this);
      endHibernateSession();
    }
    finally
    {
      StoreUtil.setReader(null);
    }
  }

  /** Clears the current hibernate session and sets a new one in the thread context */
  public void resetHibernateSession()
  {
    endHibernateSession();
    beginHibernateSession();
  }

  @Override
  public HibernateStore getStore()
  {
    return (HibernateStore)super.getStore();
  }

  // starts a hibernate session and begins a transaction
  public void beginHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating hibernate session and transaction");
    }
    assert (hibernateSession == null);
    final SessionFactory sessionFactory = getStore().getHibernateSessionFactory();
    hibernateSession = sessionFactory.openSession();
    hibernateSession.beginTransaction();
  }

  // commits/rollbacks and closes the session
  public void endHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Closing hibernate session");
    }

    if (hibernateSession != null && hibernateSession.isOpen())
    {
      try
      {
        if (isErrorOccured())
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Rolling back hb transaction");
          }
          hibernateSession.getTransaction().rollback();
        }
        else
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Committing hb transaction");
          }
          hibernateSession.getTransaction().commit();
        }
      }
      finally
      {
        hibernateSession.close();
      }
    }

    hibernateSession = null;
  }

  public Session getHibernateSession()
  {
    if (hibernateSession == null)
    {
      beginHibernateSession();
    }
    return hibernateSession;
  }

  public boolean isErrorOccured()
  {
    return errorOccured;
  }

  public void setErrorOccured(boolean errorOccured)
  {
    this.errorOccured = errorOccured;
  }
}
