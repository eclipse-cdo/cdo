/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Lehmann - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ResolutionPreference;

import java.util.List;

/**
 * @author Pascal Lehmann
 */
public class ConflictResolverExtendedTest extends AbstractCDOTest
{
  private static final String TEST_RESOURCE_NAME = "/test1";

  // --- initialize model ----------------------------------------------------

  private void initTestModelSimple() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath(TEST_RESOURCE_NAME));

    Root testRoot = getModel6Factory().createRoot();
    resource.getContents().add(testRoot);

    BaseObject bObject1 = createBaseObject("BaseObject 1");
    BaseObject bObject2 = createBaseObject("BaseObject 2");
    BaseObject bObject3 = createBaseObject("BaseObject 3");

    testRoot.getListA().add(bObject1);
    testRoot.getListA().add(bObject2);
    testRoot.getListA().add(bObject3);

    ContainmentObject cObject1L1 = createContainmentObject("ContainmentObject 1 - Level 1");
    ContainmentObject cObject1L2 = createContainmentObject("ContainmentObject 1 - Level 2");
    cObject1L1.getContainmentList().add(cObject1L2);

    testRoot.getListB().add(cObject1L1);

    transaction.commit();
  }

  private void initTestModel() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath(TEST_RESOURCE_NAME));

    Root root = getModel6Factory().createRoot();
    resource.getContents().add(root);

    // setup base objects.
    BaseObject bObject0 = createBaseObject("BaseObject 0");
    BaseObject bObject1 = createBaseObject("BaseObject 1");
    BaseObject bObject2 = createBaseObject("BaseObject 2");
    BaseObject bObject3 = createBaseObject("BaseObject 3");
    BaseObject bObject4 = createBaseObject("BaseObject 4");

    root.getListA().add(bObject0);
    root.getListA().add(bObject1);
    root.getListA().add(bObject2);
    root.getListA().add(bObject3);
    root.getListA().add(bObject4);

    // containment objects.
    ContainmentObject cObject0 = createContainmentObject("ContainmentObject 0");
    ContainmentObject cObject00 = createContainmentObject("ContainmentObject 00");
    BaseObject bcObject01 = createBaseObject("BaseContainmentObject 01");

    cObject00.getContainmentList().add(bcObject01);
    cObject0.getContainmentList().add(cObject00);

    ContainmentObject cObject1 = createContainmentObject("ContainmentObject 1");

    root.getListB().add(cObject0);
    root.getListB().add(cObject1);

    // commit the model.
    transaction.commit();
  }

  // --- conflict test -------------------------------------------------------

  public void _testProvokeConflictOnServerTest() throws Exception
  {
    // should provoke an "attempt to modify historical revision" error.
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();

    initTestModelSimple();

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
    assertNotNull(thisObject);
    BaseObject thatObject = thatTransaction.getObject(thisObject);

    thisObject.setAttributeOptional("this");
    thatObject.setAttributeOptional("that");

    thisTransaction.commit();

    try
    {
      commitAndSync(thatTransaction, thisTransaction);
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
    }
  }

  public void _testProvokeConflictLocalTest() throws Exception
  {
    // should provoke an "this transaction has conflicts" error.
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();

    initTestModelSimple();

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
    assertNotNull(thisObject);
    BaseObject thatObject = thatTransaction.getObject(thisObject);

    thisObject.setAttributeOptional("this");
    thatObject.setAttributeOptional("that");

    commitAndSync(thisTransaction, thatTransaction);

    try
    {
      commitAndSync(thatTransaction, thisTransaction);
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
    }
  }

  // --- single value conflict resolver tests --------------------------

  public void testChangeChangeTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();

    initTestModelSimple();
    addConflictResolver(thatTransaction);

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
    thisObject.setAttributeOptional("this");

    BaseObject thatObject = thatTransaction.getObject(thisObject);
    thatObject.setAttributeOptional("that");

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testRemoveChangeTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();

    initTestModelSimple();
    addConflictResolver(thatTransaction);

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
    assertNotNull(thisObject);

    BaseObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);

    // remove object.
    getTestModelRoot(thisTransaction).getListA().remove(0);

    // change object.
    thatObject.setAttributeOptional("that");

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testChangeRemoveTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();

    // add conflict resolver.
    addConflictResolver(thatTransaction);

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
    assertNotNull(thisObject);
    BaseObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);
    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // change object.
    thisObject.setAttributeOptional("this");

    // remove object.
    Root thatRoot = thatTransaction.getObject(getTestModelRoot(thisTransaction));
    thatRoot.getListA().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testRemoveAddTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();

    // add conflict resolver.
    addConflictResolver(thatTransaction);

    ContainmentObject thisObject = (ContainmentObject)getTestModelRoot(thisTransaction).getListB().get(0);
    assertNotNull(thisObject);
    ContainmentObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);

    // remove object.
    getTestModelRoot(thisTransaction).getListB().remove(0);

    // add object.
    BaseObject addObject = createBaseObject("AddObject");

    ContainmentObject thatChild = (ContainmentObject)thatObject.getContainmentList().get(0);
    assertNotNull(thatChild);
    thatChild.getContainmentList().add(addObject);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testAddRemoveTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();

    // add conflict resolver.
    addConflictResolver(thatTransaction);

    ContainmentObject thisObject = (ContainmentObject)getTestModelRoot(thisTransaction).getListB().get(0);
    assertNotNull(thisObject);
    ContainmentObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);
    int objects = getTestModelRoot(thisTransaction).getListB().size();

    // add object.
    BaseObject addObject = createBaseObject("AddObject");

    ContainmentObject thisChild = (ContainmentObject)thisObject.getContainmentList().get(0);
    assertNotNull(thisChild);
    thisChild.getContainmentList().add(addObject);

    // remove object.
    Root thatRoot = thatTransaction.getObject(getTestModelRoot(thisTransaction));
    thatRoot.getListB().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListB().size());
  }

  public void testRemoveRemoveTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();

    // add conflict resolver.
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    ContainmentObject thisObject = (ContainmentObject)thisRoot.getListB().get(0);
    assertNotNull(thisObject);

    ContainmentObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);

    // remove object.
    thisRoot.getListB().remove(0);

    // remove object.
    thatRoot.getListB().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(false, thatTransaction.hasConflict());
  }

  // --- resolve many valued changes -------------------------------------

  public void testManyValuedAddAddTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Add elements
    thisList.add(createBaseObject("ThisObject"));
    thatList.add(createBaseObject("ThatObject"));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(objects + 2, thisList.size());
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
  }

  public void testManyValuedAddChangeTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // create new objects.
    BaseObject thisObject = createBaseObject("ThisObject");

    // add elements.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().add(thisObject);

    // move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    EList<BaseObject> containmentList = thatRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects + 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedChangeAddTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // create new objects.
    BaseObject thatObject = createBaseObject("ThatObject");

    // add elements.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects + 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedAddRemoveTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // create new objects.
    BaseObject thisObject = createBaseObject("ThisObject");

    // add elements.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().add(thisObject);

    // remove element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    thatRoot.getListA().remove(1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedRemoveAddTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Remove 1 element
    thisList.remove(1);

    // Add 1 element
    thatList.add(createBaseObject("ThatObject"));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, thisList.size());
  }

  public void testManyValuedChangeRemoveTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // remove element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    thatRoot.getListA().remove(1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedChangeRemoveTest2() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    BaseObject this0 = thisList.get(0);
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Move first element to end
    thisList.move(2, this0);

    // Remove first element
    thatList.remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, thisList.size());
  }

  public void testManyValuedRemoveChangeTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // remove element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().remove(1);

    // move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    EList<BaseObject> listA = thatRoot.getListA();
    listA.move(2, 0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedRemoveChangeTest2() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    // Remove element
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().remove(0);

    // Move same element
    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> containmentList = thatRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(false, thatTransaction.hasConflict());
  }

  public void testManyValuedChangeChangeTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    EList<BaseObject> thatContainmentList = thatRoot.getListA();
    thatContainmentList.move(2, thatContainmentList.get(1));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedChangeChangeTest2() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    EList<BaseObject> thatContainmentList = thatRoot.getListA();
    thatContainmentList.move(2, thatContainmentList.get(0));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedRemoveRemoveTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // remove element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().remove(0);

    // remove element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    thatRoot.getListA().remove(2);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 2, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedRemoveRemoveTest2() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // remove element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().remove(0);

    // remove same element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    thatRoot.getListA().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedAddAddRemoveRemoveTest() throws Exception
  {
    // test to produce exception of bug #306710
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Add 2 elements and remove 1 element
    thisList.add(0, createBaseObject("ThisObject1"));
    thisList.add(0, createBaseObject("ThisObject2"));
    thisList.remove(4);

    // Remove 1 element
    thatList.remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, thisList.size());
  }

  public void testManyValuedAddAddRemoveRemove2Test() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Remove elements
    thisList.remove(0);
    thisList.remove(0);

    // Add elements
    thatList.add(createBaseObject("ThatObject1"));
    thatList.add(createBaseObject("ThatObject2"));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, thisList.size());

  }

  public void testManyValuedRemoveRemoveAddAddTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModelSimple();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // add elements.
    BaseObject thisObject = createBaseObject("ThisObject");
    BaseObject thisObject2 = createBaseObject("ThisObject2");

    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().add(thisObject);
    thisRoot.getListA().add(thisObject2);

    // remove elements.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);
    thatRoot.getListA().remove(0);
    thatRoot.getListA().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, getTestModelRoot(thisTransaction).getListA().size());

  }

  // --- list conflict resolver tests ----------------------------------------

  public void testAddHeadAddHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    EList<BaseObject> thisListA = thisRoot.getListA();
    thisListA.add(0, thisObject);

    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    EList<BaseObject> thatListA = thatRoot.getListA();
    thatListA.add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisListA);
    printList("That ", thatListA);

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisListA.get(1), thisObject);
    assertEquals(thisListA.get(0), thisTransaction.getObject(thatObject));
    assertEquals(thatListA.get(1), thatTransaction.getObject(thisObject));
    assertEquals(thatListA.get(0), thatObject);
  }

  public void testAddHeadAddTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Add element to head
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisList.add(0, thisObject);

    // Add element to tail
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatList.add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects + 2, thisList.size());
    assertEquals(objects + 2, thatList.size());
  }

  public void testAddTailAddTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(size - 1), thisObject);
    assertEquals(thisRoot.getListA().get(size - 2), thisTransaction.getObject(thatObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(size - 2), thatObject);
  }

  public void testAddTailAddHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Add element to tail
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisList.add(thisObject);

    // Add element to head
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatList.add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices
    int size = thisList.size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisList.get(size - 1), thisObject);
    assertEquals(thisList.get(0), thisTransaction.getObject(thatObject));
    assertEquals(thatList.get(size - 1), thatTransaction.getObject(thisObject));
    assertEquals(thatList.get(0), thatObject);
  }

  // add - remove

  public void testAddHeadRemoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(0, thisObject);

    // remove object (get it before deletion).
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    assertNotNull(thatRemoveObject);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    assertNotNull(thisRemoveObject);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(0), thisObject);
    assertEquals(thisRoot.getListA().get(1), thisTransaction.getObject(thatAfterRemoveObject));
    assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(1), thatAfterRemoveObject);
    assertEquals(CDOUtil.getCDOObject(thisRemoveObject).cdoState(), CDOState.INVALID);
    assertEquals(CDOUtil.getCDOObject(thatRemoveObject).cdoState(), CDOState.TRANSIENT);
  }

  public void testAddHeadRemoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(0, thisObject);

    // remove object (get it before deletion).
    int listSize = thatRoot.getListA().size();
    BaseObject thatRemoveObject = thatRoot.getListA().get(listSize - 1);
    assertNotNull(thatRemoveObject);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    assertNotNull(thisRemoveObject);

    thatRoot.getListA().remove(listSize - 1);
    BaseObject thatBeforeRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(0), thisObject);
    assertEquals(thisRoot.getListA().get(size - 1), thisTransaction.getObject(thatBeforeRemoveObject));
    assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatBeforeRemoveObject);
    assertEquals(CDOUtil.getCDOObject(thisRemoveObject).cdoState(), CDOState.INVALID);
    assertEquals(CDOUtil.getCDOObject(thatRemoveObject).cdoState(), CDOState.TRANSIENT);
  }

  public void testAddTailRemoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // remove object (get it before deletion).
    int listSize = thatRoot.getListA().size();
    BaseObject thatRemoveObject = thatRoot.getListA().get(listSize - 1);
    assertNotNull(thatRemoveObject);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    assertNotNull(thisRemoveObject);

    thatRoot.getListA().remove(listSize - 1);
    BaseObject thatBeforeRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(size - 1), thisObject);
    assertEquals(thisRoot.getListA().get(size - 2), thisTransaction.getObject(thatBeforeRemoveObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(size - 2), thatBeforeRemoveObject);
    assertEquals(CDOUtil.getCDOObject(thisRemoveObject).cdoState(), CDOState.INVALID);
    assertEquals(CDOUtil.getCDOObject(thatRemoveObject).cdoState(), CDOState.TRANSIENT);
  }

  public void testAddTailRemoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // remove object (get it before deletion).
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    assertNotNull(thatRemoveObject);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    assertNotNull(thisRemoveObject);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(size - 1), thisObject);
    assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatAfterRemoveObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(0), thatAfterRemoveObject);
    assertEquals(CDOUtil.getCDOObject(thisRemoveObject).cdoState(), CDOState.INVALID);
    assertEquals(CDOUtil.getCDOObject(thatRemoveObject).cdoState(), CDOState.TRANSIENT);
  }

  // add - move

  public void testAddHeadMoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(0, thisObject);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(0), thisObject);
    assertEquals(thisRoot.getListA().get(size - 1), thisTransaction.getObject(thatMoveObject));
    assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatMoveObject);
  }

  public void testAddHeadMoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Add element to head
    thisList.add(0, createBaseObject("ThisBaseObject 0"));

    // Move head element
    thatList.move(0, thatList.size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects + 1, thisList.size());
    assertEquals(objects + 1, thatList.size());
  }

  public void testAddTailMoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(size - 1), thisObject);
    assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatMoveObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(0), thatMoveObject);
  }

  public void testAddTailMoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // remove object.
    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(size - 1), thisObject);
    assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatAfterRemoveObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(0), thatAfterRemoveObject);
  }

  public void testAddHeadClearTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(0, thisObject);

    // clear list.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(false, thatTransaction.hasConflict());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisObject).cdoState());
  }

  public void testAddTailClearTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // clear list.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(false, thatTransaction.hasConflict());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisObject).cdoState());
  }

  public void testRemoveHeadAddHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatObject));
    assertEquals(thisRoot.getListA().get(1), thisAfterRemoveObject);
    assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thatObject));
    assertEquals(thatRoot.getListA().get(1), thatTransaction.getObject(thisAfterRemoveObject));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testRemoveHeadAddTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(listSize - 1), thisTransaction.getObject(thatObject));
    assertEquals(thisRoot.getListA().get(0), thisAfterRemoveObject);
    assertEquals(thatRoot.getListA().get(listSize - 1), thatTransaction.getObject(thatObject));
    assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thisAfterRemoveObject));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testRemoveTailAddHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatObject));
    assertEquals(thisRoot.getListA().get(listSize - 1), thisAfterRemoveObject);
    assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thatObject));
    assertEquals(thatRoot.getListA().get(listSize - 1), thatTransaction.getObject(thisAfterRemoveObject));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testRemoveTailAddTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(listSize - 1), thisTransaction.getObject(thatObject));
    assertEquals(thisRoot.getListA().get(listSize - 2), thisAfterRemoveObject);
    assertEquals(thatRoot.getListA().get(listSize - 1), thatTransaction.getObject(thatObject));
    assertEquals(thatRoot.getListA().get(listSize - 2), thatTransaction.getObject(thisAfterRemoveObject));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testRemoveHeadRemoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject1);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);
    assertNotNull(thatRemoveObject1);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(0);

    // // remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(0);
    assertNotNull(thatRemoveObject2);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);
    assertNotNull(thisRemoveObject2);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject1, thisRoot.getListA().get(0));
    assertEquals(thatAfterRemoveObject2, thatRoot.getListA().get(0));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject1).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject1).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject2).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject2).cdoState());
  }

  public void testRemoveHeadRemoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject1);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);
    assertNotNull(thatRemoveObject1);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(0);

    // // remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    assertNotNull(thatRemoveObject2);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);
    assertNotNull(thisRemoveObject2);

    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject1, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject1), thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject2), thisRoot.getListA().get(listSize - 1));
    assertEquals(thatAfterRemoveObject2, thatRoot.getListA().get(listSize - 1));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject1).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject1).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject2).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject2).cdoState());
  }

  public void testRemoveTailRemoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    assertNotNull(thisRemoveObject1);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);
    assertNotNull(thatRemoveObject1);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // // remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(0);
    assertNotNull(thatRemoveObject2);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);
    assertNotNull(thisRemoveObject2);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject1, thisRoot.getListA().get(listSize - 1));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject1), thatRoot.getListA().get(listSize - 1));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject2), thisRoot.getListA().get(0));
    assertEquals(thatAfterRemoveObject2, thatRoot.getListA().get(0));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject1).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject1).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject2).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject2).cdoState());
  }

  public void testRemoveTailRemoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    assertNotNull(thisRemoveObject1);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);
    assertNotNull(thatRemoveObject1);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // // remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    assertNotNull(thatRemoveObject2);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);
    assertNotNull(thisRemoveObject2);

    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject1, thisRoot.getListA().get(listSize - 1));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject1), thatRoot.getListA().get(listSize - 1));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject2), thisRoot.getListA().get(listSize - 1));
    assertEquals(thatAfterRemoveObject2, thatRoot.getListA().get(listSize - 1));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject1).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject1).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject2).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject2).cdoState());
  }

  public void testRemoveHeadMoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove first element (get it before deletion)
    BaseObject thisRemoveObject = thisList.get(0);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisList.remove(0);
    BaseObject thisAfterRemoveObject = thisList.get(0);

    // Move first element
    BaseObject thatMoveObject = thatList.get(0);
    thatList.move(thatList.size() - 1, 0);
    BaseObject thatAfterMoveObject = thatList.get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject, thisList.get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject), thatList.get(0));
    assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisList.get(0));
    assertEquals(thatAfterMoveObject, thatList.get(0));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatMoveObject).cdoState());
  }

  public void testRemoveHeadMoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject, thisRoot.getListA().get(1));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject), thatRoot.getListA().get(1));
    assertEquals(thisTransaction.getObject(thatMoveObject), thisRoot.getListA().get(0));
    assertEquals(thatMoveObject, thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisRoot.getListA().get(listSize - 1));
    assertEquals(thatAfterMoveObject, thatRoot.getListA().get(listSize - 1));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testRemoveTailMoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject, thisRoot.getListA().get(listSize - 2));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject), thatRoot.getListA().get(listSize - 2));
    assertEquals(thisTransaction.getObject(thatMoveObject), thisRoot.getListA().get(listSize - 1));
    assertEquals(thatMoveObject, thatRoot.getListA().get(listSize - 1));
    assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisRoot.getListA().get(0));
    assertEquals(thatAfterMoveObject, thatRoot.getListA().get(0));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testRemoveTailMoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject, thisRoot.getListA().get(listSize - 1));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject), thatRoot.getListA().get(listSize - 1));
    assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisRoot.getListA().get(listSize - 1));
    assertEquals(thatAfterMoveObject, thatRoot.getListA().get(listSize - 1));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatMoveObject).cdoState());
  }

  public void testRemoveHeadClearTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // clear.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterRemoveObject).cdoState());
  }

  public void testRemoveTailClearTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // clear.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterRemoveObject).cdoState());
  }

  public void testMoveHeadAddHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(0));
    assertEquals(thatObject, thatRoot.getListA().get(0));
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(1));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(1));
    assertEquals(thisMoveObject, thisRoot.getListA().get(size - 1));
    assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(size - 1));
  }

  public void testMoveHeadAddTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(size - 1));
    assertEquals(thatObject, thatRoot.getListA().get(size - 1));
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(0));
    assertEquals(thisMoveObject, thisRoot.getListA().get(size - 2));
    assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(size - 2));
  }

  public void testMoveTailAddHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(0));
    assertEquals(thatObject, thatRoot.getListA().get(0));
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(size - 1));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(size - 1));
    assertEquals(thisMoveObject, thisRoot.getListA().get(1));
    assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(1));
  }

  public void testMoveTailAddTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(size - 1));
    assertEquals(thatObject, thatRoot.getListA().get(size - 1));
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(size - 2));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(size - 2));
    assertEquals(thisMoveObject, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(0));
  }

  public void testMoveHeadRemoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);

    // remove object.
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject), thisRoot.getListA().get(0));
    assertEquals(thatAfterRemoveObject, thatRoot.getListA().get(0));
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisMoveObject).cdoState());
  }

  public void testMoveHeadRemoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);

    // remove object.
    BaseObject thatRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(0));
    assertEquals(thisMoveObject, thisRoot.getListA().get(size - 1));
    assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(size - 1));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject), thisRoot.getListA().get(size - 2));
    assertEquals(thatAfterRemoveObject, thatRoot.getListA().get(size - 2));
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testMoveTailRemoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // remove object.
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(size - 1));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(size - 1));
    assertEquals(thisMoveObject, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject), thisRoot.getListA().get(1));
    assertEquals(thatAfterRemoveObject, thatRoot.getListA().get(1));
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testMoveTailRemoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // remove object.
    BaseObject thatRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(size - 1));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(size - 1));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject), thisRoot.getListA().get(size - 1));
    assertEquals(thatAfterRemoveObject, thatRoot.getListA().get(size - 1));
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisMoveObject).cdoState());
  }

  public void testMoveHeadMoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject1 = thisRoot.getListA().get(0);
    BaseObject thatMoveObject1 = thatTransaction.getObject(thisMoveObject1);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject1 = thisRoot.getListA().get(0);
    BaseObject thatAfterMoveObject1 = thatTransaction.getObject(thisAfterMoveObject1);

    // Move object.
    BaseObject thatMoveObject2 = thatRoot.getListA().get(0);
    BaseObject thisMoveObject2 = thisTransaction.getObject(thatMoveObject2);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject2 = thatRoot.getListA().get(0);
    BaseObject thisAfterMoveObject2 = thisTransaction.getObject(thatAfterMoveObject2);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisMoveObject1, thisRoot.getListA().get(size - 1));
    assertEquals(thatMoveObject1, thatRoot.getListA().get(size - 1));
    assertEquals(thisMoveObject2, thisRoot.getListA().get(size - 1));
    assertEquals(thatMoveObject2, thatRoot.getListA().get(size - 1));
    assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(0));
    assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(0));
    assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(0));
    assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(0));
  }

  public void testMoveHeadMoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject1 = thisRoot.getListA().get(0);
    BaseObject thatMoveObject1 = thatTransaction.getObject(thisMoveObject1);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject1 = thisRoot.getListA().get(0);
    BaseObject thatAfterMoveObject1 = thatTransaction.getObject(thisAfterMoveObject1);

    // Move object.
    BaseObject thatMoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisMoveObject2 = thisTransaction.getObject(thatMoveObject2);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject2 = thisTransaction.getObject(thatAfterMoveObject2);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisMoveObject1, thisRoot.getListA().get(size - 1));
    assertEquals(thatMoveObject1, thatRoot.getListA().get(size - 1));
    assertEquals(thisMoveObject2, thisRoot.getListA().get(0));
    assertEquals(thatMoveObject2, thatRoot.getListA().get(0));
    assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(1));
    assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(1));
    assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(size - 2));
    assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(size - 2));
  }

  public void testMoveTailMoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatMoveObject1 = thatTransaction.getObject(thisMoveObject1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject1 = thatTransaction.getObject(thisAfterMoveObject1);

    // Move object.
    BaseObject thatMoveObject2 = thatRoot.getListA().get(0);
    BaseObject thisMoveObject2 = thisTransaction.getObject(thatMoveObject2);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject2 = thatRoot.getListA().get(0);
    BaseObject thisAfterMoveObject2 = thisTransaction.getObject(thatAfterMoveObject2);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisMoveObject1, thisRoot.getListA().get(0));
    assertEquals(thatMoveObject1, thatRoot.getListA().get(0));
    assertEquals(thisMoveObject2, thisRoot.getListA().get(size - 1));
    assertEquals(thatMoveObject2, thatRoot.getListA().get(size - 1));
    assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(size - 2));
    assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(size - 2));
    assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(1));
    assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(1));
  }

  public void testMoveTailMoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatMoveObject1 = thatTransaction.getObject(thisMoveObject1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject1 = thatTransaction.getObject(thisAfterMoveObject1);

    // Move object.
    BaseObject thatMoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisMoveObject2 = thisTransaction.getObject(thatMoveObject2);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject2 = thisTransaction.getObject(thatAfterMoveObject2);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisMoveObject1, thisRoot.getListA().get(0));
    assertEquals(thatMoveObject1, thatRoot.getListA().get(0));
    assertEquals(thisMoveObject2, thisRoot.getListA().get(0));
    assertEquals(thatMoveObject2, thatRoot.getListA().get(0));
    assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(size - 1));
    assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(size - 1));
    assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(size - 1));
    assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(size - 1));
  }

  public void testMoveHeadClearTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    BaseObject thatMoveObject = thatTransaction.getObject(thisMoveObject);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);
    BaseObject thatAfterMoveObject = thatTransaction.getObject(thisAfterMoveObject);

    // clear.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatAfterMoveObject).cdoState());
  }

  public void testMoveTailClearTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatMoveObject = thatTransaction.getObject(thisMoveObject);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatTransaction.getObject(thisAfterMoveObject);

    // clear.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatAfterMoveObject).cdoState());
  }

  public void testClearAddHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // clear.
    thisRoot.getListA().clear();

    // create objects.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(1, thisRoot.getListA().size());
    assertEquals(1, thatRoot.getListA().size());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(0));
    assertEquals(thatObject, thatRoot.getListA().get(0));
  }

  public void testClearAddTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // clear.
    thisRoot.getListA().clear();

    // create objects.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(1, thisRoot.getListA().size());
    assertEquals(1, thatRoot.getListA().size());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(0));
    assertEquals(thatObject, thatRoot.getListA().get(0));
  }

  public void testClearRemoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    // clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatAfterRemoveObject).cdoState());
  }

  public void testClearRemoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // create objects.
    BaseObject thatRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    // clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatAfterRemoveObject).cdoState());
  }

  public void testClearMoveHeadTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    BaseObject thisMoveObject = thisTransaction.getObject(thatMoveObject);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(0);
    BaseObject thisAfterMoveObject = thisTransaction.getObject(thatAfterMoveObject);

    // clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
  }

  public void testClearMoveTailTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisMoveObject = thisTransaction.getObject(thatMoveObject);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisTransaction.getObject(thatAfterMoveObject);

    // clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
  }

  public void testClearClearTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // clear.
    BaseObject thisObject = thisRoot.getListA().get(0);
    thisRoot.getListA().clear();

    BaseObject thatObject = thatRoot.getListA().get(0);
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatObject).cdoState());
  }

  public void testRemoveHeadMoveHeadRemoveMiddleTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    assertNotNull(thatRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    BaseObject thatRemoveAfterMoveObject = thatRoot.getListA().get(2);
    BaseObject thatAfterRemoveAfterMoveObject = thatRoot.getListA().get(3);
    msg(thatAfterRemoveAfterMoveObject);

    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(0);

    // Remove object.
    thatRoot.getListA().remove(thatRemoveAfterMoveObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveObject), thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisRoot.getListA().get(0));
    assertEquals(thatAfterMoveObject, thatRoot.getListA().get(0));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatMoveObject).cdoState());
  }

  public void testMoveHeadMoveHeadRemoveMiddleTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisMoveObject);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisAfterMoveObject);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    BaseObject thatRemoveAfterMoveObject = thatRoot.getListA().get(2);
    BaseObject thatAfterRemoveAfterMoveObject = thatRoot.getListA().get(3);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(0);

    // Remove object.
    BaseObject thisRemoveAfterMoveObject = thisTransaction.getObject(thatRemoveAfterMoveObject);
    thatRoot.getListA().remove(thatRemoveAfterMoveObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisMoveObject, thisRoot.getListA().get(listSize - 1));
    assertEquals(thatMoveObject, thatRoot.getListA().get(listSize - 1));
    assertEquals(thisAfterMoveObject, thisRoot.getListA().get(0));
    assertEquals(thatAfterMoveObject, thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterRemoveAfterMoveObject), thisRoot.getListA().get(1));
    assertEquals(thatAfterRemoveAfterMoveObject, thatRoot.getListA().get(1));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveAfterMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveAfterMoveObject).cdoState());
  }

  public void testMoveHeadRemoveHeadRemoveMiddleTest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object.
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    assertNotNull(thisRemoveObject);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemveObject = thisRoot.getListA().get(0);
    assertNotNull(thisAfterRemveObject);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    msg(thatMoveObject);

    BaseObject thatRemoveAfterMoveObject = thatRoot.getListA().get(2);
    BaseObject thatAfterRemoveAfterMoveObject = thatRoot.getListA().get(3);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(0);

    // Remove object.
    BaseObject thisRemoveAfterMoveObject = thisTransaction.getObject(thatRemoveAfterMoveObject);
    thatRoot.getListA().remove(thatRemoveAfterMoveObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemveObject, thisRoot.getListA().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemveObject), thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisRoot.getListA().get(0));
    assertEquals(thatAfterMoveObject, thatRoot.getListA().get(0));
    assertEquals(thisTransaction.getObject(thatAfterRemoveAfterMoveObject), thisRoot.getListA().get(1));
    assertEquals(thatAfterRemoveAfterMoveObject, thatRoot.getListA().get(1));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveAfterMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveAfterMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
  }

  public void testRemoveHeadAddChildHead() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListB();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListB();

    // Attach adapters
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove containment
    ContainmentObject thisToRemoveContainment = (ContainmentObject)thisList.get(0);
    thisList.remove(0);
    ContainmentObject thisAfterRemoveContainment = (ContainmentObject)thisList.get(0);

    ContainmentObject thatAddContainment = createContainmentObject("AddContainmentObject 0");
    thatAddContainment.setAttributeRequired("AddContainmentObject 0");
    thatAddContainment.setContainmentOptional(createBaseObject("AddBaseContainmentObject 0"));

    ContainmentObject thatParent = (ContainmentObject)thatList.get(0);
    EList<BaseObject> thatParentList = thatParent.getContainmentList();

    // Detach an object
    ContainmentObject thatReattachContainment = (ContainmentObject)thatParentList.remove(0);

    // Add a new object
    thatParentList.add(thatAddContainment);

    // Reattach containment
    thatParentList.add(thatReattachContainment);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveContainment, thisList.get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveContainment), thatList.get(0));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisToRemoveContainment).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatParent).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatReattachContainment).cdoState());
    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(thatAddContainment).cdoState());
  }

  public void testRemoveHeadSetChildHead() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove containment.
    ContainmentObject thisToRemoveContainment = (ContainmentObject)thisRoot.getListB().get(0);
    assertNotNull(thisToRemoveContainment);
    ContainmentObject thisAfterRemoveContainment = (ContainmentObject)thisRoot.getListB().get(1);
    thisRoot.getListB().remove(0);

    // add child to containment.
    BaseObject bcObject0 = createBaseObject("AddBaseContainmentObject 0");
    ContainmentObject thatAddContainment = createContainmentObject("AddContainmentObject 0");
    thatAddContainment.setAttributeRequired("AddContainmentObject 0");
    thatAddContainment.setContainmentOptional(bcObject0);

    ContainmentObject thatParent = (ContainmentObject)thatRoot.getListB().get(0);
    assertNotNull(thatParent);

    // set a new object.
    BaseObject thatUnsetContainment = thatParent.getContainmentOptional();
    msg(thatUnsetContainment);

    thatParent.setContainmentOptional(thatAddContainment);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListB());
    printList("That ", thatRoot.getListB());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveContainment, thisRoot.getListB().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveContainment), thatRoot.getListB().get(0));

    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisToRemoveContainment).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatParent).cdoState());
    // assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatUnsetContainment).cdoState()); <-- NPE
    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(thatAddContainment).cdoState());
  }

  public void testRemoveHeadRemoveChildHead() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove containment.
    ContainmentObject thisToRemoveContainment = (ContainmentObject)thisRoot.getListB().get(0);
    assertNotNull(thisToRemoveContainment);
    ContainmentObject thisAfterRemoveContainment = (ContainmentObject)thisRoot.getListB().get(1);
    thisRoot.getListB().remove(0);

    // get parent object.
    ContainmentObject thatParent = (ContainmentObject)thatRoot.getListB().get(0);
    assertNotNull(thatParent);
    // remove child from containment.
    ContainmentObject thatDetachContainment = (ContainmentObject)thatParent.getContainmentList().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListB());
    printList("That ", thatRoot.getListB());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveContainment, thisRoot.getListB().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveContainment), thatRoot.getListB().get(0));

    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisToRemoveContainment).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatParent).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatDetachContainment).cdoState());
  }

  public void testRemoveHeadRemoveChildChildHead() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove containment.
    ContainmentObject thisToRemoveContainment = (ContainmentObject)thisRoot.getListB().get(0);
    assertNotNull(thisToRemoveContainment);
    ContainmentObject thisAfterRemoveContainment = (ContainmentObject)thisRoot.getListB().get(1);
    thisRoot.getListB().remove(0);

    // get parent object.
    ContainmentObject thatParent = (ContainmentObject)thatRoot.getListB().get(0);
    assertNotNull(thatParent);
    // get child from containment.
    ContainmentObject thatChildContainment = (ContainmentObject)thatParent.getContainmentList().get(0);
    assertNotNull(thatChildContainment);
    // remove child from containment child.
    BaseObject thatDetachContainment = thatChildContainment.getContainmentList().remove(0);
    assertNotNull(thatDetachContainment);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListB());
    printList("That ", thatRoot.getListB());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveContainment, thisRoot.getListB().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveContainment), thatRoot.getListB().get(0));

    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisToRemoveContainment).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatParent).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatDetachContainment).cdoState());
  }

  public void testRemoveHeadMoveChildHead() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove containment.
    ContainmentObject thisToRemoveContainment = (ContainmentObject)thisRoot.getListB().get(0);
    assertNotNull(thisToRemoveContainment);
    ContainmentObject thisAfterRemoveContainment = (ContainmentObject)thisRoot.getListB().get(1);
    thisRoot.getListB().remove(0);

    // add child to containment.
    BaseObject bcObject0 = createBaseObject("AddBaseContainmentObject 0");
    ContainmentObject thatAddContainment = createContainmentObject("AddContainmentObject 0");
    thatAddContainment.setContainmentOptional(bcObject0);

    ContainmentObject thatParent = (ContainmentObject)thatRoot.getListB().get(0);
    assertNotNull(thatParent);
    // detach an object.
    ContainmentObject thatReattachContainment = (ContainmentObject)thatParent.getContainmentList().remove(0);
    // add a new object.
    thatParent.getContainmentList().add(thatAddContainment);
    // reattach containment.
    thatParent.getContainmentList().add(thatReattachContainment);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListB());
    printList("That ", thatRoot.getListB());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveContainment, thisRoot.getListB().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveContainment), thatRoot.getListB().get(0));

    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisToRemoveContainment).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatParent).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatReattachContainment).cdoState());
    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(thatAddContainment).cdoState());
  }

  public void testRemoveHeadReAttachHead() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    assertNotNull(thisRoot);
    Root thatRoot = thatTransaction.getObject(thisRoot);
    assertNotNull(thatRoot);

    // attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // remove containment.
    ContainmentObject thisToRemoveContainment = (ContainmentObject)thisRoot.getListB().get(0);
    assertNotNull(thisToRemoveContainment);
    ContainmentObject thisAfterRemoveContainment = (ContainmentObject)thisRoot.getListB().get(1);
    thisRoot.getListB().remove(0);

    // detach and re-attach containment.
    ContainmentObject thatAddContainment = (ContainmentObject)thatRoot.getListB().get(0);
    thatRoot.getListB().add(thatAddContainment);
    // TODO: re-add at other position (add some objects in between)?

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // print contents of lists
    printList("This ", thisRoot.getListB());
    printList("That ", thatRoot.getListB());

    // -- check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveContainment, thisRoot.getListB().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveContainment), thatRoot.getListB().get(0));

    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisToRemoveContainment).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatAddContainment).cdoState());
  }

  public void testContainerAddDifferentParent() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    initTestModel();
    addConflictResolver(thatTransaction);

    // create initial model.
    Root root = getTestModelRoot(thisTransaction);
    ContainmentObject group1 = createContainmentObject("Group 1");
    group1.setAttributeRequired("Group 1");

    ContainmentObject group2 = createContainmentObject("Group 2");
    group2.setAttributeRequired("Group 2");

    BaseObject element0 = createBaseObject("Element 0");
    element0.setAttributeRequired("Element 0");

    BaseObject element1 = createBaseObject("Element 1");
    element1.setAttributeRequired("Element 1");

    BaseObject element2 = createBaseObject("Element 2");
    element2.setAttributeRequired("Element 2");

    root.getListC().add(group1);
    root.getListC().add(group2);
    root.getListC().add(element0);
    root.getListC().add(element1);
    root.getListC().add(element2);

    commitAndSync(thisTransaction, thatTransaction);

    // get objects from that transaction.
    ContainmentObject thatGroup1 = thatTransaction.getObject(group1);
    ContainmentObject thatGroup2 = thatTransaction.getObject(group2);
    BaseObject thatElement0 = thatTransaction.getObject(element0);
    BaseObject thatElement1 = thatTransaction.getObject(element1);
    BaseObject thatElement2 = thatTransaction.getObject(element2);
    Root thatRoot = thatTransaction.getObject(root);

    // create group 1 (element 0 & 1).
    root.getListC().remove(element0);
    group1.getContainmentList().add(element0);
    root.getListC().remove(element1);
    group1.getContainmentList().add(element1);

    // create group 2 (element 1 & 2).
    thatRoot.getListC().remove(thatElement1);
    thatGroup2.getContainmentList().add(thatElement1);
    thatRoot.getListC().remove(thatElement2);
    thatGroup2.getContainmentList().add(thatElement2);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // -- Check:
    assertEquals(2, root.getListC().size()); // 2 groups.
    assertEquals(group1, element0.eContainer());
    assertEquals(group2, element1.eContainer());
    assertEquals(group2, element2.eContainer());
    assertEquals(1, group1.getContainmentList().size()); // 1 element.
    assertEquals(2, group2.getContainmentList().size()); // 2 elements.

    assertEquals(2, thatRoot.getListC().size()); // 2 groups.
    assertEquals(thatGroup1, thatElement0.eContainer());
    assertEquals(thatGroup2, thatElement1.eContainer());
    assertEquals(thatGroup2, thatElement2.eContainer());
    assertEquals(1, thatGroup1.getContainmentList().size()); // 1 element.
    assertEquals(2, thatGroup2.getContainmentList().size()); // 2 elements.
  }

  // --- container conflict resolver tests -----------------------------------
  // TODO: convert them into CDOTests

  // --- random conflict resolver tests --------------------------------------
  // TODO: convert them into CDOTests

  // ========== HELPERS ======================================================

  protected BaseObject createBaseObject(String attribute)
  {
    BaseObject baseObj = getModel6Factory().createBaseObject();
    baseObj.setAttributeRequired(attribute);
    return baseObj;
  }

  protected ReferenceObject createReferenceObject(String attribute)
  {
    ReferenceObject refObject = getModel6Factory().createReferenceObject();
    refObject.setAttributeRequired(attribute);
    return refObject;
  }

  protected ContainmentObject createContainmentObject(String attribute)
  {
    ContainmentObject contObject = getModel6Factory().createContainmentObject();
    contObject.setAttributeRequired(attribute);
    return contObject;
  }

  private Root getTestModelRoot(CDOTransaction transaction)
  {
    CDOResource resource = transaction.getResource(getResourcePath(TEST_RESOURCE_NAME));
    return (Root)resource.getContents().get(0);
  }

  private void printList(String identifier, List<BaseObject> list)
  {
    StringBuilder builder = new StringBuilder();
    for (BaseObject element : list)
    {
      if (builder.length() != 0)
      {
        builder.append(", ");
      }

      builder.append(element.getAttributeRequired() + " [" + CDOUtil.getCDOObject(element).cdoID() + "]");
    }

    IOUtil.OUT().println(identifier + "List: " + builder);
  }

  private void addConflictResolver(CDOTransaction transaction)
  {
    transaction.options().addConflictResolver(createConflictResolver());
  }

  protected CDOConflictResolver createConflictResolver()
  {
    return new CDOMergingConflictResolver(ResolutionPreference.NONE);
  }

  /**
   * @author Pascal Lehmann
   */
  private static class ListPrintingAdapter extends AdapterImpl
  {
    private String identifier;

    public ListPrintingAdapter(String identifier)
    {
      this.identifier = identifier;
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      try
      {
        System.err.println(identifier + msg);
      }
      catch (Exception e)
      {
        // ignore.
      }
    }
  }
}
