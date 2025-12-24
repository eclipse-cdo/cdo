/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Egidijus Vaisnora - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Bug 330052 - Breakage related to sticky views
 *
 * @author Egidijus Vaisnora, Caspar De Groot
 */
public class Bugzilla_330052_Test extends AbstractCDOTest
{
  /**
   * Tests whether another view in the same session can retrieve the newly committed object
   */
  public void test_otherView() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOTransaction tx = session.openTransaction();
    CDOView view = session.openView();

    CDOResource resource = tx.createResource(getResourcePath("test"));
    Address address = getModel1Factory().createAddress();
    resource.getContents().add(address);
    tx.commit();

    CDOObject object = view.getObject(CDOUtil.getCDOObject(address).cdoID());
    assertNotNull(object);
    session.close();
  }

  /**
   * Tests whether an audit view in the same session fetches the correct historical version
   */
  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void test_auditView() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("test"));
    Address address = getModel1Factory().createAddress();
    final String testName1 = "name1";
    address.setName(testName1);
    resource.getContents().add(address);
    long commitTime = transaction.commit().getTimeStamp();

    final String testName2 = "name2";
    address.setName(testName2);
    transaction.commit();

    CDOView view = session.openView(commitTime);
    Address historicalAddress = (Address)CDOUtil.getEObject(view.getObject(CDOUtil.getCDOObject(address).cdoID()));

    assertEquals(testName1, historicalAddress.getName());

    session.close();
  }
}
