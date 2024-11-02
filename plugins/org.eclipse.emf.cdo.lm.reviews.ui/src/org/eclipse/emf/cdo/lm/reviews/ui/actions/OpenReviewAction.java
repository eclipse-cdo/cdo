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
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.Input;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.InputHolder;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.ui.EntryControlAdvisor;
import org.eclipse.net4j.util.ui.EntryControlAdvisor.ControlConfig;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.chat.ChatComposite;
import org.eclipse.net4j.util.ui.chat.ChatMessage;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;
import org.eclipse.net4j.util.ui.chat.ChatRenderer;
import org.eclipse.net4j.util.ui.widgets.RoundedEntryField;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.emf.common.notify.Notification;
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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final Map<ModelReference, Topic> topics = new HashMap<>();

    private Topic firstTopic;

    private Topic currentTopic;

    private ChatRenderer renderer;

    private RoundedEntryField topicEntryField;

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

      IRegistry<String, Object> properties = input.properties();
      properties.put(KEY_REVIEW, review);
      properties.put(KEY_REVIEW_EDITOR_HANDLER, this);

      input.setEditionSelectionDialog(true); // Enable selection propagation.

      input.addPropertyChangeListener(event -> {
        if (CompareEditorInput.PROP_SELECTED_EDITION.equals(event.getProperty()))
        {
          Object value = event.getNewValue();

          TreeNode node = Input.getTreeNode(value);
          if (node != null)
          {
            ModelReference modelReference = createModelReference(node);
            if (modelReference != null)
            {
              selectTopic(modelReference);
            }
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

    public void selectFirstDiffElement(Topic firstTopic)
    {
      this.firstTopic = firstTopic;

      EMFCompareStructureMergeViewer viewerWrapper = (EMFCompareStructureMergeViewer)getInput().getViewerWrapper();
      Navigatable navigatable = viewerWrapper.getNavigatable();
      navigatable.selectChange(INavigatable.FIRST_CHANGE);
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
            replaceDiffViewerNavigatable();
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

          Composite composite = new Composite(parent, SWT.NONE);
          composite.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).create());
          composite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

          topicEntryField = createTopicEntryField(composite, entryBackgroundColor, entryControlAdvisor);
          topicEntryField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

          Composite topicActionLine = new Composite(composite, SWT.NONE);
          topicActionLine.setLayout(GridLayoutFactory.fillDefaults().create());
          topicActionLine.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

          Link link = new Link(topicActionLine, SWT.NONE);
          link.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
          link.setText("<a href=\"a1\">Model References</a>  " + "<a href=\"a2\">Select L5</a>");
          link.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
            if ("a1".equals(e.text))
            {
              getInput().forEachDiffElement(element -> {
                TreeNode node = Input.getTreeNode(element);
                ModelReference modelReference = createModelReference(node);
                if (modelReference != null)
                {
                  System.out.println(modelReference);
                }
                return false;
              });
            }
            else if ("a2".equals(e.text))
            {
              ModelReference modelReference = new ModelReference("L5");

              getInput().forEachDiffElement(element -> {
                TreeNode node = Input.getTreeNode(element);
                if (node != null)
                {
                  ModelReference m1 = modelReference;
                  ModelReference m2 = createModelReference(node);
                  if (m1.equals(m2))
                  {
                    getInput().getTreeViewer().setSelection(new StructuredSelection(element));
                    // input.getStructureInputPane().setSelection(new StructuredSelection(node));
                    return true;
                  }
                }

                return false;
              });
            }
          }));

          chatComposite = createChatComposite(composite, entryBackgroundColor, entryControlAdvisor);
          chatComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

          return composite;
        }
      };
    }

    private void handleReviewContentChange(Topic topic)
    {
      if (topic != null && topic == currentTopic)
      {
        UIUtil.asyncExec(chatComposite::refreshMessageBrowser);
      }
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

    private IStructuredSelection createDiffSelection(Topic topic)
    {
      Object diffElement = getDiffElement(topic);
      return diffElement == null ? StructuredSelection.EMPTY : new StructuredSelection(diffElement);
    }

    private RoundedEntryField createTopicEntryField(Composite parent, Color entryBackgroundColor, EntryControlAdvisor entryControlAdvisor)
    {
      ControlConfig controlConfig = new ControlConfig();
      controlConfig.setOkHandler(control -> {
        if (currentTopic != null)
        {
          try
          {
            systemDescriptor.modify(currentTopic, t -> {
              String entry = entryControlAdvisor.getEntry(control);
              t.setText(entry);
              return true;
            }, null);
          }
          catch (Exception ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      });

      return new RoundedEntryField(parent, SWT.NONE, entryBackgroundColor, entryControlAdvisor, controlConfig, renderer, true);
    }

    private void selectTopic(ModelReference modelReference)
    {
      Topic topic = topics.computeIfAbsent(modelReference, k -> {
        Topic t = review.getTopic(modelReference);
        if (t == null)
        {
          t = ReviewsFactory.eINSTANCE.createTopic();
          t.setModelReference(modelReference);
          saveTopicChanges(t);
        }

        return t;
      });

      if (topic != currentTopic)
      {
        currentTopic = topic;
        topicEntryField.setEntry(topic == null ? null : topic.getText());
        topicEntryField.setPreviewMode(true);
        chatComposite.refreshMessageBrowser();
      }

      if (false)
      {
        Input input = getInput();
        input.setDirty(true);
      }
    }

    private boolean topicNeedsSave(Topic topic)
    {
      return false;
    }

    private void saveTopicChanges(Topic topic)
    {
      try
      {
        if (topic.cdoState() == CDOState.TRANSIENT)
        {
          systemDescriptor.modify(review, r -> {
            r.getTopics().add(topic);
            return true;
          }, null);
        }
        // else
        // {
        // systemDescriptor.modify(topic, r -> {
        // Topic newTopic = ReviewsFactory.eINSTANCE.createTopic();
        // newTopic.setModelReference(modelReference);
        //
        // r.getTopics().add(newTopic);
        // return newTopic;
        // }, null);
        // }
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
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

    private void replaceDiffViewerNavigatable()
    {
      if (NAVIGATABLE_FIELD != null)
      {
        EMFCompareStructureMergeViewer viewerWrapper = (EMFCompareStructureMergeViewer)getInput().getViewerWrapper();
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
