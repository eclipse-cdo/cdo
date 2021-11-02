/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache.AdditionEvent;

import org.eclipse.net4j.util.event.Event;

/**
 * @author Eike Stepper
 */
public final class CacheAdditionEvent extends Event implements AdditionEvent
{
  private static final long serialVersionUID = 1L;

  private final CDORevision revision;

  public CacheAdditionEvent(CDORevisionCache cache, CDORevision revision)
  {
    super(cache);
    this.revision = revision;
  }

  @Override
  public CDORevisionCache getSource()
  {
    return (CDORevisionCache)super.getSource();
  }

  @Override
  public CDORevision getRevision()
  {
    return revision;
  }
}
