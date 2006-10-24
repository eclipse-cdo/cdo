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


import testmodel1.EmptyNode;
import testmodel1.EmptyRefNode;
import testmodel1.TreeNode;


/**
 * attributes in super class returned by server, ignored by client.
 * 
 * The issue occurs when an object is loaded that has no attributes.
 * All the attributes are in the super class.
 * 
 * When the object is loaded the server transmits all of the attributes,
 * which include the super class attributes. The client side ignored the
 * attributes and mis-interpreted the message containing the attributes as
 * the next object. From this point on nothing will work...
 * 
 * So, it looks like the client and server don't match here.
 * 
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=154389
 */
public class Bugzilla154389Test extends AbstractModel1Test
{
  public void testEmptyRoot() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final boolean BOOLEAN_VALUE = true;
    final int INT_VALUE = 12345;

    { // Execution
      TreeNode root = createEmpty(ROOT);
      root.setBooleanFeature(BOOLEAN_VALUE);
      root.setIntFeature(INT_VALUE);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);
      assertEquals(BOOLEAN_VALUE, root.isBooleanFeature());
      assertEquals(INT_VALUE, root.getIntFeature());
    }
  }

  public void testEmptyChild() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String CHILD = "child";
    final boolean BOOLEAN_VALUE = true;
    final int INT_VALUE = 12345;

    { // Execution
      TreeNode root = createEmpty(ROOT);
      EmptyNode child = createEmpty(CHILD, root);
      child.setBooleanFeature(BOOLEAN_VALUE);
      child.setIntFeature(INT_VALUE);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);

      TreeNode child = findChild(CHILD, root);
      assertEquals(BOOLEAN_VALUE, child.isBooleanFeature());
      assertEquals(INT_VALUE, child.getIntFeature());
    }
  }

  public void testEmptyViaRef() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String CHILD_A = "a";
    final String CHILD_B = "b";
    final boolean BOOLEAN_VALUE = true;
    final int INT_VALUE = 12345;

    { // Execution
      TreeNode root = createNode(ROOT);
      TreeNode a = createNode(CHILD_A, root);
      TreeNode b = createEmpty(CHILD_B, root);
      b.setBooleanFeature(BOOLEAN_VALUE);
      b.setIntFeature(INT_VALUE);
      a.getReferences().add(b);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);

      TreeNode a = (TreeNode) root.getChildren().get(0);
      assertNode(CHILD_A, a);

      TreeNode b = (TreeNode) a.getReferences().get(0);
      assertNode(CHILD_B, b);
      assertEquals(BOOLEAN_VALUE, b.isBooleanFeature());
      assertEquals(INT_VALUE, b.getIntFeature());
    }
  }

  public void testEmptyViaRefWithRef() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String CHILD_A = "a";
    final String CHILD_B = "b";
    final boolean BOOLEAN_VALUE = true;
    final int INT_VALUE = 12345;

    { // Execution
      TreeNode root = createNode(ROOT);
      TreeNode a = createNode(CHILD_A, root);
      TreeNode b = createEmpty(CHILD_B, root);
      b.setBooleanFeature(BOOLEAN_VALUE);
      b.setIntFeature(INT_VALUE);
      a.getReferences().add(b);
      b.getReferences().add(a);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);

      TreeNode a = (TreeNode) root.getChildren().get(0);
      assertNode(CHILD_A, a);

      TreeNode b = (TreeNode) a.getReferences().get(0);
      assertNode(CHILD_B, b);
      assertEquals(BOOLEAN_VALUE, b.isBooleanFeature());
      assertEquals(INT_VALUE, b.getIntFeature());
      assertEquals(CHILD_A, ((TreeNode) b.getReferences().get(0)).getStringFeature());
    }
  }

  public void testEmptyRefRoot() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final boolean BOOLEAN_VALUE = true;
    final int INT_VALUE = 12345;

    { // Execution
      EmptyRefNode root = createEmptyRef(ROOT);
      root.setBooleanFeature(BOOLEAN_VALUE);
      root.setIntFeature(INT_VALUE);
      root.getMoreReferences().add(root);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      EmptyRefNode root = (EmptyRefNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);
      assertEquals(BOOLEAN_VALUE, root.isBooleanFeature());
      assertEquals(INT_VALUE, root.getIntFeature());
      assertEquals(ROOT, ((TreeNode) root.getMoreReferences().get(0)).getStringFeature());
    }
  }
}
