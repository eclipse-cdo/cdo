/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.compare.CDOCompareUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class EMFCompareTest extends AbstractCDOTest
{
  public void testAllContentsOfCompany() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = createCompany();
    company.setName("ESC");

    resource.getContents().add(company);
    resource.getContents().add(createCompany());
    resource.getContents().add(createCompany());
    transaction.commit();

    company.setName("Sympedia");
    CDOCommitInfo commit2 = transaction.commit();

    company.setName("Eclipse");
    transaction.commit();

    Comparison comparison = CDOCompareUtil.compare(CDOUtil.getCDOObject(company), session.openView(commit2), null);
    dump(comparison);
  }

  public void testAllContentsOfRoot() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = createCompany();
    company.setName("ESC");

    resource.getContents().add(company);
    resource.getContents().add(createCompany());
    resource.getContents().add(createCompany());
    transaction.commit();

    company.setName("Sympedia");
    CDOCommitInfo commit2 = transaction.commit();

    company.setName("Eclipse");
    transaction.commit();

    Comparison comparison = CDOCompareUtil.compare(CDOUtil.getCDOObject(transaction.getRootResource()),
        session.openView(commit2), null);
    dump(comparison);
  }

  public void testMinimal() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = createCompany();

    resource.getContents().add(company);
    resource.getContents().add(createCompany());
    resource.getContents().add(createCompany());
    transaction.commit();

    company.setName("Sympedia");
    CDOCommitInfo commit2 = transaction.commit();

    company.setName("Eclipse");
    transaction.commit();

    Comparison comparison = CDOCompareUtil.compare(transaction, session.openView(commit2), null);
    dump(comparison);
  }

  public void testContainmentProxy() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/res1"));
    CDOResource resource2 = transaction.createResource(getResourcePath("/res2"));

    Company company = getModel1Factory().createCompany();
    company.setName("Eclipse");
    resource1.getContents().add(company);

    Category categoryCrossContained = getModel1Factory().createCategory();
    categoryCrossContained.setName("Category");
    company.getCategories().add(categoryCrossContained);
    resource2.getContents().add(categoryCrossContained);

    for (int i = 0; i < 10; ++i)
    {
      Product1 product = getModel1Factory().createProduct1();
      categoryCrossContained.getProducts().add(product);
    }

    CDOCommitInfo commitInfo = transaction.commit();

    Product1 product0 = categoryCrossContained.getProducts().get(0);
    product0.setName("CHANGED");

    transaction.commit();

    Comparison comparison = CDOCompareUtil.compare(transaction, session.openView(commitInfo), null);
    dump(comparison);

    Match match = comparison.getMatch(categoryCrossContained);
    EList<Diff> differences = match.getDifferences();

    int fails;
    assertEquals(0, differences.size());
  }

  public void testNoContainmentProxy() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/res1"));

    Company company = getModel1Factory().createCompany();
    company.setName("Company");
    resource1.getContents().add(company);

    Category category = getModel1Factory().createCategory();
    category.setName("Category");
    company.getCategories().add(category);

    for (int i = 0; i < 10; ++i)
    {
      Product1 product = getModel1Factory().createProduct1();
      category.getProducts().add(product);
    }

    company.setName("Company2");

    CDOCommitInfo commitInfo = transaction.commit();

    category.getProducts().get(0).setName("EclipseProduct");
    transaction.commit();

    Comparison comparison = CDOCompareUtil.compare(transaction, session.openView(commitInfo), null);
    dump(comparison);

    Match match = comparison.getMatch(category);
    assertEquals(0, match.getDifferences().size());
  }

  public void testChanges() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = getModel1Factory().createCompany();
    company.setName("Company");
    resource.getContents().add(company);

    Category category1 = getModel1Factory().createCategory();
    category1.setName("Category1");
    company.getCategories().add(category1);

    transaction.commit();

    // Change category1
    category1.setName("CHANGED");

    Category category2 = getModel1Factory().createCategory();
    category2.setName("Category2");

    // Change company, add category2
    company.getCategories().add(category2);

    Comparison comparison = CDOCompareUtil.compareUncommittedChanges(transaction);
    dump(comparison);

    assertEquals(1, comparison.getMatch(category1).getDifferences().size());
    assertEquals(1, comparison.getMatch(company).getDifferences().size());
  }

  public void testChangesDelete() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = getModel1Factory().createCompany();
    company.setName("Company");
    resource.getContents().add(company);

    Category category = getModel1Factory().createCategory();
    category.setName("Category");
    company.getCategories().add(category);

    transaction.commit();

    company.getCategories().clear();

    Comparison comparison = CDOCompareUtil.compareUncommittedChanges(transaction);
    dump(comparison);

    assertEquals(1, comparison.getMatch(company).getDifferences().size());
  }

  private Company createCompany()
  {
    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    return company;
  }

  private static void dump(Comparison comparison)
  {
    dump(comparison.getMatches(), "");
  }

  private static void dump(EList<Match> matches, String indent)
  {
    for (Match match : matches)
    {
      System.out.println(indent + "Match:");
      System.out.println(indent + "   Left:   " + toString(match.getLeft()));
      System.out.println(indent + "   Right:  " + toString(match.getRight()));
      String origin = toString(match.getOrigin());
      if (origin != null)
      {
        System.out.println(indent + "   Origin: " + origin);
      }

      System.out.println(indent + "   Differences:");
      for (Diff diff : match.getDifferences())
      {
        System.out.println(indent + "      " + diff);
      }

      dump(match.getSubmatches(), indent + "   ");
    }
  }

  private static String toString(EObject object)
  {
    if (object == null)
    {
      return null;
    }

    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (cdoObject != null)
    {
      return cdoObject.cdoRevision().toString();
    }

    return object.toString();
  }
}
