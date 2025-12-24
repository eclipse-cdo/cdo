/*
 * Copyright (c) 2009-2013, 2016, 2018, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class UnsetTest extends AbstractCDOTest
{
  /**
   * needed for commitAndLoadTx()
   */
  private CDOSession commitAndLoadSession;

  /**
   * needed for commitAndLoadTx()
   */
  private CDOTransaction commitAndLoadTransaction;

  /**
   * Ensures that properly typed (i.e. usable) default values can be read from dynamic packages.
   * <p>
   * Works only in standalone mode.
   */
  public static void main(String[] args)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

    URI uri = URI.createFileURI("../org.eclipse.emf.cdo.tests.model2/model/model2.ecore");
    Resource resource = resourceSet.getResource(uri, true);

    EPackage model2 = (EPackage)resource.getContents().get(0);
    EClass unsettable2 = (EClass)model2.getEClassifier("Unsettable2WithDefault");
    EAttribute unsettableInt = (EAttribute)unsettable2.getEStructuralFeature("unsettableInt");

    // Check static default value
    Integer defaultValue = (Integer)unsettableInt.getDefaultValue();
    assertEquals(5, (int)defaultValue);

    // Check dynamic default value
    EObject object = EcoreUtil.create(unsettable2);
    Integer value = (Integer)object.eGet(unsettableInt);
    assertEquals((int)defaultValue, (int)value);
  }

  public void testReadDefaultValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    resource.getContents().add(supplier);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource(getResourcePath("/test1"));
    supplier = (Supplier)resource.getContents().get(0);
    assertEquals(true, supplier.isPreferred());
  }

  public void testWriteDefaultValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    supplier.setPreferred(true);

    resource.getContents().add(supplier);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource(getResourcePath("/test1"));
    supplier = (Supplier)resource.getContents().get(0);
    assertEquals(true, supplier.isPreferred());
  }

  public void testWriteNonDefaultValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    supplier.setPreferred(false);

    resource.getContents().add(supplier);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource(getResourcePath("/test1"));
    supplier = (Supplier)resource.getContents().get(0);
    assertEquals(false, supplier.isPreferred());
  }

  public void testUnsetNonDefaultValue() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    // unsettable.setUnsettableBoolean(true);
    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetBoolean() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableBoolean(true);

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(true, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetByte() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableByte(Byte.MAX_VALUE);
    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(true, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetChar() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableChar('a');

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(true, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetDate() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableDate(new Date());

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(true, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetDouble() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableDouble(42.34d);

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(true, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetFloat() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableFloat(42.34f);

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(true, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetLong() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableLong(42L);

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(true, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetShort() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableShort(Short.MAX_VALUE);

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(true, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetString() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableString("42");

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(true, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testIsSetVAT() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableVAT(VAT.VAT15);

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(true, result.isSetUnsettableVAT());
  }

  public void testIsSetMultipleTimes() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableInt(42);

    Unsettable1 result = commitAndLoadTx(unsettable);
    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(true, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());

    result.setUnsettableByte(Byte.MAX_VALUE);
    result = commitAndLoadTx(result);

    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(true, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(true, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());

    result.unsetUnsettableByte();
    result.unsetUnsettableInt();
    result.setUnsettableString("blah");

    result = commitAndLoadTx(result);

    assertEquals(false, result.isSetUnsettableBoolean());
    assertEquals(false, result.isSetUnsettableByte());
    assertEquals(false, result.isSetUnsettableChar());
    assertEquals(false, result.isSetUnsettableDate());
    assertEquals(false, result.isSetUnsettableDouble());
    assertEquals(false, result.isSetUnsettableFloat());
    assertEquals(false, result.isSetUnsettableInt());
    assertEquals(false, result.isSetUnsettableLong());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(false, result.isSetUnsettableShort());
    assertEquals(true, result.isSetUnsettableString());
    assertEquals(false, result.isSetUnsettableVAT());
  }

  public void testUnsettableBaseTypeVsObjectType() throws Exception
  {
    EPackage pkg = createUniquePackage();
    EClass cls = EMFUtil.createEClass(pkg, "unsettableClass", false, false);
    EAttribute baseElement = EMFUtil.createEAttribute(cls, "baseElement", EcorePackage.eINSTANCE.getEInt());
    baseElement.setUnsettable(true);
    baseElement.setDefaultValue(23);

    EAttribute objectElement = EMFUtil.createEAttribute(cls, "objectElement", EcorePackage.eINSTANCE.getEIntegerObject());
    objectElement.setUnsettable(true);
    objectElement.setDefaultValue(42);
    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    {
      EObject test1 = EcoreUtil.create(cls);
      test1.eSet(baseElement, 1);
      test1.eSet(objectElement, 2);

      EObject test2 = EcoreUtil.create(cls);
      test2.eSet(baseElement, 23);
      test2.eSet(objectElement, 42);

      EObject test3 = EcoreUtil.create(cls);
      test3.eSet(baseElement, null);
      test3.eSet(objectElement, null);

      EObject test4 = EcoreUtil.create(cls);
      test4.eUnset(baseElement);
      test4.eUnset(objectElement);

      assertEquals(true, test1.eIsSet(baseElement));
      assertEquals(true, test1.eIsSet(objectElement));
      assertEquals(1, test1.eGet(baseElement));
      assertEquals(2, test1.eGet(objectElement));

      assertEquals(true, test2.eIsSet(baseElement));
      assertEquals(true, test2.eIsSet(objectElement));
      assertEquals(23, test2.eGet(baseElement));
      assertEquals(42, test2.eGet(objectElement));

      // for basetypes, setting value null seems to be equivalent
      // to unset.
      assertEquals(false, test3.eIsSet(baseElement));
      assertEquals(true, test3.eIsSet(objectElement));
      assertEquals(23, test3.eGet(baseElement));
      assertNull(test3.eGet(objectElement));

      assertEquals(false, test4.eIsSet(baseElement));
      assertEquals(false, test4.eIsSet(objectElement));
      assertEquals(23, test4.eGet(baseElement));
      assertEquals(42, test4.eGet(objectElement));

      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(pkg);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/test1"));

      res.getContents().add(test1);
      res.getContents().add(test2);
      res.getContents().add(test3);
      res.getContents().add(test4);

      transaction.commit();
      transaction.close();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(pkg);
      CDOView view = session.openTransaction();
      CDOResource res = view.getResource(getResourcePath("/test1"));

      assertEquals(4, res.getContents().size());
      EObject test1 = res.getContents().get(0);
      EObject test2 = res.getContents().get(1);
      EObject test3 = res.getContents().get(2);
      EObject test4 = res.getContents().get(3);

      assertEquals(true, test1.eIsSet(baseElement));
      assertEquals(true, test1.eIsSet(objectElement));
      assertEquals(1, test1.eGet(baseElement));
      assertEquals(2, test1.eGet(objectElement));

      assertEquals(true, test2.eIsSet(baseElement));
      assertEquals(true, test2.eIsSet(objectElement));
      assertEquals(23, test2.eGet(baseElement));
      assertEquals(42, test2.eGet(objectElement));

      assertEquals(false, test3.eIsSet(baseElement));
      assertEquals(true, test3.eIsSet(objectElement));
      assertEquals(23, test3.eGet(baseElement));
      assertNull(test3.eGet(objectElement));

      assertEquals(false, test4.eIsSet(baseElement));
      assertEquals(false, test4.eIsSet(objectElement));
      assertEquals(23, test4.eGet(baseElement));
      assertEquals(42, test4.eGet(objectElement));

      view.close();
      session.close();
    }
  }

  public void testUnsettableObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    resource.getContents().add(unsettable);
    assertEquals(false, unsettable.isSetUnsettableElement());
    transaction.commit();
    assertEquals(false, unsettable.isSetUnsettableElement());

    unsettable.setUnsettableElement(null);
    assertEquals(true, unsettable.isSetUnsettableElement());
    transaction.commit();
    assertEquals(true, unsettable.isSetUnsettableElement());

    unsettable.unsetUnsettableElement();
    assertEquals(false, unsettable.isSetUnsettableElement());
    transaction.commit();
    assertEquals(false, unsettable.isSetUnsettableElement());

    Company company = getModel1Factory().createCompany();
    unsettable.eResource().getContents().add(company);
    unsettable.setUnsettableElement(company);
    assertEquals(true, unsettable.isSetUnsettableElement());
    transaction.commit();
    assertEquals(true, unsettable.isSetUnsettableElement());
  }

  public void testIsSetElement() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
    unsettable.setUnsettableElement(null);

    Unsettable1 result = commitAndLoad(unsettable);
    assertEquals(true, result.isSetUnsettableElement());
  }

  @Override
  protected void doTearDown() throws Exception
  {
    commitAndLoadSession = null;
    commitAndLoadTransaction = null;
    super.doTearDown();
  }

  private <T extends EObject> T commitAndLoad(T object) throws Exception
  {
    msg("Committing...");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    resource.getContents().add(object);
    transaction.commit();
    session.close();

    clearCache(getRepository().getRevisionManager());

    msg("Loading...");
    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource(getResourcePath("/test1"));

    @SuppressWarnings("unchecked")
    T result = (T)resource.getContents().get(0);
    return result;
  }

  private <T extends EObject> T commitAndLoadTx(T object) throws Exception
  {
    if (commitAndLoadSession == null)
    {
      commitAndLoadSession = openSession();
      commitAndLoadTransaction = commitAndLoadSession.openTransaction();

      CDOResource resource = commitAndLoadTransaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(object);
    }

    commitAndLoadTransaction.commit();
    commitAndLoadTransaction.close();
    commitAndLoadSession.close();

    clearCache(getRepository().getRevisionManager());

    commitAndLoadSession = openSession();
    commitAndLoadTransaction = commitAndLoadSession.openTransaction();
    CDOResource resource = commitAndLoadTransaction.getResource(getResourcePath("/test1"));

    @SuppressWarnings("unchecked")
    T result = (T)resource.getContents().get(0);
    return result;
  }
}
