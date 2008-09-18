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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreReader;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStoreReader extends HibernateStoreAccessor implements IHibernateStoreReader
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStoreReader.class);

  public HibernateStoreReader(HibernateStore store, ISession session)
  {
    super(store, session);
    HibernateThreadContext.setCurrentHibernateStoreAccessor(this);
  }

  protected HibernateStoreReader(HibernateStore store, IView view)
  {
    super(store, view);
    HibernateThreadContext.setCurrentHibernateStoreAccessor(this);
  }

  public HibernateStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature)
  {
    return new HibernateStoreChunkReader(this, revision, feature);
  }

  public CloseableIterator<Object> createQueryIterator(CDOQueryInfo queryInfo)
  {
    // TODO: implement HibernateStoreReader.createQueryIterator(queryInfo)
    throw new UnsupportedOperationException();
  }

  public CloseableIterator<CDOID> readObjectIDs()
  {
    throw new UnsupportedOperationException();
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    CDORevision cdoRevision = readRevision(id, -1);
    return cdoRevision.getCDOClass().createClassRef();
  }

  public void readPackage(CDOPackage cdoPackage)
  {
    getStore().getPackageHandler().readPackage(cdoPackage);
  }

  public Collection<CDOPackageInfo> readPackageInfos()
  {
    return getStore().getPackageHandler().getCDOPackageInfos();
  }

  public CDOID readResourceID(String path)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Finding resourceid using path " + path);
    }

    Session session = getHibernateSession();
    Criteria criteria = session.createCriteria(CDOResourceClass.NAME);
    criteria.add(Expression.eq("path", path));
    List<?> result = criteria.list();
    if (result.size() == 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Resource not found");
      }

      // TODO: throw exception?
      return null;
    }

    // TODO: throw exception if list.size() > 1?
    CDORevision cdoRevision = (CDORevision)result.get(0);
    return cdoRevision.getID();
  }

  public String readResourcePath(CDOID id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("ID must be not null");
    }

    if (!(id instanceof CDOIDHibernate))
    {
      throw new IllegalArgumentException("ID type " + id.getClass().getName() + " not supported by hibernate reader");
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Finding resource using id " + id);
    }

    Session session = getHibernateSession();
    Query qry = session.createQuery("select path from " + CDOResourceClass.NAME + " where id=:id");
    CDOIDHibernate idHibernate = (CDOIDHibernate)id;
    qry.setParameter("id", idHibernate.getId());
    final List<?> result = qry.list();
    if (result.size() == 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Resource not found");
      }

      // TODO: throw exception?
      return null;
    }

    return (String)result.get(0);
  }

  public CDORevision readRevision(CDOID id, int referenceChunk)
  {
    return HibernateUtil.getInstance().getCDORevision(id);
  }

  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    // TODO Could be necessary to implement
    throw new UnsupportedOperationException();
  }

  /**
   * TODO Clarify the meaning of {@link IStoreReader#refreshRevisions()}
   * 
   * @since 2.0
   */
  public void refreshRevisions()
  {
    // Do nothing
  }

  /**
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    // TODO: implement HibernateStoreReader.queryResources(context)
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    // TODO: implement HibernateStoreReader.executeQuery(info, context)
    throw new UnsupportedOperationException();
  }

  @Override
  // this method can disappear when in transaction commit the release
  // of the accessor is done before the StoreUtil.setReader(null),
  // see the Transaction
  protected void doDeactivate() throws Exception
  {
    try
    {
      // ugly cast
      super.doDeactivate();
    }
    finally
    {
      HibernateThreadContext.setCurrentHibernateStoreAccessor(this);
    }
  }
}
