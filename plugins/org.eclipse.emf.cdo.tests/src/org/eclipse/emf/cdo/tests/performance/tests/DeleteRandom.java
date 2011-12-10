/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.performance.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.performance.framework.AbstractCDOPerformanceMeasurement;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.Random;

/**
 * @author Stefan Winkler
 */
public class DeleteRandom extends AbstractCDOPerformanceMeasurement
{
  private static final String RES_NAME = "res";

  private static final int AMOUNT_ELEMENTS = 20000;

  private CDOSession session;

  private Random random = new Random();

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    session = openSession();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RES_NAME));

    Company company = getModel1Factory().createCompany();

    for (int i = 0; i < AMOUNT_ELEMENTS; i++)
    {
      Category cat = getModel1Factory().createCategory();
      cat.setName(Integer.toString(i));
      company.getCategories().add(cat);
    }

    resource.getContents().add(company);
    transaction.commit();
    transaction.close();
  }

  public void test() throws Exception
  {
    for (int currentSize = AMOUNT_ELEMENTS; currentSize > AMOUNT_ELEMENTS / 2; currentSize--)
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath(RES_NAME));
      Company company = (Company)resource.getContents().get(0);

      int indexToRemove = random.nextInt(currentSize);
      company.getCategories().remove(indexToRemove);
      transaction.commit();
      transaction.close();
    }
  }

  @Override
  protected void doTearDown() throws Exception
  {
    session.close();

    super.doTearDown();
  }

}
