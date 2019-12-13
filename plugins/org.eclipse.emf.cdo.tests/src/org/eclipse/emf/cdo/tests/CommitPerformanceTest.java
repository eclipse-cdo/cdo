/*
 * Copyright (c) 2012, 2013, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.tests.Timer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class CommitPerformanceTest extends AbstractCDOTest
{
  private CDOSession session;

  private CDOTransaction transaction;

  private CDOResource resource;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.createResource(getResourcePath("/my/resource"));
    transaction.commit();
  }

  public void test125000() throws Exception
  {
    createModel(50, 50, 50);
    commit();
  }

  public void test250000() throws Exception
  {
    createModel(50, 50, 100);
    commit();
  }

  public void test500000() throws Exception
  {
    createModel(50, 100, 100);
    commit();
  }

  public void test500000XMI() throws Exception
  {
    createModel(50, 100, 100);

    Resource.Factory resourceFactory = new XMIResourceFactoryImpl();
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory); //$NON-NLS-1$
    Resource resource = resourceSet.createResource(URI.createFileURI(createTempFile().getAbsolutePath()));
    resource.getContents().addAll(this.resource.getContents());

    Timer timer = new Timer();
    resource.save(null);
    timer.done();
  }

  private void createModel(int companies, int categories, int products)
  {
    for (int i = 0; i < companies; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("Eclipse Foundation " + i);
      company.setStreet("Milinkovic Street");
      company.setCity("Ottawa");
      resource.getContents().add(company);

      for (int j = 0; j < categories; j++)
      {
        Category category = getModel1Factory().createCategory();
        category.setName("Special Category " + i + "/" + j);
        company.getCategories().add(category);

        for (int k = 0; k < products; k++)
        {
          Product1 product = getModel1Factory().createProduct1();
          product.setName("Awesome Product " + i + "/" + j + "/" + k);
          product.setDescription("This descriptive text is the same for all products in all categories of all companies.");
          product.setVat(VAT.VAT15);
          category.getProducts().add(product);
        }
      }
    }
  }

  private CDOCommitInfo commit() throws Exception
  {
    return Timer.execute(TimeUnit.SECONDS, new Callable<CDOCommitInfo>()
    {
      @Override
      public CDOCommitInfo call() throws Exception
      {
        return transaction.commit();
      }
    });
  }
}
