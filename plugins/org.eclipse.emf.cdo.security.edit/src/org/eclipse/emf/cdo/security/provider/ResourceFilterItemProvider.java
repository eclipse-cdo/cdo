/*
 * Copyright (c) 2013, 2015, 2016, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.provider;

import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.ResourceFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.security.ResourceFilter} object.
 * <!-- begin-user-doc -->
 * @since 4.3
 * <!-- end-user-doc -->
 * @generated
 */
public class ResourceFilterItemProvider extends PermissionFilterItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ResourceFilterItemProvider(AdapterFactory adapterFactory)
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

      addPathPropertyDescriptor(object);
      addPatternStylePropertyDescriptor(object);
      addFoldersPropertyDescriptor(object);
      addTextResourcesPropertyDescriptor(object);
      addBinaryResourcesPropertyDescriptor(object);
      addModelResourcesPropertyDescriptor(object);
      addModelObjectsPropertyDescriptor(object);
      addIncludeParentsPropertyDescriptor(object);
      addIncludeRootPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Path feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addPathPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_path_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_path_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__PATH, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Pattern Style feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addPatternStylePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_patternStyle_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_patternStyle_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__PATTERN_STYLE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Folders feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addFoldersPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_folders_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_folders_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__FOLDERS, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Model Resources feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addModelResourcesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_modelResources_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_modelResources_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__MODEL_RESOURCES, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Model Objects feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addModelObjectsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_modelObjects_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_modelObjects_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__MODEL_OBJECTS, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Include Parents feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addIncludeParentsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_includeParents_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_includeParents_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__INCLUDE_PARENTS, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Include Root feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addIncludeRootPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_includeRoot_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_includeRoot_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__INCLUDE_ROOT, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Text Resources feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTextResourcesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_textResources_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_textResources_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__TEXT_RESOURCES, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Binary Resources feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addBinaryResourcesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ResourceFilter_binaryResources_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_ResourceFilter_binaryResources_feature", "_UI_ResourceFilter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        SecurityPackage.Literals.RESOURCE_FILTER__BINARY_RESOURCES, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This returns ResourceFilter.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/ResourceFilter")); //$NON-NLS-1$
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
   * @generated NOT
   */
  @Override
  public String getText(Object object)
  {
    return ((PermissionFilter)object).format();
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

    switch (notification.getFeatureID(ResourceFilter.class))
    {
    case SecurityPackage.RESOURCE_FILTER__PATH:
    case SecurityPackage.RESOURCE_FILTER__PATTERN_STYLE:
    case SecurityPackage.RESOURCE_FILTER__FOLDERS:
    case SecurityPackage.RESOURCE_FILTER__TEXT_RESOURCES:
    case SecurityPackage.RESOURCE_FILTER__BINARY_RESOURCES:
    case SecurityPackage.RESOURCE_FILTER__MODEL_RESOURCES:
    case SecurityPackage.RESOURCE_FILTER__MODEL_OBJECTS:
    case SecurityPackage.RESOURCE_FILTER__INCLUDE_PARENTS:
    case SecurityPackage.RESOURCE_FILTER__INCLUDE_ROOT:
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
