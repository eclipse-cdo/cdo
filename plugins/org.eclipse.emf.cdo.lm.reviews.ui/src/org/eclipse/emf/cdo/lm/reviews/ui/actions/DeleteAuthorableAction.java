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
import org.eclipse.emf.cdo.lm.reviews.Authorable;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class DeleteAuthorableAction extends LMAction<Authorable>
{
  public DeleteAuthorableAction(IWorkbenchPage page, Authorable authorable)
  {
    super(page, //
        "Delete" + INTERACTIVE, //
        "Delete the " + getTypeLabel(authorable), //
        OM.getImageDescriptor("icons/Delete.gif"), //
        "Delete the " + getTypeLabel(authorable) + ".", //
        "icons/wizban/Delete.png", //
        authorable);
  }

  @Override
  protected void fillDialogArea(LMAction<Authorable>.LMDialog dialog, Composite parent)
  {
  }

  @Override
  protected void doRun(Authorable authorable, IProgressMonitor monitor) throws Exception
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(authorable);
    systemDescriptor.modify(authorable, a -> {
      EcoreUtil.remove(a);
      return true;
    }, monitor);
  }

  private static String getTypeLabel(Authorable authorable)
  {
    if (authorable instanceof Topic)
    {
      return "topic";
    }

    if (authorable instanceof Comment)
    {
      return "comment";
    }

    return "authorable";
  }
}
