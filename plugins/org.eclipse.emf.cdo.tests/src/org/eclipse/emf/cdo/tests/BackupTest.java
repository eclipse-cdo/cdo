/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.model.lob.CDOBlob;
import org.eclipse.emf.cdo.common.model.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerExporter;
import org.eclipse.emf.cdo.server.CDOServerImporter;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    CDOResource resource = transaction.createResource("/res1");
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    System.out.println(baos.toString());
  }

  public void testExportBlob() throws Exception
  {
    InputStream blobStream = null;

    try
    {
      blobStream = OM.BUNDLE.getInputStream("copyright.txt");
      CDOBlob blob = new CDOBlob(blobStream);

      Image image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(blob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
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
    System.out.println(baos.toString());
  }

  public void testExportClob() throws Exception
  {
    InputStream clobStream = null;

    try
    {
      clobStream = OM.BUNDLE.getInputStream("copyright.txt");
      CDOClob clob = new CDOClob(new InputStreamReader(clobStream));

      File file = getModel3Factory().createFile();
      file.setName("copyright.txt");
      file.setData(clob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
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
    System.out.println(baos.toString());
  }

  public void testExportCustomDataType() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    resource.getContents().add(createPoligon(new Point(1, 2), new Point(3, 1), new Point(4, 5)));
    transaction.commit();
    session.close();

    InternalRepository repo1 = getRepository();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    CDOServerExporter.XML exporter = new CDOServerExporter.XML(repo1);
    exporter.exportRepository(baos);
    System.out.println(baos.toString());
  }

  public void testImport() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
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
    System.out.println(baos.toString());

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);
  }

  public void testImportBlob() throws Exception
  {
    InputStream blobStream = null;

    try
    {
      blobStream = OM.BUNDLE.getInputStream("copyright.txt");
      CDOBlob blob = new CDOBlob(blobStream);

      Image image = getModel3Factory().createImage();
      image.setWidth(320);
      image.setHeight(200);
      image.setData(blob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
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
    System.out.println(baos.toString());

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);
  }

  public void testImportClob() throws Exception
  {
    InputStream clobStream = null;

    try
    {
      clobStream = OM.BUNDLE.getInputStream("copyright.txt");
      CDOClob clob = new CDOClob(new InputStreamReader(clobStream));

      File file = getModel3Factory().createFile();
      file.setName("copyright.txt");
      file.setData(clob);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
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
    System.out.println(baos.toString());

    InternalRepository repo2 = getRepository("repo2", false);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    CDOServerImporter.XML importer = new CDOServerImporter.XML(repo2);
    importer.importRepository(bais);

    sleep(1000000);
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
}
