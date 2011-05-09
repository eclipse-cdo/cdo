/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.provider;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator}
 * object. <!-- begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 * @since 1.0
 */
public class DawnFragmentGeneratorItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
    ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public DawnFragmentGeneratorItemProvider(AdapterFactory adapterFactory)
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

      addFragmentNamePropertyDescriptor(object);
      addDawnEditorClassNamePropertyDescriptor(object);
      addDawnGeneratorPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Fragment Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void addFragmentNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
        getResourceLocator(),
        getString("_UI_DawnFragmentGenerator_fragmentName_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DawnFragmentGenerator_fragmentName_feature",
            "_UI_DawnFragmentGenerator_type"), DawngenmodelPackage.Literals.DAWN_FRAGMENT_GENERATOR__FRAGMENT_NAME,
        true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Editor Class Name feature. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   */
  protected void addDawnEditorClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
        getResourceLocator(),
        getString("_UI_DawnFragmentGenerator_dawnEditorClassName_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DawnFragmentGenerator_dawnEditorClassName_feature",
            "_UI_DawnFragmentGenerator_type"),
        DawngenmodelPackage.Literals.DAWN_FRAGMENT_GENERATOR__DAWN_EDITOR_CLASS_NAME, true, false, false,
        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Generator feature. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected void addDawnGeneratorPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
        getResourceLocator(),
        getString("_UI_DawnFragmentGenerator_dawnGenerator_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DawnFragmentGenerator_dawnGenerator_feature",
            "_UI_DawnFragmentGenerator_type"), DawngenmodelPackage.Literals.DAWN_FRAGMENT_GENERATOR__DAWN_GENERATOR,
        true, false, true, null, null, null));
  }

  /**
   * This returns DawnFragmentGenerator.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/DawnFragmentGenerator"));
  }

  /**
   * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String getText(Object object)
  {
    String label = ((DawnFragmentGenerator)object).getFragmentName();
    return label == null || label.length() == 0 ? getString("_UI_DawnFragmentGenerator_type")
        : getString("_UI_DawnFragmentGenerator_type") + " " + label;
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

    switch (notification.getFeatureID(DawnFragmentGenerator.class))
    {
    case DawngenmodelPackage.DAWN_FRAGMENT_GENERATOR__FRAGMENT_NAME:
    case DawngenmodelPackage.DAWN_FRAGMENT_GENERATOR__DAWN_EDITOR_CLASS_NAME:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    }
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
    return DawnCodeGenEditPlugin.INSTANCE;
  }

}
