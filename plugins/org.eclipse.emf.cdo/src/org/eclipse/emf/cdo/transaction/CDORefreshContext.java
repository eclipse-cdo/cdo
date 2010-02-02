/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;

import java.util.Collection;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORefreshContext
{
  public CDOBranchPoint getBranchPoint();

  public Collection<CDOID> getDetachedObjects();

  public Set<CDOIDAndVersion> getDirtyObjects();
}
