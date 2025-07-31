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
package org.eclipse.emf.cdo.lm.reviews.ui.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.RebaseToTargetResult;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.ReviewEvent;
import org.eclipse.emf.cdo.lm.reviews.ui.ClientReviewStatemachine;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.InteractiveDeliveryMerger;
import org.eclipse.emf.cdo.lm.util.LMMerger2;
import org.eclipse.emf.cdo.lm.util.LMMerger2.LMMergeInfos;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class RebaseToTargetAction extends AbstractReviewAction
{
  public static final String OPERATION_ID = "org.eclipse.emf.cdo.lm.reviews.ui.RebaseToTargetReview".intern();

  public RebaseToTargetAction(IWorkbenchPage page, Review review)
  {
    super(page, //
        "Rebase To Target" + INTERACTIVE, //
        "Rebase the review to the target stream '" + review.getStream().getName() + "'", //
        OM.getImageDescriptor("icons/RebaseToTarget.png"), //
        "Rebase the review to the target stream '" + review.getStream().getName() + "'.", //
        "icons/wizban/RebaseToTarget.png", //
        review);
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return OPERATION_ID;
  }

  @Override
  protected boolean isDialogNeeded()
  {
    return false;
  }

  @Override
  protected void preRun(Review review, ISystemDescriptor systemDescriptor)
  {
    // Do nothing.
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent, Review review, ISystemDescriptor systemDescriptor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doRun(Review review, ISystemDescriptor systemDescriptor, IProgressMonitor monitor) throws Exception
  {
    DeliveryReview deliveryReview = (DeliveryReview)review;
    String moduleName = deliveryReview.getModule().getName();
    RebaseToTargetResult result = new RebaseToTargetResult();

    systemDescriptor.withModuleSession(moduleName, moduleSession -> {
      CDOBranchManager branchManager = moduleSession.getBranchManager();
      CDOCommitInfoManager commitInfoManager = moduleSession.getCommitInfoManager();

      CDOBranchRef reviewBranchRef = deliveryReview.getBranch();
      CDOBranch reviewBranch = reviewBranchRef.resolve(branchManager);

      long lastReviewCommitTime = commitInfoManager.getLastCommitOfBranch(reviewBranch, true);
      CDOBranchPoint lastReviewCommit = reviewBranch.getPoint(lastReviewCommitTime);

      CDOBranchRef streamBranchRef = deliveryReview.getStream().getBranch();
      CDOBranch streamBranch = streamBranchRef.resolve(branchManager);

      long lastStreamCommitTime = commitInfoManager.getLastCommitOfBranch(streamBranch, true);
      result.targetCommit = lastStreamCommitTime;

      int rebaseCount = deliveryReview.getRebaseCount() + 1;
      CDOBranch rebaseBranch = streamBranch.createBranch("review-" + deliveryReview.getId() + "-" + rebaseCount, lastStreamCommitTime);
      result.rebaseBranch = new CDOBranchRef(rebaseBranch);

      LMMergeInfos infos = new LMMergeInfos();
      infos.setSession(moduleSession);
      infos.setSourceBaseline(deliveryReview);
      infos.setSourceBranchPoint(lastReviewCommit);
      infos.setTargetBaseline(deliveryReview);
      infos.setTargetBranch(rebaseBranch);

      LMMerger2 merger = new InteractiveDeliveryMerger();
      result.success = merger.mergeDelivery(infos) != CDOBranchPoint.INVALID_DATE;
    });

    ClientReviewStatemachine.DELIVERIES.process(deliveryReview, ReviewEvent.RebaseToTarget, result);
    updateCheckouts(review, systemDescriptor, result.rebaseBranch);
  }

  private static void updateCheckouts(Review review, ISystemDescriptor systemDescriptor, CDOBranchRef rebaseBranchRef)
  {
    CDOID baselineID = review.cdoID();
    CDOBranchPointRef headRef = rebaseBranchRef.getHeadRef();

    for (IAssemblyDescriptor assemblyDescriptor : IAssemblyManager.INSTANCE.getDescriptors())
    {
      if (assemblyDescriptor.getSystemDescriptor() == systemDescriptor //
          && assemblyDescriptor.getBaseline().cdoID() == baselineID)
      {
        assemblyDescriptor.getCheckout().setBranchPoint(headRef);
      }
    }
  }
}
