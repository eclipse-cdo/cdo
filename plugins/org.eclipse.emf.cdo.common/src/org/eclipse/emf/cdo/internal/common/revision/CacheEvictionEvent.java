/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache.EvictionEvent;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.event.Event;

/**
 * @author Eike Stepper
 */
public final class CacheEvictionEvent extends Event implements EvictionEvent
{
  private static final long serialVersionUID = 1L;

  private final CDORevisionKey key;

  public CacheEvictionEvent(CDORevisionCache cache, CDORevisionKey key)
  {
    super(cache);
    this.key = key;
  }

  @Override
  public CDORevisionCache getSource()
  {
    return (CDORevisionCache)super.getSource();
  }

  @Override
  public CDOID getID()
  {
    return key.getID();
  }

  @Override
  public CDOBranch getBranch()
  {
    return key.getBranch();
  }

  @Override
  public int getVersion()
  {
    return key.getVersion();
  }

  @Override
  public InternalCDORevision getRevision()
  {
    if (key instanceof InternalCDORevision)
    {
      return (InternalCDORevision)key;
    }

    return null;
  }
}
