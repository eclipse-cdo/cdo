/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.provider;

import org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage;
import org.eclipse.emf.cdo.ecore.dependencies.Element;
import org.eclipse.emf.cdo.ecore.dependencies.bundle.DependenciesPlugin;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.ecore.dependencies.Element} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
@Deprecated
@SuppressWarnings("unused")
public class ElementItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider,
    IItemLabelProvider, IItemPropertySource, IItemStyledLabelProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  public ElementItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  @Deprecated
  @Override
  public void dispose()
  {
    super.dispose();
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

      addUriPropertyDescriptor(object);
      addExistsPropertyDescriptor(object);
      addIncomingLinksPropertyDescriptor(object);
      addBrokenLinksPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Uri feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  protected void addUriPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Addressable_uri_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Addressable_uri_feature", "_UI_Addressable_type"),
        DependenciesPackage.Literals.ADDRESSABLE__URI, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Exists feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  protected void addExistsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Element_exists_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Element_exists_feature", "_UI_Element_type"),
        DependenciesPackage.Literals.ELEMENT__EXISTS, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Incoming Links feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  protected void addIncomingLinksPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Element_incomingLinks_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Element_incomingLinks_feature", "_UI_Element_type"),
        DependenciesPackage.Literals.ELEMENT__INCOMING_LINKS, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Broken Links feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  protected void addBrokenLinksPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Element_brokenLinks_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Element_brokenLinks_feature", "_UI_Element_type"),
        DependenciesPackage.Literals.ELEMENT__BROKEN_LINKS, false, false, false, null, null, null));
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(DependenciesPackage.Literals.ELEMENT__OUTGOING_LINKS);
    }
    return childrenFeatures;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  protected EStructuralFeature getChildFeature(Object object, Object child)
  {
    // Check the type of the specified child object and return the proper feature to use for
    // adding (see {@link AddCommand}) it as a child.

    return super.getChildFeature(object, child);
  }

  /**
   * This returns Element.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Element"));
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
   * @generated
   */
  @Deprecated
  @Override
  public String getText(Object object)
  {
    return ((StyledString)getStyledText(object)).getString();
  }

  /**
   * This returns the label styled text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public Object getStyledText(Object object)
  {
    URI labelValue = ((Element)object).getUri();
    String label = labelValue == null ? null : labelValue.toString();
    StyledString styledLabel = new StyledString();
    if (label == null || label.length() == 0)
    {
      styledLabel.append(getString("_UI_Element_type"), StyledString.Style.QUALIFIER_STYLER);
    }
    else
    {
      styledLabel.append(getString("_UI_Element_type"), StyledString.Style.QUALIFIER_STYLER).append(" " + label);
    }
    return styledLabel;
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

    switch (notification.getFeatureID(Element.class))
    {
    case DependenciesPackage.ELEMENT__URI:
    case DependenciesPackage.ELEMENT__EXISTS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case DependenciesPackage.ELEMENT__OUTGOING_LINKS:
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
  @Deprecated
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);
  }

  /**
   * Return the resource locator for this item provider's resources.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public ResourceLocator getResourceLocator()
  {
    return DependenciesPlugin.INSTANCE;
  }
}
