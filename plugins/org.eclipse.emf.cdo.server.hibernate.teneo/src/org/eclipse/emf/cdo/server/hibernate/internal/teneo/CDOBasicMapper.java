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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEAttribute;
import org.eclipse.emf.teneo.hibernate.mapper.BasicMapper;
import org.eclipse.emf.teneo.hibernate.mapper.HbMapperConstants;
import org.eclipse.emf.teneo.simpledom.Element;

/**
 * Extends enum mapping with parameters for epackage and eclass. This will be solved in the next build of Teneo.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 */
public class CDOBasicMapper extends BasicMapper
{
  public CDOBasicMapper()
  {
  }

  @Override
  protected void setType(PAnnotatedEAttribute paAttribute, Element propElement)
  {
    super.setType(paAttribute, propElement);
    if (paAttribute.getEnumerated() != null)
    {
      final Element typeElement = propElement.element("type");
      for (Element element : typeElement.getChildren())
      {
        if (element.getName().equals(HbMapperConstants.ECLASSIFIER_PARAM))
        {
          // it has been done already
          return;
        }
      }

      // add the type elements
      final EAttribute eAttribute = paAttribute.getModelEAttribute();
      typeElement.addElement("param").addAttribute("name", HbMapperConstants.ECLASSIFIER_PARAM).addText(
          eAttribute.getEAttributeType().getName());
      typeElement.addElement("param").addAttribute("name", HbMapperConstants.EPACKAGE_PARAM).addText(
          eAttribute.getEAttributeType().getEPackage().getNsURI());
    }
  }
}
