/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.provider;

import org.eclipse.emf.cdo.lm.LMFactory;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;

import org.eclipse.net4j.util.StringUtil;

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
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.Stream} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class StreamItemProvider extends FloatingBaselineItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public StreamItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addBasePropertyDescriptor(object);
      addStartTimeStampPropertyDescriptor(object);
      addMajorVersionPropertyDescriptor(object);
      addMinorVersionPropertyDescriptor(object);
      addCodeNamePropertyDescriptor(object);
      addAllowedChangesPropertyDescriptor(object);
      addModePropertyDescriptor(object);
      addDevelopmentBranchPropertyDescriptor(object);
      addMaintenanceBranchPropertyDescriptor(object);
      addMaintenanceTimeStampPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
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
        getString("_UI_Stream_base_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Stream_base_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__BASE, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Start Time Stamp feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addStartTimeStampPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_startTimeStamp_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Stream_startTimeStamp_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__START_TIME_STAMP, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Major Version feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addMajorVersionPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_majorVersion_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Stream_majorVersion_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__MAJOR_VERSION, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Minor Version feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addMinorVersionPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_minorVersion_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Stream_minorVersion_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__MINOR_VERSION, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Code Name feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addCodeNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_codeName_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Stream_codeName_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__CODE_NAME, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Allowed Changes feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addAllowedChangesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_allowedChanges_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Stream_allowedChanges_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__ALLOWED_CHANGES, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Mode feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addModePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_mode_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Stream_mode_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__MODE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Development Branch feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addDevelopmentBranchPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_developmentBranch_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Stream_developmentBranch_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__DEVELOPMENT_BRANCH, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Maintenance Branch feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addMaintenanceBranchPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_maintenanceBranch_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Stream_maintenanceBranch_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__MAINTENANCE_BRANCH, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Maintenance Time Stamp feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addMaintenanceTimeStampPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Stream_maintenanceTimeStamp_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Stream_maintenanceTimeStamp_feature", "_UI_Stream_type"),
        LMPackage.Literals.STREAM__MAINTENANCE_TIME_STAMP, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(LMPackage.Literals.STREAM__CONTENTS);
    }
    return childrenFeatures;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * This returns Stream.gif.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Stream"));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    Stream stream = (Stream)object;
    StyledString styledLabel = new StyledString(stream.getMajorVersion() + "." + stream.getMinorVersion());

    String codeName = stream.getCodeName();
    if (!StringUtil.isEmpty(codeName))
    {
      styledLabel.append(" ").append(codeName);
    }

    switch (stream.getMode())
    {
    case DEVELOPMENT:
      break;

    case MAINTENANCE:
      styledLabel.append("  ").append("maintenance", StyledString.Style.DECORATIONS_STYLER);
      break;

    case CLOSED:
      styledLabel.append("  ").append("closed", StyledString.Style.DECORATIONS_STYLER);
      break;
    }

    return styledLabel;
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(Stream.class))
    {
    case LMPackage.STREAM__START_TIME_STAMP:
    case LMPackage.STREAM__MAJOR_VERSION:
    case LMPackage.STREAM__MINOR_VERSION:
    case LMPackage.STREAM__CODE_NAME:
    case LMPackage.STREAM__ALLOWED_CHANGES:
    case LMPackage.STREAM__MODE:
    case LMPackage.STREAM__DEVELOPMENT_BRANCH:
    case LMPackage.STREAM__MAINTENANCE_BRANCH:
    case LMPackage.STREAM__MAINTENANCE_TIME_STAMP:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case LMPackage.STREAM__CONTENTS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
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

    newChildDescriptors.add(createChildParameter(LMPackage.Literals.STREAM__CONTENTS, LMFactory.eINSTANCE.createStream()));

    newChildDescriptors.add(createChildParameter(LMPackage.Literals.STREAM__CONTENTS, LMFactory.eINSTANCE.createChange()));

    newChildDescriptors.add(createChildParameter(LMPackage.Literals.STREAM__CONTENTS, LMFactory.eINSTANCE.createDelivery()));

    newChildDescriptors.add(createChildParameter(LMPackage.Literals.STREAM__CONTENTS, LMFactory.eINSTANCE.createDrop()));
  }

}
