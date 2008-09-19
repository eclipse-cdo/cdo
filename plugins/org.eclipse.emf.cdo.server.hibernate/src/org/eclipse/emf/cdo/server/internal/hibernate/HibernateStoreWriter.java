/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - added hibernate specific implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreWriter;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class HibernateStoreWriter extends HibernateStoreReader implements IHibernateStoreWriter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStoreWriter.class);

  public HibernateStoreWriter(HibernateStore store, IView view)
  {
    super(store, view);
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
          if (!containerID.isNull() && containerID instanceof CDOIDTemp)
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
            + " set _contID_Entity = :contEntity, _contID_ID=:contID, _contID_class=:contClass where e_id = :id";
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
  protected void detachObjects(CDOID[] detachedObjects)
  {
    // TODO: implement HibernateStoreWriter.detachObjects(detachedObjects)
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReader()
  {
    // TODO Is this necessary?
    return false;
  }

  @Override
  public void rollback(CommitContext context)
  {
    // Don't do anything as the real action is done at commit (which does not happen now)
    if (TRACER.isEnabled())
    {
      TRACER.trace("Rollbacked called");
    }
  }

  @Override
  protected void writePackages(CDOPackage... cdoPackages)
  {
    if (cdoPackages != null && cdoPackages.length != 0)
    {
      getStore().getPackageHandler().writePackages(cdoPackages);
    }

    // Set a new hibernatesession in the thread
    resetHibernateSession();
  }

  @Override
  protected void writePackage(CDOPackage cdoPackage)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  @Override
  protected void writeClass(InternalCDOClass cdoClass)
  {
    throw new UnsupportedOperationException("Should not be called, should be handled by hibernate cascade");
  }

  @Override
  protected void writeFeature(CDOFeature feature)
  {
    throw new UnsupportedOperationException("Should not be called, should be handled by hibernate cascade");
  }

  @Override
  protected void writeSuperType(InternalCDOClass type, CDOClassProxy superType)
  {
    throw new UnsupportedOperationException("Should not be called, should be handled by hibernate cascade");
  }
}
