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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.collection.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_336590_Test extends AbstractCDOTest
{
  private CDOID removedID;

  public void test() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction tx = session.openTransaction();
    tx.options().addConflictResolver(new TestResolver());

    CDOResource resource = tx.createResource(getResourcePath("test"));
    Model1Factory f = getModel1Factory();
    SalesOrder order = f.createSalesOrder();
    for (int i = 0; i < 3; i++)
    {
      order.getOrderDetails().add(f.createOrderDetail());
    }

    resource.getContents().add(order);
    tx.commit();

    for (int i = 0; i < 3; i++)
    {
      order.getOrderDetails().add(f.createOrderDetail());
    }

    OrderDetail newDetail = f.createOrderDetail();
    order.getOrderDetails().add(newDetail);
    System.out.println("---> New object: " + newDetail);

    doRemovalInOtherSession();
    session.refresh();

    session.close();
  }

  private void doRemovalInOtherSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath("test"));

    SalesOrder order = (SalesOrder)resource.getContents().get(0);
    OrderDetail removedDetail = order.getOrderDetails().get(0);
    removedID = CDOUtil.getCDOObject(removedDetail).cdoID();
    order.getOrderDetails().remove(0);

    tx.commit();
    session.close();
  }

  /**
   * @author Caspar De Groot
   */
  private class TestResolver implements CDOConflictResolver2
  {
    private CDOTransaction tx;

    @Override
    public CDOTransaction getTransaction()
    {
      return tx;
    }

    @Override
    public void setTransaction(CDOTransaction transaction)
    {
      tx = transaction;
    }

    @Override
    public void resolveConflicts(Set<CDOObject> conflicts)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void resolveConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts, List<CDORevisionDelta> allRemoteDeltas)
    {
      assertEquals(1, allRemoteDeltas.size());

      List<CDOFeatureDelta> fDeltas = allRemoteDeltas.get(0).getFeatureDeltas();
      assertEquals(1, fDeltas.size());

      CDOFeatureDelta fDelta = fDeltas.get(0);
      assertEquals(true, fDelta instanceof CDOListFeatureDelta);

      List<CDOFeatureDelta> listDeltas = ((CDOListFeatureDelta)fDelta).getListChanges();
      for (CDOFeatureDelta lDelta : listDeltas)
      {
        System.out.println("---> listDelta: " + lDelta);
      }

      // Should find only 1 delta
      assertEquals(1, listDeltas.size());

      // And it should be a remove delta
      CDOFeatureDelta lDelta = listDeltas.get(0);
      assertEquals(true, lDelta instanceof CDORemoveFeatureDelta);

      // And its value should be a CDOID
      CDORemoveFeatureDelta removeDelta = (CDORemoveFeatureDelta)lDelta;
      Object removedValue = removeDelta.getValue();
      assertEquals(true, removedValue instanceof CDOID);

      // And that CDOID should match the CDOID we kept when removing the
      // object in the 2nd session
      assertEquals(removedID.toString(), removedValue.toString());
    }
  }
}
