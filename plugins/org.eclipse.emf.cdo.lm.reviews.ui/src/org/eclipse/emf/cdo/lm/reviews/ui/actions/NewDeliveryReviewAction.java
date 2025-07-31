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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.MergeFromSourceResult;
import org.eclipse.emf.cdo.lm.reviews.provider.ReviewsEditPlugin;
import org.eclipse.emf.cdo.lm.ui.InteractiveDeliveryMerger;
import org.eclipse.emf.cdo.lm.ui.actions.LMAction;
import org.eclipse.emf.cdo.lm.ui.widgets.BaselineComposite;
import org.eclipse.emf.cdo.lm.util.LMMerger2;
import org.eclipse.emf.cdo.lm.util.LMMerger2.LMMergeInfos;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewDeliveryReviewAction extends LMAction.NewElement<Stream>
{
  public static final String OPERATION_ID = "org.eclipse.emf.cdo.lm.reviews.ui.CreateDeliveryReview".intern();

  private boolean changeWasNull;

  private EList<Baseline> possibleChanges;

  private Change change;

  public NewDeliveryReviewAction(IWorkbenchPage page, StructuredViewer viewer, Stream stream, Change change)
  {
    super(page, viewer, //
        (change == null ? "New Delivery Review" : "New Delivery Review for " + stream.getTypeAndName()) + INTERACTIVE, //
        "Add a new delivery review to stream '" + stream.getName() + "'", //
        ExtendedImageRegistry.INSTANCE.getImageDescriptor(ReviewsEditPlugin.INSTANCE.getImage("full/obj16/DeliveryReview")), //
        "Add a new delivery review to stream '" + stream.getName() + "'.", //
        "icons/wizban/NewDelivery.png", //
        stream);
    this.change = change;
    changeWasNull = change == null;
  }

  @Override
  public String getAuthorizableOperationID()
  {
    return OPERATION_ID;
  }

  @Override
  protected void preRun() throws Exception
  {
    possibleChanges = new BasicEList<>();

    if (changeWasNull)
    {
      Stream stream = getContext();
      stream.forEachBaseline(baseline -> {
        if (baseline instanceof Change)
        {
          Change change = (Change)baseline;

          if (change.getDeliveryPoint(stream) == null)
          {
            possibleChanges.add(change);
          }
        }
      });

      possibleChanges.sort(Baseline.COMPARATOR);
    }
    else
    {
      possibleChanges.add(change);
    }

    if (change == null && !possibleChanges.isEmpty())
    {
      change = (Change)possibleChanges.get(0);
    }

    super.preRun();
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    {
      Label label = new Label(parent, SWT.NONE);
      label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
      label.setText("Change:");

      BaselineComposite changeComposite = new BaselineComposite(parent, SWT.NONE, possibleChanges, getContext());
      changeComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

      if (change != null)
      {
        changeComposite.setBaseline(change);
      }

      changeComposite.addModifyListener((control, baseline) -> {
        change = (Change)baseline;
        validateDialog();
      });

      if (!changeWasNull)
      {
        label.setEnabled(false);
        changeComposite.setEnabled(false);
      }
    }
  }

  @Override
  protected String doValidate(LMDialog dialog)
  {
    if (change == null)
    {
      return "A change must be selected.";
    }

    return super.doValidate(dialog);
  }

  @Override
  protected CDOObject newElement(Stream stream, IProgressMonitor monitor) throws Exception
  {
    // The following creates and commits a DeliveryReview object.
    // The respective branch in the module repository is created by the server:
    // org.eclipse.emf.cdo.lm.reviews.server.ReviewManager.handleNewReview()

    ISystemDescriptor systemDescriptor = ISystemManager.INSTANCE.getDescriptor(stream);
    String author = systemDescriptor.getSystemRepository().getCredentials().getUserID();

    DeliveryReview review = systemDescriptor.modify(stream, s -> {
      Change c = s.cdoView().getObject(change);

      DeliveryReview r = ReviewsFactory.eINSTANCE.createDeliveryReview();
      r.setAuthor(author);
      r.setSourceChange(c);
      s.insertContent(r);

      Annotation annotation = ReviewsPackage.getAnnotation(c, true);
      annotation.getReferences().add(r);
      return r;
    }, monitor);

    mergeFromSource(systemDescriptor, review, stream);
    return review;
  }

  public static MergeFromSourceResult mergeFromSource(ISystemDescriptor systemDescriptor, DeliveryReview review, FloatingBaseline targetBaseline)
  {
    MergeFromSourceResult result = new MergeFromSourceResult();

    Change sourceChange = review.getSourceChange();
    String moduleName = review.getModule().getName();

    systemDescriptor.withModuleSession(moduleName, session -> {
      CDOBranchRef sourceBranchRef = sourceChange.getBranch();
      CDOBranchRef targetBranchRef = review.getBranch();

      CDOBranchManager branchManager = session.getBranchManager();
      CDOBranch sourceBranch = sourceBranchRef.resolve(branchManager);
      CDOBranch targetBranch = targetBranchRef.resolve(branchManager);

      CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
      long sourceCommit = commitInfoManager.getLastCommitOfBranch(sourceBranch, true);
      CDOBranchPoint sourceBranchPoint = sourceBranch.getPoint(sourceCommit);

      LMMergeInfos infos = new LMMergeInfos();
      infos.setSession(session);
      infos.setSourceBaseline(sourceChange);
      infos.setSourceBranchPoint(sourceBranchPoint);
      infos.setTargetBaseline(targetBaseline);
      infos.setTargetBranch(targetBranch);

      LMMerger2 merger = new InteractiveDeliveryMerger();
      result.targetCommit = merger.mergeDelivery(infos);
      result.sourceCommit = sourceCommit;
    });

    return result;
  }
}
