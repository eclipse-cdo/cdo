/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.util.TestEMFUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * @author Eike Stepper
 */
public class GeneratedEcoreTest extends AbstractCDOTest
{
  /**
   * TODO Fix me
   */
  public void testGeneratedEcore() throws Exception
  {
    // Obtain model
    EPackage ecore = EcorePackage.eINSTANCE;

    // Create resource in session 1
    CDOSession session1 = openSession();
    session1.getPackageRegistry().putEPackage(ecore);
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource res1 = transaction1.createResource(getResourcePath("/res"));

    EPackage root1 = TestEMFUtil.loadEcore("model1.ecore", session1.getPackageRegistry());
    res1.getContents().add(root1);
    transaction1.commit();

    // Load resource in session 2
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource res2 = transaction2.getResource(getResourcePath("/res"));

    EList<EObject> contents = res2.getContents();
    EPackage root2 = (EPackage)contents.get(0);
    assertEquals("Unresolved proxy", false, root2.eIsProxy());
    assertEquals(res2, root2.eResource());

    CDOUtil.load(root2, transaction2);
    TestEMFUtil.saveEcore("model1X.ecore", root2);
    // assertEquals(true, "Models differ", EcoreUtil.equals(root1, root2));
  }
}
