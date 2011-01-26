/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import junit.framework.Assert;

/**
 * Bug 334608 - CommitIntegrityCheck erroneously checks non-persistent features
 * 
 * @author Egidijus Vaishnora, Caspar De Groot
 */
public class Bugzilla_334608_Test extends AbstractCDOTest
{
  public void testOpposites() throws Exception
  {
    EPackage pkg1 = null;
    {
      pkg1 = EcoreFactory.eINSTANCE.createEPackage();
      pkg1.setNsURI("http://test.com/custom");
      pkg1.setName("test");
      pkg1.setNsPrefix("t");

      EClass customClassA = EcoreFactory.eINSTANCE.createEClass();
      customClassA.setName("A");
      pkg1.getEClassifiers().add(customClassA);

      EClass customClassB = EcoreFactory.eINSTANCE.createEClass();
      customClassB.setName("B");
      pkg1.getEClassifiers().add(customClassB);

      createOpposites(customClassA, customClassB);
    }

    {
      // create model and commit it
      CDOSession openSession = openSession();
      CDOTransaction transaction = openSession.openTransaction();
      CDOResource resource = transaction.createResource("test");

      EClass classAClass = (EClass)pkg1.getEClassifier("A");
      EClass classBClass = (EClass)pkg1.getEClassifier("B");

      EObject instanceA = EcoreUtil.create(classAClass);
      EObject instanceB = EcoreUtil.create(classBClass);
      EStructuralFeature eStructuralFeatureA = classAClass.getEStructuralFeature("AB");
      EStructuralFeature eStructuralFeatureB = instanceB.eClass().getEStructuralFeature("_AB");

      instanceA.eSet(eStructuralFeatureA, instanceB);

      resource.getContents().add(instanceA);
      resource.getContents().add(instanceB);

      Assert.assertTrue(eStructuralFeatureB.isTransient());
      Assert.assertTrue(eStructuralFeatureA.isTransient());
      Assert.assertEquals(instanceA, instanceB.eGet(eStructuralFeatureB));
      transaction.commit();

      resource.getContents().remove(instanceA);
      resource.getContents().remove(instanceB);

      InternalCDOCommitContext commitContext = ((CDOTransactionImpl)transaction).createCommitContext();
      CommitIntegrityCheck integrityCheck = new CommitIntegrityCheck(commitContext,
          CommitIntegrityCheck.Style.NO_EXCEPTION);
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
    Assert.assertSame(tmpRefA_B, tmpRefB_A.getEOpposite());
  }
}
