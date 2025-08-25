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

import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.ReviewEvent;
import org.eclipse.emf.cdo.lm.reviews.ui.ClientReviewStatemachine;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.reviews.util.ReviewsOperations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class RestoreReviewAction extends AbstractReviewAction
{
  public RestoreReviewAction(IWorkbenchPage page, Review review)
  {
    super(page, //
        "Restore" + INTERACTIVE, //
        "Restore the review", //
        OM.getImageDescriptor("icons/Restore.png"), //
        "Restore the review.", //
        "icons/wizban/RestoreReview.png", //
        review);
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return ReviewsOperations.RESTORE_REVIEW;
  }

  @Override
  protected void preRun(Review review, ISystemDescriptor systemDescriptor)
  {
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent, Review review, ISystemDescriptor systemDescriptor)
  {
  }

  @Override
  protected void doRun(Review review, ISystemDescriptor systemDescriptor, IProgressMonitor monitor) throws Exception
  {
    ClientReviewStatemachine<Review> reviewStatemachine = ClientReviewStatemachine.of(review);
    reviewStatemachine.process(review, ReviewEvent.Restore, null);
  }
}
