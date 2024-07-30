/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.provider;

import org.eclipse.emf.cdo.etypes.provider.ModelElementItemProvider;
import org.eclipse.emf.cdo.lm.reviews.Commentable;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl;
import org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl.CommentStatistics;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.reviews.Commentable} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class CommentableItemProvider extends ModelElementItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CommentableItemProvider(AdapterFactory adapterFactory)
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
      addCommentCountPropertyDescriptor(object);
      addUnresolvedCountPropertyDescriptor(object);
      addResolvedCountPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
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
        getString("_UI_Commentable_review_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Commentable_review_feature", "_UI_Commentable_type"),
        ReviewsPackage.Literals.COMMENTABLE__REVIEW, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Comment Count feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addCommentCountPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Commentable_commentCount_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Commentable_commentCount_feature", "_UI_Commentable_type"),
        ReviewsPackage.Literals.COMMENTABLE__COMMENT_COUNT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
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
        getString("_UI_Commentable_unresolvedCount_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Commentable_unresolvedCount_feature", "_UI_Commentable_type"),
        ReviewsPackage.Literals.COMMENTABLE__UNRESOLVED_COUNT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
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
        getString("_UI_Commentable_resolvedCount_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Commentable_resolvedCount_feature", "_UI_Commentable_type"),
        ReviewsPackage.Literals.COMMENTABLE__RESOLVED_COUNT, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
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
      childrenFeatures.add(ReviewsPackage.Literals.COMMENTABLE__COMMENTS);
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
    Commentable commentable = (Commentable)object;
    return new StyledString(getString("_UI_Commentable_type"), StyledString.Style.QUALIFIER_STYLER).append(" ")
        .append(Integer.toString(commentable.getCommentCount()));
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    Object notifier = notification.getNotifier();
    switch (notification.getFeatureID(Commentable.class))
    {
    case ReviewsPackage.COMMENTABLE__COMMENTS:
      fireNotifyChanged(new ViewerNotification(notification, notifier, true, true));
      propagateNotification(this, notification, notifier);
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

  static Object overlayCommentableImage(Commentable commentable, Object image)
  {
    CommentStatistics statistics = CommentableImpl.getCommentStatistics(commentable);

    String overlayKey = null;
    if (statistics.getUnresolvedCount() != 0)
    {
      overlayKey = "Unresolved";
    }
    else if (statistics.getResolvedCount() != 0)
    {
      overlayKey = "Resolved";
    }

    if (overlayKey != null)
    {
      List<Object> images = new ArrayList<>(2);
      images.add(image);
      images.add(ReviewsEditPlugin.INSTANCE.getImage("full/ovr16/" + overlayKey + ".gif"));
      image = new ComposedImage(images);
    }

    return image;
  }

  static void propagateNotification(ItemProviderAdapter itemProvider, Notification notification, Object notifier)
  {
    if (notifier instanceof EObject)
    {
      EObject eContainer = ((EObject)notifier).eContainer();

      while (eContainer instanceof Commentable)
      {
        itemProvider.fireNotifyChanged(new ViewerNotification(notification, eContainer, false, true));
        eContainer = eContainer.eContainer();
      }
    }
  }

}
