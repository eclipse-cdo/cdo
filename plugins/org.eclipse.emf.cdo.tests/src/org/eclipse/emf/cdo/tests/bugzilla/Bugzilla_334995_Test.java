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

import org.eclipse.emf.ecore.resource.Resource;

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
  public void testURIClash() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resourceA = transaction.createResource(getResourcePath("/resA"));
    resourceA.getContents().add(getModel1Factory().createCustomer());
    transaction.commit();
    System.out.println("Persisted resource: " + resourceA);

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resourceB2 = transaction2.createResource(getResourcePath("/resB"));
    resourceB2.getContents().add(getModel1Factory().createSupplier());

    resourceA.setName("resB");
    commitAndSync(transaction, transaction2);

    System.out.println("newObjects:");
    for (Entry<CDOID, CDOObject> entry : transaction2.getNewObjects().entrySet())
    {
      System.out.println(" " + entry + ", state: " + entry.getValue().cdoState());
      assertNew(entry.getValue(), transaction2);
    }

    System.out.println("resources:");
    for (Resource resource : transaction2.getResourceSet().getResources())
    {
      System.out.println(" " + resource);
    }

    // Fetch the persisted resource that has the same URI
    CDOResource resourceA2 = (CDOResource)transaction2.getObject(resourceA.cdoID());
    System.out.println("Remote resource: " + resourceA2);
    System.out.println("Local resource:  " + resourceB2);

    System.out.println("newObjects:");
    for (Entry<CDOID, CDOObject> entry : transaction2.getNewObjects().entrySet())
    {
      System.out.println(" " + entry + ", state: " + entry.getValue().cdoState());
      assertNew(entry.getValue(), transaction2);
    }

    System.out.println("resources:");
    for (Resource resource : transaction2.getResourceSet().getResources())
    {
      System.out.println(" " + resource);
    }

    transaction2.commit();
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
