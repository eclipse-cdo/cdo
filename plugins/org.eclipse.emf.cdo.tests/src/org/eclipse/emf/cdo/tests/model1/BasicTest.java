/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
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

import testmodel1.TreeNode;


@SuppressWarnings("unused")
public class BasicTest extends AbstractModel1Test
{
  //  public void testSimple() throws Exception
  //  {
  //    final String RESOURCE = "/test/res";
  //    final String ROOT = "root";
  //    final String[] CHILDREN = { "a", "b", "c"};
  //
  //    // Execution
  //    {
  //      TreeNode root = createNode(ROOT);
  //      for (String name : CHILDREN)
  //        createNode(name, root);
  //
  //      saveRoot(root, RESOURCE);
  //    }
  //
  //    // Verification
  //    {
  //      TreeNode root = (TreeNode) loadRoot(RESOURCE);
  //      assertNode(ROOT, root);
  //
  //      EList children = root.getChildren();
  //      assertChildren(CHILDREN, children);
  //    }
  //  }
  //
  //  public void testContainment() throws Exception
  //  {
  //    final String RESOURCE = "/test/res";
  //    final String ROOT = "root";
  //    final String[] CHILDREN_A = { "a1", "a2", "a3", "a4"};
  //    final String[] CHILDREN_B = { "b1", "b2", "b3", "b4"};
  //
  //    // Execution
  //    {
  //      TreeNode root = createNode(ROOT);
  //      TreeNode a = root;
  //      for (String name : CHILDREN_A)
  //        a = createNode(name, a);
  //
  //      TreeNode b = root;
  //      for (String name : CHILDREN_B)
  //        b = createNode(name, b);
  //
  //      saveRoot(root, RESOURCE);
  //    }
  //
  //    // Verification
  //    {
  //      TreeNode root = (TreeNode) loadRoot(RESOURCE);
  //      assertNode(ROOT, root);
  //      assertPath(CHILDREN_A, root);
  //      assertPath(CHILDREN_B, root);
  //    }
  //  }
  //
  //  public void testXRefAlreadyLoaded() throws Exception
  //  {
  //    final String RESOURCE = "/test/res";
  //    final String ROOT = "root";
  //    final String[] CHILDREN_A = { "a1", "a2", "a3", "a4"};
  //    final String[] CHILDREN_B = { "b1", "b2", "b3", "b4"};
  //
  //    // Execution
  //    {
  //      TreeNode root = createNode(ROOT);
  //      TreeNode a = root;
  //      for (String name : CHILDREN_A)
  //        a = createNode(name, a);
  //
  //      TreeNode b = root;
  //      for (String name : CHILDREN_B)
  //        b = createNode(name, b);
  //
  //      a.getReferences().add(b);
  //      saveRoot(root, RESOURCE);
  //    }
  //
  //    // Verification
  //    {
  //      TreeNode root = (TreeNode) loadRoot(RESOURCE);
  //      assertNode(ROOT, root);
  //
  //      TreeNode a = findPath(CHILDREN_A, root);
  //      TreeNode b = findPath(CHILDREN_B, root); // PRE-LOAD
  //      assertNode(CHILDREN_B[3], (TreeNode) a.getReferences().get(0));
  //    }
  //  }

  public void testXRefNotYetLoaded() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN_A = { "a1", "a2", "a3", "a4"};
    final String[] CHILDREN_B = { "b1", "b2", "b3", "b4"};

    // Execution
    {
      TreeNode root = createNode(ROOT);
      TreeNode a = root;
      for (String name : CHILDREN_A)
        a = createNode(name, a);

      TreeNode b = root;
      for (String name : CHILDREN_B)
        b = createNode(name, b);

      a.getReferences().add(b);
      saveRoot(root, RESOURCE);
    }

    // Verification
    {
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);

      TreeNode a = findPath(CHILDREN_A, root);
      // DO NOT LOAD BY CONTAINMENT: 
      // TreeNode b = findPath(CHILDREN_B, root);
      TreeNode b = (TreeNode) a.getReferences().get(0);
      assertNode(CHILDREN_B[3], b);
    }
  }
}
