/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
  public void test() throws CommitException
  {
    CDOID resourceID;
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
      transaction.commit();
      resourceID = resource.cdoID();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource("/res1");
      msg("New resource: " + resource);
      msg("newObjects:");

      for (Entry<CDOID, CDOObject> entry : transaction.getNewObjects().entrySet())
      {
        msg("    " + entry + ", state: " + entry.getValue().cdoState());
        assertNew(entry.getValue(), transaction);
      }

      // Fetch the persisted resource that has the same URI
      CDOResource resource1 = (CDOResource)transaction.getObject(resourceID);
      msg("Persisted resource: " + resource1);

      msg("newObjects:");
      for (Entry<CDOID, CDOObject> entry : transaction.getNewObjects().entrySet())
      {
        msg("    " + entry + ", state: " + entry.getValue().cdoState());
        assertNew(entry.getValue(), transaction);
      }

      transaction.commit();
    }
  }
}
