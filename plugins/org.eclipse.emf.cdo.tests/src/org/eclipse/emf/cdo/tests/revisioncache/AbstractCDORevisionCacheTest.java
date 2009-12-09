/**
 * Copyright (c) 2004 - 2009 Andre Dietisheim (Bern, Switzerland) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.internal.db.cache.DBRevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.cache.lru.LRURevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.mem.MEMRevisionCache;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.impl.AddressImpl;
import org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.util.List;

/**
 * An abstract superclass that may be subclassed to test behaviour common to all CDORevisionCaches
 * 
 * @author Andre Dietisheim
 * @see CDORevisionCache
 * @see DBRevisionCache
 * @see LRURevisionCache
 * @see MEMRevisionCache
 * @see DerbyDBRevisionCacheTest
 * @see H2DBRevisionCacheTest
 * @see LRURevisionCacheTest
 * @see MEMRevisionCacheTest
 * @see DefaultRevisionCacheTest
 */
public abstract class AbstractCDORevisionCacheTest extends AbstractOMTest
{
  private static final String RESOURCE_PATH = "/res1";

  private static final int MAX_THREADS = 10;

  private CDOResource resource;

  private CDORevisionCache revisionCache;

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
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision cdoRevision = company.cdoRevision();
    revisionCache.addRevision(cdoRevision);

