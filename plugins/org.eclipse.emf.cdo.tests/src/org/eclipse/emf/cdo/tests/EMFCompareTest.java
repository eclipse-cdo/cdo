/*
 * Copyright (c) 2012, 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.compare.CDOCompare;
import org.eclipse.emf.cdo.compare.CDOCompareUtil;
import org.eclipse.emf.cdo.compare.CDOComparisonScope;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;
import java.util.Set;

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

    Comparison comparison = CDOCompareUtil.compare(CDOUtil.getCDOObject(transaction.getRootResource()), session.openView(commit2), null);
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
    assertEquals(0, comparison.getMatch(categoryCrossContained).getDifferences().size());
  }

  public void testContainmentProxyEMF() throws Exception
  {
    ResourceSet resourceSetA = createResourceSet();
    Category categoryCrossContained = populate(resourceSetA);

    ResourceSet resourceSetB = createResourceSet();
    Product1 product0 = populate(resourceSetB).getProducts().get(0);
    product0.setName("ProductB");

    IComparisonScope scope = new DefaultComparisonScope(resourceSetA, resourceSetB, null);

    // Configure EMF Compare
    // IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.NEVER);
    // IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
    // IMatchEngine matchEngine = new DefaultMatchEngine(matcher, comparisonFactory);
    // EMFCompare comparator = EMFCompare.builder().setMatchEngine(matchEngine).build();
    EMFCompare comparator = EMFCompare.builder().build();

    // Compare the two models
    Comparison comparison = comparator.compare(scope);
    dump(comparison);
    assertEquals(0, comparison.getMatch(categoryCrossContained).getDifferences().size());
    assertEquals(1, comparison.getMatch(product0).getDifferences().size());
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
    assertEquals(0, comparison.getMatch(category).getDifferences().size());
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

  public void testFeatureFilter() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    Company company = createCompany();
    company.setName("ESC");
    company.setCity("Berlin");
    resource.getContents().add(company);
    CDOCommitInfo commit1 = transaction.commit();

    company.setName("Eclipse");
    company.setCity("Ottawa");
    transaction.commit();

    IComparisonScope scope = CDOComparisonScope.Minimal.create(transaction, session.openView(commit1), null);
    Comparison comparison = new CDOCompare()
    {
      @Override
      protected IDiffEngine createDiffEngine()
      {
        return new DefaultDiffEngine(new DiffBuilder())
        {
          @Override
          protected FeatureFilter createFeatureFilter()
          {
            return new FeatureFilter()
            {
              Set<? extends EStructuralFeature> ignored = Collections.singleton(getModel1Package().getAddress_City());

              @Override
              protected boolean isIgnoredAttribute(EAttribute attribute)
              {
                return ignored.contains(attribute);
              }
            };
          }
        };
      }
    }.compare(scope);

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

  private static ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    return resourceSet;
  }

  private static Category populate(ResourceSet resourceSet)
  {
    Model1Factory factory = org.eclipse.emf.cdo.tests.model1.legacy.Model1Factory.eINSTANCE;

    Resource resourceA1 = resourceSet.createResource(URI.createURI("res1"));
    Resource resourceA2 = resourceSet.createResource(URI.createURI("res2"));

    Company company = factory.createCompany();
    company.setName("Eclipse");
    resourceA1.getContents().add(company);

    Category categoryCrossContained = factory.createCategory();
    categoryCrossContained.setName("Category");
    company.getCategories().add(categoryCrossContained);
    resourceA2.getContents().add(categoryCrossContained);

    for (int i = 0; i < 10; ++i)
    {
      Product1 product = factory.createProduct1();
      product.setName("Product" + i);
      categoryCrossContained.getProducts().add(product);
    }

    return categoryCrossContained;
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
      System.out.println(indent + "   Left:   " + toString(match == null ? null : match.getLeft()));
      System.out.println(indent + "   Right:  " + toString(match == null ? null : match.getRight()));
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
      CDORevision revision = cdoObject.cdoRevision();
      if (revision != null)
      {
        return revision.toString();
      }
    }

    return object.toString();
  }
}
