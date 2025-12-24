/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.emf.common.util.EList;

/**
 * Support raw replication in HorizontalBranchingMappingStrategyWithRanges.
 * <p>
 * See bug 351078
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_OFFLINE)
public class Bugzilla_351078_Test extends AbstractSyncingTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    OMPlatform.INSTANCE.setDebugging(false);
    OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
    super.doSetUp();
  }

  public void testDoNothing() throws Exception
  {
    run(new CompanyChanger());
  }

  public void testAddTwoRemoveFirst_DoNothing() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.remove(0);
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddTwoRemoveFirst_AddTwo() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.remove(0);
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddTwoRemoveSecond_DoNothing() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.remove(1);
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddTwoRemoveSecond_AddTwo() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.remove(1);
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
      }
    });
  }

  public void testAddTwo_RemoveFirst() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.remove(0);
        transaction.commit();
      }
    });
  }

  public void testAddTwo_RemoveFirstAddOne() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.remove(0);
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddTwo_RemoveSecond() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.remove(1);
        transaction.commit();
      }
    });
  }

  public void testAddTwo_RemoveSecondAddOne() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.remove(1);
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddTwo_AddOneRemoveThird() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.remove(2);
        transaction.commit();
      }
    });
  }

  public void testAddOne_AddOne() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddTwo_AddTwo() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddThree_MoveFirst() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.move(5, 0);
        transaction.commit();
      }
    });
  }

  public void testAddThree_MoveLast() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.move(0, 5);
        transaction.commit();
      }
    });
  }

  public void testAddOne_ReplaceIt() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddOneAddOne_ReplaceFirst() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddOneRemoveIt() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.remove(0);
        transaction.commit();
      }
    });
  }

  public void testAddOneAddOne_RemoveFirst() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.remove(0);
        transaction.commit();
      }
    });
  }

  public void testAddOneReplaceFirst_ReplaceFirst() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddOneAddOneReplaceFirst_ReplaceFirst() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  public void testAddAddReplaceAddRmove_ReplaceAdd() throws Exception
  {
    run(new CompanyChanger()
    {
      @Override
      public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();

        categories.add(getModel1Factory().createCategory());
        transaction.commit();

        categories.remove(0);
        transaction.commit();
      }

      @Override
      public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
      {
        categories.set(0, getModel1Factory().createCategory());
        transaction.commit();

        categories.add(0, getModel1Factory().createCategory());
        transaction.commit();
      }
    });
  }

  protected void run(CompanyChanger changer) throws Exception
  {
    // Create master session & transaction.
    InternalRepository master = getRepository("master");
    CDOSession masterSession = openSession(master.getName());
    CDOTransaction masterTransaction = masterSession.openTransaction();

    // Create resource and base model.
    CDOResource resource = masterTransaction.createResource(getResourcePath("/my/resource"));
    Company masterCompany = getModel1Factory().createCompany();
    resource.getContents().add(masterCompany);
    masterTransaction.commit();

    changer.beforeConnect(masterCompany.getCategories(), masterTransaction);

    // Create an offline clone.
    InternalRepository clone = getRepository();
    waitForOnline(clone);
    sleep(500); // TODO Clarify why waitForOnline() alone is not enough

    check(master, masterCompany, "after connect");

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);
    sleep(500);

    changer.beforeReconnect(masterCompany.getCategories(), masterTransaction);

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);
    sleep(500);

    check(master, masterCompany, "after reconnect");
  }

  protected void check(InternalRepository master, Company masterCompany, String when)
  {
    EList<Category> masterCategories = masterCompany.getCategories();

    CDOSession session = openSession();
    CDOView view = session.openView();

    CDOID id = CDOUtil.getCDOObject(masterCompany).cdoID();
    Company company = (Company)view.getObject(id);
    EList<Category> categories = company.getCategories();

    assertEquals("Size (" + when + ")", masterCategories.size(), categories.size());

    for (int i = 0; i < masterCategories.size(); i++)
    {
      CDOObject masterCategory = CDOUtil.getCDOObject(masterCategories.get(i));
      CDOObject category = CDOUtil.getCDOObject(categories.get(i));
      assertEquals("Element " + i + " (" + when + ")", masterCategory.cdoID(), category.cdoID());
    }

    session.close();
  }

  /**
   * @author Eike Stepper
   */
  protected static class CompanyChanger
  {
    public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
    {
    }

    public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
    {
    }
  }
}
