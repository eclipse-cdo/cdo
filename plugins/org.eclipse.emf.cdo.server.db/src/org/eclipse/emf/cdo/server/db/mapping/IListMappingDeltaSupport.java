/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * Interface to complement {@link IListMapping} in order to provide list delta processing support.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IListMappingDeltaSupport
{
  /**
   * Process a set of CDOFeatureDeltas for a many-valued feature.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the ID of the revision affected
   * @param oldVersion
   *          the original version of the revision
   * @param newVersion
   *          the new revision of the revision (after the change)
   * @param created
   *          the creation date for the new revision
   * @param delta
   *          the {@link CDOListFeatureDelta} which contains the list deltas.
   */
  public void processDelta(IDBStoreAccessor accessor, CDOID id, int oldVersion, int newVersion, long created,
      CDOListFeatureDelta delta);
}
