/*
 * Copyright (c) 2007-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class DymamicUmlTest extends AbstractCDOTest
{
  public void testGeneratedPackage() throws Exception
  {
    {
      // Create resource in session 1
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    {
      // Load resource in session 2
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource(getResourcePath("/res"));

      Company company = (Company)res.getContents().get(0);
      assertEquals("Eike", company.getName());
    }
  }

  public void testDynamicPackage() throws Exception
  {
    {
      // Obtain model
      EPackage model1 = loadModel("model1.ecore");
      EClass companyClass = (EClass)model1.getEClassifier("Company");
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");

      // Create resource in session 1
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      EObject company = EcoreUtil.create(companyClass);
      company.eSet(nameAttribute, "Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    {
      // Load resource in session 2
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource(getResourcePath("/res"));

      CDOObject company = CDOUtil.getCDOObject(res.getContents().get(0));
      EClass companyClass = company.eClass();
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
      String name = (String)company.eGet(nameAttribute);
      assertEquals("Eike", name);
    }
  }

  private static EPackage loadModel(String fileName) throws IOException
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    Resource resource = resourceSet.getResource(URI.createFileURI(fileName), true);
    return (EPackage)resource.getContents().get(0);
  }
}
