/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.etypes.provider;

import org.eclipse.emf.cdo.edit.CDOEditPlugin;
import org.eclipse.emf.cdo.edit.CDOItemProviderAdapter;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.etypes.provider.annotation.AnnotationItemProviderAdapterFactory;
import org.eclipse.emf.cdo.etypes.util.BasicAnnotationValidator;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.provider.annotation.EAnnotationItemProviderAdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
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
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptorDecorator;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is the item provider adapter for a {@link java.util.Map.Entry} object.
 * <!-- begin-user-doc -->
 * @since 4.6
 * <!-- end-user-doc -->
 * @generated
 */
public class StringToStringMapEntryItemProvider extends CDOItemProviderAdapter
    implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource,
    ITableItemLabelProvider, ITableItemColorProvider, ITableItemFontProvider, IItemColorProvider, IItemFontProvider, IItemStyledLabelProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StringToStringMapEntryItemProvider(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  /**
   * Returns the {@link AnnotationItemProviderAdapterFactory} if the adapter factory is of that type, <code>null</code> otherwise.
   *
   * @return the annotation item provider adapter factory or <code>null</code>
   */
  protected AnnotationItemProviderAdapterFactory getAnnotationItemProviderAdapterFactory()
  {
    return adapterFactory instanceof AnnotationItemProviderAdapterFactory ? (AnnotationItemProviderAdapterFactory)adapterFactory : null;
  }

  /**
   * Returns the {@link AnnotationItemProviderAdapterFactory#getAssistant() assistant} of the {@link #getAnnotationItemProviderAdapterFactory()
   * annotation item provider adapter factory}, if available.
   *
   * @return the assistant or <code>null</code>.
   */
  protected BasicAnnotationValidator.Assistant getAssistant()
  {
    AnnotationItemProviderAdapterFactory annotationItemProviderAdapterFactory = getAnnotationItemProviderAdapterFactory();
    return annotationItemProviderAdapterFactory == null ? null : annotationItemProviderAdapterFactory.getAssistant();
  }

  /**
   * Returns the containing annotation of the given annotation detail.
   */
  protected Annotation getAnnotation(Object object)
  {
    EObject container = ((EObject)object).eContainer();
    return container instanceof Annotation ? (Annotation)container : null;
  }

  /**
   * Returns the containing model element of the {@link #getAnnotation(Object) containing annotation}.
   */
  protected ModelElement getModelElement(Object object)
  {
    Annotation annotation = getAnnotation(object);
    return annotation != null ? annotation.getModelElement() : null;
  }

  /**
   * This returns the property descriptors for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public List<IItemPropertyDescriptor> getPropertyDescriptorsGen(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addKeyPropertyDescriptor(object);
      addValuePropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation is specialized to clear the cached {@link #itemPropertyDescriptors descriptors} if this adapter was
   * created by an {@link EAnnotationItemProviderAdapterFactory}.
   * <p>
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (getAnnotationItemProviderAdapterFactory() != null)
    {
      itemPropertyDescriptors = null;
    }

    List<IItemPropertyDescriptor> propertyDescriptors = getPropertyDescriptorsGen(object);
    return propertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Key feature.
   * <!-- begin-user-doc -->
   * This implementation is specialized to call {@link #createKeyPropertyDescriptor(Map.Entry)}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addKeyPropertyDescriptor(Object object)
  {
    @SuppressWarnings("unchecked")
    Map.Entry<String, String> entry = (Map.Entry<String, String>)object;
    itemPropertyDescriptors.add(createKeyPropertyDescriptor(entry));
  }

  /**
   * Creates the property descriptor for the entry's key.
   * <p>
   * This implementation creates specialized instance of property descriptor
   * that delegates {@link ItemPropertyDescriptor#getChoiceOfValues(Object)} to {@link #getKeyChoiceOfValues(Map.Entry)},
   * that delegates {@link ItemPropertyDescriptor#isSortChoices(Object)} to {@link #isKeySortChoices(Map.Entry)},
   * and returns <code>true</code> for {@link ItemPropertyDescriptor#isChoiceArbitrary(Object)}.
   * </p>
   * @param entry the detail entry.
   * @return the property descriptor for the entry's key.
   */
  protected IItemPropertyDescriptor createKeyPropertyDescriptor(Map.Entry<String, String> entry)
  {
    return new ValueHandlingPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_StringToStringMapEntry_key_feature"), getString("_UI_StringToStringMapEntry_key_description"),
        EtypesPackage.Literals.STRING_TO_STRING_MAP_ENTRY__KEY, true, false, isKeySortChoices(entry), ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null)
    {
      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        @SuppressWarnings("unchecked")
        Map.Entry<String, String> entry = (Map.Entry<String, String>)object;
        return getKeyChoiceOfValues(entry);
      }
    };
  }

  /**
   * Returns whether this entry supports {@link IItemPropertyDescriptor#isSortChoices(Object) sorting} choices for the property descriptor for the value.
   * <p>
   * This implementation always returns <code>true</code>
   * </p>
   * @param entry the entry to test.
   * @return whether this entry supports sorting choices for the property descriptor for the value.
   */
  protected boolean isKeySortChoices(Map.Entry<String, String> entry)
  {
    return true;
  }

  /**
   * Returns the choices for the value feature.
   * <p>
   * This implementation uses the {@link #getAnnotationItemProviderAdapterFactory()} {@link AnnotationItemProviderAdapterFactory#getAssistant() assistant}
   * to {@link org.eclipse.emf.ecore.util.BasicAnnotationValidator.Assistant#createInstance(EClass, Annotation) create} modeled objects and uses
   * the keys of their {@link org.eclipse.emf.ecore.util.BasicAnnotationValidator.Assistant#getApplicableProperties(EObject, Annotation) applicable properties}
   * to build a set of valid keys, removing any keys already in the {@link Annotation#getDetails() details},
   * and then including the current value of the entry's key.
   * Otherwise it returns <code>null</code>
   * </p>
   * @param entry the entry.
   * @return the choices for the value feature.
   */
  protected Collection<?> getKeyChoiceOfValues(Map.Entry<String, String> entry)
  {
    BasicAnnotationValidator.Assistant assistant = getAssistant();
    if (assistant != null)
    {
      ModelElement modelElement = getModelElement(entry);
      if (modelElement != null)
      {
        Annotation annotation = getAnnotation(entry);
        List<String> result = new UniqueEList<>();
        for (EClass eClass : assistant.getPropertyClasses(modelElement))
        {
          EObject instance = assistant.createInstance(eClass, annotation);
          result.addAll(assistant.getApplicableProperties(instance, annotation).keySet());
        }

        // Remove any property names already used within the annotation.
        result.removeAll(annotation.getDetails().keySet());

        // Add the entry's key, if it's not null.
        String key = entry.getKey();
        result.add(key);

        return result;
      }
    }
    return null;
  }

  /**
   * This adds a property descriptor for the Value feature.
   * <!-- begin-user-doc -->
   * This implementation is specialized to delegate to {@link #createValuePropertyDescriptor(Map.Entry)}.
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addValuePropertyDescriptor(Object object)
  {
    @SuppressWarnings("unchecked")
    Map.Entry<String, String> entry = (Map.Entry<String, String>)object;
    itemPropertyDescriptors.add(createValuePropertyDescriptor(entry));
  }

  /**
   * Creates the property descriptor the entry's value.
   * <p>
   * This implementation creates specialized instance of property descriptor
   * that delegates {@link ItemPropertyDescriptor#getChoiceOfValues(Object)} to {@link #getValueChoiceOfValues(Map.Entry)},
   * that delegates {@link ItemPropertyDescriptor#isMultiLine(Object)} to {@link #isValueMultiLine(Map.Entry)},
   * that delegates {@link ItemPropertyDescriptor#isSortChoices(Object)} to {@link #isValueSortChoices(Map.Entry)},
   * and returns <code>true</code> for {@link ItemPropertyDescriptor#isChoiceArbitrary(Object)}.
   * </p>
   * <p>
   * <p>
   * This implementation uses the {@link #getAnnotationItemProviderAdapterFactory()} {@link AnnotationItemProviderAdapterFactory#getAssistant()
   * assistant} to {@link org.eclipse.emf.ecore.util.BasicAnnotationValidator.Assistant#createInstance(EClass, Annotation) create} modeled
   * objects, when available. If the entry corresponds one of the modeled object's
   * {@link org.eclipse.emf.ecore.util.BasicAnnotationValidator.Assistant#getApplicableProperties(EObject, Annotation) applicable properties},
   * it {@link AnnotationItemProviderAdapterFactory#getPropertyDescriptor(EObject, String, EStructuralFeature, Annotation, ResourceLocator)
   * gets the property descriptor} for that object,
   * {@link AnnotationItemProviderAdapterFactory#createPropertyDescriptorDecorator(IItemPropertyDescriptor, EObject, String, EStructuralFeature,
   * Annotation, ResourceLocator, EditingDomain) creates a decorate} for descriptor, and finally creates yet another decorator that that decorator
   * that makes the property look and behave like a regular value property descriptor, except that the description, value handling, and cell
   * editor is that of the modeled object.
   * </p>
   * </p>
   * @param entry the detail entry.
   * @return the property descriptor for the entry's value.
   */
  protected IItemPropertyDescriptor createValuePropertyDescriptor(final Map.Entry<String, String> entry)
  {
    final IItemPropertyDescriptor valuePropertyDescriptor = new ValueHandlingPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString("_UI_StringToStringMapEntry_value_feature"),
        getString("_UI_StringToStringMapEntry_value_description"), EtypesPackage.Literals.STRING_TO_STRING_MAP_ENTRY__VALUE, true, isValueMultiLine(entry),
        isValueSortChoices(entry), ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null)
    {
      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        @SuppressWarnings("unchecked")
        Map.Entry<String, String> entry = (Map.Entry<String, String>)object;
        return getValueChoiceOfValues(entry);
      }
    };
    AnnotationItemProviderAdapterFactory annotationItemProviderAdapterFactory = getAnnotationItemProviderAdapterFactory();
    BasicAnnotationValidator.Assistant assistant = getAssistant();
    if (annotationItemProviderAdapterFactory != null && assistant != null)
    {
      ModelElement modelElement = getModelElement(entry);
      if (modelElement != null)
      {
        Annotation annotation = getAnnotation(entry);
        List<EClass> propertyClasses = assistant.getPropertyClasses(modelElement);
        String key = entry.getKey();
        for (EClass eClass : propertyClasses)
        {
          EObject instance = assistant.createInstance(eClass, annotation);
          Map<String, EStructuralFeature> applicableProperties = assistant.getApplicableProperties(instance, annotation);
          final EStructuralFeature eStructuralFeature = applicableProperties.get(key);
          if (eClass.getEAllStructuralFeatures().contains(eStructuralFeature))
          {
            IItemPropertyDescriptor itemPropertyDescriptor = annotationItemProviderAdapterFactory.getPropertyDescriptor(instance, key, eStructuralFeature,
                annotation, getResourceLocator());

            EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(annotation);
            IItemPropertyDescriptor decoratedItemPropertyDescriptor = annotationItemProviderAdapterFactory
                .createPropertyDescriptorDecorator(itemPropertyDescriptor, instance, key, eStructuralFeature, annotation, getResourceLocator(), domain);

            return new ItemPropertyDescriptorDecorator(entry, decoratedItemPropertyDescriptor)
            {
              @Override
              public String getDisplayName(Object thisObject)
              {
                return valuePropertyDescriptor.getDisplayName(entry);
              }

              @Override
              public Object getFeature(Object thisObject)
              {
                return eStructuralFeature;
              }

              @Override
              public String getCategory(Object thisObject)
              {
                return null;
              }

              @Override
              public String[] getFilterFlags(Object thisObject)
              {
                return null;
              }

              @Override
              public boolean isPropertySet(Object thisObject)
              {
                return valuePropertyDescriptor.isPropertySet(entry);
              }

              @Override
              public void resetPropertyValue(Object thisObject)
              {
                valuePropertyDescriptor.resetPropertyValue(entry);
              }

              @Override
              public boolean isPropertyUnsettable(Object object)
              {
                Object propertyValue = getPropertyValue(object);
                return propertyValue != null;
              }
            };
          }
        }
      }
    }

    return valuePropertyDescriptor;
  }

  /**
   * Returns whether this entry supports a {@link IItemPropertyDescriptor#isMultiLine(Object) multi-line} property descriptor for the value feature.
   * <p>
   * This implementation always returns <code>true</code>.
   * </p>
   * @param entry the entry in question.
   * @return whether this entry supported a multi-line property descriptor for the value.
   */
  protected boolean isValueMultiLine(Map.Entry<String, String> entry)
  {
    return true;
  }

  /**
   * Returns whether this entry supports {@link IItemPropertyDescriptor#isSortChoices(Object) sorting} choices for the property descriptor for the value feature.
   * <p>
   * This implementation always returns <code>true</code>.
   * </p>
   * @param entry the entry to test.
   * @return whether this entry supports a sorting choices for the property descriptor for the value.
   */
  protected boolean isValueSortChoices(Map.Entry<String, String> entry)
  {
    return true;
  }

  /**
   * Returns the choices for the value feature.
   * This implementation always returns <code>null</code>.
   * @param entry the entry.
   * @return the choices for the value feature.
   */
  protected Collection<?> getValueChoiceOfValues(Map.Entry<String, String> entry)
  {
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean hasChildren(Object object)
  {
    return hasChildren(object, true);
  }

  /**
   * This returns StringToStringMapEntry.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/StringToStringMapEntry")); //$NON-NLS-1$
  }

  /**
   * This returns <code>getImage(object)</code> for the column index <code>0</code> or <code>super.getImage(object)</code> otherwise.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getText(Object)
   * @see #getColumnText(Object, int)
   * @generated NOT
   */
  @Override
  public Object getColumnImage(Object object, int columnIndex)
  {
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
   * @generated NOT
   */
  @Override
  public String getColumnText(Object object, int columnIndex)
  {
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
    Map.Entry<?, ?> stringToStringMapEntry = (Map.Entry<?, ?>)object;
    return new StyledString("" + stringToStringMapEntry.getKey()).append(" -> ", StyledString.Style.QUALIFIER_STYLER) //$NON-NLS-1$//$NON-NLS-2$
        .append("" + stringToStringMapEntry.getValue());
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

    switch (notification.getFeatureID(Map.Entry.class))
    {
    case EtypesPackage.STRING_TO_STRING_MAP_ENTRY__KEY:
    case EtypesPackage.STRING_TO_STRING_MAP_ENTRY__VALUE:
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
   * @author Eike Stepper
   */
  private static class ValueHandlingPropertyDescriptor extends ItemPropertyDescriptor
  {
    public ValueHandlingPropertyDescriptor(AdapterFactory adapterFactory, ResourceLocator resourceLocator, String displayName, String description,
        EStructuralFeature feature, boolean isSettable, boolean multiLine, boolean sortChoices, Object staticImage, String category, String[] filterFlags)
    {
      super(adapterFactory, resourceLocator, displayName, description, feature, isSettable, multiLine, sortChoices, staticImage, category, filterFlags);
    }

    @Override
    public boolean isChoiceArbitrary(Object object)
    {
      return true;
    }
  }
}
