/*
 * Copyright (c) 2011-2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.provider;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.etypes.Annotation} object.
 * <!-- begin-user-doc -->
 *
 * @since 4.0 <!-- end-user-doc -->
 * @generated
 */
public class AnnotationItemProvider extends ModelElementItemProvider
{
  private static final boolean DISABLE_LABEL_DECORATION = OMPlatform.INSTANCE
      .isProperty("org.eclipse.emf.cdo.etypes.provider.AnnotationItemProvider.DISABLE_LABEL_DECORATION");

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public AnnotationItemProvider(AdapterFactory adapterFactory)
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

      addSourcePropertyDescriptor(object);
      addReferencesPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Source feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addSourcePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Annotation_source_feature"), //$NON-NLS-1$
        getString("_UI_Annotation_source_description"), //$NON-NLS-1$
        EtypesPackage.Literals.ANNOTATION__SOURCE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the References feature.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected void addReferencesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Annotation_references_feature"), //$NON-NLS-1$
        getString("_UI_Annotation_references_description"), //$NON-NLS-1$
        EtypesPackage.Literals.ANNOTATION__REFERENCES, true, false, true, null, null, null));
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
      childrenFeatures.add(EtypesPackage.Literals.ANNOTATION__DETAILS);
      childrenFeatures.add(EtypesPackage.Literals.ANNOTATION__CONTENTS);
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
   * This returns Annotation.gif.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Annotation")); //$NON-NLS-1$
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
    Annotation annotation = (Annotation)object;
    StringBuffer result = new StringBuffer();

    String source = annotation.getSource();
    if (source != null)
    {
      int index = getParent(annotation) instanceof Annotation ? -1 : source.lastIndexOf("/");
      if (index == -1)
      {
        result.append(source);
      }
      else
      {
        result.append(source.substring(index + 1));
      }
    }

    return result.toString();
  }

  @Override
  public Object getStyledText(Object object)
  {
    StyledString styledString = new StyledString(getText(object));

    if (!DISABLE_LABEL_DECORATION)
    {
      EList<EObject> references = ((Annotation)object).getReferences();
      if (!references.isEmpty())
      {
        AdapterFactory rootAdapterFactory = getRootAdapterFactory();
        if (rootAdapterFactory != null)
        {
          StringBuilder builder = new StringBuilder();

          for (EObject reference : references)
          {
            IItemLabelProvider labelProvider = (IItemLabelProvider)rootAdapterFactory.adapt(reference, IItemLabelProvider.class);
            String label = labelProvider == null ? null : labelProvider.getText(reference);
            if (!StringUtil.isEmpty(label))
            {
              StringUtil.appendSeparator(builder, ", ");
              builder.append(label);
            }

          }

          if (!builder.isEmpty())
          {
            builder.insert(0, "  ");
            styledString.append(builder.toString(), StyledString.Style.DECORATIONS_STYLER);
          }
        }
      }
    }

    return styledString;
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

    switch (notification.getFeatureID(Annotation.class))
    {
    case EtypesPackage.ANNOTATION__SOURCE:
    case EtypesPackage.ANNOTATION__REFERENCES:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case EtypesPackage.ANNOTATION__DETAILS:
    case EtypesPackage.ANNOTATION__CONTENTS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
      return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
   * that can be created under this object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);
  }

}
