/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Andras Peteri - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andras Peteri
 */
public class Bugzilla_500864_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testCommitDataAfterDeletion() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company1 = getModel1Factory().createCompany();
    company1.setName("TestFactory");

    Customer customer1 = getModel1Factory().createCustomer();
    customer1.setName("TestCustomer");

    company1.getCustomers().add(customer1);
    resource.getContents().add(company1);
    transaction.commit();

    CDOID company1Id = CDOUtil.getCDOObject(company1).cdoID();
    CDOID customer1Id = CDOUtil.getCDOObject(customer1).cdoID();

    resource.getContents().remove(company1);
    CDOCommitInfo commitInfoFromTransaction = transaction.commit();
    Set<CDOID> detachedIdsFromTransaction = toSet(commitInfoFromTransaction.getDetachedObjects());

    assertEquals(2, detachedIdsFromTransaction.size());
    assertTrue("Company CDOID should be in detached objects list.", detachedIdsFromTransaction.contains(company1Id));
    assertTrue("Customer CDOID should be in detached objects list.", detachedIdsFromTransaction.contains(customer1Id));

    CDOSession session2 = openSession();
    CDOCommitInfo commitInfoFromManager = session2.getCommitInfoManager().getCommitInfo(commitInfoFromTransaction.getTimeStamp());
    Set<CDOID> detachedIdsFromManager = toSet(commitInfoFromManager.getDetachedObjects());

    assertEquals(2, detachedIdsFromManager.size());
    assertTrue("Company CDOID should be in detached objects list.", detachedIdsFromManager.contains(company1Id));
    assertTrue("Customer CDOID should be in detached objects list.", detachedIdsFromManager.contains(customer1Id));

    session1.close();
    session2.close();
  }

  private Set<CDOID> toSet(List<CDOIDAndVersion> detachedObjects)
  {
    Set<CDOID> ids = new HashSet<>();
    for (CDOIDAndVersion cdoIdAndVersion : detachedObjects)
    {
      ids.add(cdoIdAndVersion.getID());
    }

    return ids;
  }
}
