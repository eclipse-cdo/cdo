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

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * Clone Repository doesn't retry to init root resource
 * <p>
 * See bug 376566.
 *
 * @author Eike Stepper
 */
public class Bugzilla_376566_Test extends AbstractSyncingTest
{
  @Override
  protected boolean isHinderInitialReplication()
  {
    return true;
  }

  public void testInitCloneWithoutMaster() throws Exception
  {
    InternalRepository clone = getRepository();
    sleep(2000);

    LifecycleUtil.deactivate(clone);
    getOfflineConfig().startMasterTransport();

    clone = getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();
  }
}
