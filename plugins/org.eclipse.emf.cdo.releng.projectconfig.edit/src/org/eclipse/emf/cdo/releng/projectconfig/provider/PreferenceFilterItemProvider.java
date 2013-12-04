/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.projectconfig.provider;

import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.Property;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class PreferenceFilterItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PreferenceFilterItemProvider(AdapterFactory adapterFactory)
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

      addPreferenceNodePropertyDescriptor(object);
      addInclusionsPropertyDescriptor(object);
      addExclusionsPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Preference Node feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addPreferenceNodePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory)
        .getRootAdapterFactory(), getResourceLocator(), getString("_UI_PreferenceFilter_preferenceNode_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PreferenceFilter_preferenceNode_feature",
            "_UI_PreferenceFilter_type"), ProjectConfigPackage.Literals.PREFERENCE_FILTER__PREFERENCE_NODE, true,
        false, true, null, null, null)
    {
      private IItemLabelProvider itemLabelProvider = new IItemLabelProvider()
      {
        public String getText(Object object)
        {
          if (object == null)
          {
            return "";
          }

          PreferenceNode preferenceNode = (PreferenceNode)object;
          List<PreferenceNode> path = PreferencesUtil.getPath(preferenceNode);
          StringBuilder result = new StringBuilder();
          for (int i = 3, size = path.size(); i < size; ++i)
          {
            if (result.length() != 0)
            {
              result.append('/');
            }

            result.append(path.get(i).getName());
          }

          return result.toString();
        }

        public Object getImage(Object object)
        {
          return null;
        }
      };

      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        Collection<Object> result = new ArrayList<Object>();
        PreferenceFilter preferenceFilter = (PreferenceFilter)object;
        PreferenceProfile preferenceProfile = preferenceFilter.getPreferenceProfile();
        if (preferenceProfile != null)
        {
          Project project = preferenceProfile.getProject();
          if (project != null)
          {
            PreferenceNode preferenceNode = project.getPreferenceNode();
            if (preferenceNode != null)
            {
              for (Iterator<EObject> it = preferenceNode.eAllContents(); it.hasNext();)
              {
                EObject eObject = it.next();
                if (eObject instanceof PreferenceNode)
                {
                  result.add(eObject);
                }
              }
            }
          }
        }

        return result;
      }

      @Override
      public IItemLabelProvider getLabelProvider(Object object)
      {
        return itemLabelProvider;
      }
    });
  }

  /**
   * This adds a property descriptor for the Inclusions feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addInclusionsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
        getResourceLocator(),
        getString("_UI_PreferenceFilter_inclusions_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PreferenceFilter_inclusions_feature",
            "_UI_PreferenceFilter_type"), ProjectConfigPackage.Literals.PREFERENCE_FILTER__INCLUSIONS, true, false,
        false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Exclusions feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addExclusionsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
        getResourceLocator(),
        getString("_UI_PreferenceFilter_exclusions_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PreferenceFilter_exclusions_feature",
            "_UI_PreferenceFilter_type"), ProjectConfigPackage.Literals.PREFERENCE_FILTER__EXCLUSIONS, true, false,
        false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This returns PreferenceFilter.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/PreferenceFilter"));
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
    PreferenceFilter preferenceFilter = (PreferenceFilter)object;
    String label = preferenceFilter.getInclusions().toString();
    String exclusions = preferenceFilter.getExclusions().toString();
    if (!"".equals(exclusions))
    {
      label += " - " + exclusions;
    }

    if (label == null)
    {
      label = "<unnamed>";
    }

    PreferenceNode preferenceNode = preferenceFilter.getPreferenceNode();
    if (preferenceNode != null)
    {
      String name = preferenceNode.getName();
      if (name != null)
      {
        label = name + " -> " + label;
      }
    }

    return label;
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(PreferenceFilter.class))
    {
    case ProjectConfigPackage.PREFERENCE_FILTER__INCLUSIONS:
    case ProjectConfigPackage.PREFERENCE_FILTER__EXCLUSIONS:
    case ProjectConfigPackage.PREFERENCE_FILTER__PREFERENCE_NODE:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, true));
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

  Map<Property, IWrapperItemProvider> wrappers = new HashMap<Property, IWrapperItemProvider>();

  private IWrapperItemProvider wrap(final PreferenceFilter preferenceFilter, int i, Property project)
  {
    IWrapperItemProvider wrapper = wrappers.get(project);
    if (wrapper == null)
    {
      wrapper = new DelegatingWrapperItemProvider(project, preferenceFilter, null, i, adapterFactory)
      {
        @Override
        public Object getParent(Object object)
        {
          return preferenceFilter;
        }

        @Override
        public Object getImage(Object object)
        {
          Object image = super.getImage(object);
          List<Object> images = new ArrayList<Object>(2);
          images.add(image);
          images.add(EMFEditPlugin.INSTANCE.getImage("full/ovr16/ControlledObject"));
          return image = new ComposedImage(images);
        }

        @Override
        public boolean hasChildren(Object object)
        {
          return false;
        }

        @Override
        public Collection<?> getChildren(Object object)
        {
          return Collections.emptyList();
        }
      };
      wrappers.put(project, wrapper);
    }
    else
    {
      wrapper.setIndex(i);
    }
    return wrapper;
  }

  @Override
  public Collection<?> getChildren(Object object)
  {
    Collection<Object> result = new ArrayList<Object>();

    PreferenceFilter preferenceFilter = (PreferenceFilter)object;
    PreferenceNode preferenceNode = preferenceFilter.getPreferenceNode();
    if (preferenceNode != null)
    {
      for (Property property : preferenceNode.getProperties())
      {
        String name = property.getName();
        if (name != null)
        {
          if (preferenceFilter.matches(name))
          {
            result.add(wrap(preferenceFilter, -1, property));
          }
        }
      }
    }

    return result;
  }

  /**
   * Return the resource locator for this item provider's resources.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return ProjectConfigEditPlugin.INSTANCE;
  }

}
