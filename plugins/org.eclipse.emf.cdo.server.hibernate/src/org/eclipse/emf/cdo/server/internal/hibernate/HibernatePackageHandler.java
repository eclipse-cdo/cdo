/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal  - moved code from HibernateStore to this class
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContextImpl;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOFeature;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackage;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Expression;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Delegate which stores and retrieves cdo packages.
 * <p>
 * TODO extend {@link Lifecycle}. See {@link #doActivate()} and {@link #doDeactivate()}.
 * 
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernatePackageHandler extends Lifecycle
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernatePackageHandler.class);

  private Configuration configuration;

  private SessionFactory sessionFactory;

  private int nextPackageID;

  private int nextClassID;

  private int nextFeatureID;

  private Collection<CDOPackageInfo> cdoPackageInfos = null;

  private HibernateStore hibernateStore;

  /**
   * TODO Necessary to pass/store/dump the properties from the store?
   */
  public HibernatePackageHandler(HibernateStore store)
  {
    hibernateStore = store;
  }

  public List<CDOPackage> getCDOPackages()
  {
    List<CDOPackage> cdoPackages = new ArrayList<CDOPackage>();
    if (HibernateThreadContext.isHibernateCommitContextSet())
    {
      CommitContext cc = HibernateThreadContext.getHibernateCommitContext().getCommitContext();
      if (cc instanceof TransactionCommitContextImpl)
      {
        TransactionCommitContextImpl tx = (TransactionCommitContextImpl)cc;
        for (CDOPackage cdoPackage : tx.getNewPackages())
        {
          cdoPackages.add(cdoPackage);
        }
      }
    }

    for (CDOPackage cdoPackage : hibernateStore.getRepository().getPackageManager().getPackages())
    {
      cdoPackages.add(cdoPackage);
    }

    for (CDOPackage cdoPackage : cdoPackages)
    {
      // force resolve
      if (cdoPackage.getClassCount() == 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Returning " + cdoPackage.getPackageURI());
        }
      }
    }

    return cdoPackages;
  }

  public void writePackages(CDOPackage... cdoPackages)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Persisting new CDOPackages");
    }

    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    boolean err = true;
    boolean updated = false;

    try
    {
      for (CDOPackage cdoPackage : cdoPackages)
      {
        if (cdoPackageExistsAndIsUnchanged(cdoPackage))
        {
          OM.LOG.warn("CDOPackage " + cdoPackage.getPackageURI() + " already exists not persisting it again!");
          continue;
        }

        if (TRACER.isEnabled())
        {
          TRACER.trace("Persisting CDOPackage " + cdoPackage.getPackageURI());
        }

        session.saveOrUpdate(cdoPackage);
        updated = true;
      }

      tx.commit();
      err = false;
    }
    finally
    {
      if (err)
      {
        tx.rollback();
      }

      session.close();
    }

    if (updated)
    {
      reset();
      hibernateStore.reInitialize();
    }
  }

  protected boolean cdoPackageExistsAndIsUnchanged(CDOPackage newCDOPackage)
  {
    CDOPackage[] cdoPackages = hibernateStore.getRepository().getPackageManager().getPackages();
    for (CDOPackage cdoPackage : cdoPackages)
    {
      if (cdoPackage.getClassCount() > 0 && cdoPackage.getPackageURI().equals(newCDOPackage.getPackageURI()))
      {
        String ecore = cdoPackage.getEcore();
        String newEcore = newCDOPackage.getEcore();
        return ObjectUtil.equals(ecore, newEcore);
      }
    }

    return false;
  }

  public void writePackage(CDOPackage cdoPackage)
  {
    if (cdoPackageExistsAndIsUnchanged(cdoPackage))
    {
      OM.LOG.warn("CDOPackage " + cdoPackage.getPackageURI() + " already exists not persisting it again!");
      return;
    }

    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    boolean err = true;
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Persisting CDOPackage " + cdoPackage.getPackageURI());
      }

      session.saveOrUpdate(cdoPackage);
      tx.commit();
      err = false;
    }
    finally
    {
      if (err)
      {
        tx.rollback();
      }

      session.close();
    }

    reset();
    hibernateStore.reInitialize();
  }

  public Collection<CDOPackageInfo> getCDOPackageInfos()
  {
    readPackageInfos();
    return cdoPackageInfos;
  }

  protected void readPackage(CDOPackage cdoPackage)
  {
    if (cdoPackage.getClassCount() > 0)
    { // already initialized go away
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Reading CDOPackage with uri " + cdoPackage.getPackageURI() + " from db");
    }

    Session session = getSessionFactory().openSession();

    try
    {
      Criteria criteria = session.createCriteria(CDOPackage.class);
      criteria.add(Expression.eq("packageURI", cdoPackage.getPackageURI()));
      List<?> list = criteria.list();
      if (list.size() != 1)
      {
        throw new IllegalArgumentException("CDOPackage with uri " + cdoPackage.getPackageURI()
            + " not present in the db");
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Found " + list.size() + " CDOPackages in DB");
      }

      CDOPackage dbPackage = (CDOPackage)list.get(0);
      if (TRACER.isEnabled())
      {
        TRACER.trace("Read CDOPackage: " + cdoPackage.getName());
      }

      ((InternalCDOPackage)cdoPackage).setServerInfo(dbPackage.getServerInfo());
      ((InternalCDOPackage)cdoPackage).setName(dbPackage.getName());
      ((InternalCDOPackage)cdoPackage).setEcore(dbPackage.getEcore());
      ((InternalCDOPackage)cdoPackage).setMetaIDRange(cdoPackage.getMetaIDRange());

      final List<CDOClass> cdoClasses = new ArrayList<CDOClass>();
      for (CDOClass cdoClass : dbPackage.getClasses())
      {
        cdoClasses.add(cdoClass);
        for (CDOClassProxy proxy : ((InternalCDOClass)cdoClass).getSuperTypeProxies())
        {
          proxy.setCDOPackageManager(hibernateStore.getRepository().getPackageManager());
        }

        for (CDOFeature cdoFeature : cdoClass.getFeatures())
        {
          final InternalCDOFeature internalFeature = (InternalCDOFeature)cdoFeature;
          internalFeature.setContainingClass(cdoClass);
          if (internalFeature.getReferenceTypeProxy() != null)
          {
            internalFeature.getReferenceTypeProxy().setCDOPackageManager(
                hibernateStore.getRepository().getPackageManager());
          }
        }

        // // force indices to be set
        // if (cdoClass.getAllFeatures().length > 0)
        // {
        // ((InternalCDOClass)cdoClass).getFeatureIndex(0);
        // }
      }

      ((InternalCDOPackage)cdoPackage).setClasses(cdoClasses);
    }
    finally
    {
      session.close();
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Finished reading CDOPackages");
    }
  }

  protected void readPackageInfos()
  {
    if (cdoPackageInfos == null || cdoPackageInfos.size() == 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Reading CDOPackages from db");
      }

      Collection<CDOPackageInfo> result = new ArrayList<CDOPackageInfo>();
      Session session = getSessionFactory().openSession();

      try
      {
        Criteria criteria = session.createCriteria(CDOPackage.class);
        List<?> list = criteria.list();
        if (TRACER.isEnabled())
        {
          TRACER.trace("Found " + list.size() + " CDOPackages in DB");
        }

        for (Object object : list)
        {
          CDOPackage cdoPackage = (CDOPackage)object;
          if (TRACER.isEnabled())
          {
            TRACER.trace("Read CDOPackage: " + cdoPackage.getName());
          }

          result.add(new CDOPackageInfo(cdoPackage.getPackageURI(), cdoPackage.isDynamic(),
              cdoPackage.getMetaIDRange(), cdoPackage.getParentURI()));
          ((InternalCDOPackage)cdoPackage).setPackageManager(hibernateStore.getRepository().getPackageManager());
        }

        cdoPackageInfos = result;
      }
      finally
      {
        session.close();
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Finished reading CDOPackages");
    }
  }

  void doDropSchema()
  {
    final SchemaExport se = new SchemaExport(configuration);
    se.drop(false, true);
  }

  public synchronized SessionFactory getSessionFactory()
  {
    if (sessionFactory == null)
    {
      sessionFactory = configuration.buildSessionFactory();
    }

    return sessionFactory;
  }

  public synchronized int getNextPackageID()
  {
    return nextPackageID++;
  }

  public synchronized int getNextClassID()
  {
    return nextClassID++;
  }

  public synchronized int getNextFeatureID()
  {
    return nextFeatureID++;
  }

  public void reset()
  {
    cdoPackageInfos = null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    initConfiguration();
    initSchema();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (sessionFactory != null)
    {
      sessionFactory.close();
      sessionFactory = null;
    }

    super.doDeactivate();
  }

  protected void initConfiguration()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Initializing configuration for CDO metadata");
    }

    InputStream in = null;

    try
    {
      in = OM.BUNDLE.getInputStream("mappings/meta.hbm.xml");
      configuration = new Configuration();
      configuration.addInputStream(in);
      configuration.setProperties(HibernateUtil.getInstance().getPropertiesFromStore(hibernateStore));
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  protected void initSchema()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Updating db schema for Hibernate PackageHandler");
    }

    new SchemaUpdate(configuration).execute(true, true);
  }
}
