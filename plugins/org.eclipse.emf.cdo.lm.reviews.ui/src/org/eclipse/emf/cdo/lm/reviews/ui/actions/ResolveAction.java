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
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicStatus;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ResolveAction extends LMAction<Topic>
{
  public ResolveAction(IWorkbenchPage page, Topic topic)
  {
    super(page, //
        "Resolve", //
        "Resolve the comment", //
        OM.getImageDescriptor("icons/Resolve.gif"), //
        null, //
        null, //
        topic);
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
  protected void doRun(Topic topic, IProgressMonitor monitor) throws Exception
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(topic);
    systemDescriptor.modify(topic, t -> {
      t.setStatus(TopicStatus.RESOLVED);
      return null;
    }, monitor);

  }
}
