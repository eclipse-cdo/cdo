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


@SuppressWarnings("unused")
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
      TreeNode b = findPath(PATH_B, root);
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
      CDOResource cdoResource = ((CDOPersistentImpl) root).cdoGetResource();

      TreeNode a = findPath(PATH_A, root);
      TreeNode b = (TreeNode) a.getReferences().get(0);
      Resource resource = b.eResource();
      assertEquals(cdoResource, resource);
    }
  }
}
