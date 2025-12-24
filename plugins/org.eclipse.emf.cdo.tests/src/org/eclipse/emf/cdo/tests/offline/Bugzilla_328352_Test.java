/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Pascal Lehmann - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import java.text.MessageFormat;

/**
 * CommitNotifications overtaking each other.
 * <p>
 * See bug 328352
 *
 * @author Pascal Lehmann
 * @since 4.0
 */
public class Bugzilla_328352_Test extends AbstractSyncingTest
{
  private final int NUM_PRODUCTS = 200;

  private final int NUM_CLIENT_VIEWS = 10;

  @Requires(IRepositoryConfig.CAPABILITY_OFFLINE)
  @Skips("DB.ranges")
  // Too slow in DB.ranges (11 minutes), see bug 357441
  public void testOfflineCloneSynchronization() throws Exception
  {
    disableConsole();

    // create an offline clone.
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    // create master session & transaction.
    InternalRepository master = getRepository("master");
    CDOSession masterSession = openSession(master.getName());
    CDOTransaction masterTransaction = masterSession.openTransaction();

    // create client session & transaction.
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // doing this that client notifications are built upon RevisionDeltas instead of RevisionKeys.
    session.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    // create additional client sessions.
    CDOView[] cloneViews = new CDOView[NUM_CLIENT_VIEWS + 1];
    for (int i = 0; i < NUM_CLIENT_VIEWS; i++)
    {
      CDOView view = session.openView();
      cloneViews[i] = view;
    }

    cloneViews[NUM_CLIENT_VIEWS] = transaction;

    // create resource and base model.
    CDOResource resource = masterTransaction.createResource(getResourcePath("/my/resource"));
    Company company = getModel1Factory().createCompany();
    Category catA = getModel1Factory().createCategory();
    catA.setName("CatA");
    company.getCategories().add(catA);
    Category catB = getModel1Factory().createCategory();
    catB.setName("CatB");
    company.getCategories().add(catB);
    resource.getContents().add(company);

    for (int i = 0; i < NUM_PRODUCTS; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Product" + i);
      catA.getProducts().add(product);
    }

    masterTransaction.commit();
    transaction.waitForUpdate(masterTransaction.getLastCommitTime(), 1000);

    // touch the objects on the views to actually receive updates.
    for (CDOView view : cloneViews)
    {
      Category vCatA = (Category)view.getObject(CDOUtil.getCDOObject(catA).cdoID());
      Category vCatB = (Category)view.getObject(CDOUtil.getCDOObject(catB).cdoID());
      vCatB.getName();
      for (Product1 vProduct : vCatA.getProducts())
      {
        vProduct.getName();
      }
    }

    // do a lot of changes on master session.
    long start = System.currentTimeMillis();
    for (int i = 0; i < NUM_PRODUCTS; i++)
    {
      Product1 p = catA.getProducts().remove(0);
      catB.getProducts().add(p);
      catB.getProducts().move(0, p);

      masterTransaction.commit();
    }

    Thread.sleep(100);
    catA.setName(catA.getName() + " empty");
    masterTransaction.commit();

    System.out.println(MessageFormat.format("## Committing changes on {0} products took: {1}", NUM_PRODUCTS, System.currentTimeMillis() - start));

    // session.waitForUpdate(masterTransaction.getLastCommitTime(), 5000);
    for (CDOView view : cloneViews)
    {
      view.waitForUpdate(masterTransaction.getLastCommitTime(), 5000);
    }

    // adding this sleep as the waitForUpdate does not seem to work as expected in case of an error.
    sleep(5000);
    System.out.println("## Started checking....");

    // check if all changes are made.
    Category cloneCatA = (Category)transaction.getObject(CDOUtil.getCDOObject(catA).cdoID());
    Category cloneCatB = (Category)transaction.getObject(CDOUtil.getCDOObject(catB).cdoID());
    System.out.println("CatA IdVersion: " + CDOUtil.getCDOObject(cloneCatA).cdoRevision().toString());
    System.out.println("CatB IdVersion: " + CDOUtil.getCDOObject(cloneCatB).cdoRevision().toString());
    assertEquals(NUM_PRODUCTS, cloneCatB.getProducts().size());
    assertEquals(0, cloneCatA.getProducts().size());
    assertEquals(catA.getName(), cloneCatA.getName());
  }

  @Override
  public void disableConsole()
  {
    OMPlatform.INSTANCE.setDebugging(false);
    OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
    // OMPlatform.INSTANCE.removeLogHandler(PrintLogHandler.CONSOLE);
  }
}
