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
package org.eclipse.emf.cdo.lm.reviews.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine;
import org.eclipse.emf.cdo.session.CDOSession;

/**
 * @author Eike Stepper
 */
public final class ServerReviewStatemachine<REVIEW extends Review> extends ReviewStatemachine.Server<REVIEW>
{
  private final ReviewManager reviewManager;

  public ServerReviewStatemachine(ReviewManager reviewManager, boolean dropReviews)
  {
    super(dropReviews);
    this.reviewManager = reviewManager;
  }

  @Override
  protected void handleCommitInSource(Review review)
  {
  }

  @Override
  protected void handleCommitInTarget(Review review)
  {
  }

  @Override
  protected ReviewStatus handleRestoreFinish(REVIEW review)
  {
    if (review instanceof DeliveryReview)
    {
      DeliveryReview deliveryReview = (DeliveryReview)review;

      String moduleName = deliveryReview.getModule().getName();
      CDOSession moduleSession = reviewManager.getLifecycleManager().getModuleSession(moduleName);

      boolean sourceOutdated = isOutdated(moduleSession, deliveryReview.getSourceChange(), deliveryReview.getSourceCommit());
      boolean targetOutdated = isOutdated(moduleSession, deliveryReview.getStream(), deliveryReview.getTargetCommit());

      return ReviewStatus.getOutdated(sourceOutdated, targetOutdated);
    }

    return ReviewStatus.NEW;
  }

  private boolean isOutdated(CDOSession moduleSession, FloatingBaseline floatingBaseline, long expectedTimeStamp)
  {
    CDOBranch baselineBranch = floatingBaseline.getBranch().resolve(moduleSession.getBranchManager());

    long sourceLastCommit = moduleSession.getCommitInfoManager().getLastCommitOfBranch(baselineBranch, true);
    if (sourceLastCommit > expectedTimeStamp)
    {
      return true;
    }

    return false;
  }
}
