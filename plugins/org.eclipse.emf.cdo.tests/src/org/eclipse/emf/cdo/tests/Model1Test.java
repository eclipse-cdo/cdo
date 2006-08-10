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



import testmodel1.TestModel1Factory;
import testmodel1.TreeNode;

import java.util.Arrays;



public class Model1Test extends AbstractTopologyTest
{
  public void testSimple() throws Exception
  {
    TreeNode root = createNode("root", null);
    createNode("node1", root);
    createNode("node2", root);
    createNode("node3", root);

    saveResource("/test/res1", root);
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
