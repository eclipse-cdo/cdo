/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * "Durable locking is not enabled for view..." while doing a rollback on a clone
 * <p>
 * See bug 376470.
 *
 * @author Eike Stepper
 */
public class Bugzilla_376470_Test extends AbstractSyncingTest
{
  public void testRollbackWhileOnline() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.setCommitComment("resource with one company created on clone");
    transaction.rollback();
  }

  public void testRollbackWhileOnline_NoAutoReleaseLocks() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(false);
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.setCommitComment("resource with one company created on clone");
    transaction.rollback();
  }

  public void testRollbackWhileOffline() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.setCommitComment("resource with one company created on clone");
    transaction.rollback();
  }

  public void testRollbackWhileOffline_NoAutoReleaseLocks() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(false);
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.setCommitComment("resource with one company created on clone");
    transaction.rollback();
  }
}
