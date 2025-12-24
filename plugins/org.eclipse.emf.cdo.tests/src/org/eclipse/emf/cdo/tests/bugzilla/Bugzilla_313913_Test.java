/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.ArrayList;
import java.util.List;

/**
 * IndexOutOfBoundsException on View Invalidation.
 * <p>
 * See bug 313913
 */
public class Bugzilla_313913_Test extends AbstractCDOTest
{
  public void testAccessOldValue() throws Exception
  {
    final CDOSession session = openSession();
    final CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    TaskContainer container = getModel2Factory().createTaskContainer();

    for (int i = 0; i < 2; i++)
    {
      Task task = getModel2Factory().createTask();
      container.getTasks().add(task);
    }

    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(container);
    transaction.commit();

    CDOView view = session.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    view.getObject(container).eAdapters().add(new TestAdapter());

    CDOView view2 = session.openView();
    view2.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    view2.getObject(container).eAdapters().add(new TestAdapter());

    CDOView view3 = session.openView();
    view3.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    view3.getObject(container).eAdapters().add(new TestAdapter());

    List<Task> tasks = new ArrayList<>(container.getTasks());
    container.getTasks().removeAll(tasks);

    container.getTasks().add(getModel2Factory().createTask());
    transaction.commit();

    sleep(100);

    System.out.println(container.getTasks().toString());
    System.out.println(view.getObject(container).getTasks().toString());
    // this will fail...
    System.out.println(view2.getObject(container).getTasks().toString());

  }
}
