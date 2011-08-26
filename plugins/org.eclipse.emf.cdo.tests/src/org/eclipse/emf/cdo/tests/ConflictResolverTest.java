/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * @author Simon McDuff
 */
@CleanRepositoriesBefore
public class ConflictResolverTest extends AbstractCDOTest
{
  public void testMergeLocalChangesPerFeature_Basic() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();

    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);

    address2.setCity("OTTAWA");

    address.setName("NAME1");

    // Resolver should be triggered.
    commitAndSync(transaction, transaction2);

    assertEquals(false, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  // Does not work in legacy as long as there is not getter interception
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testMergeLocalChangesPerFeature_BasicException() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();

    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    final Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);

    address2.setCity("OTTAWA");

    address.setCity("NAME1");
    commitAndSync(transaction, transaction2);

    assertEquals(true, transaction2.hasConflict());
    assertEquals(true, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals("OTTAWA", address2.getCity());
  }

  public void testCDOMergingConflictResolver() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());

    Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);
    address2.setCity("OTTAWA");

    address.setName("NAME1");

    // Resolver should be triggered.
    commitAndSync(transaction, transaction2);

    assertEquals(false, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  @SuppressWarnings("deprecation")
  protected CDOConflictResolver createConflictResolver()
  {
    return new org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver.MergeLocalChangesPerFeature();
  }
}
