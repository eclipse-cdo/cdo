/*
 * Copyright (c) 2010-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
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

  public void testExport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
  }

  public void testExportDate() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    purchaseOrder.setDate(new Date(1234567));
    resource.getContents().add(purchaseOrder);
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
  }

  public void testExportBlob() throws Exception
  {
    InputStream blobStream = null;

    try
    {
      blobStream = OM.BUNDLE.getInputStream("uml2/Ecore.uml");
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

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
  }

  public void testExportClob() throws Exception
  {
    InputStream clobStream = null;

    try
    {
      clobStream = OM.BUNDLE.getInputStream("uml2/Ecore.uml");
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

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
  }

  public void testExportCustomDataType() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createPoligon(new Point(1, 2), new Point(3, 1), new Point(4, 5)));
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
  }

  public void testExportFeatureMap() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    addFeatureMap(resource);

    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
  }

  public void testExportExternalReference() throws Exception
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
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
  }

  private void useAfterImport(String repoName) throws CommitException
  {
    CDOSession session2 = openSession(repoName);
    CDOTransaction transaction2 = session2.openTransaction();

    // Read all repo contents
    TreeIterator<EObject> iter = transaction2.getRootResource().getAllContents();
    while (iter.hasNext())
    {
      iter.next();
    }

    // Add content from a new package
    CDOResource resource = transaction2.createResource(getResourcePath("/r1"));
    resource.getContents().add(getModel3Factory().createPolygon());
    transaction2.commit();

    session2.close();
  }

  /**
   * TODO
   * {@link org.eclipse.emf.cdo.server.IStoreAccessor.Raw#rawStore(org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision, org.eclipse.net4j.util.om.monitor.OMMonitor)
   * rawStore()} is not adequate with range-based list mappings because they need deltas!
   */
  @Skips("DB.ranges")
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
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);

    useAfterImport("repo2");
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
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportBlob() throws Exception
  {
    InputStream blobStream = null;

    try
    {
      blobStream = OM.BUNDLE.getInputStream("uml2/Ecore.uml");
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

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportClob() throws Exception
  {
    InputStream clobStream = null;

    try
    {
      clobStream = OM.BUNDLE.getInputStream("uml2/Ecore.uml");
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

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportCustomDataType() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createPoligon(new Point(1, 2), new Point(3, 1), new Point(4, 5)));
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);
  }

  @CleanRepositoriesBefore(reason = "Inactive repository required")
  public void testImportFeatureMap() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    addFeatureMap(resource);

    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);
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
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);

    CDOSession session2 = openSession("repo2");
    CDOView view2 = session2.openView();
    CDOResource resource2 = view2.getResource(getResourcePath("/res1"));

    ResourceSet resourceSet2 = view2.getResourceSet();
    initExtResource(resourceSet2);

    SalesOrder salesOrder2 = (SalesOrder)resource2.getContents().get(0);
    Customer customer2 = salesOrder2.getCustomer();
    System.out.println(customer2);
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

  private void addFeatureMap(CDOResource resource)
  {
    Doctor doctor1 = getModel5Factory().createDoctor();
    Doctor doctor2 = getModel5Factory().createDoctor();

    resource.getContents().add(doctor1);
    resource.getContents().add(doctor2);

    TestFeatureMap featureMap = getModel5Factory().createTestFeatureMap();
    featureMap.getPeople().add(getModel5Package().getTestFeatureMap_Doctors(), doctor1);
    featureMap.getPeople().add(getModel5Package().getTestFeatureMap_Doctors(), doctor2);

    resource.getContents().add(featureMap);
  }
}
