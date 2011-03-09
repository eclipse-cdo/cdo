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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.CommitIntegrityException;

import java.util.Collections;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_338884_Test extends AbstractCDOTest
{
  public void test_single() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    // 1. Create object A;
    model4Factory factory = getModel4Factory();
    RefSingleNonContainedNPL referencer = factory.createRefSingleNonContainedNPL();
    resource.getContents().add(referencer);

    // 2. Commit;
    tx.commit();

    // 3. Create object B;
    ContainedElementNoOpposite element = factory.createContainedElementNoOpposite();
    resource.getContents().add(element);

    // 4. Create reference from object A to object B;
    referencer.setElement(element);

    // 5. Add object A to committables, but not add B;
    tx.setCommittables(Collections.singleton(referencer));

    // 6. Commit.
    try
    {
      tx.commit();
      fail("Should have thrown " + CommitException.class.getName());
    }
    catch (CommitException e)
    {
      Throwable t = e.getCause().getCause();
      if (t instanceof CommitIntegrityException)
      {
        // Good
      }
      else
      {
        fail("Unexpected exception type: " + t.getClass().getName());
      }
    }

    tx.close();
    session.close();
  }

  public void test_multi() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    // 1. Create object A;
    model4Factory factory = getModel4Factory();
    RefMultiNonContainedNPL referencer = factory.createRefMultiNonContainedNPL();
    resource.getContents().add(referencer);

    // 2. Commit;
    tx.commit();

    // 3. Create object B;
    ContainedElementNoOpposite element = factory.createContainedElementNoOpposite();
    resource.getContents().add(element);

    // 4. Create reference from object A to object B;
    referencer.getElements().add(element);

    // 5. Add object A to committables, but not add B;
    tx.setCommittables(Collections.singleton(referencer));

    // 6. Commit.
    try
    {
      tx.commit();
      fail("Should have thrown " + CommitException.class.getName());
    }
    catch (CommitException e)
    {
      Throwable t = e.getCause().getCause();
      if (t instanceof CommitIntegrityException)
      {
        // Good
      }
      else
      {
        fail("Unexpected exception type: " + t.getClass().getName());
      }
    }

    tx.close();
    session.close();
  }
}
