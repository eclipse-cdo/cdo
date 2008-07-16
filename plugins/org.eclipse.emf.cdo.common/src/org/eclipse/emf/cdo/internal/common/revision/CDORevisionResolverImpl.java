/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CDORevisionResolverImpl extends Lifecycle implements CDORevisionResolver
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionResolverImpl.class);

  private Map<CDOID, RevisionHolder> revisions = new HashMap<CDOID, RevisionHolder>();

  // {
  // private static final long serialVersionUID = 1L;
  //
  // @Override
  // public String toString()
  // {
  // List<Entry<CDOID, RevisionHolder>> entries = new ArrayList<Entry<CDOID, RevisionHolder>>(entrySet());
  // Collections.sort(entries, new Comparator<Entry<CDOID, RevisionHolder>>()
  // {
  // public int compare(Entry<CDOID, RevisionHolder> o1, Entry<CDOID, RevisionHolder> o2)
  // {
  // return o1.getKey().compareTo(o2.getKey());
  // }
  // });
  //
  // StringBuilder builder = new StringBuilder();
  // for (Entry<CDOID, RevisionHolder> entry : entries)
  // {
  // builder.append(entry.getKey());
  // builder.append(" -->");
  // RevisionHolder holder = entry.getValue();
  // while (holder != null)
  // {
  // builder.append(" ");
  // builder.append(holder.getRevision(false));
  // holder = holder.getNext();
  // }
  //
  // builder.append("\n");
  // }
  //
  // return builder.toString();
  // }
  // };

  private int currentLRUCapacity;

  private int revisedLRUCapacity;

  private LRU currentLRU;

  private LRU revisedLRU;

  public CDORevisionResolverImpl()
  {
  }

  public int getCurrentLRUCapacity()
  {
    return currentLRUCapacity;
  }

  public void setCurrentLRUCapacity(int capacity)
  {
    currentLRUCapacity = capacity;
    if (currentLRU != null)
    {
      currentLRU.capacity(capacity);
    }
  }

  public int getRevisedLRUCapacity()
  {
    return revisedLRUCapacity;
  }

  public void setRevisedLRUCapacity(int capacity)
  {
    revisedLRUCapacity = capacity;
    if (revisedLRU != null)
    {
      revisedLRU.capacity(capacity);
    }
  }

  public CDOClass getObjectType(CDOID id)
  {
    RevisionHolder holder = revisions.get(id);
    if (holder == null)
    {
      return null;
    }

    InternalCDORevision revision = (InternalCDORevision)holder.getRevision(true);
    return revision.getCDOClass();
  }

  public boolean containsRevision(CDOID id)
  {
    return revisions.containsKey(id);
  }

  public boolean containsRevisionByTime(CDOID id, long timeStamp)
  {
    return getRevisionByTime(id, 0, timeStamp, false) != null;
  }

  public boolean containsRevisionByVersion(CDOID id, int version)
  {
    return getRevisionByVersion(id, 0, version, false) != null;
  }

  public InternalCDORevision getRevision(CDOID id, int referenceChunk)
  {
    return getRevision(id, referenceChunk, true);
  }

  public List<CDORevision> getRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    List<CDOID> missingIDs = new ArrayList<CDOID>(0);
    List<CDORevision> revisions = new ArrayList<CDORevision>(ids.size());
    for (CDOID id : ids)
    {
      InternalCDORevision revision = getRevision(id, referenceChunk, false);
      revisions.add(revision);
      if (revision == null)
      {
        missingIDs.add(id);
      }
    }

    if (!missingIDs.isEmpty())
    {
      List<InternalCDORevision> missingRevisions = loadRevisions(missingIDs, referenceChunk);
      handleMissingRevisions(revisions, missingRevisions);
    }

    return revisions;
  }

  public InternalCDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    return getRevisionByTime(id, referenceChunk, timeStamp, true);
  }

  public List<CDORevision> getRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    List<CDOID> missingIDs = new ArrayList<CDOID>(0);
    List<CDORevision> revisions = new ArrayList<CDORevision>(ids.size());
    for (CDOID id : ids)
    {
      InternalCDORevision revision = getRevisionByTime(id, referenceChunk, timeStamp, false);
      revisions.add(revision);
      if (revision == null)
      {
        missingIDs.add(id);
      }
    }

    if (!missingIDs.isEmpty())
    {
      List<InternalCDORevision> missingRevisions = loadRevisions(missingIDs, referenceChunk);
      handleMissingRevisions(revisions, missingRevisions);
    }

    return revisions;
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return getRevisionByVersion(id, referenceChunk, version, true);
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version,
      boolean loadOnDemand)
  {
    RevisionHolder holder = revisions.get(id);
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      if (holderVersion > version)
      {
        holder = holder.getNext();
      }
      else if (holderVersion == version)
      {
        return (InternalCDORevision)holder.getRevision(true);
      }
      else
      {
        break;
      }
    }

    if (loadOnDemand)
    {
      InternalCDORevision revision = loadRevisionByVersion(id, referenceChunk, version);
      if (revision != null)
      {
        addRevision(revision);
        return revision;
      }
    }

    return null;
  }

  public boolean addRevision(InternalCDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding revision: {0}, created={1,date} {1,time}, revised={2,date} {2,time}, current={3}",
          revision, revision.getCreated(), revision.getRevised(), revision.isCurrent());
    }

    RevisionHolder newHolder = createHolder(revision);
    LRU list = revision.isCurrent() ? currentLRU : revisedLRU;
    list.add((DLRevisionHolder)newHolder);

    int version = revision.getVersion();
    RevisionHolder lastHolder = null;
    RevisionHolder holder = revisions.get(revision.getID());
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

    adjustHolder(revision, newHolder, lastHolder, holder);
    return true;
  }

  public InternalCDORevision getRevision(CDOID id, int referenceChunk, boolean loadOnDemand)
  {
    RevisionHolder holder = revisions.get(id);
    InternalCDORevision revision = holder == null ? null : (InternalCDORevision)holder.getRevision(true);
    if (revision == null || !revision.isCurrent())
    {
      if (loadOnDemand)
      {
        revision = loadRevision(id, referenceChunk);
        if (revision == null)
        {
          throw new IllegalStateException("Could not load revision for " + id);
        }

        addRevision(revision);
      }
      else
      {
        revision = null;
      }
    }
    else
    {
      InternalCDORevision oldRevision = revision;
      revision = verifyRevision(oldRevision, referenceChunk);
      if (revision != oldRevision)
      {
        addRevision(revision);
      }
    }

    return revision;
  }

  public synchronized InternalCDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp,
      boolean loadOnDemand)
  {
    RevisionHolder holder = revisions.get(id);
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
        InternalCDORevision oldRevision = (InternalCDORevision)holder.getRevision(true);
        InternalCDORevision revision = verifyRevision(oldRevision, referenceChunk);
        if (revision != oldRevision)
        {
          addRevision(revision);
        }

        return revision;
      }
      else
      {
        // timeStamp is before holder timeSpan
        break;
      }
    }

    if (loadOnDemand)
    {
      InternalCDORevision revision = loadRevisionByTime(id, referenceChunk, timeStamp);
      if (revision != null)
      {
        addRevision(revision);
        return revision;
      }
    }

    return null;
  }

  protected abstract InternalCDORevision loadRevision(CDOID id, int referenceChunk);

  protected abstract InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp);

  protected abstract InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version);

  protected abstract List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk);

  protected abstract List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk,
      long timeStamp);

  protected void handleMissingRevisions(List<CDORevision> revisions, List<InternalCDORevision> missingRevisions)
  {
    Iterator<InternalCDORevision> it = missingRevisions.iterator();
    for (int i = 0; i < revisions.size(); i++)
    {
      CDORevision revision = revisions.get(i);
      if (revision == null)
      {
        InternalCDORevision missingRevision = it.next();
        revisions.set(i, missingRevision);
        addRevision(missingRevision);
      }
    }
  }

  protected synchronized void removeRevision(CDOID id, int version)
  {
    RevisionHolder holder = revisions.get(id);
    while (holder != null)
    {
      int holderVersion = holder.getVersion();
      if (holderVersion > version)
      {
        holder = holder.getNext();
      }
      else
      {
        if (holderVersion == version)
        {
          removeHolder(holder);
        }

        holder = null;
      }
    }
  }

  protected InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    return revision;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    currentLRU = new LRU(currentLRUCapacity);
    revisedLRU = new LRU(revisedLRUCapacity);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    currentLRU = null;
    revisedLRU = null;
    super.doDeactivate();
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
        revisedLRU.add((DLRevisionHolder)nextHolder);

        InternalCDORevision oldRevision = (InternalCDORevision)nextHolder.getRevision(false);
        if (oldRevision != null)
        {
          oldRevision.setRevised(holder.getCreated() - 1);
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

  private RevisionHolder createHolder(InternalCDORevision revision)
  {
    LRURevisionList list = revision.isCurrent() ? currentLRU : revisedLRU;
    return new LRURevisionHolder(list, revision);
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
      return MessageFormat.format("LRU[size={0}, capacity={1}]", size(), capacity());
    }

    @Override
    protected void evict(LRURevisionHolder holder)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Evicting revision {0}v{1}", holder.getID(), holder.getVersion());
      }

      super.evict(holder);
      removeHolder(holder);
    }
  }
}
