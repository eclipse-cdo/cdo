/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.cdo.eresource.EresourcePackage;
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
 * @since 3.0
 */
public class CDOEntityMapper extends EntityMapper
{
  private PAnnotatedEClass currentEntity;

  private boolean addedExtraMappings;

  public CDOEntityMapper()
  {
  }

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

    final Element entityElement = getHbmContext().getCurrent();
    entityElement.addAttribute("lazy", "true");

    if (!addedExtraMappings && currentEntity.getPaSuperEntity() == null)
    {
      final Element resourceElement = entityElement.addElement("property"); //$NON-NLS-1$
      resourceElement.addAttribute("name", CDOHibernateConstants.RESOURCE_PROPERTY); //$NON-NLS-1$
      resourceElement.addElement("column").addAttribute("name", CDOHibernateConstants.RESOURCE_PROPERTY_COLUMN); //$NON-NLS-1$//$NON-NLS-2$
      resourceElement.addAttribute("type", CDOIDUserType.class.getName()); //$NON-NLS-1$

      final Element containerElement = entityElement.addElement("property"); //$NON-NLS-1$
      containerElement.addAttribute("name", CDOHibernateConstants.CONTAINER_PROPERTY).addAttribute("type", "string"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
      final Element columnElement = containerElement.addElement("column").addAttribute("name", //$NON-NLS-1$ //$NON-NLS-2$
          CDOHibernateConstants.CONTAINER_PROPERTY_COLUMN);

      final Element versionElement = entityElement.addElement("property"); //$NON-NLS-1$
      // add cdo_teneo prefix to prop name prevent name clashes with
      // efeatures which are accidentally called version
      // https://bugs.eclipse.org/bugs/show_bug.cgi?id=378050
      versionElement.addAttribute("name", "cdo_teneo_" + getHbmContext().getVersionColumnName()); //$NON-NLS-1$
      versionElement.addElement("meta").addAttribute("attribute", "version").setText("true");
      versionElement.addElement("column").addAttribute("name", getHbmContext().getVersionColumnName()); //$NON-NLS-1$//$NON-NLS-2$
      versionElement.addAttribute("type", Integer.class.getName()); //$NON-NLS-1$

      final Element timeStampElement = entityElement.addElement("property"); //$NON-NLS-1$
      timeStampElement.addAttribute("name", CDOHibernateConstants.COMMITTIMESTAMP_PROPERTY); //$NON-NLS-1$
      timeStampElement.addAttribute("type", Long.class.getName()); //$NON-NLS-1$

      if (getHbmContext().getCurrentEClass() == EresourcePackage.eINSTANCE.getCDOResourceNode())
      {
        // not nice but teneo will assign the first unique-key the number c0
        // and there is only one unique constraint
        columnElement.addAttribute("unique-key", "c0"); //$NON-NLS-1$//$NON-NLS-2$
      }

      addedExtraMappings = true;
    }
  }
}
