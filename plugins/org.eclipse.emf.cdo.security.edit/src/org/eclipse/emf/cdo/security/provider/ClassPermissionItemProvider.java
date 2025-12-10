/*
 * Copyright (c) 2012, 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.provider;

import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.ClassPermission;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.security.ClassPermission} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
@Deprecated
public class ClassPermissionItemProvider extends PermissionItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  public ClassPermissionItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addApplicableClassPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Applicable Class feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Deprecated
  protected void addApplicableClassPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ClassPermission_applicableClass_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_ClassPermission_applicableClass_feature", "_UI_ClassPermission_type"),
        SecurityPackage.Literals.CLASS_PERMISSION__APPLICABLE_CLASS, true, false, true, null, null, null)
    {
      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        if (object instanceof ClassPermission)
        {
          CDOView view = ((ClassPermission)object).cdoView();
          if (view != null)
          {
            return SecurityEditPlugin.getSortedClasses(view);
          }
        }

        return super.getChoiceOfValues(object);
      }
    });
  }

  /**
   * This returns ClassPermission.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/ClassPermission")); //$NON-NLS-1$
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
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
  @Deprecated
  @Override
  public String getText(Object object)
  {
    Access labelValue = ((ClassPermission)object).getAccess();
    EClass applicableClass = ((ClassPermission)object).getApplicableClass();
    String label = labelValue == null ? null : labelValue.toString();

    if (applicableClass != null)
    {
      label += " " + applicableClass.getName();
    }

    return label == null || label.length() == 0 ? getString("_UI_ClassPermission_type") //$NON-NLS-1$
        : label;
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
   * that can be created under this object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);
  }

}
