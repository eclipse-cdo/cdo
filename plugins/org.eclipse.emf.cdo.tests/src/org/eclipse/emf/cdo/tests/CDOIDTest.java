/*
 * Copyright (c) 2009-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.id.CDOIDNullImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

/**
 * @author Stefan Winkler
 */
public class CDOIDTest extends AbstractCDOTest
{
  public void testGetLong_Null()
  {
    assertEquals(0L, CDOIDUtil.getLong(null));
  }

  public void testGetLong_NullId()
  {
    CDOIDNullImpl id = CDOIDNullImpl.INSTANCE;
    assertEquals(0L, CDOIDUtil.getLong(id));
  }

  public void testGetLong_LongId()
  {
    CDOIDObjectLongImpl id = CDOIDObjectLongImpl.create(123L);
    assertEquals(123L, CDOIDUtil.getLong(id));
  }

  public void testGetLong_TempId()
  {
    CDOIDTempObjectImpl id = CDOIDTempObjectImpl.create(456);
    assertIllegalArgument(id);
  }

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testGetLong_ExtTempId()
  {
    CDOIDTempObjectExternalImpl id = CDOIDTempObjectExternalImpl.create("cdo://repo123/resource456");
    assertIllegalArgument(id);
  }

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testGetLong_ExtId()
  {
    CDOIDExternal id = CDOIDUtil.createExternal("cdo://repo123/resource456");
    assertIllegalArgument(id);
  }

  private void assertIllegalArgument(CDOID id)
  {
    boolean thrown = false;

    try
    {
      CDOIDUtil.getLong(id);
    }
    catch (IllegalArgumentException e)
    {
      thrown = true;
    }

    if (!thrown)
    {
      fail("Expected IllegalArgumentException!");
    }
  }

  public void testURIFragment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    resource.getContents().add(supplier);
    transaction.commit();

    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, CDOUtil.getCDOObject(supplier).cdoID());

    String uriFragment = builder.toString();
    System.out.println(uriFragment);

    CDOID id = CDOIDUtil.read(uriFragment);
    System.out.println(id);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testSetID() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    CDOID idToForce = CDOUtil.getCDOObject(company).cdoID();
    CDOBranch newBranch = transaction.getBranch().createBranch(getBranchName("new"));
    transaction.setBranch(newBranch);

    resource.getContents().clear();
    transaction.commit();

    Company company2 = getModel1Factory().createCompany();
    resource.getContents().add(company2);

    CDOTransactionImpl.resurrectObject(CDOUtil.getCDOObject(company2), idToForce);
    transaction.commit();

    CDOSession sessionB = openSession();
    CDOTransaction transactionB = sessionB.openTransaction(newBranch);
    CDOResource resourceB = transactionB.getResource(getResourcePath("/test1"));
    Company companyB = (Company)resourceB.getContents().get(0);
    assertEquals(idToForce, CDOUtil.getCDOObject(companyB).cdoID());
  }

  @CleanRepositoriesBefore(reason = "New transaction seems to need a clean repo be restarted")
  @Requires({ IRepositoryConfig.CAPABILITY_BRANCHING, IRepositoryConfig.CAPABILITY_RESTARTABLE })
  public void testSetIDWithReferences() throws Exception
  {
    String newPath = getBranchName("new");
    CDOID idToForce1;
    CDOID idToForce2;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Company company = getModel1Factory().createCompany();
      Category category = getModel1Factory().createCategory();
      company.getCategories().add(category);
      resource.getContents().add(company);
      transaction.commit();

      idToForce1 = CDOUtil.getCDOObject(company).cdoID();
      idToForce2 = CDOUtil.getCDOObject(category).cdoID();
      CDOBranch newBranch = transaction.getBranch().createBranch(newPath);
      transaction.setBranch(newBranch);

      resource.getContents().clear();
      transaction.commit();

      Company company2 = getModel1Factory().createCompany();
      Category category2 = getModel1Factory().createCategory();
      company2.getCategories().add(category2);
      resource.getContents().add(company2);

      CDOTransactionImpl.resurrectObject(CDOUtil.getCDOObject(company2), idToForce1);
      CDOTransactionImpl.resurrectObject(CDOUtil.getCDOObject(category2), idToForce2);
      transaction.commit();
      session.close();
    }

    restartRepository();

    CDOSession sessionB = openSession();
    CDOBranch newBranch = sessionB.getBranchManager().getBranch("MAIN/" + newPath);

    CDOTransaction transactionB = sessionB.openTransaction(newBranch);
    CDOResource resourceB = transactionB.getResource(getResourcePath("/test1"));
    Company companyB = (Company)resourceB.getContents().get(0);
    Category categoryB = companyB.getCategories().get(0);

    CDOID companyID = CDOUtil.getCDOObject(companyB).cdoID();
    assertEquals(idToForce1, companyID);

    CDOID categoryID = CDOUtil.getCDOObject(categoryB).cdoID();
    assertEquals(idToForce2, categoryID);

    Object containerID = CDOUtil.getCDOObject(categoryB).cdoRevision().data().getContainerID();
    assertEquals(companyID, containerID);
  }
}
