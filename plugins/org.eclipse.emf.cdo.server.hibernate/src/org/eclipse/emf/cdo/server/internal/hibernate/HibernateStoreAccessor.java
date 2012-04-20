/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.PersistableListHolder;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.WrappedHibernateList;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessor;
import org.eclipse.emf.cdo.spi.server.StoreChunkReader;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements the runtime behavior of accessing the hibernate store using queries and doing write and commit. The
 * HibernateStoreAccessor corresponds roughly to a Hibernate session. It offers methods to create and close them and
 * implements transaction handling. The main update/create/delete operations are done in the
 * {@link #write(InternalCommitContext, OMMonitor)} method.
 *
 * @see HibernateStore
 * @see HibernatePackageHandler
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStoreAccessor extends StoreAccessor implements IHibernateStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStoreAccessor.class);

  private static final String NAME_EFEATURE_NAME = "name";//$NON-NLS-1$

  private Session hibernateSession;

  private boolean errorOccured;

  public void addToRevisionCache(Object object)
  {
    if (object instanceof CDORevision)
    {
      getStore().getRepository().getRevisionManager().addRevision((CDORevision)object);
    }
    else if (object instanceof Object[])
    {
      // handle hibernate query result
      final Object[] objects = (Object[])object;
      for (Object o : objects)
      {
        addToRevisionCache(o);
      }
    }

    // also primitive types can get here, ignore those
  }

  /**
   * Constructor
   *
   * @param store
   *          the {@link Store} used by the accessor.
   * @param session
   *          the client session (not a Hibernate Session)
   */
  public HibernateStoreAccessor(HibernateStore store, ISession session)
  {
    super(store, session);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * Constructor for a specific transaction
   *
   * @param store
   *          the HibernateStore backing this accessor
   * @param transaction
   *          the client transaction (not the a Hibernate transaction)
   */
  public HibernateStoreAccessor(HibernateStore store, ITransaction transaction)
  {
    super(store, transaction);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + this.getClass().getName() + " for repository " + store.getRepository().getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /** Clears the current hibernate session and sets a new one in the thread context */
  public void resetHibernateSession()
  {
    endHibernateSession();
    beginHibernateSession();
  }

  /**
   * @return the backing store
   */
  @Override
  public HibernateStore getStore()
  {
    return (HibernateStore)super.getStore();
  }

  /**
   * Starts a hibernate session and begins a transaction.
   *
   * @since 2.0
   */
  public void beginHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Creating hibernate session and transaction"); //$NON-NLS-1$
    }

    assert hibernateSession == null;
    final SessionFactory sessionFactory = getStore().getHibernateSessionFactory();
    hibernateSession = sessionFactory.openSession();
    hibernateSession.beginTransaction();
  }

  /**
   * Calls {@link #endHibernateSession()}, commits the transaction and closes the session.
   *
   * @since 2.0
   */
  public void commitRollbackHibernateSession()
  {
    endHibernateSession();
  }

  /**
   * Commits/rollbacks and closes the session
   *
   * @since 2.0
   */
  public void endHibernateSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Closing hibernate session"); //$NON-NLS-1$
    }

    if (hibernateSession != null && hibernateSession.isOpen())
    {
      try
      {
        if (hibernateSession.getTransaction().isActive())
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Commiting hibernate session"); //$NON-NLS-1$
          }

          if (isErrorOccured())
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace("Rolling back hb transaction"); //$NON-NLS-1$
            }

            hibernateSession.getTransaction().rollback();
          }
          else
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace("Committing hb transaction"); //$NON-NLS-1$
            }

            hibernateSession.getTransaction().commit();
          }
        }
      }
      finally
      {
        hibernateSession.close();
      }
    }

    hibernateSession = null;
  }

  /**
   * @return the current hibernate session. If there is none then a new one is created and a transaction is started
   */
  public Session getHibernateSession()
  {
    if (hibernateSession == null)
    {
      beginHibernateSession();
    }

    return hibernateSession;
  }

  /**
   * Closes/commits the current hibernate session if there is one, and starts a new one and begins a transaction.
   *
   * @return a newly created Hibernate Session
   */
  public Session getNewHibernateSession()
  {
    if (hibernateSession != null)
    {
      endHibernateSession();
    }

    if (hibernateSession != null)
    {
      throw new IllegalStateException("Hibernate session should be null"); //$NON-NLS-1$
    }

    beginHibernateSession();
    return hibernateSession;
  }

  /**
   * @return true if an error occured during database actions. Normally means that the transaction will be rolled back
   *         and not committed.
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

  /**
   * Note: the Hibernate store does not support the {@link StoreChunkReader} concept!.
   *
   * @return a {@link HibernateStoreChunkReader} (which throws UnsupportedOperationExceptions for most methods
   */
  public HibernateStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new HibernateStoreChunkReader(this, revision, feature);
  }

  /**
   * @return the current collection of package units.
   * @see HibernateStore
   * @see HibernatePackageHandler
   */
  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return getStore().getPackageHandler().getPackageUnits();
  }

  /**
   * Loads the package units from the database and returns the EPackage instances.
   *
   * @return the loaded EPackage instances.
   * @see HibernatePackageHandler
   */
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return getStore().getPackageHandler().loadPackageUnit(packageUnit);
  }

  /**
   * Reads the revision from the database. using the passed id.
   *
   * @param id
   *          identifies the CDORevision to read
   * @param branchPoint
   *          ignored until auditing is supported.
   * @param listChunk
   *          not used by Hibernate
   * @param cache
   *          the revision cache, the read revision is added to the cache
   * @return the read revision
   */
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    if (!HibernateUtil.getInstance().isStoreCreatedID(id))
    {
      return null;
    }

    final InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(id);
    if (revision == null)
    {
      final CDOClassifierRef classifierRef = CDOIDUtil.getClassifierRef(id);
      if (classifierRef == null)
      {
        throw new IllegalArgumentException("This CDOID type of " + id + " is not supported by this store."); //$NON-NLS-1$ //$NON-NLS-2$
      }

      final EClass eClass = HibernateUtil.getInstance().getEClass(classifierRef);
      return new DetachedCDORevision(eClass, id, branchPoint.getBranch(), 0, 0);
    }

    revision.setBranchPoint(getStore().getMainBranchHead());
    return revision;
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    // TODO: implement HibernateStoreAccessor.createBranch(branchID, branchInfo)
    throw new UnsupportedOperationException();
  }

  public BranchInfo loadBranch(int branchID)
  {
    // TODO: implement HibernateStoreAccessor.loadBranch(branchID)
    throw new UnsupportedOperationException();
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    // TODO: implement HibernateStoreAccessor.loadSubBranches(branchID)
    throw new UnsupportedOperationException();
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    // TODO: implement HibernateStoreAccessor.loadBranches(startID, endID, branchHandler)
    throw new UnsupportedOperationException();
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    // TODO: implement HibernateStoreAccessor.loadCommitInfos(branch, startTime, endTime, handler)
    // throw new UnsupportedOperationException();
  }

  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    // TODO: implement HibernateStoreAccessor.readChangeSet(segments)
    throw new UnsupportedOperationException();
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    if (eClass != null)
    {
      handleRevisionsByEClass(eClass, handler);
    }
    else
    {
      for (EPackage ePackage : getStore().getPackageHandler().getEPackages())
      {
        for (EClassifier eClassifier : ePackage.getEClassifiers())
        {
          if (eClassifier instanceof EClass)
          {
            final EClass eClazz = (EClass)eClassifier;
            try
            {
              getStore().getEntityName(eClazz);
            }
            catch (IllegalArgumentException ex)
            {
              // a non-mapped eclass
              continue;
            }
            handleRevisionsByEClass(eClazz, handler);
          }
        }
      }
    }
  }

  private void handleRevisionsByEClass(EClass eClass, CDORevisionHandler handler)
  {
    // get a transaction, the hibernateStoreAccessor is placed in a threadlocal
    // so all db access uses the same session.
    final Session session = getHibernateSession();

    // create the query
    final Query query = session.createQuery("select e from " + getStore().getEntityName(eClass) + " e");
    for (Object o : query.list())
    {
      handler.handleRevision((CDORevision)o);
    }
    session.clear();
  }

  /**
   * Not supported by the Hibernate Store, auditing is not supported. Currently ignores the branchVersion and calls the
   * {@readRevision(CDOID, CDOBranchPoint, int, CDORevisionCacheAdder)} .
   */
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    InternalCDORevision revision = readRevision(id, branchVersion.getBranch().getPoint(System.currentTimeMillis()),
        listChunk, cache);
    if (revision != null)
    {
      revision.freeze();
    }

    return revision;
  }

  /**
   * Queries for resources in a certain folder and returns them in the context object
   *
   * @param context
   *          the context provides input parameters (the folder) and is used to store the results of the query.
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    final CDOID folderID = getHibernateID(context.getFolderID());
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    final Session session = getHibernateSession();
    final Criteria criteria = session.createCriteria(EresourcePackage.eINSTANCE.getCDOResourceNode().getName());
    if (folderID == null)
    {
      criteria.add(org.hibernate.criterion.Restrictions.isNull(CDOHibernateConstants.CONTAINER_PROPERTY));
    }
    else
    {
      criteria.add(org.hibernate.criterion.Restrictions.eq(CDOHibernateConstants.CONTAINER_PROPERTY, folderID));
    }

    List<?> result = criteria.list();
    for (Object o : result)
    {
      final CDORevision revision = (CDORevision)o;
      final EStructuralFeature feature = revision.getEClass().getEStructuralFeature(NAME_EFEATURE_NAME);
      if (feature != null)
      {
        Object value = revision.data().get(feature, 0);
        if (value == CDORevisionData.NIL)
        {
          value = null;
        }

        final String revisionName = (String)value;
        final boolean match = exactMatch || revisionName == null || name == null ? ObjectUtil
            .equals(revisionName, name) : revisionName.startsWith(name);

        if (match && !context.addResource(HibernateUtil.getInstance().getCDOID(revision)))
        {
          // No more results allowed
          break;
        }
      }
    }
  }

  public void queryXRefs(QueryXRefsContext context)
  {
    final Session session = getHibernateSession();
    for (CDOID targetCdoId : context.getTargetObjects().keySet())
    {
      final CDORevision revision = HibernateUtil.getInstance().getCDORevision(targetCdoId);
      final EClass targetEClass = context.getTargetObjects().get(targetCdoId);
      final String targetEntityName = getStore().getEntityName(targetEClass);
      final Map<EClass, List<EReference>> sourceCandidates = context.getSourceCandidates();
      for (EClass sourceEClass : sourceCandidates.keySet())
      {
        final String sourceEntityName = getStore().getEntityName(sourceEClass);
        for (EReference eref : sourceCandidates.get(sourceEClass))
        {
          final String hql;
          if (eref.isMany())
          {
            hql = "select ref from " + sourceEntityName + " as ref, " + targetEntityName
                + " as refTo where refTo = :to and refTo in elements(ref." + eref.getName() + ")";
          }
          else
          {
            hql = "select ref from " + sourceEntityName + " as ref where :to = ref." + eref.getName();
          }

          final Query qry = session.createQuery(hql);
          qry.setEntity("to", revision);
          ScrollableResults result = qry.scroll(ScrollMode.FORWARD_ONLY);
          while (result.next())
          {
            final InternalCDORevision sourceRevision = (InternalCDORevision)result.get()[0];
            int sourceIndex = 0;
            if (eref.isMany())
            {
              // note this takes performance for sure as the list is read,
              // consider not supporting sourceIndex, or doing it differently
              final WrappedHibernateList cdoList = (WrappedHibernateList)sourceRevision.getList(eref);
              sourceIndex = cdoList.getDelegate().indexOf(revision);
            }

            boolean more = context.addXRef(targetCdoId, sourceRevision.getID(), eref, sourceIndex);
            if (!more)
            {
              return;
            }
          }
        }
      }
    }
  }

  private CDOID getHibernateID(CDOID id)
  {
    if (!CDOIDUtil.isNull(id))
    {
      if (HibernateUtil.getInstance().isStoreCreatedID(id))
      {
        return id;
      }

      // TODO Can this happen? When?
      // the folder id is always a long
      final Long idValue = CDOIDUtil.getLong(id);
      return CDOIDUtil.createLongWithClassifier(new CDOClassifierRef(EresourcePackage.eINSTANCE.getCDOResourceNode()),
          idValue);
    }

    return null;
  }

  /**
   * @param info
   *          the query information, is not used actively in this method.
   * @return a new instance of {@link HibernateQueryHandler}
   */
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    String queryLanguage = info.getQueryLanguage();
    if (StringUtil.equalsUpperOrLowerCase(queryLanguage, IHibernateStore.QUERY_LANGUAGE))
    {
      final HibernateQueryHandler queryHandler = new HibernateQueryHandler();
      queryHandler.setHibernateStoreAccessor(this);
      return queryHandler;
    }

    return null;
  }

  /**
   * Commits the session, see {@link #commitRollbackHibernateSession()}.
   *
   * @param monitor
   *          not used
   */
  @Override
  protected void doCommit(OMMonitor monitor)
  {
    commitRollbackHibernateSession();
    HibernateThreadContext.setCommitContext(null);
  }

  /**
   * Performs the main write and update actions. Persists new EPackages, updates changed objects, creates new ones and
   * removes deleted objects. Updates both container as well as resource associations.
   *
   * @param context
   *          the context contains the changed, new and to-be-removed objects
   * @param monitor
   *          not used by this method
   */
  @Override
  public void doWrite(InternalCommitContext context, OMMonitor monitor)
  {
    // NOTE: the same flow is also present in the super class (StoreAccessor)
    // changes in flow can mean that the flow here also has to change

    monitor.begin(3);
    HibernateThreadContext.setCommitContext(context);
    if (context.getNewPackageUnits().length > 0)
    {
      writePackageUnits(context.getNewPackageUnits(), monitor.fork());
    }

    // Note: instead of an Async here, we could do much more fine-grained monitoring below. But this
    // simplistic solution is sufficient to prevent timeout errors.
    final Async async = monitor.forkAsync();
    try
    {
      // start with fresh hibernate session to prevent side effects
      final Session session = getNewHibernateSession();
      session.setFlushMode(FlushMode.MANUAL);

      // order is 1) insert, 2) update and then delete
      // this order is the most stable! Do not change it without testing

      final List<InternalCDORevision> repairContainerIDs = new ArrayList<InternalCDORevision>();
      final List<InternalCDORevision> repairResourceIDs = new ArrayList<InternalCDORevision>();
      for (InternalCDORevision revision : context.getNewObjects())
      {
        // keep track for which cdoRevisions the container id needs to be repaired afterwards
        final CDOID containerID = (CDOID)revision.getContainerID();
        if (containerID instanceof CDOIDTemp && !containerID.isNull())
        {
          repairContainerIDs.add(revision);
        }

        final CDOID resourceID = revision.getResourceID();
        if (resourceID instanceof CDOIDTemp && !resourceID.isNull())
        {
          repairResourceIDs.add(revision);
        }

        final String entityName = getStore().getEntityName(revision.getEClass());
        session.saveOrUpdate(entityName, revision);
      }

      session.flush();

      for (CDORevision revision : context.getDirtyObjects())
      {
        final String entityName = HibernateUtil.getInstance().getEntityName(revision.getID());
        session.merge(entityName, revision);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Updated Object " + revision.getEClass().getName() + " id: " + revision.getID()); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      session.flush();

      // delete all objects
      for (CDOID id : context.getDetachedObjects())
      {
        try
        {
          final CDORevision revision = HibernateUtil.getInstance().getCDORevision(id);

          // maybe deleted in parallell?
          if (revision != null)
          {
            session.delete(revision);
          }
        }
        catch (org.hibernate.ObjectNotFoundException ex)
        {
          // ignore these, an object can be removed through cascade deletes
        }
      }

      session.flush();

      // now do an update of the container without incrementing the version
      repairContainerIDs(repairContainerIDs, session);
      repairResourceIDs(repairResourceIDs, session);

      session.flush();

      // write the blobs
      ExtendedDataInputStream in = context.getLobs();
      if (in != null)
      {
        try
        {
          int count = in.readInt();
          for (int i = 0; i < count; i++)
          {
            byte[] id = in.readByteArray();
            long size = in.readLong();
            if (size > 0)
            {
              writeBlob(id, size, in);
            }
            else
            {
              writeClob(id, -size, new InputStreamReader(in));
            }
          }
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      session.flush();

    }
    catch (Exception e)
    {
      OM.LOG.error(e);
      throw WrappedException.wrap(e);
    }
    finally
    {
      async.stop();
    }

    context.applyIDMappings(monitor.fork());
    monitor.done();
  }

  private void repairContainerIDs(List<InternalCDORevision> repairContainerIDs, Session session)
  {
    for (InternalCDORevision revision : repairContainerIDs)
    {
      final CDORevision container = HibernateUtil.getInstance().getCDORevision((CDOID)revision.getContainerID());
      final String entityName = getStore().getEntityName(revision.getEClass());
      final CDOID id = revision.getID();
      final String hqlUpdate = "update " + entityName + " set " + CDOHibernateConstants.CONTAINER_PROPERTY //$NON-NLS-1$  //$NON-NLS-2$
          + " = :containerInfo where " + getStore().getIdentifierPropertyName(entityName) + " = :id"; //$NON-NLS-1$ //$NON-NLS-2$
      final Query qry = session.createQuery(hqlUpdate);
      qry.setParameter("containerInfo", ContainerInfoConverter.getInstance().convertContainerRelationToString(revision, //$NON-NLS-1$
          container.getID()));
      qry.setParameter("id", HibernateUtil.getInstance().getIdValue(id)); //$NON-NLS-1$
      if (qry.executeUpdate() != 1)
      {
        //        OM.LOG.error("Not able to update container columns of " + entityName + " with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
        throw new IllegalStateException("Not able to update container columns of " + entityName + " with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  private void repairResourceIDs(List<InternalCDORevision> repairResourceIDs, Session session)
  {
    for (InternalCDORevision revision : repairResourceIDs)
    {
      final CDORevision resource = HibernateUtil.getInstance().getCDORevision(revision.getResourceID());
      final String entityName = getStore().getEntityName(revision.getEClass());
      final CDOID id = revision.getID();
      final String hqlUpdate = "update " + entityName + " set " + CDOHibernateConstants.RESOURCE_PROPERTY //$NON-NLS-1$ //$NON-NLS-2$
          + " = :resourceInfo where " + getStore().getIdentifierPropertyName(entityName) + " = :id"; //$NON-NLS-1$ //$NON-NLS-2$
      final Query qry = session.createQuery(hqlUpdate);
      qry.setParameter("resourceInfo", resource.getID()); //$NON-NLS-1$
      qry.setParameter("id", HibernateUtil.getInstance().getIdValue(id)); //$NON-NLS-1$
      if (qry.executeUpdate() != 1)
      {
        //        OM.LOG.error("Not able to update resource ids of " + entityName + " with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
        throw new IllegalStateException("Not able to update resource ids of " + entityName + " with id " + id); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    // handled by the write method
  }

  @Override
  protected void doRollback(CommitContext context)
  {
    setErrorOccured(true);
    endHibernateSession();
    HibernateThreadContext.setCommitContext(null);
  }

  /**
   * Writes package units to the datbaase.
   *
   * @param packageUnits
   *          the package units to write to the database
   * @param monitor
   *          not used by the store
   * @see HibernatePackageHandler
   */
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    if (packageUnits != null && packageUnits.length != 0)
    {
      getStore().getPackageHandler().writePackageUnits(packageUnits);
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    // Doesn't do anything. It is done in commit().
  }

  @Override
  public void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  public CDOID getNextCDOID(CDORevision revision)
  {
    // Never called
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    // TODO: implement HibernateStoreAccessor.writeRevisionDeltas(revisionDeltas, branch, created, monitor)
    throw new UnsupportedOperationException();
  }

  public void queryLobs(List<byte[]> ids)
  {
    for (Iterator<byte[]> it = ids.iterator(); it.hasNext();)
    {
      byte[] id = it.next();
      final HibernateStoreLob lob = getCreateHibernateStoreLob(id);
      if (lob.isNew())
      {
        it.remove();
      }
    }
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    final Session session = getHibernateSession();
    final Query qry = session.createQuery("select c from " + HibernateStoreLob.class.getName() + " as c");

    try
    {
      for (Object o : qry.list())
      {
        final HibernateStoreLob lob = (HibernateStoreLob)o;
        if (lob.getBlob() != null)
        {
          final OutputStream out = handler.handleBlob(HexUtil.hexToBytes(lob.getId()), lob.getSize());
          if (out != null)
          {
            final InputStream in = lob.getBlob().getBinaryStream();
            try
            {
              IOUtil.copyBinary(in, out, lob.getSize());
            }
            finally
            {
              IOUtil.close(out);
            }
          }
        }
        else
        {
          final Clob clob = lob.getClob();
          Reader in = clob.getCharacterStream();
          Writer out = handler.handleClob(HexUtil.hexToBytes(lob.getId()), lob.getSize());
          if (out != null)
          {
            try
            {
              IOUtil.copyCharacter(in, out, lob.getSize());
            }
            finally
            {
              IOUtil.close(out);
            }
          }
        }
      }
    }
    catch (SQLException ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    final HibernateStoreLob lob = getCreateHibernateStoreLob(id);
    // can this ever occur?
    // TODO: how should non-existence be handled? Currently results in a timeout
    // on the client.
    if (lob.isNew())
    {
      throw new IllegalStateException("Lob with id " + HexUtil.bytesToHex(id) + " does not exist");
    }

    final long size = lob.getSize();
    try
    {
      if (lob.getBlob() != null)
      {
        InputStream in = lob.getBlob().getBinaryStream();
        IOUtil.copyBinary(in, out, size);
      }
      else
      {
        Clob clob = lob.getClob();
        Reader in = clob.getCharacterStream();
        IOUtil.copyCharacter(in, new OutputStreamWriter(out), size);
      }
    }
    catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    final HibernateStoreLob lob = getCreateHibernateStoreLob(id);
    if ((inputStream == null || size == 0) && !lob.isNew())
    {
      getHibernateSession().delete(lob);
    }
    else
    {
      // deprecated usage, non-deprecated api uses a session
      // TODO: research which session to use
      lob.setBlob(Hibernate.createBlob(inputStream, (int)size));
      lob.setSize((int)size);
      lob.setClob(null);
      getHibernateSession().saveOrUpdate(lob);
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    final HibernateStoreLob lob = getCreateHibernateStoreLob(id);
    if ((reader == null || size == 0) && !lob.isNew())
    {
      getHibernateSession().delete(lob);
    }
    else
    {
      // deprecated usage, non-deprecated api uses a session
      // TODO: research which session to use
      lob.setClob(Hibernate.createClob(reader, (int)size));
      lob.setSize((int)size);
      lob.setBlob(null);
      getHibernateSession().saveOrUpdate(lob);
    }
  }

  private HibernateStoreLob getCreateHibernateStoreLob(byte[] idBytes)
  {
    final String id = HexUtil.bytesToHex(idBytes);
    final Session session = getHibernateSession();
    HibernateStoreLob lob = (HibernateStoreLob)session.get(HibernateStoreLob.class, id);
    if (lob == null)
    {
      lob = new HibernateStoreLob();
      lob.setId(id);
    }

    return lob;
  }

  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void rawStore(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // TODO: implement HibernateStoreAccessor.rawStore(packageUnits, monitor)
    throw new UnsupportedOperationException("Needed for server import!");
  }

  public void rawStore(InternalCDORevision revision, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public void rawStore(byte[] id, long size, InputStream inputStream) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void rawStore(byte[] id, long size, Reader reader) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment,
      OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public void rawCommit(double commitWork, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public void rawDelete(CDOID id, int version, CDOBranch branch, EClass eClass, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    // TODO This method is called when this accessor is not needed anymore
    if (TRACER.isEnabled())
    {
      TRACER.trace("Committing/rollback and closing hibernate session"); //$NON-NLS-1$
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

  private void clearThreadState()
  {
    PersistableListHolder.getInstance().clearListMapping();
    HibernateThreadContext.setCommitContext(null);
  }

  @Override
  protected void doActivate() throws Exception
  {
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
  }
}
