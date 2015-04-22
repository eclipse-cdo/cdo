/*
 * Copyright (c) 2008-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDString;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Provides several utility and convenience methods. Mostly related to {@link CDOID} and {@link CDORevision}.
 *
 * @author Martin Taal
 */
public class HibernateUtil
{
  private static final String EXT_POINT = "mappingProviderFactories"; //$NON-NLS-1$

  private static HibernateUtil instance = new HibernateUtil();

  private static final String SEPARATOR = "_;_"; //$NON-NLS-1$

  private static final String MAPPING_PROVIDER_FACTORY_TAG = "mappingProviderFactory"; //$NON-NLS-1$

  private static final String TYPE_ATTRIBUTE = "type"; //$NON-NLS-1$

  private static final String CLASS = "class"; //$NON-NLS-1$

  private static IHibernateMappingProvider.Factory mappingProviderFactoryInstance = null;

  public static IHibernateMappingProvider.Factory getMappingProviderFactoryInstance()
  {
    return mappingProviderFactoryInstance;
  }

  public static void setMappingProviderFactoryInstance(
      IHibernateMappingProvider.Factory theMappingProviderFactoryInstance)
  {
    mappingProviderFactoryInstance = theMappingProviderFactoryInstance;
  }

  /**
   * @return the global singleton instance
   */
  public static HibernateUtil getInstance()
  {
    return instance;
  }

  /**
   * Sets the singleton used by the Hibernate store.
   *
   * @param instance
   *          the instance to set
   */
  public static void setInstance(HibernateUtil instance)
  {
    HibernateUtil.instance = instance;
  }

  // Local copy of the datatype factory
  private DatatypeFactory dataTypeFactory;

  public HibernateUtil()
  {
    try
    {
      dataTypeFactory = DatatypeFactory.newInstance();
    }
    catch (DatatypeConfigurationException ex)
    {
      throw new IllegalStateException("Exception ", ex);
    }
  }

  public DatatypeFactory getDataTypeFactory()
  {
    return dataTypeFactory;
  }

  public void setDataTypeFactory(DatatypeFactory dataTypeFactory)
  {
    this.dataTypeFactory = dataTypeFactory;
  }

  /**
   * @return true if the efeature is {@link CDOResource#getContents()} efeature.
   */
  public boolean isCDOResourceContents(EStructuralFeature eFeature)
  {
    if (!eFeature.getEContainingClass().getEPackage().getNsURI().equals(EresourcePackage.eNS_URI))
    {
      return false;
    }
    if (!eFeature.getEContainingClass().getName().equals(EresourcePackage.eINSTANCE.getCDOResource().getName()))
    {
      return false;
    }
    if (!eFeature.getName().equals(EresourcePackage.eINSTANCE.getCDOResource_Contents().getName()))
    {
      return false;
    }
    return true;
  }

  /**
   * Uses the repository package repository to find the EClass of the identified by the CDOClassifierRef.
   *
   * @param classifierRef
   *          {@link CDOClassifierRef} which identifies an EClass
   * @return the EClass instance identified by the EPackage nsuri and classifier name in the CDOClassifierRef
   * @throws IllegalArgumentException
   *           if the EClass can not be found
   * @see IRepository#getPackageRegistry()
   */
  public EClass getEClass(CDOClassifierRef classifierRef)
  {
    final CDOPackageRegistry registry = getPackageRegistry();
    final EPackage ePackage = registry.getEPackage(classifierRef.getPackageURI());
    if (ePackage == null)
    {
      throw new IllegalArgumentException("No EPackage found with nsuri " + classifierRef.getPackageURI()); //$NON-NLS-1$
    }

    final EClass eClass = (EClass)ePackage.getEClassifier(classifierRef.getClassifierName());
    if (eClass == null)
    {
      throw new IllegalArgumentException("No EClass " + classifierRef.getClassifierName() + " in EPackage " //$NON-NLS-1$ //$NON-NLS-2$
          + ePackage.getNsURI());
    }

    return eClass;
  }

  /**
   * @return the package registry as present in the repository
   * @see HibernateStore#getRepository()
   * @see IRepository#getPackageRegistry()
   */
  public CDOPackageRegistry getPackageRegistry()
  {
    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
    return accessor.getStore().getRepository().getPackageRegistry();
  }

