/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.util.CommitIntegrityCheck;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

/**
 * Bug 334608 - CommitIntegrityCheck erroneously checks non-persistent features
 *
 * @author Egidijus Vaishnora, Caspar De Groot
 */
public class Bugzilla_334608_Test extends AbstractCDOTest
{
  public void testOpposites() throws Exception
  {
    EPackage pkg = null;
    {
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
      CDOTransaction transaction = openSession.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      EClass classAClass = (EClass)pkg.getEClassifier("A");
      EClass classBClass = (EClass)pkg.getEClassifier("B");

      EObject instanceA = EcoreUtil.create(classAClass);
      EObject instanceB = EcoreUtil.create(classBClass);
      EStructuralFeature eStructuralFeatureA = classAClass.getEStructuralFeature("AB");
      EStructuralFeature eStructuralFeatureB = instanceB.eClass().getEStructuralFeature("_AB");

      instanceA.eSet(eStructuralFeatureA, instanceB);

      resource.getContents().add(instanceA);
      resource.getContents().add(instanceB);

      assertEquals(true, eStructuralFeatureB.isTransient());
      assertEquals(true, eStructuralFeatureA.isTransient());
      assertEquals(instanceA, instanceB.eGet(eStructuralFeatureB));
      transaction.commit();

      resource.getContents().remove(instanceA);
      resource.getContents().remove(instanceB);

      InternalCDOCommitContext commitContext = ((CDOTransactionImpl)transaction).createCommitContext();
      CommitIntegrityCheck integrityCheck = new CommitIntegrityCheck(commitContext, CommitIntegrityCheck.Style.NO_EXCEPTION);
      integrityCheck.check();

      transaction.commit();
      openSession.close();
    }
  }

  private void createOpposites(EClass A, EClass B)
  {
    EReference tmpRefA_B = EcoreFactory.eINSTANCE.createEReference();
    tmpRefA_B.setTransient(true);
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
