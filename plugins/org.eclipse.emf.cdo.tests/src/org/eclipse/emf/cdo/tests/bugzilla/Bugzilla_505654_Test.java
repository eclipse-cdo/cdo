/*
 * Copyright (c) 2016, 2017, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig.CountedTime;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bug 505654 - Support automatic re-merging / multiple merges from the same branch
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
@CountedTime
public class Bugzilla_505654_Test extends AbstractCDOTest
{
  private CDOTransaction lastCommit;

  private CDOTransaction leftTransaction;

  private CDOTransaction rightTransaction;

  private Company leftCompany;

  private Company rightCompany;

  private void assertIDs(List<CDOID> actual, CDOID... expected)
  {
    assertEquals(Arrays.asList(expected), actual);
  }

  private static void dump(String indent, EObject object, List<CDOID> ids)
  {
    for (EObject child : object.eContents())
    {
      System.out.println(indent + child);
      ids.add(CDOUtil.getCDOObject(child).cdoID());
      dump(indent + " ", child);
    }
  }

  private static List<CDOID> dump(String indent, EObject object)
  {
    List<CDOID> ids = new ArrayList<>();
    dump(indent, object, ids);
    return ids;
  }

  private List<CDOID> leftCommit() throws CommitException
  {
    if (lastCommit == leftTransaction)
    {
      System.out.println();
    }
    else
    {
      lastCommit = leftTransaction;
    }

    CDOCommitInfo commit = commitAndSync(leftTransaction, rightTransaction);
    System.out.println("COMMIT " + commit.getTimeStamp());
    return dump("", leftCompany);
  }

  private List<CDOID> rightCommit() throws CommitException
  {
    if (lastCommit == rightTransaction)
    {
      System.out.println();
    }
    else
    {
      lastCommit = rightTransaction;
    }

    CDOCommitInfo commit = commitAndSync(rightTransaction, leftTransaction);
    System.out.println("                              COMMIT " + commit.getTimeStamp());
    return dump("                              ", rightCompany);
  }

  private CDOMerger createMerger()
  {
    return new DefaultCDOMerger.PerFeature.ManyValued();
  }

  private List<CDOID> leftMerge() throws CommitException
  {
    System.out.println("             <----------------");
    CDOMerger merger = createMerger();
    leftTransaction.merge(rightTransaction.getBranch(), merger);
    lastCommit = null;
    return leftCommit();
  }

  private List<CDOID> rightMerge() throws CommitException
  {
    System.out.println("             ---------------->");
    CDOMerger merger = createMerger();
    rightTransaction.merge(leftTransaction.getBranch(), merger);
    lastCommit = null;
    return rightCommit();
  }

  private CDOID leftAdd(EClass eClass) throws CommitException
  {
    return add(eClass, true);
  }

  private CDOID rightAdd(EClass eClass) throws CommitException
  {
    return add(eClass, false);
  }

  private CDOID add(EClass eClass, boolean left) throws CommitException
  {
    EObject object = EcoreUtil.create(eClass);
    for (EReference containment : getModel1Package().getCompany().getEAllContainments())
    {
      if (containment.getEReferenceType() == eClass)
      {
        Company company = left ? leftCompany : rightCompany;

        @SuppressWarnings("unchecked")
        EList<EObject> list = (EList<EObject>)company.eGet(containment);
        list.add(object);
        break;
      }
    }

    if (left)
    {
      leftCommit();
    }
    else
    {
      rightCommit();
    }

    return CDOUtil.getCDOObject(object).cdoID();
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    CDOSession session = openSession();

    leftTransaction = session.openTransaction();
    CDOResource leftResource = leftTransaction.createResource(getResourcePath("/model1"));
    leftCompany = getModel1Factory().createCompany();
    leftResource.getContents().add(leftCompany);
    CDOCommitInfo commit = leftTransaction.commit();

    System.out.println("COMMIT " + commit.getTimeStamp());
    System.out.println(leftCompany);

    CDOBranch rightBranch = CDOUtil.createBranch(commit, getBranchName("branch1"));
    rightTransaction = session.openTransaction(rightBranch);
    CDOResource rightResource = rightTransaction.getResource(getResourcePath("/model1"));
    rightCompany = (Company)rightResource.getContents().get(0);
  }

  public void testMultipleRemerges() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier1 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, customer1);

    CDOID customer2 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier2 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, customer1, customer2);

    CDOID customer3 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier3 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, supplier3, customer1, customer2, customer3);
  }

  public void testMultipleRemerges_SameList() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID customer1b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1);

    CDOID customer2 = leftAdd(getModel1Package().getCustomer());
    CDOID customer2b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer2b, customer1, customer2);

    CDOID customer3 = leftAdd(getModel1Package().getCustomer());
    CDOID customer3b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer2b, customer3b, customer1, customer2, customer3);
  }

  public void testCrossMerge() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier1 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, customer1);
    assertIDs(rightMerge(), supplier1, customer1);
  }

  public void testCrossMerge_SameList() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID customer1b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1);
    assertIDs(rightMerge(), customer1b, customer1);
  }

  public void testCrossMergeAndRemerge() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier1 = rightAdd(getModel1Package().getSupplier());
    assertIDs(rightMerge(), supplier1, customer1);

    CDOID supplier2 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, customer1);
  }

  public void testCrossMergeAndRemerge_SameList() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID customer1b = rightAdd(getModel1Package().getCustomer());
    assertIDs(rightMerge(), customer1, customer1b);

    CDOID customer2b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1, customer1b, customer2b);
  }

  public void testMergeAndCrossMergeAndRemerge() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier1 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, customer1);
    assertIDs(rightMerge(), supplier1, customer1);

    CDOID supplier2 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, customer1);
  }

  public void testMergeAndCrossMergeAndRemerge_SameList() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID customer1b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1);
    assertIDs(rightMerge(), customer1b, customer1);

    CDOID customer2b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1, customer2b);
  }

  public void testCrossMergeAndMultipleRemerges() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier1 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, customer1);
    assertIDs(rightMerge(), supplier1, customer1);

    CDOID supplier2 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, customer1);

    CDOID supplier3 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, supplier3, customer1);
  }

  public void testCrossMergeAndMultipleRemerges_SameList() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID customer1b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1);
    assertIDs(rightMerge(), customer1b, customer1);

    CDOID customer2b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1, customer2b);

    CDOID customer3b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1, customer2b, customer3b);
  }

  public void testCrossMergeAndAdditionsAndRemerges() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier1 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, customer1);
    assertIDs(rightMerge(), supplier1, customer1);

    CDOID customer2 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier2 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, customer1, customer2);

    CDOID customer3 = leftAdd(getModel1Package().getCustomer());
    CDOID supplier3 = rightAdd(getModel1Package().getSupplier());
    assertIDs(leftMerge(), supplier1, supplier2, supplier3, customer1, customer2, customer3);
  }

  public void testCrossMergeAndAdditionsAndRemerges_SameList() throws Exception
  {
    CDOID customer1 = leftAdd(getModel1Package().getCustomer());
    CDOID customer1b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1);
    assertIDs(rightMerge(), customer1b, customer1);

    CDOID customer2 = leftAdd(getModel1Package().getCustomer());
    CDOID customer2b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1, customer2b, customer2);

    CDOID customer3 = leftAdd(getModel1Package().getCustomer());
    CDOID customer3b = rightAdd(getModel1Package().getCustomer());
    assertIDs(leftMerge(), customer1b, customer1, customer2b, customer3b, customer2, customer3);
  }
}
