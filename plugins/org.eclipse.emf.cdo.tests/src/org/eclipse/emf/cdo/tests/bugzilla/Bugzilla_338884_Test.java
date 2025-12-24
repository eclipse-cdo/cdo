/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.CommitIntegrityException;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * See bug 338884.
 *
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

  public void test_canHandleUnset() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    model4Factory factory = getModel4Factory();
    ContainedElementNoOpposite dummy = factory.createContainedElementNoOpposite();
    resource.getContents().add(dummy);

    RefSingleNonContainedNPL referencer = factory.createRefSingleNonContainedNPL();
    resource.getContents().add(referencer);

    ContainedElementNoOpposite referencee = factory.createContainedElementNoOpposite();
    resource.getContents().add(referencee);

    referencer.setElement(referencee);

    tx.commit();

    EReference ref = getModel4Package().getRefSingleNonContainedNPL_Element();
    referencer.eUnset(ref);

    // Make the dummy object dirty to make the commit partial
    dummy.setName("dirty");

    Set<EObject> committables = new HashSet<>();
    committables.add(referencer);
    committables.add(referencee);
    tx.setCommittables(committables);
    tx.commit();

    session.close();
  }

  public void test_nonBidiMultiRef_newTarget() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    model4Factory factory = getModel4Factory();

    CDOResource resource = transaction.createResource(getResourcePath("test"));
    transaction.commit();

    RefMultiContainedNPL parent = factory.createRefMultiContainedNPL();
    resource.getContents().add(parent);

    ContainedElementNoOpposite child = factory.createContainedElementNoOpposite();
    parent.getElements().add(child);

    transaction.setCommittables(new HashSet<>(Arrays.asList(resource, parent)));
    try
    {
      transaction.commit();
      fail("Should have thrown an exception");
    }
    catch (CommitException e)
    {
      Throwable c = e.getCause();
      if (c instanceof TransactionException && c.getCause() instanceof CommitIntegrityException)
      {
        // Good
      }
      else
      {
        throw e;
      }
    }

    session.close();
  }

  public void test_nonBidiSingleRef_newTarget() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    model4Factory factory = getModel4Factory();

    CDOResource resource = transaction.createResource(getResourcePath("test"));
    transaction.commit();

    RefSingleContainedNPL parent = factory.createRefSingleContainedNPL();
    resource.getContents().add(parent);

    ContainedElementNoOpposite child = factory.createContainedElementNoOpposite();
    parent.setElement(child);

    transaction.setCommittables(new HashSet<>(Arrays.asList(resource, parent)));
    try
    {
      transaction.commit();
      fail("Should have thrown an exception");
    }
    catch (CommitException e)
    {
      Throwable c = e.getCause();
      if (c instanceof TransactionException && c.getCause() instanceof CommitIntegrityException)
      {
        // Good
      }
      else
      {
        throw e;
      }
    }

    session.close();
  }
}
