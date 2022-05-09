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
package org.eclipse.emf.cdo.lm.modules.provider;

import org.eclipse.emf.cdo.etypes.provider.ModelElementItemProvider;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.equinox.p2.metadata.VersionRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a
 * {@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class DependencyDefinitionItemProvider extends ModelElementItemProvider
{
  private static final TargetNameProvider TARGET_NAME_PROVIDER = initTargetNameProvider();

  /**
   * This constructs an instance from a factory and a notifier. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DependencyDefinitionItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addTargetNamePropertyDescriptor(object);
      addVersionRangePropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Target Name feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  protected void addTargetNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DependencyDefinition_targetName_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DependencyDefinition_targetName_feature", "_UI_DependencyDefinition_type"),
        ModulesPackage.Literals.DEPENDENCY_DEFINITION__TARGET_NAME, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null, null)
    {

      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        return TARGET_NAME_PROVIDER.getTargetNames((DependencyDefinition)object);
      }
    });
  }

  /**
   * This adds a property descriptor for the Version Range feature. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addVersionRangePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DependencyDefinition_versionRange_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DependencyDefinition_versionRange_feature", "_UI_DependencyDefinition_type"),
        ModulesPackage.Literals.DEPENDENCY_DEFINITION__VERSION_RANGE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This returns DependencyDefinition.gif.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/DependencyDefinition"));
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
   * This returns the label text for the adapted class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public String getText(Object object)
  {
    DependencyDefinition dependencyDefinition = (DependencyDefinition)object;

    String label = dependencyDefinition.getTargetName();
    if (label != null)
    {
      VersionRange versionRange = dependencyDefinition.getVersionRange();
      if (versionRange != null)
      {
        label += " " + versionRange;
      }
    }

    return label == null || label.length() == 0 ? getString("_UI_DependencyDefinition_type") : getString("_UI_DependencyDefinition_type") + " " + label;
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

    switch (notification.getFeatureID(DependencyDefinition.class))
    {
    case ModulesPackage.DEPENDENCY_DEFINITION__TARGET_NAME:
    case ModulesPackage.DEPENDENCY_DEFINITION__VERSION_RANGE:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
  }

  /**
   * Return the resource locator for this item provider's resources. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return ModulesEditPlugin.INSTANCE;
  }

  private static TargetNameProvider initTargetNameProvider()
  {
    List<TargetNameProvider> providers = new ArrayList<>();

    IPluginContainer.INSTANCE.forEachElement(TargetNameProvider.Factory.PRODUCT_GROUP, TargetNameProvider.class, provider -> {
      if (provider != null)
      {
        providers.add(provider);
      }
    });

    return new TargetNameProvider()
    {
      @Override
      public Collection<String> getTargetNames(DependencyDefinition dependency)
      {
        for (TargetNameProvider provider : providers)
        {
          try
          {
            Collection<String> targetNames = provider.getTargetNames(dependency);
            if (targetNames != null)
            {
              return targetNames;
            }
          }
          catch (Exception ex)
          {
            ModulesEditPlugin.INSTANCE.log(ex);
          }
        }

        return null;
      }
    };
  }

  /**
   * @author Eike Stepper
   */
  public interface TargetNameProvider
  {
    public Collection<String> getTargetNames(DependencyDefinition dependency);

    /**
     * @author Eike Stepper
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.lm.modules.targetNameProviders";

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract TargetNameProvider create(String description) throws ProductCreationException;
    }
  }
}
