/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOTimestampContext;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOTimestampContextImpl implements CDOTimestampContext
{
  private long timestamp;

  private Set<CDOIDAndVersion> dirtyObjects = new HashSet<CDOIDAndVersion>();

  private Collection<CDOID> detachedObjects = new ArrayList<CDOID>();

  public CDOTimestampContextImpl(long timestamp)
  {
    this.timestamp = timestamp;
  }

  public long getTimestamp()
  {
    return timestamp;
  }

  public Set<CDOIDAndVersion> getDirtyObjects()
  {
    return dirtyObjects;
  }

  public void setDirtyObjects(Set<CDOIDAndVersion> dirtyObjects)
  {
    this.dirtyObjects = dirtyObjects;
  }

  public Collection<CDOID> getDetachedObjects()
  {
    return detachedObjects;
  }

  public void setDetachedObjects(Collection<CDOID> detachedObjects)
  {
    this.detachedObjects = detachedObjects;
  }
}
