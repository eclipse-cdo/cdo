/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */

package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * Bug 315026: Local rollback inadvertently brings in updates from other sessions
 *
 * @author Caspar De Groot
 */
public class Bugzilla_315026_Test extends AbstractCDOTest
{
  private static final String ORIGINAL_NAME = "AAA";

  private static final String DIRTY_NAME = "BBB";

  private static final String OTHER_NAME = "CCC";

  public void test() throws Exception
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(getResourcePath("/r1")); //$NON-NLS-1$

    // Create clean content
    Company company = Model1Factory.eINSTANCE.createCompany();
    company.setName(ORIGINAL_NAME);
    r1.getContents().add(company);
    tx.commit();

    // Make it dirty in this session
    company.setName(DIRTY_NAME);

    // Update and commit in another session
    doSecondSession();

    // Rollback this session
    tx.rollback();

    // Clear this session's revision cache
    ((InternalCDORevisionManager)session.getRevisionManager()).getCache().clear();

    // Verify that value in this session does *not* match value assigned in 2nd session
    String name = company.getName();
    assertEquals("Should not have the value committed by the other session", false, OTHER_NAME.equals(name));

    // Verify that value in this session still matches value assigned in this session
    assertEquals("Should have the value originally loaded in this session", ORIGINAL_NAME, name);
  }

  private void doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(getResourcePath("/r1")); //$NON-NLS-1$

    Company company = (Company)r1.getContents().get(0);
    company.setName(OTHER_NAME);
    tx.commit();

    tx.close();
    session.close();
  }
}
