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


import org.eclipse.emf.cdo.client.CDOResource;
import org.eclipse.emf.cdo.client.impl.CDOPersistentImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

import testmodel1.TreeNode;


public class BasicTest extends AbstractModel1Test
{
  public void testSimple() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};

    { // Execution
      TreeNode root = createNode(ROOT);
      createChildren(CHILDREN, root);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);

      EList children = root.getChildren();
      assertChildren(CHILDREN, children);
    }
  }

  public void testContainment() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] PATH_A = { "a1", "a2", "a3", "a4"};
    final String[] PATH_B = { "b1", "b2", "b3", "b4"};

    { // Execution
      TreeNode root = createNode(ROOT);
      createPath(PATH_A, root, false);
      createPath(PATH_B, root, false);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);
      assertPath(PATH_A, root);
      assertPath(PATH_B, root);
    }
  }

  public void testXRefAlreadyLoaded() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] PATH_A = { "a1", "a2", "a3", "a4"};
    final String[] PATH_B = { "b1", "b2", "b3", "b4"};

    { // Execution
      TreeNode root = createNode(ROOT);
      TreeNode a = createPath(PATH_A, root, false);
      TreeNode b = createPath(PATH_B, root, false);
      a.getReferences().add(b);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);

      TreeNode a = findPath(PATH_A, root);
      findPath(PATH_B, root);
      assertNode(PATH_B[3], (TreeNode) a.getReferences().get(0));
    }
  }

  public void testXRefNotYetLoaded() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] PATH_A = { "a1", "a2", "a3", "a4"};
    final String[] PATH_B = { "b1", "b2", "b3", "b4"};

    { // Execution
      TreeNode root = createNode(ROOT);
      TreeNode a = createPath(PATH_A, root, false);
      TreeNode b = createPath(PATH_B, root, false);
      a.getReferences().add(b);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      assertNode(ROOT, root);

      TreeNode a = findPath(PATH_A, root);
      TreeNode b = (TreeNode) a.getReferences().get(0);
      assertNode(PATH_B[3], b);
    }
  }

  public void testGetResource() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] PATH = { "a1", "a2", "a3", "a4"};

    { // Execution
      TreeNode root = createNode(ROOT);
      createPath(PATH, root, false);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode node = (TreeNode) loadRoot(RESOURCE);
      CDOResource cdoResource = ((CDOPersistentImpl) node).cdoGetResource();
      while (node != null)
      {
        Resource resource = node.eResource();
        assertEquals(cdoResource, resource);

        if (node.getChildren().isEmpty())
        {
          node = null;
        }
        else
        {
          node = (TreeNode) node.getChildren().get(0);
        }
      }
    }
  }

  public void testGetResourceWithXRef() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] PATH_A = { "a1", "a2", "a3", "a4"};
    final String[] PATH_B = { "b1", "b2", "b3", "b4"};

    { // Execution
      TreeNode root = createNode(ROOT);
      TreeNode a = createPath(PATH_A, root, false);
      TreeNode b = createPath(PATH_B, root, false);
      a.getReferences().add(b);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      TreeNode a = findPath(PATH_A, root);
      TreeNode b = (TreeNode) a.getReferences().get(0);
      assertResource(RESOURCE, b);
    }
  }

  public void testInterResourceXRef1() throws Exception
  {
    final String RESOURCE1 = "/test/res1";
    final String RESOURCE2 = "/test/res2";
    final String ROOT1 = "root1";
    final String ROOT2 = "root2";

    { // Execution
      TreeNode root1 = createNode(ROOT1);
      saveRoot(root1, RESOURCE1);

      TreeNode root2 = createNode(ROOT2);
      root2.getReferences().add(root1);
      saveRoot(root2, RESOURCE2);
    }

    { // Verification
      TreeNode root2 = (TreeNode) loadRoot(RESOURCE2);
      assertNode(ROOT2, root2);
      assertResource(RESOURCE2, root2);

      TreeNode root1 = (TreeNode) root2.getReferences().get(0);
      assertNode(ROOT1, root1);
      assertResource(RESOURCE1, root1);
    }
  }

  public void testInterResourceXRef2() throws Exception
  {
    final String RESOURCE1 = "/test/res1";
    final String RESOURCE2 = "/test/res2";
    final String ROOT1 = "root1";
    final String ROOT2 = "root2";
    final String CHILD1 = "child1";

    { // Execution
      TreeNode root1 = createNode(ROOT1);
      TreeNode child1 = createNode(CHILD1, root1);
      saveRoot(root1, RESOURCE1);

      TreeNode root2 = createNode(ROOT2);
      root2.getReferences().add(child1);
      saveRoot(root2, RESOURCE2);
    }

    { // Verification
      TreeNode root2 = (TreeNode) loadRoot(RESOURCE2);
      assertNode(ROOT2, root2);
      assertResource(RESOURCE2, root2);

      TreeNode child1 = (TreeNode) root2.getReferences().get(0);
      assertNode(CHILD1, child1);
      assertResource(RESOURCE1, child1);
    }
  }

  public void testInterResourceXRef3() throws Exception
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
      TreeNode child2 = createNode(CHILD2, root2);
      child2.getReferences().add(root1);
      saveRoot(root2, RESOURCE2);
    }

    { // Verification
      TreeNode root2 = (TreeNode) loadRoot(RESOURCE2);
      assertNode(ROOT2, root2);
      assertResource(RESOURCE2, root2);

      TreeNode child2 = (TreeNode) root2.getChildren().get(0);
      assertNode(CHILD2, child2);
      assertResource(RESOURCE2, child2);

      TreeNode root1 = (TreeNode) child2.getReferences().get(0);
      assertNode(ROOT1, root1);
      assertResource(RESOURCE1, root1);
    }
  }

  public void testGetContainer() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] PATH = { "a1", "a2", "a3", "a4"};

    { // Execution
      TreeNode root = createNode(ROOT);
      createPath(PATH, root, false);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      TreeNode a4 = findPath(PATH, root);

      TreeNode a3 = a4.getParent();
      assertNode(PATH[2], a3);
      assertEquals(a3, a4.eContainer());

      TreeNode a2 = a3.getParent();
      assertNode(PATH[1], a2);
      assertEquals(a2, a3.eContainer());

      TreeNode a1 = a2.getParent();
      assertNode(PATH[0], a1);
      assertEquals(a1, a2.eContainer());

      TreeNode a0 = a1.getParent();
      assertNode(ROOT, a0);
      assertEquals(a0, a1.eContainer());
      assertEquals(a0, root);

      assertResource(RESOURCE, a0);
      assertResource(RESOURCE, a1);
      assertResource(RESOURCE, a2);
      assertResource(RESOURCE, a3);
      assertResource(RESOURCE, a4);
    }
  }

  public void testGetContainerWithXRef() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] PATH = { "a1", "a2", "a3", "a4"};

    { // Execution
      TreeNode root = createNode(ROOT);
      TreeNode a4 = createPath(PATH, root, false);
      root.getReferences().add(a4);
      saveRoot(root, RESOURCE);
    }

    { // Verification
      TreeNode root = (TreeNode) loadRoot(RESOURCE);
      TreeNode a4 = (TreeNode) root.getReferences().get(0);

      TreeNode a3 = a4.getParent();
      assertNode(PATH[2], a3);
      assertEquals(a3, a4.eContainer());

      TreeNode a2 = a3.getParent();
      assertNode(PATH[1], a2);
      assertEquals(a2, a3.eContainer());

      TreeNode a1 = a2.getParent();
      assertNode(PATH[0], a1);
      assertEquals(a1, a2.eContainer());

      TreeNode a0 = a1.getParent();
      assertNode(ROOT, a0);
      assertEquals(a0, a1.eContainer());
      assertEquals(a0, root);

      assertResource(RESOURCE, a0);
      assertResource(RESOURCE, a1);
      assertResource(RESOURCE, a2);
      assertResource(RESOURCE, a3);
      assertResource(RESOURCE, a4);
    }
  }
}
