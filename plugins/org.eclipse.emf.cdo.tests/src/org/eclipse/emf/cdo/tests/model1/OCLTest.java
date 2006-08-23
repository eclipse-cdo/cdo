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


import org.eclipse.emf.cdo.client.ocl.CDOHelperUtil;

import org.eclipse.emf.ocl.helper.HelperUtil;
import org.eclipse.emf.ocl.helper.IOCLHelper;
import org.eclipse.emf.ocl.helper.OCLParsingException;

import testmodel1.TreeNode;

import java.util.Set;


public class OCLTest extends AbstractModel1Test
{
  public void testNotEmpty() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};
    final String EXPR = "self.children->isEmpty()";

    { // Create CDO resource
      TreeNode root = createNode(ROOT);
      createChildren(CHILDREN, root);
      saveRoot(root, RESOURCE);
    }

    TreeNode root = (TreeNode) loadRoot(RESOURCE);
    IOCLHelper helper = HelperUtil.createOCLHelper();
    helper.setContext(root);

    try
    {
      Object result = helper.evaluate(root, EXPR);
      assertFalse(result);
    }
    catch (OCLParsingException ex)
    {
      ex.printStackTrace();
      fail("Parse failed: " + ex.getLocalizedMessage());
    }
  }

  public void testEmpty() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String EXPR = "self.children->isEmpty()";

    { // Create CDO resource
      TreeNode root = createNode(ROOT);
      saveRoot(root, RESOURCE);
    }

    TreeNode root = (TreeNode) loadRoot(RESOURCE);
    IOCLHelper helper = HelperUtil.createOCLHelper();
    helper.setContext(root);

    try
    {
      Object result = helper.evaluate(root, EXPR);
      assertTrue(result);
    }
    catch (OCLParsingException ex)
    {
      ex.printStackTrace();
      fail("Parse failed: " + ex.getLocalizedMessage());
    }
  }

  public void testExtendedNodeExtent() throws Exception
  {
    final String RESOURCE = "/test/res";

    { // Create CDO resource
      TreeNode root = createNode("root");
      TreeNode a = createNode("a", root);
      TreeNode b = createNode("b", root);
      TreeNode c = createNode("c", root);
      createNode("a1", a);
      createNode("b1", b);
      createNode("c1", c);
      createExtended("a2", a);
      createExtended("b2", b);
      createExtended("c2", c);
      saveRoot(root, RESOURCE);
    }

    TreeNode root = (TreeNode) loadRoot(RESOURCE);
    //    CDOResource resource = root.cdoGetResource();
    IOCLHelper helper = CDOHelperUtil.createOCLHelper();
    helper.setContext(root);

    try
    {
      Set result = (Set) helper.evaluate(root, "ExtendedNode.allInstances()");
      assertEquals(3, result.size());
    }
    catch (OCLParsingException ex)
    {
      ex.printStackTrace();
      fail("Parse failed: " + ex.getLocalizedMessage());
    }
  }

  public void testTreeNodeExtent() throws Exception
  {
    final String RESOURCE = "/test/res";

    { // Create CDO resource
      TreeNode root = createNode("root");
      TreeNode a = createNode("a", root);
      TreeNode b = createNode("b", root);
      TreeNode c = createNode("c", root);
      createNode("a1", a);
      createNode("b1", b);
      createNode("c1", c);
      createExtended("a2", a);
      createExtended("b2", b);
      createExtended("c2", c);
      saveRoot(root, RESOURCE);
    }

    TreeNode root = (TreeNode) loadRoot(RESOURCE);
    //    CDOResource resource = root.cdoGetResource();
    IOCLHelper helper = CDOHelperUtil.createOCLHelper();
    helper.setContext(root);

    try
    {
      Set result = (Set) helper.evaluate(root, "TreeNode.allInstances()");
      assertEquals(10, result.size());
    }
    catch (OCLParsingException ex)
    {
      ex.printStackTrace();
      fail("Parse failed: " + ex.getLocalizedMessage());
    }
  }

  public void testExtentTwoResources() throws Exception
  {
    final String RESOURCE1 = "/test/res1";
    final String RESOURCE2 = "/test/res2";

    { // Create first CDO resource
      TreeNode root = createNode("root");
      TreeNode a = createNode("a", root);
      TreeNode b = createNode("b", root);
      TreeNode c = createNode("c", root);
      createNode("a1", a);
      createNode("b1", b);
      createNode("c1", c);
      createExtended("a2", a);
      createExtended("b2", b);
      createExtended("c2", c);
      saveRoot(root, RESOURCE1);
    }

    { // Create second CDO resource
      TreeNode root = createNode("root");
      TreeNode a = createNode("a", root);
      TreeNode b = createNode("b", root);
      TreeNode c = createNode("c", root);
      createNode("a1", a);
      createNode("b1", b);
      createNode("c1", c);
      createExtended("a2", a);
      createExtended("b2", b);
      createExtended("c2", c);
      saveRoot(root, RESOURCE2);
    }

    TreeNode root = (TreeNode) loadRoot(RESOURCE1);
    //    CDOResource resource = root.cdoGetResource();
    IOCLHelper helper = CDOHelperUtil.createOCLHelper();
    helper.setContext(root);

    try
    {
      Set result = (Set) helper.evaluate(root, "ExtendedNode.allInstances()");
      assertEquals(3, result.size());
    }
    catch (OCLParsingException ex)
    {
      ex.printStackTrace();
      fail("Parse failed: " + ex.getLocalizedMessage());
    }
  }

  public void testGlobalExtent() throws Exception
  {
    final String RESOURCE1 = "/test/res1";
    final String RESOURCE2 = "/test/res2";

    { // Create first CDO resource
      TreeNode root = createNode("root");
      TreeNode a = createNode("a", root);
      TreeNode b = createNode("b", root);
      TreeNode c = createNode("c", root);
      createNode("a1", a);
      createNode("b1", b);
      createNode("c1", c);
      createExtended("a2", a);
      createExtended("b2", b);
      createExtended("c2", c);
      saveRoot(root, RESOURCE1);
    }

    { // Create second CDO resource
      TreeNode root = createNode("root");
      TreeNode a = createNode("a", root);
      TreeNode b = createNode("b", root);
      TreeNode c = createNode("c", root);
      createNode("a1", a);
      createNode("b1", b);
      createNode("c1", c);
      createExtended("a2", a);
      createExtended("b2", b);
      createExtended("c2", c);
      saveRoot(root, RESOURCE2);
    }

    TreeNode root = (TreeNode) loadRoot(RESOURCE1);
    //    CDOResource resource = root.cdoGetResource();
    IOCLHelper helper = CDOHelperUtil.createOCLHelper(true);
    helper.setContext(root);

    try
    {
      Set result = (Set) helper.evaluate(root, "ExtendedNode.allInstances()");
      assertEquals(6, result.size());
    }
    catch (OCLParsingException ex)
    {
      ex.printStackTrace();
      fail("Parse failed: " + ex.getLocalizedMessage());
    }
  }
}
