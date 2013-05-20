/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchVersionImpl;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.WrappedHibernateList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.teneo.hibernate.auditing.model.teneoauditing.TeneoAuditEntry;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Implements server side HQL query execution..
 *
 * @author Martin Taal
 */
public class HibernateQueryHandler implements IQueryHandler
{
  /**
   * @deprecated use {@link IHibernateStore#QUERY_LANGUAGE}
   */
  @Deprecated
  public static final String QUERY_LANGUAGE = IHibernateStore.QUERY_LANGUAGE;

  /**
   * @deprecated use {@link IHibernateStore#FIRST_RESULT}
   */
  @Deprecated
  public static final String FIRST_RESULT = IHibernateStore.FIRST_RESULT;

  private HibernateStoreAccessor hibernateStoreAccessor;

  private HibernateAuditHandler hibernateAuditHandler;

  /**
   * Executes hql queries. Gets the session from the {@link HibernateStoreAccessor} creates a hibernate query and sets
   * the parameters taken from the {@link CDOQueryInfo#getParameters()}. Takes into account the
   * {@link CDOQueryInfo#getMaxResults()} and the {@link IHibernateStore#FIRST_RESULT} values for paging.
   *
   * @param info
   *          the object containing the query and parameters
   * @param context
   *          the query results are placed in the context
   * @see IQueryHandler#executeQuery(CDOQueryInfo, IQueryContext)
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    // get a transaction, the hibernateStoreAccessor is placed in a threadlocal
    // so all db access uses the same session.
    final Session session = hibernateStoreAccessor.getHibernateSession();

    // create the query
    final Query query = session.createQuery(info.getQueryString());
    query.setReadOnly(true);

    // get the parameters with some parameter conversion
    int firstResult = -1;
    boolean cacheResults = true;
    for (String key : info.getParameters().keySet())
    {
      if (key.compareToIgnoreCase(IHibernateStore.CACHE_RESULTS) == 0)
      {
        try
        {
          cacheResults = (Boolean)info.getParameters().get(key);
        }
        catch (ClassCastException e)
        {
          throw new IllegalArgumentException(
              "Parameter " + IHibernateStore.CACHE_RESULTS + " must be a boolean. errorMessage " + e.getMessage()); //$NON-NLS-1$
        }
      }
      else if (key.compareToIgnoreCase(IHibernateStore.FIRST_RESULT) == 0)
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
            throw new IllegalArgumentException("Parameter firstResult must be an integer but it is a " + o //$NON-NLS-1$
                + " class " + o.getClass().getName()); //$NON-NLS-1$
          }
        }
      }
      else
      {
        // in case the parameter is a CDOID get the object from the db
        final Object param = info.getParameters().get(key);
        if (param instanceof CDOID && HibernateUtil.getInstance().isStoreCreatedID((CDOID)param))
        {
          final CDOID id = (CDOID)param;
          final String entityName = HibernateUtil.getInstance().getEntityName(id);
          final Serializable idValue = HibernateUtil.getInstance().getIdValue(id);
          final CDORevision revision = (CDORevision)session.get(entityName, idValue);
          query.setEntity(key, revision);
          if (cacheResults)
          {
            addToRevisionCache(revision);
          }
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

    final ScrollableResults scroller = query.scroll(ScrollMode.FORWARD_ONLY);

    // and go for the query
    // future extension: support iterate, scroll through a parameter
    int i = 0;
    try
    {
      while (scroller.next())
      {
        Object[] os = scroller.get();
        Object o;
        if (os.length == 1)
        {
          o = handleAuditEntries(os[0]);
        }
        else
        {
          o = handleAuditEntries(os);
        }

        final boolean addOneMore = context.addResult(o);
        if (cacheResults && o instanceof CDORevision)
        {
          addToRevisionCache((CDORevision)o);
        }
        if (o instanceof InternalCDORevision)
        {
          ((InternalCDORevision)o).freeze();
        }

        // clear the session every 1000 results or so
        if (i++ % 1000 == 0)
        {
          session.clear();
        }

        if (!addOneMore)
        {
          return;
        }
      }
    }
    finally
    {
      scroller.close();
    }
  }

  private Object handleAuditEntries(Object o)
  {
    if (o.getClass().isArray())
    {
      for (int i = 0; i < Array.getLength(o); i++)
      {
        Array.set(o, i, handleAuditEntry(Array.get(o, i)));
      }
      return o;
    }
    return handleAuditEntry(o);
  }

  private Object handleAuditEntry(Object o)
  {
    if (!(o instanceof TeneoAuditEntry))
    {
      // repair revision numbers
      if (o instanceof InternalCDORevision && hibernateStoreAccessor.getStore().isAuditing())
      {
        final InternalCDORevision internalCDORevision = (InternalCDORevision)o;
        // a later revision, get the previous revision
        if (internalCDORevision.getVersion() > 1)
        {
          final CDORevision previousVersion = getPreviousRevision(internalCDORevision);
          if (previousVersion != null)
          {
            internalCDORevision.setBranchPoint(hibernateStoreAccessor.getStore().getMainBranchHead().getBranch()
                .getPoint(1 + previousVersion.getRevised()));
          }
        }
      }

      return o;
    }
    return hibernateAuditHandler.convertAuditEntryToCDORevision((TeneoAuditEntry)o);
  }

  private CDORevision getPreviousRevision(InternalCDORevision internalCDORevision)
  {
    final InternalCDORevisionManager cdoRevisionManager = hibernateStoreAccessor.getStore().getRepository()
        .getRevisionManager();

    final CDOBranchVersion cdoBranchVersion = new CDOBranchVersionImpl(hibernateStoreAccessor.getStore()
        .getMainBranchHead().getBranch(), internalCDORevision.getVersion() - 1);
    if (cdoRevisionManager.containsRevisionByVersion(internalCDORevision.getID(), cdoBranchVersion))
    {
      return cdoRevisionManager.getRevisionByVersion(internalCDORevision.getID(), cdoBranchVersion, -1, true);
    }
    return hibernateStoreAccessor.readRevisionByVersion(internalCDORevision.getID(), cdoBranchVersion, -1,
        cdoRevisionManager);
  }

  private void addToRevisionCache(CDORevision revision)
  {
    final InternalCDORevision internalRevision = (InternalCDORevision)revision;
    for (EStructuralFeature feature : revision.getEClass().getEAllStructuralFeatures())
    {
      if (!isMappedFeature(internalRevision, feature))
      {
        continue;
      }

      if (feature.isMany() || feature instanceof EReference)
      {
        final Object value = internalRevision.getValue(feature);
        if (value instanceof WrappedHibernateList)
        {
          Hibernate.initialize(((WrappedHibernateList)value).getDelegate());
        }
        else
        {
          Hibernate.initialize(value);
        }
      }
    }

    hibernateStoreAccessor.addToRevisionCache(revision);
  }

  private boolean isMappedFeature(InternalCDORevision revision, EStructuralFeature feature)
  {
    try
    {
      int featureID = revision.getClassInfo().getEClass().getFeatureID(feature);
      revision.getClassInfo().getPersistentFeatureIndex(featureID);
      return true;
    }
    catch (IllegalArgumentException ex)
    {
      return false;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      return false;
    }
  }

  public HibernateStoreAccessor getHibernateStoreAccessor()
  {
    return hibernateStoreAccessor;
  }

  public void setHibernateStoreAccessor(HibernateStoreAccessor hibernateStoreAccessor)
  {
    this.hibernateStoreAccessor = hibernateStoreAccessor;
    hibernateAuditHandler = hibernateStoreAccessor.getStore().getHibernateAuditHandler();
  }
}
