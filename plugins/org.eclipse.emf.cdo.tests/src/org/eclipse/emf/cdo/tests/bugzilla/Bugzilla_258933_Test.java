/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * CDORevisionImpl.eIsSet() works incorrectly
 * <p>
 * See https://bugs.eclipse.org/258933
 * 
 * @author Simon McDuff
 */
public class Bugzilla_258933_Test extends AbstractCDOTest
{
  private static final String TOP_PACKAGE_URI = "http:///www.elver.org/toppackage";

  private static final String SUB_PACKAGE_URI = "http:///www.elver.org/subPackage1";

  private static final Object NIL = new Object();

  public void testBugzilla_258933() throws Exception
  {
    testWithValue("level", NIL, false);
  }

  public void testBugzilla_258933_SetToDefaultValue() throws Exception
  {
    testWithValue("level", new Integer(10), false);
  }

  public void testBugzilla_258933_String() throws Exception
  {
    testWithValue("unsettable", NIL, false);
  }

  public void testBugzilla_258933_String_SetToDefaultValue() throws Exception
  {
    testWithValue("settable", "Simon", false);
  }

  public void testBugzilla_258933_String_SetToNull() throws Exception
  {
    testWithValue("settable", null, true);
  }

  public void testBugzilla_258933_String_unsettable() throws Exception
  {
    testWithValue("settable", NIL, false);
  }

  public void testBugzilla_258933_String_SetToDefaultValue_unsettable() throws Exception
  {
    testWithValue("unsettable", "Simon", true);
  }

  public void testBugzilla_258933_String_SetToNull_unsettable() throws Exception
  {
    testWithValue("unsettable", null, true);
  }

  private void testWithValue(String featureName, Object initializeValue, boolean isSet) throws Exception
  {
    {
      EPackage topPackage = createDynamicEPackage();

      EPackage subpackage1 = topPackage.getESubpackages().get(0);
      EClass class1Class = (EClass)subpackage1.getEClassifier("class1");
      EStructuralFeature feature = class1Class.getEStructuralFeature(featureName);

      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(topPackage);

      CDOTransaction transaction = session.openTransaction();
      EObject instance = EcoreUtil.create(class1Class);
      if (NIL != initializeValue)
      {
        instance.eSet(feature, initializeValue);
        // instance.eUnset(feature);
      }

      assertEquals(isSet, instance.eIsSet(feature));

      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(instance);
      assertEquals(isSet, instance.eIsSet(feature));

      transaction.commit();
      assertEquals(isSet, instance.eIsSet(feature));

      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    if (session instanceof org.eclipse.emf.cdo.net4j.CDOSession)
    {
      ((org.eclipse.emf.cdo.net4j.CDOSession)session).options().getProtocol().setTimeout(2000L);
    }

    EPackage subpackage1 = session.getPackageRegistry().getEPackage(SUB_PACKAGE_URI);
    EClass class1Class = (EClass)subpackage1.getEClassifier("class1");
    EStructuralFeature feature = class1Class.getEStructuralFeature(featureName);

    CDOTransaction transaction = session.openTransaction();
    CDOObject instance = (CDOObject)transaction.getResource("/test1").getContents().get(0);
    assertEquals(isSet, instance.eIsSet(feature));

    transaction.getResource("/test1").getContents().remove(0);
    assertEquals(isSet, instance.eIsSet(feature));

    instance.eUnset(feature);
    assertEquals(false, instance.eIsSet(feature));
  }

  public void testBugzilla_258278_List() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(getModel1Package());

      CDOTransaction transaction = session.openTransaction();
      Order order = getModel1Factory().createOrder();
      assertFalse(order.eIsSet(getModel1Package().getOrder_OrderDetails()));

      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(order);
      assertFalse(order.eIsSet(getModel1Package().getOrder_OrderDetails()));

      transaction.commit();
      assertFalse(order.eIsSet(getModel1Package().getOrder_OrderDetails()));

      session.close();
    }

    clearCache(getRepository().getRevisionManager());
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    Order instance = (Order)transaction.getResource("/test1").getContents().get(0);
    assertFalse(instance.eIsSet(getModel1Package().getOrder_OrderDetails()));

    transaction.getResource("/test1").getContents().remove(0);
    assertFalse(instance.eIsSet(getModel1Package().getOrder_OrderDetails()));

    instance.getOrderDetails().add(getModel1Factory().createOrderDetail());
  }

  private EPackage createDynamicEPackage()
  {
    final EcoreFactory efactory = EcoreFactory.eINSTANCE;
    final EcorePackage epackage = EcorePackage.eINSTANCE;

    // Create a new EPackage and add the new EClasses
    EPackage topPackage = efactory.createEPackage();
    topPackage.setName("toppackage");
    topPackage.setNsPrefix("toppackage");
    topPackage.setNsURI(TOP_PACKAGE_URI);

    EPackage subPackage1 = efactory.createEPackage();
    subPackage1.setName("subPackage1");
    subPackage1.setNsPrefix("subPackage1");
    subPackage1.setNsURI(SUB_PACKAGE_URI);

    {
      EClass schoolBookEClass = efactory.createEClass();
      schoolBookEClass.setName("class1");
      // create a new attribute for this EClass
      EAttribute level = efactory.createEAttribute();
      level.setName("level");
      level.setEType(epackage.getEInt());
      // level.setUnsettable(true);
      level.setDefaultValue(new Integer(10));
      schoolBookEClass.getEStructuralFeatures().add(level);

      EAttribute settable = efactory.createEAttribute();
      settable.setName("settable");
      settable.setEType(epackage.getEString());
      // level.setUnsettable(true);
      settable.setDefaultValue(new String("Simon"));
      schoolBookEClass.getEStructuralFeatures().add(settable);

      EAttribute unsettable = efactory.createEAttribute();
      unsettable.setName("unsettable");
      unsettable.setEType(epackage.getEString());
      unsettable.setUnsettable(true);
      unsettable.setDefaultValue(new String("Simon"));
      schoolBookEClass.getEStructuralFeatures().add(unsettable);

      subPackage1.getEClassifiers().add(schoolBookEClass);
    }

    topPackage.getESubpackages().add(subPackage1);
    return topPackage;
  }
}
