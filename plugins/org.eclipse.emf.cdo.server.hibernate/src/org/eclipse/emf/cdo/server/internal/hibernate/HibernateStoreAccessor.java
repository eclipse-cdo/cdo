/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - specific hibernate functionality
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreAccessor;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.PersistableListHolder;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.StoreAccessor;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
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
    HibernateThreadContext.setCurrentStoreAccessor(this);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName());
    }
  }

  public HibernateStoreAccessor(HibernateStore store, ITransaction transaction)
  {
    super(store, transaction);
    HibernateThreadContext.setCurrentStoreAccessor(this);
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
   * Commits the session
   * 
   * @since 2.0
   */
  public void commitRollbackHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Commiting hibernate session");
    }

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
        if (hibernateSession.getTransaction().isActive())
        {
          commitRollbackHibernateSession();
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

  public HibernateStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new HibernateStoreChunkReader(this, revision, feature);
  }

  public CloseableIterator<Object> createQueryIterator(CDOQueryInfo queryInfo)
  {
    // TODO: implement HibernateStoreAccessor.createQueryIterator(queryInfo)
    throw new UnsupportedOperationException();
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return getStore().getPackageHandler().getPackageUnits();
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return getStore().getPackageHandler().loadPackageUnit(packageUnit);
  }

  public InternalCDORevision readRevision(CDOID id, int listChunk, AdditionalRevisionCache cache)
  {
    if (id instanceof CDOIDHibernate)
    {
      return HibernateUtil.getInstance().getCDORevision(id);
    }

    return null;
  }

  public InternalCDORevision readRevisionByTime(CDOID id, int listChunk, AdditionalRevisionCache cache, long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, int listChunk, AdditionalRevisionCache cache, int version)
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
    final Criteria criteria = session.createCriteria(EresourcePackage.eINSTANCE.getCDOResourceNode().getName());
    if (folderID == null)
    {
      criteria.add(org.hibernate.criterion.Restrictions.isNull("containerID"));
    }
    else
    {
      criteria.add(org.hibernate.criterion.Restrictions.eq("containerID", folderID));
    }

    List<?> result = criteria.list();
    for (Object o : result)
    {
      final CDORevision revision = (CDORevision)o;
      final EStructuralFeature feature = revision.getEClass().getEStructuralFeature("name");
      if (feature != null)
      {
        String revisionName = (String)revision.data().get(feature, 0);
        boolean match = exactMatch || revisionName == null || name == null ? ObjectUtil.equals(revisionName, name)
            : revisionName.startsWith(name);

        if (match && !context.addResource(revision.getID()))
        {
          // No more results allowed
          break;
        }
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
      return CDOIDHibernateFactoryImpl.getInstance().createCDOID(longID,
          EresourcePackage.eINSTANCE.getCDOResourceNode().getName());
    }

    return null;
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    final HibernateQueryHandler queryHandler = new HibernateQueryHandler();
    queryHandler.setHibernateStoreAccessor(this);
    return queryHandler;
  }

  /**
   * Is handled through {@link #endHibernateSession()}.
   */
  public void commit(OMMonitor monitor)
  {
    commitRollbackHibernateSession();
    HibernateThreadContext.setCommitContext(null);
  }

  @Override
  public void write(IStoreAccessor.CommitContext context, OMMonitor monitor)
  {
    HibernateThreadContext.setCommitContext(context);
    if (context.getNewPackageUnits().length > 0)
    {
      writePackageUnits(context.getNewPackageUnits(), monitor);
    }

    try
    {
      // start with fresh hibernate session
      final Session session = getHibernateSession();
      session.setFlushMode(FlushMode.MANUAL);

      // first decrease the version for all dirty objects
      // hibernate will increase it again
      for (CDORevision revision : context.getDirtyObjects())
      {
        if (revision instanceof InternalCDORevision)
        {
          InternalCDORevision internalRevision = (InternalCDORevision)revision;
          internalRevision.setVersion(internalRevision.getVersion() - 1);
        }
      }

      final List<InternalCDORevision> repairContainerIDs = new ArrayList<InternalCDORevision>();
      for (InternalCDORevision revision : context.getNewObjects())
      {
        // keep track for which cdoRevisions the container id needs to be repaired afterwards
        if (revision instanceof InternalCDORevision)
        {
          final CDOID containerID = (CDOID)revision.getContainerID();
          if (containerID instanceof CDOIDTemp && !containerID.isNull())
          {
            repairContainerIDs.add(revision);
          }
        }

        session.saveOrUpdate(HibernateUtil.getInstance().getEntityName(revision), revision);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Persisted new Object " + revision.getEClass().getName() + " id: " + revision.getID());
        }
      }

      session.flush();

      for (CDORevision revision : context.getDirtyObjects())
      {
        final CDOIDHibernate hibernateCDOID = (CDOIDHibernate)revision.getID();
        // Object loadedRevision = session.get(hibernateCDOID.getEntityName(), hibernateCDOID.getId());
        // if (loadedRevision != revision)
        // {
        // session.merge(revision);
        // }
        // else
        // {
        session.saveOrUpdate(hibernateCDOID.getEntityName(), revision);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Updated Object " + revision.getEClass().getName() + " id: " + revision.getID());
        }
      }

      session.flush();

      // delete all objects
      for (CDOID id : context.getDetachedObjects())
      {
        final CDORevision revision = HibernateUtil.getInstance().getCDORevision(id);
        // maybe deleted in parallell?
        if (revision != null)
        {
          session.delete(revision);
        }
      }

      session.flush();

      // now do an update of the container without incrementing the version
      for (InternalCDORevision revision : repairContainerIDs)
      {
        final CDORevision container = HibernateUtil.getInstance().getCDORevision((CDOID)revision.getContainerID());
        final String entityName = HibernateUtil.getInstance().getEntityName(revision);
        final CDOIDHibernate id = (CDOIDHibernate)revision.getID();
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
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
      throw WrappedException.wrap(e);
    }

    context.applyIDMappings(monitor);
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, long revised, OMMonitor monitor)
  {
    // handled by the write method
  }

  @Override
  protected void rollback(CommitContext context)
  {
    setErrorOccured(true);
    endHibernateSession();
    HibernateThreadContext.setCommitContext(null);
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    if (packageUnits != null && packageUnits.length != 0)
    {
      getStore().getPackageHandler().writePackageUnits(packageUnits);
    }

    // Set a new hibernatesession in the thread
    resetHibernateSession();
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, OMMonitor monitor)
  {
    // Don't do anything it is done at commit
  }

  @Override
  protected void addIDMappings(CommitContext context, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, long created, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doActivate() throws Exception
  {
    HibernateThreadContext.setCurrentStoreAccessor(this);
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
    }
    finally
    {
      clearThreadState();
    }
  }

  @Override
  protected void doPassivate() throws Exception
  {
    clearThreadState();
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    HibernateThreadContext.setCurrentStoreAccessor(this);
  }

  private void clearThreadState()
  {
    PersistableListHolder.getInstance().clearListMapping();
    HibernateThreadContext.setCurrentStoreAccessor(null);
    HibernateThreadContext.setCommitContext(null);
  }
}
