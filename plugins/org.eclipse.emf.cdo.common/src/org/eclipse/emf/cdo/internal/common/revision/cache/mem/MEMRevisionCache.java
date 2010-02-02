/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
package org.eclipse.emf.cdo.internal.common.revision.cache.mem;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.revision.cache.EvictionEventImpl;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ref.KeyedPhantomReference;
import org.eclipse.net4j.util.ref.KeyedReference;
import org.eclipse.net4j.util.ref.KeyedSoftReference;
import org.eclipse.net4j.util.ref.KeyedStrongReference;
import org.eclipse.net4j.util.ref.KeyedWeakReference;
import org.eclipse.net4j.util.ref.ReferenceQueueWorker;
import org.eclipse.net4j.util.ref.ReferenceType;

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
public class MEMRevisionCache extends ReferenceQueueWorker<InternalCDORevision> implements InternalCDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, MEMRevisionCache.class);

  private Map<CDOID, CacheList> cacheLists = new HashMap<CDOID, CacheList>();

  private ReferenceType referenceType;

  public MEMRevisionCache(ReferenceType referenceType)
  {
    setReferenceType(referenceType);
  }

  public MEMRevisionCache()
  {
    this(ReferenceType.SOFT);
  }

  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return new MEMRevisionCache(referenceType);
  }

  public boolean isSupportingBranches()
  {
    return false;
  }

  public ReferenceType getReferenceType()
  {
    return referenceType;
  }

  public void setReferenceType(ReferenceType referenceType)
  {
    checkInactive();
    this.referenceType = referenceType;
  }

  public EClass getObjectType(CDOID id)
  {
    return null;
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list != null)
      {
        return list.getRevision(branchPoint.getTimeStamp());
      }
    }

    return null;
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list != null)
      {
        int version = branchVersion.getVersion();
        return list.getRevisionByVersion(version);
      }
    }

    return null;
  }

  public List<CDORevision> getCurrentRevisions()
  {
    ArrayList<CDORevision> currentRevisions = new ArrayList<CDORevision>();
    synchronized (cacheLists)
    {
      for (Entry<CDOID, CacheList> entry : cacheLists.entrySet())
      {
        CacheList list = entry.getValue();
        InternalCDORevision revision = list.getRevision(CDORevision.UNSPECIFIED_DATE);
        if (revision != null)
        {
          currentRevisions.add(revision);
        }
      }
    }

    return currentRevisions;
  }

  public boolean addRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");
    CDOID id = revision.getID();
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list == null)
      {
        list = new CacheList();
        cacheLists.put(id, list);
      }

      return list.addRevision((InternalCDORevision)revision);
    }
  }

  public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list != null)
      {
        int version = branchVersion.getVersion();
        list.removeRevision(version);
        if (list.isEmpty())
        {
          cacheLists.remove(id);
          if (TRACER.isEnabled())
          {
            TRACER.format("Removed cache list of {0}", id); //$NON-NLS-1$
          }
        }
      }
    }

    return null;
  }

  public void clear()
  {
    synchronized (cacheLists)
    {
      cacheLists.clear();
    }
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void work(Reference<? extends InternalCDORevision> reference)
  {
    KeyedReference<CDOIDAndVersion, InternalCDORevision> keyedRef = (KeyedReference<CDOIDAndVersion, InternalCDORevision>)reference;
    CDOIDAndVersion key = keyedRef.getKey();
    final CDOID id = key.getID();
    final CDOBranch branch = null;
    final int version = key.getVersion();

    InternalCDORevision revision = removeRevision(id, CDOBranchUtil.createBranchVersion(branch, version));
    if (revision == null)
    {
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new EvictionEventImpl(this, CDORevisionUtil.createRevisionKey(id, branch, version)), listeners);
      }
    }
    else
    {
      // Should not happen with garbage collector triggered eviction
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new EvictionEventImpl(this, revision), listeners);
      }
    }
  }

  protected KeyedReference<CDOIDAndVersion, InternalCDORevision> createReference(InternalCDORevision revision)
  {
    if (referenceType != null)
    {
      CDOIDAndVersion key = CDOIDUtil.createIDAndVersion(revision.getID(), revision.getVersion());
      switch (referenceType)
      {
      case STRONG:
        return new KeyedStrongReference<CDOIDAndVersion, InternalCDORevision>(key, revision);

      case SOFT:
        return new KeyedSoftReference<CDOIDAndVersion, InternalCDORevision>(key, revision, getQueue());

      case WEAK:
        return new KeyedWeakReference<CDOIDAndVersion, InternalCDORevision>(key, revision, getQueue());

      case PHANTOM:
        return new KeyedPhantomReference<CDOIDAndVersion, InternalCDORevision>(key, revision, getQueue());
      }
    }

    throw new IllegalStateException(MessageFormat.format(Messages.getString("MEMRevisionCache.1"), referenceType)); //$NON-NLS-1$
  }

  /**
   * @author Eike Stepper
   */
  public class CacheList extends LinkedList<KeyedReference<CDOIDAndVersion, InternalCDORevision>>
  {
    private static final long serialVersionUID = 1L;

    public CacheList()
    {
    }

    public InternalCDORevision getRevision(long timeStamp)
    {
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        KeyedReference<CDOIDAndVersion, InternalCDORevision> ref = isEmpty() ? null : getFirst();
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

      for (Iterator<KeyedReference<CDOIDAndVersion, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDOIDAndVersion, InternalCDORevision> ref = it.next();
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
            else
            {
              break;
            }
          }
        }
        else
        {
          it.remove();
        }
      }

      return null;
    }

    public InternalCDORevision getRevisionByVersion(int version)
    {
      for (Iterator<KeyedReference<CDOIDAndVersion, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDOIDAndVersion, InternalCDORevision> ref = it.next();
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

    public void removeRevision(int version)
    {
      for (Iterator<KeyedReference<CDOIDAndVersion, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDOIDAndVersion, InternalCDORevision> ref = it.next();
        CDOIDAndVersion key = ref.getKey();
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

    public boolean addRevision(InternalCDORevision revision)
    {
      KeyedReference<CDOIDAndVersion, InternalCDORevision> reference = createReference(revision);
      int version = revision.getVersion();
      for (ListIterator<KeyedReference<CDOIDAndVersion, InternalCDORevision>> it = listIterator(); it.hasNext();)
      {
        KeyedReference<CDOIDAndVersion, InternalCDORevision> ref = it.next();
        InternalCDORevision foundRevision = ref.get();
        if (foundRevision != null)
        {
          CDOIDAndVersion key = ref.getKey();
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
  }
}
