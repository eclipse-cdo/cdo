/***************************************************************************
 * Copyright (c) 2004 - 2009 Springsite B.V. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.hibernate.internal.teneo;

import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateConstants;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOIDUserType;

import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEClass;
import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEStructuralFeature;
import org.eclipse.emf.teneo.hibernate.mapper.EntityMapper;
import org.eclipse.emf.teneo.simpledom.Element;

import java.util.List;

/**
 * Adds mapping for econtainer and eresource.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 */
public class CDOEntityMapper extends EntityMapper
{

  private PAnnotatedEClass currentEntity = null;

  private boolean addedExtraMappings = false;

  @Override
  public void processEntity(PAnnotatedEClass entity)
  {
    // not the nicest solution
    currentEntity = entity;
    try
    {
      addedExtraMappings = false;
      super.processEntity(entity);
    }
    finally
    {
      currentEntity = null;
    }
  }

  // add container and resource mappings
  @Override
  protected void processFeatures(List<PAnnotatedEStructuralFeature> features)
  {
    super.processFeatures(features);

    if (!addedExtraMappings && currentEntity.getPaSuperEntity() == null)
    {
      final Element entityElement = getHbmContext().getCurrent();
      final Element resourceElement = entityElement.addElement("property");
      resourceElement.addAttribute("name", CDOHibernateConstants.RESOURCE_PROPERTY);
      resourceElement.addElement("column").addAttribute("name", CDOHibernateConstants.RESOURCE_PROPERTY_COLUMN);
      resourceElement.addAttribute("type", CDOIDUserType.class.getName());
      final Element containerElement = entityElement.addElement("property");
      containerElement.addAttribute("name", CDOHibernateConstants.CONTAINER_PROPERTY).addAttribute("type", "string");
      containerElement.addElement("column").addAttribute("name", CDOHibernateConstants.CONTAINER_PROPERTY_COLUMN);
      addedExtraMappings = true;
    }
  }
}
