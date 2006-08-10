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
package org.eclipse.emf.cdo.tests;


import org.eclipse.emf.common.util.EList;

import testmodel1.TestModel1Factory;
import testmodel1.TreeNode;

import java.util.Arrays;


public class Model1Test extends AbstractTopologyTest
{
  protected static final String PATH1 = "/test/res1";

  public void testSimple() throws Exception
  {
    TreeNode node = createNode("root");
    createNode("node1", node);
    createNode("node2", node);
    createNode("node3", node);
    saveRoot(node, PATH1);

    TreeNode root = (TreeNode) loadRoot(PATH1);
    assertNotNull(root);
    assertEquals("root", root.getStringFeature());

    EList children = root.getChildren();
    assertEquals(3, children.size());
    assertEquals("node1", ((TreeNode) children.get(0)).getStringFeature());
    assertEquals("node2", ((TreeNode) children.get(1)).getStringFeature());
    assertEquals("node3", ((TreeNode) children.get(2)).getStringFeature());
  }

  protected TreeNode createNode(String name)
  {
    return createNode(name, null);
  }

  protected TreeNode createNode(String name, TreeNode parent)
  {
    TreeNode node = TestModel1Factory.eINSTANCE.createTreeNode();
    node.setStringFeature(name);
    node.setParent(parent);
    return node;
  }

  protected TreeNode createNode(String name, TreeNode parent, TreeNode[] references)
  {
    TreeNode node = createNode(name, parent);
    node.getReferences().add(Arrays.asList(references));
    return node;
  }
}
