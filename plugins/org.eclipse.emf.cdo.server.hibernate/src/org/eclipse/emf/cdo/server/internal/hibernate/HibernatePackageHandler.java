/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.server.XRefsQueryHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private static final String CDO_PACKAGE_UNIT_ENTITY_NAME = "CDOPackageUnit"; //$NON-NLS-1$

  private static final String META_HBM_PATH = "mappings/meta.hbm.xml"; //$NON-NLS-1$

  private static final String HBM2DLL_UPDATE = "update"; //$NON-NLS-1$

  private static final String HBM2DLL_CREATE = "create"; //$NON-NLS-1$

  // made static and synchronized because apparently there can be multiple package handlers
  // in some test cases: TestExternalReferenceTest.testOneXMIResourceManyViewsOnOneResourceSet
  private static synchronized boolean writePackageUnits(InternalCDOPackageUnit[] packageUnits,
      SessionFactory sessionFactory, EPackage.Registry registry)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Persisting new EPackages"); //$NON-NLS-1$
    }

    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    boolean err = true;
    boolean updated = false;

    try
    {
      // first store and update the packageunits and the epackages
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        final HibernateCDOPackageUnitDTO hbPackageUnitDTO = new HibernateCDOPackageUnitDTO(packageUnit);

        if (packageUnit.getPackageInfos().length > 0)
        {
          final String rootNSUri = packageUnit.getTopLevelPackageInfo().getPackageURI();
          final EPackage rootEPackage = registry.getEPackage(rootNSUri);
          hbPackageUnitDTO.setEPackageByteArray(EMFUtil.getEPackageBytes(rootEPackage, true, registry));
        }

        if (session.get(CDO_PACKAGE_UNIT_ENTITY_NAME, hbPackageUnitDTO.getNsUri()) == null)
        {
          session.saveOrUpdate(CDO_PACKAGE_UNIT_ENTITY_NAME, hbPackageUnitDTO);
        }

        updated = true;
      }

      tx.commit();
      err = false;
    }
    catch (Exception e)
    {
      e.printStackTrace(System.err);
      throw WrappedException.wrap(e);
    }
    finally
    {
      if (err)
      {
        tx.rollback();
      }

      session.close();
    }

    return updated;
  }

  private Configuration configuration;

  private SessionFactory sessionFactory;

  private int nextPackageID;

  private int nextClassID;

  private int nextFeatureID;

  private Collection<InternalCDOPackageUnit> packageUnits;

  private Map<String, byte[]> ePackageBlobsByRootUri = new HashMap<String, byte[]>();

  private Map<String, EPackage[]> ePackagesByRootUri = new HashMap<String, EPackage[]>();

  private HibernateStore hibernateStore;

  private boolean doDropSchema;

  private Map<EClass, Map<EClass, List<EReference>>> sourceCandidates = new HashMap<EClass, Map<EClass, List<EReference>>>();

  /**
   * TODO Necessary to pass/store/dump the properties from the store?
   */
  public HibernatePackageHandler(HibernateStore store)
  {
    hibernateStore = store;
  }

  /**
   * @return the full list of EPackages registered in the PackageRegistry of the commit context as well as the EPackages
   *         registered earlier.
   * @see CommitContext#getPackageRegistry()
   * @see InternalRepository#getPackageRegistry()
   */
  public List<EPackage> getEPackages()
  {
    List<EPackage> ePackages = new ArrayList<EPackage>();
    final InternalRepository localRepository = hibernateStore.getRepository();

    for (EPackage ePackage : localRepository.getPackageRegistry(false).getEPackages())
    {
      ePackages.add(ePackage);
    }

    for (EPackage ePackage : localRepository.getPackageRegistry(true).getEPackages())
    {
      boolean alreadyPresent = false;
      for (EPackage ePackagePresent : ePackages)
      {
        if (ePackagePresent.getNsURI().equals(ePackage.getNsURI()))
        {
          alreadyPresent = true;
          break;
        }
      }

      if (!alreadyPresent)
      {
        ePackages.add(ePackage);
      }
    }

    return ePackages;
  }

  private InternalCDOPackageRegistry getPackageRegistry()
  {
    return hibernateStore.getRepository().getPackageRegistry();
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits)
  {
    final boolean updated = writePackageUnits(packageUnits, getSessionFactory(), getPackageRegistry());
    if (updated)
    {
      reset();
      hibernateStore.reInitialize();
      sourceCandidates = null;
    }
  }

  public Collection<InternalCDOPackageUnit> getPackageUnits()
  {
    readPackageUnits();
    return packageUnits;
  }

  public Map<EClass, List<EReference>> getSourceCandidates(EClass targetEClass)
  {
    if (sourceCandidates == null)
    {
      computeSourceCandidates();
    }

    final Map<EClass, List<EReference>> sourceCandidateList = sourceCandidates.get(targetEClass);
    if (sourceCandidateList == null)
    {
      return new HashMap<EClass, List<EReference>>();
    }

    return sourceCandidateList;
  }

  private synchronized void computeSourceCandidates()
  {
    if (sourceCandidates != null)
    {
      return;
    }

    sourceCandidates = new HashMap<EClass, Map<EClass, List<EReference>>>();

    for (EPackage ePackage : getEPackages())
    {
      for (EClassifier eClassifier : ePackage.getEClassifiers())
      {
        if (eClassifier instanceof EClass)
        {
          sourceCandidates.put((EClass)eClassifier, computeSourceCandidatesByEClass((EClass)eClassifier));
        }
      }
    }
  }

  private Map<EClass, List<EReference>> computeSourceCandidatesByEClass(EClass targetType)
  {
    final Map<EClass, List<EReference>> localSourceCandidates = new HashMap<EClass, List<EReference>>();
    final Collection<EClass> targetTypes = Collections.singletonList(targetType);
    for (CDOPackageInfo packageInfo : hibernateStore.getRepository().getPackageRegistry(false).getPackageInfos())
    {
      XRefsQueryHandler.collectSourceCandidates(packageInfo, targetTypes, localSourceCandidates);
    }

    return localSourceCandidates;
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    final String nsUri = packageUnit.getTopLevelPackageInfo().getPackageURI();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Reading EPackages with root uri " + nsUri + " from db"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    EPackage[] epacks = ePackagesByRootUri.get(nsUri);
    if (epacks == null)
    {
      final byte[] ePackageBlob = ePackageBlobsByRootUri.get(nsUri);
      if (ePackageBlob == null)
      {
        throw new IllegalArgumentException("EPackages with root uri " + nsUri + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(getPackageRegistry());
      final EPackage rootEPackage = EMFUtil.createEPackage(nsUri, ePackageBlob, ZIP_PACKAGE_BYTES, resourceSet, false);
      epacks = EMFUtil.getAllPackages(rootEPackage);
      ePackagesByRootUri.put(nsUri, epacks);
    }

    return epacks;
  }

  @SuppressWarnings("unchecked")
  protected void readPackageUnits()
  {
    if (packageUnits == null || packageUnits.size() == 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Reading Package Units from db"); //$NON-NLS-1$
      }

      Session session = getSessionFactory().openSession();

      try
      {
        Criteria criteria = session.createCriteria(CDO_PACKAGE_UNIT_ENTITY_NAME);
        List<?> list = criteria.list();
        if (TRACER.isEnabled())
        {
          TRACER.trace("Found " + list.size() + " CDOPackageUnits in DB"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        CDOModelUtil.createPackageUnit();

        packageUnits = new ArrayList<InternalCDOPackageUnit>();
        for (HibernateCDOPackageUnitDTO dto : (Collection<HibernateCDOPackageUnitDTO>)list)
        {
          packageUnits.add(dto.createCDOPackageUnit(getPackageRegistry()));
          // cache the blob because resolving the epackages right away gives errors
          ePackageBlobsByRootUri.put(dto.getNsUri(), dto.getEPackageByteArray());
        }
      }
      finally
      {
        session.close();
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Finished reading Package Units"); //$NON-NLS-1$
    }
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

    if (doDropSchema)
    {
      final SchemaExport se = new SchemaExport(configuration);
      se.drop(false, true);
    }

    configuration = null;
    super.doDeactivate();
  }

  protected void initConfiguration()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Initializing configuration for CDO metadata"); //$NON-NLS-1$
    }

    InputStream in = null;

    try
    {
      in = OM.BUNDLE.getInputStream(META_HBM_PATH);
      configuration = new Configuration();
      configuration.addInputStream(in);

      // note this store adapts the properties so create a copy from the
      // one received from the hibernate store
      final Properties props = new Properties();
      props.putAll(hibernateStore.getProperties());
      configuration.setProperties(props);

      // prevent the drop at session factory close...
      // the drop is done by the de-activate
      if (configuration.getProperty(Environment.HBM2DDL_AUTO) != null
          && configuration.getProperty(Environment.HBM2DDL_AUTO).startsWith(HBM2DLL_CREATE))
      {
        doDropSchema = true;
        // note that the value create also re-creates the db and drops the old one
        configuration.setProperty(Environment.HBM2DDL_AUTO, HBM2DLL_UPDATE);
      }
      else
      {
        doDropSchema = false;
      }
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

  SystemInformation getSystemInformation()
  {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    try
    {
      final Criteria c = session.createCriteria(SystemInformation.class);
      List<?> l = c.list();
      int records = l.size();

      final SystemInformation systemInformation;
      if (records == 0)
      {
        systemInformation = new SystemInformation();
        systemInformation.setFirstTime(true);
        systemInformation.setCreationTime(System.currentTimeMillis());
        session.save(systemInformation);
      }
      else if (records == 1)
      {
        systemInformation = (SystemInformation)l.get(0);
        systemInformation.setFirstTime(false);
      }
      else
      {
        throw new IllegalStateException("More than one record in the cdo_system_information table");
      }

      return systemInformation;
    }
    finally
    {
      session.getTransaction().commit();
      session.close();
    }
  }

  Map<String, String> getSystemProperties()
  {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();

    try
    {
      final Map<String, String> result = new HashMap<String, String>();
      final Criteria c = session.createCriteria(SystemProperty.class);
      for (Object o : c.list())
      {
        final SystemProperty systemProperty = (SystemProperty)o;
        result.put(systemProperty.getName(), systemProperty.getValue());
      }

      return result;
    }
    finally
    {
      session.getTransaction().commit();
      session.close();
    }
  }

  void setSystemProperties(Map<String, String> properties)
  {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();

    try
    {
      final Map<String, SystemProperty> currentValues = new HashMap<String, SystemProperty>();
      final Criteria c = session.createCriteria(SystemProperty.class);
      for (Object o : c.list())
      {
        final SystemProperty systemProperty = (SystemProperty)o;
        currentValues.put(systemProperty.getName(), systemProperty);
      }

      // update remove currentones
      final Map<String, String> newValues = new HashMap<String, String>();
      for (String key : properties.keySet())
      {
        if (currentValues.containsKey(key))
        {
          final SystemProperty systemProperty = currentValues.get(key);
          if (properties.get(key) == null)
          {
            session.delete(systemProperty);
          }
          else
          {
            systemProperty.setValue(properties.get(key));
            session.update(systemProperty);
          }
        }
        else
        {
          newValues.put(key, properties.get(key));
        }
      }

      // store the new ones
      for (String key : newValues.keySet())
      {
        final SystemProperty systemProperty = new SystemProperty();
        systemProperty.setName(key);
        systemProperty.setValue(newValues.get(key));
        session.save(systemProperty);
      }
    }
    finally
    {
      session.getTransaction().commit();
      session.close();
    }
  }

  protected void initSchema()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Updating db schema for Hibernate PackageHandler"); //$NON-NLS-1$
    }

    new SchemaUpdate(configuration).execute(true, true);
  }
}