  /**
   * Creates an instance of {@link IHibernateStore}.
   *
   * @param mappingProvider
   *          the provider which generates a mapping.
   * @param properties
   *          hibernate and teneo properties combined
   * @return the created HibernateStore
   * @see HibernateStore
   * @since 2.0
   */
  public IHibernateStore createStore(IHibernateMappingProvider mappingProvider, Properties properties)
  {
    HibernateStore store = new HibernateStore(mappingProvider, properties);
    mappingProvider.setHibernateStore(store);
    return store;
  }

  /**
   * Can only be used when Eclipse is running. In standalone scenarios create the mapping strategy instance by directly
   * calling the constructor of the mapping strategy class.
   *
   * @see #createFileMappingProvider(String)
   * @since 2.0
   */
  public IHibernateMappingProvider.Factory createMappingProviderFactory(String type)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    if (registry == null)
    {
      return getMappingProviderFactoryInstance();
    }
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if (MAPPING_PROVIDER_FACTORY_TAG.equals(element.getName()))
      {
        String typeAttr = element.getAttribute(TYPE_ATTRIBUTE);
        if (ObjectUtil.equals(typeAttr, type))
        {
          try
          {
            return (IHibernateMappingProvider.Factory)element.createExecutableExtension(CLASS);
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }

    return getMappingProviderFactoryInstance();
  }

  /**
   * Creates a FileMappingProvider using the passed locations.
   *
   * @param location
   *          the locations to load the mappings from
   * @return a {@link FileHibernateMappingProvider}
   * @since 3.0
   */
  public IHibernateMappingProvider createFileMappingProvider(String location)
  {
    return new FileHibernateMappingProvider(location);
  }

  /**
   * Retrieves the Hibernate Session from the current {@link HibernateStoreAccessor}. The current HibernateStoreAccessor
   * is maintained in the {@link HibernateThreadContext}.
   *
   * @return the current hibernate session, if none is there a new one is created and a transaction is started.
   * @since 2.0
   */
  public Session getHibernateSession()
  {
    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
    return accessor.getHibernateSession();
  }

  /**
   * Convenience method to convert the properties of the {@link IStore#getRepository()} to a real java Properties
   * object.
   *
   * @param store
   *          the properties of this store are converted to a real Properties object
   * @return a Properties object with the store properties
   */
  public Properties getPropertiesFromStore(IHibernateStore store)
  {
    final Properties props = new Properties();

    final Map<String, String> storeProps = store.getRepository().getProperties();
    for (String key : storeProps.keySet())
    {
      props.setProperty(key, storeProps.get(key));
    }

    return props;
  }

  /**
   * Checks if the object is a HibernateProxy. If so creates the CDOID without resolving the hibernate proxy. If it not
   * a proxy then the id is retrieved from the object itself. It is assumed to be a CDORevision then.
   */
  public CDOID getCDOID(Object o)
  {
    if (o instanceof HibernateProxy)
    {
      final Object idValue = ((HibernateProxy)o).getHibernateLazyInitializer().getIdentifier();
      final String entityName = ((HibernateProxy)o).getHibernateLazyInitializer().getEntityName();
      final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
      EClass eClass = accessor.getStore().getEClass(entityName);
      if (eClass.isInterface() || eClass.isAbstract())
      {
        // load the object to get the real eClass
        // cdo can't handle cdoids with the interface/abstract references
        // need the concrete implementation
        ((HibernateProxy)o).getHibernateLazyInitializer().initialize();
        final CDORevision loadedRevision = (CDORevision)((HibernateProxy)o).getHibernateLazyInitializer()
            .getImplementation((SessionImplementor)accessor.getHibernateSession());
        return loadedRevision.getID();
      }

      return HibernateUtil.getInstance().createCDOID(new CDOClassifierRef(eClass), idValue);
    }

    return ((CDORevision)o).getID();
  }

  /**
   * Reads the entity name while taking account the fact that the object maybe a Hibernate proxy.
   */
  public String getEntityName(Object o)
  {
    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
    if (o instanceof HibernateProxy)
    {
      final String entityName = ((HibernateProxy)o).getHibernateLazyInitializer().getEntityName();
      final EClass eClass = accessor.getStore().getEClass(entityName);
      if (eClass.isInterface() || eClass.isAbstract())
      {
        // load the object to get the real eClass
        // cdo can't handle cdoids with the interface/abstract references
        // need the concrete implementation
        ((HibernateProxy)o).getHibernateLazyInitializer().initialize();
        final CDORevision loadedRevision = (CDORevision)((HibernateProxy)o).getHibernateLazyInitializer()
            .getImplementation((SessionImplementor)accessor.getHibernateSession());
        return accessor.getStore().getEntityName(loadedRevision.getEClass());
      }
      return entityName;
    }

    final EClass eClass = ((CDORevision)o).getEClass();
    return accessor.getStore().getEntityName(eClass);
  }

  /**
   * Deprecated method use: {@link HibernateStore#getEntityName(EClass)}.
   */
  @Deprecated
  public String getEntityName(CDORevision revision)
  {
    return getEntityName((Object)revision);
  }

  /**
   * Converts a CDOID to an unique String representations. Null, {@link CDOIDTemp} and null CDOID's are returned as null
   * value. Supports {@link CDOID} and {@link CDOIDExternal}.
   *
   * @param id
   *          the id to convert
   * @return a unique String
   */
  public String convertCDOIDToString(CDOID id)
  {
    if (id == null || id.isNull() || id.isTemporary())
    {
      return null;
    }

    final StringBuilder sb = new StringBuilder();
    CDOIDUtil.write(sb, id);
    return sb.toString();
  }

  /**
   * Converts a String back to its CDOID representation. The same types as in the {@link #convertCDOIDToString(CDOID)}
   * method are supported.
   *
   * @param strID
   *          the String representation of the CDOID
   * @return a valid CDOID, can be null
   */
  public CDOID convertStringToCDOID(String strID)
  {
    if (strID == null)
    {
      return null;
    }

    return CDOIDUtil.read(strID);
  }

  /**
   * Translates a temporary {@link CDOID} into a hibernate ID, by finding the object it refers to in the
   * {@link CommitContext} and then returning or by persisting the object. Note assumes that the hibernate session and
   * CommitContext are set in HibernateThreadContext.
   *
   * @param id
   *          the CDOID to translate to a valid id, if the id is already valid then it is returned.
   * @return the passed id or an instance of CDOID which is valid.
   */
  public CDOID getCDOIDHibernate(CDOID id)
  {
    if (isStoreCreatedID(id))
    {
      return id;
    }

    final CDORevision revision = getCDORevision(id);
    if (isStoreCreatedID(revision.getID()))
    {
      return revision.getID();
    }

    return getCDOIDHibernate(revision);
  }

  /**
   * Retrieves a {@link CDOID} from the passed CDORevision. If the revision has a non-supported CDOID then the revision
   * is saved to the database.
   *
   * @param revision
   *          the revision to get the id from
   * @return a CDOID supported by this store
   */
  public CDOID getCDOIDHibernate(final CDORevision revision)
  {
    final Session session = getHibernateSession();
    if (!isStoreCreatedID(revision.getID()))
    {
      session.saveOrUpdate(revision);
    }

    checkIsSupportedID(revision.getID());

    return revision.getID();
  }

  /**
   * Retrieves the {@link InternalCDORevision} if the target is a {@link CDOObject} then the CDORevision is retrieved
   * using: {@link CDOObject#cdoRevision()}.
   *
   * @param target
   *          the object which can be a CDOObject or an InternalCDORevision
   * @return the found {@link InternalCDORevision}
   */
  public InternalCDORevision getCDORevision(Object target)
  {
    if (target instanceof CDOObject)
    {
      return (InternalCDORevision)CDOUtil.getCDOObject((EObject)target).cdoRevision();
    }
    return (InternalCDORevision)target;
  }

  /**
   * Gets a current object, first checks the new and dirty objects from the {@link CommitContext}. Otherwise reads it
   * from the session.
   *
   * @param id
   *          the {@link CDOID}, the {@link CDOIDTemp} is resolved against the CommitContext.
   * @return the retrieved {@link CDORevision} or null if the id is a null ({@link CDOIDUtil#isNull(CDOID)})
   */
  public InternalCDORevision getCDORevision(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (HibernateThreadContext.isCommitContextSet())
    {
      final HibernateCommitContext commitContext = HibernateThreadContext.getCommitContext();
      InternalCDORevision revision;
      // if ((revision = commitContext.getDirtyObject(id)) != null)
      // {
      // return revision;
      // }

      if ((revision = commitContext.getNewObject(id)) != null)
      {
        return revision;
      }

      // maybe the temp was already translated
      final CDOID newID = commitContext.getCommitContext().getIDMappings().get(id);
      if (newID != null)
      {
        return getCDORevision(newID);
      }
    }

    checkIsSupportedID(id);

    final String entityName = getEntityName(id);
    final Serializable idValue = getIdValue(id);
    return (InternalCDORevision)getHibernateSession().get(entityName, idValue);
  }

  /**
   * Retrieves a {@link InternalCDORevision} from the {@link CommitContext} or from the database/hibernate session.
   * Resolves temporary id's: {@link CDOIDTemp}.
   *
   * @param id
   *          the {@link CDOID} identifying the object,
   * @return the retrieved CDORevision or null if the revision is not found
   */
  public InternalCDORevision getCDORevisionNullable(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (HibernateThreadContext.isCommitContextSet())
    {
      final HibernateCommitContext commitContext = HibernateThreadContext.getCommitContext();
      InternalCDORevision revision;
      if ((revision = commitContext.getDirtyObject(id)) != null)
      {
        return revision;
      }

      if ((revision = commitContext.getNewObject(id)) != null)
      {
        return revision;
      }

      // maybe the temp was already translated
      if (id instanceof CDOIDTemp)
      {
        final CDOID newID = commitContext.getCommitContext().getIDMappings().get(id);
        if (newID != null)
        {
          return getCDORevision(newID);
        }
      }
    }

    if (!isStoreCreatedID(id))
    {
      return null;
    }

    final String entityName = getEntityName(id);
    final Serializable idValue = getIdValue(id);
    return (InternalCDORevision)getHibernateSession().get(entityName, idValue);
  }

  /**
   * Converts a String to a containing feature id. Note this is not the same as the feature id. The feature is the
   * containing feature of the passed EObject.
   *
   * @param contained
   *          the object which is contained
   * @param value
   *          the value to convert
   * @return the containing feature id.
   * @see #getContainerFeatureId(EClass, EObject, EStructuralFeature)
   */
  public int convertStringToFeatureID(EObject contained, String value)
  {
    final String[] values = value.split(SEPARATOR);
    final String nsuri = values[0];
    final EPackage eContainerPackage = getPackageRegistry().getEPackage(nsuri);
    final String eContainerEClassName = values[1];
    final EClass eContainingClass = (EClass)eContainerPackage.getEClassifier(eContainerEClassName);

    final EPackage eFeaturePackage = getPackageRegistry().getEPackage(values[2]);

    final String eClassifierName = values[3];
    final EClassifier eClassifier = eFeaturePackage.getEClassifier(eClassifierName);
    final EClass eFeatureClass = (EClass)eClassifier;
    final String eFeatureName = values[4];
    final EStructuralFeature eFeature = eFeatureClass.getEStructuralFeature(eFeatureName);
    return getContainerFeatureId(eContainingClass, contained, eFeature);
  }

  /**
   * Computes a valid containing feature id for a passed containing EClass, the contained object and the
   * EStructuralFeature which can be the container or the containment feature.
   *
   * @param containingEClass
   *          the EClass representing the container
   * @param contained
   *          the EObject which is contained
   * @param eFeature
   *          the EStructuralFeature, can be the efeature of the containingEClass or of the contained.
   */
  public int getContainerFeatureId(EClass containingEClass, EObject contained, EStructuralFeature eFeature)
  {
    if (eFeature instanceof EAttribute)
    {
      // featuremap??
      return InternalEObject.EOPPOSITE_FEATURE_BASE - containingEClass.getFeatureID(eFeature);
    }

    final EReference eReference = (EReference)eFeature;
    if (eReference.getEOpposite() != null)
    {
      final EReference containerEReference = eReference.getEOpposite();
      return contained.eClass().getFeatureID(containerEReference);
    }
    return InternalEObject.EOPPOSITE_FEATURE_BASE - containingEClass.getFeatureID(eReference);
  }

  /**
   * Creates the correct subclass of {@link CDOID} for the passed EClass and hibernate id object.
   *
   * @param classifierRef
   *          the EClass to set in the CDOID
   * @param idValue
   *          the real id value
   * @return a supported instance of CDOID.
   * @see CDOIDUtil
   */
  public CDOID createCDOID(CDOClassifierRef classifierRef, Object idValue)
  {
    if (idValue instanceof String)
    {
      return CDOIDUtil.createStringWithClassifier((String)idValue, classifierRef);
    }

    if (idValue instanceof Long)
    {
      return CDOIDUtil.createLongWithClassifier((Long)idValue, classifierRef);
    }

    throw new IllegalArgumentException("The ID value type " + idValue.getClass() //$NON-NLS-1$
    + " is not supported by this store. Method called with " + classifierRef); //$NON-NLS-1$
  }

  /**
   * Checks if the passed id is created/used by this store.
   *
   * @param id
   *          the {@link CDOID} to check
   * @return true if this is a CDOID which is used/created by this store.
   */
  public boolean isStoreCreatedID(CDOID id)
  {
    // TODO: not the nicest check but we know that only these are supported
    // by the hibernatestore
    return id instanceof CDOClassifierRef.Provider || id instanceof CDOIDExternal;
  }

  /**
   * Checks if the passed {@link CDOID} is a type supported by this store.
   *
   * @param id
   *          the CDOID instance to check
   * @throws IllegalArgumentException
   *           if the passed type is not supported.
   */
  public void checkIsSupportedID(CDOID id)
  {
    if (!isStoreCreatedID(id))
    {
      throw new IllegalArgumentException("This CDOID type " + id + " is not supported by this store. " //$NON-NLS-1$ //$NON-NLS-2$
          + id.getClass().getName());
    }
  }

  /**
   * @param id
   *          CDOID to get the internal id from
   * @return the id used by Hibernate, the String or Long value in the CDOID object.
   */
  public Serializable getIdValue(CDOID id)
  {
    if (id instanceof CDOIDString)
    {
      return ((CDOIDString)id).getStringValue();
    }
    return CDOIDUtil.getLong(id);
  }

  /**
   * Retrieves the entity name for the EClass present in the CDOID.
   *
   * @param id
   *          the {@link CDOID} to get the EClass from
   * @return the entity name for the EClass of the CDOID.
   * @see HibernateStore#getEntityName(EClass)
   */
  public String getEntityName(CDOID id)
  {
    final CDOID localId = resolvePossibleTempId(id);
    final CDOClassifierRef classifierRef = CDOIDUtil.getClassifierRef(localId);
    if (classifierRef == null)
    {
      throw new IllegalArgumentException("This CDOID type of " + id + " is not supported by this store."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
    return accessor.getStore().getEntityName(classifierRef);
  }

  /**
   * If the passed in id is a temp id, resolve it against the {@link HibernateThreadContext#getCommitContext()}
   */
  public CDOID resolvePossibleTempId(CDOID cdoId)
  {
    if (!(cdoId instanceof CDOIDTemp))
    {
      return cdoId;
    }
    if (HibernateThreadContext.isCommitContextSet())
    {
      final HibernateCommitContext commitContext = HibernateThreadContext.getCommitContext();

      final CDOID newID = commitContext.getCommitContext().getIDMappings().get(cdoId);
      if (newID != null)
      {
        return newID;
      }

      InternalCDORevision revision;
      if ((revision = commitContext.getNewObject(cdoId)) != null)
      {
        return revision.getID();
      }
    }
    return cdoId;
  }

  /**
   * Create a {@link XMLGregorianCalendar} from a {@link Date} instance.
   */
  public XMLGregorianCalendar getXMLGregorianCalendarDate(Date date, boolean dateTime)
  {
    final XMLGregorianCalendar gregCalendar = dataTypeFactory.newXMLGregorianCalendar();
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    gregCalendar.setYear(calendar.get(Calendar.YEAR));
    gregCalendar.setMonth(calendar.get(Calendar.MONTH) + 1); // correct with 1 on purpose
    gregCalendar.setDay(calendar.get(Calendar.DAY_OF_MONTH));

    if (dateTime)
    {
      gregCalendar.setHour(calendar.get(Calendar.HOUR_OF_DAY));
      gregCalendar.setMinute(calendar.get(Calendar.MINUTE));
      gregCalendar.setSecond(calendar.get(Calendar.SECOND));
      gregCalendar.setMillisecond(calendar.get(Calendar.MILLISECOND));
    }

    return gregCalendar;
  }

}
