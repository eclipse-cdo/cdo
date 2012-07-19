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

import org.eclipse.emf.cdo.server.hibernate.internal.teneo.bundle.OM;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.PackageRegistryProvider;
import org.eclipse.emf.teneo.annotations.pamodel.PAnnotatedEPackage;
import org.eclipse.emf.teneo.annotations.xml.XmlPersistenceContentHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * With cdo packages are registered at runtime so if the package in the xml is not yet present then be lenient about it.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 * @since 3.0
 */
public class CDOXmlPersistenceContentHandler extends XmlPersistenceContentHandler
{
  private PAnnotatedEPackage localPAPackage;

  // is also present in super class, should be made protected there
  private static final int ROOT = 0;

  public CDOXmlPersistenceContentHandler()
  {
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException
  {
    // unknown epackage, ignore for now
    if (localPAPackage == null)
    {
      return;
    }

    super.characters(ch, start, length);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException
  {
    // unknown epackage, ignore for now
    if (localPAPackage == null)
    {
      return;
    }

    super.endElement(uri, localName, qName);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if (getParseState() != ROOT)
    {
      if (localName.equals("epackage")) //$NON-NLS-1$
      {
        final String nameSpaceUri = attributes.getValue("namespace-uri"); //$NON-NLS-1$
        final EPackage ePackage = PackageRegistryProvider.getInstance().getPackageRegistry().getEPackage(nameSpaceUri);
        if (ePackage != null)
        {
          localPAPackage = getPAnnotatedModel().getPAnnotated(ePackage);
        }
        else
        {
          localPAPackage = null;
        }

        if (localPAPackage == null)
        {
          OM.LOG.warn("No EPackage found for namespace " + nameSpaceUri //$NON-NLS-1$
              + ". This is not a problem if this EPackage is registered later."); //$NON-NLS-1$
        }
      }

      // unknown epackage, ignore for now
      if (localPAPackage == null)
      {
        return;
      }
    }

    super.startElement(uri, localName, qName, attributes);
  }
}
