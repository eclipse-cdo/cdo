/*
 * Copyright (c) 2011-2013, 2015 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ref.ReferenceQueueWorker;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDORevisionCache extends ReferenceQueueWorker<InternalCDORevision>
    implements InternalCDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, AbstractCDORevisionCache.class);

  private static boolean disableGC;

  private CDOBranchManager branchManager;

  private String name;

  public AbstractCDORevisionCache()
  {
    setDaemon(true);
  }

  public CDOBranchManager getBranchManager()
  {
    return branchManager;
  }

  public void setBranchManager(CDOBranchManager branchManager)
  {
    this.branchManager = branchManager;
  }

  protected final void checkBranch(CDOBranch branch)
  {
    if (branchManager != null)
    {
      CDOBranchManager actualBranchManager = branch.getBranchManager();
      if (actualBranchManager != branchManager)
      {
        throw new IllegalArgumentException(
            "Wrong branch manager: " + actualBranchManager + "; expected: " + branchManager);
      }
    }
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public String toString()
  {
    return formatName("CDORevisionCache");
  }

  @Override
  protected String getThreadName()
  {
    return formatName("CDORevisionCacheCleaner");
  }

  private String formatName(String prefix)
  {
    return prefix + (name == null ? "" : "-" + name);
  }

  @Override
  protected void work(Reference<? extends InternalCDORevision> reference)
  {
    CDORevisionKey key = (CDORevisionKey)reference;

    CDOID id = key.getID();
    CDOBranch branch = key.getBranch();
    int version = key.getVersion();

    InternalCDORevision revision = (InternalCDORevision)removeRevision(id, branch.getVersion(version));
    if (revision != null)
    {
      // Use revision in eviction event
      key = revision;
    }

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new EvictionEventImpl(this, key), listeners);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Evicted {0} from {1}", key, this); //$NON-NLS-1$
    }
  }

  protected Reference<InternalCDORevision> createReference(CDORevision revision)
  {
    if (disableGC)
    {
      return createStrongReference(revision);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Adding revision {0} to {1}", revision, this); //$NON-NLS-1$
    }

    return new CacheSoftReference((InternalCDORevision)revision, getQueue());
  }

  private Reference<InternalCDORevision> createStrongReference(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding revision {0} to {1} (STRONGLY REFERENCED)", revision, this); //$NON-NLS-1$
    }

    return new CacheStrongReference((InternalCDORevision)revision);
  }

  /**
   * @author Eike Stepper
   */
  private static final class CacheSoftReference extends SoftReference<InternalCDORevision>implements CDORevisionKey
  {
    private CDOID id;

    private CDOBranch branch;

    private int version;

    public CacheSoftReference(InternalCDORevision revision, ReferenceQueue<InternalCDORevision> queue)
    {
      super(revision, queue);
      id = revision.getID();
      branch = revision.getBranch();
      version = revision.getVersion();
    }

    public CDOID getID()
    {
      return id;
    }

    public CDOBranch getBranch()
    {
      return branch;
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
  private static final class CacheStrongReference extends SoftReference<InternalCDORevision>implements CDORevisionKey
  {
    private CDOID id;

    private CDOBranch branch;

    private int version;

    public CacheStrongReference(InternalCDORevision revision)
    {
      super(revision);
      id = revision.getID();
      branch = revision.getBranch();
      version = revision.getVersion();
    }

    public CDOID getID()
    {
      return id;
    }

    public CDOBranch getBranch()
    {
      return branch;
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
}
