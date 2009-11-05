/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - changed handling of propertygetters/setters
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateConstants;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.Getter;
import org.hibernate.property.Setter;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.entity.AbstractEntityTuplizer;
import org.hibernate.tuple.entity.EntityMetamodel;

/**
 * @author Eike Stepper
 */
public class CDORevisionTuplizer extends AbstractEntityTuplizer
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDORevisionTuplizer.class);

  private EClass eClass;

  public CDORevisionTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo)
  {
    super(entityMetamodel, mappingInfo);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created CDORevisionTuplizer for entity " + mappingInfo.getEntityName());
    }

    initEClass(mappingInfo);
  }

  private void initEClass(PersistentClass mappingInfo)
  {
    if (eClass != null)
    {
      return;
    }

    HibernateStore hbStore = HibernateStore.getCurrentHibernateStore();

    // find the EClass/Package
    String entityName = mappingInfo.getEntityName();
    String ePackageURI = mappingInfo.getMetaAttribute("epackage").getValue();
    String eClassName = mappingInfo.getMetaAttribute("eclassName").getValue();

    if (ePackageURI == null || eClassName == null)
    {
      throw new IllegalArgumentException("The mapping for the persistentclass " + mappingInfo.getEntityName()
          + " is incorrect, there should be meta data tags for both epackage and "
          + "eclassname, one or both are missing.");
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("EntityName/eclassname/packageURI " + entityName + "/" + eClassName + "/" + ePackageURI);
    }

    for (EPackage ePackage : hbStore.getPackageHandler().getEPackages())
    {
      if (ePackage.getNsURI().compareTo(ePackageURI) != 0)
      {
        continue;
      }

      for (EClass localCdoClass : EMFUtil.getPersistentClasses(ePackage))
      {
        if (localCdoClass.getName().compareTo(eClassName) == 0)
        {
          eClass = localCdoClass;
          break;
        }
      }
    }

    if (eClass == null && ePackageURI.compareTo(EresourcePackage.eINSTANCE.getNsURI()) == 0)
    {
      for (EClass localCdoClass : EMFUtil.getPersistentClasses(EresourcePackage.eINSTANCE))
      {
        if (localCdoClass.getName().compareTo(eClassName) == 0)
        {
          eClass = localCdoClass;
          if (TRACER.isEnabled())
          {
            TRACER.trace("Class is CDOResource class");
          }

          break;
        }
      }
    }

    // add the entityName <--> EClass mapping
    HibernateStore.getCurrentHibernateStore().addEntityNameEClassMapping(entityName, eClass);

    if (eClass == null)
    {
      throw new IllegalArgumentException("The mapped class " + mappingInfo.getEntityName()
          + " does not have a eClass equivalent");
    }
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.tuple.entity.EntityTuplizer#determineConcreteSubclassEntityName(java.lang.Object,
   * org.hibernate.engine.SessionFactoryImplementor)
   */
  public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory)
  {
    final Class<?> concreteEntityClass = entityInstance.getClass();
    if (concreteEntityClass == getMappedClass())
    {
      return getEntityName();
    }
    else
    {
      String entityName = getEntityMetamodel().findEntityNameByEntityClass(concreteEntityClass);
      if (entityName == null)
      {
        throw new HibernateException("Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]"
            + " expected instance/subclass of [" + getEntityName() + "]");
      }

      return entityName;
    }
  }

  public EntityMode getEntityMode()
  {
    return EntityMode.MAP;
  }

  public EntityNameResolver[] getEntityNameResolvers()
  {
    return new EntityNameResolver[] { new CDOEntityNameResolver() };
  }

  private class CDOEntityNameResolver implements EntityNameResolver
  {
    public String resolveEntityName(Object object)
    {
      return getEntityName();
    }
  }

  // MT: probably not required as the property getter/setter do all the work
  // /*
  // * (non-Javadoc)
  // *
  // * @see org.hibernate.tuple.entity.AbstractEntityTuplizer#getVersion(java.lang.Object)
  // */
  // @Override
  // public Object getVersion(Object entity) throws HibernateException
  // {
  // if (entity instanceof CDORevision)
  // {
  // final CDORevision cdoRevision = (CDORevision)entity;
  // return cdoRevision.getVersion();
  // }
  // throw new IllegalArgumentException("Entity of type: " + entity.getClass().getName()
  // + " not supported by this tuplizer");
  // }
  //
  // @Override
  // public Serializable getIdentifier(Object entity) throws HibernateException
  // {
  // if (entity instanceof CDORevision)
  // {
  // final CDOID cdoID = ((CDORevision)entity).getID();
  // if (cdoID instanceof CDOIDHibernate)
  // {
  // return ((CDOIDHibernate)cdoID).getId();
  // }
  // }
  // throw new IllegalArgumentException("Entity of type: " + entity.getClass().getName()
  // + " not supported by this tuplizer");
  // }
  //
  // @Override
  // public void setIdentifier(Object entity, Serializable id) throws HibernateException
  // {
  // if (entity instanceof InternalCDORevision)
  // {
  // final InternalCDORevision cdoRevision = (InternalCDORevision)entity;
  // final CDOID cdoID = cdoRevision.getID();
  // if (cdoID == null)
  // {
  // CDOIDHibernate hid = (CDOIDHibernate)HibernateStore.CDOID_OBJECT_FACTORY.createCDOIDObject(null);
  // hid.setId(id);
  // cdoRevision.setID(hid);
  // return;
  // }
  // else if (cdoID instanceof CDOIDHibernate)
  // {
  // ((CDOIDHibernate)cdoID).setId(id);
  // return;
  // }
  // }
  // throw new IllegalArgumentException("Entity of type: " + entity.getClass().getName()
  // + " not supported by this tuplizer");
  // }

  public EClass getEClass()
  {
    return eClass;
  }

  @Override
  protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity)
  {
    initEClass(mappedEntity);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Building property getter for " + eClass.getName() + "." + mappedProperty.getName());
    }

    if (mappedProperty.isBackRef())
    {
      return mappedProperty.getGetter(mappedEntity.getMappedClass());
    }
    else if (mappedProperty == mappedEntity.getIdentifierProperty())
    {
      return new CDOIDPropertyGetter(this, mappedProperty.getName());
    }
    else if (mappedProperty == mappedEntity.getVersion())
    {
      return new CDOVersionPropertyGetter(this, mappedProperty.getName());
    }
    else if (mappedProperty.getName().compareTo(CDOHibernateConstants.RESOURCE_PROPERTY) == 0)
    {
      return new CDOResourceIDGetter(this, mappedProperty.getName());
    }
    else if (mappedProperty.getName().compareTo(CDOHibernateConstants.CONTAINER_PROPERTY) == 0)
    {
      return new CDOContainerGetter(this, mappedProperty.getName());
    }

    EStructuralFeature feature = getEClass().getEStructuralFeature(mappedProperty.getName());
    if (feature instanceof EReference && feature.isMany())
    {
      return new CDOManyReferenceGetter(this, mappedProperty.getName());
    }
    else if (feature instanceof EReference)
    {
      return new CDOReferenceGetter(this, mappedProperty.getName());
    }
    else if (feature instanceof EAttribute && feature.isMany())
    {
      return new CDOManyAttributeGetter(this, mappedProperty.getName());
    }

    return new CDOPropertyGetter(this, mappedProperty.getName());
  }

  @Override
  protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity)
  {
    initEClass(mappedEntity);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Building property setter for " + eClass.getName() + "." + mappedProperty.getName());
    }

    if (mappedProperty.isBackRef())
    {
      return mappedProperty.getSetter(mappedEntity.getMappedClass());
    }

    if (mappedProperty == mappedEntity.getIdentifierProperty())
    {
      return new CDOIDPropertySetter(this, mappedProperty.getName());
    }

    if (mappedProperty == mappedEntity.getVersion())
    {
      return new CDOVersionPropertySetter(this, mappedProperty.getName());
    }

    if (mappedProperty.getName().compareTo(CDOHibernateConstants.RESOURCE_PROPERTY) == 0)
    {
      return new CDOResourceIDSetter(this, mappedProperty.getName());
    }

    if (mappedProperty.getName().compareTo(CDOHibernateConstants.CONTAINER_PROPERTY) == 0)
    {
      return new CDOContainerSetter(this, mappedProperty.getName());
    }

    EStructuralFeature feature = getEClass().getEStructuralFeature(mappedProperty.getName());
    if (feature instanceof EReference && feature.isMany())
    {
      // TODO Clarify feature maps
      return new CDOManyReferenceSetter(this, mappedProperty.getName());
    }

    if (feature instanceof EAttribute && feature.isMany())
    {
      // TODO Clarify feature maps
      return new CDOManyAttributeSetter(this, mappedProperty.getName());
    }

    if (feature instanceof EReference)
    {
      // TODO Clarify feature maps
      return new CDOReferenceSetter(this, mappedProperty.getName());
    }

    return new CDOPropertySetter(this, mappedProperty.getName());
  }

  @Override
  protected Instantiator buildInstantiator(PersistentClass mappingInfo)
  {
    return new CDORevisionInstantiator(this, mappingInfo);
  }

  @Override
  protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter)
  {
    ProxyFactory pf = new CDORevisionProxyFactory();

    try
    {
      pf.postInstantiate(getEntityName(), null, null, null, null, null);
    }
    catch (HibernateException ex)
    {
      OM.LOG.error("Could not create proxy factory for " + getEntityName(), ex);
      pf = null;
    }

    return pf;
  }

  @SuppressWarnings("unchecked")
  public Class getMappedClass()
  {
    return InternalCDORevision.class;
  }

  @SuppressWarnings("unchecked")
  public Class getConcreteProxyClass()
  {
    return InternalCDORevision.class;
  }

  public boolean isInstrumented()
  {
    return false;
  }
}
