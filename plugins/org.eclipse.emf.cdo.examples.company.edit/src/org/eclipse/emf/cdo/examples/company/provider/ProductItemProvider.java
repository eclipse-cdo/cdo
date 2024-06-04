/*
 * Copyright (c) 2009-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.provider;

import org.eclipse.emf.cdo.edit.CDOItemProviderAdapter;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Product;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemColorProvider;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.examples.company.Company} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class ProductItemProvider extends CDOItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider,
    IItemLabelProvider, IItemPropertySource, IItemColorProvider, IItemFontProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ProductItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addNamePropertyDescriptor(object);
      addVatPropertyDescriptor(object);
      addDescriptionPropertyDescriptor(object);
      addPricePropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Name feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Product_name_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Product_name_feature", "_UI_Product_type"),
        CompanyPackage.Literals.PRODUCT__NAME, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Vat feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addVatPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Product_vat_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Product_vat_feature", "_UI_Product_type"),
        CompanyPackage.Literals.PRODUCT__VAT, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Description feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addDescriptionPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Product_description_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Product_description_feature", "_UI_Product_type"),
        CompanyPackage.Literals.PRODUCT__DESCRIPTION, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Price feature.
   * <!-- begin-user-doc -->
   *
   * @since 4.0<!-- end-user-doc -->
   * @generated
   */
  protected void addPricePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Product_price_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Product_price_feature", "_UI_Product_type"),
        CompanyPackage.Literals.PRODUCT__PRICE, true, false, false, ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean hasChildren(Object object)
  {
    return hasChildren(object, true);
  }

  /**
   * This returns Product.gif.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Product"));
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getText(Object object)
  {
    String label = ((Product)object).getName();
    return label == null || label.length() == 0 ? getString("_UI_Product_type") : getString("_UI_Product_type") + " " + label;
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

    switch (notification.getFeatureID(Product.class))
    {
    case CompanyPackage.PRODUCT__NAME:
    case CompanyPackage.PRODUCT__VAT:
    case CompanyPackage.PRODUCT__DESCRIPTION:
    case CompanyPackage.PRODUCT__PRICE:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
   * that can be created under this object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);
  }

  /**
   * Return the resource locator for this item provider's resources.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return ((IChildCreationExtender)adapterFactory).getResourceLocator();
  }

}
