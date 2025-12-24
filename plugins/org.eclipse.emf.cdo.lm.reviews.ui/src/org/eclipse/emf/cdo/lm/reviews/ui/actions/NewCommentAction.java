/*
 * Copyright (c) 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;
import org.eclipse.emf.cdo.lm.reviews.provider.ReviewsEditPlugin;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.reviews.util.ReviewsOperations;
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewCommentAction extends LMAction.NewElement<TopicContainer>
{
  private String text;

  public NewCommentAction(IWorkbenchPage page, StructuredViewer viewer, TopicContainer container)
  {
    super(page, viewer, //
        "New Comment" + INTERACTIVE, //
        "Add a new comment", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(ReviewsEditPlugin.INSTANCE.getImage("full/obj16/Comment")), //
        "Add a new comment.", //
        "icons/wizban/NewComment.png", //
        container);
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return ReviewsOperations.CREATE_COMMENT;
  }

  @Override
  protected ImageDescriptor getImageDescriptor(String imagePath)
  {
    return OM.getImageDescriptor(imagePath);
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).create());
      label.setText("Text:");

      Text textArea = new Text(parent, SWT.BORDER | SWT.MULTI);
      textArea.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
      textArea.addModifyListener(event -> {
        text = textArea.getText();
        validateDialog();
      });
    }
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (StringUtil.isEmpty(text))
    {
      return "A text must be entered.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected CDOObject newElement(TopicContainer container, IProgressMonitor monitor) throws Exception
  {
    Comment comment = ReviewsFactory.eINSTANCE.createComment();
    comment.setText(text);

    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(container);
    return systemDescriptor.modify(container, tc -> {
      tc.getComments().add(comment);
      return comment;
    }, monitor);
  }
}
