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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.db.CDOCommonDBUtil;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.internal.db.cache.DBRevisionCache;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.model1.impl.AddressImpl;
import org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andre Dietisheim
 */
public class DBRevisionCacheTest extends AbstractCDOTest
{
  private static final String DB_NAME = "/temp/dbRevisionCache1";

  private static final String RESOURCE_PATH = "/res1";

  private DBRevisionCache revisionCache;

  private CDOResource resource;

  @Override
  protected void doSetUp() throws Exception
  {
    skipUnlessConfig(MEM);
    super.doSetUp();

    InternalCDOSession session = (InternalCDOSession)openSession();
    InternalCDOTransaction transaction = (InternalCDOTransaction)session.openTransaction();
    resource = transaction.createResource(RESOURCE_PATH);

    // TODO: replace by Configuration
    IDBProvider dbProvider = new H2DBProvider();
    DataSource dataSource = dbProvider.createDataSource();
    Connection connection = dataSource.getConnection();

    try
    {
      dbProvider.dropAllTables(connection);
    }
    finally
    {
      DBUtil.close(connection);
    }

    revisionCache = (DBRevisionCache)CDOCommonDBUtil.createDBCache(//
        new H2Adapter() //
        , DBUtil.createConnectionProvider(dataSource)//
        , CDOListFactory.DEFAULT//
        , session.getPackageRegistry() //
        , session.getRevisionManager().getFactory());
    LifecycleUtil.activate(revisionCache);
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
    InternalCDORevision fetchedCDORevision = revisionCache.getRevision(cdoID);
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

    InternalCDORevision fetchedCDORevision = revisionCache.getRevision(company.cdoID());
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

    InternalCDORevision fetchedRevision = revisionCache.getRevision(cdoID);
    assertTrue(fetchedRevision.getRevised() == 0);
  }

  public void testAddedResourceIsGettable()
  {
    CDOTransaction transaction = (CDOTransaction)resource.cdoView();
    String containerName = "container1";
    CDOResourceImpl resource = (CDOResourceImpl)transaction.createResource("/folder/" + containerName);
    transaction.commit();

    InternalCDORevision firstVersion = resource.cdoRevision();
    revisionCache.addRevision(firstVersion);

    CompanyImpl company = (CompanyImpl)getModel1Factory().createCompany();
    company.setName("Puzzle");
    resource.getContents().add(company);
    transaction.commit();

    InternalCDORevision secondVersion = resource.cdoRevision();
    revisionCache.addRevision(secondVersion);

    // TODO getResourceID() is obsolete. Do we still need this test case?
    // CDOID resourceID = revisionCache.getResourceID(resource.cdoID(), containerName, firstVersion.getCreated());
    // assertTrue(resource.cdoID().equals(resourceID));
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
    InternalCDORevision fetchedRevision = revisionCache.getRevisionByVersion(cdoID, firstRevision.getVersion());
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

    InternalCDORevision fetchedRevision = revisionCache.getRevision(cdoID);
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
    InternalCDORevision fetchedRevision = revisionCache.getRevisionByTime(cdoID, secondRevision.getCreated());
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

  public void testRevisionMayBeRemoved()
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

    InternalCDORevision removedRevision = revisionCache.removeRevision(firstVersion.getID(), firstVersion.getVersion());
    assertEquals(firstVersion, removedRevision);
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
    InternalCDORevision fetchedRevision = revisionCache.getRevision(firstVersion.getID());
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
    InternalCDORevision fetchedRevision = revisionCache.getRevisionByTime(firstVersion.getID(), firstVersion
        .getRevised() - 1);
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
    InternalCDORevision fetchedRevision = revisionCache.getRevisionByVersion(firstVersion.getID(), firstVersion
        .getVersion());
    assertNull(fetchedRevision);

    fetchedRevision = revisionCache.getRevisionByVersion(secondVersion.getID(), secondVersion.getVersion());
    assertNull(fetchedRevision);

  }

  private void assertEquals(InternalCDORevision thisRevision, InternalCDORevision thatRevision)
  {
    assertEquals(thisRevision.getVersion(), thatRevision.getVersion());
    assertEquals(thisRevision.getCreated(), thatRevision.getCreated());
    assertEquals(thisRevision.getRevised(), thatRevision.getRevised());
  }

  private interface IDBProvider
  {
    public DataSource createDataSource();

    public void dropAllTables(Connection connection);
  }

  @SuppressWarnings("unused")
  private static class DerbyDBProvider implements IDBProvider
  {

    public DataSource createDataSource()
    {
      Map<Object, Object> properties = new HashMap<Object, Object>();
      properties.put("class", "org.apache.derby.jdbc.EmbeddedDataSource");
      properties.put("databaseName", DB_NAME);
      properties.put("createDatabase", "create");
      return DBUtil.createDataSource(properties);
    }

    /**
     * Drop all table on a given derby database.
     */
    public void dropAllTables(Connection connection)
    {
      DBUtil.dropAllTables(connection, DB_NAME);
    }
  }

  private static class H2DBProvider implements IDBProvider
  {
    public DataSource createDataSource()
    {
      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setURL("jdbc:h2:" + DB_NAME);
      return dataSource;
    }

    /**
     * Drop all table on a given h2 database.
     */
    public void dropAllTables(Connection connection)
    {
      DBUtil.dropAllTables(connection, null);
    }

  }
}
