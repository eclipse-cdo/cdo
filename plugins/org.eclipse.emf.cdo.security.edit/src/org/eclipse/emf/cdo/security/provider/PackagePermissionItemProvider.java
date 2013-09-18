/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.provider;

import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.PackagePermission;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemColorProvider;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemColorProvider;
import org.eclipse.emf.edit.provider.ITableItemFontProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.security.PackagePermission} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
@Deprecated
public class PackagePermissionItemProvider extends PermissionItemProvider implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource,
    ITableItemLabelProvider, ITableItemColorProvider, ITableItemFontProvider, IItemColorProvider, IItemFontProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PackagePermissionItemProvider(AdapterFactory adapterFactory)
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

      addApplicablePackagePropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Applicable Package feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addApplicablePackagePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory)
        .getRootAdapterFactory(), getResourceLocator(), getString("_UI_PackagePermission_applicablePackage_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PackagePermission_applicablePackage_feature",
            "_UI_PackagePermission_type"), SecurityPackage.Literals.PACKAGE_PERMISSION__APPLICABLE_PACKAGE, true,
        false, true, null, null, null)
    {
      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        if (object instanceof PackagePermission)
        {
          PackagePermission packagePermission = (PackagePermission)object;
          CDOView view = packagePermission.cdoView();
          if (view != null)
          {
            List<EPackage> result = new ArrayList<EPackage>();
            for (CDOPackageInfo packageInfo : view.getSession().getPackageRegistry().getPackageInfos())
            {
              result.add(packageInfo.getEPackage());
            }

            Collections.sort(result, new Comparator<EPackage>()
            {
              public int compare(EPackage p1, EPackage p2)
              {
                return p1.getNsURI().compareTo(p2.getNsURI());
              }
            });

            return result;
          }
        }

        return super.getChoiceOfValues(object);
      }
    });
  }

  /**
   * This returns PackagePermission.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/PackagePermission")); //$NON-NLS-1$
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
    Access labelValue = ((PackagePermission)object).getAccess();
    EPackage applicablePackage = ((PackagePermission)object).getApplicablePackage();
    String label = labelValue == null ? "?" : labelValue.toString(); //$NON-NLS-1$

    if (applicablePackage != null)
    {
      label += " " + applicablePackage.getName(); //$NON-NLS-1$
    }

    return label == null || label.length() == 0 ? getString("_UI_PackagePermission_type") : label; //$NON-NLS-1$
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
  }

}
