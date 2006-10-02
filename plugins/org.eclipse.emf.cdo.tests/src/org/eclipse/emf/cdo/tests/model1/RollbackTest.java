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


import org.eclipse.emf.cdo.client.OptimisticControlException;
import org.eclipse.emf.cdo.client.ResourceManager;

import org.eclipse.emf.ecore.EObject;

import testmodel1.TreeNode;

import java.util.List;

import junit.framework.ComparisonFailure;


public class RollbackTest extends AbstractModel1Test
{
  public void testNoNotification() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String NEW_ROOT1 = "new root 1";
    final String NEW_ROOT2 = "new root 2";
    final long TIME_LIMIT = 10000;

    // Client1 creates resource
    TreeNode root = createNode(ROOT);
    saveRoot(root, RESOURCE);

    // Client2 loads and modifies resource
    TreeNode loaded = (TreeNode) loadRoot(RESOURCE);
    loaded.setStringFeature(NEW_ROOT2);

    // Client1 modifies and commits resource
    root.setStringFeature(NEW_ROOT1);
    root.eResource().save(null);

    // Give server and client2 enough time to get notified
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < TIME_LIMIT)
    {
      assertNode(NEW_ROOT2, loaded);
      Thread.sleep(1);
    }
  }

  public void testOptimisticControlException() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String NEW_ROOT1 = "new root 1";
    final String NEW_ROOT2 = "new root 2";
    final long TIME_LIMIT = 1000;
    final boolean[] notificationReceived = { false};

    // Client1 creates resource
    TreeNode client1 = createNode(ROOT);
    saveRoot(client1, RESOURCE);

    // Client2 loads and modifies resource
    TreeNode client2 = (TreeNode) loadRoot(RESOURCE);
    client2.setStringFeature(NEW_ROOT2);

    // Client2 remembers notifications
    ResourceManager resourceManager = client2.cdoGetResource().getResourceManager();
    resourceManager.addInvalidationListener(new ResourceManager.InvalidationListener()
    {
      public void notifyInvalidation(ResourceManager resourceManager, List<EObject> invalidated,
          List<EObject> deferred)
      {
        notificationReceived[0] = true;
      }
    });

    // Client1 modifies and commits resource
    client1.setStringFeature(NEW_ROOT1);
    client1.eResource().save(null);

    // Give server and client2 enough time to get notified
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < TIME_LIMIT)
    {
      if (notificationReceived[0]) break;
      Thread.sleep(1);
    }

    assertTrue("Notification did not arrive within " + TIME_LIMIT + " millis",
        notificationReceived[0]);

    // Client2 commits resource, verify that exception occurs
    try
    {
      client2.eResource().save(null);
      fail("OptimisticControlException did not occur");
    }
    catch (OptimisticControlException ex)
    {
      ; // This is the expected case
    }

    // Verify that client2 has been rolled back
    try
    {
      assertNode(NEW_ROOT2, client2);
      fail("Client2 has not been rolled back");
    }
    catch (ComparisonFailure ex)
    {
      ; // This is the expected case
    }

    // TODO Clarify what should be done with invalidated objects on rollback
    assertNode(NEW_ROOT1, client2);
  }
}
