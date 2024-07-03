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
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.CommentStatus;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ResolveCommentAction extends LMAction<Comment>
{
  public ResolveCommentAction(IWorkbenchPage page, Comment comment)
  {
    super(page, //
        "Resolve", //
        "Resolve the comment", //
        OM.getImageDescriptor("icons/Resolve.gif"), //
        null, //
        null, //
        comment);
  }

  @Override
  protected boolean isDialogNeeded()
  {
    return false;
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    // Do nothing.
  }

  @Override
  protected void doRun(Comment comment, IProgressMonitor monitor) throws Exception
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(getContext().getSystem());
    systemDescriptor.modify(comment, c -> {
      c.setStatus(CommentStatus.RESOLVED);
      return null;
    }, monitor);

  }
}
