/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.util;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.session.CDOSession;

/**
 * Merges the commits of a source {@link Change change} into the {@link CDOBranch branch} of a target {@link Stream stream}
 * in order to create a {@link Delivery delivery}.
 * <p>
 * Note: The {@link #mergeDelivery(LMMergeInfos)} method allows to augment the merge parameters in the future without breaking APIs.
 *
 * @author Eike Stepper
 * @since 1.2
 */
public interface LMMerger2 extends LMMerger
{
  /**
   * Merges the changes indicated by the given {@link LMMergeInfos merge infos} and returns the time stamp of the
   * resulting commit into the merge target branch, or {@link CDOBranchPoint#INVALID_DATE CDOBranchPoint.INVALID_DATE}
   * if no commit has happened.
   */
  public long mergeDelivery(LMMergeInfos infos);

  /**
   * @deprecated As of 1.2 implement {@link #mergeDelivery(LMMergeInfos)}.
   */
  @Deprecated
  @Override
  public default long mergeDelivery(CDOSession session, CDOBranchPoint sourceBranchPoint, CDOBranch targetBranch)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Encapsulates the {@link LMMerger2#mergeDelivery(LMMergeInfos) merge} parameters.
   *
   * @author Eike Stepper
   */
  public static final class LMMergeInfos
  {
    private CDOSession session;

    private Baseline sourceBaseline;

    private CDOBranchPoint sourceBranchPoint;

    private FloatingBaseline targetBaseline;

    private CDOBranch targetBranch;

    public LMMergeInfos()
    {
    }

    public CDOSession getSession()
    {
      return session;
    }

    public void setSession(CDOSession session)
    {
      this.session = session;
    }

    public Baseline getSourceBaseline()
    {
      return sourceBaseline;
    }

    public void setSourceBaseline(Baseline sourceBaseline)
    {
      this.sourceBaseline = sourceBaseline;
    }

    public CDOBranchPoint getSourceBranchPoint()
    {
      return sourceBranchPoint;
    }

    public void setSourceBranchPoint(CDOBranchPoint sourceBranchPoint)
    {
      this.sourceBranchPoint = sourceBranchPoint;
    }

    public FloatingBaseline getTargetBaseline()
    {
      return targetBaseline;
    }

    public void setTargetBaseline(FloatingBaseline targetBaseline)
    {
      this.targetBaseline = targetBaseline;
    }

    public CDOBranch getTargetBranch()
    {
      return targetBranch;
    }

    public void setTargetBranch(CDOBranch targetBranch)
    {
      this.targetBranch = targetBranch;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("LMMergeInfos[session=");
      builder.append(session);
      builder.append(", sourceBaseline=");
      builder.append(sourceBaseline);
      builder.append(", sourceBranchPoint=");
      builder.append(sourceBranchPoint);
      builder.append(", targetBaseline=");
      builder.append(targetBaseline);
      builder.append(", targetBranch=");
      builder.append(targetBranch);
      builder.append("]");
      return builder.toString();
    }

  }
}
