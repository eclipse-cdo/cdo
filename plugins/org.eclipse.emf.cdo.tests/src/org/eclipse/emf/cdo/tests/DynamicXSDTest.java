/*
 * Copyright (c) 2009-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christopher Albert - adaption to XSD
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * @author Eike Stepper
 */
public class DynamicXSDTest extends AbstractCDOTest
{
  public void testDynamicEcore() throws Exception
  {
    {
      // Obtain model
      EPackage ecore = createPackage();
      EClass companyClass = (EClass)ecore.getEClassifier("Company");
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");

      // Create resource in session 1
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(ecore);
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

  private EPackage createPackage()
  {
    EPackage result = createUniquePackage();
    EClass company = EMFUtil.createEClass(result, "Company", false, false);
    ExtendedMetaData.INSTANCE.setName(company, "Company");
    ExtendedMetaData.INSTANCE.setContentKind(company, ExtendedMetaData.ELEMENT_ONLY_CONTENT);

    EAttribute att = EMFUtil.createEAttribute(company, "name", XMLTypePackage.eINSTANCE.getString());
    att.setLowerBound(1);
    ExtendedMetaData.INSTANCE.setName(att, "name");
    ExtendedMetaData.INSTANCE.setFeatureKind(att, ExtendedMetaData.ELEMENT_FEATURE);

    return result;
  }
}
