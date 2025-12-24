/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.util.Collections;

/**
 * @author Esteban Dugueperoux
 */
public class Bugzilla_373096_Test extends AbstractCDOTest
{
  public void testDragAndDrop() throws Throwable
  {
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    CDOResource cdoResource = transaction.createResource(getResourcePath("/test1"));
    transaction.commit();

    Company company = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();
    Category category2 = getModel1Factory().createCategory();
    Product1 productToDragAndDrop = getModel1Factory().createProduct1();
    productToDragAndDrop.setName("test1");

    company.getCategories().add(category1);
    company.getCategories().add(category2);
    category1.getProducts().add(productToDragAndDrop);

    cdoResource.getContents().add(company);
    cdoResource.save(Collections.emptyMap());
    CDOObject cdoObject = CDOUtil.getCDOObject(productToDragAndDrop);

    // dragAndDrop to category1
    category1.getProducts().remove(productToDragAndDrop);
    category2.getProducts().add(productToDragAndDrop);

    // Set a first save point
    transaction.setSavepoint();

    // undo the dragAndDrop
    category2.getProducts().remove(productToDragAndDrop);
    category1.getProducts().add(productToDragAndDrop);

    // Set a second save point
    transaction.setSavepoint();

    assertEquals(true, transaction.getDirtyObjects().values().contains(cdoObject));

    CDOState cdoState = cdoObject.cdoState();
    assertNotSame(CDOState.CLEAN, cdoState);

    cdoResource.save(Collections.emptyMap());
  }
}
