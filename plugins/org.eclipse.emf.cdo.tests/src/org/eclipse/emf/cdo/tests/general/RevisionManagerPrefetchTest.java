/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Regression tests for containment prefetching of missing revisions through the revision-loading protocol.
 *
 * @author Eike Stepper
 */
public class RevisionManagerPrefetchTest extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.SUPPORTING_AUDITS, "false");
  }

  public void testMissingRevisionWithPrefetch() throws Exception
  {
    Fixture fixture = createFixture();
    assertMissingRevision(fixture);

    CDORevision revision = fixture.revisionManager.request().lookupLoaderOnly().prefetchDepthOne().getRevision(fixture.missingID, fixture.branchPoint);
    assertNull(revision);
  }

  public void testMixedExistingAndMissingRevisionsWithPrefetch() throws Exception
  {
    Fixture fixture = createFixture();
    assertMissingRevision(fixture);

    List<CDORevision> revisions = fixture.revisionManager.request().lookupLoaderOnly().prefetchDepthOne()
        .getRevisions(Arrays.asList(fixture.companyID, fixture.missingID), fixture.branchPoint);

    assertEquals(2, revisions.size());
    assertNotNull(revisions.get(0));
    assertEquals(fixture.companyID, revisions.get(0).getID());
    assertNull(revisions.get(1));
  }

  public void testSuccessfulContainmentPrefetch() throws Exception
  {
    Fixture fixture = createFixture();

    CDORevision companyRevision = fixture.revisionManager.request().lookupLoaderOnly().prefetchDepthOne().getRevision(fixture.companyID, fixture.branchPoint);

    assertNotNull(companyRevision);
    assertEquals(fixture.companyID, companyRevision.getID());

    CDORevision customerRevision = fixture.revisionManager.request().lookupCacheOnly().getRevision(fixture.customerID, fixture.branchPoint);
    assertNotNull(customerRevision);
    assertEquals(fixture.customerID, customerRevision.getID());
  }

  private Fixture createFixture() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/revision-prefetch"));

    Company company = getModel1Factory().createCompany();
    company.setName("company");
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");
    company.getCustomers().add(customer);
    resource.getContents().add(company);
    Company missingCompany = getModel1Factory().createCompany();
    missingCompany.setName("missing");
    resource.getContents().add(missingCompany);
    transaction.commit();

    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    CDOID missingID = CDOUtil.getCDOObject(missingCompany).cdoID();
    resource.getContents().remove(missingCompany);
    transaction.commit();

    CDOBranchPoint branchPoint = transaction.getBranch().getHead();
    return new Fixture(session.getRevisionManager(), branchPoint, companyID, customerID, missingID);
  }

  private void assertMissingRevision(Fixture fixture)
  {
    CDORevision revision = fixture.revisionManager.request().lookupLoaderOnly().prefetchDepthNone().getRevision(fixture.missingID, fixture.branchPoint);
    assertNull("The detached object's repository-generated ID must be a valid missing revision ID", revision);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Fixture
  {
    private final CDORevisionManager revisionManager;

    private final CDOBranchPoint branchPoint;

    private final CDOID companyID;

    private final CDOID customerID;

    private final CDOID missingID;

    private Fixture(CDORevisionManager revisionManager, CDOBranchPoint branchPoint, CDOID companyID, CDOID customerID, CDOID missingID)
    {
      this.revisionManager = revisionManager;
      this.branchPoint = branchPoint;
      this.companyID = companyID;
      this.customerID = customerID;
      this.missingID = missingID;
    }
  }
}
