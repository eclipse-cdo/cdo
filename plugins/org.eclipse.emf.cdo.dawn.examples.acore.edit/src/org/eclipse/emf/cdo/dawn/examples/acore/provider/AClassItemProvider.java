/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.provider;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class AClassItemProvider extends ABasicClassItemProvider implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n \r\nContributors:\r\n    Martin Fluegge - initial API and implementation\r\n";

  /**
   * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public AClassItemProvider(AdapterFactory adapterFactory)
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

      addSubClassesPropertyDescriptor(object);
      addImplementedInterfacesPropertyDescriptor(object);
      addAssociationsPropertyDescriptor(object);
      addCompositionsPropertyDescriptor(object);
      addAggregationsPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Sub Classes feature. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void addSubClassesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AClass_subClasses_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AClass_subClasses_feature", "_UI_AClass_type"),
        AcorePackage.Literals.ACLASS__SUB_CLASSES, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Implemented Interfaces feature. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   */
  protected void addImplementedInterfacesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AClass_implementedInterfaces_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AClass_implementedInterfaces_feature", "_UI_AClass_type"),
        AcorePackage.Literals.ACLASS__IMPLEMENTED_INTERFACES, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Associations feature. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void addAssociationsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AClass_associations_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AClass_associations_feature", "_UI_AClass_type"),
        AcorePackage.Literals.ACLASS__ASSOCIATIONS, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Compositions feature. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void addCompositionsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AClass_compositions_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AClass_compositions_feature", "_UI_AClass_type"),
        AcorePackage.Literals.ACLASS__COMPOSITIONS, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Aggregations feature. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void addAggregationsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_AClass_aggregations_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_AClass_aggregations_feature", "_UI_AClass_type"),
        AcorePackage.Literals.ACLASS__AGGREGATIONS, true, false, true, null, null, null));
  }

  /**
   * This returns AClass.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/AClass.png"));
  }

  /**
   * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String getText(Object object)
  {
    String label = ((AClass)object).getName();
    return label == null || label.length() == 0 ? getString("_UI_AClass_type") : getString("_UI_AClass_type") + " "
        + label;
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

}
