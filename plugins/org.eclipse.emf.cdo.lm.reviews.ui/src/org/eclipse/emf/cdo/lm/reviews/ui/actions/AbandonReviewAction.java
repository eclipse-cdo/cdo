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
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class AbandonReviewAction extends AbstractReviewAction
{
  public AbandonReviewAction(IWorkbenchPage page, Review review)
  {
    super(page, //
        "Abandon" + INTERACTIVE, //
        "Abandon the review", //
        OM.getImageDescriptor("icons/Abandon.png"), //
        "Abandon the review.", //
        "icons/wizban/AbandonReview.png", //
        review);
  }

  @Override
  protected void preRun(Review review, ISystemDescriptor systemDescriptor)
  {
  }

  @Override
  protected void fillDialogArea(LMAction<Review>.LMDialog dialog, Composite parent, Review review, ISystemDescriptor systemDescriptor)
  {
  }

  @Override
  protected void doRun(Review review, ISystemDescriptor systemDescriptor, IProgressMonitor monitor) throws Exception
  {
    ClientReviewStatemachine<Review> reviewStatemachine = ClientReviewStatemachine.of(review);
    reviewStatemachine.process(review, ReviewEvent.Abandon, null);
  }
}
