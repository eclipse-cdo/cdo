/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class MEMStore extends Store
{
  public static final String TYPE = "mem";

  private Map<CDOID, List<CDORevision>> mapOfRevisions = new HashMap<CDOID, List<CDORevision>>();

  public MEMStore()
  {
    super(TYPE);
  }

  public synchronized CDORevision getRevision(CDOID rev)
  {
    List<CDORevision> list = getList(rev);
    if (list.size() == 0)
    {
      return null;
    }

    return list.get(list.size() - 1);
  }

  public synchronized List<CDORevision> getList(CDOID rev)
  {
    List<CDORevision> list = mapOfRevisions.get(rev);
    if (list == null)
    {
      list = new ArrayList<CDORevision>();
      mapOfRevisions.put(rev, list);
    }

    return list;
  }

  public synchronized CDORevision getRevision(CDOID rev, int version)
  {
    List<CDORevision> list = getList(rev);

    for (CDORevision revision : list)
    {
      if (revision.getVersion() == version) return revision;
    }
    return null;
  }

  public synchronized void addRevision(CDORevision revision)
  {

    CDORevision rev = getRevision(revision.getID(), revision.getVersion());
    if (rev == null)
    {
      getList(revision.getID()).add(revision);
    }
  }

  public boolean hasWriteDeltaSupport()
  {
    return true;
  }

  public boolean hasAuditingSupport()
  {
    return true;
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasEfficientTypeLookup()
  {
    return true;
  }

  public void repairAfterCrash()
  {
  }

  public MEMStoreAccessor getReader(ISession session)
  {
    return new MEMStoreAccessor(this, session);
  }

  public MEMStoreAccessor getWriter(IView view)
  {
    return new MEMStoreAccessor(this, view);
  }
}
