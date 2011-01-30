/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.utils;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyProxy;

import com.objy.db.app.ooId;

/**
 * TODO - this file was taken from the old code without verification for
 *        all its functionality's requirement to the new code.
 *
 *      - We might need to cleanup the various CDOID transformations.
 *
 *      * The idea is to convert the OID parts into long value, except that
 *        we only use the 6-bits from the DB, the rest is used to mark
 *        the OID as
 */

/**
 * @author Simon McDuff
 */
public class OBJYCDOIDUtil
{

  public static CDOID createLong(long value)
  {
    if (value == 0L)
    {
      return CDOID.NULL;
    }
    return CDOIDUtil.createLong(value);
  }

  public static CDOID getCDOID(ooId id)
  {
    return createLong(getLong(id));
  }

  public static CDOID getCDOID(long id)
  {
    return createLong(id);
  }

  public static long getLong(ooId id)
  {
    long value = 0;

    if (id != null)
    {
      value = (long)id.getSlot() << 48 | (long)id.getPage() << 32 | (long)id.getOC() << 16 | id.getDB();
    }

    return value;
  }

  public static long addProxy(long ooid)
  {
    return ooid | (long)1 << 63;
  }

  public static long removeProxy(long ooid)
  {
    return ooid << 1 >>> 1;
  }

  public static boolean isProxy(long ooid)
  {
    return ooid >>> 63 == 1;
  }

  public static long adaptOOID(long ooid)
  {
    return removeProxy(ooid);
  }

  // 2.0 code
  public static boolean isValidObjyId(CDOID id)
  {
    if (id instanceof CDOIDObject)
    {
      try
      {
        return CDOIDUtil.getLong(id) > 1717828929;
      }
      catch (Exception ignore)
      {
        // Fall through
      }
    }

    return false;
  }

  public static ooId getooId(long longCdoID)
  {
    int slot = (int)(longCdoID >> 48 & 0xFFFF);
    int page = (int)(longCdoID >> 32 & 0xFFFF);
    int OC = (int)(longCdoID >> 16 & 0xFFFF);
    int DB = (int)(longCdoID & 0xFFFF);
    return new com.objy.pm.ooId(DB, OC, page, slot, 0);
  }

  public static ooId getContainerId(long longCdoID)
  {
    int slot = 1;
    int page = 1;
    int OC = (int)(longCdoID >> 16 & 0xFFFF);
    int DB = (int)(longCdoID & 0xFFFF);
    return new com.objy.pm.ooId(DB, OC, page, slot, 0);
  }

  public static ooId getooId(CDOID id)
  {
    long longCdoID = CDOIDUtil.getLong(id);
    return getooId(longCdoID);
  }

  public static ooId getContainerId(CDOID id)
  {
    long longCdoID = org.eclipse.emf.cdo.common.id.CDOIDUtil.getLong(id);
    return getContainerId(longCdoID);
  }

  public static CDOIDExternal createCDIDExternal(ObjyProxy proxyObject)
  {
    return CDOIDUtil.createExternal(proxyObject.getUri());
  }
}
