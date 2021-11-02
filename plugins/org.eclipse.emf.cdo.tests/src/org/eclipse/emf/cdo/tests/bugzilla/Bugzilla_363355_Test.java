/*
 * Copyright (c) 2014, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.resource.Resource;

import java.util.Collections;

/**
 * @author Esteban Dugueperoux
 *
 * Test about remote detachment.
 */
public class Bugzilla_363355_Test extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test1";

  private CDOSession sessionOfUser1;

  private CDOTransaction transactionOfUser1;

  private ContainmentObject co1;

  private ContainmentObject co2;

  private ContainmentObject co3;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    sessionOfUser1 = openSession();
    transactionOfUser1 = sessionOfUser1.openTransaction();
    CDOResource resourceOfUser1 = transactionOfUser1.createResource(getResourcePath(RESOURCE_PATH));

    co1 = getModel6Factory().createContainmentObject();

    co2 = getModel6Factory().createContainmentObject();
    co1.setContainmentOptional(co2);

    co3 = getModel6Factory().createContainmentObject();
    co2.setContainmentOptional(co3);

    resourceOfUser1.getContents().add(co1);
    resourceOfUser1.save(Collections.emptyMap());
  }

  /**
   * Test about remotely detached object make CDOTransaction dirty
   * in invalidation while it shouldn't.
   *
   * @throws Exception
   */
  public void testCDOTransactionDirtyOnInvalidation() throws Exception
  {
    // User 2 do changes
    CDOSession sessionOfUser2 = openSession();
    CDOTransaction transactionOfUser2 = sessionOfUser2.openTransaction();
    transactionOfUser2.options().setAutoReleaseLocksEnabled(false);

    Resource resourceOfUser2 = transactionOfUser2.getResource(getResourcePath(RESOURCE_PATH));
    ContainmentObject co1_OfUser2 = (ContainmentObject)resourceOfUser2.getContents().get(0);

    ContainmentObject co2_OfUser2 = (ContainmentObject)co1_OfUser2.getContainmentOptional();

    ContainmentObject newCO3FromUser2 = getModel6Factory().createContainmentObject();
    co2_OfUser2.setContainmentOptional(newCO3FromUser2);

    commitAndSync(transactionOfUser2, transactionOfUser1);

    // User 1 checks that its transaction is not dirty
    assertFalse("Receiving remote changes shouldn't set local CDOTransaction dirty", transactionOfUser1.isDirty());

    commitAndSync(transactionOfUser1, transactionOfUser2);

    transactionOfUser2.close();
    sessionOfUser2.close();
  }

  /**
   * Because we have found a scenario for which the initial
   * fix, i.e. manage detached objects before changed objects in invalidation,
   * cause a regression "Cannot modify a frozen revisison" in invalidation in
   * legacy.
   * <p>
   * Scenario :
   * <ol>
   * <li>User1 have a ContainmentObject co1 containing a ContainmentObject co2 and this last contains a ContainmentObject co3</li>
   * <li>User1 set a new co2 having a its own co3 to co1 and move
   * the original co3 of the old co2 to the new co2 and commit</li>
   * <li>User2 get a "Cannot modify a frozen revision" exception in
   * invalidation</li>
   * </ol>
   * </p>
   *
   * @throws Exception
   */
  public void testCannotModifyFrozenRevisionOnInvalidation() throws Exception
  {
    // User 2 do changes
    CDOSession sessionOfUser2 = openSession();
    CDOTransaction transactionOfUser2 = sessionOfUser2.openTransaction();
    transactionOfUser2.options().setAutoReleaseLocksEnabled(false);

    Resource resourceOfUser2 = transactionOfUser2.getResource(getResourcePath(RESOURCE_PATH));
    ContainmentObject co1_OfUser2 = (ContainmentObject)resourceOfUser2.getContents().get(0);
    ContainmentObject co2_OfUser2 = (ContainmentObject)co1_OfUser2.getContainmentOptional();
    co2_OfUser2.getContainmentOptional();

    ContainmentObject newCO2 = getModel6Factory().createContainmentObject();
    ContainmentObject newCO3 = getModel6Factory().createContainmentObject();
    newCO2.setContainmentOptional(newCO3);
    co1_OfUser2.setContainmentOptional(newCO2);

    ContainmentObject oldCO3 = (ContainmentObject)co2_OfUser2.getContainmentOptional();
    newCO2.setContainmentOptional(oldCO3);

    commitAndSync(transactionOfUser2, transactionOfUser1);

    // Asserts on user1 transaction, receiving the changes committed by
    // user2
    assertNotSame(co2, co3.eContainer());
    assertSame(co1.getContainmentOptional(), co3.eContainer());
    assertNull(co2.eContainer());
    assertFalse("Receiving remote changes shouldn't set local CDOTransaction dirty", transactionOfUser2.isDirty());

    transactionOfUser2.close();
    sessionOfUser2.close();
  }

  @Override
  public void tearDown() throws Exception
  {
    transactionOfUser1.close();
    transactionOfUser1 = null;
    sessionOfUser1.close();
    sessionOfUser1 = null;
    co1 = null;
    co2 = null;
    co3 = null;
    super.tearDown();
  }

}
