/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.analyzer.CDOAbstractFeatureRuleAnalyzer;
import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerModelBased;
import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerUI;
import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingPolicyImpl;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * Bug 449806 : Test {@link CDOFetchRuleManager} on {@link CDOResource}.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_449806_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  private static final int NB_CATEGORY = 10;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    for (int i = 0; i < NB_CATEGORY; i++)
    {
      Category category = getModel1Factory().createCategory();
      company.getCategories().add(category);
    }
    resource.getContents().add(company);
    transaction.commit();
    transaction.close();
    session.close();
  }

  public void testCDOFeatureAnalyzerUI() throws Exception
  {
    testCDOAbstractFeatureRuleAnalyzer(new CDOFeatureAnalyzerUI());
  }

  public void testCDOFeatureAnalyzerModelBased() throws Exception
  {
    testCDOAbstractFeatureRuleAnalyzer(new CDOFeatureAnalyzerModelBased());
  }

  private void testCDOAbstractFeatureRuleAnalyzer(CDOAbstractFeatureRuleAnalyzer abstractFeatureRuleAnalyzer) throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(new CDOCollectionLoadingPolicyImpl(1, 0));
    ((InternalCDOSession)session).setFetchRuleManager(abstractFeatureRuleAnalyzer);

    CDOView currentView = session.openView();
    currentView.options().setFeatureAnalyzer(abstractFeatureRuleAnalyzer);

    CDOResource resource = currentView.getResource(getResourcePath(RESOURCE_NAME));

    Company company = (Company)resource.getContents().get(0);
    assertEquals(NB_CATEGORY, company.getCategories().size());
  }
}
