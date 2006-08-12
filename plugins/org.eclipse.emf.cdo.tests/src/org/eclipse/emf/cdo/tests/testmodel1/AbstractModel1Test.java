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
package org.eclipse.emf.cdo.tests.testmodel1;


import org.eclipse.emf.cdo.tests.topology.AbstractTopologyTest;

import org.eclipse.emf.common.util.EList;

import org.springframework.jdbc.core.JdbcTemplate;

import testmodel1.TestModel1Factory;
import testmodel1.TreeNode;

import java.util.Arrays;
import java.util.Iterator;


public class AbstractModel1Test extends AbstractTopologyTest
{
  @Override
  protected void wipeDatabase(JdbcTemplate jdbc)
  {
    super.wipeDatabase(jdbc);
    jdbc.execute("DROP TABLE TREE_NODE");
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

  protected void assertNode(String name, TreeNode node)
  {
    assertNotNull(node);
    assertNotNull(name);
    assertEquals(name, node.getStringFeature());
  }

  protected void assertChildren(String[] names, EList nodes)
  {
    assertNotNull(nodes);
    assertNotNull(names);
    assertEquals(names.length, nodes.size());
    for (int i = 0; i < names.length; i++)
    {
      assertEquals(names[i], ((TreeNode) nodes.get(i)).getStringFeature());
    }
  }

  protected void assertChild(String name, EList nodes)
  {
    assertNotNull(nodes);
    assertNotNull(name);
    assertEquals(1, nodes.size());
    assertEquals(name, ((TreeNode) nodes.get(0)).getStringFeature());
  }

  protected void assertPath(String[] names, TreeNode node)
  {
    TreeNode result = findPath(names, node);
    assertNotNull(result);
  }

  protected TreeNode findPath(String[] names, TreeNode node)
  {
    assertNotNull(names);
    for (int i = 0; i < names.length; i++)
    {
      String name = names[i];
      TreeNode child = findNode(name, node.getChildren());
      if (child == null) return null;
      node = child;
    }

    return node;
  }

  protected TreeNode findNode(String name, EList nodes)
  {
    assertNotNull(name);
    for (Iterator it = nodes.iterator(); it.hasNext();)
    {
      TreeNode node = (TreeNode) it.next();
      if (name.equals(node.getStringFeature()))
      {
        return node;
      }
    }

    return null;
  }
}
