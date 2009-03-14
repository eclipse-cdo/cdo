/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal  - moved code from HibernateStore to this class
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

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

  private Collection<InternalCDOPackageUnit> packageUnits = null;

  private HibernateStore hibernateStore;

  /**
   * TODO Necessary to pass/store/dump the properties from the store?
   */
  public HibernatePackageHandler(HibernateStore store)
  {
    hibernateStore = store;
  }

  public List<EPackage> getEPackages()
  {
    List<EPackage> ePackages = new ArrayList<EPackage>();
    if (HibernateThreadContext.isHibernateCommitContextSet())
    {
      IStoreAccessor.CommitContext cc = HibernateThreadContext.getHibernateCommitContext().getCommitContext();
      if (cc != null)
      {
        for (InternalCDOPackageUnit packageUnit : cc.getNewPackageUnits())
        {
          for (EPackage ePackage : packageUnit.getEPackages(true))
          {
            ePackages.add(ePackage);
          }
        }
      }
    }

    for (EPackage ePackage : getPackageRegistry().getEPackages())
    {
      ePackages.add(ePackage);
    }

    XXX(); // XXX Still needed?
    // for (EPackage ePackage : ePackages)
    // {
    // // force resolve
    // if (ePackage.getClassCount() == 0)
    // {
    // if (TRACER.isEnabled())
    // {
    // TRACER.trace("Returning " + ePackage.getNsURI());
    // }
    // }
    // }

    return ePackages;
  }

  private InternalCDOPackageRegistry getPackageRegistry()
  {
    return (InternalCDOPackageRegistry)hibernateStore.getRepository().getPackageRegistry();
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Persisting new EPackages");
    }

    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    boolean err = true;
    boolean updated = false;

    try
    {
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        XXX(); // XXX Really on a per EPackage base?
        for (EPackage ePackage : packageUnit.getEPackages(true))
        {
          if (ePackageExistsAndIsUnchanged(ePackage))
          {
            OM.LOG.warn("EPackage " + ePackage.getNsURI() + " already exists not persisting it again!");
            continue;
          }

          if (TRACER.isEnabled())
          {
            TRACER.trace("Persisting EPackage " + ePackage.getNsURI());
          }

          session.saveOrUpdate(ePackage);
          updated = true;
        }
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

  protected boolean ePackageExistsAndIsUnchanged(EPackage newEPackage)
  {
    for (EPackage ePackage : getPackageRegistry().getEPackages())
    {
      if (ePackage.getClassCount() > 0 && ePackage.getNsURI().equals(newEPackage.getNsURI()))
      {
        String ecore = ePackage.getEcore();
        String newEcore = newEPackage.getEcore();
        return ObjectUtil.equals(ecore, newEcore);
      }
    }

    return false;
  }

  public void writePackage(EPackage ePackage)
  {
    XXX(); // XXX Is this method needed?

    if (ePackageExistsAndIsUnchanged(ePackage))
    {
      OM.LOG.warn("EPackage " + ePackage.getNsURI() + " already exists not persisting it again!");
      return;
    }

    Session session = getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    boolean err = true;
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Persisting EPackage " + ePackage.getNsURI());
      }

      session.saveOrUpdate(ePackage);
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

  public Collection<InternalCDOPackageUnit> getPackageUnits()
  {
    readPackageUnits();
    return packageUnits;
  }

  // protected void readPackage(EPackage ePackage)
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    if (ePackage.getClassCount() > 0)
    { // already initialized go away
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Reading EPackage with uri " + ePackage.getNsURI() + " from db");
    }

    Session session = getSessionFactory().openSession();

    try
    {
      Criteria criteria = session.createCriteria(EPackage.class);
      criteria.add(Expression.eq("packageURI", ePackage.getNsURI()));
      List<?> list = criteria.list();
      if (list.size() != 1)
      {
        throw new IllegalArgumentException("EPackage with uri " + ePackage.getNsURI() + " not present in the db");
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Found " + list.size() + " EPackages in DB");
      }

      EPackage dbPackage = (EPackage)list.get(0);
      if (TRACER.isEnabled())
      {
        TRACER.trace("Read EPackage: " + ePackage.getName());
      }

      ((InternalEPackage)ePackage).setServerInfo(dbPackage.getServerInfo());
      ((InternalEPackage)ePackage).setName(dbPackage.getName());
      ((InternalEPackage)ePackage).setEcore(dbPackage.getEcore());
      ((InternalEPackage)ePackage).setMetaIDRange(ePackage.getMetaIDRange());

      final List<EClass> eClasses = new ArrayList<EClass>();
      for (EClass eClass : EMFUtil.getPersistentClasses(dbPackage))
      {
        eClasses.add(eClass);
        for (EClassProxy proxy : ((InternalEClass)eClass).getSuperTypeProxies())
        {
          proxy.setCDOPackageManager(hibernateStore.getRepository().getPackageRegistry());
        }

        for (EStructuralFeature feature : eClass.getFeatures())
        {
          final InternalCDOFeature internalFeature = (InternalCDOFeature)feature;
          internalFeature.setContainingClass(eClass);
          if (internalFeature.getReferenceTypeProxy() != null)
          {
            internalFeature.getReferenceTypeProxy().setCDOPackageManager(
                hibernateStore.getRepository().getPackageRegistry());
          }
        }

        // // force indices to be set
        // if (TODO.getAllPersistentFeatures(eClass).length > 0)
        // {
        // ((InternalEClass)eClass).getFeatureIndex(0);
        // }
      }

      ((InternalEPackage)ePackage).setClasses(eClasses);
    }
    finally
    {
      session.close();
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Finished reading EPackages");
    }
  }

  protected void readPackageUnits()
  {
    if (packageUnits == null || packageUnits.size() == 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Reading EPackages from db");
      }

      Collection<InternalCDOPackageUnit> result = new ArrayList<InternalCDOPackageUnit>();
      Session session = getSessionFactory().openSession();

      try
      {
        Criteria criteria = session.createCriteria(EPackage.class);
        List<?> list = criteria.list();
        if (TRACER.isEnabled())
        {
          TRACER.trace("Found " + list.size() + " EPackages in DB");
        }

        for (Object object : list)
        {
          EPackage ePackage = (EPackage)object;
          if (TRACER.isEnabled())
          {
            TRACER.trace("Read EPackage: " + ePackage.getName());
          }

          result.add(new CDOPackageInfo(ePackage.getNsURI(), ePackage.getParentURI(), ePackage.isDynamic(), ePackage
              .getMetaIDRange()));
          ((InternalEPackage)ePackage).setPackageManager(hibernateStore.getRepository().getPackageRegistry());
        }

        packageUnits = result;
      }
      finally
      {
        session.close();
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Finished reading EPackages");
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
    packageUnits = null;
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
