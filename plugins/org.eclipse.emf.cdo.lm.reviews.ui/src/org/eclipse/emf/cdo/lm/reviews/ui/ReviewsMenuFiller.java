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

import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.CommentStatus;
import org.eclipse.emf.cdo.lm.reviews.Commentable;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.provider.ReviewsEditPlugin;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.AbandonReviewAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.DeleteReviewAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.MergeFromSourceAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.NewCommentAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.NewDeliveryReviewAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.NewDropReviewAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.OpenReviewAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.RebaseToTargetAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.ResolveCommentAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.RestoreReviewAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.SubmitReviewAction;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.UnresolveCommentAction;
import org.eclipse.emf.cdo.lm.ui.actions.CheckoutAction;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.MenuFiller;

import org.eclipse.emf.common.util.EList;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ReviewsMenuFiller implements MenuFiller
{
  public ReviewsMenuFiller()
  {
  }

  @Override
  public boolean fillMenu(IWorkbenchPage page, StructuredViewer viewer, IMenuManager menu, Object selectedElement)
  {
    int oldSize = menu.getItems().length;

    if (selectedElement instanceof Stream)
    {
      Stream stream = (Stream)selectedElement;
      menu.add(new NewDeliveryReviewAction(page, viewer, stream, null));
    }
    else if (selectedElement instanceof Change)
    {
      Change change = (Change)selectedElement;

      for (Stream s : change.getModule().getStreams())
      {
        if (change.getDeliveryPoint(s) == null)
        {
          menu.add(new NewDeliveryReviewAction(page, viewer, s, change));
        }
      }
    }
    else if (selectedElement instanceof Delivery)
    {
      Delivery delivery = (Delivery)selectedElement;

      EList<DropType> possibleDropTypes = delivery.getSystem().getProcess().getDropTypes();
      for (DropType dropType : possibleDropTypes)
      {
        menu.add(new NewDropReviewAction(page, viewer, delivery, dropType));
      }
    }
    else if (selectedElement instanceof Review)
    {
      Review review = (Review)selectedElement;

      if (OpenReviewAction.ENABLED)
      {
        menu.add(new OpenReviewAction(page, review));
      }

      menu.add(new CheckoutAction(page, ReviewsEditPlugin.INSTANCE, null, review));
      menu.add(new Separator());

      ReviewStatus status = review.getStatus();
      switch (status)
      {
      case NEW:
        addSubmitAction(page, viewer, menu, review);
        menu.add(new AbandonReviewAction(page, review));
        menu.add(new DeleteReviewAction(page, review));
        break;

      case SOURCE_OUTDATED:
        menu.add(new MergeFromSourceAction(page, review));
        addSubmitAction(page, viewer, menu, review);
        menu.add(new AbandonReviewAction(page, review));
        menu.add(new DeleteReviewAction(page, review));
        break;

      case TARGET_OUTDATED:
        menu.add(new RebaseToTargetAction(page, review));
        menu.add(new AbandonReviewAction(page, review));
        menu.add(new DeleteReviewAction(page, review));
        break;

      case OUTDATED:
        menu.add(new MergeFromSourceAction(page, review));
        menu.add(new RebaseToTargetAction(page, review));
        menu.add(new AbandonReviewAction(page, review));
        menu.add(new DeleteReviewAction(page, review));
        break;

      case ABANDONED:
        menu.add(new RestoreReviewAction(page, review));
        break;

      default:
        break;
      }
    }

    if (selectedElement instanceof Commentable)
    {
      Commentable commentable = (Commentable)selectedElement;

      Review review = commentable.getReview();
      if (review == null || review.getStatus().isOpen())
      {
        menu.add(new Separator());
        menu.add(new NewCommentAction(page, viewer, commentable, true));
        menu.add(new NewCommentAction(page, viewer, commentable, false));

        if (commentable instanceof Comment)
        {
          Comment comment = (Comment)commentable;

          CommentStatus status = comment.getStatus();
          if (status == CommentStatus.UNRESOLVED)
          {
            menu.add(new Separator());
            menu.add(new ResolveCommentAction(page, comment));
          }
          else if (status == CommentStatus.NONE || status == CommentStatus.RESOLVED)
          {
            menu.add(new Separator());
            menu.add(new UnresolveCommentAction(page, comment));
          }
        }
      }
    }

    return menu.getItems().length > oldSize;
  }

  private void addSubmitAction(IWorkbenchPage page, StructuredViewer viewer, IMenuManager menu, Review review)
  {
    if (review.getUnresolvedCount() > 0)
    {
      return;
    }

    if (review instanceof DeliveryReview)
    {
      DeliveryReview deliveryReview = (DeliveryReview)review;
      Stream stream = deliveryReview.getStream();
      Change sourceChange = deliveryReview.getSourceChange();

      if (sourceChange.getDeliveryPoint(stream) != null)
      {
        return;
      }
    }

    menu.add(new SubmitReviewAction(page, viewer, review));
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends MenuFiller.Factory
  {
    public static final String TYPE = "lm.reviews";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public MenuFiller create(String description) throws ProductCreationException
    {
      return new ReviewsMenuFiller();
    }
  }
}
