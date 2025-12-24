/*
 * Copyright (c) 2008-2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDODetachedRevisionDeltaImpl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

/**
 * Represents the change delta between two {@link CDORevision revisions} of a CDO object. The detailed
 * {@link CDOFeatureDelta feature deltas} are returned by the {@link #getFeatureDeltas()} method.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionDelta extends CDORevisionKey
{
  /**
   * This constant is only passed into conflict resolvers to indicate that a conflict was caused by remote detachment of
   * an object. Calling any method on this marker instance will result in an {@link UnsupportedOperationException} being
   * thrown.
   *
   * @since 4.0
   */
  public static final CDORevisionDelta DETACHED = new CDODetachedRevisionDeltaImpl();

  /**
   * @since 3.0
   */
  public EClass getEClass();

  /**
   * @since 4.0
   */
  public CDORevisable getTarget();

  /**
   * @since 4.2
   */
  public int size();

  /**
   * @since 3.0
   */
  public boolean isEmpty();

  /**
   * @since 4.0
   */
  public CDORevisionDelta copy();

  /**
   * @since 4.0
   */
  public CDOFeatureDelta getFeatureDelta(EStructuralFeature feature);

  public List<CDOFeatureDelta> getFeatureDeltas();

  /**
   * @deprecated As of 4.3 use {@link #applyTo(CDORevision)}.
   */
  @Deprecated
  public void apply(CDORevision revision);

  /**
   * Applies the {@link #getFeatureDeltas() feature deltas} in this revision delta to the {@link CDORevisionData data}
   * of the given revision.
   * <p>
   * The system data of the given revision, e.g. {@link CDOBranchPoint branch point} or {@link CDOBranchVersion branch
   * version} of the given revision are <b>not</b> modified.
   *
   * @since 4.3
   */
  public void applyTo(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);

  /**
   * @since 4.2
   * @deprecated As of 4.9 use {@link #accept(CDOFeatureDeltaVisitor, java.util.function.Predicate)}.
   */
  @Deprecated
  public void accept(CDOFeatureDeltaVisitor visitor, org.eclipse.net4j.util.Predicate<EStructuralFeature> filter);

  /**
   * @since 4.9
   */
  public void accept(CDOFeatureDeltaVisitor visitor, java.util.function.Predicate<EStructuralFeature> filter);
}
