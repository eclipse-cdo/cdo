/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples;

import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Webinar20090129
{
  private static final EPackage MODEL = CompanyPackage.eINSTANCE;

  public static void xmlSetup() throws IOException
  {
    ResourceSet rs = new ResourceSetImpl();
    rs.getResourceFactoryRegistry().getExtensionToFactoryMap() //
        .put("xml", new XMLResourceFactoryImpl()); //$NON-NLS-1$
    rs.getPackageRegistry().put(MODEL.getNsURI(), MODEL);

    URI uri = URI.createFileURI("C:/business/company.xml"); //$NON-NLS-1$
    Resource resource = rs.getResource(uri, true);
    resource.setTrackingModification(true);

    Company company = (Company)resource.getContents().get(0);
    executeBusinessLogic(company);

    if (resource.isModified())
    {
      resource.save(null);
    }
  }

  private static void executeBusinessLogic(Company company)
  {
  }
}
