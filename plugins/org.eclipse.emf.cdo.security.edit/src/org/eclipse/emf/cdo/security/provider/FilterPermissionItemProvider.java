/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.provider;

import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.security.FilterPermission} object.
 * <!-- begin-user-doc -->
 * @since 4.3
 * <!-- end-user-doc -->
 * @generated
 */
public class FilterPermissionItemProvider extends PermissionItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FilterPermissionItemProvider(AdapterFactory adapterFactory)
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

    }
    return itemPropertyDescriptors;
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
      childrenFeatures.add(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS);
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
   * This returns FilterPermission.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/FilterPermission")); //$NON-NLS-1$
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
    FilterPermission permission = (FilterPermission)object;
    Access access = permission.getAccess();
    EList<PermissionFilter> filters = permission.getFilters();

    StringBuilder builder = new StringBuilder();
    builder.append(access == null ? "?" : access);

    int size = filters.size();
    if (size == 1)
    {
      builder.append(" ");
      builder.append(filters.get(0).format());
    }
    else if (size > 1)
    {
      builder.append(" And(");

      Iterator<PermissionFilter> iterator = filters.iterator();
      builder.append(iterator.next().format());

      while (iterator.hasNext())
      {
        builder.append(", ");
        builder.append(iterator.next().format());
      }

      builder.append(")");
    }

    return builder.toString();
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

    switch (notification.getFeatureID(FilterPermission.class))
    {
    case SecurityPackage.FILTER_PERMISSION__FILTERS:
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

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createLinkedFilter()));

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createPackageFilter()));

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createClassFilter()));

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createResourceFilter()));

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createExpressionFilter()));

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createNotFilter()));

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createAndFilter()));

    newChildDescriptors.add(createChildParameter(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, SecurityFactory.eINSTANCE.createOrFilter()));
  }

}
