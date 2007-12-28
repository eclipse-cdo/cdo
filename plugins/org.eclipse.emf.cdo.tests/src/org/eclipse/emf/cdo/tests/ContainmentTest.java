/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author Eike Stepper
 */
public class ContainmentTest extends AbstractCDOTest
{
  public void testTransientContainment() throws Exception
  {
    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Creating company");
    Company company = Model1Factory.eINSTANCE.createCompany();

    msg("Adding supplier");
    company.getSuppliers().add(supplier);

    assertTransient(company);
    assertTransient(supplier);
    assertContent(company, supplier);
  }

  public void testBasicContainment() throws Exception
  {
    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Creating company");
    Company company = Model1Factory.eINSTANCE.createCompany();

    msg("Adding supplier");
    company.getSuppliers().add(supplier);

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Adding company");
    resource.getContents().add(company);

    msg("Committing");
    transaction.commit();

    assertClean(resource);
    assertClean(company);
    assertClean(supplier);
    assertContent(resource, company);
    assertContent(company, supplier);
  }

  public void test3Levels() throws Exception
  {
    msg("Creating category1");
    Category category1 = Model1Factory.eINSTANCE.createCategory();
    category1.setName("category1");

    msg("Creating category2");
    Category category2 = Model1Factory.eINSTANCE.createCategory();
    category2.setName("category2");

    msg("Creating category3");
    Category category3 = Model1Factory.eINSTANCE.createCategory();
    category3.setName("category3");

    msg("Creating company");
    Company company = Model1Factory.eINSTANCE.createCompany();

    msg("Adding categories");
    company.getCategories().add(category1);
    category1.getCategories().add(category2);
    category2.getCategories().add(category3);

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Adding company");
    resource.getContents().add(company);

    msg("Committing");
    transaction.commit();

    assertClean(resource);
    assertClean(company);
    assertClean(category1);
    assertClean(category2);
    assertClean(category3);
    assertContent(resource, company);
    assertContent(company, category1);
    assertContent(category1, category2);
    assertContent(category2, category3);
  }

  public void testSeparateView() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    {
      msg("Creating category1");
      Category category1 = Model1Factory.eINSTANCE.createCategory();
      category1.setName("category1");

      msg("Creating category2");
      Category category2 = Model1Factory.eINSTANCE.createCategory();
      category2.setName("category2");

      msg("Creating category3");
      Category category3 = Model1Factory.eINSTANCE.createCategory();
      category3.setName("category3");

      msg("Creating company");
      Company company = Model1Factory.eINSTANCE.createCompany();

      msg("Adding categories");
      company.getCategories().add(category1);
      category1.getCategories().add(category2);
      category2.getCategories().add(category3);

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Adding company");
      resource.getContents().add(company);

      msg("Committing");
      transaction.commit();
    }

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");
    assertProxy(resource);

    EList<EObject> contents = resource.getContents();
    Company company = (Company)contents.get(0);
    assertClean(company);
    assertClean(resource);
    assertContent(resource, company);

    Category category1 = company.getCategories().get(0);
    assertClean(category1);
    assertClean(company);
    assertContent(company, category1);

    Category category2 = category1.getCategories().get(0);
    assertClean(category2);
    assertClean(category1);
    assertContent(category1, category2);

    Category category3 = category2.getCategories().get(0);
    assertClean(category3);
    assertClean(category2);
    assertContent(category2, category3);
    assertClean(category3);
  }

  public void testSeparateSession() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Creating category1");
      Category category1 = Model1Factory.eINSTANCE.createCategory();
      category1.setName("category1");

      msg("Creating category2");
      Category category2 = Model1Factory.eINSTANCE.createCategory();
      category2.setName("category2");

      msg("Creating category3");
      Category category3 = Model1Factory.eINSTANCE.createCategory();
      category3.setName("category3");

      msg("Creating company");
      Company company = Model1Factory.eINSTANCE.createCompany();

      msg("Adding categories");
      company.getCategories().add(category1);
      category1.getCategories().add(category2);
      category2.getCategories().add(category3);

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Adding company");
      resource.getContents().add(company);

      msg("Committing");
      transaction.commit();
    }

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");
    assertProxy(resource);

    EList<EObject> contents = resource.getContents();
    Company company = (Company)contents.get(0);
    assertClean(company);
    assertClean(resource);
    assertContent(resource, company);

    Category category1 = company.getCategories().get(0);
    assertClean(category1);
    assertClean(company);
    assertContent(company, category1);

    Category category2 = category1.getCategories().get(0);
    assertClean(category2);
    assertClean(category1);
    assertContent(category1, category2);

    Category category3 = category2.getCategories().get(0);
    assertClean(category3);
    assertClean(category2);
    assertContent(category2, category3);
    assertClean(category3);
  }
}
