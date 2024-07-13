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
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class AbstractReviewAction extends LMAction<Review>
{
  private ISystemDescriptor systemDescriptor;

  public AbstractReviewAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, String bannerMessage, String bannerImagePath,
      Review review)
  {
    super(page, text, toolTipText, image, bannerMessage, bannerImagePath, review);
  }

  @Override
  protected ImageDescriptor getImageDescriptor(String imagePath)
  {
    return OM.getImageDescriptor(imagePath);
  }

  @Override
  protected final void preRun() throws Exception
  {
    Review review = getContext();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(review);

    preRun(review, systemDescriptor);
    super.preRun();
  }

  protected abstract void preRun(Review review, ISystemDescriptor systemDescriptor);

  @Override
  protected final void fillDialogArea(LMDialog dialog, Composite parent)
  {
    Review review = getContext();
    fillDialogArea(dialog, parent, review, systemDescriptor);
  }

  protected abstract void fillDialogArea(LMDialog dialog, Composite parent, Review review, ISystemDescriptor systemDescriptor);

  @Override
  protected final void doRun(Review review, IProgressMonitor monitor) throws Exception
  {
    doRun(review, systemDescriptor, monitor);
  }

  protected abstract void doRun(Review review, ISystemDescriptor systemDescriptor, IProgressMonitor monitor) throws Exception;
}
