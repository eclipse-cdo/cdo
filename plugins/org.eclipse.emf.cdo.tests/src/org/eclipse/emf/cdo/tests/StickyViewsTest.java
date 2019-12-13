/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.common.util.EList;

/**
 * @author Caspar De Groot
 */
// Sticky behavior is only enabled when store supports auditing, so these tests are
// meaningless and/or guaranteed to fail if this is not the case
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class StickyViewsTest extends AbstractCDOTest
{
  private static final int N_CATEGORIES = 3;

  public void test_removeLast() throws CommitException
  {
    test(new AbstractClosure()
    {
      @Override
      public void doChange(EList<Category> categories)
      {
        categories.remove(N_CATEGORIES - 1);
      }
    });
  }

  public void test_removeFirst() throws CommitException
  {
    test(new AbstractClosure()
    {
      @Override
      public void doChange(EList<Category> categories)
      {
        categories.remove(0);
      }
    });
  }

  public void test_changeName() throws CommitException
  {
    test(new AbstractClosure()
    {
      @Override
      public void doChange(EList<Category> categories)
      {
        categories.get(0).setName("zzz");
      }

      @Override
      public void verify(EList<Category> categories)
      {
        assertEquals("category0", categories.get(0).getName());
      }
    });
  }

  private void test(Closure closure) throws CommitException
  {
    // Create a company with N categories
    Company company1 = getModel1Factory().createCompany();
    company1.setName("company");
    for (int i = 0; i < N_CATEGORIES; i++)
    {
      Category category = getModel1Factory().createCategory();
      company1.getCategories().add(category);
      category.setName("category" + i);
    }

    // Persist it
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    CDOResource res = transaction1.createResource(getResourcePath("/res1"));
    res.getContents().add(company1);
    transaction1.commit();

    assertEquals(N_CATEGORIES, company1.getCategories().size());

    // Fetch the same company in another session/tx
    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource res2 = transaction2.getResource(getResourcePath("/res1"));
    Company company2 = (Company)res2.getContents().get(0);

    // In the 1st session, manipulate the categories
    closure.doChange(company1.getCategories());
    transaction1.commit();

    // Clear server-side cache to force loading of revisions from IStore
    IRepository repo = getRepositoryConfig().getRepository(IRepositoryConfig.REPOSITORY_NAME);
    ((InternalCDORevisionManager)repo.getRevisionManager()).getCache().clear();

    // Verify that in 2nd session, all categories still appear to be present
    closure.verify(company2.getCategories());

    msg("Done");

    transaction1.close();
    session.close();

    session2.close();
    transaction2.close();
  }

  /**
   * Ensures that a newly committed object in this tx, can be reloaded from the server.
   */
  public void test_newCommittedObject() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction tx = session.openTransaction();
    CDOResource res = tx.createResource(getResourcePath("/res1"));
    Company company1 = getModel1Factory().createCompany();
    res.getContents().add(company1);
    tx.commit();

    // Make dirty, then roll back, so as to force PROXY state
    //
    company1.setName("company1");
    tx.rollback();

    // Clear cache to force loading of revisions from server
    //
    ((InternalCDORevisionManager)session.getRevisionManager()).getCache().clear();

    // Clear server-side cache to force loading of revisions from IStore
    //
    IRepository repo = getRepositoryConfig().getRepository(IRepositoryConfig.REPOSITORY_NAME);
    ((InternalCDORevisionManager)repo.getRevisionManager()).getCache().clear();

    assertClean(CDOUtil.getCDOObject(company1), tx);

    msg(company1.getName());

    CDOID id = CDOUtil.getCDOObject(company1).cdoID();
    company1 = null;

    CDOObject obj = tx.getObject(id);
    assertNotNull(obj);

    session.close();
  }

  /**
   * Ensures that an object that was updated and committed in this tx, can be reloaded in its *updated* state from the
   * server.
   */
  public void test_dirtyCommittedObject() throws CommitException
  {
    // Put a company in the repo
    {
      CDOSession sess = openSession();
      CDOTransaction tx = sess.openTransaction();
      CDOResource res = tx.createResource(getResourcePath("/res1"));
      Company company1 = getModel1Factory().createCompany();
      company1.setName("aaa");
      res.getContents().add(company1);
      tx.commit();
      sess.close();
    }

    // Load it up in a different, sticky session
    {
      CDOSession sess = openSession();
      CDOTransaction tx = sess.openTransaction();
      sess.options().setPassiveUpdateEnabled(false);
      CDOResource res = tx.getResource(getResourcePath("/res1"));

      // Save with a new name
      Company company1 = (Company)res.getContents().get(0);
      company1.setName("bbb");
      tx.commit();

      // Make dirty then roll back, so as to force PROXY state
      company1.setName("ccc");
      tx.rollback();

      assertClean(CDOUtil.getCDOObject(company1), tx);

      // Clear cache to force loading of revisions from server
      ((InternalCDORevisionManager)sess.getRevisionManager()).getCache().clear();

      // Clear server-side cache to force loading of revisions from IStore
      IRepository repo = getRepositoryConfig().getRepository(IRepositoryConfig.REPOSITORY_NAME);
      ((InternalCDORevisionManager)repo.getRevisionManager()).getCache().clear();

      String name = company1.getName();
      assertEquals("bbb", name);
      sess.close();
    }
  }

  /**
   * Ensures that an object that was removed in this tx, can no longer be loaded (even though the sticky time is still
   * set to a time when the object still existed!)
   */
  public void test_detachedCommittedObject() throws CommitException
  {
    // Put a company in the repo
    {
      CDOSession sess = openSession();
      CDOTransaction tx = sess.openTransaction();
      CDOResource res = tx.createResource(getResourcePath("/res1"));
      Company company1 = getModel1Factory().createCompany();
      company1.setName("aaa");
      res.getContents().add(company1);
      tx.commit();
      sess.close();
    }

    // Load it up in a different, sticky session
    {
      CDOSession sess = openSession();
      CDOTransaction tx = sess.openTransaction();
      sess.options().setPassiveUpdateEnabled(false);
      CDOResource res = tx.getResource(getResourcePath("/res1"));

      // Remove it
      Company company1 = (Company)res.getContents().get(0);
      CDOID companyID = CDOUtil.getCDOObject(company1).cdoID();
      res.getContents().remove(company1);
      tx.commit();

      // Clear cache to force loading of revisions from server
      //
      ((InternalCDORevisionManager)sess.getRevisionManager()).getCache().clear();

      // Clear server-side cache to force loading of revisions from IStore
      //
      IRepository repo = getRepositoryConfig().getRepository(IRepositoryConfig.REPOSITORY_NAME);
      ((InternalCDORevisionManager)repo.getRevisionManager()).getCache().clear();

      try
      {
        tx.getObject(companyID);
        fail("Should have thrown " + ObjectNotFoundException.class.getSimpleName());
      }
      catch (ObjectNotFoundException e)
      {
        // Good
      }

      sess.close();
    }
  }

  public void test_refresh() throws CommitException
  {
    CDOSession sess = openSession();
    CDOTransaction tx = sess.openTransaction();
    sess.options().setPassiveUpdateEnabled(false);

    CDOResource res = tx.createResource(getResourcePath("/res1"));
    Company company1 = getModel1Factory().createCompany();
    company1.setName("aaa");
    res.getContents().add(company1);
    tx.commit(); // Puts the company in committedSinceLastRefresh

    sess.refresh(); // Removes the company from committedSinceLastRefresh

    // Make dirty then rollback to force proxy
    company1.setName("bbb");
    tx.rollback();

    // Clear cache to force loading of revisions from server
    //
    ((InternalCDORevisionManager)sess.getRevisionManager()).getCache().clear();

    doOtherSession(); // Creates a new revision on the server

    assertClean(CDOUtil.getCDOObject(company1), tx);
    assertEquals("The company name should not have the value set in the other session", "aaa", company1.getName());

    sess.close();
  }

  public void test_otherSessionCommittedLatest() throws CommitException
  {
    CDOSession sess = openSession();
    CDOTransaction tx = sess.openTransaction();
    sess.options().setPassiveUpdateEnabled(false);

    CDOResource res = tx.createResource(getResourcePath("/res1"));
    Company company1 = getModel1Factory().createCompany();
    company1.setName("aaa");
    res.getContents().add(company1);
    tx.commit(); // Puts the company in committedSinceLastRefresh

    // Make dirty then rollback to force proxy
    company1.setName("bbb");
    tx.rollback();

    // Clear cache to force loading of revisions from server
    ((InternalCDORevisionManager)sess.getRevisionManager()).getCache().clear();

    doOtherSession(); // Creates a new revision on the server

    assertClean(CDOUtil.getCDOObject(company1), tx);

    // Verify that this session still fetches the revision that *this* session just committed,
    // rather than the latest revision, which was committed by the other session
    assertEquals("aaa", company1.getName());

    sess.close();
  }

  private void doOtherSession() throws CommitException
  {
    CDOSession sess = openSession();
    CDOTransaction tx = sess.openTransaction();
    CDOResource res = tx.getResource(getResourcePath("/res1"));
    Company company1 = (Company)res.getContents().get(0);
    company1.setName("ccc");
    tx.commit();
    tx.close();
    sess.close();
  }

  /**
   * @author Caspar De Groot
   */
  private interface Closure
  {
    public void doChange(EList<Category> categories);

    public void verify(EList<Category> categories);
  }

  /**
   * @author Caspar De Groot
   */
  private abstract class AbstractClosure implements Closure
  {
    @Override
    public void verify(EList<Category> categories)
    {
      assertEquals(N_CATEGORIES, categories.size());

      // Now fetch each category in the 2nd session
      for (int i = 0; i < N_CATEGORIES; i++)
      {
        msg("Getting index " + i);
        assertEquals(N_CATEGORIES, categories.size());

        try
        {
          Category cat = categories.get(i);
          assertNotNull(cat);
        }
        catch (ObjectNotFoundException e)
        {
          fail("Should not have thrown " + ObjectNotFoundException.class.getName());
        }
      }
    }
  }
}
