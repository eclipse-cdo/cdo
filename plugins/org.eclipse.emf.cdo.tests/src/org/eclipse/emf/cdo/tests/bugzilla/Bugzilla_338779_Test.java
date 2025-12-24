/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EObject;

import java.util.Date;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_338779_Test extends AbstractCDOTest
{
  public void test_refresh() throws CommitException
  {
    runTest(null);
  }

  public void test_passiveUpdate_invalidations() throws CommitException
  {
    runTest(PassiveUpdateMode.INVALIDATIONS);
  }

  public void test_passiveUpdate_changes() throws CommitException
  {
    runTest(PassiveUpdateMode.CHANGES);
  }

  public void test_passiveUpdate_additions() throws CommitException
  {
    runTest(PassiveUpdateMode.ADDITIONS);
  }

  private void runTest(PassiveUpdateMode passiveUpdateMode) throws CommitException
  {
    final CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    if (passiveUpdateMode != null)
    {
      session.options().setPassiveUpdateEnabled(true);
      session.options().setPassiveUpdateMode(passiveUpdateMode);
    }
    else
    {
      session.options().setPassiveUpdateEnabled(false);
    }

    CDOResource resource1 = transaction.createResource(getResourcePath("/r1")); //$NON-NLS-1$

    PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
    purchaseOrder1.setDate(new Date());
    resource1.getContents().add(purchaseOrder1);

    transaction.commit();

    check(purchaseOrder1, session);

    long timestamp = doSecondSession();
    if (passiveUpdateMode != null)
    {
      session.waitForUpdate(timestamp);

      if (passiveUpdateMode == PassiveUpdateMode.INVALIDATIONS)
      {
        // Read something on the object to force load
        purchaseOrder1.getDate();
      }
    }
    else
    {
      session.refresh();
    }

    check(purchaseOrder1, session);

    transaction.close();
    session.close();
  }

  private void check(EObject eObject, CDOSession session)
  {
    CDOObject obj = CDOUtil.getCDOObject(eObject);
    assertClean(obj, obj.cdoView());
    CDORevision rev1 = obj.cdoRevision();
    CDORevision rev2 = session.getRevisionManager().getRevisionByVersion(rev1.getID(), rev1, 0, false);
    assertEquals(rev1, rev2);
    assertSame(rev1, rev2);
  }

  private long doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.getResource(getResourcePath("/r1")); //$NON-NLS-1$

    // Change the purchaseOrder
    PurchaseOrder purchaseOrder1 = (PurchaseOrder)resource1.getContents().get(0);
    purchaseOrder1.setDate(new Date());

    CDOCommitInfo commitInfo = transaction.commit();
    transaction.close();
    session.close();

    return commitInfo.getTimeStamp();
  }
}
