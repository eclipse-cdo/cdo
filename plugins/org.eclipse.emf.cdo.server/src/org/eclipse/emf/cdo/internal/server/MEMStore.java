/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.model.resource.CDOPathFeature;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
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
public class MEMStore extends LongIDStore
{
  public static final String TYPE = "mem";

  private Map<CDOID, List<CDORevision>> revisions = new HashMap<CDOID, List<CDORevision>>();

  // {
  // private static final long serialVersionUID = 1L;
  //
  // @Override
  // public String toString()
  // {
  // List<Entry<CDOID, List<CDORevision>>> entries = new ArrayList<Entry<CDOID, List<CDORevision>>>(entrySet());
  // Collections.sort(entries, new Comparator<Entry<CDOID, List<CDORevision>>>()
  // {
  // public int compare(Entry<CDOID, List<CDORevision>> o1, Entry<CDOID, List<CDORevision>> o2)
  // {
  // return o1.getKey().compareTo(o2.getKey());
  // }
  // });
  //
  // StringBuilder builder = new StringBuilder();
  // for (Entry<CDOID, List<CDORevision>> entry : entries)
  // {
  // builder.append(entry.getKey());
  // builder.append(" -->");
  // for (CDORevision revision : entry.getValue())
  // {
  // builder.append(" ");
  // builder.append(revision);
  // }
  //
  // builder.append("\n");
  // }
  //
  // return builder.toString();
  // }
  // };

  public MEMStore()
  {
    super(TYPE);
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

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasWriteDeltaSupport()
  {
    return true;
  }

  public boolean hasAuditingSupport()
  {
    return true;
  }

  public void repairAfterCrash()
  {
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
