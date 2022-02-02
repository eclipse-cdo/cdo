/*
 * Copyright (c) 2010-2013, 2016, 2020, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * IndexOutOfBoundsException during merge.
 * <p>
 * See bug 314264
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_314264_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipStoreWithoutChangeSets();
  }

  public void testMerge() throws Exception
  {
    // Setup transaction.
    CDOSession session = openSession();
    CDOTransaction tr1 = session.openTransaction();
    tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource = tr1.getOrCreateResource(getResourcePath("/test"));
    TaskContainer container1 = getModel2Factory().createTaskContainer();
    resource.getContents().add(container1);

    // Add at least 2 elements to avoid getting a clear when removing one.
    container1.getTasks().add(getModel2Factory().createTask());
    container1.getTasks().add(getModel2Factory().createTask());
    tr1.commit();

    CDOBranch branch2 = tr1.getBranch().createBranch(getBranchName("branch-" + System.currentTimeMillis()));
    CDOTransaction tr2 = session.openTransaction(branch2);

    TaskContainer container2 = tr2.getObject(container1);
    assertNotNull(container2);

    // Add a new element on other branch at index 0.
    container2.getTasks().add(0, getModel2Factory().createTask());

    // Remove an element on main branch at index 0.
    container1.getTasks().remove(0);
    commitAndSync(tr1, tr2);
    commitAndSync(tr2, tr1);

    // Merge the other branch to main.
    tr1.merge(tr2.getBranch(), new DefaultCDOMerger.PerFeature.ManyValued());

    tr1.commit();
    assertEquals(false, tr1.isDirty());
    session.close();
  }

  public void testMerge10() throws Exception
  {
    // Try again after some warm up. See bug 383602.
    testMerge();
    testMerge();
    testMerge();
    testMerge();
  }

  public void testNotificationBuilderTest() throws Exception
  {
    // Setup transaction.
    final CDOSession session = openSession();
    final CDOTransaction tr1 = session.openTransaction();
    tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final CDOResource resource = tr1.createResource(getResourcePath("/test1"));
    TaskContainer container = getModel2Factory().createTaskContainer();
    resource.getContents().add(container);
    tr1.commit();

    final BlockingResultContainer result = new BlockingResultContainer();

    // Setup additional view.
    CDOView view = session.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    TestAdapter adapter = new TestAdapter()
    {
      private int counter;

      @Override
      public void notifyChanged(Notification notification)
      {
        if (counter != 0)
        {
          result.setResult(Boolean.valueOf(notification.getPosition() == 0));
        }

        counter++;
      }
    };

    TaskContainer containerObject = view.getObject(container);
    containerObject.eAdapters().add(adapter);

    // Add elements at index 0 causing NotificationBuilder to patch indices beyond 0.
    container.getTasks().add(0, getModel2Factory().createTask());
    container.getTasks().add(0, getModel2Factory().createTask());
    tr1.commit();

    Boolean indexWasCorrect = (Boolean)result.getResult();
    assertEquals(true, indexWasCorrect != null && indexWasCorrect.booleanValue());
  }

  /**
   * @author Eike Stepper
   */
  private static class BlockingResultContainer
  {
    private Object result;

    public synchronized Object getResult() throws Exception
    {
      while (result == null)
      {
        wait(5000);
      }

      return result;
    }

    public synchronized void setResult(Object result)
    {
      this.result = result;
      notifyAll();
    }
  }
}
