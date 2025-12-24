/*
 * Copyright (c) 2011-2013, 2015, 2019-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.collection.CollectionUtil.KeepMappedValue;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.ecore.EClass;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class CDORevisionCacheNonAuditing extends AbstractCDORevisionCache
{
  private Map<CDOID, Reference<InternalCDORevision>> revisions = CDOIDUtil.createMap();

  public CDORevisionCacheNonAuditing()
  {
  }

  @Override
  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return new CDORevisionCacheNonAuditing();
  }

  @Override
  public EClass getObjectType(CDOID id)
  {
    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
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
    checkBranch(branchPoint.getBranch());

    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null && revision.isValid(branchPoint))
        {
          return revision;
        }
      }

      return null;
    }
  }

  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    checkBranch(branchVersion.getBranch());

    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null && revision.getVersion() == branchVersion.getVersion())
        {
          return revision;
        }
      }

      return null;
    }
  }

  @Override
  public void forEachCurrentRevision(Consumer<CDORevision> consumer)
  {
    forEachRevision(r -> {
      if (!r.isHistorical())
      {
        consumer.accept(r);
      }
    });
  }

  @Override
  public void forEachRevision(Consumer<CDORevision> consumer)
  {
    synchronized (revisions)
    {
      for (Reference<InternalCDORevision> ref : revisions.values())
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          consumer.accept(revision);
        }
      }
    }
  }

  @Override
  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    Map<CDOBranch, List<CDORevision>> result = new HashMap<>();
    synchronized (revisions)
    {
      List<CDORevision> list = new ArrayList<>();
      for (Reference<InternalCDORevision> ref : revisions.values())
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          list.add(revision);
        }
      }

      if (!list.isEmpty())
      {
        result.put(list.get(0).getBranch(), list);
      }
    }

    return result;
  }

  @Override
  public void getAllRevisions(List<InternalCDORevision> result)
  {
    for (Reference<InternalCDORevision> ref : revisions.values())
    {
      InternalCDORevision revision = ref.get();
      if (revision != null)
      {
        result.add(revision);
      }
    }
  }

  @Override
  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint)
  {
    checkBranch(branchPoint.getBranch());
    List<CDORevision> result = new ArrayList<>();

    synchronized (revisions)
    {
      for (Reference<InternalCDORevision> ref : revisions.values())
      {
        InternalCDORevision revision = ref.get();
        if (revision != null && revision.isValid(branchPoint))
        {
          result.add(revision);
        }
      }
    }

    return result;
  }

  @Override
  public CDORevision internRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision"); //$NON-NLS-1$
    checkBranch(revision.getBranch());

    if (!revision.isHistorical())
    {
      CDOID id = revision.getID();
      CDORevision passedRevision = revision;

      IListener[] listeners = getListeners();
      CacheAdditionEvent[] event = { null };

      synchronized (revisions)
      {
        try
        {
          revisions.compute(id, (k, cachedReference) -> {
            if (cachedReference != null)
            {
              InternalCDORevision cachedRevision = cachedReference.get();
              if (cachedRevision != null)
              {
                if (cachedRevision.getVersion() > passedRevision.getVersion())
                {
                  // Keep the cachedRevision in the cache because it's basically newer than the passedRevision,
                  // but don't change the result of internRevision().
                  throw new KeepMappedValue(passedRevision);
                }

                if (cachedRevision.equals(passedRevision))
                {
                  // Keep the cachedRevision in the cache because it's basically equal to the passedRevision,
                  // and change the result of internRevision() to the already cachedRevision.
                  throw new KeepMappedValue(cachedRevision);
                }
              }
            }

            // No revision is already cached, so cache and return the passedRevision.
            if (listeners.length != 0)
            {
              event[0] = new CacheAdditionEvent(this, passedRevision);
            }

            return createReference(passedRevision);
          });
        }
        catch (KeepMappedValue ex)
        {
          revision = ex.mappedValue();
        }
      }

      fireEvent(event[0], listeners);
    }

    return revision;
  }

  @Override
  public void removeRevisions(CDOBranch... branches)
  {
    // Only needed with branching
    throw new UnsupportedOperationException();
  }

  @Override
  protected InternalCDORevision doRemoveRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    checkBranch(branchVersion.getBranch());
    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          if (revision.getVersion() == branchVersion.getVersion()) // No branch check needed in non-auditing
          {
            revisions.remove(id);
            return revision;
          }
        }
        else
        {
          revisions.remove(id);
        }
      }
    }

    return null;
  }

  @Override
  public void clear()
  {
    synchronized (revisions)
    {
      revisions.clear();
    }
  }
}
