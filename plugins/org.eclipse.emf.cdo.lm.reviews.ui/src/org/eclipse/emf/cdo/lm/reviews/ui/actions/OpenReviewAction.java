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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;
import org.eclipse.emf.cdo.lm.reviews.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.Input;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil.InputHolder;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.chat.ChatAdvisor;
import org.eclipse.net4j.util.ui.chat.ChatComposite;
import org.eclipse.net4j.util.ui.chat.ChatMessage;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.tree.TreeNode;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class OpenReviewAction extends AbstractReviewAction
{
  public static final boolean ENABLED = new File("/develop/authors.bin").isFile();

  public OpenReviewAction(IWorkbenchPage page, Review review)
  {
    super(page, //
        "Open" + INTERACTIVE, //
        "Open the review", //
        OM.getImageDescriptor("icons/Submit.png"), //
        "Open the review.", //
        "icons/wizban/SubmitReview.png", //
        review);
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
        CDOCompareEditorUtil.setInputConsumer(new ReviewEditorHandler(deliveryReview, systemDescriptor));
        CDOCompareEditorUtil.openEditor(leftView, rightView, originView, true);
      });
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ReviewEditorHandler extends InputHolder
  {
    private final DeliveryReview review;

    private final ISystemDescriptor systemDescriptor;

    private ChatComposite chatComposite;

    public ReviewEditorHandler(DeliveryReview review, ISystemDescriptor systemDescriptor)
    {
      this.review = review;
      this.systemDescriptor = systemDescriptor;
    }

    @Override
    public void activate(Input input)
    {
      input.setEditionSelectionDialog(true); // Enable selection propagation.

      input.addPropertyChangeListener(event -> {
        if (CompareEditorInput.PROP_SELECTED_EDITION.equals(event.getProperty()))
        {
          Object value = event.getNewValue();
          if (value instanceof Adapter)
          {
            Notifier target = ((Adapter)value).getTarget();
            if (target instanceof TreeNode)
            {
              EObject data = ((TreeNode)target).getData();
              if (data instanceof Match)
              {
                selectedMatch((Match)data);
              }
              else if (data instanceof AttributeChange)
              {
                selectedAttributeChange((AttributeChange)data);
              }
              else if (data instanceof ReferenceChange)
              {
                selectedReferenceChange((ReferenceChange)data);
              }
              else if (data instanceof Diff)
              {
                selectedDiff((Diff)data);
              }
            }
          }
        }
      });
    }

    @Override
    public Exception deactivate()
    {
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
          return defaultContentsCreator.apply(parent);
        }

        @Override
        protected Control createControl2(Composite parent)
        {
          chatComposite = createChatCompoiste(parent);
          return chatComposite;
        }
      };
    }

    private ChatComposite createChatCompoiste(Composite parent)
    {
      ChatAdvisor<?> advisor = IPluginContainer.INSTANCE.getElementOrNull(ChatAdvisor.Factory.PRODUCT_GROUP, "mylyn", "Markdown");

      ChatComposite chatComposite = new ChatComposite(parent, SWT.NONE, //
          advisor, //
          "estepper", //
          ReviewEditorHandler.this::computeMessages, //
          ReviewEditorHandler.this::sendMessage);

      EContentAdapter adapter = new EContentAdapter()
      {
        @Override
        public void notifyChanged(Notification notification)
        {
          super.notifyChanged(notification);

          if (!notification.isTouch())
          {
            UIUtil.asyncExec(parent.getDisplay(), chatComposite::refreshMessageBrowser);
          }
        }
      };

      review.eAdapters().add(adapter);
      chatComposite.addDisposeListener(e -> review.eAdapters().remove(adapter));
      return chatComposite;
    }

    private void sendMessage(String markup)
    {
      Comment comment = ReviewsFactory.eINSTANCE.createComment();
      comment.setAuthor(review.cdoView().getSession().getUserID());
      comment.setText(markup);

      try
      {
        systemDescriptor.modify(review, r -> {
          r.getComments().add(comment);
          return true;
        }, null);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    private ChatMessage[] computeMessages()
    {
      List<ChatMessage> messages = new ArrayList<>();
      review.cdoView().syncExec(() -> addMessages(messages, review));
      return messages.toArray(new ChatMessage[messages.size()]);
    }

    private static final Pattern OBJECT_REFERENCE_PATTERN = Pattern.compile("(.*)(\\*\\*CDO\\[([^\\]]+)\\]\\*\\*)(.*)");

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

    private void updateObjectReference(CDOID id, String name)
    {
      String objectReference = "**CDO[" + id;
      if (name != null)
      {
        objectReference += "::" + name;
      }

      objectReference += "]**";

      ChatAdvisor<?> advisor = chatComposite.getAdvisor();
      String entry = advisor.getEntry(chatComposite);

      Matcher matcher = OBJECT_REFERENCE_PATTERN.matcher(entry);
      if (matcher.matches())
      {
        String begin = matcher.group(1);
        String end = matcher.group(4);
        entry = begin + objectReference + end;
      }
      else
      {
        entry = objectReference + " " + entry;
      }

      advisor.setEntry(chatComposite, entry);
    }

    private void selectedMatch(Match match)
    {
      CDOID id = getMatchObjectID(match);
      updateObjectReference(id, null);
      System.out.println("### Match " + id);
    }

    private void selectedAttributeChange(AttributeChange diff)
    {
      CDOID id = getMatchObjectID(diff.getMatch());
      String name = diff.getAttribute().getName();
      updateObjectReference(id, name);
      System.out.println("### AttributeChange " + id + "::" + name);
    }

    private void selectedReferenceChange(ReferenceChange diff)
    {
      CDOID id = getMatchObjectID(diff.getMatch());
      String name = diff.getReference().getName();
      updateObjectReference(id, name);
      System.out.println("### ReferenceChange " + id + "::" + name);
    }

    private void selectedDiff(Diff diff)
    {
      System.out.println("### Diff " + getMatchObjectID(diff.getMatch()) + " --> " + diff);
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
