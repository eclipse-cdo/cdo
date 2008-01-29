/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper() throws Exception{} Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution() throws Exception{} and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

import org.eclipse.emf.common.util.URI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class StateMachineTest extends AbstractCDOTest
{
  private static final long TIMESTAMP = 12345678L;

  // ///////////////////////////////////////////////////

  public void test_TRANSIENT_with_ATTACH() throws Exception
  {
    // Attach resource
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    assertNew(resource, transaction);
    assertEquals(URI.createURI("cdo:/test1"), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());

    // Attach single object
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    resource.getContents().add(supplier);
    assertNew(supplier, transaction);
    assertEquals(transaction, supplier.cdoView());
    assertEquals(resource, supplier.cdoResource());
    assertEquals(resource, supplier.eResource());
    assertEquals(null, supplier.eContainer());

    // Attach object tree
    Category cat1 = Model1Factory.eINSTANCE.createCategory();
    cat1.setName("CAT1");
    Category cat2 = Model1Factory.eINSTANCE.createCategory();
    cat2.setName("CAT2");
    cat1.getCategories().add(cat2);
    Product p1 = Model1Factory.eINSTANCE.createProduct();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    Product p2 = Model1Factory.eINSTANCE.createProduct();
    p2.setName("P2");
    cat1.getProducts().add(p2);
    Product p3 = Model1Factory.eINSTANCE.createProduct();
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
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    try
    {
      CDOStateMachine.INSTANCE.detach((InternalCDOObject)supplier);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_TRANSIENT_with_READ() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    CDOStateMachine.INSTANCE.read((InternalCDOObject)supplier);
  }

  public void test_TRANSIENT_with_WRITE() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    CDOStateMachine.INSTANCE.write((InternalCDOObject)supplier);
  }

  public void test_TRANSIENT_with_INVALIDATE() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    try
    {
      CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)supplier, TIMESTAMP);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_TRANSIENT_with_RELOAD() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    CDOStateMachine.INSTANCE.reload((InternalCDOObject)supplier);
    assertTransient(supplier);
  }

  public void test_TRANSIENT_with_COMMIT() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);
    try
    {
      CDOStateMachine.INSTANCE.commit((InternalCDOObject)supplier, new CommitTransactionResult(12345678L));
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_TRANSIENT_with_ROLLBACK() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    assertTransient(supplier);

    // Rollback locally
    try
    {
      CDOStateMachine.INSTANCE.rollback((InternalCDOObject)supplier, false);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }

    // Rollback remotely
    try
    {
      CDOStateMachine.INSTANCE.rollback((InternalCDOObject)supplier, true);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  // ///////////////////////////////////////////////////

  public void test_PREPARED_with_ATTACH() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    try
    {
      attach(supplier);
      fail("Expected NullPointerException due to revision==null");
    }
    catch (NullPointerException ex)
    {
    }
  }

  public void test_PREPARED_with_DETACH() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    try
    {
      CDOStateMachine.INSTANCE.detach((InternalCDOObject)supplier);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_PREPARED_with_READ() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    CDOStateMachine.INSTANCE.read((InternalCDOObject)supplier);
    assertEquals(CDOState.PREPARED, supplier.cdoState());
  }

  public void test_PREPARED_with_WRITE() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    try
    {
      CDOStateMachine.INSTANCE.write((InternalCDOObject)supplier);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_PREPARED_with_INVALIDATE() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    try
    {
      CDOStateMachine.INSTANCE.invalidate((InternalCDOObject)supplier, TIMESTAMP);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_PREPARED_with_RELOAD() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    try
    {
      reload(supplier);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_PREPARED_with_COMMIT() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    try
    {
      CDOStateMachine.INSTANCE.commit((InternalCDOObject)supplier, new CommitTransactionResult(TIMESTAMP));
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  public void test_PREPARED_with_ROLLBACK() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    ((InternalCDOObject)supplier).cdoInternalSetState(CDOState.PREPARED);
    try
    {
      CDOStateMachine.INSTANCE.rollback((InternalCDOObject)supplier, false);
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException ex)
    {
      assertFailure(ex);
    }
  }

  // ///////////////////////////////////////////////////

  public void test_NEW_with_ATTACH() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    assertNew(resource, transaction);

    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier); // ATTACH
    assertNew(supplier, transaction);
  }

  public void _____test_NEW_with_DETACH() throws Exception
  {
    // Detach single object
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    assertNew(resource, transaction);

    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier); // ATTACH
    assertNew(supplier, transaction);

    CDOStateMachine.INSTANCE.detach((InternalCDOObject)supplier);
    assertTransient(supplier);

    // Detach object tree
    Category cat1 = Model1Factory.eINSTANCE.createCategory();
    cat1.setName("CAT1");
    Category cat2 = Model1Factory.eINSTANCE.createCategory();
    cat2.setName("CAT2");
    cat1.getCategories().add(cat2);
    Product p1 = Model1Factory.eINSTANCE.createProduct();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    Product p2 = Model1Factory.eINSTANCE.createProduct();
    p2.setName("P2");
    cat1.getProducts().add(p2);
    Product p3 = Model1Factory.eINSTANCE.createProduct();
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

  public void test_REATTACH() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    Category cat1 = Model1Factory.eINSTANCE.createCategory();
    cat1.setName("CAT1");
    resource.getContents().add(cat1);
    Category cat2 = Model1Factory.eINSTANCE.createCategory();
    cat2.setName("CAT2");
    resource.getContents().add(cat2);
    Product p1 = Model1Factory.eINSTANCE.createProduct();
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

    CDOID id = p1.cdoID();
    cat2.getProducts().add(p1);
    assertNew(p1, transaction);
    assertEquals(id, p1.cdoID());
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

  public void test_NEW_with_RELOAD() throws Exception
  {
  }

  public void test_NEW_with_COMMIT() throws Exception
  {
  }

  public void test_NEW_with_ROLLBACK() throws Exception
  {
  }

  // // ///////////////////////////////////////////////////
  //
  // public void test_CLEAN_with_ATTACH() throws Exception
  // {
  // }
  //
  // public void test_CLEAN_with_DETACH() throws Exception
  // {
  // }
  //
  // public void test_CLEAN_with_READ() throws Exception
  // {
  // }
  //
  // public void test_CLEAN_with_WRITE() throws Exception
  // {
  // }
  //
  // public void test_CLEAN_with_INVALIDATE() throws Exception
  // {
  // }
  //
  // public void test_CLEAN_with_RELOAD() throws Exception
  // {
  // }
  //
  // public void test_CLEAN_with_COMMIT() throws Exception
  // {
  // }
  //
  // public void test_CLEAN_with_ROLLBACK() throws Exception
  // {
  // }
  //
  // // ///////////////////////////////////////////////////
  //
  // public void test_DIRTY_with_ATTACH() throws Exception
  // {
  // }
  //
  // public void test_DIRTY_with_DETACH() throws Exception
  // {
  // }
  //
  // public void test_DIRTY_with_READ() throws Exception
  // {
  // }
  //
  // public void test_DIRTY_with_WRITE() throws Exception
  // {
  // }
  //
  // public void test_DIRTY_with_INVALIDATE() throws Exception
  // {
  // }
  //
  // public void test_DIRTY_with_RELOAD() throws Exception
  // {
  // }
  //
  // public void test_DIRTY_with_COMMIT() throws Exception
  // {
  // }
  //
  // public void test_DIRTY_with_ROLLBACK() throws Exception
  // {
  // }
  //
  // // ///////////////////////////////////////////////////
  //
  // public void test_PROXY_with_ATTACH() throws Exception
  // {
  // }
  //
  // public void test_PROXY_with_DETACH() throws Exception
  // {
  // }
  //
  // public void test_PROXY_with_READ() throws Exception
  // {
  // }
  //
  // public void test_PROXY_with_WRITE() throws Exception
  // {
  // }
  //
  // public void test_PROXY_with_INVALIDATE() throws Exception
  // {
  // }
  //
  // public void test_PROXY_with_RELOAD() throws Exception
  // {
  // }
  //
  // public void test_PROXY_with_COMMIT() throws Exception
  // {
  // }
  //
  // public void test_PROXY_with_ROLLBACK() throws Exception
  // {
  // }
  //
  // // ///////////////////////////////////////////////////
  //
  // public void test_CONFLICT_with_ATTACH() throws Exception
  // {
  // }
  //
  // public void test_CONFLICT_with_DETACH() throws Exception
  // {
  // }
  //
  // public void test_CONFLICT_with_READ() throws Exception
  // {
  // }
  //
  // public void test_CONFLICT_with_WRITE() throws Exception
  // {
  // }
  //
  // public void test_CONFLICT_with_INVALIDATE() throws Exception
  // {
  // }
  //
  // public void test_CONFLICT_with_RELOAD() throws Exception
  // {
  // }
  //
  // public void test_CONFLICT_with_COMMIT() throws Exception
  // {
  // }
  //
  // public void test_CONFLICT_with_ROLLBACK() throws Exception
  // {
  // }

  // ///////////////////////////////////////////////////

  @Override
  protected boolean useJVMTransport()
  {
    return true;
  }

  private static void assertFailure(IllegalStateException ex)
  {
    ex.printStackTrace();
    assertTrue("Expected FAIL transition", ex.getMessage().startsWith("Failing event "));
  }

  private static void attach(CDOObject object) throws Exception
  {
    invokeMethod(object, "testAttach");
  }

  private static void reload(CDOObject object) throws Exception
  {
    invokeMethod(object, "testReload");
  }

  private static void invokeMethod(CDOObject object, String methodName) throws Exception
  {
    Method method = null;

    try
    {
      method = CDOStateMachine.class.getDeclaredMethod(methodName, new Class[] { InternalCDOObject.class });
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
      ex.printStackTrace();
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
