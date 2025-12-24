/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Caspar De Groot
 */
@Requires(IModelConfig.CAPABILITY_LEGACY)
public class Bugzilla_335675_Test extends AbstractCDOTest
{
  public void test0() throws Exception
  {
    EPackage pkg = null;
    EAttribute attr1 = null;
    EAttribute attr2 = null;
    EAttribute attr3 = null;

    final int TRANSIENT_VALUE = 2;

    EClass classA = null;
    {
      pkg = createUniquePackage();

      classA = EcoreFactory.eINSTANCE.createEClass();
      classA.setName("A");

      attr1 = createAttribute("attr1", EcorePackage.eINSTANCE.getEInt(), false);
      attr2 = createAttribute("attr2", EcorePackage.eINSTANCE.getEInt(), true);
      attr3 = createAttribute("attr3", EcorePackage.eINSTANCE.getEInt(), false);

      classA.getEStructuralFeatures().add(attr1);
      classA.getEStructuralFeatures().add(attr2);
      classA.getEStructuralFeatures().add(attr3);

      pkg.getEClassifiers().add(classA);
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      classA = (EClass)pkg.getEClassifier("A");
      EObject instanceA = EcoreUtil.create(classA);
      attr1 = (EAttribute)classA.getEStructuralFeature("attr1");
      attr2 = (EAttribute)classA.getEStructuralFeature("attr2");
      attr3 = (EAttribute)classA.getEStructuralFeature("attr3");
      instanceA.eSet(attr1, 1);
      instanceA.eSet(attr2, TRANSIENT_VALUE);
      instanceA.eSet(attr3, 3);

      resource.getContents().add(instanceA);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("test"));
      CDOObject cdoObject = CDOUtil.getCDOObject(resource.getContents().get(0));
      classA = cdoObject.eClass();
      attr1 = (EAttribute)classA.getEStructuralFeature("attr1");
      attr2 = (EAttribute)classA.getEStructuralFeature("attr2");
      attr3 = (EAttribute)classA.getEStructuralFeature("attr3");

      BaseCDORevision rev = (BaseCDORevision)cdoObject.cdoRevision();
      // int attr1Val = (Integer)rev.getValue(attr1);
      // int attr3Val = (Integer)rev.getValue(attr3);
      try
      {
        int attr2Val = (Integer)rev.getValue(attr2);
        fail("Should have thrown an exception, but fetched value " + attr2Val);
      }
      catch (IllegalArgumentException ex)
      {
        // Good
      }

      session.close();
    }
  }

  public void test1() throws CommitException
  {
    EAttribute nameAttr = Model1Package.eINSTANCE.getProduct1_Name();
    assertEquals(false, nameAttr.isTransient());

    EAttribute descriptionAttr = Model1Package.eINSTANCE.getProduct1_Description();
    assertEquals(true, descriptionAttr.isTransient());

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      Product1 product = Model1Factory.eINSTANCE.createProduct1();
      product.setName("name");
      product.setDescription("description");
      product.setVat(VAT.VAT7);
      resource.getContents().add(product);

      PurchaseOrder order = Model1Factory.eINSTANCE.createPurchaseOrder();
      OrderDetail detail = Model1Factory.eINSTANCE.createOrderDetail();
      order.getOrderDetails().add(detail);
      resource.getContents().add(order);

      detail.setProduct(product);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("test"));
      CDOObject cdoObject = CDOUtil.getCDOObject(resource.getContents().get(0));

      BaseCDORevision rev = (BaseCDORevision)cdoObject.cdoRevision();
      // String name = (String)rev.getValue(nameAttr);
      try
      {
        String desc = (String)rev.getValue(descriptionAttr);
        fail("Should have thrown an exception, but fetched value: " + desc);
      }
      catch (Exception ex)
      {
        // Good
      }

      session.close();
    }
  }

  private EAttribute createAttribute(String name, EDataType type, boolean tranzient)
  {
    EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
    attr.setName(name);
    attr.setEType(type);
    attr.setTransient(tranzient);
    return attr;
  }
}
