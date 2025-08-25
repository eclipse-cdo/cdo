/*
 * Copyright (c) 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.provider.ReviewsEditPlugin;
import org.eclipse.emf.cdo.lm.reviews.util.ReviewsOperations;
import org.eclipse.emf.cdo.lm.ui.actions.CheckoutAction;
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewDropReviewAction extends LMAction.NewElement<Delivery>
{
  private final DropType dropType;

  private String dropLabel;

  private boolean checkout;

  public NewDropReviewAction(IWorkbenchPage page, StructuredViewer viewer, Delivery delivery, DropType dropType)
  {
    super(page, viewer, //
        "New " + dropType.getName() + " Review" + INTERACTIVE, //
        "Add a new " + dropType.getName().toLowerCase() + " review to stream '" + delivery.getStream().getName() + "'", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(ReviewsEditPlugin.INSTANCE.getImage( //
            dropType.isRelease() ? "full/obj16/ReleaseReview" : "full/obj16/DropReview")), //
        "Add a new " + dropType.getName().toLowerCase() + " review to stream '" + delivery.getStream().getName() + "'.", //
        dropType.isRelease() ? "icons/wizban/NewRelease.png" : "icons/wizban/NewDrop.png", //
        delivery);
    this.dropType = dropType;
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return ReviewsOperations.CREATE_DROP_REVIEW;
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    Delivery delivery = getContext();
    dropLabel = delivery.getVersion().toString();

    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Version:");

      Text versionText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
      versionText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      versionText.setText(dropLabel);
    }

    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Drop Label:");

      Text labelText = new Text(parent, SWT.BORDER | SWT.SINGLE);
      labelText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
      labelText.setText(dropLabel);
      labelText.addModifyListener(e -> {
        dropLabel = labelText.getText();
        validateDialog();
      });
      labelText.setFocus();
      labelText.selectAll();
    }

    {
      Button button = newCheckBox(parent, "Checkout");
      button.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
        checkout = button.getSelection();
        validateDialog();
      }));
    }
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (dropLabel == null || dropLabel.isEmpty())
    {
      return "A drop label must be entered.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected CDOObject newElement(Delivery delivery, IProgressMonitor monitor) throws Exception
  {
    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(delivery);
    String author = systemDescriptor.getSystemRepository().getCredentials().getUserID();

    DropReview review = systemDescriptor.modify(delivery, d -> {
      DropReview r = ReviewsFactory.eINSTANCE.createDropReview();
      r.setDelivery(delivery);
      r.setDropType(dropType);
      r.setDropLabel(dropLabel);
      r.setVersion(d.getVersion());
      r.getDependencies().addAll(EcoreUtil.copyAll(d.getDependencies()));
      r.setAuthor(author);
      d.getStream().insertContent(r);

      Annotation annotation = ReviewsPackage.getAnnotation(d, true);
      annotation.getReferences().add(r);
      return r;
    }, monitor);

    if (checkout)
    {
      if (review != null)
      {
        try
        {
          String checkoutLabel = getCheckoutLabel(review);
          CheckoutAction.checkout(review, checkoutLabel, SubMonitor.convert(monitor, 1));
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
      else
      {
        monitor.worked(1);
      }
    }

    return review;
  }

  private String getCheckoutLabel(DropReview review)
  {
    // TODO Move to NamingStrategy.
    if (StringUtil.isEmpty(dropLabel))
    {
      return "";
    }

    return "Module " + getContext().getModule().getName() + " - " + dropType.getName() + " Review " + review.getId() + " - " + dropLabel;
  }
}
