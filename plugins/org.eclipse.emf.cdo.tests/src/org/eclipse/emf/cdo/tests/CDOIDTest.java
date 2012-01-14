/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.id.CDOIDNullImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDObjectLongImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * @author Stefan Winkler
 */
public class CDOIDTest extends AbstractCDOTest
{
  public void testGetLong_Null()
  {
    assertEquals(AbstractCDOIDLong.NULL_VALUE, CDOIDUtil.getLong(null));
  }

  public void testGetLong_NullId()
  {
    CDOIDNullImpl id = CDOIDNullImpl.INSTANCE;
    assertEquals(AbstractCDOIDLong.NULL_VALUE, CDOIDUtil.getLong(id));
  }

  public void testGetLong_LongId()
  {
    CDOIDObjectLongImpl id = new CDOIDObjectLongImpl(123L);
    assertEquals(123L, CDOIDUtil.getLong(id));
  }

  public void testGetLong_TempId()
  {
    CDOIDTempObjectImpl id = new CDOIDTempObjectImpl(456);
    assertIllegalArgument(id);
  }

  public void testGetLong_ExtTempId()
  {
    CDOIDTempObjectExternalImpl id = new CDOIDTempObjectExternalImpl("cdo://repo123/resource456");
    assertIllegalArgument(id);
  }

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
}
