/*
 * Copyright (c) 2015, 2016, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.List;

/**
 * Bug 456993 about allInstances ocl query on branches.
 *
 * @author Esteban Dugueperoux
 */
@CleanRepositoriesBefore(reason = "Query result counting")
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_456993_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  private static final String B1 = "b1";

  private static final String B11 = "b11";

  private static final String B12 = "b12";

  private static final String B2 = "b2";

  private static final String B21 = "b21";

  private static final String B22 = "b22";

  public void testAllInstancesOCLQueryOnBranches() throws Exception
  {
    testAllInstancesQueryOnBranches("ocl", ".allInstances()", null, null);
  }

  public void testAllInstancesInstancesQueryOnBranches() throws Exception
  {
    testAllInstancesQueryOnBranches(CDOProtocolConstants.QUERY_LANGUAGE_INSTANCES, null, null, null);
  }

  private void testAllInstancesQueryOnBranches(String language, String queryStringSuffix, String parameterName, String parameterValue) throws Exception
  {
    CDOSession session1 = openSession();
    CDOBranch mainBranch = session1.getBranchManager().getMainBranch();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));

    Company company = getModel1Factory().createCompany();
    CDOQuery query = createQuery(transaction1, language, getModel1Package().getCompany(), queryStringSuffix);
    List<Object> instances = query.getResult();
    assertEquals(0, instances.size());

    resource1.getContents().add(company);

    query = createQuery(transaction1, language, getModel1Package().getCompany(), queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    transaction1.commit();

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    Category category = getModel1Factory().createCategory();
    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    CDOBranch b1 = mainBranch.createBranch(getBranchName(B1));
    transaction1.setBranch(b1);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    company.getCategories().add(category);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    transaction1.commit();

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    transaction1.setBranch(mainBranch);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    CDOBranch b11 = b1.createBranch(B11);
    CDOBranch b12 = b1.createBranch(B12);
    CDOBranch b2 = mainBranch.createBranch(getBranchName(B2));
    CDOBranch b21 = b2.createBranch(B21);
    CDOBranch b22 = b2.createBranch(B22);

    transaction1.setBranch(b11);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    transaction1.setBranch(b12);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    CDOID companyCDOID = CDOUtil.getCDOObject(company).cdoID();
    company = (Company)CDOUtil.getEObject(transaction1.getObject(companyCDOID));
    EcoreUtil.remove(company);
    transaction1.commit();

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    transaction1.setBranch(b2);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    company = (Company)CDOUtil.getEObject(transaction1.getObject(companyCDOID));
    EcoreUtil.remove(company);
    transaction1.commit();

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    transaction1.setBranch(b21);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());

    transaction1.setBranch(b22);

    query = createQuery(transaction1, language, company, queryStringSuffix);
    instances = query.getResult();
    assertEquals(1, instances.size());

    query = createQuery(transaction1, language, category, queryStringSuffix);
    instances = query.getResult();
    assertEquals(0, instances.size());
  }

  private CDOQuery createQuery(CDOView view, String language, EObject eObject, String queryStringSuffix)
  {
    EClass eClass = eObject.eClass();
    CDOQuery query = view.createQuery(language, queryStringSuffix != null ? eClass.getEPackage().getName() + "::" + eClass.getName() + queryStringSuffix : null,
        eObject.eResource() != null ? eObject : null);
    if (queryStringSuffix == null)
    {
      query.setParameter(CDOProtocolConstants.QUERY_LANGUAGE_INSTANCES_TYPE, eClass);
    }

    return query;
  }
}
