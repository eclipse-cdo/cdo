/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOCustomTypeUserType;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOENumIntegerType;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOENumStringType;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOIDExternalUserType;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDORevisionTuplizer;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOSyntheticIdPropertyHandler;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOSyntheticVersionPropertyHandler;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.FeatureMapEntryTuplizer;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureMapEntry;

import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEClass;
import org.eclipse.emf.teneo.extension.ExtensionManager;
import org.eclipse.emf.teneo.hibernate.mapper.EntityMapper;
import org.eclipse.emf.teneo.hibernate.mapper.MappingContext;
import org.eclipse.emf.teneo.simpledom.Element;

/**
 * Mapping context for CDO. It provides cdo classes as propertyhandler etc.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 * @since 3.0
 */
public class CDOMappingContext extends MappingContext
{
  public CDOMappingContext()
  {
  }

  /** Add a tuplizer element or not */
  @Override
  public void addTuplizerElement(Element entityElement, PAnnotatedEClass aclass)
  {
    Element tuplizerElement = new Element("tuplizer").addAttribute("entity-mode", "dynamic-map").addAttribute("class", //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        CDORevisionTuplizer.class.getName());
    entityElement.add(0, tuplizerElement);
    tuplizerElement = new Element("tuplizer").addAttribute("entity-mode", "pojo").addAttribute("class", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        CDORevisionTuplizer.class.getName());
    entityElement.add(0, tuplizerElement);

    if (entityElement.getAttributeValue("name") != null)
    {
      entityElement.addAttribute("proxy", CDORevision.class.getName());
      entityElement.removeAttribute("name");
    }
  }

  @Override
  public void setExtensionManager(ExtensionManager extensionManager)
  {
    super.setExtensionManager(extensionManager);
    extensionManager.registerExtension(EntityMapper.class.getName(), CDOEntityMapper.class.getName());
  }

  @Override
  public String getComponentPropertyHandlerName()
  {
    return super.getComponentPropertyHandlerName();
  }

  @Override
  public String getIdPropertyHandlerName()
  {
    return null;
  }

  @Override
  public String getPropertyHandlerName()
  {
    return super.getPropertyHandlerName();
  }

  @Override
  public String getVersionPropertyHandlerName()
  {
    return null;
  }

  @Override
  public String getExternalUserType()
  {
    return CDOIDExternalUserType.class.getName();
  }

  @Override
  public String getComponentFeatureMapTuplizer()
  {
    return FeatureMapEntryTuplizer.class.getName();
  }

  @Override
  public String getFeatureMapEntryClassName()
  {
    return CDOFeatureMapEntry.class.getName();
  }

  @Override
  public String getEnumUserType()
  {
    return CDOENumStringType.class.getName();
  }

  @Override
  public String getEnumIntegerUserType()
  {
    return CDOENumIntegerType.class.getName();
  }

  @Override
  public String getSyntheticIdPropertyHandlerName()
  {
    return CDOSyntheticIdPropertyHandler.class.getName();
  }

  @Override
  public String getSyntheticVersionPropertyHandlerName()
  {
    return CDOSyntheticVersionPropertyHandler.class.getName();
  }

  @Override
  public String getDynamicEnumUserType()
  {
    return CDOENumStringType.class.getName();
  }

  @Override
  public String getDynamicEnumIntegerUserType()
  {
    return CDOENumIntegerType.class.getName();
  }

  @Override
  public String getDefaultUserType()
  {
    return CDOCustomTypeUserType.class.getName();
  }

  @Override
  public String getXSDDateUserType()
  {
    return super.getXSDDateUserType();
  }

  @Override
  public String getXSDDateTimeUserType()
  {
    return super.getXSDDateTimeUserType();
  }
}
