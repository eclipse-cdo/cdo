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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.cache.noop.NOOPRevisionCache;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDORevisionCache extends INotifier, CDORevisionCacheAdder
{
  /**
   * @since 3.0
   */
  public static final CDORevisionCache NOOP = NOOPRevisionCache.INSTANCE;

  public EClass getObjectType(CDOID id);

  /**
   * @since 3.0
   */
  public CDORevision getRevision(CDOID id);

  /**
   * @since 3.0
   */
  public CDORevision getRevisionByTime(CDOID id, long timeStamp);

  /**
   * @since 3.0
   */
  public CDORevision getRevisionByVersion(CDOID id, int version);

  /**
   * @since 3.0
   */
  public void removeRevision(CDORevision revision);

  /**
   * @since 3.0
   */
  public CDORevision removeRevision(CDOID id, int version);

  /**
   * Returns a list of {@link CDORevision revisions} that are current.
   */
  public List<CDORevision> getRevisions();

  public void clear();

  /**
   * @author Eike Stepper
   */
  public interface EvictionEvent extends IEvent
  {
    /**
     * @since 3.0
     */
    public CDORevisionCache getSource();

    public CDOID getID();

    public int getVersion();

    /**
     * @since 3.0
     */
    public CDORevision getRevision();
  }
}
