/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Sebastien REVOL (CEA LIST) sebastien.revol@cea.fr - Initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.xml.type.impl;

import org.eclipse.emf.cdo.ecore.impl.EStringToStringMapEntryImpl;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * @author Sebastien Revol
 */
public class XMLTypeDocumentRootImpl extends org.eclipse.emf.ecore.xml.type.impl.XMLTypeDocumentRootImpl
{
  public XMLTypeDocumentRootImpl()
  {
  }

  @Override
  public EMap<String, String> getXMLNSPrefixMap()
  {
    if (xMLNSPrefixMap == null)
    {
      // The super class implementation would pass EMF's EStringToStringMapEntryImpl,
      // but that's not compatible with CDO's replacement. So pass in CDO's version of it.
      xMLNSPrefixMap = new EcoreEMap<>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this,
          XMLTypePackage.XML_TYPE_DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
    }

    return xMLNSPrefixMap;
  }

  @Override
  public EMap<String, String> getXSISchemaLocation()
  {
    if (xSISchemaLocation == null)
    {
      // The super class implementation would pass EMF's EStringToStringMapEntryImpl,
      // but that's not compatible with CDO's replacement. So pass in CDO's version of it.
      xSISchemaLocation = new EcoreEMap<>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this,
          XMLTypePackage.XML_TYPE_DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
    }

    return xSISchemaLocation;
  }
}
