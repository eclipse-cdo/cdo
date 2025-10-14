/*
 * Copyright (c) 2010-2013, 2015, 2018, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.tests.model6.UnsettableAttributes;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author Eike Stepper
 */
public class BackupTest extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
    skipStoreWithoutRawAccess();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    super.doTearDown();
  }

  protected CDOServerExporter<?> createExporter(InternalRepository repo1)
  {
    return new CDOServerExporter.XML(repo1);
  }

  protected CDOServerImporter createImporter(InternalRepository repo2)
  {
    return new CDOServerImporter.XML(repo2);
  }

  private Customer initExtResource(ResourceSet resourceSet)
  {
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    Resource extResource = resourceSet.createResource(URI.createURI("ext.xmi"));

    Customer customer = getModel1Factory().createCustomer();
    extResource.getContents().add(customer);
    return customer;
  }

  private Customer createCustomer(String name)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName(name);
    return customer;
  }

  private SalesOrder createSalesOrder(Customer customer)
  {
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setId(4711);
    salesOrder.setCustomer(customer);
    return salesOrder;
  }

  private Polygon createPoligon(Point... points)
  {
    Polygon polygon = getModel3Factory().createPolygon();
    for (Point point : points)
    {
      polygon.getPoints().add(point);
    }

    return polygon;
  }

  private void useAfterImport(String repoName) throws CommitException
  {
    CDOSession session2 = openSession(repoName);
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.getResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

    // Read all repo contents
    TreeIterator<EObject> iter = transaction2.getRootResource().getAllContents();
    while (iter.hasNext())
    {
      EObject eObject = iter.next();
      System.out.println(eObject);
    }

    // Add content from a new package
    CDOResource resource = transaction2.createResource(getResourcePath("/r1"));
    resource.getContents().add(getModel3Factory().createPolygon());
    transaction2.commit();

    session2.close();
  }

  private void doExportImport() throws Exception, CommitException
  {
    doExportImport(true);
  }

  private void doExportImport(boolean useAfterImport) throws Exception, CommitException
  {
    InternalRepository repo1 = getRepository();
    int sessionsBeforeExport = repo1.getSessionManager().getSessions().length;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter<?> exporter = createExporter(repo1);
    exporter.exportRepository(baos);

    // Test bug 552512.
    assertEquals(sessionsBeforeExport, repo1.getSessionManager().getSessions().length);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter importer = createImporter(repo2);
    importer.importRepository(bais);

    if (useAfterImport)
    {
      useAfterImport("repo2");
    }
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    Customer eike = createCustomer("Eike");
    resource.getContents().add(eike);
    resource.getContents().add(createCustomer("Jos"));
    resource.getContents().add(createCustomer("Simon"));
    transaction.commit();
    SalesOrder salesOrder = createSalesOrder(eike);
    salesOrder.getOrderDetails().add(getModel1Factory().createOrderDetail());
    resource.getContents().add(salesOrder);
    transaction.commit();
    closeSession(session);

    doExportImport();
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportDate() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    purchaseOrder.setDate(new Date(1234567));
    resource.getContents().add(purchaseOrder);
    transaction.commit();
    closeSession(session);

    doExportImport();
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportBlob() throws Exception
  {
    InputStream blobStream = null;

    try
    {
      blobStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
      CDOBlob blob = new CDOBlob(blobStream);

      Image image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(blob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      resource.getContents().add(image);
      transaction.commit();
    }
    finally
    {
      IOUtil.close(blobStream);
    }

    doExportImport();
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportClob() throws Exception
  {
    InputStream clobStream = null;

    try
    {
      clobStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
      CDOClob clob = new CDOClob(new InputStreamReader(clobStream));

      File file = getModel3Factory().createFile();
      file.setName("Ecore.uml");
      file.setData(clob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      resource.getContents().add(file);
      transaction.commit();
    }
    finally
    {
      IOUtil.close(clobStream);
    }

    doExportImport();
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportByteArray() throws Exception
  {
    UnsettableAttributes object = getModel6Factory().createUnsettableAttributes();
    object.setAttrByteArray(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -2, -3, -128, 127 });

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(object);
    transaction.commit();

    doExportImport();
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportNIL() throws Exception
  {
    Unsettable1 object = getModel2Factory().createUnsettable1();
    object.setUnsettableString(null);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(object);
    transaction.commit();

    assertEquals(CDORevisionData.NIL, CDOUtil.getCDOObject(object).cdoRevision().data().get(getModel2Package().getUnsettable1_UnsettableString(), 0));

    doExportImport();
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportCustomDataType() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createPoligon(new Point(1, 2), new Point(3, 1), new Point(4, 5)));
    transaction.commit();
    closeSession(session);

    doExportImport();
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportExternalReference() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    ResourceSet resourceSet = transaction.getResourceSet();
    Customer customer = initExtResource(resourceSet);

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setCustomer(customer);
    resource.getContents().add(salesOrder);

    transaction.commit();
    closeSession(session);

    doExportImport(false);

    CDOSession session2 = openSession("repo2");
    CDOView view2 = session2.openView();
    CDOResource resource2 = view2.getResource(getResourcePath("/res1"));

    ResourceSet resourceSet2 = view2.getResourceSet();
    initExtResource(resourceSet2);

    SalesOrder salesOrder2 = (SalesOrder)resource2.getContents().get(0);
    Customer customer2 = salesOrder2.getCustomer();
    System.out.println(customer2);
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testImportDetachedRevision() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    purchaseOrder.setDate(new Date(1234567));
    resource.getContents().add(purchaseOrder);
    transaction.commit();
    resource.getContents().clear();
    transaction.commit();
    closeSession(session);

    doExportImport();

    EClass eClass = getModel1Package().getPurchaseOrder();
    CDOSession session2 = openSession("repo2");
    CDOBranch branch = session2.getBranchManager().getMainBranch();
    session2.getRevisionManager().handleRevisions(eClass, branch, true, 0, false, new CDORevisionHandler()
    {
      @Override
      public boolean handleRevision(CDORevision revision)
      {
        fail("No PurchaseOrder revision should be visible by now.");
        return true;
      }
    });
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportNullListElement() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
    polygon.getPoints().add(new Point(1, 2));
    polygon.getPoints().add(null);
    polygon.getPoints().add(new Point(3, 4));
    resource.getContents().add(polygon);

    transaction.commit();
    closeSession(session);

    doExportImport();

    CDOSession session2 = openSession("repo2");
    CDOView view2 = session2.openView();
    CDOResource resource2 = view2.getResource(getResourcePath("/res1"));
    PolygonWithDuplicates polygon2 = (PolygonWithDuplicates)resource2.getContents().get(0);
    assertEquals(new Point(1, 2), polygon2.getPoints().get(0));
    assertEquals(null, polygon2.getPoints().get(1));
    assertEquals(new Point(3, 4), polygon2.getPoints().get(2));
  }
}
