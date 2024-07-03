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
package org.eclipse.emf.cdo.lm.reviews.ui;

import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine;

/**
 * @author Eike Stepper
 */
public final class ClientReviewStatemachine<REVIEW extends Review> extends ReviewStatemachine.Client<REVIEW>
{
  public static final ClientReviewStatemachine<DeliveryReview> DELIVERIES = new ClientReviewStatemachine<>(false);

  public static final ClientReviewStatemachine<DropReview> DROPS = new ClientReviewStatemachine<>(true);

  private ClientReviewStatemachine(boolean dropReviews)
  {
    super(dropReviews);
  }

  @Override
  protected void handleMergeFromSource(REVIEW review, long sourceCommit)
  {
    DeliveryReview deliveryReview = (DeliveryReview)review;
    deliveryReview.setSourceCommit(sourceCommit);
  }

  @Override
  protected void handleRebaseToTarget(REVIEW review, CDOBranchRef rebaseBranch)
  {
    DeliveryReview deliveryReview = (DeliveryReview)review;
    deliveryReview.setRebaseCount(deliveryReview.getRebaseCount() + 1);
    deliveryReview.setBranch(rebaseBranch);
  }

  @Override
  protected void handleSubmit(REVIEW review, Object data)
  {
    Stream stream = review.getStream();

    if (review instanceof DeliveryReview)
    {
      DeliveryReview deliveryReview = (DeliveryReview)review;
      Change change = deliveryReview.getSourceChange();

      Delivery delivery = (Delivery)data;
      delivery.setChange(change);
      stream.insertContent(delivery);

      Annotation annotation = ReviewsPackage.getAnnotation(delivery, true);
      annotation.getContents().add(deliveryReview);

      setCommitComment(review, "Add delivery '" + delivery.getName() + "' to stream '" + stream.getName() + "'");
    }
    else if (review instanceof DropReview)
    {
      DropReview dropReview = (DropReview)review;

      Drop drop = (Drop)data;
      drop = dropReview.cdoView().getObject(drop);

      Annotation annotation = ReviewsPackage.getAnnotation(drop, true);
      annotation.getContents().add(dropReview);

      setCommitComment(review, "Add drop '" + drop.getName() + "' to stream '" + stream.getName() + "'");
    }
  }

  @Override
  protected void handleAbandon(REVIEW review)
  {
  }

  @Override
  protected void handleRestore(REVIEW review)
  {
    // String moduleName = review.getModule().getName();
    // ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(review);
    // systemDescriptor.withModuleSession(moduleName, session -> {
    //
    // });
  }

  @Override
  protected void handleDelete(REVIEW review)
  {
  }

  @SuppressWarnings("unchecked")
  public static <REVIEW extends Review> ClientReviewStatemachine<REVIEW> of(REVIEW review)
  {
    if (review instanceof DeliveryReview)
    {
      return (ClientReviewStatemachine<REVIEW>)DELIVERIES;
    }

    if (review instanceof DropReview)
    {
      return (ClientReviewStatemachine<REVIEW>)DROPS;
    }

    return null;
  }
}
