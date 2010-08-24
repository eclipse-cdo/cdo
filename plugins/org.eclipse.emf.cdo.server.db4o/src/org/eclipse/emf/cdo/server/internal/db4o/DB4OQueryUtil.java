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
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/**
 * @author Victor Roldan Betancort
 */
public final class DB4OQueryUtil
{
  public static DB4ORevision getRevision(ObjectContainer container, CDOID id, int branchID)
  {
    // TODO Optimize this loop away
    DB4ORevision result = null;
    for (Object value : getRevisions(container, id, branchID))
    {
      DB4ORevision revision = (DB4ORevision)value;
      if (result == null)
      {
        result = revision;
      }

      if (revision.getVersion() > result.getVersion())
      {
        result = revision;
      }
    }

    return result;
  }

  public static DB4ORevision getRevisionByVersion(ObjectContainer container, CDOID id, int branchID, int version)
  {
    // TODO Optimize this loop away
    for (Object value : getRevisions(container, id, branchID))
    {
      DB4ORevision revision = (DB4ORevision)value;
      if (revision.getVersion() == version)
      {
        return revision;
      }
    }

    return null;
  }

  private static ObjectSet<?> getRevisions(ObjectContainer container, CDOID id, int branchID)
  {
    Query query = container.query();
    query.constrain(DB4ORevision.class);
    query.descend("id").constrain(CDOIDUtil.getLong(id));
    query.descend("branchID").constrain(branchID);
    return query.execute();
  }

  public static void removeRevisionFromContainer(ObjectContainer container, int branchID, CDOID id)
  {
    DB4ORevision primitiveRevision = DB4OQueryUtil.getRevision(container, id, branchID);
    if (primitiveRevision == null)
    {
      throw new IllegalArgumentException("Revision with ID " + id + " not found");
    }

    container.delete(primitiveRevision);
  }
}
