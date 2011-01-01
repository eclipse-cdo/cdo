/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ref.KeyedReference;
import org.eclipse.net4j.util.ref.KeyedSoftReference;
import org.eclipse.net4j.util.ref.KeyedStrongReference;
import org.eclipse.net4j.util.ref.ReferenceQueueWorker;

import org.eclipse.emf.ecore.EClass;

import java.lang.ref.Reference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class CDORevisionCacheImpl extends ReferenceQueueWorker<InternalCDORevision> implements InternalCDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionCacheImpl.class);

  private static boolean disableGC;

  private Map<CDOIDAndBranch, RevisionList> revisionLists = new HashMap<CDOIDAndBranch, RevisionList>();

  public CDORevisionCacheImpl()
  {
  }

  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return new CDORevisionCacheImpl();
  }

  public EClass getObjectType(CDOID id)
  {
    synchronized (revisionLists)
    {
      for (Entry<CDOIDAndBranch, RevisionList> entry : revisionLists.entrySet())
      {
        if (id.equals(entry.getKey().getID()))
        {
          RevisionList revisionList = entry.getValue();
          EClass type = revisionList.getObjectType();
          if (type != null)
          {
            return type;
          }
        }
      }
    }

    return null;
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    RevisionList revisionList = getRevisionList(id, branchPoint.getBranch());
    if (revisionList != null)
    {
      return revisionList.getRevision(branchPoint.getTimeStamp());
    }

    return null;
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    RevisionList revisionList = getRevisionList(id, branchVersion.getBranch());
    if (revisionList != null)
    {
      return revisionList.getRevisionByVersion(branchVersion.getVersion());
    }

    return null;
  }

  public List<CDORevision> getCurrentRevisions()
  {
    List<CDORevision> currentRevisions = new ArrayList<CDORevision>();
    synchronized (revisionLists)
    {
      for (RevisionList revisionList : revisionLists.values())
      {
        InternalCDORevision revision = revisionList.getRevision(CDORevision.UNSPECIFIED_DATE);
        if (revision != null)
        {
          currentRevisions.add(revision);
        }
      }
    }

    return currentRevisions;
  }

  public void addRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");
    CDOIDAndBranch key = CDOIDUtil.createIDAndBranch(revision.getID(), revision.getBranch());
    synchronized (revisionLists)
    {
      RevisionList list = revisionLists.get(key);
      if (list == null)
      {
        list = new RevisionList();
        revisionLists.put(key, list);
      }

      InternalCDORevision rev = (InternalCDORevision)revision;
      list.addRevision(rev, createReference(key, rev));
    }
  }

  public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    CDOIDAndBranch key = CDOIDUtil.createIDAndBranch(id, branchVersion.getBranch());
    synchronized (revisionLists)
    {
      RevisionList list = revisionLists.get(key);
      if (list != null)
      {
        list.removeRevision(branchVersion.getVersion());
        if (list.isEmpty())
        {
          revisionLists.remove(key);
          if (TRACER.isEnabled())
          {
            TRACER.format("Removed cache list of {0}", key); //$NON-NLS-1$
          }
        }
      }
    }

    return null;
  }

  public void clear()
  {
    synchronized (revisionLists)
    {
      revisionLists.clear();
    }
  }

  @Override
  public String toString()
  {
    synchronized (revisionLists)
    {
      return revisionLists.toString();
    }
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    Map<CDOBranch, List<CDORevision>> result = new HashMap<CDOBranch, List<CDORevision>>();
    synchronized (revisionLists)
    {
      for (RevisionList list : revisionLists.values())
      {
        list.getAllRevisions(result);
      }
    }

    return result;
  }

  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint)
  {
    List<CDORevision> result = new ArrayList<CDORevision>();
    CDOBranch branch = branchPoint.getBranch();
    synchronized (revisionLists)
    {
      for (Map.Entry<CDOIDAndBranch, RevisionList> entry : revisionLists.entrySet())
      {
        if (ObjectUtil.equals(entry.getKey().getBranch(), branch))
        {
          RevisionList list = entry.getValue();
          InternalCDORevision revision = list.getRevision(branchPoint.getTimeStamp());
          if (revision != null)
          {
            result.add(revision);
          }
        }
      }
    }

    return result;
  }

  @Override
  protected void work(Reference<? extends InternalCDORevision> reference)
  {
    @SuppressWarnings("unchecked")
    KeyedReference<CDORevisionKey, InternalCDORevision> keyedRef = (KeyedReference<CDORevisionKey, InternalCDORevision>)reference;
    CDORevisionKey key = keyedRef.getKey();

    CDOID id = key.getID();
    CDOBranch branch = key.getBranch();
    int version = key.getVersion();

    InternalCDORevision revision = removeRevision(id, branch.getVersion(version));
    if (revision == null)
    {
      // Use revision in eviction event
      key = revision;
    }

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new EvictionEventImpl(this, key), listeners);
    }
  }

  private KeyedReference<CDORevisionKey, InternalCDORevision> createReference(CDOIDAndBranch idAndBranch,
      InternalCDORevision revision)
  {
    CDORevisionKey key = new RevisionKey(idAndBranch, revision.getVersion());
    if (disableGC)
    {
      return new KeyedStrongReference<CDORevisionKey, InternalCDORevision>(key, revision);
    }

    return new KeyedSoftReference<CDORevisionKey, InternalCDORevision>(key, revision, getQueue());
  }

  private RevisionList getRevisionList(CDOID id, CDOBranch branch)
  {
    CDOIDAndBranch key = CDOIDUtil.createIDAndBranch(id, branch);
    synchronized (revisionLists)
    {
      return revisionLists.get(key);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RevisionKey implements CDORevisionKey
  {
    private CDOIDAndBranch idAndBranch;

    private int version;

    public RevisionKey(CDOIDAndBranch idAndBranch, int version)
    {
      this.idAndBranch = idAndBranch;
      this.version = version;
    }

    public CDOID getID()
    {
      return idAndBranch.getID();
    }

    public CDOBranch getBranch()
    {
      return idAndBranch.getBranch();
    }

    public int getVersion()
    {
      return version;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("{0}:{1}v{2}", getID(), getBranch().getID(), getVersion());
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RevisionList extends LinkedList<KeyedReference<CDORevisionKey, InternalCDORevision>>
  {
    private static final long serialVersionUID = 1L;

    public RevisionList()
    {
    }

    public synchronized EClass getObjectType()
    {
      for (Iterator<KeyedReference<CDORevisionKey, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          EClass type = revision.getEClass();
          if (type != null)
          {
            return type;
          }
        }

        it.remove();
      }

      return null;
    }

    public synchronized InternalCDORevision getRevision(long timeStamp)
    {
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = isEmpty() ? null : getFirst();
        if (ref != null)
        {
          InternalCDORevision revision = ref.get();
          if (revision != null)
          {
            if (!revision.isHistorical())
            {
              return revision;
            }
          }
          else
          {
            removeFirst();
          }
        }

        return null;
      }

      for (Iterator<KeyedReference<CDORevisionKey, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          long created = revision.getTimeStamp();
          if (created <= timeStamp)
          {
            long revised = revision.getRevised();
            if (timeStamp <= revised || revised == CDORevision.UNSPECIFIED_DATE)
            {
              return revision;
            }

            break;
          }
        }
        else
        {
          it.remove();
        }
      }

      return null;
    }

    public synchronized InternalCDORevision getRevisionByVersion(int version)
    {
      for (Iterator<KeyedReference<CDORevisionKey, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          int v = revision.getVersion();
          if (v == version)
          {
            return revision;
          }
          else if (v < version)
          {
            break;
          }
        }
        else
        {
          it.remove();
        }
      }

      return null;
    }

    public synchronized boolean addRevision(InternalCDORevision revision,
        KeyedReference<CDORevisionKey, InternalCDORevision> reference)
    {
      int version = revision.getVersion();
      for (ListIterator<KeyedReference<CDORevisionKey, InternalCDORevision>> it = listIterator(); it.hasNext();)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = it.next();
        InternalCDORevision foundRevision = ref.get();
        if (foundRevision != null)
        {
          CDORevisionKey key = ref.getKey();
          int v = key.getVersion();
          if (v == version)
          {
            return false;
          }

          if (v < version)
          {
            it.previous();
            it.add(reference);
            return true;
          }
        }
        else
        {
          it.remove();
        }
      }

      addLast(reference);
      return true;
    }

    public synchronized void removeRevision(int version)
    {
      for (Iterator<KeyedReference<CDORevisionKey, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = it.next();
        CDORevisionKey key = ref.getKey();
        int v = key.getVersion();
        if (v == version)
        {
          it.remove();
          if (TRACER.isEnabled())
          {
            TRACER.format("Removed version {0} from cache list of {1}", version, key.getID()); //$NON-NLS-1$
          }

          break;
        }
        else if (v < version)
        {
          break;
        }
      }
    }

    @Override
    public String toString()
    {
      StringBuffer buffer = new StringBuffer();
      for (Iterator<KeyedReference<CDORevisionKey, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (buffer.length() == 0)
        {
          buffer.append("{");
        }
        else
        {
          buffer.append(", ");
        }

        buffer.append(revision);
      }

      buffer.append("}");
      return buffer.toString();
    }

    public void getAllRevisions(Map<CDOBranch, List<CDORevision>> result)
    {
      for (Iterator<KeyedReference<CDORevisionKey, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDORevisionKey, InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          CDOBranch branch = revision.getBranch();
          List<CDORevision> resultList = result.get(branch);
          if (resultList == null)
          {
            resultList = new ArrayList<CDORevision>(1);
            result.put(branch, resultList);
          }

          resultList.add(revision);
        }
      }
    }
  }
}
