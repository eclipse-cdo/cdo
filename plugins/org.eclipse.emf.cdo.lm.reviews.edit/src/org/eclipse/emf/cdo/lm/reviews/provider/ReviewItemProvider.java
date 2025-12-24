/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.provider;

import org.eclipse.emf.cdo.lm.provider.BaselineItemProvider;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.reviews.Review} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ReviewItemProvider extends BaselineItemProvider
{
  private IItemPropertyDescriptor statusPropertyDescriptor;

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addReviewPropertyDescriptor(object);
      addTopicCountPropertyDescriptor(object);
      addUnresolvedCountPropertyDescriptor(object);
      addResolvedCountPropertyDescriptor(object);
      addIdPropertyDescriptor(object);
      addAuthorPropertyDescriptor(object);
      addReviewersPropertyDescriptor(object);
      addStatusPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Topic Count feature.
   * <!-- begin-user-doc -->
   * @since 1.1
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTopicCountPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_TopicContainer_topicCount_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_TopicContainer_topicCount_feature", "_UI_TopicContainer_type"),
        ReviewsPackage.Literals.TOPIC_CONTAINER__TOPIC_COUNT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Unresolved Count feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addUnresolvedCountPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_TopicContainer_unresolvedCount_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_TopicContainer_unresolvedCount_feature", "_UI_TopicContainer_type"),
        ReviewsPackage.Literals.TOPIC_CONTAINER__UNRESOLVED_COUNT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Resolved Count feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addResolvedCountPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_TopicContainer_resolvedCount_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_TopicContainer_resolvedCount_feature", "_UI_TopicContainer_type"),
        ReviewsPackage.Literals.TOPIC_CONTAINER__RESOLVED_COUNT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Review feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addReviewPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_TopicContainer_review_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_TopicContainer_review_feature", "_UI_TopicContainer_type"),
        ReviewsPackage.Literals.TOPIC_CONTAINER__REVIEW, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Id feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addIdPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Review_id_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Review_id_feature", "_UI_Review_type"),
        ReviewsPackage.Literals.REVIEW__ID, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Author feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addAuthorPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Review_author_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Review_author_feature", "_UI_Review_type"),
        ReviewsPackage.Literals.REVIEW__AUTHOR, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Reviewers feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addReviewersPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Review_reviewers_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Review_reviewers_feature", "_UI_Review_type"),
        ReviewsPackage.Literals.REVIEW__REVIEWERS, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Status feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addStatusPropertyDescriptor(Object object)
  {
    // Remember the statusPropertyDescriptor for appendStatus() below.
    statusPropertyDescriptor = createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Review_status_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Review_status_feature", "_UI_Review_type"),
        ReviewsPackage.Literals.REVIEW__STATUS, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null);
    itemPropertyDescriptors.add(statusPropertyDescriptor);
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(ReviewsPackage.Literals.TOPIC_CONTAINER__TOPICS);
      childrenFeatures.add(ReviewsPackage.Literals.TOPIC_CONTAINER__COMMENTS);
    }
    return childrenFeatures;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EStructuralFeature getChildFeature(Object object, Object child)
  {
    // Check the type of the specified child object and return the proper feature to use for
    // adding (see {@link AddCommand}) it as a child.

    return super.getChildFeature(object, child);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected boolean shouldComposeCreationImage()
  {
    return true;
  }

  /**
   * This returns the label text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getText(Object object)
  {
    return ((StyledString)getStyledText(object)).getString();
  }

  /**
   * This returns the label styled text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getStyledText(Object object)
  {
    Review review = (Review)object;
    return new StyledString(getString("_UI_Review_type"), StyledString.Style.QUALIFIER_STYLER).append(" ").append(Integer.toString(review.getId()));
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * @since 1.1
   * @deprecated Only here to show the unmodified code created by the generator in contrast to the
   * hand-modified {@link #notifyChanged(Notification)}. <b>Do not attempt to call notifyChangedGen()
   * from notifyChanged() as that would lead to StackOverflowError between TopicContainerItemProvider
   * and TopicItemProvider!</b>
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  public void notifyChangedGen(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(Review.class))
    {
    case ReviewsPackage.REVIEW__TOPIC_COUNT:
    case ReviewsPackage.REVIEW__UNRESOLVED_COUNT:
    case ReviewsPackage.REVIEW__RESOLVED_COUNT:
    case ReviewsPackage.REVIEW__ID:
    case ReviewsPackage.REVIEW__AUTHOR:
    case ReviewsPackage.REVIEW__REVIEWERS:
    case ReviewsPackage.REVIEW__STATUS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case ReviewsPackage.REVIEW__TOPICS:
    case ReviewsPackage.REVIEW__COMMENTS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
      return;
    }
    super.notifyChanged(notification);
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(Review.class))
    {
    case ReviewsPackage.REVIEW__TOPIC_COUNT:
    case ReviewsPackage.REVIEW__UNRESOLVED_COUNT:
    case ReviewsPackage.REVIEW__RESOLVED_COUNT:
    case ReviewsPackage.REVIEW__ID:
    case ReviewsPackage.REVIEW__AUTHOR:
    case ReviewsPackage.REVIEW__REVIEWERS:
    case ReviewsPackage.REVIEW__STATUS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case ReviewsPackage.REVIEW__COMMENTS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
      return;
    case ReviewsPackage.REVIEW__TOPICS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, true));
      return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
   * that can be created under this object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);

    newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.TOPIC_CONTAINER__TOPICS, ReviewsFactory.eINSTANCE.createTopic()));

    newChildDescriptors.add(createChildParameter(ReviewsPackage.Literals.TOPIC_CONTAINER__COMMENTS, ReviewsFactory.eINSTANCE.createComment()));
  }

  protected final StyledString appendStatus(StyledString styledString, Review review)
  {
    getPropertyDescriptors(review); // Ensure that the descriptors are initialized.

    String status = statusPropertyDescriptor.getLabelProvider(review).getText(review.getStatus());
    styledString.append("  ").append("[" + status + "]", StyledString.Style.COUNTER_STYLER);
    return styledString;
  }
}
