/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOInterceptor;
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

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStore extends Store implements IHibernateStore
{
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

  private Configuration hibernateConfiguration;

  private SessionFactory hibernateSessionFactory;

  private HibernatePackageHandler packageHandler;

  private IHibernateMappingProvider mappingProvider;

  private boolean doDropSchema;

  private SystemInformation systemInformation;

  private Map<String, EClass> entityNameToEClass;

  private Map<String, String> eClassToEntityName;

  private Map<String, String> identifierPropertyNameByEntity;

  private Properties properties;

  // is initialized on get
  private CDOBranchPoint mainBranchHead;

  public HibernateStore(IHibernateMappingProvider mappingProvider)
  {
    this(mappingProvider, null);
  }

  public HibernateStore(IHibernateMappingProvider mappingProvider, Properties properties)
  {
    super(TYPE, OBJECT_ID_TYPES, set(ChangeFormat.REVISION), set(RevisionTemporality.NONE),
        set(RevisionParallelism.NONE));
    this.mappingProvider = mappingProvider;
    packageHandler = new HibernatePackageHandler(this);
    this.properties = properties;

    if (TRACER.isEnabled() && mappingProvider != null)
    {
      TRACER.trace("HibernateStore with mappingProvider " + mappingProvider.getClass().getName()); //$NON-NLS-1$
    }
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

  public void addEntityNameEClassMapping(String entityName, EClass eClass)
  {
    if (entityNameToEClass.get(entityName) != null)
    {
      final EClass currentEClass = entityNameToEClass.get(entityName);
      throw new IllegalArgumentException("There is a entity name collision for EClasses " //$NON-NLS-1$
          + currentEClass.getEPackage().getName() + "." + currentEClass.getName() + "/" //$NON-NLS-1$ //$NON-NLS-2$
          + eClass.getEPackage().getName() + "." + eClass.getName()); //$NON-NLS-1$
    }

    entityNameToEClass.put(entityName, eClass);
    eClassToEntityName.put(eClass.getEPackage().getNsURI() + CDOClassifierRef.URI_SEPARATOR + eClass.getName(),
        entityName);
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

  public String getEntityName(EClass eClass)
  {
    if (eClass == null)
    {
      throw new IllegalArgumentException("EClass argument is null"); //$NON-NLS-1$
    }

    final String entityName = eClassToEntityName.get(eClass.getEPackage().getNsURI() + CDOClassifierRef.URI_SEPARATOR
        + eClass.getName());
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

    final String entityName = eClassToEntityName.get(classifierRef.getPackageURI() + CDOClassifierRef.URI_SEPARATOR
        + classifierRef.getClassifierName());
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

    final EClass eClass = entityNameToEClass.get(entityName);
    if (eClass == null)
    {
      throw new IllegalArgumentException("entityname " + entityName //$NON-NLS-1$
          + " does not map to an EClass, has it been mapped to Hibernate?"); //$NON-NLS-1$
    }

    return eClass;
  }

  public Configuration getHibernateConfiguration()
  {
    return hibernateConfiguration;
  }

  public synchronized SessionFactory getHibernateSessionFactory()
  {
    if (hibernateSessionFactory == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Initializing SessionFactory for HibernateStore"); //$NON-NLS-1$
      }

      currentHibernateStore.set(this);

      entityNameToEClass = new HashMap<String, EClass>();
      eClassToEntityName = new HashMap<String, String>();
      identifierPropertyNameByEntity = new HashMap<String, String>();

      try
      {
        initConfiguration();

        final Iterator<?> iterator = hibernateConfiguration.getClassMappings();
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

        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
      }
      finally
      {
        currentHibernateStore.set(null);
      }
    }

    return hibernateSessionFactory;
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

  public boolean isLocal(CDOID id)
  {
    return false;
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
    if (Hibernate.STRING.getName().equals(idTypeStr))
    {
      return HibernateUtil.getInstance().createCDOID(classifierRef, idPart);
    }
    else if (Hibernate.LONG.getName().equals(idTypeStr))
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

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    return packageHandler.getSystemProperties();
  }

  public void setPropertyValues(Map<String, String> properties)
  {
    packageHandler.setSystemProperties(properties);
  }

  public void removePropertyValues(Set<String> names)
  {
    // TODO: implement HibernateStore.removePropertyValues(names)
    throw new UnsupportedOperationException();
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
    if (hibernateSessionFactory != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Closing SessionFactory"); //$NON-NLS-1$
      }

      hibernateSessionFactory.close();
      hibernateSessionFactory = null;
    }

    // and now do the drop action
    if (doDropSchema)
    {
      final Configuration conf = getHibernateConfiguration();
      final SchemaExport se = new SchemaExport(conf);
      se.drop(false, true);
    }

    hibernateConfiguration = null;
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

    if (hibernateSessionFactory != null)
    {
      if (!hibernateSessionFactory.isClosed())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Closing SessionFactory"); //$NON-NLS-1$
        }

        hibernateSessionFactory.close();
      }

      hibernateSessionFactory = null;
    }
  }

  protected void initConfiguration()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Initializing Configuration"); //$NON-NLS-1$
    }

    InputStream in = null;

    try
    {
      hibernateConfiguration = new Configuration();
      if (mappingProvider != null)
      {
        mappingProvider.setHibernateStore(this);
        hibernateConfiguration.addXML(mappingProvider.getMapping());
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Adding resource.hbm.xml to configuration"); //$NON-NLS-1$
      }

      in = OM.BUNDLE.getInputStream(RESOURCE_HBM_PATH);
      hibernateConfiguration.addInputStream(in);
      hibernateConfiguration.setInterceptor(new CDOInterceptor());

      // make a local copy as it is adapted in the next if-statement
      // and we want to keep the original one untouched, if not
      // subsequent test runs will fail as they use the same
      // properties object
      final Properties props = new Properties();
      props.putAll(getProperties());
      hibernateConfiguration.setProperties(props);

      // prevent the drop on close because the sessionfactory is also closed when
      // new packages are written to the db, so only do a real drop at deactivate
      if (hibernateConfiguration.getProperty(Environment.HBM2DDL_AUTO) != null
          && hibernateConfiguration.getProperty(Environment.HBM2DDL_AUTO).startsWith(HBM2DLL_CREATE))
      {
        doDropSchema = true;
        // note that the value create also re-creates the db and drops the old one
        hibernateConfiguration.setProperty(Environment.HBM2DDL_AUTO, HBM2DLL_UPDATE);
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

  public static HibernateStore getCurrentHibernateStore()
  {
    return currentHibernateStore.get();
  }

  public boolean isFirstTime()
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
}
