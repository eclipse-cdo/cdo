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

import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.ui.actions.OpenReviewAction;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.OpenHandler;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ReviewsOpenHandler implements OpenHandler
{
  public ReviewsOpenHandler()
  {
  }

  @Override
  public boolean handleOpen(IWorkbenchPage page, StructuredViewer viewer, Object selectedElement)
  {
    if (selectedElement instanceof Review)
    {
      new OpenReviewAction(page, (Review)selectedElement, null).run();
      return true;
    }

    if (selectedElement instanceof Topic)
    {
      new OpenReviewAction(page, (Topic)selectedElement).run();
      return true;
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends OpenHandler.Factory
  {
    public static final String TYPE = "lm.reviews";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public OpenHandler create(String description) throws ProductCreationException
    {
      return new ReviewsOpenHandler();
    }
  }
}
