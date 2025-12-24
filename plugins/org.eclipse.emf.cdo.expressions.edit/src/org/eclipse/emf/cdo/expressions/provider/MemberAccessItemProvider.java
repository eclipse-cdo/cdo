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
package org.eclipse.emf.cdo.expressions.provider;

import org.eclipse.emf.cdo.expressions.ExpressionsFactory;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.MemberAccess;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.expressions.MemberAccess} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class MemberAccessItemProvider extends AccessItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberAccessItemProvider(AdapterFactory adapterFactory)
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
      childrenFeatures.add(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT);
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
   * This returns MemberAccess.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/MemberAccess"));
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
    return getString("_UI_MemberAccess_type");
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

    switch (notification.getFeatureID(MemberAccess.class))
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
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

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createBooleanValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createByteValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createShortValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createIntValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createLongValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createFloatValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createDoubleValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createCharValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createStringValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createFunctionInvocation()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createMemberInvocation()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createStaticAccess()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createMemberAccess()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createContextAccess()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createContainedObject()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createLinkedObject()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createLinkedExpression()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, ExpressionsFactory.eINSTANCE.createListConstruction()));
  }

  /**
   * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection)
  {
    Object childFeature = feature;
    Object childObject = child;

    boolean qualify = childFeature == ExpressionsPackage.Literals.ACCESS__NAME || childFeature == ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT;

    if (qualify)
    {
      return getString("_UI_CreateChild_text2", new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
    }
    return super.getCreateChildText(owner, feature, child, selection);
  }

}
