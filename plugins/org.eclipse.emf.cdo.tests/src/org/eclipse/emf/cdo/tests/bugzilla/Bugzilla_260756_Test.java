/*
 * Copyright (c) 2009-2012, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CDOEList::toArray not implemented
 * <p>
 * See bug 260756
 *
 * @author Simon McDuff
 */
public class Bugzilla_260756_Test extends AbstractCDOTest
{
  @Requires("MEM")
  public void testBugzilla_260756() throws Exception
  {
    Set<Object> objects = new HashSet<>();
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));
    objects.add(resource1);
    Company company1 = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();
    Category category2 = getModel1Factory().createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);
    company1.getCategories().add(category2);

    objects.add(category1);
    objects.add(category2);

    company1.setName("TEST");

    transaction.commit();
    System.out.println(category1.eClass().getEPackage().getNsURI());

    CDOQuery cdoQuery = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    cdoQuery.setParameter("context", getModel1Package().getCategory());

    List<Category> queryResult = cdoQuery.getResult(Category.class);
    assertEquals(2, queryResult.size());
    assertEquals(true, objects.contains(queryResult.get(0)));
    assertEquals(true, objects.contains(queryResult.get(1)));

    Object[] array1 = queryResult.toArray();
    Category[] arrayOfCategory = queryResult.toArray(new Category[queryResult.size()]);
    assertEquals(2, array1.length);
    assertEquals(queryResult.get(0), array1[0]);
    assertEquals(queryResult.get(1), array1[1]);

    assertEquals(2, arrayOfCategory.length);
    assertEquals(queryResult.get(0), arrayOfCategory[0]);
    assertEquals(queryResult.get(1), arrayOfCategory[1]);

    transaction.close();
    session.close();
  }
}
