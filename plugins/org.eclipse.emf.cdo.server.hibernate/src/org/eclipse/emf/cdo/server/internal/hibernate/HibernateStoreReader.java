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

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreReader;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.CloseableIterator;

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
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName());
    }
  }

  protected HibernateStoreReader(HibernateStore store, IView view)
  {
    super(store, view);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName());
    }
  }

  public HibernateStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature)
  {
    return new HibernateStoreChunkReader(this, revision, feature);
  }

  public CloseableIterator<CDOID> readObjectIDs(boolean withTypes)
  {
    throw new UnsupportedOperationException();
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    final CDORevision cdoRevision = readRevision(id, -1);
    return cdoRevision.getCDOClass().createClassRef();
  }

  public void readPackage(CDOPackage cdoPackage)
  {
    // Does nothing, assumes that the packages have been read
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

    final Session session = getHibernateSession();
    final Criteria criteria = session.createCriteria(CDOResourceClass.NAME);
    criteria.add(Expression.eq("path", path));
    final List<?> result = criteria.list();
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
    final CDORevision cdoRevision = (CDORevision)result.get(0);
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

    final Session session = getHibernateSession();
    final Query qry = session.createQuery("select path from " + CDOResourceClass.NAME + " where id=:id");
    final CDOIDHibernate idHibernate = (CDOIDHibernate)id;
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
}
