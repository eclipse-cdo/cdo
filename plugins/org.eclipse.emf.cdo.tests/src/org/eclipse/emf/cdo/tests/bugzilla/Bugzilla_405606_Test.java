/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

/**
 * @author Eike Stepper
 */
public class Bugzilla_405606_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testUnchunkedRevisionWithPCL() throws Exception
  {
    {
      // Set up a resource with 10 objects as content
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/test"));

      Category cat = getModel1Factory().createCategory();
      cat.setName("Container");
      res.getContents().add(cat);

      for (int i = 0; i < 10; i++)
      {
        Category c = getModel1Factory().createCategory();
        c.setName("Test " + i);
        cat.getCategories().add(c);
      }

      transaction.commit();
      session.close();
    }

    // Now clear the cache on server
    clearCache(getRepository().getRevisionManager());

    {
      // Open a new session with PCL enabled
      CDOSession session = openSession();
      session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));

      // Load the category into the resource (is now partially loaded)
      CDOView view = session.openView();
      CDOResource res = view.getResource(getResourcePath("/test"));
      Category cat = (Category)res.getContents().get(0);

      IOUtil.OUT().println("Unchunked: " + ((InternalCDORevision)CDOUtil.getCDOObject(cat).cdoRevision()).isUnchunked());

      session.close();
    }

    {
      // Open a new session, this time with PCL disabled
      CDOSession session = openSession();
      session.options().setCollectionLoadingPolicy(null);

      // Load the category into the resource (is now partially loaded)
      CDOView view = session.openView();
      CDOResource res = view.getResource(getResourcePath("/test"));
      Category cat = (Category)res.getContents().get(0);

      IOUtil.OUT().println("Unchunked: " + ((InternalCDORevision)CDOUtil.getCDOObject(cat).cdoRevision()).isUnchunked());

      // Try to iterate over the items
      for (Category child : cat.getCategories())
      {
        IOUtil.OUT().println(child.getName());
      }

      session.close();
    }
  }
}
