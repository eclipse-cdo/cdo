/*
 * Copyright (c) 2009-2012, 2014, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOListResolver;
import org.eclipse.emf.cdo.common.revision.CDORevision;

/**
 * A strategy that specifies which list elements must be present (loaded) in a {@link CDOID} list of a
 * {@link CDORevision revision} when a certain list index is accessed. Implementations of this interface can control the
 * exact characteristics of a certain <em>partial collection loading</em> strategy.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOCollectionLoadingPolicy extends CDOListResolver
{
  /**
   * @since 4.0
   */
  public CDOSession getSession();

  /**
   * @since 4.0
   */
  public void setSession(CDOSession session);

  /**
   * Returns the maximum number of CDOIDs to be loaded for collections when the owning object is loaded initially, i.e.
   * <b>before</b> any of the collection elements is actually accessed. The remaining elements will be initialized as
   * {@link CDOElementProxy proxies}.
   */
  public int getInitialChunkSize();

  /**
   * Returns the maximum number of CDOIDs to be loaded for collections when the owning object is already loaded but the
   * actually accessed element is still a {@link CDOElementProxy proxy}.
   *
   * @since 4.0
   */
  public int getResolveChunkSize();
}
