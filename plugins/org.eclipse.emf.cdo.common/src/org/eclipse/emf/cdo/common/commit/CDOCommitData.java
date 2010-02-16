/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.commit;

import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOCommitData
{
  public List<CDOPackageUnit> getNewPackageUnits();

  public List<CDOIDAndVersion> getNewObjects();

  /**
   * Returns a collection of revision keys denoting which (original) revisions have been changed in the context of a
   * commit operation. Depending on various conditions like change subscriptions particular elements can also be full
   * {@link CDORevisionDelta revision deltas}.
   */
  public List<CDORevisionKey> getChangedObjects();

  public List<CDOIDAndVersion> getDetachedObjects();
}
