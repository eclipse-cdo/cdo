/*
 * Copyright (c) 2012, 2014, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Ronald Krijgsheld - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOInvalidationPolicy;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bug 368223.
 *
 * @author Ronald Krijgsheld
 */
public class Bugzilla_368223_Test extends AbstractCDOTest
{
  @Requires({ IRepositoryConfig.CAPABILITY_BRANCHING, "MEM" })
  public void testRules1() throws Throwable
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setInvalidationPolicy(CDOInvalidationPolicy.STRICT);
    transaction.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);
    transaction.createResource(getResourcePath("/test1"));
    transaction.commit();

    AtomicReference<Throwable> exception = new AtomicReference<>();
    long start = System.currentTimeMillis();

    Creator creator = new Creator(exception);
    creator.start();

    Loader loader = new Loader(exception);
    loader.start();

    creator.join();
    loader.join();

    System.out.println(System.currentTimeMillis() - start);

    Throwable ex = exception.get();
    if (ex != null && !(ex instanceof Success))
    {
      throw exception.get();
    }
  }

  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    super.doTearDown();
  }

  /**
   * @author Eike Stepper
   */
  private static final class Success extends Exception
  {
    private static final long serialVersionUID = 1L;
  }

  /**
   * @author Eike Stepper
   */
  private abstract class Actor extends Thread
  {
    private final AtomicReference<Throwable> exception;

    protected Actor(AtomicReference<Throwable> exception)
    {
      this.exception = exception;
    }

    @Override
    public final void run()
    {
      try
      {
        runSafe(exception);
      }
      catch (Throwable ex)
      {
        exception.compareAndSet(null, ex);
      }
    }

    protected abstract void runSafe(AtomicReference<Throwable> exception) throws Exception;
  }

  /**
   * @author Ronald Krijgsheld
   */
  private final class Creator extends Actor
  {
    public Creator(AtomicReference<Throwable> exception)
    {
      super(exception);
    }

    @Override
    protected void runSafe(AtomicReference<Throwable> exception) throws Exception
    {
      CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      transaction.options().setInvalidationPolicy(CDOInvalidationPolicy.STRICT);
      transaction.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);

      final CDOResource resource = transaction.getResource(getResourcePath("/test1"));
      final List<Company> listOfCompanies = new ArrayList<>();

      int loop = 5;
      while (exception.get() == null && --loop != 0)
      {
        transaction.sync().call(() -> {
          for (int i = 0; i < 20; i++)
          {
            Company company = createCompanyWithCategories(resource);
            listOfCompanies.add(company);
          }

          transaction.commit();

          while (!listOfCompanies.isEmpty())
          {
            Company company = listOfCompanies.remove(0);

            EList<Category> categories = company.getCategories();
            while (!categories.isEmpty())
            {
              categories.remove(0);
              transaction.commit();
            }

            resource.getContents().remove(company);
            transaction.commit();
          }

          return null;
        });
      }

      throw new Success();
    }

    private Company createCompanyWithCategories(CDOResource resource)
    {
      Company company = getModel1Factory().createCompany();
      EList<Category> categories = company.getCategories();

      for (int i = 0; i < 5; i++)
      {
        Category category = getModel1Factory().createCategory();
        categories.add(category);
      }

      resource.getContents().add(company);
      return company;
    }
  }

  /**
   * @author Ronald Krijgsheld
   */
  private final class Loader extends Actor
  {
    public Loader(AtomicReference<Throwable> exception)
    {
      super(exception);
    }

    @Override
    protected void runSafe(AtomicReference<Throwable> exception) throws Exception
    {
      getTestProperties().put(SessionConfig.PROP_TEST_FETCH_RULE_MANAGER, CDOUtil.createThreadLocalFetchRuleManager());
      CDOSession session = openSession();

      CDOTransaction transaction = session.openTransaction();
      transaction.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);
      transaction.options().setInvalidationPolicy(CDOInvalidationPolicy.STRICT);
      transaction.options().setFeatureAnalyzer(CDOUtil.createModelBasedFeatureAnalyzer());

      final CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      while (exception.get() == null)
      {
        try
        {
          transaction.sync().call(() -> {
            for (EObject object : resource.getContents())
            {
              Company company = (Company)object;
              EList<Category> categories = company.getCategories();
              if (categories.size() > 0)
              {
                Category category = categories.get(0);
                msg(category);
              }
            }

            return null;
          });
        }
        catch (InvalidObjectException ex)
        {
          System.err.println(ex.getMessage());
          continue;
        }
        catch (ObjectNotFoundException ex)
        {
          System.err.println(ex.getMessage());
          continue;
        }
      }

      throw new Success();
    }
  }
}
