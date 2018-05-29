/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Martin Taal
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class HibernateBugzilla_395684_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
    skipStoreWithoutRawAccess();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    super.doTearDown();
  }

  public void testVersionChange() throws Exception
  {
    CDOSession session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("Martin");
      resource.getContents().add(customer);
      transaction.commit();
    }
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      Customer customer = (Customer)resource.getContents().get(0);
      customer.setName("Eike");
      transaction.commit();
    }
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      resource.getContents().remove(0);
      transaction.commit();
    }

    getRepository().getRevisionManager().getCache().clear();

    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      assertEquals(0, resource.getContents().size());
      transaction.commit();
    }

    {
      CDOView view = session.openView();

      int i = 0;
      for (CDOCommitInfo cdoCommitInfo : getCDOCommitHistory(view).getElements())
      {
        if (i == 0)
        {
          assertEquals(1, cdoCommitInfo.getDetachedObjects().size());
          assertEquals(1, cdoCommitInfo.getChangedObjects().size());
          assertEquals(0, cdoCommitInfo.getNewObjects().size());
          CDOIDAndVersion cdoIDAndVersion = cdoCommitInfo.getDetachedObjects().get(0);
          // get the view before the delete
          CDOView otherView = session.openView(cdoCommitInfo.getTimeStamp() - 1);
          CDOObject obj = otherView.getObject(cdoIDAndVersion.getID());
          {
            final Customer oldCustomer = (Customer)CDOUtil.getEObject(obj);
            assertEquals("Eike", oldCustomer.getName());
          }
          {
            final CDORevision cdoRevision = CDOUtil.getRevisionByVersion(obj, cdoIDAndVersion.getVersion());
            // get the detached entry
            final CDORevision cdoRevisionDetached = CDOUtil.getRevisionByVersion(obj, 1 + cdoIDAndVersion.getVersion());
            assertEquals(cdoRevision.getRevised() + 1, cdoRevisionDetached.getTimeStamp());
          }

        }
        else if (i == 1)
        {
          assertEquals(0, cdoCommitInfo.getDetachedObjects().size());
          assertEquals(1, cdoCommitInfo.getChangedObjects().size());
          assertEquals(0, cdoCommitInfo.getNewObjects().size());
        }
        else
        {
          break;
        }
        i++;
      }
      view.close();
    }
  }

  private synchronized CDOCommitHistory getCDOCommitHistory(CDOView audit)
  {
    CDOCommitHistory history = audit.getHistory();
    history.triggerLoad();
    long startTime = System.currentTimeMillis();
    while (history.isLoading())
    {
      ConcurrencyUtil.sleep(10);

      // waited too long
      if (System.currentTimeMillis() - startTime > 5000)
      {
        throw new IllegalStateException("commit info could not be loaded");
      }
    }
    return history;
  }
}
