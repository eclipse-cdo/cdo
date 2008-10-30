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
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceNodeClass;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.StoreAccessor;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreAccessor;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.PersistableListHolder;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStoreAccessor extends StoreAccessor implements IHibernateStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStoreAccessor.class);

  private Session hibernateSession;

  private boolean errorOccured = false;

  public HibernateStoreAccessor(HibernateStore store, ISession session)
  {
    super(store, session);
    HibernateThreadContext.setCurrentHibernateStoreAccessor(this);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName());
    }
  }

  public HibernateStoreAccessor(HibernateStore store, ITransaction transaction)
  {
    super(store, transaction);
    HibernateThreadContext.setCurrentHibernateStoreAccessor(this);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName());
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

  /**
   * starts a hibernate session and begins a transaction
   * 
   * @since 2.0
   */
  public void beginHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating hibernate session and transaction");
    }

    assert hibernateSession == null;
    final SessionFactory sessionFactory = getStore().getHibernateSessionFactory();
    hibernateSession = sessionFactory.openSession();
    hibernateSession.beginTransaction();
  }

  /**
   * commits/rollbacks and closes the session
   * 
   * @since 2.0
   */
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

  /**
   * @since 2.0
   */
  public boolean isErrorOccured()
  {
    return errorOccured;
  }

  /**
   * @since 2.0
   */
  public void setErrorOccured(boolean errorOccured)
  {
    this.errorOccured = errorOccured;
  }

  public HibernateStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature)
  {
    return new HibernateStoreChunkReader(this, revision, feature);
  }

  public CloseableIterator<Object> createQueryIterator(CDOQueryInfo queryInfo)
  {
    // TODO: implement HibernateStoreAccessor.createQueryIterator(queryInfo)
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

  public void readPackageEcore(CDOPackage cdoPackage)
  {
    throw new UnsupportedOperationException();
  }

  public Collection<CDOPackageInfo> readPackageInfos()
  {
    return getStore().getPackageHandler().getCDOPackageInfos();
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
   * TODO Clarify the meaning of {@link IStoreAccessor#refreshRevisions()}
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
    CDOIDHibernate folderID = getHibernateID(context.getFolderID());
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    final Session session = getHibernateSession();
    final Criteria criteria = session.createCriteria(CDOResourceNodeClass.NAME);
    if (folderID == null)
    {
      criteria.add(Expression.isNull("containerID"));
    }
    else
    {
      criteria.add(Expression.eq("containerID", folderID));
    }

    List<?> result = criteria.list();
    for (Object o : result)
    {
      final CDORevision revision = (CDORevision)o;
      String revisionName = (String)revision.getData().get(getResourceNameFeature(), 0);
      boolean match = exactMatch || revisionName == null || name == null ? ObjectUtil.equals(revisionName, name)
          : revisionName.startsWith(name);

      if (match && !context.addResource(revision.getID()))
      {
        // No more results allowed
        break;
      }
    }
  }

  private CDOIDHibernate getHibernateID(CDOID id)
  {
    if (!CDOIDUtil.isNull(id))
    {
      if (id instanceof CDOIDHibernate)
      {
        return (CDOIDHibernate)id;
      }

      // TODO Can this happen? When?
      final long longID = CDOIDUtil.getLong(id);
      return CDOIDHibernateFactoryImpl.getInstance().createCDOID(longID, CDOResourceNodeClass.NAME);
    }

    return null;
  }

  private CDOFeature getResourceNameFeature()
  {
    return getResourceNodeClass().getCDONameFeature();
  }

  private CDOResourceNodeClass getResourceNodeClass()
  {
    return getStore().getRepository().getPackageManager().getCDOResourcePackage().getCDOResourceNodeClass();
  }

  /**
   * @since 2.0
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    // TODO: implement HibernateStoreAccessor.executeQuery(info, context)
    throw new UnsupportedOperationException();
  }

  /**
   * Is handled through {@link #endHibernateSession()}.
   */
  public void commit()
  {
    // Do nothing
  }

  @Override
  public void write(CommitContext context)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Committing transaction");
    }

    HibernateThreadContext.setCommitContext(context);
    writePackages(context.getNewPackages());
    boolean err = true;

    try
    {
      // start with fresh hibernate session
      final Session session = getHibernateSession();
      session.setFlushMode(FlushMode.MANUAL);

      final List<CDORevision> cdoRevisions = Arrays.asList(context.getNewObjects());

      // keep track for which cdoRevisions the container id needs to be repaired afterwards
      final List<InternalCDORevision> repairContainerIDs = new ArrayList<InternalCDORevision>();

      // first save the non-cdoresources
      for (CDORevision cdoRevision : cdoRevisions)
      {
        if (cdoRevision instanceof InternalCDORevision)
        {
          final CDOID containerID = (CDOID)((InternalCDORevision)cdoRevision).getContainerID();
          if (containerID instanceof CDOIDTemp && !containerID.isNull())
          {
            repairContainerIDs.add((InternalCDORevision)cdoRevision);
          }
        }

        session.save(HibernateUtil.getInstance().getEntityName(cdoRevision), cdoRevision);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Persisted new Object " + cdoRevision.getCDOClass().getName() + " id: " + cdoRevision.getID());
        }
      }

      // first repair the version for all dirty objects
      for (CDORevision cdoRevision : context.getDirtyObjects())
      {
        if (cdoRevision instanceof InternalCDORevision)
        {
          ((InternalCDORevision)cdoRevision).setVersion(cdoRevision.getVersion() - 1);
        }
      }

      for (CDORevision cdoRevision : context.getDirtyObjects())
      {
        session.update(HibernateUtil.getInstance().getEntityName(cdoRevision), cdoRevision);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Updated Object " + cdoRevision.getCDOClass().getName() + " id: " + cdoRevision.getID());
        }
      }

      session.flush();

      // now do an update of the container without incrementing the version
      for (InternalCDORevision cdoRevision : repairContainerIDs)
      {
        final CDORevision container = HibernateUtil.getInstance().getCDORevision((CDOID)cdoRevision.getContainerID());
        final String entityName = HibernateUtil.getInstance().getEntityName(cdoRevision);
        final CDOIDHibernate id = (CDOIDHibernate)cdoRevision.getID();
        final CDOIDHibernate containerID = (CDOIDHibernate)container.getID();
        final String hqlUpdate = "update " + entityName
            + " set contID_Entity = :contEntity, contID_ID=:contID, contID_class=:contClass where e_id = :id";
        final Query qry = session.createQuery(hqlUpdate);
        qry.setParameter("contEntity", containerID.getEntityName());
        qry.setParameter("contID", containerID.getId().toString());
        qry.setParameter("contClass", containerID.getId().getClass().getName());
        qry.setParameter("id", id.getId());
        if (qry.executeUpdate() != 1)
        {
          throw new IllegalStateException("Not able to update container columns of " + entityName + " with id " + id);
        }
      }

      session.flush();

      // does the commit
      endHibernateSession();
      err = false;
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
      throw WrappedException.wrap(e);
    }
    finally
    {
      if (err)
      {
        setErrorOccured(true);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Clearing used hibernate session");
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Applying id mappings");
      }

      context.applyIDMappings();
      HibernateThreadContext.setCommitContext(null);
    }
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, long revised)
  {
    // TODO: implement HibernateStoreAccessor.detachObjects(detachedObjects)
    throw new UnsupportedOperationException();
  }

  @Override
  protected void rollback(CommitContext context)
  {
    // Don't do anything as the real action is done at commit (which does not happen now)
    if (TRACER.isEnabled())
    {
      TRACER.trace("Rollbacked called");
    }
  }

  @Override
  protected void writePackages(CDOPackage[] cdoPackages)
  {
    if (cdoPackages != null && cdoPackages.length != 0)
    {
      getStore().getPackageHandler().writePackages(cdoPackages);
    }

    // Set a new hibernatesession in the thread
    resetHibernateSession();
  }

  @Override
  protected void writeRevisions(CDORevision[] revisions)
  {
    // Don't do anything it is done at commit
  }

  @Override
  protected void writeRevisionDeltas(CDORevisionDelta[] revisionDeltas, long created)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doActivate() throws Exception
  {
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    // TODO This method is called when this accessor is not needed anymore
    if (TRACER.isEnabled())
    {
      TRACER.trace("Committing/rollback and closing hibernate session");
    }

    try
    {
      endHibernateSession();
      PersistableListHolder.getInstance().clearListMapping();
    }
    finally
    {
      HibernateThreadContext.setCurrentHibernateStoreAccessor(this);
    }
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // TODO This method is called right before this accessor is added to a pool
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // TODO This method is called right after this accessor is removed from a pool
  }
}
