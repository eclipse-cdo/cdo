/*
 * Copyright (c) 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alex Lagarde - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchVersionImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Bug 400311 - CDOObject modifies Store even for Touch notifications.
 *
 * @author Alex Lagarde
 */
public class Bugzilla_400311b_Test extends AbstractCDOTest
{
  private CDOSession session;

  private CDOTransaction transaction;

  private OrderDetail orderDetail1;

  private OrderDetail orderDetail2;

  private Product1 product1;

  private Product1 product2;

  private ContainmentObject containmentObject;

  private BaseObject childObject;

  private int orderDetail1InitialVersion;

  private int product1InitialVersion;

  private int containmentObjectInitialVersion;

  private int childObjectInitialVersion;

  private CDOResource resource;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    session = openSession();
    transaction = session.openTransaction();

    orderDetail1 = getModel1Factory().createOrderDetail();
    orderDetail2 = getModel1Factory().createOrderDetail();
    product1 = getModel1Factory().createProduct1();
    product2 = getModel1Factory().createProduct1();
    orderDetail1.setProduct(product1);
    product1.getOrderDetails().add(orderDetail1);
    product1.getOrderDetails().add(orderDetail2);
    product1.setVat(VAT.VAT0);
    product1.getOtherVATs().add(VAT.VAT7);
    product1.getOtherVATs().add(VAT.VAT15);
    containmentObject = getModel6Factory().createContainmentObject();
    containmentObject.getAttributeList().add("attr1");
    containmentObject.getAttributeList().add("attr2");
    childObject = getModel6Factory().createBaseObject();
    containmentObject.getContainmentList().add(childObject);

    resource = transaction.createResource(getResourcePath("/test"));
    resource.getContents().add(orderDetail1);
    resource.getContents().add(orderDetail2);
    resource.getContents().add(product1);
    resource.getContents().add(product2);
    resource.getContents().add(containmentObject);

    transaction.commit();

    orderDetail1InitialVersion = CDOUtil.getCDOObject(orderDetail1).cdoRevision().getVersion();
    product1InitialVersion = CDOUtil.getCDOObject(product1).cdoRevision().getVersion();
    containmentObjectInitialVersion = CDOUtil.getCDOObject(containmentObject).cdoRevision().getVersion();
    childObjectInitialVersion = CDOUtil.getCDOObject(childObject).cdoRevision().getVersion();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    // Check that the server never contains 2 adjacent identical revisions
    for (EObject element : resource.getContents())
    {
      CDOID elementID = CDOUtil.getCDOObject(element).cdoID();
      assertServerDoesNotContainIdenticalAdjacentRevision(elementID, transaction);
    }

    transaction.close();
    session.close();

