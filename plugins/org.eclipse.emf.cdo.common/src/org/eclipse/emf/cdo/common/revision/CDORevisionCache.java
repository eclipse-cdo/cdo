/*
 * Copyright (c) 2010-2012, 2015, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.common.revision.NOOPRevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.ecore.EClass;

import java.util.List;
import java.util.function.Consumer;

/**
 * Caches {@link CDORevision revisions} and possibly {@link EvictionEvent evicts} those that are no longer strongly
 * referenced when free memory runs low.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionCache extends CDORevisionCacheAdder, INotifier
{
  /**
   * @since 3.0
   */
  public static final CDORevisionCache NOOP = NOOPRevisionCache.INSTANCE;

  public EClass getObjectType(CDOID id);

  /**
   * @since 3.0
   */
  public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint);

  /**
   * @since 3.0
   */
  public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion);

  /**
   * Returns a list of {@link CDORevision revisions} that are current.
   *
   * @since 3.0
   */
  public List<CDORevision> getCurrentRevisions();

  /**
   * Passes each {@link CDORevision revision} that is current into the given consumer.
   *
   * @since 4.9
   */
  public void forEachCurrentRevision(Consumer<CDORevision> consumer);

  /**
   * Passes each {@link CDORevision revision} that is valid at the given {@link CDOBranchPoint branch point}
   * (or optionally at the base ranch points) into the given consumer.
   *
   * @since 4.15
   */
  public void forEachValidRevision(CDOBranchPoint branchPoint, boolean considerBranchBases, Consumer<CDORevision> consumer);

  /**
   * Passes each {@link CDORevision revision} into the given consumer.
   *
   * @since 4.15
   */
  public void forEachRevision(Consumer<CDORevision> consumer);

  /**
   * @deprecated As of 4.15 use {@link InternalCDORevisionCache#internRevision(CDORevision)}.
   */
  @Deprecated
  @Override
  public void addRevision(CDORevision revision);

  /**
   * An {@link IEvent event} fired from a {@link CDORevisionCache revision cache} when a {@link CDORevision revision} is added to it.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @since 4.15
   */
  public interface AdditionEvent extends IEvent
  {
    @Override
    public CDORevisionCache getSource();

    public CDORevision getRevision();
  }

  /**
   * An {@link IEvent event} fired from a {@link CDORevisionCache revision cache} for {@link CDORevision revisions} that
   * are evicted because they are no longer strongly referenced when free memory runs low.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface EvictionEvent extends IEvent, CDORevisionKey
  {
    /**
     * @since 3.0
     */
    @Override
    public CDORevisionCache getSource();

    /**
     * May be <code>null</code> for certain cache implementations.
     *
     * @since 3.0
     */
    public CDORevision getRevision();
  }
}
