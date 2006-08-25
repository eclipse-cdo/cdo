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
import org.eclipse.emf.cdo.client.PausableChangeRecorder;
import org.eclipse.emf.cdo.client.ResourceManager;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import testmodel1.TreeNode;

import java.util.Iterator;


public class AdapterTest extends AbstractModel1Test
{
  public void testFiveLevels() throws Exception
  {
    final String RESOURCE = "/test/res";
    final String ROOT = "root";
    final String[] CHILDREN = { "a", "b", "c"};

    TreeNode root = createNode(ROOT);
    TreeNode[] children = createChildren(CHILDREN, root);
    for (int i = 0; i < children.length; i++)
      createChildren(new String[] { "a" + i, "b" + i, "c" + i}, children[i]);
    CDOResource resource = saveRoot(root, RESOURCE);

    ResourceManager resourceManager = resource.getResourceManager();
    ResourceSet resourceSet = resourceManager.getResourceSet();
    PausableChangeRecorder transaction = resourceManager.getTransaction();
    assertAdapter(resourceSet, transaction);
  }

  protected void assertAdapter(Notifier notifier, Adapter adapter)
  {
    EList adapters = notifier.eAdapters();
    assertNotNull(notifier.toString(), adapters);

    int count = 0;
    for (Iterator it = adapters.iterator(); it.hasNext();)
    {
      Adapter element = (Adapter) it.next();
      count += element == adapter ? 1 : 0;
    }
    assertEquals(notifier.toString(), 1, count);

    if (notifier instanceof ResourceSet)
    {
      ResourceSet resourceSet = (ResourceSet) notifier;
      EList resources = resourceSet.getResources();
      for (Iterator it = resources.iterator(); it.hasNext();)
      {
        Resource resource = (Resource) it.next();
        assertAdapter(resource, adapter);
      }
    }
    else if (notifier instanceof Resource)
    {
      Resource resource = (Resource) notifier;
      EList contents = resource.getContents();
      for (Iterator it = contents.iterator(); it.hasNext();)
      {
        EObject object = (EObject) it.next();
        assertAdapter(object, adapter);
      }
    }
    else if (notifier instanceof EObject)
    {
      EObject object = (EObject) notifier;
      EList contents = object.eContents();
      for (Iterator it = contents.iterator(); it.hasNext();)
      {
        EObject child = (EObject) it.next();
        assertAdapter(child, adapter);
      }
    }
  }
}
