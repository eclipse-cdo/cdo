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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.internal.client.SystemDescriptor;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.ReviewEvent;
import org.eclipse.emf.cdo.lm.reviews.ui.ClientReviewStatemachine;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.lm.util.CoreDeliveryMerger;
import org.eclipse.emf.cdo.lm.util.LMMerger;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class SubmitReviewAction extends AbstractReviewAction
{
  private final StructuredViewer viewer;

  private IAssemblyDescriptor[] assemblyDescriptors = {};

  private boolean deleteCheckouts;

  public SubmitReviewAction(IWorkbenchPage page, StructuredViewer viewer, Review review)
  {
    super(page, //
        "Submit" + INTERACTIVE, //
        "Submit the review to stream '" + review.getStream().getName() + "'", //
        OM.getImageDescriptor("icons/Submit.png"), //
        "Submit the review to stream '" + review.getStream().getName() + "'.", //
        "icons/wizban/SubmitReview.png", //
        review);
    this.viewer = viewer;
  }

  @Override
  protected void preRun(Review review, ISystemDescriptor systemDescriptor)
  {
    assemblyDescriptors = IAssemblyManager.INSTANCE.getDescriptors(review);
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent, Review review, ISystemDescriptor systemDescriptor)
  {
    {
      Button button = newCheckBox(parent, "Delete checkout(s)");
      button.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
        deleteCheckouts = button.getSelection();
        validateDialog();
      }));

      button.setSelection(deleteCheckouts);
      button.setEnabled(assemblyDescriptors.length != 0);
    }
  }

  @Override
  protected void doRun(Review review, ISystemDescriptor systemDescriptor, IProgressMonitor monitor) throws Exception
  {
    Object[] data = { null };
    Stream stream = review.getStream();

    if (review instanceof DeliveryReview)
    {
      DeliveryReview deliveryReview = (DeliveryReview)review;

      String moduleName = deliveryReview.getModule().getName();

      systemDescriptor.withModuleSession(moduleName, moduleSession -> {
        CDOBranchRef sourceBranchRef = deliveryReview.getBranch();
        CDOBranchRef targetBranchRef = stream.getBranch();

        CDOBranchManager branchManager = moduleSession.getBranchManager();
        CDOBranch sourceBranch = sourceBranchRef.resolve(branchManager);
        CDOBranch targetBranch = targetBranchRef.resolve(branchManager);

        CDOCommitInfoManager commitInfoManager = moduleSession.getCommitInfoManager();
        long sourceCommitTime = commitInfoManager.getLastCommitOfBranch(sourceBranch, true);

        CDOBranchPointRef sourceBranchPointRef = sourceBranchRef.getPointRef(sourceCommitTime);
        CDOBranchPoint sourceBranchPoint = sourceBranch.getPoint(sourceCommitTime);

        LMMerger merger = new SubmitDeliveryMerger();
        long targetCommitTime = merger.mergeDelivery(moduleSession, sourceBranchPoint, targetBranch);

        if (targetCommitTime != CDOBranchPoint.INVALID_DATE)
        {
          CDOBranchPointRef targetBranchPointRef = stream.getBranch().getPointRef(targetCommitTime);

          ModuleDefinition moduleDefinition = systemDescriptor.extractModuleDefinition(stream, targetCommitTime);
          Version moduleVersion = moduleDefinition.getVersion();

          Delivery delivery = LMFactory.eINSTANCE.createDelivery();
          delivery.setVersion(moduleVersion);
          delivery.setMergeSource(sourceBranchPointRef);
          delivery.setMergeTarget(targetBranchPointRef);
          ((SystemDescriptor)systemDescriptor).addDependencies(moduleDefinition, delivery);

          data[0] = delivery;
        }
      });
    }
    else if (review instanceof DropReview)
    {
      DropReview dropReview = (DropReview)review;

      DropType dropType = dropReview.getDropType();
      long timeStamp = dropReview.getTargetTimeStamp();
      String dropLabel = dropReview.getDropLabel();

      data[0] = systemDescriptor.createDrop(stream, dropType, timeStamp, dropLabel, monitor);
    }

    if (data[0] != null)
    {
      ClientReviewStatemachine<Review> reviewStatemachine = ClientReviewStatemachine.of(review);
      reviewStatemachine.process(review, ReviewEvent.Submit, data[0]);

      if (deleteCheckouts)
      {
        for (IAssemblyDescriptor assemblyDescriptor : assemblyDescriptors)
        {
          try
          {
            CDOCheckout checkout = assemblyDescriptor.getCheckout();
            checkout.delete(true);
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      }

      EObject annotation = review.eContainer();
      EObject result = annotation.eContainer();
      UIUtil.asyncExec(() -> {
        viewer.setSelection(new StructuredSelection(result), true);

        if (viewer instanceof TreeViewer)
        {
          ((TreeViewer)viewer).setExpandedState(result, false);
        }
      });
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class SubmitDeliveryMerger extends CoreDeliveryMerger
  {
    public SubmitDeliveryMerger()
    {
    }

    @Override
    protected CDOTransaction openTransaction(CDOSession session, CDOBranch branch)
    {
      CDOTransaction transaction = super.openTransaction(session, branch);
      transaction.setCommitProperty(ReviewStatemachine.PROP_SUBMITTING, StringUtil.TRUE);
      return transaction;
    }
  }
}
