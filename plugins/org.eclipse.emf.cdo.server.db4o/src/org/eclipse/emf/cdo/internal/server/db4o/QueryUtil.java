/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.db4o;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * @author Victor Roldan Betancort
 */
public final class QueryUtil
{

  public static PrimitiveRevision getLastPrimitiveRevision(ObjectContainer container, int branchId, CDOID id)
  {
    ObjectSet<?> set = getRevisionById(container, branchId, id);

    PrimitiveRevision lastRevision = null;
    for (Object value : set)
    {
      PrimitiveRevision rev = (PrimitiveRevision)value;
      if (lastRevision == null)
      {
        lastRevision = rev;
      }
      if (rev.getVersion() > lastRevision.getVersion())
      {
        lastRevision = rev;
      }
    }
    return lastRevision;
  }

  public static PrimitiveRevision getPrimitiveRevisionByVersion(ObjectContainer container, CDOID id, int branchId,
      int version)
  {
    ObjectSet<?> set = getRevisionById(container, branchId, id);
    for (Object value : set)
    {
      PrimitiveRevision rev = (PrimitiveRevision)value;
      if (rev.getVersion() == version)
      {
        return rev;
      }
    }
    return null;
  }

  private static ObjectSet<?> getRevisionById(ObjectContainer container, int branchId, CDOID id)
  {
    Query query = container.query();
    query.constrain(PrimitiveRevision.class);
    query.descend("id").constrain(CDOIDUtil.getLong(id));
    query.descend("branchID").constrain(branchId);
    ObjectSet<?> set = query.execute();
    return set;
  }

  public static void removeRevisionFromContainer(ObjectContainer container, int branchId, CDOID id)
  {
    PrimitiveRevision primitiveRevision = QueryUtil.getLastPrimitiveRevision(container, branchId, id);
    if (primitiveRevision != null)
    {
      container.delete(primitiveRevision);
    }
    else
    {
      throw new IllegalArgumentException("Revision with ID " + id + " not found, cannot detach");
    }
  }

}
