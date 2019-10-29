/*
 * Copyright (c) 2008-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Christian W. Damus (CEA) - 378620 support unsettable features of custom data type
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute;
import org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.Polygon;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.tests.model6.HasNillableAttribute;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class AttributeTest extends AbstractCDOTest
{
  public void testPrimitiveDefaults() throws Exception
  {
    {
      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("Preferred Supplier");
      assertEquals(true, supplier.isPreferred());

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(supplier);
      assertEquals(true, supplier.isPreferred());
      transaction.commit();
      assertEquals(true, supplier.isPreferred());
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/my/resource"));
      Supplier supplier = (Supplier)resource.getContents().get(0);
      assertEquals(true, supplier.isPreferred());
      view.close();
      session.close();
    }
  }

  public void testEnumDefaults() throws Exception
  {
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Test Product");
      assertEquals(VAT.VAT15, product.getVat());

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(product);
      assertEquals(VAT.VAT15, product.getVat());
      transaction.commit();
      assertEquals(VAT.VAT15, product.getVat());
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/my/resource"));
      Product1 product = (Product1)resource.getContents().get(0);
      assertEquals(VAT.VAT15, product.getVat());
      view.close();
      session.close();
    }
  }

  public void testByteArray() throws Exception
  {
    byte saveByteArray[] = new byte[] { 0, 1, 2, 3, 0, 1, 0, 100 };

    {
      EPackage packageWithBytes = createDynamicEPackageWithByte();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(packageWithBytes);

      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)packageWithBytes.getEClassifier("GenOfByteArray");
      EObject genOfByteArray = packageWithBytes.getEFactoryInstance().create(eClass);
      genOfByteArray.eSet(genOfByteArray.eClass().getEStructuralFeature("bytes"), saveByteArray);

      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(genOfByteArray);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/my/resource"));
      EObject genOfByteArray = resource.getContents().get(0);
      byte storeByteArray[] = (byte[])genOfByteArray.eGet(genOfByteArray.eClass().getEStructuralFeature("bytes"));
      assertEquals(storeByteArray.length, saveByteArray.length);
      for (int i = 0; i < storeByteArray.length; i++)
      {
        assertEquals(storeByteArray[i], saveByteArray[i]);
      }

      view.close();
      session.close();
    }
  }

  public void testByteArrayEmpty() throws Exception
  {
    byte saveByteArray[] = new byte[0];

    {
      EPackage packageWithBytes = createDynamicEPackageWithByte();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(packageWithBytes);
      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)packageWithBytes.getEClassifier("GenOfByteArray");
      EObject genOfByteArray = packageWithBytes.getEFactoryInstance().create(eClass);
      genOfByteArray.eSet(genOfByteArray.eClass().getEStructuralFeature("bytes"), saveByteArray);

      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(genOfByteArray);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/my/resource"));
      EObject genOfByteArray = resource.getContents().get(0);
      byte storeByteArray[] = (byte[])genOfByteArray.eGet(genOfByteArray.eClass().getEStructuralFeature("bytes"));
      assertEquals(0, storeByteArray.length);
      view.close();
      session.close();
    }
  }

  public void testByteArrayNull() throws Exception
  {
    byte saveByteArray[] = null;

    {
      EPackage packageWithBytes = createDynamicEPackageWithByte();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(packageWithBytes);
      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)packageWithBytes.getEClassifier("GenOfByteArray");
      EObject genOfByteArray = packageWithBytes.getEFactoryInstance().create(eClass);
      genOfByteArray.eSet(genOfByteArray.eClass().getEStructuralFeature("bytes"), saveByteArray);

      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(genOfByteArray);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/my/resource"));
      EObject genOfByteArray = resource.getContents().get(0);
      byte storeByteArray[] = (byte[])genOfByteArray.eGet(genOfByteArray.eClass().getEStructuralFeature("bytes"));
      assertEquals(true, storeByteArray == null || storeByteArray.length == 0);
      view.close();
      session.close();
    }
  }

  public void testBigDecimalAndBigInteger() throws Exception
  {
    BigDecimal bigDecimal = new BigDecimal(10);
    BigInteger bigInteger = BigInteger.valueOf(10);

    {
      EPackage ePackage = createDynamicEPackageBigIntegerAndBigDecimal();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(ePackage);
      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)ePackage.getEClassifier("Gen");
      EStructuralFeature bigDecimalFeature = eClass.getEStructuralFeature("bigDecimal");
      EStructuralFeature bigIntegerFeature = eClass.getEStructuralFeature("bigInteger");

      EObject gen = ePackage.getEFactoryInstance().create(eClass);
      gen.eSet(bigDecimalFeature, bigDecimal);
      gen.eSet(bigIntegerFeature, bigInteger);

      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(gen);

      CDORevisionData data = CDOUtil.getCDOObject(gen).cdoRevision().data();
      assertEquals(BigDecimal.class, data.get(bigDecimalFeature, -1).getClass());
      assertEquals(BigInteger.class, data.get(bigIntegerFeature, -1).getClass());

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/my/resource"));
      EObject gen = resource.getContents().get(0);
      BigDecimal bigDecimalStore = (BigDecimal)gen.eGet(gen.eClass().getEStructuralFeature("bigDecimal"));
      BigInteger bigIntegerStore = (BigInteger)gen.eGet(gen.eClass().getEStructuralFeature("bigInteger"));
      assertEquals(bigDecimal, bigDecimalStore);
      assertEquals(bigInteger, bigIntegerStore);

      view.close();
      session.close();
    }
  }

  public void testBigDecimalAndBigIntegerNull() throws Exception
  {
    BigDecimal bigDecimal = null;
    BigInteger bigInteger = null;

    {
      EPackage ePackage = createDynamicEPackageBigIntegerAndBigDecimal();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(ePackage);
      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)ePackage.getEClassifier("Gen");
      EStructuralFeature bigDecimalFeature = eClass.getEStructuralFeature("bigDecimal");
      EStructuralFeature bigIntegerFeature = eClass.getEStructuralFeature("bigInteger");

      EObject gen = ePackage.getEFactoryInstance().create(eClass);
      gen.eSet(bigDecimalFeature, bigDecimal);
      gen.eSet(bigIntegerFeature, bigInteger);

      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(gen);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/my/resource"));
      EObject gen = resource.getContents().get(0);
      BigDecimal bigDecimalStore = (BigDecimal)gen.eGet(gen.eClass().getEStructuralFeature("bigDecimal"));
      BigInteger bigIntegerStore = (BigInteger)gen.eGet(gen.eClass().getEStructuralFeature("bigInteger"));
      assertNull(bigDecimalStore);
      assertNull(bigIntegerStore);

      view.close();
      session.close();
    }
  }

  private EPackage createDynamicEPackageWithByte()
  {
    final EcoreFactory efactory = EcoreFactory.eINSTANCE;
    final EcorePackage epackage = EcorePackage.eINSTANCE;

    EClass schoolBookEClass = efactory.createEClass();
    schoolBookEClass.setName("GenOfByteArray");

    // create a new attribute for this EClass
    EAttribute level = efactory.createEAttribute();
    level.setName("bytes");
    level.setEType(epackage.getEByteArray());
    schoolBookEClass.getEStructuralFeatures().add(level);

    // Create a new EPackage and add the new EClasses
    EPackage schoolPackage = createUniquePackage();
    schoolPackage.getEClassifiers().add(schoolBookEClass);
    return schoolPackage;
  }

  private EPackage createDynamicEPackageBigIntegerAndBigDecimal()
  {
    final EcoreFactory efactory = EcoreFactory.eINSTANCE;
    final EcorePackage epackage = EcorePackage.eINSTANCE;

    EClass schoolBookEClass = efactory.createEClass();
    schoolBookEClass.setName("Gen");

    // create a new attribute for this EClass
    EAttribute attrBigDecimal = efactory.createEAttribute();
    attrBigDecimal.setName("bigDecimal");
    attrBigDecimal.setEType(epackage.getEBigDecimal());

    schoolBookEClass.getEStructuralFeatures().add(attrBigDecimal);

    EAttribute attrBigInteger = efactory.createEAttribute();
    attrBigInteger.setName("bigInteger");
    attrBigInteger.setEType(epackage.getEBigInteger());
    schoolBookEClass.getEStructuralFeatures().add(attrBigInteger);

    // Create a new EPackage and add the new EClasses
    EPackage schoolPackage = createUniquePackage();
    schoolPackage.getEClassifiers().add(schoolBookEClass);
    return schoolPackage;
  }

  public void testManyValuedCustomDataType_Get() throws Exception
  {
    Polygon polygon = getModel3Factory().createPolygon();
    EList<Point> points = polygon.getPoints();
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));

    points = polygon.getPoints();
    assertEquals(3, points.size());
    assertInstanceOf(Point.class, points.get(0));
    assertInstanceOf(Point.class, points.get(1));
    assertInstanceOf(Point.class, points.get(2));

    Object[] array1 = points.toArray();
    assertEquals(points.size(), array1.length);
    assertInstanceOf(Point.class, array1[0]);
    assertInstanceOf(Point.class, array1[1]);
    assertInstanceOf(Point.class, array1[2]);

    Point[] array2 = points.toArray(new Point[3]);
    assertEquals(points.size(), array2.length);
    assertInstanceOf(Point.class, array2[0]);
    assertInstanceOf(Point.class, array2[1]);
    assertInstanceOf(Point.class, array2[2]);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(polygon);

    points = polygon.getPoints();
    assertEquals(3, points.size());
    assertInstanceOf(Point.class, points.get(0));
    assertInstanceOf(Point.class, points.get(1));
    assertInstanceOf(Point.class, points.get(2));

    array1 = points.toArray();
    assertEquals(points.size(), array1.length);
    assertInstanceOf(Point.class, array1[0]);
    assertInstanceOf(Point.class, array1[1]);
    assertInstanceOf(Point.class, array1[2]);

    array2 = points.toArray(new Point[3]);
    assertEquals(points.size(), array2.length);
    assertInstanceOf(Point.class, array2[0]);
    assertInstanceOf(Point.class, array2[1]);
    assertInstanceOf(Point.class, array2[2]);

    transaction.commit();

    points = polygon.getPoints();
    assertEquals(3, points.size());
    assertInstanceOf(Point.class, points.get(0));
    assertInstanceOf(Point.class, points.get(1));
    assertInstanceOf(Point.class, points.get(2));

    array1 = points.toArray();
    assertEquals(points.size(), array1.length);
    assertInstanceOf(Point.class, array1[0]);
    assertInstanceOf(Point.class, array1[1]);
    assertInstanceOf(Point.class, array1[2]);

    array2 = points.toArray(new Point[3]);
    assertEquals(points.size(), array2.length);
    assertInstanceOf(Point.class, array2[0]);
    assertInstanceOf(Point.class, array2[1]);
    assertInstanceOf(Point.class, array2[2]);

    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    polygon = (Polygon)resource.getContents().get(0);

    points = polygon.getPoints();
    assertEquals(3, points.size());
    assertInstanceOf(Point.class, points.get(0));
    assertInstanceOf(Point.class, points.get(1));
    assertInstanceOf(Point.class, points.get(2));

    array1 = points.toArray();
    assertEquals(points.size(), array1.length);
    assertInstanceOf(Point.class, array1[0]);
    assertInstanceOf(Point.class, array1[1]);
    assertInstanceOf(Point.class, array1[2]);

    array2 = points.toArray(new Point[3]);
    assertEquals(points.size(), array2.length);
    assertInstanceOf(Point.class, array2[0]);
    assertInstanceOf(Point.class, array2[1]);
    assertInstanceOf(Point.class, array2[2]);
  }

  public void testManyValuedCustomDataType_Contains() throws Exception
  {
    Polygon polygon = getModel3Factory().createPolygon();
    EList<Point> points = polygon.getPoints();
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));
    points.add(new Point(7, 8));
    points.add(new Point(9, 0));

    assertEquals(true, points.contains(new Point(1, 2)));
    assertEquals(true, points.contains(new Point(3, 4)));
    assertEquals(true, points.contains(new Point(5, 6)));
    assertEquals(true, points.contains(new Point(7, 8)));
    assertEquals(true, points.contains(new Point(9, 0)));

    assertEquals(false, points.contains(new Point(0, 2)));
    assertEquals(false, points.contains(new Point(0, 4)));
    assertEquals(false, points.contains(new Point(0, 6)));
    assertEquals(false, points.contains(new Point(0, 8)));
    assertEquals(false, points.contains(new Point(0, 0)));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(polygon);

    points = polygon.getPoints();
    assertEquals(true, points.contains(new Point(1, 2)));
    assertEquals(true, points.contains(new Point(3, 4)));
    assertEquals(true, points.contains(new Point(5, 6)));
    assertEquals(true, points.contains(new Point(7, 8)));
    assertEquals(true, points.contains(new Point(9, 0)));

    assertEquals(false, points.contains(new Point(0, 2)));
    assertEquals(false, points.contains(new Point(0, 4)));
    assertEquals(false, points.contains(new Point(0, 6)));
    assertEquals(false, points.contains(new Point(0, 8)));
    assertEquals(false, points.contains(new Point(0, 0)));

    transaction.commit();

    points = polygon.getPoints();
    assertEquals(true, points.contains(new Point(1, 2)));
    assertEquals(true, points.contains(new Point(3, 4)));
    assertEquals(true, points.contains(new Point(5, 6)));
    assertEquals(true, points.contains(new Point(7, 8)));
    assertEquals(true, points.contains(new Point(9, 0)));

    assertEquals(false, points.contains(new Point(0, 2)));
    assertEquals(false, points.contains(new Point(0, 4)));
    assertEquals(false, points.contains(new Point(0, 6)));
    assertEquals(false, points.contains(new Point(0, 8)));
    assertEquals(false, points.contains(new Point(0, 0)));

    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    polygon = (Polygon)resource.getContents().get(0);

    points = polygon.getPoints();
    assertEquals(true, points.contains(new Point(1, 2)));
    assertEquals(true, points.contains(new Point(3, 4)));
    assertEquals(true, points.contains(new Point(5, 6)));
    assertEquals(true, points.contains(new Point(7, 8)));
    assertEquals(true, points.contains(new Point(9, 0)));

    assertEquals(false, points.contains(new Point(0, 2)));
    assertEquals(false, points.contains(new Point(0, 4)));
    assertEquals(false, points.contains(new Point(0, 6)));
    assertEquals(false, points.contains(new Point(0, 8)));
    assertEquals(false, points.contains(new Point(0, 0)));
  }

  public void testManyValuedCustomDataType_ContainsAll() throws Exception
  {
    Polygon polygon = getModel3Factory().createPolygon();
    EList<Point> points = polygon.getPoints();
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));
    points.add(new Point(7, 8));
    points.add(new Point(9, 0));

    assertEquals(true, points.containsAll(points));

    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(9, 0) })));

    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(0, 0) })));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(polygon);

    points = polygon.getPoints();
    assertEquals(true, points.containsAll(points));

    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(9, 0) })));

    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(0, 0) })));

    transaction.commit();

    points = polygon.getPoints();
    assertEquals(true, points.containsAll(points));

    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(9, 0) })));

    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(0, 0) })));

    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    polygon = (Polygon)resource.getContents().get(0);

    points = polygon.getPoints();
    assertEquals(true, points.containsAll(points));

    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(9, 0) })));
    assertEquals(true, points.containsAll(Arrays.asList(new Point[] { new Point(9, 0) })));

    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(3, 4), new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(5, 6), new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(7, 8), new Point(0, 0) })));
    assertEquals(false, points.containsAll(Arrays.asList(new Point[] { new Point(0, 0) })));
  }

  public void testManyValuedCustomDataType_IndexOf() throws Exception
  {
    PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
    EList<Point> points = polygon.getPoints();
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));
    points.add(new Point(7, 8));
    points.add(new Point(9, 0));
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));
    points.add(new Point(7, 8));
    points.add(new Point(9, 0));

    assertEquals(0, points.indexOf(new Point(1, 2)));
    assertEquals(1, points.indexOf(new Point(3, 4)));
    assertEquals(2, points.indexOf(new Point(5, 6)));
    assertEquals(3, points.indexOf(new Point(7, 8)));
    assertEquals(4, points.indexOf(new Point(9, 0)));

    assertEquals(-1, points.indexOf(new Point(0, 2)));
    assertEquals(-1, points.indexOf(new Point(0, 4)));
    assertEquals(-1, points.indexOf(new Point(0, 6)));
    assertEquals(-1, points.indexOf(new Point(0, 8)));
    assertEquals(-1, points.indexOf(new Point(0, 0)));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(polygon);

    points = polygon.getPoints();
    assertEquals(0, points.indexOf(new Point(1, 2)));
    assertEquals(1, points.indexOf(new Point(3, 4)));
    assertEquals(2, points.indexOf(new Point(5, 6)));
    assertEquals(3, points.indexOf(new Point(7, 8)));
    assertEquals(4, points.indexOf(new Point(9, 0)));

    assertEquals(-1, points.indexOf(new Point(0, 2)));
    assertEquals(-1, points.indexOf(new Point(0, 4)));
    assertEquals(-1, points.indexOf(new Point(0, 6)));
    assertEquals(-1, points.indexOf(new Point(0, 8)));
    assertEquals(-1, points.indexOf(new Point(0, 0)));

    transaction.commit();

    points = polygon.getPoints();
    assertEquals(0, points.indexOf(new Point(1, 2)));
    assertEquals(1, points.indexOf(new Point(3, 4)));
    assertEquals(2, points.indexOf(new Point(5, 6)));
    assertEquals(3, points.indexOf(new Point(7, 8)));
    assertEquals(4, points.indexOf(new Point(9, 0)));

    assertEquals(-1, points.indexOf(new Point(0, 2)));
    assertEquals(-1, points.indexOf(new Point(0, 4)));
    assertEquals(-1, points.indexOf(new Point(0, 6)));
    assertEquals(-1, points.indexOf(new Point(0, 8)));
    assertEquals(-1, points.indexOf(new Point(0, 0)));

    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    polygon = (PolygonWithDuplicates)resource.getContents().get(0);

    points = polygon.getPoints();
    assertEquals(0, points.indexOf(new Point(1, 2)));
    assertEquals(1, points.indexOf(new Point(3, 4)));
    assertEquals(2, points.indexOf(new Point(5, 6)));
    assertEquals(3, points.indexOf(new Point(7, 8)));
    assertEquals(4, points.indexOf(new Point(9, 0)));

    assertEquals(-1, points.indexOf(new Point(0, 2)));
    assertEquals(-1, points.indexOf(new Point(0, 4)));
    assertEquals(-1, points.indexOf(new Point(0, 6)));
    assertEquals(-1, points.indexOf(new Point(0, 8)));
    assertEquals(-1, points.indexOf(new Point(0, 0)));
  }

  public void testManyValuedCustomDataType_LastIndexOf() throws Exception
  {
    PolygonWithDuplicates polygon = getModel3Factory().createPolygonWithDuplicates();
    EList<Point> points = polygon.getPoints();
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));
    points.add(new Point(7, 8));
    points.add(new Point(9, 0));
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));
    points.add(new Point(7, 8));
    points.add(new Point(9, 0));

    assertEquals(5, points.lastIndexOf(new Point(1, 2)));
    assertEquals(6, points.lastIndexOf(new Point(3, 4)));
    assertEquals(7, points.lastIndexOf(new Point(5, 6)));
    assertEquals(8, points.lastIndexOf(new Point(7, 8)));
    assertEquals(9, points.lastIndexOf(new Point(9, 0)));

    assertEquals(-1, points.lastIndexOf(new Point(0, 2)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 4)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 6)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 8)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 0)));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(polygon);

    points = polygon.getPoints();
    assertEquals(5, points.lastIndexOf(new Point(1, 2)));
    assertEquals(6, points.lastIndexOf(new Point(3, 4)));
    assertEquals(7, points.lastIndexOf(new Point(5, 6)));
    assertEquals(8, points.lastIndexOf(new Point(7, 8)));
    assertEquals(9, points.lastIndexOf(new Point(9, 0)));

    assertEquals(-1, points.lastIndexOf(new Point(0, 2)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 4)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 6)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 8)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 0)));

    transaction.commit();

    points = polygon.getPoints();
    assertEquals(5, points.lastIndexOf(new Point(1, 2)));
    assertEquals(6, points.lastIndexOf(new Point(3, 4)));
    assertEquals(7, points.lastIndexOf(new Point(5, 6)));
    assertEquals(8, points.lastIndexOf(new Point(7, 8)));
    assertEquals(9, points.lastIndexOf(new Point(9, 0)));

    assertEquals(-1, points.lastIndexOf(new Point(0, 2)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 4)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 6)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 8)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 0)));

    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    polygon = (PolygonWithDuplicates)resource.getContents().get(0);

    points = polygon.getPoints();
    assertEquals(5, points.lastIndexOf(new Point(1, 2)));
    assertEquals(6, points.lastIndexOf(new Point(3, 4)));
    assertEquals(7, points.lastIndexOf(new Point(5, 6)));
    assertEquals(8, points.lastIndexOf(new Point(7, 8)));
    assertEquals(9, points.lastIndexOf(new Point(9, 0)));

    assertEquals(-1, points.lastIndexOf(new Point(0, 2)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 4)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 6)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 8)));
    assertEquals(-1, points.lastIndexOf(new Point(0, 0)));
  }

  public void testManyValuedCustomDataType_Bugzilla_319950() throws Exception
  {
    Polygon polygon = getModel3Factory().createPolygon();
    EList<Point> points = polygon.getPoints();
    points.add(new Point(1, 2));
    points.add(new Point(3, 4));
    points.add(new Point(5, 6));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(polygon);
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    EList<EObject> contents = resource.getContents();

    // java.lang.ClassCastException: org.eclipse.emf.internal.cdo.revision.CDOListWithElementProxiesImpl
    EcoreUtil.copyAll(contents);
  }

  /**
   * Bug 355787.
   */
  public void testJavaClass() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(createClassWithJavaClassAttribute(CDOTransaction.class));
    resource.getContents().add(createClassWithJavaClassAttribute(boolean.class));
    resource.getContents().add(createClassWithJavaClassAttribute(byte.class));
    resource.getContents().add(createClassWithJavaClassAttribute(char.class));
    resource.getContents().add(createClassWithJavaClassAttribute(double.class));
    resource.getContents().add(createClassWithJavaClassAttribute(float.class));
    resource.getContents().add(createClassWithJavaClassAttribute(int.class));
    resource.getContents().add(createClassWithJavaClassAttribute(long.class));
    resource.getContents().add(createClassWithJavaClassAttribute(short.class));
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    EList<EObject> contents = resource.getContents();
    assertEquals(CDOTransaction.class, ((ClassWithJavaClassAttribute)contents.get(0)).getJavaClass());
    assertEquals(boolean.class, ((ClassWithJavaClassAttribute)contents.get(1)).getJavaClass());
    assertEquals(byte.class, ((ClassWithJavaClassAttribute)contents.get(2)).getJavaClass());
    assertEquals(char.class, ((ClassWithJavaClassAttribute)contents.get(3)).getJavaClass());
    assertEquals(double.class, ((ClassWithJavaClassAttribute)contents.get(4)).getJavaClass());
    assertEquals(float.class, ((ClassWithJavaClassAttribute)contents.get(5)).getJavaClass());
    assertEquals(int.class, ((ClassWithJavaClassAttribute)contents.get(6)).getJavaClass());
    assertEquals(long.class, ((ClassWithJavaClassAttribute)contents.get(7)).getJavaClass());
    assertEquals(short.class, ((ClassWithJavaClassAttribute)contents.get(8)).getJavaClass());
  }

  private ClassWithJavaClassAttribute createClassWithJavaClassAttribute(Class<?> javaClass)
  {
    ClassWithJavaClassAttribute object = getModel3Factory().createClassWithJavaClassAttribute();
    object.setJavaClass(javaClass);
    return object;
  }

  /**
   * Bug 355787.
   */
  public void testJavaObject() throws Exception
  {
    Map<String, Number> salaries = new HashMap<String, Number>();
    salaries.put("Eike", 5000);
    salaries.put("Martin", 6000.99);
    salaries.put("Ed", 7000f);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(createClassWithJavaObjectAttribute(salaries));
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/resource"));
    EList<EObject> contents = resource.getContents();
    assertEquals(salaries, ((ClassWithJavaObjectAttribute)contents.get(0)).getJavaObject());
  }

  private ClassWithJavaObjectAttribute createClassWithJavaObjectAttribute(Object javaObject)
  {
    ClassWithJavaObjectAttribute object = getModel3Factory().createClassWithJavaObjectAttribute();
    object.setJavaObject(javaObject);
    return object;
  }

  /**
   * Bug 378620.
   */
  public void testNillableAttributeOfCustomType() throws Exception
  {
    HasNillableAttribute hasNonNull = getModel6Factory().createHasNillableAttribute();
    hasNonNull.setNillable("not nil");
    HasNillableAttribute hasImplicitNull = getModel6Factory().createHasNillableAttribute();
    hasImplicitNull.unsetNillable(); // implicit null
    HasNillableAttribute hasExplicitNull = getModel6Factory().createHasNillableAttribute();
    hasExplicitNull.setNillable(null); // explicit null

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/nillable"));
    resource.getContents().add(hasNonNull);
    resource.getContents().add(hasImplicitNull);
    resource.getContents().add(hasExplicitNull);
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/my/nillable"));
    EList<EObject> contents = resource.getContents();
    assertEquals(3, contents.size());

    HasNillableAttribute hasNillable = (HasNillableAttribute)contents.get(0);
    assertEquals("not nil", hasNillable.getNillable());
    assertEquals(true, hasNillable.isSetNillable());
    hasNillable = (HasNillableAttribute)contents.get(1);
    assertNull(hasNillable.getNillable());
    assertEquals(false, hasNillable.isSetNillable());
    hasNillable = (HasNillableAttribute)contents.get(2);
    assertNull(hasNillable.getNillable());
    assertEquals(true, hasNillable.isSetNillable());
  }
}
