/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;

import org.hibernate.Query;
import org.hibernate.Session;

import java.io.Serializable;

/**
 * Implements server side HQL query execution..
 * 
 * @author Martin Taal
 */
public class HibernateQueryHandler implements IQueryHandler
{
  public static final String QUERY_LANGUAGE = "hql";

  public static final String FIRST_RESULT = "firstResult";

  private HibernateStoreAccessor hibernateStoreAccessor;

  /**
   * Executes hql queries. Gets the session from the {@link HibernateStoreAccessor} creates a hibernate query and sets
   * the parameters taken from the {@link CDOQueryInfo#getParameters()}. Takes into account the
   * {@link CDOQueryInfo#getMaxResults()} and the {@link HibernateQueryHandler#FIRST_RESULT} values for paging.
   * 
   * @param info
   *          the object containing the query and parameters
   * @param context
   *          the query results are placed in the context
   * @see IQueryHandler#executeQuery(CDOQueryInfo, IQueryContext)
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    if (!QUERY_LANGUAGE.equals(info.getQueryLanguage().toLowerCase()))
    {
      throw new IllegalArgumentException("Query language " + info.getQueryLanguage() + " not supported by this store");
    }

    // get a transaction, the hibernateStoreAccessor is placed in a threadlocal
    // so all db access uses the same session.
    final Session session = hibernateStoreAccessor.getHibernateSession();

    try
    {
      // create the query
      final Query query = session.createQuery(info.getQueryString());

      // get the parameters with some parameter conversion
      int firstResult = -1;
      for (String key : info.getParameters().keySet())
      {
        if (key.toLowerCase().equals(FIRST_RESULT.toLowerCase()))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              firstResult = (Integer)o;
            }
            catch (ClassCastException e)
            {
              throw new IllegalArgumentException("Parameter firstResult must be an integer but it is a " + o
                  + " class " + o.getClass().getName());
            }
          }
        }
        else
        {
          // in case the parameter is a CDOID get the object from the db
          final Object param = info.getParameters().get(key);
          if (param instanceof CDOID && HibernateUtil.getInstance().isStoreCreatedID((CDOID)param))
          {
            final CDOID cdoID = (CDOID)param;
            final String entityName = HibernateUtil.getInstance().getEntityName(cdoID);
            final Serializable idValue = HibernateUtil.getInstance().getIdValue(cdoID);
            final CDORevision revision = (CDORevision)session.get(entityName, idValue);
            query.setEntity(key, revision);
            hibernateStoreAccessor.addToRevisionCache(revision);
          }
          else
          {
            query.setParameter(key, param);
          }
        }
      }

      // set the first result
      if (firstResult > -1)
      {
        query.setFirstResult(firstResult);
      }

      // the max result
      if (info.getMaxResults() != CDOQueryInfo.UNLIMITED_RESULTS)
      {
        query.setMaxResults(info.getMaxResults());
      }

      // and go for the query
      // future extension: support iterate, scroll through a parameter
      for (Object o : query.list())
      {
        final boolean addOneMore = context.addResult(o);
        hibernateStoreAccessor.addToRevisionCache(o);
        if (!addOneMore)
        {
          return;
        }
      }
    }
    finally
    {
      hibernateStoreAccessor.endHibernateSession();
    }
  }

  public HibernateStoreAccessor getHibernateStoreAccessor()
  {
    return hibernateStoreAccessor;
  }

  public void setHibernateStoreAccessor(HibernateStoreAccessor hibernateStoreAccessor)
  {
    this.hibernateStoreAccessor = hibernateStoreAccessor;
  }
}
