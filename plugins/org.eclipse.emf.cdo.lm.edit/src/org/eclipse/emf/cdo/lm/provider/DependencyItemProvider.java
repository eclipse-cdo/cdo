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

import org.eclipse.emf.cdo.etypes.provider.ModelElementItemProvider;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Module;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.equinox.p2.metadata.VersionRange;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.Dependency} object.
 * <!-- begin-user-doc
 * --> <!-- end-user-doc -->
 * @generated
 */
public class DependencyItemProvider extends ModelElementItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DependencyItemProvider(AdapterFactory adapterFactory)
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

      addTargetPropertyDescriptor(object);
      addVersionRangePropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Target feature.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected void addTargetPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Dependency_target_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Dependency_target_feature", "_UI_Dependency_type"),
        LMPackage.Literals.DEPENDENCY__TARGET, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Version Range feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addVersionRangePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Dependency_versionRange_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Dependency_versionRange_feature", "_UI_Dependency_type"),
        LMPackage.Literals.DEPENDENCY__VERSION_RANGE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This returns Dependency.gif.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Dependency"));
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
    Dependency dependency = (Dependency)object;

    Module target = dependency.getTarget();
    String targetName = target == null ? null : target.getName();
    String label = StringUtil.isEmpty(targetName) ? "?" : targetName;
    StyledString styledLabel = new StyledString(label);

    VersionRange versionRange = dependency.getVersionRange();
    if (versionRange != null)
    {
      styledLabel.append("  ").append(versionRange.toString(), StyledString.Style.DECORATIONS_STYLER);
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

    switch (notification.getFeatureID(Dependency.class))
    {
    case LMPackage.DEPENDENCY__VERSION_RANGE:
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
