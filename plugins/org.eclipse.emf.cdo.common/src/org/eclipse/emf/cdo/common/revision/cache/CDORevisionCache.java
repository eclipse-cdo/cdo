/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.revision.cache;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.revision.cache.mem.MEMRevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.cache.noop.NOOPRevisionCache;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDORevisionCache extends INotifier
{
  /**
   * @since 3.0
   */
  public static final CDORevisionCache NOOP = NOOPRevisionCache.INSTANCE;

  /**
   * @since 3.0
   */
  public boolean isSupportingBranches();

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
   * @author Eike Stepper
   */
  public interface EvictionEvent extends IEvent, CDORevisionKey
  {
    /**
     * @since 3.0
     */
    public CDORevisionCache getSource();

    /**
     * May be <code>null</code> for certain cache implementations, e.g. {@link MEMRevisionCache}.
     * 
     * @since 3.0
     */
    public CDORevision getRevision();
  }
}
