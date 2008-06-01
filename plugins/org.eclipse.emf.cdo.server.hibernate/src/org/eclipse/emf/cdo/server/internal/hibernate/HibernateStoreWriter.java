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

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassProxy;
import org.eclipse.emf.cdo.internal.protocol.model.InternalCDOClass;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreWriter;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.WrappedException;

import org.hibernate.FlushMode;
import org.hibernate.Session;

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
  public void commit(CommitContext context)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Committing transaction");
    }

    HibernateThreadContext.setCommitContext(context);
    writePackages(context.getNewPackages());

    try
    {
      // start with fresh hibernate session
      Session session = getHibernateSession();
      session.setFlushMode(FlushMode.COMMIT);
      session.beginTransaction();
      for (Object o : context.getNewObjects())
      {
        CDORevision cdoRevision = (CDORevision)o;
        session.save(HibernateUtil.getInstance().getEntityName(cdoRevision), o);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Persisted new Object " + ((CDORevision)o).getCDOClass().getName() + " id: "
              + cdoRevision.getID());
        }
      }

      for (Object o : context.getDirtyObjects())
      {
        try
        {
          CDORevision cdoRevision = (CDORevision)o;
          if (cdoRevision instanceof CDORevisionImpl)
          {
            ((CDORevisionImpl)cdoRevision).setVersion(cdoRevision.getVersion() - 1);
          }

          session.update(HibernateUtil.getInstance().getEntityName(cdoRevision), o);
          if (TRACER.isEnabled())
          {
            TRACER.trace("Updated Object " + ((CDORevision)o).getCDOClass().getName() + " id: " + cdoRevision.getID());
          }
        }
        catch (Exception e)
        {
          OM.LOG.error(e);
          throw WrappedException.wrap(e);
        }
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Commit hibernate transaction");
      }

      session.getTransaction().commit();
    }
    finally
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Clearing used hibernate session");
      }

      HibernateThreadContext.setCommitContext(null);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Applying id mappings");
    }

    context.applyIDMappings();
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
  protected void writeRevision(CDORevision revision)
  {
    // Do nothing, do it all at commit
  }

  @Override
  protected void writeRevisionDelta(CDORevisionDelta revisionDelta)
  {
  }

  @Override
  protected void writeRevisionDeltas(CDORevisionDelta[] revisionDeltas)
  {
  }

  @Override
  protected void writeRevisions(CDORevision[] revisions)
  {
    // Don't do anything it is done at commit
  }

  @Override
  protected void writeSuperType(InternalCDOClass type, CDOClassProxy superType)
  {
    throw new UnsupportedOperationException("Should not be called, should be handled by hibernate cascade");
  }
}
