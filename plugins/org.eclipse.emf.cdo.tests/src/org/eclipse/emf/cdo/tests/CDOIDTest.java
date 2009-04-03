/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalTempImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDNullImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempMetaImpl;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectImpl;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDLong;
import org.eclipse.emf.cdo.spi.common.id.CDOIDLongImpl;

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
    CDOIDLongImpl id = new CDOIDLongImpl(123L);
    assertEquals(123L, CDOIDUtil.getLong(id));
  }

  public void testGetLong_MetaId()
  {
    CDOIDMetaImpl id = new CDOIDMetaImpl(135L);
    assertEquals(135, CDOIDUtil.getLong(id));
  }

  public void testGetLong_TempId()
  {
    CDOIDTempObjectImpl id = new CDOIDTempObjectImpl(456);
    assertIllegalArgument(id);
  }

  public void testGetLong_TempMetaId()
  {
    CDOIDTempMetaImpl id = new CDOIDTempMetaImpl(789);
    assertIllegalArgument(id);
  }

  public void testGetLong_ExtTempId()
  {
    CDOIDExternalTempImpl id = new CDOIDExternalTempImpl("cdo://repo123/resource456");
    assertIllegalArgument(id);
  }

  public void testGetLong_ExtId()
  {
    CDOIDExternalImpl id = new CDOIDExternalImpl("cdo://repo123/resource456");
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

}
