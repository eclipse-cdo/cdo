/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233273    
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.resource.CDOPathFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IMEMStore;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class MEMStore extends LongIDStore implements IMEMStore
{
  public static final String TYPE = "mem";

  private Map<CDOID, List<CDORevision>> revisions = new HashMap<CDOID, List<CDORevision>>();

  private int listLimit;

  /**
   * @param listLimit
   *          See {@link #setListLimit(int)}.
   * @since 2.0
   */
  public MEMStore(int listLimit)
  {
    super(TYPE);
    this.listLimit = listLimit;
  }

  public MEMStore()
  {
    this(UNLIMITED);
  }

  /**
   * @since 2.0
   */
  public int getListLimit()
  {
    return listLimit;
  }

  /**
   * @since 2.0
   */
  public synchronized void setListLimit(int listLimit)
  {
    if (listLimit != UNLIMITED && this.listLimit != listLimit)
    {
      for (List<CDORevision> list : revisions.values())
      {
        enforceListLimit(list);
      }
    }

    this.listLimit = listLimit;
  }

  /**
   * @since 2.0
   */
  public synchronized List<CDORevision> getRevisions()
  {
    ArrayList<CDORevision> simpleRevisions = new ArrayList<CDORevision>();
    Iterator<List<CDORevision>> itr = revisions.values().iterator();
    while (itr.hasNext())
    {
      List<CDORevision> list = itr.next();
      CDORevision revision = list.get(list.size() - 1);
      simpleRevisions.add(revision);
    }

    return simpleRevisions;
  }

  public synchronized CDORevision getRevision(CDOID id)
  {
    List<CDORevision> list = revisions.get(id);
    if (list != null)
    {
      return list.get(list.size() - 1);
    }

    return null;
  }

  public synchronized CDORevision getRevisionByVersion(CDOID id, int version)
  {
    List<CDORevision> list = revisions.get(id);
    if (list != null)
    {
      return getRevisionByVersion(list, version);
    }

    return null;
  }

  public synchronized void addRevision(CDORevision revision)
  {
    CDOID id = revision.getID();
    List<CDORevision> list = revisions.get(id);
    if (list == null)
    {
      list = new ArrayList<CDORevision>();
      revisions.put(id, list);
    }

    CDORevision rev = getRevisionByVersion(list, revision.getVersion());
    if (rev != null)
    {
      throw new IllegalStateException("Concurrent modification of revision " + rev);
    }

    list.add(revision);
    if (listLimit != UNLIMITED)
    {
      enforceListLimit(list);
    }
  }

  private void enforceListLimit(List<CDORevision> list)
  {
    while (list.size() > listLimit)
    {
      list.remove(0);
    }
  }

  public synchronized boolean removeRevision(CDORevision revision)
  {
    CDOID id = revision.getID();
    List<CDORevision> list = revisions.get(id);
    if (list == null)
    {
      return false;
    }

    for (Iterator<CDORevision> it = list.iterator(); it.hasNext();)
    {
      CDORevision rev = it.next();
      if (rev.getVersion() == revision.getVersion())
      {
        it.remove();
        return true;
      }
    }

    return false;
  }

  public synchronized CDORevision getResource(String path)
  {
    CDOPathFeature pathFeature = getRepository().getPackageManager().getCDOResourcePackage().getCDOResourceClass()
        .getCDOPathFeature();
    for (List<CDORevision> list : revisions.values())
    {
      if (!list.isEmpty())
      {
        CDORevision revision = list.get(0);
        if (revision.isResource())
        {
          String p = (String)revision.getData().get(pathFeature, 0);
          if (ObjectUtil.equals(p, path))
          {
            return revision;
          }
        }
      }
    }

    return null;
  }

  @Override
  public boolean hasBranchingSupport()
  {
    return false;
  }

  @Override
  public boolean hasWriteDeltaSupport()
  {
    return true;
  }

  @Override
  public boolean hasAuditingSupport()
  {
    return true;
  }

  public void repairAfterCrash()
  {
    // Do nothing
  }

  @Override
  public MEMStoreAccessor createReader(ISession session)
  {
    return new MEMStoreAccessor(this, session);
  }

  @Override
  public MEMStoreAccessor createWriter(IView view)
  {
    return new MEMStoreAccessor(this, view);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    revisions.clear();
    super.doDeactivate();
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    // Pooling of store accessors not supported
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    // Pooling of store accessors not supported
    return null;
  }

  private CDORevision getRevisionByVersion(List<CDORevision> list, int version)
  {
    for (CDORevision revision : list)
    {
      if (revision.getVersion() == version)
      {
        return revision;
      }
    }

    return null;
  }
}
