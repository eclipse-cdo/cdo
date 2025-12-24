/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model6.MyEnum;
import org.eclipse.emf.cdo.tests.model6.MyEnumList;
import org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * @author Eike Stepper
 */
public class Bugzilla_322218_Test extends AbstractCDOTest
{
  public void testMyEnumList() throws Exception
  {
    MyEnumList myEnumList = getModel6Factory().createMyEnumList();
    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(myEnumList);

    transaction.commit();
  }

  public void testMyEnumList2() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumList myEnumList = getModel6Factory().createMyEnumList();
    resource.getContents().add(myEnumList);

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  public void testMyEnumList3() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumList myEnumList = getModel6Factory().createMyEnumList();
    resource.getContents().add(myEnumList);
    transaction.commit();

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumListPCL() throws Exception
  {
    MyEnumList myEnumList = getModel6Factory().createMyEnumList();
    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(myEnumList);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumList2PCL() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumList myEnumList = getModel6Factory().createMyEnumList();
    resource.getContents().add(myEnumList);

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumList3PCL() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumList myEnumList = getModel6Factory().createMyEnumList();
    resource.getContents().add(myEnumList);
    transaction.commit();

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumList3PCL_Reload() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));
      MyEnumList myEnumList = getModel6Factory().createMyEnumList();
      resource.getContents().add(myEnumList);
      transaction.commit();

      myEnumList.getMyEnum().add(MyEnum.ZERO);
      myEnumList.getMyEnum().add(MyEnum.ONE);
      myEnumList.getMyEnum().add(MyEnum.TWO);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("res"));

    MyEnumList myEnumList = (MyEnumList)resource.getContents().get(0);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  public void testMyEnumListUnsettable() throws Exception
  {
    MyEnumListUnsettable myEnumList = getModel6Factory().createMyEnumListUnsettable();
    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(myEnumList);

    transaction.commit();
  }

  public void testMyEnumListUnsettable2() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumListUnsettable myEnumList = getModel6Factory().createMyEnumListUnsettable();
    resource.getContents().add(myEnumList);

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  public void testMyEnumListUnsettable3() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumListUnsettable myEnumList = getModel6Factory().createMyEnumListUnsettable();
    resource.getContents().add(myEnumList);
    transaction.commit();

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumListUnsettablePCL() throws Exception
  {
    MyEnumListUnsettable myEnumList = getModel6Factory().createMyEnumListUnsettable();
    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(myEnumList);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumListUnsettable2PCL() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumListUnsettable myEnumList = getModel6Factory().createMyEnumListUnsettable();
    resource.getContents().add(myEnumList);

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumListUnsettable3PCL() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    MyEnumListUnsettable myEnumList = getModel6Factory().createMyEnumListUnsettable();
    resource.getContents().add(myEnumList);
    transaction.commit();

    myEnumList.getMyEnum().add(MyEnum.ZERO);
    myEnumList.getMyEnum().add(MyEnum.ONE);
    myEnumList.getMyEnum().add(MyEnum.TWO);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testMyEnumListUnsettable3PCL_Reload() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));
      MyEnumListUnsettable myEnumList = getModel6Factory().createMyEnumListUnsettable();
      resource.getContents().add(myEnumList);
      transaction.commit();

      myEnumList.getMyEnum().add(MyEnum.ZERO);
      myEnumList.getMyEnum().add(MyEnum.ONE);
      myEnumList.getMyEnum().add(MyEnum.TWO);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("res"));

    MyEnumListUnsettable myEnumList = (MyEnumListUnsettable)resource.getContents().get(0);
    myEnumList.getMyEnum().add(MyEnum.THREE);

    transaction.commit();
  }
}
