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


import org.eclipse.emf.cdo.client.CDOResource;
import org.eclipse.emf.cdo.client.ResourceManager;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import testmodel1.TestModel1Factory;
import testmodel1.TreeNode;

import java.util.Arrays;


public class IntegrationTest extends AbstractIntegrationTest
{
  public void testSimple() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    ResourceManager resourceManager = createResourceManager(resourceSet);

    CDOResource resource = (CDOResource) resourceSet.createResource(URI
        .createURI("cdo:///test/res1"));

    TreeNode node0 = createNode("node0", null);
    TreeNode node1 = createNode("node1", node0);
    TreeNode node2 = createNode("node2", node0);
    TreeNode node3 = createNode("node3", node0);

    resource.getContents().add(node0);
    resourceManager.commit();
    resourceManager.stop();

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
