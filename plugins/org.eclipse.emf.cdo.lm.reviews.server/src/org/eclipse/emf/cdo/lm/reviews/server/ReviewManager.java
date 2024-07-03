/*
 * Copyright (c) 2022-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.ReviewEvent;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager.ModuleCommitEvent;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager.NewBaselineEvent;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager.SystemCommitEvent;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class ReviewManager extends Lifecycle implements LMPackage.Literals, ReviewsPackage.Literals
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.lm.reviews.server.reviewManagers"; //$NON-NLS-1$

  public static final String DEFAULT_TYPE = "default"; //$NON-NLS-1$

  private static final String PROP_LAST_REVIEW_ID = "org.eclipse.emf.cdo.lm.reviews.lastReviewID";

  private static final String UNKNOWN = "unknown";

  private final ServerReviewStatemachine<DeliveryReview> deliveriesReviewStatemachine = new ServerReviewStatemachine<>(this, false);

  private final ServerReviewStatemachine<DropReview> dropsReviewStatemachine = new ServerReviewStatemachine<>(this, true);

  private AbstractLifecycleManager lifecycleManager;

  private IListener lifecycleManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof SystemCommitEvent)
      {
        SystemCommitEvent e = (SystemCommitEvent)event;
        handleSystemCommit(e);
      }
      else if (event instanceof ModuleCommitEvent)
      {
        ModuleCommitEvent e = (ModuleCommitEvent)event;
        handleModuleCommit(e);
      }
      else if (event instanceof NewBaselineEvent)
      {
        NewBaselineEvent e = (NewBaselineEvent)event;
        handleNewBaseline(e);
      }
    }
  };

  private ExecutorService executorService;

  private int lastReviewID;

  public ReviewManager()
  {
  }

  public AbstractLifecycleManager getLifecycleManager()
  {
    return lifecycleManager;
  }

  public void setLifecycleManager(AbstractLifecycleManager lifecycleManager)
  {
    checkInactive();
    this.lifecycleManager = lifecycleManager;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    CheckUtil.checkState(lifecycleManager, "lifecycleManager");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    executorService = ConcurrencyUtil.getExecutorService(lifecycleManager.getContainer());
    loadLastReviewID();

    lifecycleManager.addListener(lifecycleManagerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    lifecycleManager.removeListener(lifecycleManagerListener);

    saveLastReviewID();
    executorService = null;
    super.doDeactivate();
  }

  protected void handleSystemCommit(SystemCommitEvent event)
  {
    CommitContext commitContext = event.getCommitContext();
    long timeStamp = commitContext.getBranchPoint().getTimeStamp();

    for (InternalCDORevisionDelta revisionDelta : commitContext.getDirtyObjectDeltas())
    {
      ReviewStatus status = getReviewStatus(revisionDelta);
      if (status != null)
      {
        CDOID id = revisionDelta.getID();
        CDOView systemView = lifecycleManager.getSystem().cdoView();

        executorService.submit(() -> {
          systemView.waitForUpdate(timeStamp);

          CDOObject reviewObject = systemView.getObject(id);
          if (reviewObject instanceof Review)
          {
            Review review = (Review)reviewObject;
            handleReviewStateChanged(review, status);
          }
        });
      }
    }
  }

  protected void handleReviewStateChanged(Review review, ReviewStatus status)
  {
    if (status == ReviewStatus.RESTORING)
    {
      ServerReviewStatemachine<Review> reviewStatemachine = getReviewStatemachine(review);
      reviewStatemachine.process(review, ReviewEvent.Finish, null);
    }
  }

  protected void handleModuleCommit(ModuleCommitEvent event)
  {
    Map<String, String> commitProperties = event.getCommitContext().getCommitProperties();
    if (commitProperties.containsKey(ReviewStatemachine.PROP_SUBMITTING))
    {
      // This is the commit of a SubmitReview action.
      // Don't touch the review status!
      return;
    }

    FloatingBaseline baseline = event.getCommitBaseline();
    if (baseline instanceof Stream)
    {
      Stream stream = (Stream)baseline;

      stream.forEachBaseline(content -> {
        if (content instanceof DeliveryReview)
        {
          DeliveryReview review = (DeliveryReview)content;
          deliveriesReviewStatemachine.process(review, ReviewEvent.CommitInTarget, null);
        }
      });
    }
    else if (baseline instanceof Change)
    {
      Change change = (Change)baseline;

      forEachAnnotationObject(change, true, DeliveryReview.class, //
          review -> deliveriesReviewStatemachine.process(review, ReviewEvent.CommitInSource, null));
    }
  }

  protected void handleNewBaseline(NewBaselineEvent event)
  {
    CDORevision baseline = event.getNewBaseline();
    EClass eClass = baseline.getEClass();

    if (isReviewClass(eClass))
    {
      int reviewID;
      synchronized (this)
      {
        reviewID = ++lastReviewID;
      }

      CDOID cdoid = baseline.getID();
      CommitContext commitContext = event.getCommitContext();

      commitContext.modify(context -> {
        for (CDOIDAndVersion cdoidAndVersion : context.getChangeSetData().getNewObjects())
        {
          if (cdoidAndVersion.getID() == cdoid)
          {
            InternalCDORevision reviewRevision = (InternalCDORevision)cdoidAndVersion;
            handleNewReview(commitContext, reviewRevision, reviewID);
            break;
          }
        }
      });
    }
  }

  protected void handleNewReview(CommitContext commitContext, InternalCDORevision reviewRevision, int reviewID)
  {
    reviewRevision.set(REVIEW__ID, 0, reviewID);

    if (reviewRevision.getEClass() == DELIVERY_REVIEW)
    {
      CDOID changeID = (CDOID)reviewRevision.data().get(DELIVERY_REVIEW__SOURCE_CHANGE, 0);
      CDORevision changeRevision = commitContext.getRevision(changeID);
      String changeBranchPath = (String)changeRevision.data().get(CHANGE__BRANCH, 0);

      CDOID streamID = (CDOID)reviewRevision.data().getContainerID();
      CDORevision streamRevision = commitContext.getRevision(streamID);
      String streamBranchPath = (String)streamRevision.data().get(STREAM__MAINTENANCE_BRANCH, 0);
      if (streamBranchPath == null)
      {
        streamBranchPath = (String)streamRevision.data().get(STREAM__DEVELOPMENT_BRANCH, 0);
      }

      CDOID moduleID = (CDOID)streamRevision.data().getContainerID();
      CDORevision moduleRevision = commitContext.getRevision(moduleID);
      String moduleName = (String)moduleRevision.data().get(MODULE__NAME, 0);

      CDOSession moduleSession = lifecycleManager.getModuleSession(moduleName);
      CDOBranchManager branchManager = moduleSession.getBranchManager();
      CDOCommitInfoManager commitInfoManager = moduleSession.getCommitInfoManager();

      CDOBranch changeBranch = branchManager.getBranch(changeBranchPath);
      long lastChangeCommit = commitInfoManager.getLastCommitOfBranch(changeBranch, true);

      CDOBranch streamBranch = branchManager.getBranch(streamBranchPath);
      long lastStreamCommit = commitInfoManager.getLastCommitOfBranch(streamBranch, true);

      CDOBranch reviewBranch = streamBranch.createBranch("review-" + reviewID);
      reviewRevision.set(DELIVERY_REVIEW__BRANCH, 0, reviewBranch.getPathName());
      reviewRevision.set(DELIVERY_REVIEW__SOURCE_COMMIT, 0, lastChangeCommit);
      reviewRevision.set(DELIVERY_REVIEW__TARGET_COMMIT, 0, lastStreamCommit);
    }
  }

  private void loadLastReviewID()
  {
    InternalStore store = lifecycleManager.getSystemRepository().getStore();
    Map<String, String> properties = store.getPersistentProperties(Collections.singleton(PROP_LAST_REVIEW_ID));

    String property = properties.get(PROP_LAST_REVIEW_ID);
    if (property != null)
    {
      if (property.equals(UNKNOWN))
      {
        lifecycleManager.getSystem().forEachBaseline(baseline -> {
          if (baseline instanceof Review)
          {
            Review review = (Review)baseline;
            lastReviewID = Math.max(lastReviewID, review.getId());
          }
        });
      }
      else
      {
        lastReviewID = Integer.parseInt(property);
      }
    }

    properties.put(PROP_LAST_REVIEW_ID, UNKNOWN);
    store.setPersistentProperties(properties);
  }

  private void saveLastReviewID()
  {
    Map<String, String> properties = new HashMap<>();
    properties.put(PROP_LAST_REVIEW_ID, Integer.toString(lastReviewID));

    InternalStore store = lifecycleManager.getSystemRepository().getStore();
    store.setPersistentProperties(properties);
  }

  @SuppressWarnings("unchecked")
  private <REVIEW extends Review> ServerReviewStatemachine<REVIEW> getReviewStatemachine(REVIEW review)
  {
    if (review instanceof DeliveryReview)
    {
      return (ServerReviewStatemachine<REVIEW>)deliveriesReviewStatemachine;
    }

    if (review instanceof DropReview)
    {
      return (ServerReviewStatemachine<REVIEW>)dropsReviewStatemachine;
    }

    return null;
  }

  private static ReviewStatus getReviewStatus(InternalCDORevisionDelta revisionDelta)
  {
    if (isReviewClass(revisionDelta.getEClass()))
    {
      CDOSetFeatureDelta statusDelta = (CDOSetFeatureDelta)revisionDelta.getFeatureDelta(REVIEW__STATUS);
      if (statusDelta != null)
      {
        int value = (int)statusDelta.getValue();

        try
        {
          return ReviewStatus.get(value);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }
    }

    return null;
  }

  private static boolean isReviewClass(EClass eClass)
  {
    return eClass == DELIVERY_REVIEW || eClass == DROP_REVIEW;
  }

  private static <T extends EObject> void forEachAnnotationObject(ModelElement modelElement, boolean referenced, Class<T> type, Consumer<T> consumer)
  {
    Annotation annotation = ReviewsPackage.getAnnotation(modelElement, false);
    if (annotation != null)
    {
      EList<EObject> objects = referenced ? annotation.getReferences() : annotation.getContents();

      for (EObject object : objects)
      {
        if (type.isInstance(object))
        {
          consumer.accept(type.cast(object));
        }
      }
    }
  }
}
