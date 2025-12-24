/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Steve Monnier - initial API and implementation
 *    Eike Stepper - adapted for more correct test model definition
 *    Christian W. Damus (CEA) - adapted for new test model with unsettable attribute
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import static org.junit.Assert.assertNotEquals;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.tests.model3.subpackage.legacy.SubpackageFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * Bug 405543 - An unsettable many-valued reference that is set to an empty list is unset on new transaction.
 *
 * @author Steve Monnier
 */
public class Bugzilla_405543_Test extends AbstractCDOTest
{
  /**
   * This test validates that an unset list isSet method is false.
   */
  public void testIsUnsetOnUnsettableReference() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    Class1 element = getModel3Factory().createClass1();
    element.setAdditionalValue("Arbitrary value to cause eSettings to be non-null");
    assertFalse(element.isSetClass2());

    resource.getContents().add(element);
    assertFalse(element.isSetClass2());

    transaction.commit();
    assertFalse(element.isSetClass2());

    // Closing/Reopening Transaction and session.
    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource(getResourcePath("/res"));

    element = (Class1)resource.getContents().get(0);
    assertFalse(element.isSetClass2());

    Class2 element2 = getModel3SubpackageFactory().createClass2();
    element.getClass2().add(element2);
    assertTrue(element.isSetClass2());

    resource.getContents().add(element2);
    assertTrue(element.isSetClass2());

    transaction.commit();
    assertTrue(element.isSetClass2());

    // Closing/Reopening Transaction and session.
    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource(getResourcePath("/res"));

    element = (Class1)resource.getContents().get(0);
    assertTrue(element.isSetClass2());

    element.unsetClass2();
    assertFalse(element.isSetClass2());

    transaction.commit();
    assertFalse(element.isSetClass2());

    // Closing/Reopening Transaction and session.
    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource(getResourcePath("/res"));

    element = (Class1)resource.getContents().get(0);
    assertFalse(element.isSetClass2());
  }

  /**
   * This test validates that an empty unsettable reference isSet method is true.
   * In this test, the reference is set before the first commit.
   */
  public void testIsSetOnUnsettableReference() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    Class1 element = getModel3Factory().createClass1();
    Class2 element2 = getModel3SubpackageFactory().createClass2();
    assertFalse(element.isSetClass2());

    element.getClass2().add(element2);
    assertTrue(element.isSetClass2());

    element.getClass2().remove(element2);
    assertTrue(element.isSetClass2());

    resource.getContents().add(element);
    assertTrue(element.isSetClass2());

    resource.getContents().add(element2);
    assertTrue(element.isSetClass2());

    transaction.commit();
    assertTrue(element.isSetClass2());

    // Closing/Reopening Transaction and session.
    transaction.close();
    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource(getResourcePath("/res"));
    element = (Class1)resource.getContents().get(0);

    // Validate that class1 reference 'class2' is still set but empty.
    assertTrue(element.isSetClass2());
  }

  /**
   * This test validates that an empty unsettable reference isSet method is true.
   * In this test, the reference is set after the first commit.
   */
  public void testIsSetOnUnsettableReference2() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    // Create an object and set is a string attribute to the empty string.
    Class1 element = getModel3Factory().createClass1();
    Class2 element2 = getModel3SubpackageFactory().createClass2();

    resource.getContents().add(element);
    assertFalse(element.isSetClass2());

    resource.getContents().add(element2);
    assertFalse(element.isSetClass2());

    transaction.commit();
    assertFalse(element.isSetClass2());

    element.getClass2().add(element2);
    assertTrue(element.isSetClass2());

    element.getClass2().remove(element2);
    assertTrue(element.isSetClass2());

    // Commit and reopen transaction on a new session.
    transaction.commit();
    session.close();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource(getResourcePath("/res"));
    element = (Class1)resource.getContents().get(0);

    // Validate that class1 reference 'class2' is still set but empty.
    assertTrue(element.isSetClass2());
  }

  public static void main(String[] args)
  {
    Class1 element = Model3Factory.eINSTANCE.createClass1();
    assertFalse(element.isSetClass2());

    EList<Class2> list = element.getClass2();
    assertNotEquals(null, list);
    assertEquals(0, list.size());
    assertTrue(list.isEmpty());
    assertFalse(((InternalEList.Unsettable<Class2>)list).isSet());

    Object[] array1 = list.toArray();
    assertNotEquals(null, array1);
    assertEquals(0, array1.length);

    Class2[] array2 = list.toArray(new Class2[list.size()]);
    assertNotEquals(null, array2);
    assertEquals(0, array2.length);

    assertEquals(-1, list.indexOf(SubpackageFactory.eINSTANCE.createClass2()));
    assertEquals(-1, list.lastIndexOf(SubpackageFactory.eINSTANCE.createClass2()));
    assertFalse(list.remove(SubpackageFactory.eINSTANCE.createClass2()));
    assertFalse(list.contains(SubpackageFactory.eINSTANCE.createClass2()));

    try
    {
      list.get(0);
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // SUCCESS
    }

    try
    {
      list.remove(0);
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // SUCCESS
    }

    try
    {
      list.move(0, SubpackageFactory.eINSTANCE.createClass2());
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // SUCCESS
    }
  }
}
