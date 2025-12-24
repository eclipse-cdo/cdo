/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * Bug 403681: Tests the persistence of the {@linkplain EStructuralFeature#getEType() eType} references
 * (derived from {@linkplain EStructuralFeature#getEGenericType() eGenericType}) of Ecore features.
 *
 * @author Christian W. Damus (CEA)
 */
@Requires(IModelConfig.CAPABILITY_LEGACY)
public class Bugzilla_403681_Test extends AbstractCDOTest
{
  public void testImportEStructuralFeatureTypes() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("localtest.ecore"));

    // Add the package
    resource.getContents().add(createTestPackage());

    transaction.commit();
    transaction.close();

    // Load the package again in a new transaction
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("localtest.ecore"));

    assertEquals(resource.getContents().isEmpty(), false);
    EPackage ePackage = (EPackage)resource.getContents().get(0);

    for (EClassifier next : ePackage.getEClassifiers())
    {
      if (next instanceof EClass)
      {
        for (EStructuralFeature feature : ((EClass)next).getEStructuralFeatures())
        {
          assertNotNull("EStructuralFeature is missing its eType", feature.getEType());
        }
      }
    }
  }

  protected EPackage createTestPackage()
  {
    EPackage result = createUniquePackage();

    EClass classA = EcoreFactory.eINSTANCE.createEClass();
    classA.setName("A");
    result.getEClassifiers().add(classA);

    EClass classB = EcoreFactory.eINSTANCE.createEClass();
    classB.setName("B");
    result.getEClassifiers().add(classB);

    EReference b_a = EcoreFactory.eINSTANCE.createEReference();
    b_a.setName("a");
    b_a.setLowerBound(0);
    b_a.setEType(classA);
    classB.getEStructuralFeatures().add(b_a);

    return result;
  }
}
