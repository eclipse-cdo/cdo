/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class Bugzilla_318919_Test extends AbstractCDOTest
{
  private static int counter;

  private Category createCategoryTree(int depth)
  {
    if (depth == 0)
    {
      return null;
    }

    Category category = getModel1Factory().createCategory();
    for (int i = 0; i < 2; i++)
    {
      Category child = createCategoryTree(depth - 1);
      if (child != null)
      {
        category.getCategories().add(child);
      }
    }

    for (int i = 0; i < 3; i++)
    {
      Product1 child = getModel1Factory().createProduct1();
      // generate a unique id
      String id = "test " + depth + "_" + i + "_" + ++counter;
      child.setName(id);
      category.getProducts().add(child);
    }

    return category;
  }

  private int countObjects(EObject tree)
  {
    int count = 1;
    for (Iterator<?> it = tree.eAllContents(); it.hasNext();)
    {
      it.next();
      ++count;
    }

    return count;
  }

  public void testPrefetchResource_PROXY() throws Exception
  {
    int expected;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Category tree = createCategoryTree(3);
      expected = countObjects(tree);
      resource.getContents().add(tree);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    TestRevisionManager revisionManager = (TestRevisionManager)session.getRevisionManager();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/res1"));
    assertProxy(resource);

    revisionManager.resetAdditionalCounter();
    resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    assertEquals(expected, revisionManager.getAdditionalCounter());
  }

  public void testPrefetchResource_CLEAN() throws Exception
  {
    int expected;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Category tree = createCategoryTree(3);
      expected = countObjects(tree);
      resource.getContents().add(tree);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    TestRevisionManager revisionManager = (TestRevisionManager)session.getRevisionManager();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/res1"));
    resource.getContents().get(0);
    assertClean(resource, transaction);

    revisionManager.resetAdditionalCounter();
    resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    assertEquals(expected, revisionManager.getAdditionalCounter());
  }

  public void testPrefetchObject() throws Exception
  {
    int expected;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Category tree = createCategoryTree(3);
      expected = countObjects(tree);
      resource.getContents().add(tree);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    TestRevisionManager revisionManager = (TestRevisionManager)session.getRevisionManager();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/res1"));
    Category category = (Category)resource.getContents().get(0);
    category.getName(); // Not really needed
    --expected; // Tree root is not "additional"
    assertClean(category, transaction);

    revisionManager.resetAdditionalCounter();
    CDOUtil.getCDOObject(category).cdoPrefetch(CDORevision.DEPTH_INFINITE);
    assertEquals(expected, revisionManager.getAdditionalCounter());
  }
}
