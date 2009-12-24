/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

/**
 * @author Eike Stepper
 */
public class UnsetTest extends AbstractCDOTest
{
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
    CDOResource resource = transaction.createResource("/test1");

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    resource.getContents().add(supplier);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource("/test1");
    supplier = (Supplier)resource.getContents().get(0);
    assertEquals(true, supplier.isPreferred());
  }

  public void testWriteDefaultValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    supplier.setPreferred(true);

    resource.getContents().add(supplier);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource("/test1");
    supplier = (Supplier)resource.getContents().get(0);
    assertEquals(true, supplier.isPreferred());
  }

  public void testWriteNonDefaultValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    supplier.setPreferred(false);

    resource.getContents().add(supplier);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource("/test1");
    supplier = (Supplier)resource.getContents().get(0);
    assertEquals(false, supplier.isPreferred());
  }

  public void testUnsetNonDefaultValue() throws Exception
  {
    Unsettable1 unsettable = getModel2Factory().createUnsettable1();
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
    assertEquals(false, result.isSetUnsettableVAT());
  }

  private <T extends EObject> T commitAndLoad(T object) throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    resource.getContents().add(object);
    transaction.commit();
    session.close();

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource("/test1");

    @SuppressWarnings("unchecked")
    T result = (T)resource.getContents().get(0);
    return result;
  }
}
