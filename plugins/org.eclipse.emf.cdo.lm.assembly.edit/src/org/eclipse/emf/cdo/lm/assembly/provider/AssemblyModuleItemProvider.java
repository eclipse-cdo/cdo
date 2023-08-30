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
package org.eclipse.emf.cdo.lm.assembly.provider;

import org.eclipse.emf.cdo.etypes.provider.ModelElementItemProvider;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.assembly.AssemblyPackage;

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
 * This is the item provider adapter for a
 * {@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class AssemblyModuleItemProvider extends ModelElementItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public AssemblyModuleItemProvider(AdapterFactory adapterFactory)
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

      addNamePropertyDescriptor(object);
      addVersionPropertyDescriptor(object);
      addBranchPointPropertyDescriptor(object);
      addRootPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Name feature.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected void addNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AssemblyModule_name_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AssemblyModule_name_feature", "_UI_AssemblyModule_type"),
        AssemblyPackage.Literals.ASSEMBLY_MODULE__NAME, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Version feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addVersionPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AssemblyModule_version_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AssemblyModule_version_feature", "_UI_AssemblyModule_type"),
        AssemblyPackage.Literals.ASSEMBLY_MODULE__VERSION, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
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
        getString("_UI_AssemblyModule_branchPoint_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AssemblyModule_branchPoint_feature", "_UI_AssemblyModule_type"),
        AssemblyPackage.Literals.ASSEMBLY_MODULE__BRANCH_POINT, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Root feature.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected void addRootPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AssemblyModule_root_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AssemblyModule_root_feature", "_UI_AssemblyModule_type"),
        AssemblyPackage.Literals.ASSEMBLY_MODULE__ROOT, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This returns AssemblyModule.gif.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/AssemblyModule"));
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
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
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
    String label = ((AssemblyModule)object).getName();
    if (StringUtil.isEmpty(label))
    {
      label = getString("_UI_AssemblyModule_type");
    }

    return new StyledString(label);
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(AssemblyModule.class))
    {
    case AssemblyPackage.ASSEMBLY_MODULE__NAME:
    case AssemblyPackage.ASSEMBLY_MODULE__VERSION:
    case AssemblyPackage.ASSEMBLY_MODULE__BRANCH_POINT:
    case AssemblyPackage.ASSEMBLY_MODULE__ROOT:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s
   * describing the children that can be created under this object. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);
  }
}
