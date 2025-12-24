/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Egidijus Vaishnora - initial API and implementation
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Egidijus Vaishnora, Caspar De Groot
 */
public class Bugzilla_333950_Test extends AbstractCDOTest
{
  public void testOpposites() throws Exception
  {
    EPackage pkg = null;
    {
      // create ECore metamodel. Major aspect is to create two classes, which has linked opposite EReferences and one of
      // reference is not persistence
      pkg = createUniquePackage();

      EClass customClassA = EcoreFactory.eINSTANCE.createEClass();
      customClassA.setName("A");
      pkg.getEClassifiers().add(customClassA);

      EClass customClassB = EcoreFactory.eINSTANCE.createEClass();
      customClassB.setName("B");
      pkg.getEClassifiers().add(customClassB);

      createOpposites(customClassA, customClassB);
    }

    {
      // create model and commit it
      CDOSession openSession = openSession();
      CDOTransaction openTransaction = openSession.openTransaction();
      CDOResource createResource = openTransaction.createResource(getResourcePath("test"));

      EClass classAClass = (EClass)pkg.getEClassifier("A");
      EClass classBClass = (EClass)pkg.getEClassifier("B");

      EObject instanceA = EcoreUtil.create(classAClass);
      EObject instanceB = EcoreUtil.create(classBClass);
      EStructuralFeature eStructuralFeatureA = classAClass.getEStructuralFeature("AB");
      EStructuralFeature eStructuralFeatureB = instanceB.eClass().getEStructuralFeature("_AB");

      instanceA.eSet(eStructuralFeatureA, instanceB);

      assertEquals(instanceA, instanceB.eGet(eStructuralFeatureB));

      createResource.getContents().add(instanceA);
      createResource.getContents().add(instanceB);

      openTransaction.commit();

      assertEquals(true, eStructuralFeatureB.isTransient());
      assertEquals(instanceA, instanceB.eGet(eStructuralFeatureB));

      System.out.println("---> instanceA: " + CDOUtil.getCDOObject(instanceA).cdoID());
      System.out.println("---> instanceB: " + CDOUtil.getCDOObject(instanceB).cdoID());

      openSession.close();
    }

    // open committed model and validate if transient opposite reference is available
    CDOSession openSession2 = openSession();
    CDOTransaction openTransaction2 = openSession2.openTransaction();
    CDOResource resource = openTransaction2.getResource(getResourcePath("test"));
    EObject eObjectA = resource.getContents().get(0);
    EObject eObjectB = resource.getContents().get(1);

    EStructuralFeature eStructuralFeatureA = eObjectA.eClass().getEStructuralFeature("AB");
    assertEquals(false, eStructuralFeatureA.isTransient());
    assertEquals(eObjectB, eObjectA.eGet(eStructuralFeatureA));

    EStructuralFeature eStructuralFeatureB = eObjectB.eClass().getEStructuralFeature("_AB");
    assertEquals(true, eStructuralFeatureB.isTransient());
    assertEquals(eObjectA, eObjectB.eGet(eStructuralFeatureB));
  }

  private void createOpposites(EClass A, EClass B)
  {
    EReference tmpRefA_B = EcoreFactory.eINSTANCE.createEReference();
    tmpRefA_B.setName(A.getName() + B.getName());
    A.getEStructuralFeatures().add(tmpRefA_B);
    tmpRefA_B.setEType(B);

    EReference tmpRefB_A = EcoreFactory.eINSTANCE.createEReference();
    tmpRefB_A.setTransient(true);
    tmpRefB_A.setName("_" + tmpRefA_B.getName());
    B.getEStructuralFeatures().add(tmpRefB_A);
    tmpRefB_A.setEType(A);

    tmpRefA_B.setEOpposite(tmpRefB_A);
    tmpRefB_A.setEOpposite(tmpRefA_B);
    assertSame(tmpRefA_B, tmpRefB_A.getEOpposite());
  }
}
