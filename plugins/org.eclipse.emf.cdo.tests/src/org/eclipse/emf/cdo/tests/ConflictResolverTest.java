/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver.MergeLocalChangesPerFeature;

/**
 * @author Simon McDuff
 */
public class ConflictResolverTest extends AbstractCDOTest
{
  public void testMergeLocalChangesPerFeature_Basic() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();

    transaction.getOrCreateResource("/res1").getContents().add(address);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(new MergeLocalChangesPerFeature());
    Address address2 = (Address)transaction2.getOrCreateResource("/res1").getContents().get(0);

    address2.setCity("OTTAWA");

    address.setName("NAME1");

    transaction.commit();

    // Resolver should be triggered. Should we always used a timer ?
    Thread.sleep(1000);

    assertEquals(false, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  public void testMergeLocalChangesPerFeature_BasicException() throws Exception
  {
    // Does not work in legacy as long as there is not getter interception
    skipConfig(LEGACY);

    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();

    transaction.getOrCreateResource("/res1").getContents().add(address);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(new MergeLocalChangesPerFeature());
    final Address address2 = (Address)transaction2.getOrCreateResource("/res1").getContents().get(0);

    address2.setCity("OTTAWA");

    address.setCity("NAME1");

    transaction.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return CDOUtil.getCDOObject(address2).cdoConflict();
      }
    }.assertNoTimeOut();

    sleep(1000);
    assertEquals(true, transaction2.hasConflict());
    assertEquals("OTTAWA", address2.getCity());
  }
}
