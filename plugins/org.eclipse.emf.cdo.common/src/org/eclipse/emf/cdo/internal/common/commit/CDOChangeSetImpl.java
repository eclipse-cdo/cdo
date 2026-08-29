/*
 * Copyright (c) 2010-2012, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

/**
 * @author Eike Stepper
 */
public class CDOChangeSetImpl extends CDOChangeSetDataImpl implements CDOChangeSet
{
  private final CDOBranchPoint startPoint;

  private final CDOBranchPoint endPoint;

  /**
   * Provides full revisions at {@link #startPoint} when the change-set producer has them available. The provider is
   * deliberately attached to this internal implementation rather than the public {@link CDOChangeSet} contract:
   * ordinary consumers need only deltas, while semantic list merging also needs ancestor values and set-state.
   */
  private final CDORevisionProvider startRevisionProvider;

  public CDOChangeSetImpl(CDOBranchPoint startPoint, CDOBranchPoint endPoint, CDOChangeSetData data)
  {
    this(startPoint, endPoint, data, startPoint instanceof CDORevisionProvider ? (CDORevisionProvider)startPoint : null);
  }

  /**
   * Creates a change set and retains an optional provider for the revisions from which its executable histories start.
   *
   * @param startPoint the coordinate at which the histories start.
   * @param endPoint the coordinate reached after applying the histories.
   * @param data the additions, revision deltas, and detachments in the histories.
   * @param startRevisionProvider the provider of full start revisions, or {@code null} when unavailable.
   */
  public CDOChangeSetImpl(CDOBranchPoint startPoint, CDOBranchPoint endPoint, CDOChangeSetData data, CDORevisionProvider startRevisionProvider)
  {
    super(data.getNewObjects(), data.getChangedObjects(), data.getDetachedObjects());
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.startRevisionProvider = startRevisionProvider;
  }

  @Override
  public CDOBranchPoint getStartPoint()
  {
    return startPoint;
  }

  @Override
  public CDOBranchPoint getEndPoint()
  {
    return endPoint;
  }

  @Override
  public CDOBranchPoint getAncestorPoint()
  {
    return CDOBranchUtil.getAncestor(startPoint, endPoint);
  }

  /**
   * Returns the optional provider of full revisions at the start coordinate.
   * <p>
   * A {@code null} result means that only the public delta-only contract is available. In particular, callers must not
   * infer unsettable-feature state from an origin list size because SET {@code []} and UNSET {@code []} both have size
   * zero.
   *
   * @return the start-revision provider, or {@code null} if it was not retained.
   */
  @Override
  public CDORevisionProvider getStartRevisionProvider()
  {
    return startRevisionProvider;
  }
}
