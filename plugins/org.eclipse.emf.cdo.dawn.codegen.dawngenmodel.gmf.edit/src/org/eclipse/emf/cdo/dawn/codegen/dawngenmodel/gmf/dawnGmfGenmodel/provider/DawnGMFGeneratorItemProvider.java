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
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.provider;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelPackage;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.provider.DawnFragmentGeneratorItemProvider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * @generated
 * @author Martin Fluegge
 */
public class DawnGMFGeneratorItemProvider extends DawnFragmentGeneratorItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DawnGMFGeneratorItemProvider(AdapterFactory adapterFactory)
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

      addDawnDocumentProviderClassNamePropertyDescriptor(object);
      addDawnEditorUtilClassNamePropertyDescriptor(object);
      addDawnCreationWizardClassNamePropertyDescriptor(object);
      addDawnCanonicalEditingPolicyClassNamePropertyDescriptor(object);
      addDawnDiagramEditPartClassNamePropertyDescriptor(object);
      addDawnEditPartFactoryClassNamePropertyDescriptor(object);
      addDawnEditPartProviderClassNamePropertyDescriptor(object);
      addDawnEditPolicyProviderClassNamePropertyDescriptor(object);
      addGMFGenEditorGeneratorPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Dawn Document Provider Class Name feature. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  protected void addDawnDocumentProviderClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnDocumentProviderClassName_feature"),
            getString("_UI_PropertyDescriptor_description",
                "_UI_DawnGMFGenerator_dawnDocumentProviderClassName_feature", "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Editor Util Class Name feature. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  protected void addDawnEditorUtilClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnEditorUtilClassName_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_DawnGMFGenerator_dawnEditorUtilClassName_feature",
                "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Creation Wizard Class Name feature. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  protected void addDawnCreationWizardClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnCreationWizardClassName_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_DawnGMFGenerator_dawnCreationWizardClassName_feature",
                "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Canonical Editing Policy Class Name feature. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @generated
   */
  protected void addDawnCanonicalEditingPolicyClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnCanonicalEditingPolicyClassName_feature"),
            getString("_UI_PropertyDescriptor_description",
                "_UI_DawnGMFGenerator_dawnCanonicalEditingPolicyClassName_feature", "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME, true, false,
            false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Diagram Edit Part Class Name feature. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  protected void addDawnDiagramEditPartClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnDiagramEditPartClassName_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_DawnGMFGenerator_dawnDiagramEditPartClassName_feature",
                "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Edit Part Factory Class Name feature. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  protected void addDawnEditPartFactoryClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnEditPartFactoryClassName_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_DawnGMFGenerator_dawnEditPartFactoryClassName_feature",
                "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Edit Part Provider Class Name feature. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  protected void addDawnEditPartProviderClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnEditPartProviderClassName_feature"),
            getString("_UI_PropertyDescriptor_description",
                "_UI_DawnGMFGenerator_dawnEditPartProviderClassName_feature", "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Dawn Edit Policy Provider Class Name feature. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  protected void addDawnEditPolicyProviderClassNamePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(), getString("_UI_DawnGMFGenerator_dawnEditPolicyProviderClassName_feature"),
            getString("_UI_PropertyDescriptor_description",
                "_UI_DawnGMFGenerator_dawnEditPolicyProviderClassName_feature", "_UI_DawnGMFGenerator_type"),
            DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the GMF Gen Editor Generator feature. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  protected void addGMFGenEditorGeneratorPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_DawnGMFGenerator_GMFGenEditorGenerator_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_DawnGMFGenerator_GMFGenEditorGenerator_feature",
            "_UI_DawnGMFGenerator_type"),
        DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR, true, false, true, null, null,
        null));
  }

  /**
   * This returns DawnGMFGenerator.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/DawnGMFGenerator"));
  }

  /**
   * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getText(Object object)
  {
    String label = ((DawnGMFGenerator)object).getFragmentName();
    return label == null || label.length() == 0 ? getString("_UI_DawnGMFGenerator_type")
        : getString("_UI_DawnGMFGenerator_type") + " " + label;
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

    switch (notification.getFeatureID(DawnGMFGenerator.class))
    {
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME:
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME:
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME:
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME:
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME:
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME:
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME:
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME:
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
