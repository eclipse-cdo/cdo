/*
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
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
@CleanRepositoriesBefore
public class Bugzilla_314264_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  @CleanRepositoriesBefore
  public void testMergeTest() throws Exception
  {
    // setup transaction.
    final CDOSession session = openSession();
    final CDOTransaction tr1 = session.openTransaction();
    tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final CDOResource resource = tr1.createResource(getResourcePath("/test1"));
    TaskContainer container = getModel2Factory().createTaskContainer();
    resource.getContents().add(container);

    // add at least 2 elements to avoid getting a clear when removing one.
    container.getTasks().add(getModel2Factory().createTask());
    container.getTasks().add(getModel2Factory().createTask());
    tr1.commit();

    // sleep(1000);

    final CDOBranch otherBranch = tr1.getBranch().createBranch("other");
    final CDOTransaction tr2 = session.openTransaction(otherBranch);

    TaskContainer otherContainer = tr2.getObject(container);
    assertNotNull(otherContainer);

    // add a new element on other branch at index 0.
    otherContainer.getTasks().add(0, getModel2Factory().createTask());

    // remove an element on main branch at index 0.
    container.getTasks().remove(0);

    commitAndSync(tr1, tr2);
    commitAndSync(tr2, tr1);

    // merge the other branch to main.
    tr1.merge(tr2.getBranch().getHead(), new DefaultCDOMerger.PerFeature.ManyValued());

    tr1.commit();
    assertEquals(false, tr1.isDirty());
  }

  public void testNotificationBuilderTest() throws Exception
  {
    // setup transaction.
    final CDOSession session = openSession();
    final CDOTransaction tr1 = session.openTransaction();
    tr1.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    final CDOResource resource = tr1.createResource(getResourcePath("/test1"));
    TaskContainer container = getModel2Factory().createTaskContainer();
    resource.getContents().add(container);
    tr1.commit();

    final BlockingResultContainer result = new BlockingResultContainer();

    // setup additional view.
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
          result.setResult(new Boolean(notification.getPosition() == 0));
        }

        counter++;
      }
    };

    TaskContainer containerObject = view.getObject(container);
    containerObject.eAdapters().add(adapter);

    // add elements at index 0 causing NotificationBuilder to patch indices beyond 0.
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