    CDOID cdoID = ((CDOObject)company).cdoID();
    CDORevision fetchedCDORevision = revisionCache.getRevision(cdoID);
    assertTrue(CDOIDUtil.equals(cdoRevision.getID(), fetchedCDORevision.getID()));
  }

  public void testGetRevisionReturnsLatestVersion()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    assertEquals(2, secondRevision.getVersion());
    revisionCache.addRevision(secondRevision);

    CDORevision fetchedCDORevision = revisionCache.getRevision(company.cdoID());
    assertEquals(2, fetchedCDORevision.getVersion());
  }

  public void testAddedRevisionIsNotRevised()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    CDOID cdoID = company.cdoID();
    CDORevision fetchedRevision = revisionCache.getRevision(cdoID);
    assertTrue(fetchedRevision.getRevised() == 0);
  }

  public void testFormerVersionIsGettable()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    // add new version
    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    revisionCache.addRevision(secondRevision);

    // fetch older version and check version and ID equality
    CDOID cdoID = company.cdoID();
    CDORevision fetchedRevision = revisionCache.getRevisionByVersion(cdoID, firstRevision.getVersion());
    assertNotNull(fetchedRevision);
    assertTrue(firstRevision.getID().equals(fetchedRevision.getID()));
    assertTrue(firstRevision.getVersion() == fetchedRevision.getVersion());
  }

  public void testAddRevisionUpdatesRevisedTimeStampOfLastRevision()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    CDOID cdoID = company.cdoID();

    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    CDORevision fetchedRevision = revisionCache.getRevision(cdoID);
    assertTrue(fetchedRevision.getRevised() == 0);

    // add new version
    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    // fetch older version and check revised timestamp
    fetchedRevision = revisionCache.getRevisionByVersion(cdoID, firstVersion.getVersion());
    assertTrue(fetchedRevision.getRevised() != 0);
    assertTrue(fetchedRevision.getRevised() < secondVersion.getCreated());
    assertTrue(fetchedRevision.getRevised() == firstVersion.getRevised());
  }

  public void testTheFormerRevisionOf2VersionsMayBeFetchedByTimestamp()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    CDOID cdoID = ((CDOObject)company).cdoID();
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    // add new version
    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    revisionCache.addRevision(secondRevision);

    // add new version
    company.setName("CDO");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision thirdRevision = company.cdoRevision();
    revisionCache.addRevision(thirdRevision);

    // fetch version by timstamp check version and ID equality
    CDORevision fetchedRevision = revisionCache.getRevisionByTime(cdoID, secondRevision.getCreated());
    assertTrue(secondRevision.getID().equals(fetchedRevision.getID()));
    assertTrue(secondRevision.getVersion() == fetchedRevision.getVersion());
  }

  public void testGiven3ObjectsOf2TypesGetRevisionsReturns2Versions()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    revisionCache.addRevision(company.cdoRevision());

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    revisionCache.addRevision(company.cdoRevision());

    AddressImpl address = (AddressImpl)Model1Factory.eINSTANCE.createAddress();
    address.setStreet("Eigerplatz 4");
    resource.getContents().add(address);
    ((CDOTransaction)company.cdoView()).commit();
    revisionCache.addRevision(address.cdoRevision());

    List<CDORevision> revisionList = revisionCache.getRevisions();
    assertEquals(2, revisionList.size());
  }

  public void testReturnsRemovedVersionWhenRemoving()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    CDORevision removedRevision = revisionCache.removeRevision(firstVersion.getID(), firstVersion.getVersion());
    assertNotNull(removedRevision);
    assertEqualRevisions(firstVersion, removedRevision);
  }

  public void testRemovedRevisionIsRemovedFromCache()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.removeRevision(secondVersion.getID(), secondVersion.getVersion());
    assertNull(revisionCache.getRevisionByVersion(secondVersion.getID(), secondVersion.getVersion()));
  }

  public void testRemoveSecondRevisionResultsInNoActiveRevision()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.removeRevision(secondVersion.getID(), secondVersion.getVersion());
    CDORevision fetchedRevision = revisionCache.getRevision(firstVersion.getID());
    assertNull(fetchedRevision);
  }

  public void testRemovedRevisionIsNotGettableByTimeStamp()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.removeRevision(firstVersion.getID(), firstVersion.getVersion());
    CDORevision fetchedRevision = revisionCache.getRevisionByTime(firstVersion.getID(), firstVersion.getRevised() - 1);
    assertNull(fetchedRevision);
  }

  public void testClearedCacheDoesNotContainAnyRevisions()
  {
    CompanyImpl company = (CompanyImpl)createCompanyInResource("Puzzle");
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    ((CDOTransaction)company.cdoView()).commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.clear();
    CDORevision fetchedRevision = revisionCache.getRevisionByVersion(firstVersion.getID(), firstVersion.getVersion());
    assertNull(fetchedRevision);

    fetchedRevision = revisionCache.getRevisionByVersion(secondVersion.getID(), secondVersion.getVersion());
    assertNull(fetchedRevision);
  }

  public void testConcurrentAccess() throws Throwable
  {

    Runnable[] testCases = new Runnable[] {

    new Runnable()
    {
      public void run()
      {
        // transactions are not thread safe, open a new one, this is probably a bug
        // CDOObject company = createCompanyInResource("Puzzle", (CDOTransaction)resource.cdoView());
        CDOObject company = createCompanyInResource("Puzzle", session.openTransaction());
        CDORevision revision = company.cdoRevision();
        revisionCache.addRevision(revision);
        CDORevision fetchedRevision = revisionCache.getRevision(revision.getID());
        assertNotNull(fetchedRevision != null);
      }
    }, new Runnable()
    {
      public void run()
      {
        CDOObject company = createCompanyInResource("Puzzle", session.openTransaction());
        CDORevision revision = company.cdoRevision();
        revisionCache.addRevision(revision);
        CDORevision fetchedRevision = revisionCache.getRevisionByVersion(revision.getID(), revision.getVersion());
        assertEquals(revision.getVersion(), fetchedRevision.getVersion());
        assertEquals(revision.getCreated(), fetchedRevision.getCreated());
      }
    }, new Runnable()
    {
      public void run()
      {
        CDOObject company = createCompanyInResource("Puzzle", session.openTransaction());
        CDORevision revision = company.cdoRevision();
        revisionCache.addRevision(revision);
        revisionCache.removeRevision(revision.getID(), revision.getVersion());
      }
    }, new Runnable()
    {
      public void run()
      {
        revisionCache.getRevisions();
      }
    }, new Runnable()
    {
      public void run()
      {
        CDOObject company = createCompanyInResource("Puzzle", session.openTransaction());
        CDORevision revision = company.cdoRevision();
        revisionCache.addRevision(revision);
        CDORevision fetchedRevision = revisionCache.getRevisionByTime(revision.getID(), revision.getCreated());
        assertEquals(revision.getVersion(), fetchedRevision.getVersion());
        assertEquals(revision.getCreated(), fetchedRevision.getCreated());
        revisionCache.removeRevision(revision.getID(), revision.getVersion());
      }
    } };
    ConcurrentRunner.run(testCases, MAX_THREADS, 50);
  }

  private void assertEqualRevisions(CDORevision thisRevision, CDORevision thatRevision)
  {
    assertEquals(thisRevision.getVersion(), thatRevision.getVersion());
    assertEquals(thisRevision.getCreated(), thatRevision.getCreated());
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
    transaction.commit();
    return (CDOObject)company;
  }

  private CDOResource createResource()
  {
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(RESOURCE_PATH);
    transaction.commit();
    return resource;
  }

  protected abstract CDORevisionCache createRevisionCache(CDOSession session) throws Exception;
}
