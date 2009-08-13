/**
 * Copyright (c) 2004 - 2009 Andre Dietisheim (Bern, Switzerland) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.impl.AddressImpl;
import org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.List;

/**
 * @author Andre Dietisheim
 */
public abstract class AbstractCDORevisionCacheTest extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/res1";

  private CDOResource resource;

  private CDORevisionCache revisionCache;

  @Override
  protected void doSetUp() throws Exception
  {
    skipUnlessConfig(MEM);
    super.doSetUp();

    InternalCDOSession session = (InternalCDOSession)openSession();
    resource = createResource(session);
    revisionCache = createRevisionCache(session);
    LifecycleUtil.activate(revisionCache);
  }

  private CDOResource createResource(CDOSession session)
  {
    InternalCDOTransaction transaction = (InternalCDOTransaction)session.openTransaction();
    return transaction.createResource(RESOURCE_PATH);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(revisionCache);
    LifecycleUtil.deactivate(resource.cdoView().getSession());
    super.doTearDown();
  }

  public void testAddedRevisionIsGettable()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    company.setName("Puzzle");
    resource.getContents().add(company);
    transaction.commit();
    InternalCDORevision cdoRevision = company.cdoRevision();
    revisionCache.addRevision(cdoRevision);

    CDOID cdoID = ((CDOObject)company).cdoID();
    CDORevision fetchedCDORevision = revisionCache.getRevision(cdoID);
    assertTrue(CDOIDUtil.equals(cdoRevision.getID(), fetchedCDORevision.getID()));
  }

  public void testGetRevisionReturnsLatestVersion()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    company.setName("Puzzle");
    resource.getContents().add(company);
    transaction.commit();
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    revisionCache.addRevision(secondRevision);

    CDORevision fetchedCDORevision = revisionCache.getRevision(company.cdoID());
    assertEquals(2, fetchedCDORevision.getVersion());
  }

  public void testAddedRevisionIsNotRevised()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    company.setName("Puzzle");
    resource.getContents().add(company);
    transaction.commit();
    CDOID cdoID = ((CDOObject)company).cdoID();
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    CDORevision fetchedRevision = revisionCache.getRevision(cdoID);
    assertTrue(fetchedRevision.getRevised() == 0);
  }

  public void testFormerVersionIsGettable()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    company.setName("Puzzle");
    resource.getContents().add(company);
    transaction.commit();
    CDOID cdoID = ((CDOObject)company).cdoID();
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    // add new version
    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    revisionCache.addRevision(secondRevision);

    // fetch older version and check version and ID equality
    CDORevision fetchedRevision = revisionCache.getRevisionByVersion(cdoID, firstRevision.getVersion());
    assertNotNull(fetchedRevision);
    assertTrue(firstRevision.getID().equals(fetchedRevision.getID()));
    assertTrue(firstRevision.getVersion() == fetchedRevision.getVersion());
  }

  public void testAddRevisionUpdatesRevisedTimeStampOfLastRevision()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    company.setName("Puzzle");
    resource.getContents().add(company);
    transaction.commit();
    CDOID cdoID = ((CDOObject)company).cdoID();

    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    CDORevision fetchedRevision = revisionCache.getRevision(cdoID);
    assertTrue(fetchedRevision.getRevised() == 0);

    // add new version
    company.setName("Andre");
    transaction.commit();
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
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    company.setName("Puzzle");
    resource.getContents().add(company);
    transaction.commit();
    CDOID cdoID = ((CDOObject)company).cdoID();
    InternalCDORevision firstRevision = company.cdoRevision();
    revisionCache.addRevision(firstRevision);

    // add new version
    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondRevision = company.cdoRevision();
    revisionCache.addRevision(secondRevision);

    // add new version
    company.setName("CDO");
    transaction.commit();
    InternalCDORevision thirdRevision = company.cdoRevision();
    revisionCache.addRevision(thirdRevision);

    // fetch version by timstampt check version and ID equality
    CDORevision fetchedRevision = revisionCache.getRevisionByTime(cdoID, secondRevision.getCreated());
    assertTrue(secondRevision.getID().equals(fetchedRevision.getID()));
    assertTrue(secondRevision.getVersion() == fetchedRevision.getVersion());
  }

  public void testGiven3ObjectsOf2TypesGetRevisionsReturns2Versions()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    resource.getContents().add(company);

    company.setName("Puzzle");
    transaction.commit();
    revisionCache.addRevision(company.cdoRevision());

    company.setName("Andre");
    transaction.commit();
    revisionCache.addRevision(company.cdoRevision());

    AddressImpl address = (AddressImpl)getModel1Factory().createAddress();
    address.setStreet("Eigerplatz 4");
    resource.getContents().add(address);
    transaction.commit();

    revisionCache.addRevision(address.cdoRevision());

    List<CDORevision> revisionList = revisionCache.getRevisions();
    assertEquals(2, revisionList.size());
  }

  public void testReturnsRemovedVersionWhenRemoving()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    resource.getContents().add(company);

    company.setName("Puzzle");
    transaction.commit();
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    CDORevision removedRevision = revisionCache.removeRevision(firstVersion.getID(), firstVersion.getVersion());
    assertNotNull(removedRevision);
    assertEqualRevisions(firstVersion, removedRevision);
  }

  public void testRemovedRevisionIsRemovedFromCache()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    resource.getContents().add(company);

    company.setName("Puzzle");
    transaction.commit();
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.removeRevision(secondVersion.getID(), secondVersion.getVersion());
    assertNull(revisionCache.getRevisionByVersion(secondVersion.getID(), secondVersion.getVersion()));
  }

  public void testRemoveSecondRevisionResultsInNoActiveRevision()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    resource.getContents().add(company);

    company.setName("Puzzle");
    transaction.commit();
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.removeRevision(secondVersion.getID(), secondVersion.getVersion());
    CDORevision fetchedRevision = revisionCache.getRevision(firstVersion.getID());
    assertNull(fetchedRevision);
  }

  public void testRemovedRevisionIsNotGettableByTimeStamp()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    resource.getContents().add(company);

    company.setName("Puzzle");
    transaction.commit();
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.removeRevision(firstVersion.getID(), firstVersion.getVersion());
    CDORevision fetchedRevision = revisionCache.getRevisionByTime(firstVersion.getID(), firstVersion.getRevised() - 1);
    assertNull(fetchedRevision);
  }

  public void testClearedCacheDoesNotContainAnyRevisions()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    resource.getContents().add(company);

    company.setName("Puzzle");
    transaction.commit();
    InternalCDORevision firstVersion = company.cdoRevision();
    revisionCache.addRevision(firstVersion);

    company.setName("Andre");
    transaction.commit();
    InternalCDORevision secondVersion = company.cdoRevision();
    revisionCache.addRevision(secondVersion);

    revisionCache.clear();
    CDORevision fetchedRevision = revisionCache.getRevisionByVersion(firstVersion.getID(), firstVersion.getVersion());
    assertNull(fetchedRevision);

    fetchedRevision = revisionCache.getRevisionByVersion(secondVersion.getID(), secondVersion.getVersion());
    assertNull(fetchedRevision);
  }

  private void assertEqualRevisions(CDORevision thisRevision, CDORevision thatRevision)
  {
    assertEquals(thisRevision.getVersion(), thatRevision.getVersion());
    assertEquals(thisRevision.getCreated(), thatRevision.getCreated());
    assertEquals(thisRevision.getRevised(), thatRevision.getRevised());
  }

  protected CDOResource getResource()
  {
    return resource;
  }

  @SuppressWarnings("unused")
  private boolean isTestFor(String testName)
  {
    assertNotNull(testName);
    return getClass().getSimpleName().toLowerCase().indexOf(testName.toLowerCase()) >= 0;
  }

  protected abstract CDORevisionCache createRevisionCache(CDOSession session) throws Exception;

}
