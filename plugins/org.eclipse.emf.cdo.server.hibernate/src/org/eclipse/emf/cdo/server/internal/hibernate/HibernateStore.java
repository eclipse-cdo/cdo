/*
 * Copyright (c) 2008-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - moved cdopackage handler to other class, changed configuration
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext.TransactionPackageRegistry;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOInterceptor;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOMergeEventListener;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.Constants;
import org.eclipse.emf.teneo.PackageRegistryProvider;
import org.eclipse.emf.teneo.PersistenceOptions;
import org.eclipse.emf.teneo.annotations.mapper.PersistenceMappingBuilder;
import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedModel;
import org.eclipse.emf.teneo.hibernate.EMFInterceptor;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbSessionDataStore;
import org.eclipse.emf.teneo.hibernate.auditing.AuditHandler;
import org.eclipse.emf.teneo.hibernate.auditing.AuditProcessHandler;
import org.eclipse.emf.teneo.hibernate.auditing.model.teneoauditing.TeneoAuditEntry;
import org.eclipse.emf.teneo.hibernate.auditing.model.teneoauditing.TeneoauditingPackage;
import org.eclipse.emf.teneo.hibernate.mapper.HibernateMappingGenerator;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.type.StandardBasicTypes;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStore extends Store implements IHibernateStore
{
  public static final String PERSISTENCE_XML = PersistenceOptions.PERSISTENCE_XML;

  public static final String TYPE = "hibernate"; //$NON-NLS-1$

  public static final String ID_TYPE_EANNOTATION_SOURCE = "teneo.cdo";

  public static final String ID_TYPE_EANNOTATION_KEY = "id_type";

  public static final Set<ObjectType> OBJECT_ID_TYPES = new HashSet<ObjectType>(Arrays.asList(
      CDOID.ObjectType.STRING_WITH_CLASSIFIER, CDOID.ObjectType.LONG_WITH_CLASSIFIER));

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStore.class);

  private static final String RESOURCE_HBM_PATH = "mappings/resource.hbm.xml"; //$NON-NLS-1$

  private static final String HBM2DLL_UPDATE = "update"; //$NON-NLS-1$

  private static final String HBM2DLL_CREATE = "create"; //$NON-NLS-1$

  /**
   * Used to give different extensions of Hibernate a context when initializing
   */
  private static ThreadLocal<HibernateStore> currentHibernateStore = new ThreadLocal<HibernateStore>();

  private HbSessionDataStore cdoDataStore;

  private HibernateAuditHandler hibernateAuditHandler;

  private HibernatePackageHandler packageHandler;

  private IHibernateMappingProvider mappingProvider;

  private boolean doDropSchema;

  private SystemInformation systemInformation;

  private List<EPackage> auditEPackages;

  private Map<String, String> identifierPropertyNameByEntity;

  private Properties properties;

  // is initialized on get
  private CDOBranchPoint mainBranchHead;

  private String mappingXml = null;

  public HibernateStore(IHibernateMappingProvider mappingProvider)
  {
    this(mappingProvider, null);
  }

  public HibernateStore(IHibernateMappingProvider mappingProvider, Properties properties)
  {
    super(TYPE, OBJECT_ID_TYPES, set(ChangeFormat.REVISION),
        set(RevisionTemporality.NONE, RevisionTemporality.AUDITING), set(RevisionParallelism.NONE));
    this.mappingProvider = mappingProvider;
    packageHandler = new HibernatePackageHandler(this);
    this.properties = properties;

    if (TRACER.isEnabled() && mappingProvider != null)
    {
      TRACER.trace("HibernateStore with mappingProvider " + mappingProvider.getClass().getName()); //$NON-NLS-1$
    }
  }

  public boolean isAuditing()
  {
    return getRevisionTemporality() == RevisionTemporality.AUDITING;
  }

  public CDOBranchPoint getMainBranchHead()
  {
    if (mainBranchHead == null)
    {
      mainBranchHead = getRepository().getBranchManager().getMainBranch().getHead();
    }

    return mainBranchHead;
  }

  public String getIdentifierPropertyName(String entityName)
  {
    return identifierPropertyNameByEntity.get(entityName);
  }

  public boolean isMapped(EClass eClass)
  {
    return null != cdoDataStore.toEntityName(eClass);
  }

  public Properties getProperties()
  {
    if (properties == null || properties.isEmpty())
    {
      properties = new Properties();

      final Map<String, String> storeProps = getRepository().getProperties();
      for (String key : storeProps.keySet())
      {
        properties.setProperty(key, storeProps.get(key));
      }
    }

    return properties;
  }

  public void addEntityNameEClassMapping(String entityName, EClass eClass)
  {
    cdoDataStore.addEntityNameEClassMapping(entityName, eClass);
  }

  /**
   * Returns all model epackages, so no audit epackages or system 
   * epackages.
   */
  public List<EPackage> getModelEPackages()
  {
    final List<EPackage> epacks = getPackageHandler().getEPackages();
    final ListIterator<EPackage> iterator = epacks.listIterator();
    while (iterator.hasNext())
    {
      final EPackage epack = iterator.next();
      if (CDOModelUtil.isSystemPackage(epack) && epack != EtypesPackage.eINSTANCE)
      {
        iterator.remove();
      }
      else if (isAuditEPackage(epack))
      {
        // an auditing package
        iterator.remove();
      }
    }
    return epacks;
  }

  private boolean isAuditEPackage(EPackage ePackage)
  {
    return TeneoauditingPackage.eNS_URI.equals(ePackage.getNsURI())
        || ePackage.getEAnnotation(Constants.ANNOTATION_SOURCE_AUDITING) != null;
  }

  public String getEntityName(EClass eClass)
  {
    if (eClass == null)
    {
      throw new IllegalArgumentException("EClass argument is null"); //$NON-NLS-1$
    }
    final String entityName = cdoDataStore.toEntityName(eClass);
    if (entityName == null)
    {
      throw new IllegalArgumentException("EClass " + eClass.getName() //$NON-NLS-1$
          + " does not have an entity name, has it been mapped to Hibernate?"); //$NON-NLS-1$
    }

    return entityName;
  }

  public String getEntityName(CDOClassifierRef classifierRef)
  {
    if (classifierRef == null)
    {
      throw new IllegalArgumentException("classifierRef argument is null"); //$NON-NLS-1$
    }

    EClass eClass = (EClass)classifierRef.resolve(getRepository().getPackageRegistry());

    // initialize everything
    getHibernateSessionFactory();

    final String entityName = cdoDataStore.toEntityName(eClass);
    if (entityName == null)
    {
      throw new IllegalArgumentException("EClass " + classifierRef //$NON-NLS-1$
          + " does not have an entity name, has it been mapped to Hibernate?"); //$NON-NLS-1$
    }

    return entityName;
  }

  public EClass getEClass(String entityName)
  {
    if (entityName == null)
    {
      throw new IllegalArgumentException("entityname argument is null"); //$NON-NLS-1$
    }

    final EClass eClass = cdoDataStore.toEClass(entityName);
    if (eClass == null)
    {
      throw new IllegalArgumentException("entityname " + entityName //$NON-NLS-1$
          + " does not map to an EClass, has it been mapped to Hibernate?"); //$NON-NLS-1$
    }

    return eClass;
  }

  public Configuration getHibernateConfiguration()
  {
    return cdoDataStore.getConfiguration();
  }

  public synchronized SessionFactory getHibernateSessionFactory()
  {
    if (cdoDataStore == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Initializing SessionFactory for HibernateStore"); //$NON-NLS-1$
      }

      currentHibernateStore.set(this);

      identifierPropertyNameByEntity = new HashMap<String, String>();

      try
      {
        initDataStore();

        // this has to be done before the classmapping is iterated
        // otherwise it is not initialized
        SessionFactory hibernateSessionFactory = cdoDataStore.getSessionFactory();
        ServiceRegistry serviceRegistry = ((SessionFactoryImpl)hibernateSessionFactory).getServiceRegistry();
        final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventListenerRegistry.setListeners(EventType.MERGE, new CDOMergeEventListener());

        final Iterator<?> iterator = cdoDataStore.getConfiguration().getClassMappings();
        while (iterator.hasNext())
        {
          final PersistentClass pc = (PersistentClass)iterator.next();
          if (pc.getIdentifierProperty() == null)
          {
            // happens for featuremaps for now...
            continue;
          }

          identifierPropertyNameByEntity.put(pc.getEntityName(), pc.getIdentifierProperty().getName());
        }
      }
      catch (Throwable t)
      {
        t.printStackTrace(System.err);
        if (TRACER.isEnabled())
        {
          TRACER.trace(t);
        }
        throw new RuntimeException(t);
      }
      finally
      {
        currentHibernateStore.set(null);
      }
    }

    return cdoDataStore.getSessionFactory();
  }

  public void ensureCorrectPackageRegistry()
  {
    if (cdoDataStore == null)
    {
      return;
    }
    if (cdoDataStore.getPackageRegistry() instanceof TransactionPackageRegistry)
    {
      setInternalPackageRegistry();
    }
  }

  private synchronized void setInternalPackageRegistry()
  {
    cdoDataStore.setPackageRegistry(getRepository().getPackageRegistry(false));
  }

  public Connection getConnection()
  {
    String connectionURL = getProperties().getProperty("hibernate.connection.url");
    String userName = getProperties().getProperty("hibernate.connection.username");
    String passWord = getProperties().getProperty("hibernate.connection.password");

    try
    {
      Connection connection = DriverManager.getConnection(connectionURL, userName, passWord);
      if (connection == null)
      {
        throw new DBException("No connection from driver manager: " + connectionURL); //$NON-NLS-1$
      }

      String autoCommit = getProperties().getProperty("hibernate.connection.autocommit");
      if (autoCommit != null)
      {
        connection.setAutoCommit(Boolean.valueOf(autoCommit));
      }

      return connection;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Deprecated
  public boolean isLocal(CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  public CDOID createObjectID(String val)
  {
    final int index = val.lastIndexOf(CDOClassifierRef.URI_SEPARATOR);
    if (index == -1)
    {
      throw new IllegalArgumentException("Id string " + val + " is not a valid id");
    }

    final String uriPart = val.substring(0, index);
    final String idPart = val.substring(index + 1);
    final CDOClassifierRef classifierRef = new CDOClassifierRef(uriPart);
    final String entityName = getEntityName(classifierRef);
    final EClass eClass = getEClass(entityName);
    final EAnnotation typeEAnnotation = eClass.getEAnnotation(ID_TYPE_EANNOTATION_SOURCE);
    if (typeEAnnotation == null)
    {
      throw new IllegalStateException("EClass " + eClass + " does not have a type annotation");
    }

    final String idTypeStr = typeEAnnotation.getDetails().get(ID_TYPE_EANNOTATION_KEY);
    if (StandardBasicTypes.STRING.getName().equals(idTypeStr))
    {
      return HibernateUtil.getInstance().createCDOID(classifierRef, idPart);
    }
    else if (StandardBasicTypes.LONG.getName().equals(idTypeStr))
    {
      return HibernateUtil.getInstance().createCDOID(classifierRef, new Long(idPart));
    }
    else
    {
      throw new IllegalArgumentException("ID type " + idTypeStr + " not supported ");
    }
  }

  @Override
  public HibernateStoreAccessor createReader(ISession session)
  {
    return new HibernateStoreAccessor(this, session);
  }

  @Override
  public HibernateStoreAccessor createWriter(ITransaction transaction)
  {
    return new HibernateStoreAccessor(this, transaction);
  }

  public Map<String, String> getPersistentProperties(Set<String> names)
  {
    final Map<String, String> result = packageHandler.getSystemProperties();
    if (names == null || names.isEmpty())
    {
      return result;
    }
    final Map<String, String> filteredResult = new HashMap<String, String>();
    for (String name : names)
    {
      if (result.containsKey(name))
      {
        filteredResult.put(name, result.get(name));
      }
    }
    return filteredResult;
  }

  public void setPersistentProperties(Map<String, String> properties)
  {
    packageHandler.setSystemProperties(properties);
  }

  public void removePersistentProperties(Set<String> names)
  {
    final Map<String, String> props = getPersistentProperties(null);
    for (String name : names)
    {
      props.remove(name);
    }
    setPersistentProperties(props);
  }

  public synchronized int getNextPackageID()
  {
    return packageHandler.getNextPackageID();
  }

  public synchronized int getNextClassID()
  {
    return packageHandler.getNextClassID();
  }

  public synchronized int getNextFeatureID()
  {
    return packageHandler.getNextFeatureID();
  }

  public long getCreationTime()
  {
    return getSystemInformation().getCreationTime();
  }

  public void setCreationTime(long creationTime)
  {
    getSystemInformation().setCreationTime(creationTime);
  }

  public HibernatePackageHandler getPackageHandler()
  {
    return packageHandler;
  }

  // TODO: synchronize??
  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    packageHandler.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    Configuration configuration = null;
    if (cdoDataStore != null)
    {
      configuration = cdoDataStore.getConfiguration();
      if (TRACER.isEnabled())
      {
        TRACER.trace("Closing SessionFactory"); //$NON-NLS-1$
      }
      cdoDataStore.close();
    }

    // and now do the drop action
    if (configuration != null && doDropSchema)
    {
      final SchemaExport se = new SchemaExport(configuration);
      se.drop(false, true);
    }

    cdoDataStore = null;
    hibernateAuditHandler = null;
    // get rid of the audit epackages
    if (auditEPackages != null)
    {
      for (EPackage ePackage : auditEPackages)
      {
        getRepository().getPackageRegistry().remove(ePackage.getNsURI());
      }
      auditEPackages = null;
    }
    LifecycleUtil.deactivate(packageHandler, OMLogger.Level.WARN);
    super.doDeactivate();
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    // TODO Consider usings multiple pools for readers (e.g. bound to the session context)
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    // TODO Consider usings multiple pools for writers (e.g. bound to the session context)
    return null;
  }

  // is called after a new package has been added
  // TODO: synchronize??
  // TODO: combine with doActivate/doDeactivate??
  // TODO: assumes that packageHandler has been reset
  protected void reInitialize()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Re-Initializing HibernateStore"); //$NON-NLS-1$
    }

    if (cdoDataStore != null)
    {
      if (!cdoDataStore.isClosed())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Closing SessionFactory"); //$NON-NLS-1$
        }

        cdoDataStore.close();
      }

      cdoDataStore = null;
    }
  }

  protected void initDataStore()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Initializing Configuration"); //$NON-NLS-1$
    }

    InputStream in = null;

    try
    {
      PackageRegistryProvider.getInstance().setThreadPackageRegistry(getRepository().getPackageRegistry());

      cdoDataStore = new CDODataStore();
      hibernateAuditHandler = new HibernateAuditHandler();
      hibernateAuditHandler.setCdoDataStore(cdoDataStore);
      hibernateAuditHandler.setHibernateStore(this);

      cdoDataStore.setResetConfigurationOnInitialization(false);
      cdoDataStore.setName("cdo");
      cdoDataStore.setPackageRegistry(getRepository().getPackageRegistry());
      cdoDataStore.getExtensionManager().registerExtension(EMFInterceptor.class.getName(),
          CDOInterceptor.class.getName());
      cdoDataStore.getExtensionManager().registerExtension(AuditHandler.class.getName(),
          CDOAuditHandler.class.getName());
      cdoDataStore.getExtensionManager().registerExtension(AuditProcessHandler.class.getName(),
          CDOAuditProcessHandler.class.getName());

      // don't do any persistence xml mapping in this datastore
      // make a local copy as it is adapted in the next if-statement
      // and we want to keep the original one untouched, if not
      // subsequent test runs will fail as they use the same
      // properties object
      final Properties props = new Properties();
      props.putAll(getProperties());
      props.remove(PersistenceOptions.PERSISTENCE_XML);

      if (!props.containsKey(PersistenceOptions.HANDLE_UNSET_AS_NULL))
      {
        props.setProperty(PersistenceOptions.HANDLE_UNSET_AS_NULL, "true");
      }

      cdoDataStore.setDataStoreProperties(props);
      Configuration hibernateConfiguration = cdoDataStore.getConfiguration();

      if (mappingProvider != null)
      {
        mappingProvider.setHibernateStore(this);
        mappingXml = mappingProvider.getMapping();
        hibernateConfiguration.addXML(mappingXml);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Adding resource.hbm.xml to configuration"); //$NON-NLS-1$
      }

      in = OM.BUNDLE.getInputStream(RESOURCE_HBM_PATH);
      hibernateConfiguration.addInputStream(in);
      // hibernateConfiguration.setInterceptor(new CDOInterceptor());

      hibernateConfiguration.setProperties(props);

      // prevent the drop on close because the sessionfactory is also closed when
      // new packages are written to the db, so only do a real drop at deactivate
      if (hibernateConfiguration.getProperty(Environment.HBM2DDL_AUTO) != null
          && hibernateConfiguration.getProperty(Environment.HBM2DDL_AUTO).startsWith(HBM2DLL_CREATE))
      {
        doDropSchema = true;
        // note that the value create also re-creates the db and drops the old one
        hibernateConfiguration.setProperty(Environment.HBM2DDL_AUTO, HBM2DLL_UPDATE);
        cdoDataStore.getDataStoreProperties().setProperty(Environment.HBM2DDL_AUTO, HBM2DLL_UPDATE);
      }
      else
      {
        doDropSchema = false;
      }

      final List<EPackage> ePackages = new ArrayList<EPackage>(packageHandler.getEPackages());

      // get rid of the system packages
      for (EPackage ePackage : packageHandler.getEPackages())
      {
        if (CDOModelUtil.isSystemPackage(ePackage) && ePackage != EtypesPackage.eINSTANCE)
        {
          ePackages.remove(ePackage);
        }
      }
      // remove the persistence xml if no epackages as this won't work without
      // epackages
      if (ePackages.size() == 0 && props.getProperty(PersistenceOptions.PERSISTENCE_XML) != null)
      {
        cdoDataStore.getDataStoreProperties().remove(PersistenceOptions.PERSISTENCE_XML);
      }

      if (isAuditing())
      {
        auditEPackages = createAuditEPackages(cdoDataStore);
        final String auditMapping = mapAuditingEPackages(cdoDataStore, auditEPackages);
        // System.err.println(auditMapping);
        hibernateConfiguration.addXML(auditMapping);
        cdoDataStore.setAuditing(true);
      }
      cdoDataStore.setEPackages(ePackages.toArray(new EPackage[0]));
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      PackageRegistryProvider.getInstance().setThreadPackageRegistry(null);
      IOUtil.close(in);
    }
  }

  public static HibernateStore getCurrentHibernateStore()
  {
    return currentHibernateStore.get();
  }

  public boolean isFirstStart()
  {
    return getSystemInformation().isFirstTime();
  }

  private SystemInformation getSystemInformation()
  {
    if (systemInformation == null)
    {
      systemInformation = getPackageHandler().getSystemInformation();
    }

    return systemInformation;
  }

  public String getMappingXml()
  {
    return mappingXml;
  }

  private List<EPackage> createAuditEPackages(HbDataStore dataStore)
  {
    final PersistenceOptions po = dataStore.getPersistenceOptions();

    final List<EPackage> epacks = new ArrayList<EPackage>();
    for (EPackage ePackage : getModelEPackages())
    {
      if (!CDOModelUtil.isSystemPackage(ePackage) && !isAuditEPackage(ePackage))
      {
        epacks.add(dataStore.getAuditHandler().createAuditingEPackage(dataStore, ePackage,
            getRepository().getPackageRegistry(), po));
      }
    }
    epacks.add(dataStore.getAuditHandler().createAuditingEPackage(dataStore,
        getRepository().getPackageRegistry().getEPackage(EresourcePackage.eNS_URI),
        getRepository().getPackageRegistry(), po));

    epacks.add(TeneoauditingPackage.eINSTANCE);

    getRepository().getPackageRegistry().put(TeneoauditingPackage.eNS_URI, TeneoauditingPackage.eINSTANCE);

    // also register them all in the non-transaction registry
    for (EPackage ePackage : epacks)
    {
      getRepository().getPackageRegistry(false).put(ePackage.getNsURI(), ePackage);
    }
    return epacks;
  }

  public String mapAuditingEPackages(HbDataStore dataStore, List<EPackage> auditEPackages)
  {
    // create a new persistence options to not change the original
    final Properties props = new Properties();
    props.putAll(dataStore.getPersistenceOptions().getProperties());
    props.remove(PersistenceOptions.PERSISTENCE_XML);
    if (props.containsKey(PersistenceOptions.AUDITING_PERSISTENCE_XML))
    {
      props.setProperty(PersistenceOptions.PERSISTENCE_XML, PersistenceOptions.AUDITING_PERSISTENCE_XML);
    }
    final PersistenceOptions po = dataStore.getExtensionManager().getExtension(PersistenceOptions.class,
        new Object[] { props });
    PAnnotatedModel paModel = dataStore.getExtensionManager().getExtension(PersistenceMappingBuilder.class)
        .buildMapping(auditEPackages, po, dataStore.getExtensionManager(), dataStore.getPackageRegistry());
    final HibernateMappingGenerator hmg = dataStore.getExtensionManager().getExtension(HibernateMappingGenerator.class);
    hmg.setPersistenceOptions(po);
    final String hbm = hmg.generateToString(paModel);

    return hbm;
  }

  private class CDODataStore extends HbSessionDataStore
  {

    private static final long serialVersionUID = 1L;

    @Override
    protected void addContainerMapping(PersistentClass pc)
    {
      // prevent container mapping for cdo objects
      if (pc.getTuplizerMap() != null)
      {
        for (Object tuplizerName : pc.getTuplizerMap().values())
        {
          if (((String)tuplizerName).contains("org.eclipse.emf.cdo"))
          {
            return;
          }
        }
      }
      super.addContainerMapping(pc);
    }

    @Override
    protected void mapModel()
    {
      if (getPersistenceOptions().getMappingFilePath() != null || getPersistenceOptions().isUseMappingFile())
      {
        super.mapModel();
      }
    }
  }

  public HibernateAuditHandler getHibernateAuditHandler()
  {
    ensureCorrectPackageRegistry();
    return hibernateAuditHandler;
  }

  public void setHibernateAuditHandler(HibernateAuditHandler hibernateAuditHandler)
  {
    this.hibernateAuditHandler = hibernateAuditHandler;
  }

  public static class CDOAuditProcessHandler extends AuditProcessHandler
  {

    private static final long serialVersionUID = 1L;

    @Override
    protected long getCommitTime()
    {
      if (HibernateThreadContext.isCommitContextSet())
      {
        return HibernateThreadContext.getCommitContext().getCommitContext().getBranchPoint().getTimeStamp();
      }
      return super.getCommitTime();
    }

    @Override
    protected void doAuditWorkInSession(Session session, List<AuditWork> auditWorks)
    {
      try
      {
        PackageRegistryProvider.getInstance().setThreadPackageRegistry(getDataStore().getPackageRegistry());

        if (HibernateThreadContext.isCommitContextSet())
        {
          AuditProcessHandler.setCurrentUserName(HibernateThreadContext.getCommitContext().getCommitContext()
              .getUserID());
          AuditProcessHandler.setCurrentComment(HibernateThreadContext.getCommitContext().getCommitContext()
              .getCommitComment());
        }
        super.doAuditWorkInSession(session, auditWorks);
      }
      finally
      {
        PackageRegistryProvider.getInstance().setThreadPackageRegistry(null);
        AuditProcessHandler.setCurrentUserName(null);
        AuditProcessHandler.setCurrentComment(null);
      }
    }

    @Override
    protected void setContainerInfo(Session session, TeneoAuditEntry auditEntry, Object entity)
    {
      if (!(entity instanceof InternalCDORevision))
      {
        return;
      }
      final InternalCDORevision cdoRevision = (InternalCDORevision)entity;

      // set the resource id
      if (cdoRevision.getResourceID() != null)
      {
        auditEntry.setTeneo_resourceid(getAuditHandler().entityToIdString(session, cdoRevision.getResourceID()));
      }

      if (cdoRevision.getContainerID() == null || cdoRevision.getContainerID() == CDOID.NULL)
      {
        return;
      }

      auditEntry.setTeneo_container_id(getAuditHandler().entityToIdString(session, cdoRevision.getContainerID()));
      auditEntry.setTeneo_container_feature_id(cdoRevision.getContainingFeatureID());
    }

    @Override
    protected boolean performVersionCheck()
    {
      return true;
    }
  }

  public HbSessionDataStore getCDODataStore()
  {
    return cdoDataStore;
  }
}