    super.doTearDown();
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnSingleValuedReference() throws Exception
  {
    // Step 1: Make touch modification
    orderDetail1.setProduct(product1);
    assertVersion(orderDetail1InitialVersion, orderDetail1);

    // Step 2: Commit
    transaction.commit();
    assertVersion(orderDetail1InitialVersion, orderDetail1);

    // Step 3: make non touch modification
    orderDetail1.setProduct(null);
    assertDirty(orderDetail1, transaction);
    transaction.commit();
    assertVersionIncreased(orderDetail1InitialVersion, orderDetail1);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnMultiValuedReference() throws Exception
  {
    // Step 1: Make touch modification
    product1.getOrderDetails().add(orderDetail1);
    assertVersion(product1InitialVersion, product1);

    // Step 2: Commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);

    // Step 3: make non touch modification
    product1.getOrderDetails().remove(orderDetail1);
    assertDirty(product1, transaction);
    transaction.commit();
    assertVersionIncreased(product1InitialVersion, product1);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnMultiValuedReferenceMove() throws Exception
  {
    // Step 1: Make touch modification
    product1.getOrderDetails().move(0, orderDetail1);
    assertVersion(product1InitialVersion, product1);

    // Step 2: Commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);

    // Step 3: make non touch modification
    product1.getOrderDetails().remove(orderDetail1);
    assertDirty(product1, transaction);
    transaction.commit();
    assertVersionIncreased(product1InitialVersion, product1);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnSingleValuedAttribute() throws Exception
  {
    // Step 1: Make touch modification
    product1.setName(product1.getName());
    assertVersion(product1InitialVersion, product1);

    // Step 2: Commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);

    // Step 3: make non touch modification
    product1.setName(product1.getName() + "-MODIFIED");
    assertDirty(product1, transaction);
    transaction.commit();
    assertVersionIncreased(product1InitialVersion, product1);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnMultiValuedAttribute() throws Exception
  {
    // Step 1: Make touch modification
    containmentObject.getAttributeList().add("attr2");
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 2: Commit
    transaction.commit();
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 3: make non touch modification
    containmentObject.getAttributeList().remove("attr2");
    assertDirty(containmentObject, transaction);
    transaction.commit();
    assertVersionIncreased(containmentObjectInitialVersion, containmentObject);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnMultiValuedAttributeMove() throws Exception
  {
    // Step 1: Make touch modification
    containmentObject.getAttributeList().move(1, "attr2");
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 2: Commit
    transaction.commit();
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 3: make non touch modification
    containmentObject.getAttributeList().move(0, "attr2");
    assertDirty(containmentObject, transaction);
    transaction.commit();
    assertVersionIncreased(containmentObjectInitialVersion, containmentObject);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnSingleValuedEnum() throws Exception
  {
    // Step 1: Make touch modification
    product1.setVat(VAT.VAT0);
    assertVersion(product1InitialVersion, product1);

    // Step 2: Commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);

    // Step 3: make non touch modification
    product1.setVat(VAT.VAT7);
    assertDirty(product1, transaction);
    transaction.commit();
    assertVersionIncreased(product1InitialVersion, product1);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnMultiValuedEnum() throws Exception
  {
    // Step 1: Make touch modification
    product1.getOtherVATs().add(VAT.VAT7);
    assertVersion(product1InitialVersion, product1);

    // Step 2: Commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);

    // Step 3: make non touch modification
    product1.getOtherVATs().remove(VAT.VAT7);
    assertDirty(product1, transaction);
    transaction.commit();
    assertVersionIncreased(product1InitialVersion, product1);
  }

  public void testTouchModificationDoNotMakeObjectsDirtyOnMultiValuedEnumMove() throws Exception
  {
    // Step 1: Make touch modification
    product1.getOtherVATs().move(0, VAT.VAT7);
    assertVersion(product1InitialVersion, product1);

    // Step 2: Commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);

    // Step 3: make non touch modification
    product1.getOtherVATs().move(1, VAT.VAT7);
    assertDirty(product1, transaction);
    transaction.commit();
    assertVersionIncreased(product1InitialVersion, product1);
  }

  public void testUndoRevertsToCleanStateOnSingleValuedReference() throws Exception
  {
    // Step 1: make modification
    orderDetail1.setProduct(product2);
    assertDirty(orderDetail1, transaction);

    // Step 2: get back to initial clean state
    product1.getOrderDetails().add(0, orderDetail1);
    assertVersion(orderDetail1InitialVersion, orderDetail1);

    // Step 3: commit
    transaction.commit();
    assertVersion(orderDetail1InitialVersion, orderDetail1);
  }

  public void testUndoRevertsToCleanStateOnMultiValuedReference() throws Exception
  {
    // Step 1: make modification
    product1.getOrderDetails().remove(orderDetail1);
    assertDirty(product1, transaction);

    // Step 2: get back to initial clean state
    product1.getOrderDetails().add(0, orderDetail1);
    assertVersion(product1InitialVersion, product1);

    // Step 3: commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);
  }

  public void testUndoRevertsToCleanStateOnMultiValuedReferenceMove() throws Exception
  {
    // Step 1: make modification
    int oldIndex = product1.getOrderDetails().indexOf(orderDetail1);
    product1.getOrderDetails().move(1, orderDetail1);
    assertDirty(product1, transaction);

    // Step 2: get back to initial clean state
    product1.getOrderDetails().move(oldIndex, orderDetail1);
    assertVersion(product1InitialVersion, product1);

    // Step 3: commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);
  }

  public void testUndoRevertsToCleanStateOnSingleValuedAttribute() throws Exception
  {
    // Step 1: make modification
    String initialValue = product1.getName();
    product1.setName("New name");
    assertDirty(product1, transaction);

    // Step 2: get back to initial clean state
    product1.setName(initialValue);
    assertVersion(product1InitialVersion, product1);

    // Step 3: commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);
  }

  public void testUndoRevertsToCleanStateOnMultiValuedAttribute() throws Exception
  {
    // Step 1: make modification
    containmentObject.getAttributeList().add("attr3");
    assertDirty(containmentObject, transaction);

    // Step 2: get back to initial clean state
    containmentObject.getAttributeList().remove("attr3");
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 3: commit
    transaction.commit();
    assertVersion(containmentObjectInitialVersion, containmentObject);
  }

  public void testUndoRevertsToCleanStateOnMultiValuedAttributeMove() throws Exception
  {
    // Step 1: make modification
    int oldIndex = containmentObject.getAttributeList().indexOf("attr2");
    containmentObject.getAttributeList().move(0, "attr2");
    assertDirty(containmentObject, transaction);

    // Step 2: get back to initial clean state
    containmentObject.getAttributeList().move(oldIndex, "attr2");
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 3: commit
    transaction.commit();
    assertVersion(containmentObjectInitialVersion, containmentObject);
  }

  public void testUndoRevertsToCleanStateOnSingleValuedEnum() throws Exception
  {
    // Step 1: make modification
    product1.setVat(VAT.VAT7);
    assertDirty(product1, transaction);

    // Step 2: get back to initial clean state
    product1.setVat(VAT.VAT0);
    assertVersion(product1InitialVersion, product1);

    // Step 3: commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);
  }

  public void testUndoRevertsToCleanStateOnMultiValuedEnum() throws Exception
  {
    // Step 1: make modification
    product1.getOtherVATs().add(VAT.VAT0);
    assertDirty(product1, transaction);

    // Step 2: get back to initial clean state
    product1.getOtherVATs().remove(VAT.VAT0);
    assertVersion(product1InitialVersion, product1);

    // Step 3: commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);
  }

  public void testUndoRevertsToCleanStateOnMultiValuedEnumMove() throws Exception
  {
    // Step 1: make modification
    int oldIndex = product1.getOtherVATs().indexOf(VAT.VAT7);
    product1.getOtherVATs().move(1, VAT.VAT7);
    assertDirty(product1, transaction);

    // Step 2: get back to initial clean state
    product1.getOtherVATs().move(oldIndex, VAT.VAT7);
    assertVersion(product1InitialVersion, product1);

    // Step 3: commit
    transaction.commit();
    assertVersion(product1InitialVersion, product1);
  }

  public void testUndoRevertsToCleanStateOnObjectCreation() throws Exception
  {
    EList<BaseObject> containmentList = containmentObject.getContainmentList();

    // Step 1: create a new object
    BaseObject newObject = getModel6Factory().createBaseObject();
    containmentList.add(newObject);
    assertDirty(containmentObject, transaction);

    // Step 2: delete the created object
    containmentList.remove(newObject);
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 3: commit
    transaction.commit();
    assertVersion(containmentObjectInitialVersion, containmentObject);
  }

  /**
   * CDOLegacyAdapter creates (bogus?) CONTAINER deltas that aren't detected.
   */
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testUndoRevertsToCleanStateOnObjectDeletion() throws Exception
  {
    CDOID elementToDeleteID = CDOUtil.getCDOObject(childObject).cdoID();
    EList<BaseObject> containmentList = containmentObject.getContainmentList();

    // For better debugging:
    // Map<InternalCDOObject, InternalCDORevision> cleanRevisions = ((InternalCDOTransaction)transaction)
    // .getCleanRevisions();
    // CDOSavepoint savepoint = transaction.getLastSavepoint();
    // Collection<CDORevisionDelta> revisionDeltas = savepoint.getRevisionDeltas2().values();

    // Step 1: delete object
    containmentList.remove(childObject);
    assertDirty(containmentObject, transaction);
    assertEquals("Element should be detached", true, transaction.getDetachedObjects().containsKey(elementToDeleteID));

    // Step 2: undo deletion
    containmentList.add(childObject);
    assertVersion(containmentObjectInitialVersion, containmentObject);
    assertVersion(childObjectInitialVersion, childObject);
    assertEquals("Element should not be detached", false, transaction.getDetachedObjects().containsKey(elementToDeleteID));

    // Step 3: commit
    transaction.commit();
    assertVersion(containmentObjectInitialVersion, containmentObject);
    assertVersion(childObjectInitialVersion, childObject);
  }

  /**
   * CDOLegacyAdapter creates (bogus?) CONTAINER deltas that aren't detected.
   */
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testUndoRevertsToCleanStateOnComplexModifications() throws Exception
  {
    String oldContainmentObjectAttributeValue = containmentObject.getAttributeOptional();
    CDOID elementToDeleteID = CDOUtil.getCDOObject(childObject).cdoID();
    EList<BaseObject> containmentList = containmentObject.getContainmentList();

    // Step 1: make several modifications (modifying several features on the same object and several objects)
    containmentList.remove(childObject);
    BaseObject newObject = getModel6Factory().createBaseObject();
    containmentList.add(newObject);
    containmentObject.setAttributeOptional("MODIFIED");

    String oldProduct1Name = product1.getName();
    product1.setName("New name");
    int oldVATIndexForProduct1 = product1.getOtherVATs().indexOf(VAT.VAT7);
    product1.getOtherVATs().move(1, VAT.VAT7);

    orderDetail1.setProduct(product2);

    // Check that all modified objects are dirty/detached
    assertDirty(containmentObject, transaction);
    assertDirty(product1, transaction);
    assertDirty(orderDetail1, transaction);
    assertEquals("Element should be detached", true, transaction.getDetachedObjects().containsKey(elementToDeleteID));

    // Step 2: get back to initial clean state step by step
    product1.getOrderDetails().add(0, orderDetail1);
    assertVersion(orderDetail1InitialVersion, orderDetail1, true);

    product1.getOtherVATs().move(oldVATIndexForProduct1, VAT.VAT7);
    assertDirty(product1, transaction);

    product1.setName(oldProduct1Name);
    assertVersion(product1InitialVersion, product1, true);

    containmentList.add(childObject);
    assertDirty(containmentObject, transaction);

    containmentList.remove(newObject);
    assertDirty(containmentObject, transaction);

    containmentObject.setAttributeOptional(oldContainmentObjectAttributeValue);
    assertVersion(containmentObjectInitialVersion, containmentObject);

    // Step 3: commit
    transaction.commit();
    assertVersion(orderDetail1InitialVersion, orderDetail1);
    assertVersion(product1InitialVersion, product1);
    assertVersion(containmentObjectInitialVersion, containmentObject);
  }

  /**
   * Ensures that the given object is clean and that its version was not increased compared to the given initial version.
   * @param initalVersion the initial version of the object before it was modified
   * @param element the EObject to test
   */
  private void assertVersion(int expectedVersion, EObject element)
  {
    assertVersion(expectedVersion, element, false);
  }

  private void assertVersion(int expectedVersion, EObject element, boolean expectedDirtyTransaction)
  {
    CDOObject cdoElement = CDOUtil.getCDOObject(element);
    assertClean(cdoElement, ((CDOResource)cdoElement.eResource()).cdoView());
    assertEquals(expectedVersion, cdoElement.cdoRevision().getVersion());
    assertEquals("Transaction is not expected to contain revision deltas on the given object", null, transaction.getRevisionDeltas().get(cdoElement.cdoID()));

    if (expectedDirtyTransaction)
    {
      assertNotSame("Transaction is expected to contain revision deltas", CDOIDUtil.createMap(), transaction.getRevisionDeltas());
    }
    else
    {
      assertEquals("Transaction is not expected to contain revision deltas", CDOIDUtil.createMap(), transaction.getRevisionDeltas());
    }

    assertEquals("Transaction is expected to be " + (expectedDirtyTransaction ? "dirty" : "clean"), expectedDirtyTransaction, transaction.isDirty());
  }

  /**
   * Ensures that the given object is dirty and that its version was increased compared to the given initial version.
   * @param initalVersion the initial version of the object before it was modified
   * @param element the EObject to test
   */
  private static void assertVersionIncreased(int initialVersion, EObject element)
  {
    CDOObject cdoElement = CDOUtil.getCDOObject(element);
    assertEquals(initialVersion + 1, cdoElement.cdoRevision().getVersion());
  }

  /**
   * Ensures that the CDOServer does not contain 2 identical Adjacent revisions.
   */
  private static void assertServerDoesNotContainIdenticalAdjacentRevision(CDOID targetElement, CDOTransaction transaction)
  {
    // Step 1: get all revisions for the given ID through the revision manager
    CDORevisionManager revisionManager = transaction.getSession().getRevisionManager();
    int initialChunkSize = transaction.getSession().options().getCollectionLoadingPolicy().getInitialChunkSize();

    int version = CDOBranchVersion.FIRST_VERSION;
    List<CDORevision> revisions = new LinkedList<>();

    boolean noMoreRevisionsAvailable = false;
    while (!noMoreRevisionsAvailable)
    {
      CDOBranchVersion branchVersion = new CDOBranchVersionImpl(transaction.getBranch(), version);
      if (revisionManager.containsRevisionByVersion(targetElement, branchVersion))
      {
        CDORevision fetched = revisionManager.getRevisionByVersion(targetElement, branchVersion, initialChunkSize, true);
        if (fetched != null)
        {
          revisions.add(fetched);
        }
      }
      else
      {
        noMoreRevisionsAvailable = true;
      }

      version++;
    }

    // Step 2: compare all adjacent revisions and check that there are different
    if (revisions.size() > 1)
    {
      for (int i = 0; i < revisions.size() - 1; i++)
      {
        CDORevision rev1 = revisions.get(i);
        CDORevision rev2 = revisions.get(i + 1);
        CDORevisionDelta rDelta = rev1.compare(rev2);
        if (rDelta.getFeatureDeltas().size() == 0)
        {
          // The revision delta contains no feature deltas
          // this means the revisions are identical
          fail("2 Adjacent Revisions should never be equals.");
        }
      }
    }
  }
}
