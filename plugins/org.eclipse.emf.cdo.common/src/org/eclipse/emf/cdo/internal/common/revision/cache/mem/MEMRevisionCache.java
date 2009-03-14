/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/230832
 */
package org.eclipse.emf.cdo.internal.common.revision.cache.mem;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.revision.cache.EvictionEventImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ref.KeyedPhantomReference;
import org.eclipse.net4j.util.ref.KeyedReference;
import org.eclipse.net4j.util.ref.KeyedSoftReference;
import org.eclipse.net4j.util.ref.KeyedStrongReference;
import org.eclipse.net4j.util.ref.KeyedWeakReference;
import org.eclipse.net4j.util.ref.ReferenceQueueWorker;
import org.eclipse.net4j.util.ref.ReferenceType;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.lang.ref.Reference;
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
public class MEMRevisionCache extends ReferenceQueueWorker<InternalCDORevision> implements CDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, MEMRevisionCache.class);

  private Map<CDOID, CacheList> cacheLists = new HashMap<CDOID, CacheList>();

  private ReferenceType referenceType;

  public MEMRevisionCache(ReferenceType referenceType)
  {
    this.referenceType = referenceType;
  }

  public MEMRevisionCache()
  {
    this(ReferenceType.SOFT);
  }

  public ReferenceType getReferenceType()
  {
    return referenceType;
  }

  public void setReferenceType(ReferenceType referenceType)
  {
    this.referenceType = referenceType;
  }

  public EClass getObjectType(CDOID id)
  {
    return null;
  }

  public InternalCDORevision getRevision(CDOID id)
  {
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list != null)
      {
        return list.getRevision();
      }
    }

    return null;
  }

  public InternalCDORevision getRevisionByTime(CDOID id, long timeStamp)
  {
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list != null)
      {
        return list.getRevisionByTime(timeStamp);
      }
    }

    return null;
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, int version)
  {
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list != null)
      {
        return list.getRevisionByVersion(version);
      }
    }

    return null;
  }

  public List<CDORevision> getRevisions()
  {
    ArrayList<CDORevision> currentRevisions = new ArrayList<CDORevision>();
    synchronized (cacheLists)
    {
      for (Entry<CDOID, CacheList> entry : cacheLists.entrySet())
      {
        CacheList list = entry.getValue();
        InternalCDORevision revision = list.getRevision();
        if (revision != null)
        {
          currentRevisions.add(revision);
        }
      }
    }

    return currentRevisions;
  }

  public boolean addRevision(InternalCDORevision revision)
  {
    CDOID id = revision.getID();
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list == null)
      {
        list = new CacheList();
        cacheLists.put(id, list);
      }

      return list.addRevision(revision);
    }
  }

  public InternalCDORevision removeRevision(CDOID id, int version)
  {
    synchronized (cacheLists)
    {
      CacheList list = cacheLists.get(id);
      if (list != null)
      {
        list.removeRevision(version);
        if (list.isEmpty())
        {
          cacheLists.remove(id);
          if (TRACER.isEnabled())
          {
            TRACER.format("Removed cache list of {0}", id);
          }
        }
      }
    }

    return null;
  }

  public CDOID getResourceID(CDOID folderID, String name, long timeStamp)
  {
    CDOID[] ids = getRevisionIDs();
    for (CDOID id : ids)
    {
      synchronized (cacheLists)
      {
        CacheList list = cacheLists.get(id);
        if (list != null)
        {
          return list.getResourceID(folderID, name, timeStamp);
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

  private synchronized CDOID[] getRevisionIDs()
  {
    return cacheLists.keySet().toArray(new CDOID[cacheLists.size()]);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void work(Reference<? extends InternalCDORevision> reference)
  {
    KeyedReference<CDOIDAndVersion, InternalCDORevision> keyedRef = (KeyedReference<CDOIDAndVersion, InternalCDORevision>)reference;
    CDOIDAndVersion key = keyedRef.getKey();
    CDOID id = key.getID();
    int version = key.getVersion();

    InternalCDORevision revision = removeRevision(id, version);
    if (revision == null)
    {
      fireEvent(new EvictionEventImpl(this, id, version));
    }
    else
    {
      // Should not happen with garbage collector triggered eviction
      fireEvent(new EvictionEventImpl(this, revision));
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

    throw new IllegalStateException("Invalid referenceType: " + referenceType);
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

    public InternalCDORevision getRevision()
    {
      KeyedReference<CDOIDAndVersion, InternalCDORevision> ref = isEmpty() ? null : getFirst();
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          if (revision.isCurrent())
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

    public InternalCDORevision getRevisionByTime(long timeStamp)
    {
      return getRevisionByTime(timeStamp, false);
    }

    public CDOID getResourceID(CDOID folderID, String name, long timeStamp)
    {
      InternalCDORevision revision = getRevisionByTime(timeStamp, true);
      if (revision != null)
      {
        CDOID revisionFolderID = (CDOID)revision.getContainerID();
        if (CDOIDUtil.equals(revisionFolderID, folderID))
        {
          EStructuralFeature feature = revision.getEClass().getEStructuralFeature(
              CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
          String revisionName = (String)revision.getValue(feature);
          if (ObjectUtil.equals(revisionName, name))
          {
            return revision.getID();
          }
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
            TRACER.format("Removed version {0} from cache list of {1}", version, key.getID());
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
        if (ref.get() != null)
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

    private InternalCDORevision getRevisionByTime(long timeStamp, boolean onlyResource)
    {
      for (Iterator<KeyedReference<CDOIDAndVersion, InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        KeyedReference<CDOIDAndVersion, InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          if (onlyResource && !revision.isResource())
          {
            return null;
          }

          long created = revision.getCreated();
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
  }
}
