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
import org.eclipse.emf.cdo.ecore.dependencies.Model;
import org.eclipse.emf.cdo.ecore.dependencies.bundle.DependenciesPlugin;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.ecore.dependencies.Model} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
@SuppressWarnings("unused")
public class ModelItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider,
    IItemLabelProvider, IItemPropertySource, IItemStyledLabelProvider
{
  private final IListener prefsListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof OMPreferencesChangeEvent)
      {
        OMPreference<?> preference = ((OMPreferencesChangeEvent<?>)event).getPreference();
        preferenceChanged(preference);
      }
    }
  };

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public ModelItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
    DependenciesPlugin.PREFS.addListener(prefsListener);
  }

  @Override
  public void dispose()
  {
    DependenciesPlugin.PREFS.removeListener(prefsListener);
    super.dispose();
  }

  protected void preferenceChanged(OMPreference<?> preference)
  {
    if (preference == DependenciesPlugin.PREF_SHOW_CALLERS || preference == DependenciesPlugin.PREF_SHOW_FLAT
        || preference == DependenciesPlugin.PREF_SORT_BY_DEPENDENCIES)
    {
      resetChildren();
    }
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

      addUriPropertyDescriptor(object);
      addFilePropertyDescriptor(object);
      addWorkspacePropertyDescriptor(object);
      addExistsPropertyDescriptor(object);
      addNsURIPropertyDescriptor(object);
      addNamePropertyDescriptor(object);
      addOutgoingLinksPropertyDescriptor(object);
      addIncomingLinksPropertyDescriptor(object);
      addBrokenLinksPropertyDescriptor(object);
      addDependingModelsPropertyDescriptor(object);
      addFlatDependenciesPropertyDescriptor(object);
      addFlatDependingModelsPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Uri feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addUriPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Addressable_uri_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Addressable_uri_feature", "_UI_Addressable_type"),
        DependenciesPackage.Literals.ADDRESSABLE__URI, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the File feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addFilePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_file_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_file_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__FILE, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Workspace feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addWorkspacePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_workspace_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_workspace_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__WORKSPACE, false, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Exists feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addExistsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_exists_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_exists_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__EXISTS, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Ns URI feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addNsURIPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_nsURI_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_nsURI_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__NS_URI, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Name feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_name_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_name_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__NAME, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Outgoing Links feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addOutgoingLinksPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_outgoingLinks_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_outgoingLinks_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__OUTGOING_LINKS, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Incoming Links feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addIncomingLinksPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_incomingLinks_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_incomingLinks_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__INCOMING_LINKS, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Broken Links feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addBrokenLinksPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_brokenLinks_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_brokenLinks_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__BROKEN_LINKS, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Depending Models feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addDependingModelsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_dependingModels_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Model_dependingModels_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__DEPENDING_MODELS, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Flat Dependencies feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addFlatDependenciesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_flatDependencies_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Model_flatDependencies_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__FLAT_DEPENDENCIES, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Flat Depending Models feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addFlatDependingModelsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Model_flatDependingModels_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Model_flatDependingModels_feature", "_UI_Model_type"),
        DependenciesPackage.Literals.MODEL__FLAT_DEPENDING_MODELS, false, false, false, null, null, null));
  }

  protected final void resetChildren()
  {
    childrenFeatures = null;
    childrenStoreMap = null;
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(getChildrenFeature());
    }

    return childrenFeatures;
  }

  protected EReference getChildrenFeature()
  {
    boolean showCallers = DependenciesPlugin.PREF_SHOW_CALLERS.getValue();
    boolean showFlat = DependenciesPlugin.PREF_SHOW_FLAT.getValue();

    if (showCallers)
    {
      if (showFlat)
      {
        return DependenciesPackage.Literals.MODEL__FLAT_DEPENDING_MODELS;
      }

      return DependenciesPackage.Literals.MODEL__DEPENDING_MODELS;
    }

    if (showFlat)
    {
      return DependenciesPackage.Literals.MODEL__FLAT_DEPENDENCIES;
    }

    return DependenciesPackage.Literals.MODEL__DEPENDENCIES;
  }

  @Override
  protected Object getValue(EObject eObject, EStructuralFeature eStructuralFeature)
  {
    Object value = super.getValue(eObject, eStructuralFeature);

    if (eStructuralFeature == getChildrenFeature())
    {
      @SuppressWarnings("unchecked")
      List<Model> models = new ArrayList<>((List<Model>)value);
      return Model.sortModels(models, DependenciesPlugin.PREF_SORT_BY_DEPENDENCIES.getValue());
    }

    return value;
  }

  /**
   * This returns Model.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getImage(Object object)
  {
    Model model = (Model)object;

    String key = "Model";
    if (DependenciesPlugin.PREF_SHOW_CALLERS.getValue())
    {
      if (!model.getDependingModels().isEmpty())
      {
        key = "Callers";
      }
    }
    else
    {
      if (!model.getDependencies().isEmpty())
      {
        key = "Callees";
      }
    }

    return overlayImage(object, getResourceLocator().getImage("full/obj16/" + key + ".gif"));
  }

  @Override
  protected Object overlayImage(Object object, Object image)
  {
    image = super.overlayImage(object, image);

    Model model = (Model)object;
    if (!model.isExists() || model.hasBrokenLinks())
    {
      List<Object> images = new ArrayList<>(2);
      images.add(image);
      images.add(getResourceLocator().getImage("full/ovr16/Error.gif"));
      image = new ComposedImage(images);
    }

    return image;
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
    String label = ((Model)object).getUri().toString();
    return label == null || label.length() == 0 ? getString("_UI_Model_type") : label;
  }

  /**
   * This returns the label styled text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getStyledText(Object object)
  {
    Model model = (Model)object;

    int colorIndex;
    if (!model.isExists())
    {
      colorIndex = URIStyler.MISSING;
    }
    else if (!model.isWorkspace())
    {
      colorIndex = URIStyler.EXTERNAL;
    }
    else
    {
      colorIndex = URIStyler.WORKSPACE;
    }

    return URIStyler.getEMFStyledURI(model.getUri(), colorIndex);
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

    switch (notification.getFeatureID(Model.class))
    {
    case DependenciesPackage.MODEL__URI:
    case DependenciesPackage.MODEL__FILE:
    case DependenciesPackage.MODEL__WORKSPACE:
    case DependenciesPackage.MODEL__EXISTS:
    case DependenciesPackage.MODEL__NS_URI:
    case DependenciesPackage.MODEL__NAME:
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
   * Return the resource locator for this item provider's resources.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return DependenciesPlugin.INSTANCE;
  }
}
