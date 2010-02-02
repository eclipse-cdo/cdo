/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.internal.common.revision.cache.lru;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.revision.cache.EvictionEventImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LRURevisionCache extends Lifecycle implements InternalCDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, LRURevisionCache.class);

  private Map<CDOID, RevisionHolder> revisions = new HashMap<CDOID, RevisionHolder>();

  private int capacityCurrent;

  private int capacityRevised;

  private LRU currentLRU;

  private LRU revisedLRU;

  public LRURevisionCache()
  {
  }

  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    LRURevisionCache cache = new LRURevisionCache();
    cache.setCapacityCurrent(capacityCurrent);
    cache.setCapacityRevised(capacityRevised);
    return cache;
  }

  public boolean isSupportingBranches()
  {
    return false;
  }

  public int getCapacityCurrent()
  {
    return capacityCurrent;
  }

  /**
   * Sets the capacity of LRU cache for <em>current</em> revisions. A value of zero disables eviction completely such
   * that the cache will grow indefinetely.
   */
  public void setCapacityCurrent(int capacity)
  {
    capacityCurrent = capacity;
    if (currentLRU != null)
    {
      currentLRU.capacity(capacity);
    }
  }

  public int getCapacityRevised()
  {
    return capacityRevised;
  }

  /**
   * Sets the capacity of LRU cache for old (<em>revised</em>) revisions. A value of zero disables eviction completely
   * such that the cache will grow indefinetely.
   */
  public void setCapacityRevised(int capacity)
  {
    capacityRevised = capacity;
    if (revisedLRU != null)
    {
      revisedLRU.capacity(capacity);
    }
  }

  public synchronized List<CDORevision> getCurrentRevisions()
  {
    List<CDORevision> currentRevisions = new ArrayList<CDORevision>();
    for (RevisionHolder holder : revisions.values())
    {
      InternalCDORevision revision = holder.getRevision();
      if (revision != null && !revision.isHistorical())
      {
        currentRevisions.add(revision);
      }
    }

    return currentRevisions;
  }

  public synchronized EClass getObjectType(CDOID id)
  {
    RevisionHolder holder = getHolder(id);
    if (holder == null)
    {
      return null;
    }

    InternalCDORevision revision = holder.getRevision();
    return revision.getEClass();
  }

  public synchronized InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    RevisionHolder holder = getHolder(id);
    return getRevision(holder, branchPoint.getTimeStamp());
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    RevisionHolder holder = getHolder(id);
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      int version = branchVersion.getVersion();
      if (holderVersion > version)
      {
        holder = holder.getNext();
      }
      else if (holderVersion == version)
      {
        return holder.getRevision();
      }
      else
      {
        break;
      }
    }

    return null;
  }

  public synchronized boolean addRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");
    if (TRACER.isEnabled())
    {
      TRACER
          .format(
              "Adding revision: {0}, timeStamp={1,date} {1,time,HH:mm:ss:SSS}, revised={2,date} {2,time,HH:mm:ss:SSS}, historical={3}", //$NON-NLS-1$
              revision, revision.getTimeStamp(), revision.getRevised(), revision.isHistorical());
    }

    int version = revision.getVersion();
    RevisionHolder lastHolder = null;
    RevisionHolder holder = getHolder(revision.getID());
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      if (holderVersion > version)
      {
        lastHolder = holder;
        holder = holder.getNext();
      }
      else if (holderVersion == version)
      {
        return false;
      }
      else
      {
        break;
      }
    }

    // Create holder only if require
    RevisionHolder newHolder = createHolder((InternalCDORevision)revision);
    LRU list = revision.isHistorical() ? revisedLRU : currentLRU;
    list.add((DLRevisionHolder)newHolder);

    adjustHolder((InternalCDORevision)revision, newHolder, lastHolder, holder);
    return true;
  }

  public synchronized InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    InternalCDORevision revision = null;
    RevisionHolder holder = getHolder(id);
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      int version = branchVersion.getVersion();
      if (holderVersion > version)
      {
        holder = holder.getNext();
      }
      else
      {
        if (holderVersion == version)
        {
          revision = holder.getRevision();
          LRU list = revision.isHistorical() ? revisedLRU : currentLRU;
          list.remove((DLRevisionHolder)holder);
          removeHolder(holder);
        }

        holder = null;
      }
    }

    return revision;
  }

  public synchronized boolean removeRevisions(CDOID id, CDOBranch branch)
  {
    RevisionHolder lookupHolder = getHolder(id);
    RevisionHolder holder = lookupHolder;
    while (holder != null)
    {
      RevisionHolder nextHolder = holder.getNext();
      removeHolder(holder);
      holder = nextHolder;
    }

    return lookupHolder != null;
  }

  public synchronized void clear()
  {
    revisions.clear();
    currentLRU = new LRU(capacityCurrent);
    revisedLRU = new LRU(capacityRevised);
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    throw new UnsupportedOperationException();
  }

  private InternalCDORevision getRevision(RevisionHolder holder, long timeStamp)
  {
    while (holder != null)
    {
      int indicator = holder.compareTo(timeStamp);
      if (indicator == 1)
      {
        // timeStamp is after holder timeSpan
        holder = holder.getNext();
      }
      else if (indicator == 0)
      {
        // timeStamp is within holder timeSpan
        return holder.getRevision();
      }
      else
      {
        // timeStamp is before holder timeSpan
        break;
      }
    }

    return null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    currentLRU = new LRU(capacityCurrent);
    revisedLRU = new LRU(capacityRevised);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    currentLRU = null;
    revisedLRU = null;
    super.doDeactivate();
  }

  public final synchronized RevisionHolder getHolder(CDOID id)
  {
    return revisions.get(id);
  }

  protected RevisionHolder createHolder(InternalCDORevision revision)
  {
    return new LRURevisionHolder(revision);
  }

  private void adjustHolder(InternalCDORevision revision, RevisionHolder holder, RevisionHolder prevHolder,
      RevisionHolder nextHolder)
  {
    if (prevHolder != null)
    {
      if (nextHolder == null)
      {
        nextHolder = prevHolder.getNext();
      }

      holder.setPrev(prevHolder);
      holder.setNext(nextHolder);
      prevHolder.setNext(holder);
    }
    else
    {
      holder.setNext(nextHolder);
      revisions.put(revision.getID(), holder);
    }

    reviseHolder(holder, nextHolder);
  }

  private void reviseHolder(RevisionHolder holder, RevisionHolder nextHolder)
  {
    if (nextHolder != null)
    {
      nextHolder.setPrev(holder);
      if (holder.isCurrent() && nextHolder.isCurrent())
      {
        currentLRU.remove((DLRevisionHolder)nextHolder);

        InternalCDORevision oldRevision = nextHolder.getRevision();

        if (oldRevision != null && oldRevision.getRevised() == CDORevision.UNSPECIFIED_DATE
            && holder.getCreated() > CDORevision.UNSPECIFIED_DATE
            && oldRevision.getVersion() == holder.getVersion() - 1)
        {
          revisedLRU.add((DLRevisionHolder)nextHolder);
          oldRevision.setRevised(holder.getCreated() - 1);
        }
        else
        {
          removeHolder(nextHolder);
        }
      }
    }
  }

  private synchronized void removeHolder(RevisionHolder holder)
  {
    CDOID id = holder.getID();
    RevisionHolder prev = holder.getPrev();
    RevisionHolder next = holder.getNext();
    if (next != null)
    {
      next.setPrev(prev);
    }

    if (prev != null)
    {
      prev.setNext(next);
    }
    else
    {
      if (next != null)
      {
        revisions.put(id, next);
      }
      else
      {
        revisions.remove(id);
      }
    }

    holder.setPrev(null);
    holder.setNext(null);
  }

  /**
   * @author Eike Stepper
   */
  private final class LRU extends LRURevisionList
  {
    public LRU(int capacity)
    {
      super(capacity);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("LRU[size={0}, capacity={1}]", size(), capacity()); //$NON-NLS-1$
    }

    @Override
    protected void evict(LRURevisionHolder holder)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Evicting revision {0}v{1}", holder.getID(), holder.getVersion()); //$NON-NLS-1$
      }

      // Remember some values before the holder may be changed
      InternalCDORevision revision = holder.getRevision();
      boolean revised = !holder.isCurrent();

      super.evict(holder);
      removeHolder(holder);

      if (revision != null)
      {
        if (this == currentLRU && revised)
        {
          addRevision(revision);
        }
        else
        {
          IListener[] listeners = getListeners();
          if (listeners != null)
          {
            fireEvent(new EvictionEventImpl(LRURevisionCache.this, revision), listeners);
          }
        }
      }
    }
  }
}
