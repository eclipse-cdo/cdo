/*
 * Copyright (c) 2010-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 * @since 4.0
 */
public class Bugzilla_318518_Test extends AbstractCDOTest
{
  private List<Exception> exceptions = new ArrayList<>();

  /**
   * @deprecated AbstractObjectConflictResolver is deprecated
   */
  @Deprecated
  public void testCorrectNumberOfFeatureDeltas() throws Exception
  {
    // setup transaction.
    final CDOSession session = openSession();
    final CDOTransaction tr1 = session.openTransaction();

    final CDOResource resource = tr1.createResource(getResourcePath("/test1"));
    TaskContainer container = getModel2Factory().createTaskContainer();
    resource.getContents().add(container);

    for (int i = 0; i < 2; i++)
    {
      Task task = getModel2Factory().createTask();
      task.setDescription(Integer.toString(i));
      container.getTasks().add(task);
    }
    tr1.commit();

    sleep(500);

    final CDOTransaction tr2 = session.openTransaction();

    tr2.options().addConflictResolver(new org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver()
    {
      @Override
      protected void resolveConflict(CDOObject conflict, CDORevision oldRevision, CDORevisionDelta localDelta, CDORevisionDelta remoteDelta,
          List<CDORevisionDelta> deltas)
      {
        CDOListFeatureDelta list = (CDOListFeatureDelta)localDelta.getFeatureDeltas().get(0);

        int size = list.getListChanges().size();
        if (size != 1)
        {
          exceptions.add(new Exception("Size of list changes should be 1 but is " + size));
          return;
        }
      }
    });

    TaskContainer otherContainer = tr2.getObject(container);
    assertNotNull(otherContainer);

    // Just do something and commit
    container.getTasks().move(1, 0);

    // Remove in transaction 2.
    otherContainer.getTasks().remove(1);

    tr1.commit();

    sleep(1000);

    if (exceptions.size() > 0)
    {
      throw exceptions.get(0);
    }
  }
}
