/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.provider;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelPackage;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.provider.DawnFragmentGeneratorItemProvider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 * @author Martin Fluegge
 */
public class DawnEMFGeneratorItemProvider extends DawnFragmentGeneratorItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DawnEMFGeneratorItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addEmfGenModelPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Emf Gen Model feature. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addEmfGenModelPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnEMFGenerator_emfGenModel_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_DawnEMFGenerator_emfGenModel_feature",
                "_UI_DawnEMFGenerator_type"),
            DawnEmfGenmodelPackage.Literals.DAWN_EMF_GENERATOR__EMF_GEN_MODEL, true, false, true, null, null, null));
  }

  /**
   * This returns DawnEMFGenerator.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/DawnEMFGenerator"));
  }

  /**
   * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getText(Object object)
  {
    String label = ((DawnEMFGenerator)object).getFragmentName();
    return label == null || label.length() == 0 ? getString("_UI_DawnEMFGenerator_type")
        : getString("_UI_DawnEMFGenerator_type") + " " + label;
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating a
   * viewer notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created under
   * this object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);
  }

  /**
   * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return DawnGenModelEditPlugin.INSTANCE;
  }

}
