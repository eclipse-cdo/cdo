/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.WithIndex;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.TestAdapter;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.Iterator;
import java.util.List;

/**
 * Databinding & CDO Notifications.
 * <p>
 * See bug 316887
 * 
 * @author Cyril Jaquier
 * @since 4.0
 */
public class Bugzilla_316887_Test extends AbstractCDOTest
{
  /**
   * @deprecated AbstractObjectConflictResolver is deprecated
   */
  @Deprecated
  public void testResolveConflictWithAdjustedNotifcations() throws Exception
  {
    // setup transaction.
    final CDOSession session = openSession();
    final CDOTransaction tr1 = session.openTransaction();

    final CDOResource resource = tr1.createResource("/test1");
    TaskContainer container = getModel2Factory().createTaskContainer();
    resource.getContents().add(container);

    for (int i = 0; i < 7; i++)
    {
      Task task = getModel2Factory().createTask();
      task.setDescription(Integer.toString(i));
      container.getTasks().add(task);
    }

    tr1.commit();

    sleep(500);

    final CDOTransaction tr2 = session.openTransaction();

    // Adds a conflict resolver.
    tr2.options().addConflictResolver(new org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver()
    {
      @Deprecated
      @Override
      protected void resolveConflict(CDOObject conflict, CDORevision oldRemoteRevision, CDORevisionDelta localDelta,
          CDORevisionDelta remoteDelta, List<CDORevisionDelta> allRemoteDeltas)
      {
        CDOStateMachine.INSTANCE.rollback((InternalCDOObject)conflict);

        // Adjusts the local delta
        CDOListFeatureDelta list = (CDOListFeatureDelta)localDelta.getFeatureDeltas().get(0);
        CDORemoveFeatureDelta remove = (CDORemoveFeatureDelta)list.getListChanges().get(0);
        ((WithIndex)remove).adjustAfterRemoval(0);

        changeObject(conflict, localDelta);

        // Adjusts the "notifications"
        CDORevisionDelta delta = allRemoteDeltas.get(0);
        list = (CDOListFeatureDelta)delta.getFeatureDeltas().get(0);
        CDOMoveFeatureDelta move = (CDOMoveFeatureDelta)list.getListChanges().get(0);
        ((WithIndex)move).adjustAfterRemoval(0);
      }
    });

    TaskContainer otherContainer = tr2.getObject(container);
    assertNotNull(otherContainer);

    //
    TestAdapter adapter = new TestAdapter();
    otherContainer.eAdapters().add(adapter);

    // Move in transaction 1.
    container.getTasks().move(6, 0);

    // Remove in transaction 2.
    otherContainer.getTasks().remove(2);

    tr1.commit();

    sleep(500);

    assertEquals(2, adapter.getNotifications().length);
    assertEquals(Notification.REMOVE, adapter.getNotifications()[0].getEventType());
    assertEquals(Notification.MOVE, adapter.getNotifications()[1].getEventType());
    assertEquals(5, adapter.getNotifications()[1].getPosition());

    tr2.commit();

    sleep(500);

    // At this point, both transactions must have an similar list.
    assertEquals(container.getTasks().size(), otherContainer.getTasks().size());

    Iterator<Task> i1 = container.getTasks().iterator();
    Iterator<Task> i2 = otherContainer.getTasks().iterator();
    while (i1.hasNext())
    {
      Task task1 = i1.next();
      Task task2 = i2.next();
      assertEquals(task1.getDescription(), task2.getDescription());
    }
  }
}
