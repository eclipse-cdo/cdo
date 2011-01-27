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
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * @author Simon McDuff
 */
public class ConflictResolverTest extends AbstractCDOTest
{
  @SuppressWarnings("deprecation")
  public void testMergeLocalChangesPerFeature_Basic() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();

    transaction.getOrCreateResource("/res1").getContents().add(address);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(
        new org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver.MergeLocalChangesPerFeature());
    Address address2 = (Address)transaction2.getOrCreateResource("/res1").getContents().get(0);

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

  public void testMergeLocalChangesPerFeature_Bug1() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    EList<EObject> contents1 = transaction1.getOrCreateResource("/res1").getContents();

    contents1.add(getModel1Factory().createAddress());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(new CDOMergingConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource("/res1").getContents();

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);
  }

  public void testMergeLocalChangesPerFeature_Bug2() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(new CDOMergingConflictResolver());
    EList<EObject> contents1 = transaction1.getOrCreateResource("/res1").getContents();

    contents1.add(getModel1Factory().createAddress());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(new CDOMergingConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource("/res1").getContents();

    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction2, transaction1);
    commitAndSync(transaction1, transaction2);
  }

  @SuppressWarnings("deprecation")
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
    transaction2.options().addConflictResolver(
        new org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver.MergeLocalChangesPerFeature());
    final Address address2 = (Address)transaction2.getOrCreateResource("/res1").getContents().get(0);

    address2.setCity("OTTAWA");

    address.setCity("NAME1");
    commitAndSync(transaction, transaction2);

    assertEquals(true, transaction2.hasConflict());
    assertEquals(true, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals("OTTAWA", address2.getCity());
  }

  /**
   * TODO Why do I fail only on Hudson???
   */
  public void testCDOMergingConflictResolver() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();
    transaction.getOrCreateResource("/res1").getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(new CDOMergingConflictResolver());

    Address address2 = (Address)transaction2.getOrCreateResource("/res1").getContents().get(0);
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
}
