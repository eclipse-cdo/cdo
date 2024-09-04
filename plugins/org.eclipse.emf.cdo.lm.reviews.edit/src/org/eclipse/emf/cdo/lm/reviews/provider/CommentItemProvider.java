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

import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.CommentStatus;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.StyledString.Style;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.reviews.Comment} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class CommentItemProvider extends CommentableItemProvider
{
  private static final Style STYLE_UNRESOLVED = Style.newBuilder().setForegroundColor(URI.createURI("color://rgb/220/40/40")).toStyle();

  private static final Style STYLE_RESOLVED = Style.newBuilder().setForegroundColor(URI.createURI("color://rgb/20/180/20")).toStyle();

  private ItemPropertyDescriptor statusPropertyDescriptor;

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CommentItemProvider(AdapterFactory adapterFactory)
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

      addParentHeadingPropertyDescriptor(object);
      addAuthorPropertyDescriptor(object);
      addTextPropertyDescriptor(object);
      addStatusPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Parent Heading feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addParentHeadingPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Comment_parentHeading_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Comment_parentHeading_feature", "_UI_Comment_type"),
        ReviewsPackage.Literals.COMMENT__PARENT_HEADING, false, false, false, null, null, null));
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
        getString("_UI_Comment_author_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Comment_author_feature", "_UI_Comment_type"),
        ReviewsPackage.Literals.COMMENT__AUTHOR, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Text feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTextPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Comment_text_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Comment_text_feature", "_UI_Comment_type"),
        ReviewsPackage.Literals.COMMENT__TEXT, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
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
        getString("_UI_Comment_status_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Comment_status_feature", "_UI_Comment_type"),
        ReviewsPackage.Literals.COMMENT__STATUS, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null);
    itemPropertyDescriptors.add(statusPropertyDescriptor);
  }

  /**
   * This returns Comment.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Comment"));
  }

  @Override
  protected Object overlayImage(Object object, Object image)
  {
    image = super.overlayImage(object, image);
    return CommentableItemProvider.overlayCommentableImage((Comment)object, image);
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
    Comment comment = (Comment)object;
    StyledString styledString = new StyledString(getTypeString(), StyledString.Style.QUALIFIER_STYLER) //
        .append(" ").append(getTextString(comment));

    Style style = null;
    CommentStatus status = comment.getStatus();

    if (status == CommentStatus.UNRESOLVED)
    {
      style = STYLE_UNRESOLVED;
    }
    else if (status == CommentStatus.RESOLVED)
    {
      style = STYLE_RESOLVED;
    }

    if (style != null)
    {
      getPropertyDescriptors(comment); // Ensure that the descriptors are initialized.

      String statusLabel = statusPropertyDescriptor.getLabelProvider(comment).getText(status);
      styledString.append("  ").append("[" + statusLabel + "]", style);
    }

    return styledString;
  }

  protected String getTypeString()
  {
    return getString("_UI_Comment_type");
  }

  protected String getTextString(Comment comment)
  {
    return comment.getText();
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
    switch (notification.getFeatureID(Comment.class))
    {
    case ReviewsPackage.COMMENT__TEXT:
      fireNotifyChanged(new ViewerNotification(notification, notifier, false, true));
      return;

    case ReviewsPackage.COMMENT__STATUS:
      fireNotifyChanged(new ViewerNotification(notification, notifier, false, true));
      CommentableItemProvider.propagateNotification(this, notification, notifier);
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
