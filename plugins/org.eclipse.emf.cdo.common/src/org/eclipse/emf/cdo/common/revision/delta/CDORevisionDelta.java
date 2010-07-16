/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionDelta extends CDORevisionKey
{
  /**
   * @since 3.0
   */
  public EClass getEClass();

  /**
   * @since 3.0
   */
  public boolean isEmpty();

  public List<CDOFeatureDelta> getFeatureDeltas();

  /**
   * Applies the {@link #getFeatureDeltas() feature deltas} in this revision delta to the {@link CDORevisionData data}
   * of the given revision.
   * <p>
   * The system data of the given revision, e.g. {@link CDOBranchPoint branch point} or {@link CDOBranchVersion branch
   * version} of the given revision are <b>not</b> modified.
   */
  public void apply(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);
}
