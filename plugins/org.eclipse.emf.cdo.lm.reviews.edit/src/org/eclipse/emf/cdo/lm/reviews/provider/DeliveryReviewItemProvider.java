/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.provider;

import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class DeliveryReviewItemProvider extends ReviewItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DeliveryReviewItemProvider(AdapterFactory adapterFactory)
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

      addClosedPropertyDescriptor(object);
      addBasePropertyDescriptor(object);
      addImpactPropertyDescriptor(object);
      addBranchPropertyDescriptor(object);
      addDeliveriesPropertyDescriptor(object);
      addSourceChangePropertyDescriptor(object);
      addSourceCommitPropertyDescriptor(object);
      addTargetCommitPropertyDescriptor(object);
      addRebaseCountPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Closed feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addClosedPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_FloatingBaseline_closed_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_FloatingBaseline_closed_feature", "_UI_FloatingBaseline_type"),
        LMPackage.Literals.FLOATING_BASELINE__CLOSED, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Base feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addBasePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_base_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_base_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__BASE, false, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Impact feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addImpactPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_impact_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_impact_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__IMPACT, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Branch feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addBranchPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_branch_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_branch_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__BRANCH, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Deliveries feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addDeliveriesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_deliveries_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_deliveries_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__DELIVERIES, false, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Source Change feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addSourceChangePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_sourceChange_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_sourceChange_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__SOURCE_CHANGE, false, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Source Commit feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addSourceCommitPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_sourceCommit_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_sourceCommit_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__SOURCE_COMMIT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Target Commit feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTargetCommitPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_targetCommit_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_targetCommit_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__TARGET_COMMIT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Rebase Count feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addRebaseCountPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DeliveryReview_rebaseCount_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DeliveryReview_rebaseCount_feature", "_UI_DeliveryReview_type"),
        ReviewsPackage.Literals.DELIVERY_REVIEW__REBASE_COUNT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This returns DeliveryReview.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/DeliveryReview"));
  }

  @Override
  protected Object overlayImage(Object object, Object image)
  {
    image = super.overlayImage(object, image);
    return TopicContainerItemProvider.overlayTopicContainerImage((Review)object, image);
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
   * @generated NOT
   */
  @Override
  public Object getStyledText(Object object)
  {
    DeliveryReview review = (DeliveryReview)object;
    StyledString styledString = new StyledString(getString("_UI_DeliveryReview_type"), StyledString.Style.QUALIFIER_STYLER) //
        .append(" ").append(Integer.toString(review.getId()));

    Change change = review.getSourceChange();
    String name = change == null ? null : change.getName();
    styledString.append(" - ").append(StringUtil.isEmpty(name) ? "???" : name);

    return appendStatus(styledString, review);
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(DeliveryReview.class))
    {
    case ReviewsPackage.DELIVERY_REVIEW__CLOSED:
    case ReviewsPackage.DELIVERY_REVIEW__IMPACT:
    case ReviewsPackage.DELIVERY_REVIEW__BRANCH:
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
    case ReviewsPackage.DELIVERY_REVIEW__REBASE_COUNT:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
  }

}
