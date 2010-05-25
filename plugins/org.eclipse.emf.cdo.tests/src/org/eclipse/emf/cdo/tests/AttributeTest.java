/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import java.math.BigDecimal;
import java.math.BigInteger;

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

      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(supplier);
      assertEquals(true, supplier.isPreferred());
      transaction.commit();
      assertEquals(true, supplier.isPreferred());
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
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

      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(product);
      assertEquals(VAT.VAT15, product.getVat());
      transaction.commit();
      assertEquals(VAT.VAT15, product.getVat());
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
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
      EPackage packageBytes = createDynamicEPackageWithByte();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(packageBytes);
      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)packageBytes.getEClassifier("GenOfByteArray");
      EObject genOfByteArray = packageBytes.getEFactoryInstance().create(eClass);
      genOfByteArray.eSet(genOfByteArray.eClass().getEStructuralFeature("bytes"), saveByteArray);

      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(genOfByteArray);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
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
      EPackage packageBytes = createDynamicEPackageWithByte();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(packageBytes);
      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)packageBytes.getEClassifier("GenOfByteArray");
      EObject genOfByteArray = packageBytes.getEFactoryInstance().create(eClass);
      genOfByteArray.eSet(genOfByteArray.eClass().getEStructuralFeature("bytes"), saveByteArray);

      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(genOfByteArray);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
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
      EPackage packageBytes = createDynamicEPackageWithByte();
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(packageBytes);
      CDOTransaction transaction = session.openTransaction();

      EClass eClass = (EClass)packageBytes.getEClassifier("GenOfByteArray");
      EObject genOfByteArray = packageBytes.getEFactoryInstance().create(eClass);
      genOfByteArray.eSet(genOfByteArray.eClass().getEStructuralFeature("bytes"), saveByteArray);

      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(genOfByteArray);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
      EObject genOfByteArray = resource.getContents().get(0);
      byte storeByteArray[] = (byte[])genOfByteArray.eGet(genOfByteArray.eClass().getEStructuralFeature("bytes"));
      assertNull(storeByteArray);
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

      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(gen);

      CDORevisionData data = ((CDOObject)gen).cdoRevision().data();
      assertEquals(BigDecimal.class, data.get(bigDecimalFeature, -1).getClass());
      assertEquals(BigInteger.class, data.get(bigIntegerFeature, -1).getClass());

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
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

      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(gen);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
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
    EPackage schoolPackage = efactory.createEPackage();
    schoolPackage.setName("EPackageTest");
    schoolPackage.setNsPrefix("EPackageTest");
    schoolPackage.setNsURI("http:///www.cdo.org/testcase");
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
    EPackage schoolPackage = efactory.createEPackage();
    schoolPackage.setName("EPackageTest");
    schoolPackage.setNsPrefix("EPackageTest");
    schoolPackage.setNsURI("http:///www.cdo.org/testcase");
    schoolPackage.getEClassifiers().add(schoolBookEClass);
    return schoolPackage;
  }
}
