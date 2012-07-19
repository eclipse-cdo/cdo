/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateConstants;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.component.AbstractComponentTuplizer;

/**
 * Tuplizer for feature map entries. These types are mapped using the dynamic capabilities of Hibernate.
 * 
 * @see CDOFeatureMapEntry
 * @see FeatureMapEntryPropertyHandler
 * @see FeatureMapEntryFeatureURIPropertyHandler
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 */

public class FeatureMapEntryTuplizer extends AbstractComponentTuplizer
{
  private static final long serialVersionUID = 1L;

  private static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

  private static final EStructuralFeature CDATA = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_CDATA();

  private static final EStructuralFeature COMMENT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Comment();

  public FeatureMapEntryTuplizer(Component component)
  {
    super(component);
  }

  @Override
  protected Instantiator buildInstantiator(Component component)
  {
    return new FeatureMapEntryInstantiator(component);
  }

  @Override
  protected Getter buildGetter(Component component, Property prop)
  {
    return getPropertyAccessor(prop, component).getGetter(component.getComponentClass(), prop.getName());
  }

  @Override
  protected Setter buildSetter(Component component, Property prop)
  {
    return getPropertyAccessor(prop, component).getSetter(component.getComponentClass(), prop.getName());
  }

  protected PropertyAccessor getPropertyAccessor(Property mappedProperty, Component component)
  {
    if (mappedProperty.getName().compareToIgnoreCase(CDOHibernateConstants.FEATUREMAP_PROPERTY_FEATURE) == 0)
    {
      return new FeatureMapEntryFeatureURIPropertyHandler();
    }
    else if (mappedProperty.getName().compareToIgnoreCase(CDOHibernateConstants.FEATUREMAP_PROPERTY_COMMENT) == 0)
    {
      final FeatureMapEntryPropertyHandler propertyHandler = new FeatureMapEntryPropertyHandler();
      propertyHandler.setPropertyName(COMMENT.getName());
      return propertyHandler;
    }
    else if (mappedProperty.getName().compareToIgnoreCase(CDOHibernateConstants.FEATUREMAP_PROPERTY_CDATA) == 0)
    {
      final FeatureMapEntryPropertyHandler propertyHandler = new FeatureMapEntryPropertyHandler();
      propertyHandler.setPropertyName(CDATA.getName());
      return propertyHandler;
    }
    else if (mappedProperty.getName().compareToIgnoreCase(CDOHibernateConstants.FEATUREMAP_PROPERTY_TEXT) == 0)
    {
      final FeatureMapEntryPropertyHandler propertyHandler = new FeatureMapEntryPropertyHandler();
      propertyHandler.setPropertyName(TEXT.getName());
      return propertyHandler;
    }
    else if (mappedProperty.getName().endsWith(CDOHibernateConstants.FEATUREMAP_PROPERTY_ANY_PRIMITIVE))
    {
      final WildCardAttributePropertyHandler propertyHandler = new WildCardAttributePropertyHandler();
      final int index = mappedProperty.getName().lastIndexOf(CDOHibernateConstants.PROPERTY_SEPARATOR);
      final String propName = mappedProperty.getName().substring(0, index);
      propertyHandler.setPropertyName(propName);
      return propertyHandler;
    }
    else if (mappedProperty.getName().endsWith(CDOHibernateConstants.FEATUREMAP_PROPERTY_ANY_REFERENCE))
    {
      final FeatureMapEntryPropertyHandler propertyHandler = new FeatureMapEntryPropertyHandler();
      final int index = mappedProperty.getName().lastIndexOf(CDOHibernateConstants.PROPERTY_SEPARATOR);
      final String propName = mappedProperty.getName().substring(0, index);
      propertyHandler.setPropertyName(propName);
      return propertyHandler;
    }

    final FeatureMapEntryPropertyHandler propertyHandler = new FeatureMapEntryPropertyHandler();
    propertyHandler.setPropertyName(mappedProperty.getName());
    return propertyHandler;
  }

  @SuppressWarnings("rawtypes")
  public Class getMappedClass()
  {
    return CDOFeatureMapEntry.class;
  }
}
