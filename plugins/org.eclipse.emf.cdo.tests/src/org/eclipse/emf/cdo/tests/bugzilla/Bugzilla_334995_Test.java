/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import java.util.Map.Entry;

/**
 * Bug 334995 - CDOTransaction corrupted by persisted + new resource with same URI
 *
 * @author Caspar De Groot
 */
public class Bugzilla_334995_Test extends AbstractCDOTest
{
  /**
   * The following test seems obsolete because (as of bug xxxxxx) no local changes can create resource duplicates.
   */
  public void _test() throws CommitException
  {
    CDOID resourceID = persistResources("/res1")[0];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      msg("New resource: " + resource);
      msg("newObjects:");

      for (Entry<CDOID, CDOObject> entry : transaction.getNewObjects().entrySet())
      {
        msg(" " + entry + ", state: " + entry.getValue().cdoState());
        assertNew(entry.getValue(), transaction);
      }

      // Fetch the persisted resource that has the same URI
      CDOResource resource1 = (CDOResource)transaction.getObject(resourceID);
      msg("Persisted resource: " + resource1);

      msg("newObjects:");
      for (Entry<CDOID, CDOObject> entry : transaction.getNewObjects().entrySet())
      {
        msg(" " + entry + ", state: " + entry.getValue().cdoState());
        assertNew(entry.getValue(), transaction);
      }

      transaction.commit();
    }
  }

  public void testRename() throws CommitException
  {
    CDOID[] resourceIDs = persistResources("/res1", "/res2");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource1 = transaction.getResource(getResourcePath("/res1"));
      resource1.setPath("/res2");

      CDOResource resource2 = (CDOResource)transaction.getObject(resourceIDs[1]);

      resource1.getContents().add(getModel1Factory().createAddress());
      resource2.getContents().add(getModel1Factory().createAddress());
      transaction.commit();
    }
  }

  private CDOID[] persistResources(String... resourceNames) throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    int i = 0;
    CDOResource[] resources = new CDOResource[resourceNames.length];
    for (String resourceName : resourceNames)
    {
      resources[i++] = transaction.createResource(getResourcePath(resourceName));
    }
    transaction.commit();

    CDOID[] resourceIDs = new CDOID[resourceNames.length];
    for (i = 0; i < resources.length; i++)
    {
      resourceIDs[i] = resources[i].cdoID();
    }

    session.close();
    return resourceIDs;
  }
}
