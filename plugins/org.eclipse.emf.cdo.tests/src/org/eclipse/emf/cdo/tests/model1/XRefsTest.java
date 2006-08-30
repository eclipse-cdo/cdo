/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.model1;


import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureListIterator;

import testmodel1.TestModel1Package;
import testmodel1.TreeNode;
import junit.framework.ComparisonFailure;


public class XRefsTest extends AbstractModel1Test
{
  public void testList() throws Exception
  {
    final String RESOURCE1 = "/test/res1";
    final String RESOURCE2 = "/test/res2";
    final String ROOT1 = "root1";
    final String ROOT2 = "root2";
    final String CHILD2 = "child2";

    { // Execution
      TreeNode root1 = createNode(ROOT1);
      saveRoot(root1, RESOURCE1);

      TreeNode root2 = createNode(ROOT2);
      root2.getReferences().add(root1);

      TreeNode child2 = createNode(CHILD2, root2);
      child2.getReferences().add(root1);
      saveRoot(root2, RESOURCE2);
    }

    { // Verification
      TreeNode root1 = (TreeNode) loadRoot(RESOURCE1);
      EList list = root1.eCrossReferences();
      assertNotNull(list);
      assertEquals(2, list.size());

      try
      {
        assertNode(ROOT2, (TreeNode) list.get(0));
        assertNode(CHILD2, (TreeNode) list.get(1));
      }
      catch (ComparisonFailure ex)
      {
        assertNode(ROOT2, (TreeNode) list.get(1));
        assertNode(CHILD2, (TreeNode) list.get(0));
      }
    }
  }

  public void testIterator() throws Exception
  {
    final String RESOURCE1 = "/test/res1";
    final String RESOURCE2 = "/test/res2";
    final String ROOT1 = "root1";
    final String ROOT2 = "root2";
    final String CHILD2 = "child2";

    { // Execution
      TreeNode root1 = createNode(ROOT1);
      saveRoot(root1, RESOURCE1);

      TreeNode root2 = createNode(ROOT2);
      root2.getReferences().add(root1);

      TreeNode child2 = createNode(CHILD2, root2);
      child2.getReferences().add(root1);
      saveRoot(root2, RESOURCE2);
    }

    { // Verification
      TreeNode root1 = (TreeNode) loadRoot(RESOURCE1);
      EList list = root1.eCrossReferences();
      int count = 0;

      for (FeatureListIterator it = (FeatureListIterator) list.iterator(); it.hasNext();)
      {
        count++;
        TreeNode node = (TreeNode) it.next();
        assertTrue(ROOT2.equals(node.getStringFeature()) || CHILD2.equals(node.getStringFeature()));

        EReference reference = (EReference) it.feature();
        assertEquals(TestModel1Package.eINSTANCE.getTreeNode_References(), reference);
      }

      assertEquals(2, count);
    }
  }
}
