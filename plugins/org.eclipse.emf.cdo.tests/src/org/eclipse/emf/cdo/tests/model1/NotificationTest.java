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


import org.eclipse.emf.cdo.client.ResourceManager;

import org.eclipse.emf.ecore.resource.Resource;

import testmodel1.TreeNode;
import junit.framework.ComparisonFailure;


public class NotificationTest extends AbstractModel1Test
{
  public void testRoot() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String NEW_ROOT = "new root";
    final long TIME_LIMIT = 1000;

    // Client1 creates resource
    TreeNode root = createNode(ROOT);
    saveRoot(root, RESOURCE);

    // Client2 loads resource
    TreeNode loaded = (TreeNode) loadRoot(RESOURCE);

    // Client1 modifies and commits resource
    root.setStringFeature(NEW_ROOT);
    Resource resource = root.eResource();
    resource.save(null);

    // Give server and client2 enough time to get notified
    long start = System.currentTimeMillis();
    try
    {
      assertNode(NEW_ROOT, loaded);
    }
    catch (ComparisonFailure ex)
    {
      long duration = System.currentTimeMillis() - start;
      if (duration > TIME_LIMIT) throw ex;
      Thread.sleep(1);
    }
  }

  public void testChildNotYetLoaded() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String CHILD = "a";
    final String NEW_NAME = "a2";
    final long TIME_LIMIT = 1000;

    // Client1 creates resource
    TreeNode root = createNode(ROOT);
    TreeNode a = createNode(CHILD, root);
    saveRoot(root, RESOURCE);

    // Client2 loads resource
    TreeNode loaded = (TreeNode) loadRoot(RESOURCE);
    TreeNode loadedA = (TreeNode) loaded.getChildren().get(0);

    // Client1 modifies and commits resource
    a.setStringFeature(NEW_NAME);
    Resource resource = root.eResource();
    resource.save(null);

    // Give server and client2 enough time to get notified
    long start = System.currentTimeMillis();
    try
    {
      assertNode(NEW_NAME, loadedA);
    }
    catch (ComparisonFailure ex)
    {
      long duration = System.currentTimeMillis() - start;
      if (duration > TIME_LIMIT) throw ex;
      Thread.sleep(1);
    }
  }

  public void testChildAlreadyLoaded() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String CHILD = "a";
    final String NEW_NAME = "a2";
    final long TIME_LIMIT = 1000;

    // Client1 creates resource
    TreeNode root = createNode(ROOT);
    TreeNode a = createNode(CHILD, root);
    saveRoot(root, RESOURCE);

    // Client2 loads resource
    TreeNode loaded = (TreeNode) loadRoot(RESOURCE);
    TreeNode loadedA = (TreeNode) loaded.getChildren().get(0);
    assertNode(CHILD, loadedA);

    // Client1 modifies and commits resource
    a.setStringFeature(NEW_NAME);
    Resource resource = root.eResource();
    resource.save(null);

    // Give server and client2 enough time to get notified
    long start = System.currentTimeMillis();
    try
    {
      assertNode(NEW_NAME, loadedA);
    }
    catch (ComparisonFailure ex)
    {
      long duration = System.currentTimeMillis() - start;
      if (duration > TIME_LIMIT) throw ex;
      Thread.sleep(1);
    }
  }

  public void testListener() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String CHILD = "a";
    final String NEW_NAME = "a2";
    final long TIME_LIMIT = 1000;
    final boolean[] notificationReceived = { false};

    // Client1 creates resource
    TreeNode root = createNode(ROOT);
    TreeNode a = createNode(CHILD, root);
    saveRoot(root, RESOURCE);

    // Client2 loads resource
    TreeNode loaded = (TreeNode) loadRoot(RESOURCE);
    TreeNode loadedA = (TreeNode) loaded.getChildren().get(0);
    assertNode(CHILD, loadedA);

    // Client2 remembers notifications
    ResourceManager client2 = loaded.cdoGetResource().getResourceManager();
    client2.addInvalidationListener(new ResourceManager.InvalidationListener()
    {
      public void notifyInvalidation(ResourceManager resourceManager, long[] oids)
      {
        notificationReceived[0] = true;
      }
    });

    // Client1 modifies and commits resource
    a.setStringFeature(NEW_NAME);
    Resource resource = root.eResource();
    resource.save(null);

    // Give server and client2 enough time to get notified
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < TIME_LIMIT)
    {
      if (notificationReceived[0]) return;
      Thread.sleep(1);
    }

    fail("Notification did not arrive within " + TIME_LIMIT + " millis");
  }
}
