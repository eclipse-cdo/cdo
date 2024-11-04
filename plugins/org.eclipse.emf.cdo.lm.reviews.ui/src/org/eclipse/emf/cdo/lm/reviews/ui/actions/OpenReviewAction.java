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

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.ModelReference;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;
import org.eclipse.emf.cdo.lm.reviews.TopicStatus;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.Input;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.InputHolder;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.ui.ColorStyler;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.chat.ChatComposite;
import org.eclipse.net4j.util.ui.chat.ChatMessage;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;
import org.eclipse.net4j.util.ui.chat.ChatRenderer;
import org.eclipse.net4j.util.ui.widgets.EntryControlAdvisor;
import org.eclipse.net4j.util.ui.widgets.EntryControlAdvisor.ControlConfig;
import org.eclipse.net4j.util.ui.widgets.EntryField;
import org.eclipse.net4j.util.ui.widgets.EntryField.ButtonAdvisor;
import org.eclipse.net4j.util.ui.widgets.EntryField.FieldConfig;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.EMFCompareStructureMergeViewer;
import org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.EMFCompareStructureMergeViewerContentProvider;
import org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.Navigatable;
import org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.WrappableTreeViewer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.tree.TreeNode;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.INavigatable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class OpenReviewAction extends AbstractReviewAction
{
  @SuppressWarnings("restriction")
  public static final boolean ENABLED = new File("/develop/authors.bin").isFile();

  private static final String COMPARE_EDITOR_ID = "org.eclipse.compare.CompareEditor";

  private static final String KEY_REVIEW = "cdo.review";

  private static final String KEY_REVIEW_EDITOR_HANDLER = "cdo.review.editor.handler";

  private final Topic initialTopic;

  public OpenReviewAction(IWorkbenchPage page, Topic initialTopic)
  {
    this(page, initialTopic.getReview(), initialTopic);
  }

  public OpenReviewAction(IWorkbenchPage page, Review review, Topic initialTopic)
  {
    super(page, //
        "Open" + INTERACTIVE, //
        "Open the review", //
        OM.getImageDescriptor("icons/Open.gif"), //
        "Open the review.", //
        null, //
        review);
    this.initialTopic = initialTopic;
  }

  @Override
  protected void preRun(Review review, ISystemDescriptor systemDescriptor)
  {
    // Do nothing.
  }

  @Override
  protected boolean isDialogNeeded()
  {
    return false;
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent, Review review, ISystemDescriptor systemDescriptor)
  {
    // Do nothing.
  }

  @Override
  protected void doRun(Review review, ISystemDescriptor systemDescriptor, IProgressMonitor monitor) throws Exception
  {
    IEditorPart editor = findReviewEditor(review);
    if (editor != null)
    {
      IWorkbenchPage page = editor.getSite().getPage();
      UIUtil.asyncExec(() -> {
        page.activate(editor);

        Input input = (Input)editor.getEditorInput();
        ReviewEditorHandler handler = (ReviewEditorHandler)input.properties().get(KEY_REVIEW_EDITOR_HANDLER);
        handler.selectFirstDiffElement(initialTopic);
      });

      return;
    }

    if (review instanceof DeliveryReview)
    {
      DeliveryReview deliveryReview = (DeliveryReview)review;

      String moduleName = deliveryReview.getModule().getName();

      systemDescriptor.withModuleSession(moduleName, moduleSession -> {
        CDOBranchRef branchRef = deliveryReview.getBranch();
        CDOBranch branch = branchRef.resolve(moduleSession.getBranchManager());
        CDOBranchPoint basePoint = branch.getPoint(branch.getBase().getTimeStamp());
        CDOBranchPoint headPoint = branch.getHead();

        CDOView leftView = moduleSession.openView(headPoint);
        CDOView rightView = moduleSession.openView(basePoint);

        try
        {
          systemDescriptor.configureModuleResourceSet(leftView);
          systemDescriptor.configureModuleResourceSet(rightView);
        }
        catch (ResolutionException ex)
        {
          throw WrappedException.wrap(ex);
        }

        CDOView[] originView = { null };
        CDOCompareEditorUtil.setInputConsumer(new ReviewEditorHandler(deliveryReview, initialTopic, systemDescriptor));
        CDOCompareEditorUtil.openEditor(leftView, rightView, originView, true);
      });
    }

  }

  private IEditorPart findReviewEditor(Review review)
  {
    for (IEditorReference reference : getPage().getEditorReferences())
    {
      String id = reference.getId();
      if (COMPARE_EDITOR_ID.equals(id))
      {
        IEditorPart editor = reference.getEditor(false);
        if (editor != null)
        {
          IEditorInput editorInput = editor.getEditorInput();
          if (editorInput instanceof Input)
          {
            Input input = (Input)editorInput;

            Object property = input.properties().get(KEY_REVIEW);
            if (property == review)
            {
              return editor;
            }
          }
        }
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ReviewEditorHandler extends InputHolder
  {
    private static final Field NAVIGATABLE_FIELD;

    private static final String UNRESOLVE_ACTION = "unresolve";

    private static final String UNRESOLVE_LABEL = "Needs resolution";

    private static final Styler UNRESOLVED_STYLER = new ColorStyler(new Color(UIUtil.getDisplay(), 220, 40, 40));

    private static final String RESOLVE_ACTION = "resolve";

    private static final String RESOLVE_LABEL = "Resolve";

    private static final Styler RESOLVED_STYLER = new ColorStyler(new Color(UIUtil.getDisplay(), 20, 180, 20));

    private final DeliveryReview review;

    private final EContentAdapter reviewContentAdapter = new EContentAdapter()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        super.notifyChanged(notification);

        if (!notification.isTouch())
        {
          Object notifier = notification.getNotifier();

          Review notifyingReview = getReview(notifier);
          if (notifyingReview == review)
          {
            Topic topic = getTopic(notifier);
            handleReviewContentChange(topic);
          }
        }
      }

      private Review getReview(Object object)
      {
        if (object instanceof Review)
        {
          return (Review)object;
        }

        if (object instanceof EObject)
        {
          return getReview(((EObject)object).eContainer());
        }

        return null;
      }

      private Topic getTopic(Object object)
      {
        if (object instanceof Topic)
        {
          return (Topic)object;
        }

        if (object instanceof EObject)
        {
          return getTopic(((EObject)object).eContainer());
        }

        return null;
      }
    };

    private final ISystemDescriptor systemDescriptor;

    private final Image topicImage = OM.getImage("icons/editor/Topic.png");

    private final Image topicImageUnresolved = OM.getOverlayImage("icons/editor/Topic.png", "icons/editor/Unresolved.png", 18, 2);

    private final Image topicImageResolved = OM.getOverlayImage("icons/editor/Topic.png", "icons/editor/Resolved.png", 14, 2);

    private Topic firstTopic;

    private Topic currentTopic;

    private Object lastDiffSelection;

    private ChatRenderer renderer;

    private Label topicIcon;

    private EntryField topicEntryField;

    private Link topicActionLine;

    private TopicStatus topicStatus;

    private ChatComposite chatComposite;

    static
    {
      Field field = null;

      try
      {
        field = ReflectUtil.getField(EMFCompareStructureMergeViewer.class, "navigatable");
      }
      catch (Throwable ex)
      {
        OM.LOG.error(ex);
      }

      NAVIGATABLE_FIELD = field;
    }

    public ReviewEditorHandler(DeliveryReview review, Topic firstTopic, ISystemDescriptor systemDescriptor)
    {
      this.review = review;
      this.systemDescriptor = systemDescriptor;
      this.firstTopic = firstTopic;
    }

    @Override
    public void activate(Input input)
    {
      input.setTitle(review.getTypeAndName());
      input.setSaveHandler(this::handleSave);

      IRegistry<String, Object> properties = input.properties();
      properties.put(KEY_REVIEW, review);
      properties.put(KEY_REVIEW_EDITOR_HANDLER, this);

      input.setEditionSelectionDialog(true); // Enable selection propagation.
      input.addPropertyChangeListener(event -> {
        if (CompareEditorInput.PROP_SELECTED_EDITION.equals(event.getProperty()))
        {
          Object diffSelection = event.getNewValue();
          if (diffSelection != lastDiffSelection)
          {
            if (!handleDiffSelection(lastDiffSelection, diffSelection))
            {
              TreeViewer treeViewer = input.getTreeViewer();
              treeViewer.setSelection(new StructuredSelection(lastDiffSelection));
              return;
            }

            lastDiffSelection = diffSelection;
          }
        }
      });

      review.eAdapters().add(reviewContentAdapter);
    }

    @Override
    public Exception deactivate()
    {
      review.eAdapters().remove(reviewContentAdapter);
      return null;
    }

    @Override
    public Control createContents(Composite parent, Function<Composite, Control> defaultContentsCreator)
    {
      return new SashComposite(parent, SWT.NONE, 100, 50, true, true, false)
      {
        @Override
        protected Control createControl1(Composite parent)
        {
          try
          {
            return defaultContentsCreator.apply(parent);
          }
          finally
          {
            customizeDiffViewer();
          }
        }

        @Override
        protected Control createControl2(Composite parent)
        {
          Display display = parent.getDisplay();
          Color entryBackgroundColor = new Color(display, 241, 241, 241);
          addDisposeListener(e -> entryBackgroundColor.dispose());

          renderer = IPluginContainer.INSTANCE.getElementOrNull(ChatRenderer.Factory.PRODUCT_GROUP, "mylyn", "Markdown");
          EntryControlAdvisor entryControlAdvisor = IPluginContainer.INSTANCE.getElementOrNull(EntryControlAdvisor.Factory.PRODUCT_GROUP, "mylyn", "Markdown");

          Composite topicContainer = new Composite(parent, SWT.NONE);
          topicContainer.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).spacing(LayoutConstants.getSpacing().x, 2).numColumns(2).create());
          topicContainer.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

          topicIcon = new Label(topicContainer, SWT.NONE);
          topicIcon.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP).create());
          topicIcon.setImage(topicImage);

          topicEntryField = createTopicEntryField(topicContainer, entryBackgroundColor, entryControlAdvisor);
          topicEntryField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).create());

          Label spacer = new Label(topicContainer, SWT.NONE);
          spacer.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP).create());

          topicActionLine = new Link(topicContainer, SWT.NONE);
          topicActionLine.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(10, 0).create());
          topicActionLine.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> handleTopicAction(e.text)));

          chatComposite = createChatComposite(topicContainer, entryBackgroundColor, entryControlAdvisor);
          chatComposite.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());

          refreshTopicUI();
          return topicContainer;
        }
      };
    }

    public void selectFirstDiffElement(Topic firstTopic)
    {
      this.firstTopic = firstTopic;

      EMFCompareStructureMergeViewer viewerWrapper = (EMFCompareStructureMergeViewer)getInput().getViewerWrapper();
      Navigatable navigatable = viewerWrapper.getNavigatable();
      navigatable.selectChange(INavigatable.FIRST_CHANGE);
    }

    private boolean handleDiffSelection(Object oldDiffSelection, Object newDiffSelection)
    {
      boolean cancel = false;

      if (oldDiffSelection != null && topicNeedsSave())
      {
        int result = MessageDialog.open(MessageDialog.QUESTION_WITH_CANCEL, topicEntryField.getShell(), "Topic Change",
            "Do you want to save the changes to the current topic?", SWT.SHEET, "Save", "Don't Save", "Cancel");
        if (result == 2 || result == -1)
        {
          // Cancel.
          return false;
        }

        if (result == 0)
        {
          // Save.
          handleSave(new NullProgressMonitor());
        }
        else
        {
          // Don't save.
          cancel = true;
        }
      }

      TreeNode node = Input.getTreeNode(newDiffSelection);
      if (node != null)
      {
        ModelReference modelReference = createModelReference(node);
        if (modelReference != null)
        {
          selectTopic(modelReference);
        }
      }

      if (cancel)
      {
        updateDiffSelectionLabel(oldDiffSelection);
      }

      return true;
    }

    private void handleTopicAction(String action)
    {
      TopicStatus oldStatus = topicStatus;
      TopicStatus newStatus = oldStatus;

      if (UNRESOLVE_ACTION.equals(action))
      {
        newStatus = TopicStatus.UNRESOLVED;
      }
      else if (RESOLVE_ACTION.equals(action))
      {
        newStatus = TopicStatus.RESOLVED;
      }

      if (newStatus != oldStatus)
      {
        topicStatus = newStatus;

        Object diffSelection = getDiffElement(currentTopic);
        updateDiffSelectionLabel(diffSelection);

        refreshTopicUI();
        updateDirtyness();
      }
    }

    private void updateDiffSelectionLabel(Object diffSelection)
    {
      if (diffSelection != null)
      {
        getInput().getTreeViewer().update(diffSelection, null);
      }
    }

    private void updateDirtyness()
    {
      boolean dirty = isCurrentTopicDirty();
      getInput().setDirty(dirty);
      topicEntryField.setExtraButtonVisible(0, dirty);
    }

    private boolean isCurrentTopicDirty()
    {
      if (currentTopic == null)
      {
        return false;
      }

      return topicEntryField.isDirty() || topicStatus != null;
    }

    private void handleTopicDirtyChanged(boolean dirty)
    {
      updateDirtyness();
    }

    private void handleReviewContentChange(Topic topic)
    {
      if (topic != null && topic == currentTopic)
      {
        UIUtil.asyncExec(chatComposite::refreshMessageBrowser);
      }
    }

    private void handleSave(IProgressMonitor monitor)
    {
      if (currentTopic != null)
      {
        try
        {
          if (currentTopic.cdoState() == CDOState.TRANSIENT)
          {
            systemDescriptor.modify(review, r -> {
              currentTopic.setText(topicEntryField.getEntry());
              currentTopic.setStatus(topicStatus == null ? TopicStatus.NONE : topicStatus);
              r.getTopics().add(currentTopic);
              return true;
            }, monitor);

            EList<Topic> topics = review.getTopics();
            currentTopic = topics.get(topics.size() - 1);
          }
          else
          {
            systemDescriptor.modify(currentTopic, t -> {
              t.setText(topicEntryField.getEntry());

              if (topicStatus != null)
              {
                t.setStatus(topicStatus);
              }
              return true;
            }, monitor);
          }
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      topicEntryField.resetDirty();
      topicEntryField.setPreviewMode(!topicEntryField.isEmpty());
      topicStatus = null;
      updateDirtyness();
      setChatCompositeVisible(true);
    }

    private Object getDiffElement(Topic topic)
    {
      Object[] result = { null };

      if (topic != null)
      {
        ModelReference modelReference = topic.getModelReference();
        if (modelReference != null)
        {
          getInput().forEachDiffElement(element -> {
            TreeNode node = Input.getTreeNode(element);
            if (node != null)
            {
              if (modelReference.equals(createModelReference(node)))
              {
                result[0] = element;
                return true;
              }
            }

            return false;
          });
        }
      }

      return result[0];
    }

    private EntryField createTopicEntryField(Composite parent, Color entryBackgroundColor, EntryControlAdvisor entryControlAdvisor)
    {
      ControlConfig controlConfig = new ControlConfig();
      controlConfig.setOkDetector(null);

      FieldConfig fieldConfig = new EntryField.FieldConfig();
      fieldConfig.setEntryBackground(entryBackgroundColor);
      fieldConfig.setEntryControlAdvisor(entryControlAdvisor);
      fieldConfig.setEntryControlConfig(controlConfig);
      fieldConfig.setEmptyHint("Create a review topic");
      fieldConfig.setPreviewProvider(renderer);
      fieldConfig.setInitialPreviewMode(true);
      fieldConfig.setDirtyHandler(entryField -> handleTopicDirtyChanged(entryField.isDirty()));
      fieldConfig.setExtraButtonAdvisors(new ButtonAdvisor()
      {
        @Override
        public String getToolTipText()
        {
          return "Save changes";
        }

        @Override
        public Image getHoverImage()
        {
          return SharedIcons.getImage(SharedIcons.ETOOL_SAVE);
        }

        @Override
        public void run()
        {
          getInput().getEditor().doSave(new NullProgressMonitor());
        }
      });

      EntryField entryField = new EntryField(parent, SWT.NONE, fieldConfig);
      entryField.setExtraButtonVisible(0, false);

      return entryField;
    }

    private void selectTopic(ModelReference modelReference)
    {
      Topic topic = review.getTopic(modelReference);
      setChatCompositeVisible(topic != null);

      if (topic == null)
      {
        topic = ReviewsFactory.eINSTANCE.createTopic();
        topic.setModelReference(modelReference);
      }

      if (topic != currentTopic)
      {
        currentTopic = topic;
        topicStatus = null;

        String entry = topic.getText();
        topicEntryField.setEntry(entry);

        boolean previewMode = !topicEntryField.isEmpty();
        topicEntryField.setPreviewMode(previewMode);

        refreshTopicUI();
        chatComposite.refreshMessageBrowser();
      }
    }

    private Topic getTopic(Object diffSelection)
    {
      TreeNode node = Input.getTreeNode(diffSelection);
      if (node != null)
      {
        ModelReference modelReference = createModelReference(node);
        if (modelReference != null)
        {
          if (currentTopic != null && modelReference.equals(currentTopic.getModelReference()))
          {
            return currentTopic;
          }

          return review.getTopic(modelReference);
        }
      }

      return null;
    }

    private void refreshTopicUI()
    {
      TopicStatus status = topicStatus;
      if (status == null && currentTopic != null)
      {
        status = currentTopic.getStatus();
      }

      switch (Objects.requireNonNullElse(status, TopicStatus.NONE))
      {
      case NONE:
        topicIcon.setImage(topicImage);
        topicActionLine.setText(createTopicActionText(UNRESOLVE_ACTION, UNRESOLVE_LABEL));
        break;

      case UNRESOLVED:
        topicIcon.setImage(topicImageUnresolved);
        topicActionLine.setText(createTopicActionText(RESOLVE_ACTION, RESOLVE_LABEL));
        break;

      case RESOLVED:
        topicIcon.setImage(topicImageResolved);
        topicActionLine.setText(createTopicActionText(UNRESOLVE_ACTION, UNRESOLVE_LABEL));
        break;
      }
    }

    private boolean topicNeedsSave()
    {
      return currentTopic != null && (topicStatus != null || topicEntryField.isDirty());
    }

    private ChatComposite createChatComposite(Composite parent, Color entryBackgroundColor, EntryControlAdvisor entryControlAdvisor)
    {
      ChatComposite.Config config = new ChatComposite.Config();
      config.setOwnUserID("estepper");
      config.setMessageProvider(ReviewEditorHandler.this::computeMessages);
      config.setChatRenderer(renderer);
      config.setEntryBackgroundColor(entryBackgroundColor);
      config.setEntryControlAdvisor(entryControlAdvisor);
      config.setSendHandler(ReviewEditorHandler.this::sendMessage);

      return new ChatComposite(parent, SWT.NONE, config);
    }

    private void setChatCompositeVisible(boolean visible)
    {
      GridData gridData = (GridData)chatComposite.getLayoutData();
      if (gridData.exclude == visible)
      {
        chatComposite.setVisible(visible);

        gridData.exclude = !visible;
        chatComposite.getParent().requestLayout();
      }
    }

    private ChatMessage[] computeMessages()
    {
      List<ChatMessage> messages = new ArrayList<>();
      review.cdoView().syncExec(() -> addMessages(messages, currentTopic == null ? review : currentTopic));
      return messages.toArray(new ChatMessage[messages.size()]);
    }

    private void sendMessage(String markup)
    {
      Comment comment = ReviewsFactory.eINSTANCE.createComment();
      comment.setText(markup);

      TopicContainer container = currentTopic == null ? review : currentTopic;
      try
      {
        systemDescriptor.modify(container, c -> {
          c.getComments().add(comment);
          return true;
        }, null);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    private void customizeDiffViewer()
    {
      if (NAVIGATABLE_FIELD != null)
      {
        EMFCompareStructureMergeViewer viewerWrapper = (EMFCompareStructureMergeViewer)getInput().getViewerWrapper();

        // Customize the label provider with topic status decorations.
        IStyledLabelProvider delegate = viewerWrapper.getLabelProvider().getStyledStringProvider();
        viewerWrapper.setLabelProvider(new DelegatingStyledCellLabelProvider(delegate)
        {
          @Override
          protected StyledString getStyledText(Object element)
          {
            StyledString styledText = super.getStyledText(element);

            Topic topic = getTopic(element);
            if (topic != null)
            {
              TopicStatus status = topic == currentTopic ? topicStatus : topic.getStatus();
              if (status == TopicStatus.UNRESOLVED)
              {
                styledText.append("  ").append("[Unresolved]", UNRESOLVED_STYLER);

              }
              else if (status == TopicStatus.RESOLVED)
              {
                styledText.append("  ").append("[Resolved]", RESOLVED_STYLER);
              }
            }

            return styledText;
          }
        });

        // Customize the navigatable to select the initial topic as the first diff.
        EMFCompareStructureMergeViewerContentProvider contentProvider = viewerWrapper.getContentProvider();
        WrappableTreeViewer treeViewer = viewerWrapper.getNavigatable().getViewer();

        Navigatable navigatable = new Navigatable(treeViewer, contentProvider)
        {
          @Override
          public boolean hasChange(int flag)
          {
            if (flag == INavigatable.FIRST_CHANGE)
            {
              // The initial topic.
              return true;
            }

            return super.hasChange(flag);
          }

          @Override
          public boolean selectChange(int flag)
          {
            if (flag == INavigatable.FIRST_CHANGE && firstTopic != null)
            {
              // The initial topic.
              contentProvider.runWhenReady(uiSyncCallbackType, new Runnable()
              {
                @Override
                public void run()
                {
                  Object newSelection = getDiffElement(firstTopic);
                  if (newSelection != null)
                  {
                    fireOpen(newSelection);
                  }
                }
              });

              return false;
            }

            return super.selectChange(flag);
          }
        };

        ReflectUtil.setValue(NAVIGATABLE_FIELD, viewerWrapper, navigatable);
        viewerWrapper.getControl().setData(INavigatable.NAVIGATOR_PROPERTY, navigatable);
      }
    }

    private static void addMessages(List<ChatMessage> messages, TopicContainer container)
    {
      Author[] authors;

      try
      {
        List<Author> authorsList = IOUtil.readObject(new File("/develop/authors.bin"), Author.class.getClassLoader());
        authors = authorsList.toArray(new Author[authorsList.size()]);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }

      int a = 0;
      Author author = null;
      Random random = new Random(4711L);

      for (Comment comment : container.getComments())
      {
        if (--a <= 0)
        {
          for (;;)
          {
            Author newAuthor = authors[random.nextInt(authors.length)];
            if (newAuthor != author)
            {
              author = newAuthor;
              a = 1 + random.nextInt(4);
              break;
            }
          }
        }

        Author finalAuthor = author;

        messages.add(new ChatMessage()
        {
          @Override
          public Object getID()
          {
            return comment;
          }

          @Override
          public Author getAuthor()
          {
            return finalAuthor;
          }

          @Override
          public long getCreationTime()
          {
            return comment.cdoRevision().getTimeStamp();
          }

          @Override
          public long getEditTime()
          {
            return getCreationTime();
          }

          @Override
          public String getContent()
          {
            return comment.getText();
          }

          @Override
          public ChatMessage getReplyTo()
          {
            return null;
          }
        });
      }
    }

    private static String createTopicActionText(String action, String label)
    {
      return "<a href=\"" + action + "\">" + label + "</a>";
    }

    private static ModelReference createModelReference(TreeNode node)
    {
      EObject data = node.getData();
      if (data instanceof Match)
      {
        Match match = (Match)data;

        CDOID id = getMatchObjectID(match);
        return new ModelReference(id);
      }

      if (data instanceof AttributeChange)
      {
        AttributeChange diff = (AttributeChange)data;

        CDOID id = getMatchObjectID(diff.getMatch());
        return new ModelReference(id, diff.getAttribute().getName());
      }

      if (data instanceof ReferenceChange)
      {
        ReferenceChange diff = (ReferenceChange)data;

        CDOID id = getMatchObjectID(diff.getMatch());
        return new ModelReference(id, diff.getReference().getName());
      }

      return null;
    }

    private static CDOID getMatchObjectID(Match match)
    {
      EObject matchObject = getMatchObject(match);
      return CDOIDUtil.getCDOID(matchObject);
    }

    private static EObject getMatchObject(Match match)
    {
      EObject left = match.getLeft();
      if (left != null)
      {
        return left;
      }

      EObject right = match.getRight();
      if (right != null)
      {
        return right;
      }

      return match.getOrigin();
    }
  }
}
