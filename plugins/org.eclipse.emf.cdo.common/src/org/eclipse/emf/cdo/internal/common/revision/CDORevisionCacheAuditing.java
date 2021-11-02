/*
 * Copyright (c) 2011-2015, 2019-2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Eike Stepper
 */
public class CDORevisionCacheAuditing extends AbstractCDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionCacheAuditing.class);

  protected Map<Object, RevisionList> revisionLists = new HashMap<>();

  public CDORevisionCacheAuditing()
  {
  }

  @Override
  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return new CDORevisionCacheAuditing();
  }

  @Override
  public EClass getObjectType(CDOID id)
  {
    synchronized (revisionLists)
    {
      RevisionList list = revisionLists.get(id);
      if (list != null && !list.isEmpty())
      {
        Reference<InternalCDORevision> ref = list.getFirst();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          return revision.getEClass();
        }
      }

      return null;
    }
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    CDOBranch branch = branchPoint.getBranch();
    checkBranch(branch);

    return withRevisionList(id, branch, list -> list.getRevision(branchPoint.getTimeStamp()));
  }

  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    CDOBranch branch = branchVersion.getBranch();
    checkBranch(branch);

    return withRevisionList(id, branch, list -> list.getRevisionByVersion(branchVersion.getVersion()));
  }

  @Override
  public void forEachCurrentRevision(Consumer<CDORevision> consumer)
  {
    forEachRevisionList(list -> {
      InternalCDORevision revision = list.getRevision(CDORevision.UNSPECIFIED_DATE);
      if (revision != null)
      {
        consumer.accept(revision);
      }
    });
  }

  @Override
  public void forEachRevision(Consumer<CDORevision> consumer)
  {
    forEachRevisionList(list -> list.forEachRevision(consumer));
  }

  @Override
  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    Map<CDOBranch, List<CDORevision>> result = new HashMap<>();
    forEachRevisionList(list -> list.getAllRevisions(result));
    return result;
  }

  @Override
  public void getAllRevisions(List<InternalCDORevision> result)
  {
    forEachRevisionList(list -> list.getAllRevisions(result));
  }

  @Override
  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint)
  {
    CDOBranch branch = branchPoint.getBranch();
    checkBranch(branch);

    List<CDORevision> result = new ArrayList<>();
    synchronized (revisionLists)
    {
      for (Map.Entry<Object, RevisionList> entry : revisionLists.entrySet())
      {
        if (isKeyInBranch(entry.getKey(), branch))
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
  public CDORevision internRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");

    CDOBranch branch = revision.getBranch();
    checkBranch(branch);

    CDOID id = revision.getID();
    Object key = createKey(id, branch);

    IListener[] listeners = getListeners();
    CacheAdditionEvent event = null;

    try
    {
      synchronized (revisionLists)
      {
        RevisionList list = revisionLists.get(key);
        if (list == null)
        {
          list = new RevisionList();
          revisionLists.put(key, list);
        }

        CDORevision cachedRevision = list.addRevision(revision, () -> createReference(revision));
        if (cachedRevision != revision)
        {
          return cachedRevision;
        }

        typeRefIncrease(id, revision.getEClass());
      }

      if (listeners.length != 0)
      {
        event = new CacheAdditionEvent(this, revision);
      }

      return revision;
    }
    finally
    {
      if (event != null)
      {
        fireEvent(event, listeners);
      }
    }
  }

  @Override
  protected InternalCDORevision doRemoveRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    CDOBranch branch = branchVersion.getBranch();
    checkBranch(branch);

    Object key = createKey(id, branch);
    synchronized (revisionLists)
    {
      RevisionList list = revisionLists.get(key);
      if (list != null)
      {
        list.removeRevision(branchVersion.getVersion());
        if (list.isEmpty())
        {
          revisionLists.remove(key);
          typeRefDecrease(id);

          if (TRACER.isEnabled())
          {
            TRACER.format("Removed cache list of {0}", key); //$NON-NLS-1$
          }
        }
      }
    }

    return null;
  }

  @Override
  public void clear()
  {
    synchronized (revisionLists)
    {
      revisionLists.clear();
      typeRefDispose();
    }
  }

  protected void typeRefIncrease(CDOID id, EClass type)
  {
    // Do nothing
  }

  protected void typeRefDecrease(CDOID id)
  {
    // Do nothing
  }

  protected void typeRefDispose()
  {
    // Do nothing
  }

  protected Object createKey(CDOID id, CDOBranch branch)
  {
    return id;
  }

  protected boolean isKeyInBranch(Object key, CDOBranch branch)
  {
    return true;
  }

  // protected RevisionList getRevisionList(CDOID id, CDOBranch branch)
  // {
  // return withRevisionList(id, branch, list -> list);
  // }

  protected <T> T withRevisionList(CDOID id, CDOBranch branch, Function<RevisionList, T> function)
  {
    Object key = createKey(id, branch);
    synchronized (revisionLists)
    {
      RevisionList list = revisionLists.get(key);
      if (list != null)
      {
        return function.apply(list);
      }
    }

    return null;
  }

  protected void forEachRevisionList(Consumer<RevisionList> consumer)
  {
    synchronized (revisionLists)
    {
      for (RevisionList revisionList : revisionLists.values())
      {
        consumer.accept(revisionList);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static final class RevisionList extends LinkedList<Reference<InternalCDORevision>>
  {
    private static final long serialVersionUID = 1L;

    public RevisionList()
    {
    }

    public InternalCDORevision getRevision(long timeStamp)
    {
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        Reference<InternalCDORevision> ref = isEmpty() ? null : getFirst();
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

      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
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

    public InternalCDORevision getRevisionByVersion(int version)
    {
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
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

    public void forEachRevision(Consumer<CDORevision> consumer)
    {
      for (Reference<InternalCDORevision> ref : this)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          consumer.accept(revision);
        }
      }
    }

    public CDORevision addRevision(CDORevision revision, Supplier<Reference<InternalCDORevision>> referenceCreator)
    {
      int version = revision.getVersion();
      for (ListIterator<Reference<InternalCDORevision>> it = listIterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision foundRevision = ref.get();
        if (foundRevision != null)
        {
          CDORevisionKey key = (CDORevisionKey)ref;
          int v = key.getVersion();
          if (v == version)
          {
            return foundRevision;
          }

          if (v < version)
          {
            it.previous();
            it.add(referenceCreator.get());
            return revision;
          }
        }
        else
        {
          it.remove();
        }
      }

      addLast(referenceCreator.get());
      return revision;
    }

    public void removeRevision(int version)
    {
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        CDORevisionKey key = (CDORevisionKey)ref;
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
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
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
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          CDOBranch branch = revision.getBranch();
          List<CDORevision> resultList = result.get(branch);
          if (resultList == null)
          {
            resultList = new ArrayList<>(1);
            result.put(branch, resultList);
          }

          resultList.add(revision);
        }
      }
    }

    public void getAllRevisions(List<InternalCDORevision> result)
    {
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          result.add(revision);
        }
      }
    }
  }
}
