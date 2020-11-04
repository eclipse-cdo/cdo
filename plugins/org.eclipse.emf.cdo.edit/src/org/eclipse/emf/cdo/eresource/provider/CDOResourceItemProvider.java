/*
 * Copyright (c) 2007-2012, 2014-2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.eresource.provider;

import org.eclipse.emf.cdo.eresource.CDOResource;
//import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
//import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.CopyCommand.Helper;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.eresource.CDOResource} object.
 * <!-- begin-user-doc
 * -->
 *
 * @noextend This class is not intended to be subclassed by clients. <!-- end-user-doc -->
 * @generated
 */
public class CDOResourceItemProvider extends CDOResourceLeafItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOResourceItemProvider(AdapterFactory adapterFactory)
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

      addURIPropertyDescriptor(object);
      addModifiedPropertyDescriptor(object);
      addLoadedPropertyDescriptor(object);
      addTrackingModificationPropertyDescriptor(object);
      addTimeStampPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the URI feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addURIPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_CDOResource_uRI_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_CDOResource_uRI_feature", "_UI_CDOResource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        EresourcePackage.Literals.CDO_RESOURCE__URI, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Modified feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addModifiedPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_CDOResource_modified_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_CDOResource_modified_feature", "_UI_CDOResource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        EresourcePackage.Literals.CDO_RESOURCE__MODIFIED, false, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Loaded feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addLoadedPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_CDOResource_loaded_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_CDOResource_loaded_feature", "_UI_CDOResource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        EresourcePackage.Literals.CDO_RESOURCE__LOADED, false, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Tracking Modification feature. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  protected void addTrackingModificationPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_CDOResource_trackingModification_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_CDOResource_trackingModification_feature", //$NON-NLS-1$//$NON-NLS-2$
            "_UI_CDOResource_type"), //$NON-NLS-1$
        EresourcePackage.Literals.CDO_RESOURCE__TRACKING_MODIFICATION, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Time Stamp feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addTimeStampPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_CDOResource_timeStamp_feature"), //$NON-NLS-1$
        getString("_UI_PropertyDescriptor_description", "_UI_CDOResource_timeStamp_feature", //$NON-NLS-1$//$NON-NLS-2$
            "_UI_CDOResource_type"), //$NON-NLS-1$
        EresourcePackage.Literals.CDO_RESOURCE__TIME_STAMP, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(EresourcePackage.Literals.CDO_RESOURCE__CONTENTS);
    }
    return childrenFeatures;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
   * This returns CDOResource.gif.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getImage(Object object)
  {
    Object image = null;

    CDOResource resource = (CDOResource)object;
    if (resource.isRoot())
    {
      InternalCDOView view = (InternalCDOView)resource.cdoView();
      if (view != null)
      {
        image = getResourceLocator().getImage("full/obj16/repo");
      }
    }

    if (image == null)
    {
      image = getResourceLocator().getImage("full/obj16/CDOResource");
    }

    return overlayImage(object, image);
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
   * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public String getText(Object object)
  {
    CDOResource resource = (CDOResource)object;
    if (resource.isRoot())
    {
      InternalCDOView view = (InternalCDOView)resource.cdoView();
      if (view != null)
      {
        return view.getRepositoryName();
      }
    }

    String name = resource.getName();
    return name == null ? resource.toString() : name;
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

    switch (notification.getFeatureID(CDOResource.class))
    {
    case EresourcePackage.CDO_RESOURCE__RESOURCE_SET:
    case EresourcePackage.CDO_RESOURCE__URI:
    case EresourcePackage.CDO_RESOURCE__MODIFIED:
    case EresourcePackage.CDO_RESOURCE__LOADED:
    case EresourcePackage.CDO_RESOURCE__TRACKING_MODIFICATION:
    case EresourcePackage.CDO_RESOURCE__ERRORS:
    case EresourcePackage.CDO_RESOURCE__WARNINGS:
    case EresourcePackage.CDO_RESOURCE__TIME_STAMP:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case EresourcePackage.CDO_RESOURCE__CONTENTS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
      return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created under
   * this object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
  }

  @Override
  public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain, Object sibling)
  {
    return new ArrayList<>();
  }

  @Override
  protected Command createRemoveCommand(EditingDomain domain, EObject owner, EStructuralFeature feature, Collection<?> collection)
  {
    return UnexecutableCommand.INSTANCE;
  }

  @Override
  protected Command createCopyCommand(EditingDomain domain, EObject owner, Helper helper)
  {
    // As a resource can't be pasted anywhere, copying it doesn't make much sense in the first place.
    return UnexecutableCommand.INSTANCE;
  }

}
