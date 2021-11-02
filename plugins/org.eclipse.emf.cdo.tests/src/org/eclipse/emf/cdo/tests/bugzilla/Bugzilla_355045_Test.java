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
import org.eclipse.emf.cdo.CDOState;
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

import java.util.Collections;

/**
 * @author Esteban Dugueperoux
 */
public class Bugzilla_355045_Test extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test1";

  private CDOTransaction transaction;

  private Company company;

  private Category category1;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    CDOSession cdoSession = openSession();
    cdoSession.options().setLockNotificationMode(LockNotificationMode.ALWAYS);
    transaction = cdoSession.openTransaction();
    // cdoTransaction.options().setAutoReleaseLocksEnabled(false);
    CDOResource cdoResource = transaction.createResource(getResourcePath(RESOURCE_PATH));

    company = getModel1Factory().createCompany();
    category1 = getModel1Factory().createCategory();
    company.getCategories().add(category1);

    cdoResource.getContents().add(company);
    cdoResource.save(Collections.emptyMap());
  }

  public void testLockOnCommitOfSingleNewObject() throws Exception
  {
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");
    category1.getCategories().add(category2);

    transaction.lockObjects(CDOUtil.getCDOObjects(category2), LockType.WRITE, DEFAULT_TIMEOUT);
    transaction.options().addAutoReleaseLocksExemptions(true, category2);
    assertLockStatus(category2, true, false);

    transaction.commit();
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(category2).cdoState());
    assertLockStatus(category2, true, false);
  }

  public void testRecursiveLockOnCommitOfNewObjectsTree() throws Exception
  {
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");
    Category category4 = getModel1Factory().createCategory();
    category4.setName("category4");
    Category category5 = getModel1Factory().createCategory();
    category5.setName("category5");
    Category category6 = getModel1Factory().createCategory();
    category6.setName("category6");
    Category category7 = getModel1Factory().createCategory();
    category7.setName("category7");

    category2.getCategories().add(category4);
    category2.getCategories().add(category5);
    category4.getCategories().add(category6);
    category5.getCategories().add(category7);
    category1.getCategories().add(category2);

    Category category3 = getModel1Factory().createCategory();
    category3.setName("category3");
    Category category8 = getModel1Factory().createCategory();
    category8.setName("category8");
    Category category9 = getModel1Factory().createCategory();
    category9.setName("category9");
    Category category10 = getModel1Factory().createCategory();
    category10.setName("category10");
    Category category11 = getModel1Factory().createCategory();
    category11.setName("category11");

    category3.getCategories().add(category8);
    category3.getCategories().add(category9);
    category8.getCategories().add(category10);
    category9.getCategories().add(category11);
    category1.getCategories().add(category3);

    transaction.lockObjects(CDOUtil.getCDOObjects(category2), LockType.WRITE, DEFAULT_TIMEOUT, true);
    transaction.options().addAutoReleaseLocksExemptions(true, category2);

    assertLockStatus(category1, false, false);
    assertLockStatus(category2, true, true);
    assertLockStatus(category3, false, true);

    transaction.lockObjects(CDOUtil.getCDOObjects(category3), LockType.WRITE, DEFAULT_TIMEOUT, true);
    transaction.options().addAutoReleaseLocksExemptions(true, category3);

    assertLockStatus(category1, false, false);
    assertLockStatus(category2, true, true);
    assertLockStatus(category3, true, true);

    transaction.unlockObjects(CDOUtil.getCDOObjects(category3), LockType.WRITE);

    assertLockStatus(category1, false, false);
    assertLockStatus(category2, true, true);
    assertLockStatus(category3, false, false);
    assertLockStatus(category8, true, true);
    assertLockStatus(category9, true, true);

    transaction.commit();

    assertLockStatus(category1, false, false);
    assertLockStatus(category2, true, true);
    assertLockStatus(category3, false, false);
    assertLockStatus(category8, true, true);
    assertLockStatus(category9, true, true);
  }

  public void testRecursiveLockOfObjectsTreeContainingASubTreeOfNewObjects() throws Exception
  {
    Category category2 = getModel1Factory().createCategory();
    category2.setName("category2");
    Category category4 = getModel1Factory().createCategory();
    category4.setName("category4");
    Category category5 = getModel1Factory().createCategory();
    category5.setName("category5");
    Category category6 = getModel1Factory().createCategory();
    category6.setName("category6");
    Category category7 = getModel1Factory().createCategory();
    category7.setName("category7");

    category2.getCategories().add(category4);
    category2.getCategories().add(category5);
    category4.getCategories().add(category6);
    category5.getCategories().add(category7);
    category1.getCategories().add(category2);

    Category category3 = getModel1Factory().createCategory();
    category3.setName("category3");
    Category category8 = getModel1Factory().createCategory();
    category8.setName("category8");
    Category category9 = getModel1Factory().createCategory();
    category9.setName("category9");
    Category category10 = getModel1Factory().createCategory();
    category10.setName("category10");
    Category category11 = getModel1Factory().createCategory();
    category11.setName("category11");

    category3.getCategories().add(category8);
    category3.getCategories().add(category9);
    category8.getCategories().add(category10);
    category9.getCategories().add(category11);
    category1.getCategories().add(category3);

    transaction.lockObjects(CDOUtil.getCDOObjects(category1), LockType.WRITE, DEFAULT_TIMEOUT, true);
    transaction.options().addAutoReleaseLocksExemptions(false, category1);
    assertLockStatus(category1, true, false);

    transaction.commit();
    assertLockStatus(category1, true, false);
  }

  private void assertLockStatus(Category category, boolean lockedByMe, boolean recursive)
  {
    CDOObject categoryCDOObject = CDOUtil.getCDOObject(category);
    CDOLockState cdoLockState = categoryCDOObject.cdoLockState();
    assertEquals("new object " + category.getName() + (lockedByMe ? " should be locally locked" : " shouldn't be locally locked"), lockedByMe,
        cdoLockState.isLocked(LockType.WRITE, transaction, false));

    if (recursive)
    {
      for (Category subCategory : category.getCategories())
      {
        assertLockStatus(subCategory, lockedByMe, recursive);
      }
    }
  }
}
