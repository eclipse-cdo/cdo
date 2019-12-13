/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.impl.AddressImpl;
import org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;
import org.eclipse.net4j.util.tests.ConcurrentRunner;

import java.util.List;

/**
 * An abstract superclass that may be subclassed to test behavior common to all CDORevisionCaches
 *
 * @author Andre Dietisheim
 * @see CDORevisionCache
 * @see DefaultRevisionCacheTest
 */
public abstract class AbstractRevisionCacheTest extends AbstractOMTest
{
  private static final String RESOURCE_PATH = "/res1";

  private static final String COMPANY = "Eclipse";

  private static final int MAX_THREADS = 10;

  private static final CDOBranch BRANCH = null;

  private static final CDOBranchPoint BRANCH_POINT = BRANCH.getHead();

  private CDOResource resource;

  private InternalCDORevisionCache revisionCache;

  private CDOSession session;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    Session sessionHolder = new Session();
    LifecycleUtil.activate(sessionHolder);
    session = sessionHolder.getSession(Model1Package.eINSTANCE);
    resource = createResource();
    revisionCache = createRevisionCache(session);
    LifecycleUtil.activate(revisionCache);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(session);
    LifecycleUtil.deactivate(revisionCache);
    super.doTearDown();
  }

  public void testAddedRevisionIsGettable()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision cdoRevision = company.cdoRevision();
    addRevision(cdoRevision);

    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    CDORevision fetchedCDORevision = revisionCache.getRevision(id, BRANCH_POINT);
    assertEquals(true, CDOIDUtil.equals(cdoRevision.getID(), fetchedCDORevision.getID()));
  }

  private void addRevision(CDORevision revision)
  {
    revisionCache.addRevision(revision);
  }

  public void testGetRevisionReturnsLatestVersion() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstRevision = company.cdoRevision();
    addRevision(firstRevision);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    assertEquals(2, secondRevision.getVersion());
    addRevision(secondRevision);

    CDORevision fetchedCDORevision = revisionCache.getRevision(company.cdoID(), BRANCH_POINT);
    assertEquals(2, fetchedCDORevision.getVersion());
  }

  public void testAddedRevisionIsNotRevised() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstRevision = company.cdoRevision();
    addRevision(firstRevision);

    CDOID id = company.cdoID();
    CDORevision fetchedRevision = revisionCache.getRevision(id, BRANCH_POINT);
    assertEquals(true, fetchedRevision.getRevised() == 0);
  }

  public void testFormerVersionIsGettable() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstRevision = company.cdoRevision();
    addRevision(firstRevision);

    // add new version
    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    addRevision(secondRevision);

    // fetch older version and check version and ID equality
    CDOID id = company.cdoID();
    CDORevision fetchedRevision = revisionCache.getRevisionByVersion(id, BRANCH.getVersion(firstRevision.getVersion()));
    assertNotNull(fetchedRevision);
    assertEquals(true, firstRevision.getID().equals(fetchedRevision.getID()));
    assertEquals(true, firstRevision.getVersion() == fetchedRevision.getVersion());
  }

  public void testAddRevisionUpdatesRevisedTimeStampOfLastRevision() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    CDOID id = company.cdoID();

    InternalCDORevision firstVersion = company.cdoRevision();
    addRevision(firstVersion);

    CDORevision fetchedRevision = revisionCache.getRevision(id, BRANCH_POINT);
    assertEquals(true, fetchedRevision.getRevised() == 0);

    // add new version
    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    addRevision(secondVersion);

    // fetch older version and check revised timestamp
    fetchedRevision = revisionCache.getRevisionByVersion(id, BRANCH.getVersion(firstVersion.getVersion()));
    assertEquals(true, fetchedRevision.getRevised() != 0);
    assertEquals(true, fetchedRevision.getRevised() < secondVersion.getTimeStamp());
    assertEquals(true, fetchedRevision.getRevised() == firstVersion.getRevised());
  }

  public void testTheFormerRevisionOf2VersionsMayBeFetchedByTimestamp() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    InternalCDORevision firstRevision = company.cdoRevision();
    addRevision(firstRevision);

    // add new version
    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    addRevision(secondRevision);

    // add new version
    company.setName("CDO");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision thirdRevision = company.cdoRevision();
    addRevision(thirdRevision);

    // fetch version by timstamp check version and ID equality
    CDORevision fetchedRevision = revisionCache.getRevision(id, BRANCH_POINT);
    assertEquals(true, secondRevision.getID().equals(fetchedRevision.getID()));
    assertEquals(true, secondRevision.getVersion() == fetchedRevision.getVersion());
  }

  public void testGiven3ObjectsOf2TypesGetRevisionsReturns2Versions() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    addRevision(company.cdoRevision());

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    addRevision(company.cdoRevision());

    AddressImpl address = (AddressImpl)Model1Factory.eINSTANCE.createAddress();
    address.setStreet("Eigerplatz 4");
    resource.getContents().add(address);
    ((CDOTransaction)company.cdoView()).commit();
    addRevision(address.cdoRevision());

    List<CDORevision> revisionList = revisionCache.getCurrentRevisions();
    assertEquals(2, revisionList.size());
  }

  public void testReturnsRemovedVersionWhenRemoving() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstVersion = company.cdoRevision();
    addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    addRevision(secondVersion);

    CDORevision removedRevision = revisionCache.removeRevision(firstVersion.getID(), firstVersion.getBranch().getVersion(firstVersion.getVersion()));
    assertNotNull(removedRevision);
    assertEqualRevisions(firstVersion, removedRevision);
  }

  public void testRemovedRevisionIsRemovedFromCache() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstVersion = company.cdoRevision();
    addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    addRevision(secondVersion);

    revisionCache.removeRevision(secondVersion.getID(), secondVersion.getBranch().getVersion(secondVersion.getVersion()));
    assertNull(revisionCache.getRevisionByVersion(secondVersion.getID(), BRANCH.getVersion(secondVersion.getVersion())));
  }

  public void testRemoveSecondRevisionResultsInNoActiveRevision() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstVersion = company.cdoRevision();
    addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    addRevision(secondVersion);

    revisionCache.removeRevision(secondVersion.getID(), secondVersion.getBranch().getVersion(secondVersion.getVersion()));
    CDORevision fetchedRevision = revisionCache.getRevision(firstVersion.getID(), BRANCH_POINT);
    assertNull(fetchedRevision);
  }

  public void testRemovedRevisionIsNotGettableByTimeStamp() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstVersion = company.cdoRevision();
    addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    addRevision(secondVersion);

    revisionCache.removeRevision(firstVersion.getID(), firstVersion);
    CDORevision fetchedRevision = revisionCache.getRevision(firstVersion.getID(), BRANCH_POINT);
    assertNull(fetchedRevision);
  }

  public void testClearedCacheDoesNotContainAnyRevisions() throws Exception
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource(COMPANY);
    InternalCDORevision firstVersion = company.cdoRevision();
    addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    addRevision(secondVersion);

    revisionCache.clear();
    CDORevision fetchedRevision = revisionCache.getRevisionByVersion(firstVersion.getID(), BRANCH.getVersion(firstVersion.getVersion()));
    assertNull(fetchedRevision);

    fetchedRevision = revisionCache.getRevisionByVersion(secondVersion.getID(), BRANCH.getVersion(secondVersion.getVersion()));
    assertNull(fetchedRevision);
  }

  public void testConcurrentAccess() throws Throwable
  {
    Runnable[] testCases = new Runnable[] {

        new Runnable()
        {
          @Override
          public void run()
          {
            CDOObject company = createCompanyInResource(COMPANY, session.openTransaction());
            CDORevision revision = company.cdoRevision();
            addRevision(revision);
            CDORevision fetchedRevision = revisionCache.getRevision(revision.getID(), BRANCH_POINT);
            assertNotNull(fetchedRevision != null);
          }
        } //

        , new Runnable()
        {
          @Override
          public void run()
          {
            CDOObject company = createCompanyInResource(COMPANY, session.openTransaction());
            CDORevision revision = company.cdoRevision();
            addRevision(revision);
            CDORevision fetchedRevision = revisionCache.getRevisionByVersion(revision.getID(), BRANCH.getVersion(revision.getVersion()));
            assertEquals(revision.getVersion(), fetchedRevision.getVersion());
            assertEquals(revision.getTimeStamp(), fetchedRevision.getTimeStamp());
          }
        } //

        , new Runnable()
        {
          @Override
          public void run()
          {
            CDOObject company = createCompanyInResource(COMPANY, session.openTransaction());
            CDORevision revision = company.cdoRevision();
            addRevision(revision);
            revisionCache.removeRevision(revision.getID(), revision);
          }
        } //

        , new Runnable()
        {
          @Override
          public void run()
          {
            revisionCache.getCurrentRevisions();
          }
        } //

        , new Runnable()
        {
          @Override
          public void run()
          {
            CDOObject company = createCompanyInResource(COMPANY, session.openTransaction());
            CDORevision revision = company.cdoRevision();
            addRevision(revision);
            CDORevision fetchedRevision = revisionCache.getRevision(revision.getID(), BRANCH_POINT);
            assertEquals(revision.getVersion(), fetchedRevision.getVersion());
            assertEquals(revision.getTimeStamp(), fetchedRevision.getTimeStamp());
            revisionCache.removeRevision(revision.getID(), revision);
          }
        } };

    ConcurrentRunner.run(testCases, MAX_THREADS, 50);
  }

  private void assertEqualRevisions(CDORevision thisRevision, CDORevision thatRevision)
  {
    assertEquals(thisRevision.getVersion(), thatRevision.getVersion());
    assertEquals(thisRevision.getTimeStamp(), thatRevision.getTimeStamp());
    assertEquals(thisRevision.getRevised(), thatRevision.getRevised());
  }

  private CDOObject createCompanyInResource(String name)
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    return createCompanyInResource(name, transaction);
  }

  private CDOObject createCompanyInResource(String name, CDOTransaction transaction)
  {
    Company company = Model1Factory.eINSTANCE.createCompany();
    company.setName(name);
    resource.getContents().add(company);

    try
    {
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    return CDOUtil.getCDOObject(company);
  }

  private CDOResource createResource()
  {
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(RESOURCE_PATH);

    try
    {
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    return resource;
  }

  protected abstract InternalCDORevisionCache createRevisionCache(CDOSession session) throws Exception;
}
