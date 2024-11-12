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
package org.eclipse.emf.cdo.lm.reviews.internal.server;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine;
import org.eclipse.emf.cdo.lm.reviews.impl.ReviewStatemachine.ReviewEvent;
import org.eclipse.emf.cdo.lm.reviews.internal.server.bundle.OM;
import org.eclipse.emf.cdo.lm.reviews.server.IReviewManager;
import org.eclipse.emf.cdo.lm.reviews.server.IReviewManagerEvent;
import org.eclipse.emf.cdo.lm.reviews.server.IReviewManagerEvent.Type;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager.ModuleCommitEvent;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager.NewBaselineEvent;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager.SystemCommitEvent;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Eike Stepper
 */
public class ReviewManager extends Lifecycle implements IReviewManager, LMPackage.Literals, ReviewsPackage.Literals
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.lm.reviews.server.reviewManagers"; //$NON-NLS-1$

  public static final String DEFAULT_TYPE = "default"; //$NON-NLS-1$

  private final ServerReviewStatemachine<DeliveryReview> deliveriesReviewStatemachine = new ServerReviewStatemachine<>(this, false);

  private final ServerReviewStatemachine<DropReview> dropsReviewStatemachine = new ServerReviewStatemachine<>(this, true);

  private AbstractLifecycleManager lifecycleManager;

  private final IListener lifecycleManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof SystemCommitEvent)
      {
        SystemCommitEvent e = (SystemCommitEvent)event;
        handleSystemCommitEvent(e);
      }
      else if (event instanceof ModuleCommitEvent)
      {
        ModuleCommitEvent e = (ModuleCommitEvent)event;
        handleModuleCommitEvent(e);
      }
      else if (event instanceof NewBaselineEvent)
      {
        NewBaselineEvent e = (NewBaselineEvent)event;
        handleNewBaselineEvent(e);
      }
    }
  };

  private final IDCounter reviewIDCounter = new IDCounter("org.eclipse.emf.cdo.lm.reviews.lastReviewID");

  private final IDCounter topicIDCounter = new IDCounter("org.eclipse.emf.cdo.lm.reviews.lastTopicID");

  private final IDCounter commentIDCounter = new IDCounter("org.eclipse.emf.cdo.lm.reviews.lastCommentID");

  private ExecutorService executorService;

  public ReviewManager()
  {
  }

  @Override
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
  public String toString()
  {
    String str = super.toString();

    if (lifecycleManager != null)
    {
      InternalRepository systemRepository = lifecycleManager.getSystemRepository();
      if (systemRepository != null)
      {
        str += "[systemRepository=" + systemRepository.getName() + "]";
      }
    }

    return str;
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

    lifecycleManager.addSystemCommitHandler(this::handleSystemCommit);
    lifecycleManager.addListener(lifecycleManagerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    lifecycleManager.removeListener(lifecycleManagerListener);
    lifecycleManager.removeSystemCommitHandler(this::handleSystemCommit);

    saveLastReviewID();
    executorService = null;
    super.doDeactivate();
  }

  protected void handleSystemCommit(CommitContext commitContext)
  {
    String author = commitContext.getUserID();
    long timeStamp = commitContext.getBranchPoint().getTimeStamp();

    InternalCDORevision[] newObjects = commitContext.getNewObjects();
    for (int i = 0; i < newObjects.length; i++)
    {
      InternalCDORevision revision = newObjects[i];
      EClass eClass = revision.getEClass();

      if (isAuthorableClass(eClass))
      {
        int index = i;
        int id = (eClass == TOPIC ? topicIDCounter : commentIDCounter).getNextID();

        commitContext.modify(modificationContext -> {
          List<CDOIDAndVersion> list = modificationContext.getChangeSetData().getNewObjects();
          InternalCDORevision element = (InternalCDORevision)list.get(index);
          element.setValue(AUTHORABLE__ID, id);
          element.setValue(AUTHORABLE__AUTHOR, author);
          element.setValue(AUTHORABLE__CREATION_TIME, timeStamp);
          element.setValue(AUTHORABLE__EDIT_TIME, null);
        });
      }
    }
  }

  protected void handleSystemCommitEvent(SystemCommitEvent systemCommitEvent)
  {
    CommitContext commitContext = systemCommitEvent.getCommitContext();
    long timeStamp = commitContext.getBranchPoint().getTimeStamp();

    IListener[] listeners = getListeners();
    Map<CDOID, ReviewManagerEvent> events = listeners.length == 0 ? null : new HashMap<>();

    ISession session = commitContext.getTransaction().getSession();
    CDOBranchPoint oldBranchPoint = CDOBranchUtil.adjustTime(commitContext.getBranchPoint(), -1L);

    ViewSupplier oldViewSupplier = new ViewSupplier(() -> CDOServerUtil.openView(session, oldBranchPoint));
    ViewSupplier newViewSupplier = new ViewSupplier(() -> CDOServerUtil.openView(commitContext));

    if (events != null)
    {
      InternalCDORevision[] newObjects = commitContext.getNewObjects();
      for (int i = 0; i < newObjects.length; i++)
      {
        InternalCDORevision newObject = newObjects[i];
        EClass eClass = newObject.getEClass();

        if (isReviewClass(eClass))
        {
          CDOID reviewID = newObject.getID();
          events.put(reviewID, new ReviewManagerEvent(this, Type.ReviewCreated, reviewID, oldViewSupplier, newViewSupplier));
        }
      }
    }

    for (InternalCDORevisionDelta dirtyObjectDelta : commitContext.getDirtyObjectDeltas())
    {
      EClass eClass = dirtyObjectDelta.getEClass();
      if (isReviewClass(eClass))
      {
        CDOID reviewID = dirtyObjectDelta.getID();

        if (events != null)
        {
          ReviewManagerEvent event = events.computeIfAbsent(reviewID,
              k -> new ReviewManagerEvent(this, Type.ReviewChanged, reviewID, oldViewSupplier, newViewSupplier));
          event.changedFeatures.addAll(dirtyObjectDelta.getFeatureDeltaMap().keySet());
        }

        ReviewStatus newStatus = getReviewStatus(dirtyObjectDelta);
        if (newStatus == ReviewStatus.RESTORING)
        {
          executorService.submit(() -> {
            CDOView systemView = lifecycleManager.getSystem().cdoView();
            systemView.waitForUpdate(timeStamp);

            CDOObject reviewObject = systemView.getObject(reviewID);
            if (reviewObject instanceof Review)
            {
              Review review = (Review)reviewObject;

              ServerReviewStatemachine<Review> reviewStatemachine = getReviewStatemachine(review);
              reviewStatemachine.process(review, ReviewEvent.Finish, null);
            }
          });
        }
      }
      else if (events != null && isAuthorableClass(eClass))
      {
        CDOID authorableID = dirtyObjectDelta.getID();

        InternalCDORevision reviewRevision = getReviewRevision(authorableID, commitContext);
        if (reviewRevision != null)
        {
          CDOID reviewID = reviewRevision.getID();

          ReviewManagerEvent event = events.computeIfAbsent(reviewID,
              k -> new ReviewManagerEvent(this, Type.ReviewChanged, reviewID, oldViewSupplier, newViewSupplier));
          event.changedFeatures.addAll(dirtyObjectDelta.getFeatureDeltaMap().keySet());
        }
      }
    }

    if (events != null)
    {
      for (ReviewManagerEvent event : events.values())
      {
        fireEvent(event, listeners);
      }

      newViewSupplier.deactivate();
      oldViewSupplier.deactivate();
    }
  }

  protected void handleModuleCommitEvent(ModuleCommitEvent event)
  {
    Map<String, String> commitProperties = event.getCommitContext().getCommitProperties();
    String value = commitProperties.get(ReviewStatemachine.PROP_SUBMITTING);
    int submittingReviewID = value == null ? -1 : Integer.valueOf(value);

    FloatingBaseline baseline = event.getCommitBaseline();
    if (baseline instanceof Stream)
    {
      Stream stream = (Stream)baseline;

      stream.forEachBaseline(content -> {
        if (content instanceof DeliveryReview)
        {
          DeliveryReview review = (DeliveryReview)content;

          if (review.getId() == submittingReviewID)
          {
            // This is the commit of the SubmitReview action.
            // Don't touch the review status!
            return;
          }

          deliveriesReviewStatemachine.process(review, ReviewEvent.CommitInTarget, null);
        }
      });
    }
    else if (baseline instanceof Change)
    {
      Change change = (Change)baseline;

      forEachAnnotationObject(change, true, DeliveryReview.class, review -> {
        if (review.getId() == submittingReviewID)
        {
          // This is the commit of a SubmitReview action.
          // Don't touch the review status!
          return;
        }

        deliveriesReviewStatemachine.process(review, ReviewEvent.CommitInSource, null);
      });
    }
  }

  protected void handleNewBaselineEvent(NewBaselineEvent event)
  {
    CDORevision baseline = event.getNewBaseline();
    EClass eClass = baseline.getEClass();

    if (isReviewClass(eClass))
    {
      int reviewID = reviewIDCounter.getNextID();
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

  private boolean containsUnknownID(Collection<String> values)
  {
    for (String value : values)
    {
      if (IDCounter.UNKNOWN.equals(value))
      {
        return true;
      }
    }

    return false;
  }

  private void initTopicAndCommentIDs(TopicContainer container)
  {
    for (Comment comment : container.getComments())
    {
      commentIDCounter.initID(comment.getId());
    }

    for (Topic topic : container.getTopics())
    {
      topicIDCounter.initID(topic.getId());
      initTopicAndCommentIDs(topic);
    }
  }

  private void loadLastReviewID()
  {
    Set<String> keys = new HashSet<>();
    keys.add(reviewIDCounter.propertyKey);
    keys.add(topicIDCounter.propertyKey);
    keys.add(commentIDCounter.propertyKey);

    InternalStore store = lifecycleManager.getSystemRepository().getStore();
    Map<String, String> properties = store.getPersistentProperties(keys);

    if (containsUnknownID(properties.values()))
    {
      OM.LOG.info("Restoring ID counters...");
      lifecycleManager.getSystem().forEachBaseline(baseline -> {
        if (baseline instanceof Review)
        {
          Review review = (Review)baseline;
          reviewIDCounter.initID(review.getId());
          initTopicAndCommentIDs(review);
        }
      });
    }
    else
    {
      reviewIDCounter.initID(properties);
      topicIDCounter.initID(properties);
      commentIDCounter.initID(properties);
    }

    for (String key : keys)
    {
      properties.put(key, IDCounter.UNKNOWN);
    }

    store.setPersistentProperties(properties);

    OM.LOG.info(reviewIDCounter.toString());
    OM.LOG.info(topicIDCounter.toString());
    OM.LOG.info(commentIDCounter.toString());
  }

  private void saveLastReviewID()
  {
    Map<String, String> properties = new HashMap<>();
    reviewIDCounter.addToMap(properties);
    topicIDCounter.addToMap(properties);
    commentIDCounter.addToMap(properties);

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

    return null;
  }

  private static InternalCDORevision getReviewRevision(CDOID id, CommitContext commitContext)
  {
    InternalCDORevision revision = (InternalCDORevision)commitContext.getRevision(id);
    if (revision != null)
    {
      return getReviewRevision(revision, commitContext);
    }

    return null;
  }

  private static InternalCDORevision getReviewRevision(InternalCDORevision revision, CDORevisionProvider revisionProvider)
  {
    EClass eClass = revision.getEClass();
    if (isReviewClass(eClass))
    {
      return revision;
    }

    if (isAuthorableClass(eClass))
    {
      CDOID containerID = (CDOID)revision.getContainerID();
      if (!CDOIDUtil.isNull(containerID))
      {
        InternalCDORevision container = (InternalCDORevision)revisionProvider.getRevision(containerID);
        if (container != null)
        {
          return getReviewRevision(container, revisionProvider);
        }
      }
    }

    return null;
  }

  private static boolean isReviewClass(EClass eClass)
  {
    return eClass == DELIVERY_REVIEW || eClass == DROP_REVIEW;
  }

  private static boolean isAuthorableClass(EClass eClass)
  {
    return eClass == TOPIC || eClass == COMMENT;
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

  /**
   * @author Eike Stepper
   */
  private static final class IDCounter
  {
    private static final String UNKNOWN = "unknown";

    private final String propertyKey;

    private int lastID;

    public IDCounter(String propertyKey)
    {
      this.propertyKey = propertyKey;
    }

    public synchronized int getNextID()
    {
      return ++lastID;
    }

    public void addToMap(Map<String, String> properties)
    {
      properties.put(propertyKey, Integer.toString(lastID));
    }

    public void initID(int id)
    {
      lastID = Math.max(lastID, id);
    }

    public void initID(Map<String, String> properties)
    {
      String property = properties.get(propertyKey);
      if (property != null)
      {
        lastID = Integer.parseInt(property);
      }
    }

    @Override
    public String toString()
    {
      return propertyKey + " = " + lastID;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ViewSupplier implements Supplier<CDOView>, IDeactivateable
  {
    private final Supplier<CDOView> viewOpener;

    private CDOView view;

    public ViewSupplier(Supplier<CDOView> viewOpener)
    {
      this.viewOpener = viewOpener;
    }

    @Override
    public synchronized CDOView get()
    {
      if (view == null)
      {
        view = viewOpener.get();
      }

      return view;
    }

    @Override
    public Exception deactivate()
    {
      CDOView v;
      synchronized (this)
      {
        v = view;
        view = null;
      }

      if (v != null)
      {
        return LifecycleUtil.deactivate(v);
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ReviewManagerEvent extends org.eclipse.net4j.util.event.Event implements IReviewManagerEvent
  {
    private static final long serialVersionUID = 1L;

    private final Type type;

    private final CDOID cdoid;

    private final Supplier<CDOView> oldViewSupplier;

    private final Supplier<CDOView> newViewSupplier;

    private final Set<EStructuralFeature> changedFeatures = new HashSet<>();

    public ReviewManagerEvent(IReviewManager notifier, Type type, CDOID cdoid, Supplier<CDOView> oldViewSupplier, Supplier<CDOView> newViewSupplier)
    {
      super(notifier);
      this.type = type;
      this.cdoid = cdoid;
      this.oldViewSupplier = oldViewSupplier;
      this.newViewSupplier = newViewSupplier;
    }

    @Override
    public IReviewManager getSource()
    {
      return (IReviewManager)super.getSource();
    }

    @Override
    public Type getType()
    {
      return type;
    }

    @Override
    public CDOID getCDOID()
    {
      return cdoid;
    }

    @Override
    public Review getOldReview()
    {
      if (type == Type.ReviewCreated || oldViewSupplier == null)
      {
        return null;
      }

      CDOView view = oldViewSupplier.get();

      try
      {
        return (Review)view.getObject(cdoid);
      }
      catch (ObjectNotFoundException ex)
      {
        return null;
      }
    }

    @Override
    public Review getNewReview()
    {
      if (type == Type.ReviewDeleted || newViewSupplier == null)
      {
        return null;
      }

      CDOView view = newViewSupplier.get();

      try
      {
        return (Review)view.getObject(cdoid);
      }
      catch (ObjectNotFoundException ex)
      {
        return null;
      }
    }

    @Override
    public Set<EStructuralFeature> getChangedFeatures()
    {
      return Collections.unmodifiableSet(changedFeatures);
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "type=" + type + ", cdoid=" + cdoid + ", changedFeatures=" + //
          changedFeatures.stream().map(ReviewManagerEvent::formatFeatureName).collect(Collectors.toList());
    }

    private static String formatFeatureName(EStructuralFeature feature)
    {
      return feature.getEContainingClass().getName() + "." + feature.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ReviewManagerRegistry implements Registry
  {
    public static final ReviewManagerRegistry INSTANCE = new ReviewManagerRegistry();

    private final Map<InternalRepository, IReviewManager> reviewManagers = Collections.synchronizedMap(new HashMap<>());

    private ReviewManagerRegistry()
    {
    }

    @Override
    public IReviewManager getReviewManager(IRepository repository)
    {
      return reviewManagers.get(repository);
    }

    public IReviewManager removeReviewManager(InternalRepository repository)
    {
      return reviewManagers.remove(repository);
    }

    public void addReviewManager(InternalRepository repository, IReviewManager reviewManager)
    {
      reviewManagers.put(repository, reviewManager);
    }
  }
}
