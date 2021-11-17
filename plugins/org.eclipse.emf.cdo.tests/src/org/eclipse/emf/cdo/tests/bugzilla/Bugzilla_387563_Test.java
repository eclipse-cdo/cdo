/*
 * Copyright (c) 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Collections;
import java.util.List;

/**
 * @author Esteban Dugueperoux
 */
public class Bugzilla_387563_Test extends AbstractCDOTest
{
  private CDOTransaction transaction;

  private Company company;

  private Category category1;

  private Category category2;

  private Category category3;

  private Category category4;

  private Category category5;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    CDOSession session = openSession();
    session.options().setLockNotificationMode(LockNotificationMode.ALWAYS);

    transaction = session.openTransaction();
    // transaction.options().setAutoReleaseLocksEnabled(false);

    CDOResource resource = transaction.createResource(getResourcePath("test1"));

    category1 = getModel1Factory().createCategory();
    category1.setName("category1");

    category2 = getModel1Factory().createCategory();
    category2.setName("category2");

    category3 = getModel1Factory().createCategory();
    category3.setName("category3");

    category4 = getModel1Factory().createCategory();
    category4.setName("category4");

    category5 = getModel1Factory().createCategory();
    category5.setName("category5");

    Category category6 = getModel1Factory().createCategory();
    category6.setName("category6");

    Category category7 = getModel1Factory().createCategory();
    category7.setName("category7");

    Category category8 = getModel1Factory().createCategory();
    category8.setName("category8");

    Category category9 = getModel1Factory().createCategory();
    category9.setName("category9");

    Category category10 = getModel1Factory().createCategory();
    category10.setName("category10");

    Category category11 = getModel1Factory().createCategory();
    category11.setName("category11");

    category1.getCategories().add(category2);
    category1.getCategories().add(category3);
    category2.getCategories().add(category4);
    category2.getCategories().add(category5);
    category3.getCategories().add(category8);
    category3.getCategories().add(category9);
    category4.getCategories().add(category6);
    category5.getCategories().add(category7);
    category8.getCategories().add(category10);
    category9.getCategories().add(category11);

    company = getModel1Factory().createCompany();
    company.getCategories().add(category1);

    resource.getContents().add(company);

    log("", Collections.singletonList(category1));
  }

  public void testPartialLockOnCommit() throws Exception
  {
    transaction.lockObjects(CDOUtil.getCDOObjects(category1), LockType.WRITE, DEFAULT_TIMEOUT, true);

    // Mark all objects to stay locked on commit
    transaction.options().addAutoReleaseLocksExemptions(true, category1);
    assertLockStatus(category1, true, true);

    // Mark category2 to be unlocked on commit
    transaction.options().removeAutoReleaseLocksExemptions(false, category2);
    assertLockStatus(category1, true, true);

    // Mark category3 and its descendants to be unlocked on commit
    transaction.options().removeAutoReleaseLocksExemptions(true, category3);
    assertLockStatus(category1, true, true);

    transaction.commit();
    assertLockStatus(category1, true, false);
    assertLockStatus(category2, false, false);
    assertLockStatus(category3, false, true);
    assertLockStatus(category4, true, true);
    assertLockStatus(category5, true, true);
  }

  public void testPartialUnlockOnCommit() throws Exception
  {
    transaction.lockObjects(CDOUtil.getCDOObjects(category1), LockType.WRITE, DEFAULT_TIMEOUT, true);

    // Mark category1 and its descendants to stay locked on commit
    transaction.options().addAutoReleaseLocksExemptions(true, category1);
    assertLockStatus(category1, true, true);

    transaction.unlockObjects(CDOUtil.getCDOObjects(category3), LockType.WRITE, true);
    assertLockStatus(category3, false, true);

    transaction.commit();
    assertLockStatus(category1, true, false);
    assertLockStatus(category2, true, true);
    assertLockStatus(category3, false, true);
  }

  private static void assertLockStatus(Category category, boolean expected, boolean recursive)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(category);
    CDOLockState lockState = cdoObject.cdoLockState();

    assertEquals("new object " + category.getName() + (expected ? " should be locally locked" : " shouldn't be locally locked."), expected,
        lockState.isLocked(LockType.WRITE, ((InternalCDOTransaction)cdoObject.cdoView()).getLockOwner(), false));

    if (recursive)
    {
      for (Category subCategory : category.getCategories())
      {
        assertLockStatus(subCategory, expected, recursive);
      }
    }
  }

  private static void log(String indent, List<Category> categories)
  {
    for (Category category : categories)
    {
      IOUtil.OUT().println(indent + category.getName() + " --> " + CDOUtil.getCDOObject(category).cdoID());
      log(indent + "   ", category.getCategories());
    }
  }
}
