/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision.cache;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache.EvictionEvent;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.event.Event;

/**
 * @author Eike Stepper
 */
public class EvictionEventImpl extends Event implements EvictionEvent
{
  private static final long serialVersionUID = 1L;

  private CDOID id;

  private int version;

  private InternalCDORevision revision;

  public EvictionEventImpl(CDORevisionCache cache, InternalCDORevision revision)
  {
    super(cache);
    id = revision.getID();
    version = revision.getVersion();
    this.revision = revision;
  }

  public EvictionEventImpl(CDORevisionCache cache, CDOID id, int version)
  {
    super(cache);
    this.id = id;
    this.version = version;
  }

  @Override
  public CDORevisionCache getSource()
  {
    return (CDORevisionCache)super.getSource();
  }

  public CDORevisionCache getCache()
  {
    return getSource();
  }

  public CDOID getID()
  {
    return id;
  }

  public int getVersion()
  {
    return version;
  }

  public InternalCDORevision getRevision()
  {
    return revision;
  }
}
