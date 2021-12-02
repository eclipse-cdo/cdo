/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Eike Stepper
 */
public class Bugzilla_321986_Test extends AbstractCDOTest
{
  public void testRollback() throws CommitException
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();

    CDOResource resource1 = transaction1.createResource(getResourcePath("test"));
    Address address = getModel1Factory().createAddress();
    resource1.getContents().add(address);
    transaction1.commit();

    CDOObject cdoAddress = CDOUtil.getCDOObject(address);
    cdoAddress.cdoWriteLock().lock();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.options().setOptimisticLockingTimeout(200);
    CDOResource resource2 = transaction2.getResource(getResourcePath("test"));
    resource2.getContents().clear();

    try
    {
      // We must fail here, because object was locked
      transaction2.commit();
      fail("Commit should have failed");
    }
    catch (CommitException e)
    {
      // Good
    }

    // Remove in the 1st transaction
    EcoreUtil.delete(address);
    transaction1.commit();

    try
    {
      transaction2.rollback();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail("Exception on rollbak:" + ex.getMessage());
    }

    session2.close();
    session1.close();
  }
}
