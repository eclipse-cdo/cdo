/*
 * Copyright (c) 2010-2012, 2014 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * See bug 298561
 *
 * @author Eike Stepper
 */
public class Bugzilla_298561_Test extends AbstractCDOTest
{
  private static String RESOURCENAME = "/r1";

  public void testNew() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(getResourcePath(RESOURCENAME));

    // Create referencee and store it
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);
    tx.commit();

    // Create referencer, don't store it -- keep it as NEW
    RefSingleNonContainedNPL referencer = getModel4Factory().createRefSingleNonContainedNPL();
    r1.getContents().add(referencer);
    referencer.setElement(referencee);
    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    EReference ref = getModel4Package().getRefSingleNonContainedNPL_Element();
    boolean isSet = referencer.eIsSet(ref);
    if (isSet)
    {
      assertNull(referencer.getElement());
    }
  }

  public void testDirty() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(getResourcePath(RESOURCENAME));

    // Create referencee and referencer (but no reference yet), and store them
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);

    RefSingleNonContainedNPL referencer = getModel4Factory().createRefSingleNonContainedNPL();
    r1.getContents().add(referencer);
    tx.commit();

    // Create the reference, making the referencer dirty
    referencer.setElement(referencee);
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    boolean isSet = referencer.eIsSet(getModel4Package().getRefSingleNonContainedNPL_Element());
    if (isSet)
    {
      assertNull(referencer.getElement());
    }
  }

  public void testNewMulti() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(getResourcePath(RESOURCENAME));

    // Create referencee and store it
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);
    tx.commit();

    // Create referencer, don't store it -- keep it as NEW
    RefMultiNonContainedNPL referencer = getModel4Factory().createRefMultiNonContainedNPL();
    r1.getContents().add(referencer);
    referencer.getElements().add(referencee);
    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    boolean isSet = referencer.eIsSet(getModel4Package().getRefMultiNonContainedNPL_Elements());
    if (isSet && referencer.getElements().size() > 0)
    {
      assertNull(referencer.getElements().get(0));
    }
  }

  public void testDirtyMulti() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(getResourcePath(RESOURCENAME));

    // Create referencee and referencer (but no reference yet), and store them
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);
    RefMultiNonContainedNPL referencer = getModel4Factory().createRefMultiNonContainedNPL();
    r1.getContents().add(referencer);
    tx.commit();

    // Create the reference, making the referencer dirty
    referencer.getElements().add(referencee);
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    boolean isSet = referencer.eIsSet(getModel4Package().getRefMultiNonContainedNPL_Elements());
    if (isSet && referencer.getElements().size() > 0)
    {
      assertNull(referencer.getElements().get(0));
    }
  }

  private void doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(getResourcePath(RESOURCENAME));
    ContainedElementNoOpposite referencee = (ContainedElementNoOpposite)r1.getContents().get(0);
    EcoreUtil.delete(referencee);
    tx.commit();
    session.close();
  }
}
