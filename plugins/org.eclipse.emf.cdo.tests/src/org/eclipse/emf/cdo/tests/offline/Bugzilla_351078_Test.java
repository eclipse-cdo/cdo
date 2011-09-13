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
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

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
@CleanRepositoriesBefore
public class Bugzilla_351078_Test extends AbstractSyncingTest
{
  @Override
  protected boolean isRawReplication()
  {
    return true;
  }

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

  private void run(CompanyChanger changer) throws Exception
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

    // Create client session & transaction.
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOID id = CDOUtil.getCDOObject(masterCompany).cdoID();
    Company company = (Company)transaction.getObject(id);
    EList<Category> categories = company.getCategories();

    for (int i = 0; i < masterCompany.getCategories().size(); i++)
    {
      CDOObject masterCategory = CDOUtil.getCDOObject(masterCompany.getCategories().get(i));
      CDOObject category = CDOUtil.getCDOObject(categories.get(i));
      assertEquals(masterCategory.cdoID(), category.cdoID());
    }

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);
    sleep(500);

    changer.beforeReconnect(masterCompany.getCategories(), masterTransaction);

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);
    sleep(500);

    for (int i = 0; i < masterCompany.getCategories().size(); i++)
    {
      CDOObject masterCategory = CDOUtil.getCDOObject(masterCompany.getCategories().get(i));
      CDOObject category = CDOUtil.getCDOObject(categories.get(i));
      assertEquals("Element " + i, masterCategory.cdoID(), category.cdoID());
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class CompanyChanger
  {
    public void beforeConnect(EList<Category> categories, CDOTransaction transaction) throws Exception
    {
    }

    public void beforeReconnect(EList<Category> categories, CDOTransaction transaction) throws Exception
    {
    }
  }
}
