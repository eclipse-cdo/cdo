/*
 * Copyright (c) 2011, 2012, 2014-2016, 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.provider;

import org.eclipse.emf.cdo.edit.CDOEditPlugin;
import org.eclipse.emf.cdo.edit.CDOItemProviderAdapter;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemColorProvider;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemColorProvider;
import org.eclipse.emf.edit.provider.ITableItemFontProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.etypes.ModelElement} object.
 * <!-- begin-user-doc
 * -->
 *
 * @since 4.0 <!-- end-user-doc -->
 * @generated
 */
public class ModelElementItemProvider extends CDOItemProviderAdapter
    implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource,
    ITableItemLabelProvider, ITableItemColorProvider, ITableItemFontProvider, IItemColorProvider, IItemFontProvider, IItemStyledLabelProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ModelElementItemProvider(AdapterFactory adapterFactory)
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

    }
    return itemPropertyDescriptors;
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
      childrenFeatures.add(EtypesPackage.Literals.MODEL_ELEMENT__ANNOTATIONS);
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean hasChildren(Object object)
  {
    return hasChildren(object, true);
  }

  /**
   * This returns <code>getImage(object)</code> for the column index <code>0</code> or <code>super.getImage(object)</code> otherwise.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getText(Object)
   * @see #getColumnText(Object, int)
   * @generated
   */
  @Override
  public Object getColumnImage(Object object, int columnIndex)
  {
    // TODO: implement this method to return appropriate information for each column.
    // Ensure that you remove @generated or mark it @generated NOT
    return columnIndex == 0 ? getImage(object) : super.getImage(object);
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getText(Object object)
  {
    return ((StyledString)getStyledText(object)).getString();
  }

  /**
   * This returns <code>getText(object)</code> for the column index <code>0</code> or <code>super.getText(object)</code> otherwise.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getImage(Object)
   * @see #getColumnImage(Object, int)
   * @generated
   */
  @Override
  public String getColumnText(Object object, int columnIndex)
  {
    // TODO: implement this method to return appropriate information for each column.
    // Ensure that you remove @generated or mark it @generated NOT
    return columnIndex == 0 ? getText(object) : super.getText(object);
  }

  /**
   * This returns the label styled text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getStyledText(Object object)
  {
    return new StyledString(getString("_UI_ModelElement_type")); //$NON-NLS-1$
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

    switch (notification.getFeatureID(ModelElement.class))
    {
    case EtypesPackage.MODEL_ELEMENT__ANNOTATIONS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
      return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
   * that can be created under this object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);

    ModelElement modelElement = (ModelElement)object;

    // Collect the sources we will use for creating annotations.
    // It will always include null which will always be first.
    Collection<String> sources = new ArrayList<>();
    sources.add(null);

    // Create an annotation that we will temporarily add as a child.
    // We will disable notifications because no adapter should see this happen.
    EList<Annotation> annotations = modelElement.getAnnotations();
    Annotation annotation = EtypesFactory.eINSTANCE.createAnnotation();
    modelElement.eSetDeliver(false);
    try
    {
      // Add the annotation
      annotations.add(annotation);
      IItemPropertyDescriptor propertyDescriptor = new AdapterFactoryItemDelegator(getRootAdapterFactory()).getPropertyDescriptor(annotation,
          EtypesPackage.Literals.ANNOTATION__SOURCE);

      @SuppressWarnings("unchecked")
      Collection<String> choiceOfValues = (Collection<String>)propertyDescriptor.getChoiceOfValues(annotation);
      if (choiceOfValues != null)
      {
        sources.addAll(choiceOfValues);
      }
    }
    finally
    {
      // No matter what might go wrong, we will remove the annotation, re-enable notification, and clear any adapters
      // added to the annotation.
      annotations.remove(annotation);
      modelElement.eSetDeliver(true);
      annotation.eAdapters().clear();
    }

    // Create a child descriptor for each source.
    for (String source : sources)
    {
      newChildDescriptors.add(createChildParameter( //
          EtypesPackage.Literals.MODEL_ELEMENT__ANNOTATIONS, //
          EtypesFactory.eINSTANCE.createAnnotation(source)));
    }
  }

  /**
   * Return the resource locator for this item provider's resources.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    if (adapterFactory instanceof IChildCreationExtender)
    {
      return ((IChildCreationExtender)adapterFactory).getResourceLocator();
    }

    return CDOEditPlugin.INSTANCE;
  }

  /**
   * Strips whitespace and converts the empty string to null.
   *
   * @param value Any string or null.
   * @return the trimmed value or null if it's an empty string.
   * @ADDED
   * @since 4.6
   */
  public String stripToNull(String value)
  {
    if (value != null)
    {
      value = value.trim();
      if (value.length() == 0)
      {
        value = null;
      }
    }

    return value;
  }
}
