/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.provider;

import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.LMPackage;

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
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.Drop} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class DropItemProvider extends FixedBaselineItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DropItemProvider(AdapterFactory adapterFactory)
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

      addTypePropertyDescriptor(object);
      addLabelPropertyDescriptor(object);
      addBranchPointPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Type feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTypePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Drop_type_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Drop_type_feature", "_UI_Drop_type"),
        LMPackage.Literals.DROP__TYPE, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Label feature.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected void addLabelPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Drop_label_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Drop_label_feature", "_UI_Drop_type"),
        LMPackage.Literals.DROP__LABEL, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Branch Point feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addBranchPointPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Drop_branchPoint_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Drop_branchPoint_feature", "_UI_Drop_type"),
        LMPackage.Literals.DROP__BRANCH_POINT, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This returns Drop.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public Object getImage(Object object)
  {
    Drop drop = (Drop)object;
    DropType type = drop.getType();
    return overlayImage(object, getResourceLocator().getImage(type != null && type.isRelease() ? "full/obj16/Release" : "full/obj16/Drop"));
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
    Drop drop = (Drop)object;
    DropType type = drop.getType();
    String typeName = type == null ? null : type.getName();
    String typeLabel = StringUtil.isEmpty(typeName) ? getString("_UI_Drop_type") : typeName;
    StyledString styledString = new StyledString(typeLabel, StyledString.Style.QUALIFIER_STYLER);

    String label = drop.getLabel();
    if (!StringUtil.isEmpty(label))
    {
      styledString.append(" ").append(label);
    }

    return styledString;
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

    switch (notification.getFeatureID(Drop.class))
    {
    case LMPackage.DROP__LABEL:
    case LMPackage.DROP__BRANCH_POINT:
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
