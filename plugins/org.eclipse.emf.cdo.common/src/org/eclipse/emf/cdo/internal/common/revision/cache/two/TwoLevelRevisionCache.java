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
package org.eclipse.emf.cdo.internal.common.revision.cache.two;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class TwoLevelRevisionCache extends Lifecycle implements CDORevisionCache, IListener
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, TwoLevelRevisionCache.class);

  private CDORevisionCache level1;

  private CDORevisionCache level2;

  public TwoLevelRevisionCache()
  {
  }

  public CDORevisionCache getLevel1()
  {
    return level1;
  }

  public void setLevel1(CDORevisionCache level1)
  {
    this.level1 = level1;
  }

  public CDORevisionCache getLevel2()
  {
    return level2;
  }

  public void setLevel2(CDORevisionCache level2)
  {
    this.level2 = level2;
  }

  public EClass getObjectType(CDOID id)
  {
    EClass objectType = level1.getObjectType(id);
    if (objectType == null)
    {
      objectType = level2.getObjectType(id);
    }

    return objectType;
  }

  public InternalCDORevision getRevision(CDOID id)
  {
    InternalCDORevision revision = level1.getRevision(id);
    if (revision == null)
    {
      revision = level2.getRevision(id);
    }

    return revision;
  }

  public InternalCDORevision getRevisionByTime(CDOID id, long timeStamp)
  {
    InternalCDORevision revision = level1.getRevisionByTime(id, timeStamp);
    if (revision == null)
    {
      revision = level2.getRevisionByTime(id, timeStamp);
    }

    return revision;
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, int version)
  {
    InternalCDORevision revision = level1.getRevisionByVersion(id, version);
    if (revision == null)
    {
      revision = level2.getRevisionByVersion(id, version);
    }

    return revision;
  }

  public List<CDORevision> getRevisions()
  {
    List<CDORevision> revisions = new ArrayList<CDORevision>();
    revisions.addAll(level1.getRevisions());
    revisions.addAll(level2.getRevisions());
    return revisions;
  }

  public boolean addRevision(InternalCDORevision revision)
  {
    return level1.addRevision(revision);
  }

  public InternalCDORevision removeRevision(CDOID id, int version)
  {
    InternalCDORevision revision = level1.removeRevision(id, version);
    if (revision == null)
    {
      revision = level2.removeRevision(id, version);
    }

    return revision;
  }

  public CDOID getResourceID(CDOID folderID, String name, long timeStamp)
  {
    CDOID id = level1.getResourceID(folderID, name, timeStamp);
    if (id == null)
    {
      id = level2.getResourceID(folderID, name, timeStamp);
    }

    return id;
  }

  public void clear()
  {
    level1.clear();
    level2.clear();
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof EvictionEvent)
    {
      EvictionEvent e = (EvictionEvent)event;
      InternalCDORevision revision = e.getRevision();
      if (revision != null)
      {
        CDORevisionCache cache = e.getCache();
        if (cache == level1)
        {
          evictedFromLevel1(revision);
        }
        else if (cache == level2)
        {
          evictedFromLevel2(revision);
        }
      }
    }
  }

  protected void evictedFromLevel1(InternalCDORevision revision)
  {
    level2.addRevision(revision);
    if (TRACER.isEnabled())
    {
      TRACER.format("Recached revision {0}", revision);
    }
  }

  protected void evictedFromLevel2(InternalCDORevision revision)
  {
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(level1, "level1");
    checkState(level2, "level2");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    level1.addListener(this);
    level2.addListener(this);
    LifecycleUtil.activate(level1);
    LifecycleUtil.activate(level2);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(level2);
    LifecycleUtil.deactivate(level1);
    level2.removeListener(this);
    level1.removeListener(this);
    super.doDeactivate();
  }
}
