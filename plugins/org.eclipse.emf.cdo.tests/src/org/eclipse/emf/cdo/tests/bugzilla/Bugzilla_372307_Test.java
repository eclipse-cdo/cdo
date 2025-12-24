/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Steve Monnier - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.B;
import org.eclipse.emf.cdo.tests.model6.C;
import org.eclipse.emf.cdo.tests.model6.D;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;

/**
 * @author Steve Monnier
 */
public class Bugzilla_372307_Test extends AbstractCDOTest
{
  public void testMoveElementToOtherContainer() throws Exception
  {
    A session1a1 = getModel6Factory().createA();
    A session1a2 = getModel6Factory().createA();
    D session1d = getModel6Factory().createD();

    session1a1.getOwnedDs().add(session1d);

    // Create model using session1
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("myResource"));
    resource1.getContents().add(session1a1);
    resource1.getContents().add(session1a2);
    transaction1.commit();

    System.out.println("a1: " + CDOUtil.getCDOObject(session1a1));
    System.out.println("a2: " + CDOUtil.getCDOObject(session1a2));
    System.out.println("d:  " + CDOUtil.getCDOObject(session1d));

    // Open a second session on the shared resource
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath("myResource"), true);
    A session2a1 = (A)resource2.getContents().get(0);
    A session2a2 = (A)resource2.getContents().get(1);
    D session2d = session2a1.getOwnedDs().get(0);

    // Move the D element from parent A1 to A2 in session1 and commit
    EList<D> ownedDs = session1a2.getOwnedDs();
    ownedDs.add(session1d);
    commitAndSync(transaction1, transaction2);

    // Validate that A1 has no content in both sessions
    assertEquals("A1 in session1 is expected to be empty", 0, session1a1.eContents().size());
    assertEquals("A1 in session2 is expected to be empty", 0, session2a1.eContents().size());

    // Validate that A2 contains D in both sessions
    assertEquals("A2 in session1 is expected to have contents", 1, session1a2.eContents().size());
    assertEquals("A2 in session2 is expected to have contents", 1, session2a2.eContents().size());

    CDOID id1 = CDOUtil.getCDOObject(session1a2.eContents().get(0)).cdoID();
    CDOID id2 = CDOUtil.getCDOObject(session2a2.eContents().get(0)).cdoID();
    assertEquals("Element contained in A1 should have the same CDOID in both sessions", id1, id2);

    // Validate that the container of the D element is the same in both sessions
    id1 = CDOUtil.getCDOObject(session1d.eContainer()).cdoID();
    id2 = CDOUtil.getCDOObject(session2d.eContainer()).cdoID();
    assertEquals("Container of D should have the same CDOID in both sessions", id1, id2);

    // Validate that the containment feature of D on both sessions are not null, and equal
    EReference ownedDs_Feature = getModel6Package().getA_OwnedDs();
    assertEquals(ownedDs_Feature, session1d.eContainmentFeature());
    assertEquals(ownedDs_Feature, session2d.eContainmentFeature());
  }

  public void testMoveElementToOtherContainerSingleValued() throws Exception
  {
    B session1a1 = getModel6Factory().createB();
    B session1a2 = getModel6Factory().createB();
    C session1d = getModel6Factory().createC();

    session1a1.setOwnedC(session1d);

    // Create model using session1
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("myResource"));
    resource1.getContents().add(session1a1);
    resource1.getContents().add(session1a2);
    transaction1.commit();

    System.out.println("a1: " + CDOUtil.getCDOObject(session1a1));
    System.out.println("a2: " + CDOUtil.getCDOObject(session1a2));
    System.out.println("d:  " + CDOUtil.getCDOObject(session1d));

    // Move the D element from parent A1 to A2 in session1 and commit
    session1a2.setOwnedC(session1d);
    transaction1.commit();

    // Open a second session on the shared resource
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    C session2d = transaction2.getObject(session1d);

    assertEquals(CDOUtil.getCDOObject(session1a2).cdoID(), CDOUtil.getCDOObject(session2d.eContainer()).cdoID());

    // Validate that the containment feature of D on both sessions are not null, and equal
    EReference ownedC_Feature = getModel6Package().getB_OwnedC();
    assertEquals(ownedC_Feature, session1d.eContainmentFeature());
    assertEquals(ownedC_Feature, session2d.eContainmentFeature());
  }
}
