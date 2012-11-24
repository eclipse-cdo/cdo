/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateConstants;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metamodel.binding.AttributeBinding;
import org.hibernate.metamodel.binding.EntityBinding;
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

  private static final String EPACKAGE_META = "epackage"; //$NON-NLS-1$

  private static final String ECLASSNAME_META = "eclassName"; //$NON-NLS-1$

  private EClass eClass;

  public CDORevisionTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo)
  {
    super(entityMetamodel, mappingInfo);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created CDORevisionTuplizer for entity " + mappingInfo.getEntityName()); //$NON-NLS-1$
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
    String ePackageURI = mappingInfo.getMetaAttribute(EPACKAGE_META).getValue();
    String eClassName = mappingInfo.getMetaAttribute(ECLASSNAME_META).getValue();

    if (ePackageURI == null || eClassName == null)
    {
      throw new IllegalArgumentException("The mapping for the persistentclass " + mappingInfo.getEntityName() //$NON-NLS-1$
          + " is incorrect, there should be meta data tags for both epackage and " //$NON-NLS-1$
          + "eclassname, one or both are missing."); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("EntityName/eclassname/packageURI " + entityName + "/" + eClassName + "/" + ePackageURI); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
            TRACER.trace("Class is CDOResource class"); //$NON-NLS-1$
          }

          break;
        }
      }
    }

    // add the entityName <--> EClass mapping
    HibernateStore.getCurrentHibernateStore().addEntityNameEClassMapping(entityName, eClass);

    if (eClass == null)
    {
      throw new IllegalArgumentException("The mapped class " + mappingInfo.getEntityName() //$NON-NLS-1$
          + " does not have a eClass equivalent"); //$NON-NLS-1$
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

    String entityName = getEntityMetamodel().findEntityNameByEntityClass(concreteEntityClass);
    if (entityName == null)
    {
      throw new HibernateException("Unable to resolve entity name from Class [" + concreteEntityClass.getName() + "]" //$NON-NLS-1$ //$NON-NLS-2$
          + " expected instance/subclass of [" + getEntityName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return entityName;
  }

  public EntityMode getEntityMode()
  {
    return EntityMode.MAP;
  }

  public EntityNameResolver[] getEntityNameResolvers()
  {
    return new EntityNameResolver[0];
    // return new EntityNameResolver[] { new CDOEntityNameResolver() };
  }

  // private class CDOEntityNameResolver implements EntityNameResolver
  // {
  // public String resolveEntityName(Object object)
  // {
  // return getEntityName();
  // }
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
      TRACER.trace("Building property getter for " + eClass.getName() + "." + mappedProperty.getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (mappedProperty.isBackRef())
    {
      return mappedProperty.getGetter(mappedEntity.getMappedClass());
    }
    else if (mappedProperty == mappedEntity.getIdentifierProperty())
    {
      return new CDOIDPropertyGetter(this, mappedProperty.getName());
    }
    else if (mappedProperty.getMetaAttribute("version") != null)
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
    else if (mappedProperty.getName().compareTo(CDOHibernateConstants.COMMITTIMESTAMP_PROPERTY) == 0)
    {
      return new CDOBranchTimeStampGetter(this, mappedProperty.getName());
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
      TRACER.trace("Building property setter for " + eClass.getName() + "." + mappedProperty.getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (mappedProperty.isBackRef())
    {
      return mappedProperty.getSetter(mappedEntity.getMappedClass());
    }

    if (mappedProperty == mappedEntity.getIdentifierProperty())
    {
      setIdentifierTypeAsAnnotation(mappedProperty);
      return new CDOIDPropertySetter(this, mappedProperty.getName());
    }

    if (mappedProperty.getMetaAttribute("version") != null)
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

    if (mappedProperty.getName().compareTo(CDOHibernateConstants.COMMITTIMESTAMP_PROPERTY) == 0)
    {
      return new CDOBranchTimeStampSetter(this, mappedProperty.getName());
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
    CDORevisionProxyFactory pf = new CDORevisionProxyFactory();
    pf.setEntityName(getEntityName());

    try
    {
      pf.postInstantiate(getEntityName(), null, null, null, null, null);
    }
    catch (HibernateException ex)
    {
      OM.LOG.error("Could not create proxy factory for " + getEntityName(), ex); //$NON-NLS-1$
      pf = null;
    }

    return pf;
  }

  @SuppressWarnings("rawtypes")
  public Class getMappedClass()
  {
    return InternalCDORevision.class;
  }

  @SuppressWarnings("rawtypes")
  public Class getConcreteProxyClass()
  {
    return InternalCDORevision.class;
  }

  public boolean isInstrumented()
  {
    return false;
  }

  private void setIdentifierTypeAsAnnotation(Property prop)
  {
    EAnnotation eAnnotation = getEClass().getEAnnotation(HibernateStore.ID_TYPE_EANNOTATION_SOURCE);
    if (eAnnotation == null)
    {
      eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
      eAnnotation.setSource(HibernateStore.ID_TYPE_EANNOTATION_SOURCE);
      eAnnotation.getDetails().put(HibernateStore.ID_TYPE_EANNOTATION_KEY, prop.getType().getName());
      getEClass().getEAnnotations().add(eAnnotation);
    }
    else if (!eAnnotation.getDetails().containsKey(HibernateStore.ID_TYPE_EANNOTATION_KEY))
    {
      eAnnotation.getDetails().put(HibernateStore.ID_TYPE_EANNOTATION_KEY, prop.getType().getName());
    }
  }

  @Override
  protected Instantiator buildInstantiator(EntityBinding arg0)
  {
    return null;
  }

  @Override
  protected Getter buildPropertyGetter(AttributeBinding arg0)
  {
    return null;
  }

  @Override
  protected Setter buildPropertySetter(AttributeBinding arg0)
  {
    return null;
  }

  @Override
  protected ProxyFactory buildProxyFactory(EntityBinding arg0, Getter arg1, Setter arg2)
  {
    return null;
  }

  @Override
  public Object getVersion(Object entity) throws HibernateException
  {
    if (entity instanceof CDORevision)
    {
      return ((CDORevision)entity).getVersion();
    }
    return super.getVersion(entity);
  }
}
