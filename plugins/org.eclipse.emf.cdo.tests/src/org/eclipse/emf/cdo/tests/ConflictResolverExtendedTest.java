/*
 * Copyright (c) 2011-2013, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.StringUtil;
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
@Skips(IModelConfig.CAPABILITY_LEGACY)
public class ConflictResolverExtendedTest extends AbstractCDOTest
{
  private static final String TEST_RESOURCE_NAME = "/test1";

  public void _testProvokeConflictOnServer() throws Exception
  {
    initTestModelSimple();

    // Should provoke an "attempt to modify historical revision" error.
    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
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

  public void _testProvokeConflictLocal() throws Exception
  {
    initTestModelSimple();

    // Should provoke an "this transaction has conflicts" error.
    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
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

  public void testChangeChange() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testRemoveChange() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
    BaseObject thatObject = thatTransaction.getObject(thisObject);

    // Remove object.
    getTestModelRoot(thisTransaction).getListA().remove(0);

    // Change object.
    thatObject.setAttributeOptional("that");

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testChangeRemove() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    BaseObject thisObject = getTestModelRoot(thisTransaction).getListA().get(0);
    BaseObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);
    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Change object.
    thisObject.setAttributeOptional("this");

    // Remove object.
    Root thatRoot = thatTransaction.getObject(getTestModelRoot(thisTransaction));
    thatRoot.getListA().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testRemoveAdd() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListB();
    ContainmentObject thisObject = (ContainmentObject)thisList.get(0);

    ContainmentObject thatObject = thatTransaction.getObject(thisObject);
    ContainmentObject thatChild = (ContainmentObject)thatObject.getContainmentList().get(0);

    // Remove object.
    thisList.remove(0);

    // Add object.
    BaseObject addObject = createBaseObject("AddObject");
    thatChild.getContainmentList().add(addObject);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testAddRemove() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    ContainmentObject thisObject = (ContainmentObject)getTestModelRoot(thisTransaction).getListB().get(0);
    ContainmentObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);
    int objects = getTestModelRoot(thisTransaction).getListB().size();

    // Add object.
    BaseObject addObject = createBaseObject("AddObject");

    ContainmentObject thisChild = (ContainmentObject)thisObject.getContainmentList().get(0);
    thisChild.getContainmentList().add(addObject);

    // Remove object.
    Root thatRoot = thatTransaction.getObject(getTestModelRoot(thisTransaction));
    thatRoot.getListB().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListB().size());
  }

  public void testRemoveRemove() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    ContainmentObject thisObject = (ContainmentObject)thisRoot.getListB().get(0);
    ContainmentObject thatObject = thatTransaction.getObject(thisObject);
    assertNotNull(thatObject);

    // Remove object.
    thisRoot.getListB().remove(0);

    // Remove object.
    thatRoot.getListB().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(false, thatTransaction.hasConflict());
  }

  public void test_RemoveRemove_MoveRemoveRemoveRemove() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    EList<BaseObject> thisList = thisRoot.getListA();
    EList<BaseObject> thatList = thatRoot.getListA();
    BaseObject thatLastObject = thatList.get(4);

    thisList.remove(1); // Removes the original index 1.
    thisList.remove(1); // Removes the original index 2.

    thatList.move(1, 4);
    thatList.remove(3); // Removes the original index 2.
    thatList.remove(3); // Removes the original index 3.
    thatList.remove(2); // Removes the original index 1.
    thatList.remove(0); // Removes the original index 4.

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(false, thatTransaction.hasConflict());

    assertEquals(1, thatList.size());
    assertEquals(thatLastObject, thatList.get(0));
  }

  public void testManyValuedAddAdd() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testManyValuedAddAdd_SameValue() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Add the same existing element from list B.
    thisList.add(0, thisRoot.getListB().get(0));
    thatList.add(0, thatRoot.getListB().get(0));
    thatList.remove(1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(objects + 1 - 1, thisList.size());
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
  }

  public void testManyValuedAddSet_SameValue() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Add the same existing element from list B.
    thisList.add(0, thisRoot.getListB().get(0));
    thatList.set(0, thatRoot.getListB().get(0));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(objects + 1 - 1, thisList.size());
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
  }

  public void testManyValuedSetAdd_SameValue() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisList = thisRoot.getListA();
    int objects = thisList.size();

    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatList = thatRoot.getListA();

    // Add the same existing element from list B.
    thisList.set(0, thisRoot.getListB().get(0));
    thatList.add(0, thatRoot.getListB().get(0));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(objects + 1 - 1, thisList.size());
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
  }

  public void testManyValuedAddChange() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Create new objects.
    BaseObject thisObject = createBaseObject("ThisObject");

    // Add elements.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().add(thisObject);

    // Move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> containmentList = thatRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects + 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedChangeAdd() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // Create new objects.
    BaseObject thatObject = createBaseObject("ThatObject");

    // Add elements.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects + 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedAddRemove() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Create new objects.
    BaseObject thisObject = createBaseObject("ThisObject");

    // Add elements.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().add(thisObject);

    // Remove element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    thatRoot.getListA().remove(1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedRemoveAdd() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testManyValuedChangeRemove() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // Remove element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    thatRoot.getListA().remove(1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedChangeRemoveTest2() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testManyValuedRemoveChange() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Remove element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().remove(1);

    // Move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
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
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testManyValuedChangeChange() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // Move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
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
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Move element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> containmentList = thisRoot.getListA();
    containmentList.move(2, containmentList.get(0));

    // Move element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    EList<BaseObject> thatContainmentList = thatRoot.getListA();
    thatContainmentList.move(2, thatContainmentList.get(0));

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedRemoveRemove() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Remove element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().remove(0);

    // Remove element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    thatRoot.getListA().remove(2);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 2, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedRemoveRemoveTest2() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Remove element.
    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().remove(0);

    // Remove same element.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    thatRoot.getListA().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects - 1, getTestModelRoot(thisTransaction).getListA().size());
  }

  public void testManyValuedAddAddRemoveRemove() throws Exception
  {
    initTestModelSimple();

    // test to produce exception of bug #306710
    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testManyValuedAddAddRemoveRemove2() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testManyValuedRemoveRemoveAddAdd() throws Exception
  {
    initTestModelSimple();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    int objects = getTestModelRoot(thisTransaction).getListA().size();

    // Add elements.
    BaseObject thisObject = createBaseObject("ThisObject");
    BaseObject thisObject2 = createBaseObject("ThisObject2");

    Root thisRoot = getTestModelRoot(thisTransaction);
    thisRoot.getListA().add(thisObject);
    thisRoot.getListA().add(thisObject2);

    // Remove elements.
    Root thatRoot = thatTransaction.getObject(thisRoot);
    thatRoot.getListA().remove(0);
    thatRoot.getListA().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(objects, getTestModelRoot(thisTransaction).getListA().size());

  }

  public void testAddHeadAddHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    EList<BaseObject> thisListA = thisRoot.getListA();
    thisListA.add(0, thisObject);

    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    EList<BaseObject> thatListA = thatRoot.getListA();
    thatListA.add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisListA);
    printList("That ", thatListA);

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisListA.get(0), thisObject);
      assertEquals(thisListA.get(1), thisTransaction.getObject(thatObject));
      assertEquals(thatListA.get(0), thatTransaction.getObject(thisObject));
      assertEquals(thatListA.get(1), thatObject);
    }
  }

  public void testAddHeadAddTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testAddTailAddTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(size - 2), thisObject);
    assertEquals(thisRoot.getListA().get(size - 1), thisTransaction.getObject(thatObject));
    assertEquals(thatRoot.getListA().get(size - 2), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatObject);
  }

  public void testAddTailAddHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisList.get(size - 1), thisObject);
      assertEquals(thisList.get(0), thisTransaction.getObject(thatObject));
      assertEquals(thatList.get(size - 1), thatTransaction.getObject(thisObject));
      assertEquals(thatList.get(0), thatObject);
    }
  }

  public void testAddHeadRemoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(0, thisObject);

    // Remove object (get it before deletion).
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOUtil.getCDOObject(thisRemoveObject).cdoState(), CDOState.INVALID);
    assertEquals(CDOUtil.getCDOObject(thatRemoveObject).cdoState(), CDOState.TRANSIENT);

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisRoot.getListA().get(0), thisObject);
      assertEquals(thisRoot.getListA().get(1), thisTransaction.getObject(thatAfterRemoveObject));
      assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thisObject));
      assertEquals(thatRoot.getListA().get(1), thatAfterRemoveObject);
    }
  }

  public void testAddHeadRemoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(0, thisObject);

    // Remove object (get it before deletion).
    int listSize = thatRoot.getListA().size();
    BaseObject thatRemoveObject = thatRoot.getListA().get(listSize - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);

    thatRoot.getListA().remove(listSize - 1);
    BaseObject thatBeforeRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOUtil.getCDOObject(thisRemoveObject).cdoState(), CDOState.INVALID);
    assertEquals(CDOUtil.getCDOObject(thatRemoveObject).cdoState(), CDOState.TRANSIENT);

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisRoot.getListA().get(0), thisObject);
      assertEquals(thisRoot.getListA().get(size - 1), thisTransaction.getObject(thatBeforeRemoveObject));
      assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thisObject));
      assertEquals(thatRoot.getListA().get(size - 1), thatBeforeRemoveObject);
    }
  }

  public void testAddTailRemoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // Remove object (get it before deletion).
    int listSize = thatRoot.getListA().size();
    BaseObject thatRemoveObject = thatRoot.getListA().get(listSize - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);

    thatRoot.getListA().remove(listSize - 1);
    BaseObject thatBeforeRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testAddTailRemoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // Remove object (get it before deletion).
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testAddHeadMoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(0, thisObject);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisRoot.getListA().get(0), thisObject);
      assertEquals(thisRoot.getListA().get(size - 1), thisTransaction.getObject(thatMoveObject));
      assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thisObject));
      assertEquals(thatRoot.getListA().get(size - 1), thatMoveObject);
    }
  }

  public void testAddHeadMoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testAddTailMoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create object.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisRoot.getListA().get(size - 1), thisObject);
      assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatMoveObject));
      assertEquals(thatRoot.getListA().get(size - 1), thatTransaction.getObject(thisObject));
      assertEquals(thatRoot.getListA().get(0), thatMoveObject);
    }
  }

  public void testAddTailMoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisRoot.getListA().add(thisObject);

    // Remove object.
    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisRoot.getListA().get(size - 1), thisObject);
    assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatAfterRemoveObject));
    assertEquals(thatRoot.getListA().get(size - 1), thatTransaction.getObject(thisObject));
    assertEquals(thatRoot.getListA().get(0), thatAfterRemoveObject);
  }

  public void testAddHeadClear() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Access lists.
    EList<BaseObject> thisListA = thisRoot.getListA();
    EList<BaseObject> thatListA = thatRoot.getListA();

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisListA.add(0, thisObject);

    // Clear list.
    thatListA.clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisListA);
    printList("That ", thatListA);

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(false, thatTransaction.hasConflict());
    assertEquals(1, thisListA.size()); // CLEAR means remove all *originally existing* elements
    assertEquals(1, thatListA.size()); // CLEAR means remove all *originally existing* elements
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(thisObject).cdoState());
  }

  public void testAddTailClear() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Access lists.
    EList<BaseObject> thisListA = thisRoot.getListA();
    EList<BaseObject> thatListA = thatRoot.getListA();

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thisObject = createBaseObject("ThisBaseObject 0");
    thisListA.add(thisObject);

    // Clear list.
    thatListA.clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisListA);
    printList("That ", thatListA);

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(false, thatTransaction.hasConflict());
    assertEquals(1, thisListA.size()); // CLEAR means remove all *originally existing* elements
    assertEquals(1, thatListA.size()); // CLEAR means remove all *originally existing* elements
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(thisObject).cdoState());
  }

  public void testRemoveHeadAddHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatObject));
      assertEquals(thisRoot.getListA().get(1), thisAfterRemoveObject);
      assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thatObject));
      assertEquals(thatRoot.getListA().get(1), thatTransaction.getObject(thisAfterRemoveObject));
    }
  }

  public void testRemoveHeadAddTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testRemoveTailAddHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisRoot.getListA().get(0), thisTransaction.getObject(thatObject));
      assertEquals(thisRoot.getListA().get(listSize - 1), thisAfterRemoveObject);
      assertEquals(thatRoot.getListA().get(0), thatTransaction.getObject(thatObject));
      assertEquals(thatRoot.getListA().get(listSize - 1), thatTransaction.getObject(thisAfterRemoveObject));
    }
  }

  public void testRemoveTailAddTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testRemoveHeadRemoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(0);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(0);

    // // Remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveObject1, thisRoot.getListA().get(0));
    assertEquals(thatAfterRemoveObject2, thatRoot.getListA().get(0));
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject1).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject1).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject2).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject2).cdoState());
  }

  public void testRemoveHeadRemoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(0);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(0);

    // // Remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);

    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testRemoveTailRemoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // // Remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);

    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testRemoveTailRemoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatRemoveObject1 = thatTransaction.getObject(thisRemoveObject1);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject1 = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // // Remove object (get it before deletion).
    BaseObject thatRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject2 = thisTransaction.getObject(thatRemoveObject2);

    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject2 = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testRemoveHeadMoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
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

  public void testRemoveHeadMoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(0);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisAfterRemoveObject, thisRoot.getListA().get(1));
      assertEquals(thatTransaction.getObject(thisAfterRemoveObject), thatRoot.getListA().get(1));
      assertEquals(thisTransaction.getObject(thatMoveObject), thisRoot.getListA().get(0));
      assertEquals(thatMoveObject, thatRoot.getListA().get(0));
      assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisRoot.getListA().get(listSize - 1));
      assertEquals(thatAfterMoveObject, thatRoot.getListA().get(listSize - 1));
    }
  }

  public void testRemoveTailMoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int listSize = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatRemoveObject).cdoState());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisAfterRemoveObject, thisRoot.getListA().get(listSize - 2));
      assertEquals(thatTransaction.getObject(thisAfterRemoveObject), thatRoot.getListA().get(listSize - 2));
      assertEquals(thisTransaction.getObject(thatMoveObject), thisRoot.getListA().get(listSize - 1));
      assertEquals(thatMoveObject, thatRoot.getListA().get(listSize - 1));
      assertEquals(thisTransaction.getObject(thatAfterMoveObject), thisRoot.getListA().get(0));
      assertEquals(thatAfterMoveObject, thatRoot.getListA().get(0));
    }
  }

  public void testRemoveTailMoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testRemoveHeadClear() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Access lists.
    EList<BaseObject> thisListA = thisRoot.getListA();
    EList<BaseObject> thatListA = thatRoot.getListA();

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisListA.get(0);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisListA.remove(0);
    BaseObject thisAfterRemoveObject = thisListA.get(0);

    // Clear.
    thatListA.clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisListA);
    printList("That ", thatListA);

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisListA.size());
    assertEquals(0, thatListA.size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterRemoveObject).cdoState());
  }

  public void testRemoveTailClear() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

    thisRoot.getListA().remove(thisRoot.getListA().size() - 1);
    BaseObject thisAfterRemoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Clear.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterRemoveObject).cdoState());
  }

  public void testMoveHeadAddHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    EList<BaseObject> thisList = thisRoot.getListA();
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisList.get(0);
    thisList.move(thisList.size() - 1, 0);
    BaseObject thisAfterMoveObject = thisList.get(0);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatList.add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices.
    int size = thisList.size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisTransaction.getObject(thatObject), thisList.get(4));
      assertEquals(thatObject, thatList.get(4));
      assertEquals(thisAfterMoveObject, thisList.get(0));
      assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatList.get(0));
      assertEquals(thisMoveObject, thisList.get(size - 1));
      assertEquals(thatTransaction.getObject(thisMoveObject), thatList.get(size - 1));
    }
  }

  public void testMoveHeadAddTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(size - 1));
      assertEquals(thatObject, thatRoot.getListA().get(size - 1));
      assertEquals(thisAfterMoveObject, thisRoot.getListA().get(0));
      assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(0));
      assertEquals(thisMoveObject, thisRoot.getListA().get(size - 2));
      assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(size - 2));
    }
  }

  public void testMoveTailAddHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    EList<BaseObject> thisList = thisRoot.getListA();
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisList.get(thisList.size() - 1);
    thisList.move(0, thisList.size() - 1);
    BaseObject thisAfterMoveObject = thisList.get(thisList.size() - 1);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatList.add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices.
    int size = thisList.size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisTransaction.getObject(thatObject), thisList.get(1));
      assertEquals(thatObject, thatList.get(1));
      assertEquals(thisAfterMoveObject, thisList.get(size - 1));
      assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatList.get(size - 1));
      assertEquals(thisMoveObject, thisList.get(0));
      assertEquals(thatTransaction.getObject(thisMoveObject), thatList.get(0));
    }
  }

  public void testMoveTailAddTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Create object.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(size - 1));
      assertEquals(thatObject, thatRoot.getListA().get(size - 1));
      assertEquals(thisAfterMoveObject, thisRoot.getListA().get(size - 2));
      assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(size - 2));
      assertEquals(thisMoveObject, thisRoot.getListA().get(0));
      assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(0));
    }
  }

  public void testMoveHeadRemoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    EList<BaseObject> thisList = thisRoot.getListA();
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisList.get(0);
    thisList.move(thisList.size() - 1, 0);
    BaseObject thisAfterMoveObject = thisList.get(0);

    // Remove object.
    BaseObject thatRemoveObject = thatList.get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatList.remove(0);
    BaseObject thatAfterRemoveObject = thatList.get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterMoveObject, thisList.get(0));
    assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatList.get(0));
    assertEquals(thisTransaction.getObject(thatAfterRemoveObject), thisList.get(0));
    assertEquals(thatAfterRemoveObject, thatList.get(0));
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisMoveObject).cdoState());
  }

  public void testMoveHeadRemoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);

    // Remove object.
    BaseObject thatRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisAfterMoveObject, thisRoot.getListA().get(0));
      assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(0));
      assertEquals(thisMoveObject, thisRoot.getListA().get(size - 1));
      assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(size - 1));
      assertEquals(thisTransaction.getObject(thatAfterRemoveObject), thisRoot.getListA().get(size - 2));
      assertEquals(thatAfterRemoveObject, thatRoot.getListA().get(size - 2));
    }
  }

  public void testMoveTailRemoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Remove object.
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisAfterMoveObject, thisRoot.getListA().get(size - 1));
      assertEquals(thatTransaction.getObject(thisAfterMoveObject), thatRoot.getListA().get(size - 1));
      assertEquals(thisMoveObject, thisRoot.getListA().get(0));
      assertEquals(thatTransaction.getObject(thisMoveObject), thatRoot.getListA().get(0));
      assertEquals(thisTransaction.getObject(thatAfterRemoveObject), thisRoot.getListA().get(1));
      assertEquals(thatAfterRemoveObject, thatRoot.getListA().get(1));
    }
  }

  public void testMoveTailRemoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);

    // Remove object.
    BaseObject thatRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testMoveHeadMoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
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

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisMoveObject1, thisRoot.getListA().get(size - 1));
      assertEquals(thatMoveObject1, thatRoot.getListA().get(size - 1));
      assertEquals(thisMoveObject2, thisRoot.getListA().get(size - 1));
      assertEquals(thatMoveObject2, thatRoot.getListA().get(size - 1));
      assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(0));
      assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(0));
      assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(0));
      assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(0));
    }
  }

  public void testMoveHeadMoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
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

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisMoveObject1, thisRoot.getListA().get(size - 1));
      assertEquals(thatMoveObject1, thatRoot.getListA().get(size - 1));
      assertEquals(thisMoveObject2, thisRoot.getListA().get(0));
      assertEquals(thatMoveObject2, thatRoot.getListA().get(0));
      assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(1));
      assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(1));
      assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(size - 2));
      assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(size - 2));
    }
  }

  public void testMoveTailMoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
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

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisMoveObject1, thisRoot.getListA().get(0));
      assertEquals(thatMoveObject1, thatRoot.getListA().get(0));
      assertEquals(thisMoveObject2, thisRoot.getListA().get(size - 1));
      assertEquals(thatMoveObject2, thatRoot.getListA().get(size - 1));
      assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(size - 2));
      assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(size - 2));
      assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(1));
      assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(1));
    }
  }

  public void testMoveTailMoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
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

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    int size = thisRoot.getListA().size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisMoveObject1, thisRoot.getListA().get(0));
      assertEquals(thatMoveObject1, thatRoot.getListA().get(0));
      assertEquals(thisMoveObject2, thisRoot.getListA().get(0));
      assertEquals(thatMoveObject2, thatRoot.getListA().get(0));
      assertEquals(thisAfterMoveObject1, thisRoot.getListA().get(size - 1));
      assertEquals(thatAfterMoveObject1, thatRoot.getListA().get(size - 1));
      assertEquals(thisAfterMoveObject2, thisRoot.getListA().get(size - 1));
      assertEquals(thatAfterMoveObject2, thatRoot.getListA().get(size - 1));
    }
  }

  public void testMoveHeadClear() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(0);
    BaseObject thatMoveObject = thatTransaction.getObject(thisMoveObject);
    thisRoot.getListA().move(thisRoot.getListA().size() - 1, 0);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(0);
    BaseObject thatAfterMoveObject = thatTransaction.getObject(thisAfterMoveObject);

    // Clear.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatAfterMoveObject).cdoState());
  }

  public void testMoveTailClear() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatMoveObject = thatTransaction.getObject(thisMoveObject);
    thisRoot.getListA().move(0, thisRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisRoot.getListA().get(thisRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatTransaction.getObject(thisAfterMoveObject);

    // Clear.
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatAfterMoveObject).cdoState());
  }

  public void testClearAddHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Clear.
    thisRoot.getListA().clear();

    // Create objects.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(0, thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(1, thisRoot.getListA().size());
    assertEquals(1, thatRoot.getListA().size());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(0));
    assertEquals(thatObject, thatRoot.getListA().get(0));
  }

  public void testClearAddTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Clear.
    thisRoot.getListA().clear();

    // Create objects.
    BaseObject thatObject = createBaseObject("ThatBaseObject 0");
    thatRoot.getListA().add(thatObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(1, thisRoot.getListA().size());
    assertEquals(1, thatRoot.getListA().size());
    assertEquals(thisTransaction.getObject(thatObject), thisRoot.getListA().get(0));
    assertEquals(thatObject, thatRoot.getListA().get(0));
  }

  public void testClearRemoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thatRemoveObject = thatRoot.getListA().get(0);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(0);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(0);

    // Clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatAfterRemoveObject).cdoState());
  }

  public void testClearRemoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Create objects.
    BaseObject thatRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisRemoveObject = thisTransaction.getObject(thatRemoveObject);
    thatRoot.getListA().remove(thatRoot.getListA().size() - 1);
    BaseObject thatAfterRemoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);

    // Clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisRemoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatAfterRemoveObject).cdoState());
  }

  public void testClearMoveHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(0);
    BaseObject thisMoveObject = thisTransaction.getObject(thatMoveObject);
    thatRoot.getListA().move(thatRoot.getListA().size() - 1, 0);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(0);
    BaseObject thisAfterMoveObject = thisTransaction.getObject(thatAfterMoveObject);

    // Clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
  }

  public void testClearMoveTail() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thatMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisMoveObject = thisTransaction.getObject(thatMoveObject);
    thatRoot.getListA().move(0, thatRoot.getListA().size() - 1);
    BaseObject thatAfterMoveObject = thatRoot.getListA().get(thatRoot.getListA().size() - 1);
    BaseObject thisAfterMoveObject = thisTransaction.getObject(thatAfterMoveObject);

    // Clear.
    thisRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatMoveObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisAfterMoveObject).cdoState());
  }

  public void testClearClear() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Clear.
    BaseObject thisObject = thisRoot.getListA().get(0);
    thisRoot.getListA().clear();

    BaseObject thatObject = thatRoot.getListA().get(0);
    thatRoot.getListA().clear();

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(0, thisRoot.getListA().size());
    assertEquals(0, thatRoot.getListA().size());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisObject).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatObject).cdoState());
  }

  public void testRemoveHeadMoveHeadRemoveMiddle() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object (get it before deletion).
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);

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

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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

  public void testMoveHeadMoveHeadRemoveMiddle() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    EList<BaseObject> thisList = thisRoot.getListA();
    EList<BaseObject> thatList = thatRoot.getListA();

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Move object.
    BaseObject thisMoveObject = thisList.get(0);
    thisList.move(thisList.size() - 1, 0);
    BaseObject thisAfterMoveObject = thisList.get(0);

    // Move object.
    BaseObject thatMoveObject = thatList.get(0);
    BaseObject thatRemoveAfterMoveObject = thatList.get(2);
    BaseObject thatAfterRemoveAfterMoveObject = thatList.get(3);
    thatList.move(thatList.size() - 1, 0);
    BaseObject thatAfterMoveObject = thatList.get(0);

    // Remove object.
    BaseObject thisRemoveAfterMoveObject = thisTransaction.getObject(thatRemoveAfterMoveObject);
    thatList.remove(thatRemoveAfterMoveObject);

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisList);
    printList("That ", thatList);

    // Check indices.
    int listSize = thisList.size();
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thatRemoveAfterMoveObject).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thisRemoveAfterMoveObject).cdoState());

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      assertEquals(thisMoveObject, thisList.get(listSize - 1));
      assertEquals(thatMoveObject, thatList.get(listSize - 1));
      assertEquals(thisAfterMoveObject, thisList.get(0));
      assertEquals(thatAfterMoveObject, thatList.get(0));
      assertEquals(thisTransaction.getObject(thatAfterRemoveAfterMoveObject), thisList.get(1));
      assertEquals(thatAfterRemoveAfterMoveObject, thatList.get(1));
    }
  }

  public void testMoveHeadRemoveHeadRemoveMiddle() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove object.
    BaseObject thisRemoveObject = thisRoot.getListA().get(0);
    BaseObject thatRemoveObject = thatTransaction.getObject(thisRemoveObject);
    thisRoot.getListA().remove(0);
    BaseObject thisAfterRemveObject = thisRoot.getListA().get(0);

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

    // Print contents of lists
    printList("This ", thisRoot.getListA());
    printList("That ", thatRoot.getListA());

    // Check indices.
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
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Access lists.
    EList<BaseObject> thisList = thisRoot.getListB();
    EList<BaseObject> thatList = thatRoot.getListB();

    // Attach adapters
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove containment
    thisList.remove(0);

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
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testRemoveHeadSetChildHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove containment.
    thisRoot.getListB().remove(0);

    // Add child to containment.
    BaseObject bcObject0 = createBaseObject("AddBaseContainmentObject 0");
    ContainmentObject thatAddContainment = createContainmentObject("AddContainmentObject 0");
    thatAddContainment.setAttributeRequired("AddContainmentObject 0");
    thatAddContainment.setContainmentOptional(bcObject0);

    ContainmentObject thatParent = (ContainmentObject)thatRoot.getListB().get(0);

    // Set a new object.
    BaseObject thatUnsetContainment = thatParent.getContainmentOptional();
    msg(thatUnsetContainment);

    thatParent.setContainmentOptional(thatAddContainment);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testRemoveHeadRemoveChildHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove containment.
    thisRoot.getListB().remove(0);

    // Remove child from containment.
    ((ContainmentObject)thatRoot.getListB().get(0)).getContainmentList().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testRemoveHeadRemoveChildChildHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove containment.
    thisRoot.getListB().remove(0);

    // Get parent object.
    ContainmentObject thatParent = (ContainmentObject)thatRoot.getListB().get(0);

    // Get child from containment.
    ContainmentObject thatChildContainment = (ContainmentObject)thatParent.getContainmentList().get(0);

    // Remove child from containment child.
    thatChildContainment.getContainmentList().remove(0);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testRemoveHeadMoveChildHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove containment.
    thisRoot.getListB().remove(0);

    // Add child to containment.
    BaseObject bcObject0 = createBaseObject("AddBaseContainmentObject 0");
    ContainmentObject thatAddContainment = createContainmentObject("AddContainmentObject 0");
    thatAddContainment.setContainmentOptional(bcObject0);

    ContainmentObject thatParent = (ContainmentObject)thatRoot.getListB().get(0);

    // Detach an object.
    ContainmentObject thatReattachContainment = (ContainmentObject)thatParent.getContainmentList().remove(0);

    // Add a new object.
    thatParent.getContainmentList().add(thatAddContainment);

    // Reattach containment.
    thatParent.getContainmentList().add(thatReattachContainment);

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(true, thatTransaction.hasConflict());
  }

  public void testRemoveHeadReAttachHead() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Access objects.
    Root thisRoot = getTestModelRoot(thisTransaction);
    Root thatRoot = thatTransaction.getObject(thisRoot);

    // Attach adapters.
    thisRoot.eAdapters().add(new ListPrintingAdapter("This root: "));
    thatRoot.eAdapters().add(new ListPrintingAdapter("That root: "));

    // Remove containment.
    ContainmentObject thisToRemoveContainment = (ContainmentObject)thisRoot.getListB().get(0);
    ContainmentObject thisAfterRemoveContainment = (ContainmentObject)thisRoot.getListB().get(1);
    thisRoot.getListB().remove(0);

    // Detach and re-attach containment.
    ContainmentObject thatAddContainment = (ContainmentObject)thatRoot.getListB().get(0);
    thatRoot.getListB().add(thatAddContainment);
    // TODO: re-add at other position (add some objects in between)?

    commitAndSync(thisTransaction, thatTransaction);
    commitAndSync(thatTransaction, thisTransaction);

    // Print contents of lists
    printList("This ", thisRoot.getListB());
    printList("That ", thatRoot.getListB());

    // Check indices.
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thatTransaction.isDirty());
    assertEquals(thisAfterRemoveContainment, thisRoot.getListB().get(0));
    assertEquals(thatTransaction.getObject(thisAfterRemoveContainment), thatRoot.getListB().get(0));

    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(thisToRemoveContainment).cdoState());
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(thatAddContainment).cdoState());
  }

  public void testContainerAddDifferentParent() throws Exception
  {
    initTestModel();

    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction thisTransaction = session.openTransaction();
    CDOTransaction thatTransaction = session.openTransaction();
    addConflictResolver(thatTransaction);

    // Create initial model.
    Root thisRoot = getTestModelRoot(thisTransaction);
    EList<BaseObject> thisListC = thisRoot.getListC();

    ContainmentObject thisGroup1 = createContainmentObject("Group 1");
    thisGroup1.setAttributeRequired("Group 1");

    ContainmentObject thisGroup2 = createContainmentObject("Group 2");
    thisGroup2.setAttributeRequired("Group 2");

    BaseObject thisElement0 = createBaseObject("Element 0");
    thisElement0.setAttributeRequired("Element 0");

    BaseObject thisElement1 = createBaseObject("Element 1");
    thisElement1.setAttributeRequired("Element 1");

    BaseObject thisElement2 = createBaseObject("Element 2");
    thisElement2.setAttributeRequired("Element 2");

    thisListC.add(thisGroup1);
    thisListC.add(thisGroup2);
    thisListC.add(thisElement0);
    thisListC.add(thisElement1);
    thisListC.add(thisElement2);

    commitAndSync(thisTransaction, thatTransaction);

    // Get objects from that transaction.
    ContainmentObject thatGroup2 = thatTransaction.getObject(thisGroup2);
    BaseObject thatElement1 = thatTransaction.getObject(thisElement1);
    BaseObject thatElement2 = thatTransaction.getObject(thisElement2);

    // THIS: Create group 1 (element 0 & 1).
    thisGroup1.getContainmentList().add(thisElement0); // Move element0 from listC to group1
    thisGroup1.getContainmentList().add(thisElement1); // Move element1 from listC to group1

    // THAT: Create group 2 (element 1 & 2).
    thatGroup2.getContainmentList().add(thatElement1); // Move element1 from listC to group2 (CONFLICT)
    thatGroup2.getContainmentList().add(thatElement2); // Move element2 from listC to group2

    commitAndSync(thisTransaction, thatTransaction);
    assertEquals(false, thisTransaction.isDirty());
    assertEquals(false, thisTransaction.hasConflict());
    assertEquals(true, thatTransaction.isDirty());
    assertEquals(true, thatTransaction.hasConflict());
  }

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

  private CDOSession openSessionWithAdditionsMode()
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(true);
    session.options().setPassiveUpdateMode(CDOCommonSession.Options.PassiveUpdateMode.ADDITIONS);
    return session;
  }

  private void initTestModelSimple() throws CommitException
  {
    CDOSession session = openSessionWithAdditionsMode();
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
    session.close();
  }

  private void initTestModel() throws CommitException
  {
    CDOSession session = openSessionWithAdditionsMode();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath(TEST_RESOURCE_NAME));

    Root root = getModel6Factory().createRoot();
    resource.getContents().add(root);

    // Setup base objects.
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

    // Containment objects.
    ContainmentObject cObject0 = createContainmentObject("ContainmentObject 0");
    ContainmentObject cObject00 = createContainmentObject("ContainmentObject 00");
    BaseObject bcObject01 = createBaseObject("BaseContainmentObject 01");

    cObject00.getContainmentList().add(bcObject01);
    cObject0.getContainmentList().add(cObject00);

    ContainmentObject cObject1 = createContainmentObject("ContainmentObject 1");

    root.getListB().add(cObject0);
    root.getListB().add(cObject1);

    // Commit the model.
    transaction.commit();
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
      StringUtil.appendSeparator(builder, ", ");
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
