/*
 * Copyright (c) 2014-2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Alex Lagarde - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests ensuring that moving elements inside the same containment list does not cause any issue when committing.
 *
 * @author Alex Lagarde
 */
@Skips({ IRepositoryConfig.CAPABILITY_AUDITING, IRepositoryConfig.CAPABILITY_BRANCHING, IRepositoryConfig.CAPABILITY_UNORDERED_LISTS })
public class Bugzilla_435532_Test extends AbstractCDOTest
{
  private static final int CHILDREN_NUMBER = 100;

  private static final int CHILDREN_TO_MOVE_NUMBER = 50;

  private Company root;

  private CDOSession cdoSession;

  private CDOTransaction cdoTransaction;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    // Initialize semantic model
    cdoSession = openSession();
    cdoTransaction = cdoSession.openTransaction();
    CDOResource resource = cdoTransaction.createResource(getResourcePath("resource"));

    // Create a root with CHILDREN_NUMBER children
    root = getModel1Factory().createCompany();
    for (int i = 0; i < CHILDREN_NUMBER; i++)
    {
      root.getCategories().add(getModel1Factory().createCategory());
    }
    resource.getContents().add(root);
    cdoTransaction.commit();
  }

  /**
   * Ensures that moving elements at the end of the same containment list does not cause any issue when committing.
   */
  public void testDragChildrenAtListEnd() throws ConcurrentAccessException, CommitException
  {
    doTestDragChildren(20, CHILDREN_NUMBER - 1);
  }

  /**
   * Ensures that moving elements at the beginning of the same containment list does not cause any issue when committing.
   */
  public void testDragChildrenAtListBegin() throws ConcurrentAccessException, CommitException
  {
    doTestDragChildren(20, 0);
  }

  public void testDragChildrenFromEndToBegin() throws ConcurrentAccessException, CommitException
  {
    doTestDragChildren(CHILDREN_NUMBER - CHILDREN_TO_MOVE_NUMBER - 1, 0);
  }

  private void doTestDragChildren(int oldPosition, int newPosition) throws ConcurrentAccessException, CommitException
  {
    // Step 1: get children to move
    List<Category> childrenToMove = new ArrayList<>(root.getCategories().subList(oldPosition, oldPosition + CHILDREN_TO_MOVE_NUMBER));
    List<CDOID> childrenToMoveIds = new ArrayList<>();
    for (Category child : childrenToMove)
    {
      childrenToMoveIds.add(CDOUtil.getCDOObject(child).cdoID());
    }

    // Step 2: move each child through a command
    for (int i = 0; i < childrenToMove.size(); i++)
    {
      Category childToMove = childrenToMove.get(i);
      root.getCategories().move(newPosition, childToMove);
    }

    // Step 3: commit
    cdoTransaction.commit();

    // Step 4: check that move correctly occured
    cdoTransaction.close();
    cdoSession.close();
    cdoSession = openSession();
    cdoTransaction = cdoSession.openTransaction();
    root = (Company)cdoTransaction.getResource(getResourcePath("resource")).getContents().get(0);
    childrenToMove = new ArrayList<>();
    for (CDOID childID : childrenToMoveIds)
    {
      childrenToMove.add((Category)CDOUtil.getEObject(cdoTransaction.getObject(childID)));
    }
    for (int i = 0; i < childrenToMove.size(); i++)
    {
      int expectedPosition = Math.abs(newPosition - i);
      Category child = childrenToMove.get(childrenToMove.size() - 1 - i);
      assertEquals(expectedPosition, root.getCategories().indexOf(child));
    }
  }

  @Override
  protected void doTearDown() throws Exception
  {
    cdoTransaction.close();
    cdoSession.close();
    super.doTearDown();
  }
}
