/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * @author Victor Roldan Betancort
 */
public final class InternalCDODBUtil
{
  /**
   * @author Eike Stepper
   */
  public static long convertCDOIDToLong(IExternalReferenceManager manager, IDBStoreAccessor accessor, CDOID id)
  {
    if (id.getType() == CDOID.Type.EXTERNAL_OBJECT)
    {
      return manager.mapExternalReference(accessor, (CDOIDExternal)id);
    }

    return CDOIDUtil.getLong(id);
  }

  /**
   * @author Eike Stepper
   */
  public static CDOID convertLongToCDOID(IExternalReferenceManager manager, IDBStoreAccessor accessor, long id)
  {
    if (id < 0)
    {
      return manager.unmapExternalReference(accessor, id);
    }

    return CDOIDUtil.createLong(id);
  }
}
