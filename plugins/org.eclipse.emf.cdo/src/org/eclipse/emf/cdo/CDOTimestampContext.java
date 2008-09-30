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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;

import java.util.Collection;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTimestampContext
{
  public long getTimestamp();

  public Collection<CDOID> getDetachedObjects();

  public Set<CDOIDAndVersion> getDirtyObjects();
}
