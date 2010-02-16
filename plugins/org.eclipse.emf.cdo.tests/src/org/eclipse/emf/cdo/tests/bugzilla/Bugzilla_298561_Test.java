/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Eike Stepper
 */
public class Bugzilla_298561_Test extends AbstractCDOTest
{
  private static String RESOURCENAME = "/r1";

  public void test_new()
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(RESOURCENAME);

    // Create referencee and store it
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);
    tx.commit();

    // Create referencer, don't store it -- keep it as NEW
    RefSingleNonContainedNPL referencer = getModel4Factory().createRefSingleNonContainedNPL();
    r1.getContents().add(referencer);
    referencer.setElement(referencee);
    assertEquals(CDOState.NEW, ((CDOObject)referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    //
    try
    {
      EReference ref = getModel4Package().getRefSingleNonContainedNPL_Element();
      boolean isSet = referencer.eIsSet(ref);
      if (isSet)
      {
        referencer.getElement();
      }
    }
    catch (ObjectNotFoundException e)
    {
      fail("Should not have thrown ObjectNotFoundException");
    }

    tx.close();
    session.close();
  }

  public void test_dirty()
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(RESOURCENAME);

    // Create referencee and store it
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);
    tx.commit();

    // Create referencer, store it, then make it DIRTY
    RefSingleNonContainedNPL referencer = getModel4Factory().createRefSingleNonContainedNPL();
    r1.getContents().add(referencer);
    referencer.setElement(referencee);
    tx.commit();
    referencer.setElement(null);
    referencer.setElement(referencee);
    assertEquals(CDOState.DIRTY, ((CDOObject)referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    try
    {
      boolean isSet = referencer.eIsSet(getModel4Package().getRefSingleNonContainedNPL_Element());
      if (isSet)
      {
        referencer.getElement();
      }
    }
    catch (ObjectNotFoundException e)
    {
      fail("Should not have thrown ObjectNotFoundException");
    }

    tx.close();
    session.close();
  }

  private void doSecondSession()
  {
    CDOSession session = openModel1Session();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(RESOURCENAME);
    ContainedElementNoOpposite referencee = (ContainedElementNoOpposite)r1.getContents().get(0);
    EcoreUtil.delete(referencee);
    tx.commit();
    tx.close();
    session.close();
  }

  public void test_new_multi()
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(RESOURCENAME);

    // Create referencee and store it
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);
    tx.commit();

    // Create referencer, don't store it -- keep it as NEW
    RefMultiNonContainedNPL referencer = getModel4Factory().createRefMultiNonContainedNPL();
    r1.getContents().add(referencer);
    referencer.getElements().add(referencee);
    assertEquals(CDOState.NEW, ((CDOObject)referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    //
    try
    {
      boolean isSet = referencer.eIsSet(getModel4Package().getRefMultiNonContainedNPL_Elements());
      if (isSet && referencer.getElements().size() > 0)
      {
        referencer.getElements().get(0);
      }
    }
    catch (ObjectNotFoundException e)
    {
      fail("Should not have thrown ObjectNotFoundException");
    }

    tx.close();
    session.close();
  }

  public void test_dirty_multi()
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    session.getPackageRegistry().putEPackage(getModel4Package());

    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(RESOURCENAME);

    // Create referencee and store it
    ContainedElementNoOpposite referencee = getModel4Factory().createContainedElementNoOpposite();
    r1.getContents().add(referencee);
    tx.commit();

    // Create referencer, store it, then make it DIRTY
    RefMultiNonContainedNPL referencer = getModel4Factory().createRefMultiNonContainedNPL();
    r1.getContents().add(referencer);
    referencer.getElements().add(referencee);
    tx.commit();
    referencer.getElements().remove(referencee);
    referencer.getElements().add(referencee);
    assertEquals(CDOState.DIRTY, ((CDOObject)referencer).cdoState());

    // Delete the referencee in 2nd session
    doSecondSession();

    // Refresh
    session.refresh();

    //
    try
    {
      boolean isSet = referencer.eIsSet(getModel4Package().getRefMultiNonContainedNPL_Elements());
      if (isSet && referencer.getElements().size() > 0)
      {
        referencer.getElements().get(0);
      }
    }
    catch (ObjectNotFoundException e)
    {
      fail("Should not have thrown ObjectNotFoundException");
    }

    tx.close();
    session.close();
  }
}
