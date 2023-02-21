/*
 * Copyright (c) 2009-2013, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;

/**
 * Add new packages infos in the Notifications process at the client side
 * <p>
 * See bug 267050
 *
 * @author Simon McDuff
 */
public class Bugzilla_267050_Test extends AbstractCDOTest
{
  @CleanRepositoriesBefore(reason = "Package registration")
  public void testBugzilla_267050() throws Exception
  {
    @SuppressWarnings("unchecked")
    final Collection<CDOPackageUnit> newPackagesUnits[] = new Collection[1];
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    session2.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOSessionInvalidationEvent)
        {
          CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
          newPackagesUnits[0] = e.getNewPackageUnits();
        }
      }
    });

    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOResource res = transaction.createResource(getResourcePath("/res"));
    Company specialPurchaseOrder = getModel1Factory().createCompany();
    res.getContents().add(specialPurchaseOrder);
    transaction.commit();

    assertNoTimeout(() -> newPackagesUnits[0] != null);
    assertEquals(1, newPackagesUnits[0].size());
    assertEquals(getModel1Package().getNsURI(), newPackagesUnits[0].iterator().next().getTopLevelPackageInfo().getPackageURI());
  }
}
