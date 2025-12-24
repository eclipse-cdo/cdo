/*
 * Copyright (c) 2008-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
@Requires(ISessionConfig.CAPABILITY_NET4J_JVM)
public class StateMachineTest extends AbstractCDOTest
{
  // ///////////////////////////////////////////////////

  public void test_TRANSIENT_with_ATTACH() throws Exception
  {
    // Attach resource
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    assertNew(resource, transaction);
    assertEquals(URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1")), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());

    // Attach single object
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    resource.getContents().add(supplier);
    assertNew(supplier, transaction);
    assertEquals(transaction, CDOUtil.getCDOObject(supplier).cdoView());
    assertEquals(resource, CDOUtil.getCDOObject(supplier).cdoDirectResource());
    assertEquals(resource, supplier.eResource());
    assertEquals(null, supplier.eContainer());

    // Attach object tree
    Category cat1 = getModel1Factory().createCategory();
    cat1.setName("CAT1");
    Category cat2 = getModel1Factory().createCategory();
    cat2.setName("CAT2");
    cat1.getCategories().add(cat2);
    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    Product1 p2 = getModel1Factory().createProduct1();
    p2.setName("P2");
    cat1.getProducts().add(p2);
    Product1 p3 = getModel1Factory().createProduct1();
    p3.setName("P3");
    cat2.getProducts().add(p3);
    assertTransient(cat1);
    assertTransient(cat2);
    assertTransient(p1);
    assertTransient(p2);
    assertTransient(p3);
    resource.getContents().add(cat1);
    assertNew(cat1, transaction);
    assertNew(cat2, transaction);
    assertNew(p1, transaction);
    assertNew(p2, transaction);
    assertNew(p3, transaction);
  }

  public void _____test_TRANSIENT_with_DETACH() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    try
    {
      detach(supplier);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      assertFailure(expected);
    }
  }

  public void test_TRANSIENT_with_READ() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    read(supplier);
  }

  public void test_TRANSIENT_with_WRITE() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    write(supplier);
  }

  public void test_TRANSIENT_with_INVALIDATE() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    invalidate(supplier);
    assertTransient(supplier);
  }

  public void test_TRANSIENT_with_ROLLBACK() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);

    // Rollback locally
    try
    {
      rollback(supplier);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      assertFailure(expected);
    }

    // Rollback remotely
    try
    {
      rollback(supplier);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      assertFailure(expected);
    }
  }

  // ///////////////////////////////////////////////////

  public void test_PREPARED_with_ATTACH() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    setState(supplier, CDOState.PREPARED);

    try
    {
      testAttach(CDOUtil.getCDOObject(supplier));
      fail("Expected NullPointerException due to revision==null");
    }
    catch (NullPointerException ex)
    {
    }
  }

  public void test_PREPARED_with_DETACH() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    setState(supplier, CDOState.PREPARED);

    try
    {
      detach(supplier);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      assertFailure(expected);
    }
  }

  public void test_PREPARED_with_READ() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    setState(supplier, CDOState.PREPARED);
    read(supplier);
    assertEquals(CDOState.PREPARED, CDOUtil.getCDOObject(supplier).cdoState());
  }

  public void test_PREPARED_with_WRITE() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    setState(supplier, CDOState.PREPARED);

    try
    {
      write(supplier);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      assertFailure(expected);
    }
  }

  public void test_PREPARED_with_INVALIDATE() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    setState(supplier, CDOState.PREPARED);

    try
    {
      invalidate(supplier);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      assertFailure(expected);
    }
  }

  public void test_PREPARED_with_ROLLBACK() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    setState(supplier, CDOState.PREPARED);

    try
    {
      rollback(supplier);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      assertFailure(expected);
    }
  }

  // ///////////////////////////////////////////////////

  public void test_NEW_with_ATTACH() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    assertNew(resource, transaction);

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier); // ATTACH
    assertNew(supplier, transaction);
  }

  public void _____test_NEW_with_DETACH() throws Exception
  {
    // Detach single object
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    assertNew(resource, transaction);

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier); // ATTACH
    assertNew(supplier, transaction);

    detach(supplier);
    assertTransient(supplier);

    // Detach object tree
    Category cat1 = getModel1Factory().createCategory();
    cat1.setName("CAT1");
    Category cat2 = getModel1Factory().createCategory();
    cat2.setName("CAT2");
    cat1.getCategories().add(cat2);
    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    Product1 p2 = getModel1Factory().createProduct1();
    p2.setName("P2");
    cat1.getProducts().add(p2);
    Product1 p3 = getModel1Factory().createProduct1();
    p3.setName("P3");
    cat2.getProducts().add(p3);
    resource.getContents().add(cat1);
    assertNew(cat1, transaction);
    assertNew(cat2, transaction);
    assertNew(p1, transaction);
    assertNew(p2, transaction);
    assertNew(p3, transaction);
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    resource.getContents().remove(cat1);
    assertTransient(cat1);
    assertTransient(cat2);
    assertTransient(p1);
    assertTransient(p2);
    assertTransient(p3);
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void test_REATTACH() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Category cat1 = getModel1Factory().createCategory();
    cat1.setName("CAT1");
    resource.getContents().add(cat1);
    Category cat2 = getModel1Factory().createCategory();
    cat2.setName("CAT2");
    resource.getContents().add(cat2);
    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    assertNew(cat1, transaction);
    assertNew(cat2, transaction);
    assertNew(p1, transaction);

    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();

    CDOID id = CDOUtil.getCDOObject(p1).cdoID();
    cat2.getProducts().add(p1);
    assertNew(p1, transaction);
    assertEquals(id, CDOUtil.getCDOObject(p1).cdoID());
  }

  public void test_NEW_with_READ() throws Exception
  {
  }

  public void test_NEW_with_WRITE() throws Exception
  {
  }

  public void test_NEW_with_INVALIDATE() throws Exception
  {
  }

  public void test_NEW_with_COMMIT() throws Exception
  {
  }

  public void test_NEW_with_ROLLBACK() throws Exception
  {
  }

  private static void assertFailure(IllegalStateException ex)
  {
    IOUtil.print(ex);
    assertEquals("Expected FAIL transition", true, ex.getMessage().startsWith("Failing event "));
  }

  private static void setState(EObject object, CDOState state)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      ((InternalCDOObject)cdoObject).cdoInternalSetState(state);
    }
  }

  private static void detach(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      CDOStateMachine.INSTANCE.detach((InternalCDOObject)cdoObject);
    }
  }

  private static void read(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      CDOStateMachine.INSTANCE.read((InternalCDOObject)cdoObject);
    }
  }

  private static void write(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      CDOStateMachine.INSTANCE.write((InternalCDOObject)cdoObject, null);
    }
  }

  private static void invalidate(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)cdoObject, null);
    }
  }

  private static void rollback(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      CDOStateMachine.INSTANCE.rollback((InternalCDOObject)cdoObject, (InternalCDOTransaction)cdoObject.cdoView());
    }
  }

  private static void testAttach(EObject object) throws Exception
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      invokeMethod(cdoObject, "testAttach");
    }
  }

  private static void invokeMethod(CDOObject object, String methodName) throws Exception
  {
    Method method = null;

    try
    {
      method = CDOStateMachine.class.getDeclaredMethod(methodName, InternalCDOObject.class);
      method.setAccessible(true);
      method.invoke(CDOStateMachine.INSTANCE, object);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (InvocationTargetException ex)
    {
      throw (Exception)ex.getTargetException();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
      fail("Reflection problem: " + ex.getMessage());
    }
    finally
    {
      if (method != null)
      {
        method.setAccessible(false);
      }
    }
  }
}
