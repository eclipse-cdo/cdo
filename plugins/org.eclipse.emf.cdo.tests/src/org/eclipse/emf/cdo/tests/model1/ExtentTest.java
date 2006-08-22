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
import org.eclipse.emf.cdo.client.ClassInfo;
import org.eclipse.emf.cdo.client.PackageManager;
import org.eclipse.emf.cdo.client.ResourceManager;
import org.eclipse.emf.cdo.client.protocol.ClientCDOProtocolImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.net4j.core.Channel;

import testmodel1.ExtendedNode;
import testmodel1.TestModel1Package;
import testmodel1.TreeNode;


public class ExtentTest extends AbstractModel1Test
{
  public void testExactGlobal() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};
    final String OTHER_CHILD = "XYZ";

    TreeNode root = createNode(ROOT);
    TreeNode[] exactChildren = createChildren(CHILDREN, root);
    createExtended(OTHER_CHILD, root);

    CDOResource resource = saveRoot(root, RESOURCE);
    ResourceManager resourceManager = resource.getResourceManager();
    Channel channel = resourceManager.getChannel();

    PackageManager packageManager = resourceManager.getPackageManager();
    EClass eClass = TestModel1Package.eINSTANCE.getTreeNode();
    ClassInfo classInfo = packageManager.getClassInfo(eClass);
    int cid = classInfo.getCID();

    EList extent = ClientCDOProtocolImpl.requestQueryExtent(channel, cid, true);
    assertEquals(1 + CHILDREN.length, extent.size());

    assertTrue(extent.contains(root));
    assertResource(RESOURCE, root);

    for (TreeNode child : exactChildren)
    {
      assertTrue(extent.contains(child));
      assertResource(RESOURCE, child);
    }
  }

  public void testDerivedGlobal() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};
    final String OTHER_CHILD = "XYZ";

    TreeNode root = createNode(ROOT);
    TreeNode[] exactChildren = createChildren(CHILDREN, root);
    ExtendedNode otherChild = createExtended(OTHER_CHILD, root);

    CDOResource resource = saveRoot(root, RESOURCE);
    ResourceManager resourceManager = resource.getResourceManager();
    Channel channel = resourceManager.getChannel();

    PackageManager packageManager = resourceManager.getPackageManager();
    EClass eClass = TestModel1Package.eINSTANCE.getTreeNode();
    ClassInfo classInfo = packageManager.getClassInfo(eClass);
    int cid = classInfo.getCID();

    EList extent = ClientCDOProtocolImpl.requestQueryExtent(channel, cid, false);
    assertEquals(2 + CHILDREN.length, extent.size());

    assertTrue(extent.contains(root));
    assertResource(RESOURCE, root);

    assertTrue(extent.contains(otherChild));
    assertResource(RESOURCE, otherChild);

    for (TreeNode child : exactChildren)
    {
      assertTrue(extent.contains(child));
      assertResource(RESOURCE, child);
    }
  }
}
