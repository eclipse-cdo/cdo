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
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
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
    // TODO: error handling if meta attribute not present
    // TODO: error handling if entityname not set
    String entityName = mappingInfo.getEntityName();
    String ePackageURI = mappingInfo.getMetaAttribute("epackage").getValue();

    if (TRACER.isEnabled())
    {
      TRACER.trace("EntityName/packageURI " + entityName + " " + ePackageURI);
    }

    for (EPackage ePackage : hbStore.getPackageHandler().getEPackages())
    {
      if (ePackage.getNsURI().compareTo(ePackageURI) != 0)
      {
        continue;
      }

      for (EClass localCdoClass : EMFUtil.getPersistentClasses(ePackage))
      {
        if (localCdoClass.getName().compareTo(entityName) == 0)
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
        if (localCdoClass.getName().compareTo(entityName) == 0)
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

    if (eClass == null)
    {
      throw new IllegalArgumentException("The mapped class " + mappingInfo.getEntityName()
          + " does not have a eClass equivalent");
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

  @Override
  public EntityMode getEntityMode()
  {
    return EntityMode.MAP;
  }

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
    else if (mappedProperty.getName().compareTo("resourceID") == 0)
    {
      return new CDOResourceIDGetter(this, mappedProperty.getName());
    }
    else if (mappedProperty.getName().compareTo("containerID") == 0)
    {
      return new CDOContainerIDGetter(this, mappedProperty.getName());
    }
    else if (mappedProperty.getName().compareTo("containingFeatureID") == 0)
    {
      return new CDOContainingFeatureIDGetter(this, mappedProperty.getName());
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

    // TODO: externalize this
    if (mappedProperty.getName().compareTo("resourceID") == 0)
    {
      return new CDOResourceIDSetter(this, mappedProperty.getName());
    }

    if (mappedProperty.getName().compareTo("containerID") == 0)
    {
      return new CDOContainerIDSetter(this, mappedProperty.getName());
    }

    if (mappedProperty.getName().compareTo("containingFeatureID") == 0)
    {
      return new CDOContainingFeatureIDSetter(this, mappedProperty.getName());
    }

    EStructuralFeature feature = getEClass().getEStructuralFeature(mappedProperty.getName());
    if (feature instanceof EReference && feature.isMany())
    {
      return new CDOManyReferenceSetter(this, mappedProperty.getName());
    }

    if (feature instanceof EReference)
    {
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
