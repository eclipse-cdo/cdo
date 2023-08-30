/*
 * Copyright (c) 2011-2016, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.etypes.AnnotationValidator;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.etypes.provider.annotation.AnnotationItemProviderAdapterFactory;
import org.eclipse.emf.cdo.etypes.provider.annotation.AnnotationItemProviderAdapterFactory.Group;
import org.eclipse.emf.cdo.etypes.util.BasicAnnotationValidator;
import org.eclipse.emf.cdo.etypes.util.EtypesValidator;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import org.eclipse.emf.ecore.provider.annotation.EAnnotationItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   * Gets the root factory, which must be a {@link ComposeableAdapterFactory}.
   *
   * @since 4.6
   */
  @Override
  protected ComposeableAdapterFactory getRootAdapterFactory()
  {
    return ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory();
  }

  /**
   * Returns the {@link EAnnotationItemProviderAdapterFactory} if the adapter factory is of that type, <code>null</code> otherwise.
   *
   * @return the annotation item provider adapter factory or <code>null</code>
   * @since 4.6
   */
  protected AnnotationItemProviderAdapterFactory getAnnotationItemProviderAdapterFactory()
  {
    return adapterFactory instanceof AnnotationItemProviderAdapterFactory ? (AnnotationItemProviderAdapterFactory)adapterFactory : null;
  }

  /**
   * Returns the {@link AnnotationItemProviderAdapterFactory#getAssistant() assistant} of the {@link #getAnnotationItemProviderAdapterFactory() annotation item provider adapter factory}, if available.
   *
   * @return the assistant or <code>null</code>.
   * @since 4.6
   */
  protected BasicAnnotationValidator.Assistant getAssistant()
  {
    AnnotationItemProviderAdapterFactory annotationItemProviderAdapterFactory = getAnnotationItemProviderAdapterFactory();
    return annotationItemProviderAdapterFactory == null ? null : annotationItemProviderAdapterFactory.getAssistant();
  }

  /**
   * This returns the property descriptors for the adapted class.
   * <!-- begin-user-doc -->
   * @since 4.6
   * <!-- end-user-doc -->
   * @generated
   */
  public List<IItemPropertyDescriptor> getPropertyDescriptorsGen(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      addSourcePropertyDescriptor(object);
      addReferencesPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

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
   * This adds a property descriptor for the Source feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addSourcePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new SourcePropertyDescriptor());
  }

  /**
   * This adds a property descriptor for the References feature.
   * <!-- begin-user-doc -->
   * This implementation is specialized to use the {@link #getAssistant() assistant} when available. If the assistant
   * indicates that {@link org.eclipse.emf.cdo.etypes.util.BasicAnnotationValidator.Assistant#isReferencesSupported(Annotation)
   * references aren't supported} and the {@link Annotation#getReferences() references} are empty,
   * the property descriptor is not added. Otherwise an instance of {@link ReferencesPropertyDescriptor} is created.
   * @see ReferencesPropertyDescriptor
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addReferencesPropertyDescriptor(Object object)
  {
    BasicAnnotationValidator.Assistant assistant = getAssistant();
    if (assistant != null)
    {
      Annotation annotation = (Annotation)object;
      if (!assistant.isReferencesSupported(annotation) && annotation.getReferences().isEmpty())
      {
        return;
      }
    }

    itemPropertyDescriptors.add(new ReferencesPropertyDescriptor());
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

          if (builder.length() != 0)
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
   * @generated NOT
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
  {
    super.collectNewChildDescriptors(newChildDescriptors, object);
    newChildDescriptors.add(
        createChildParameter(EtypesPackage.Literals.ANNOTATION__DETAILS, EtypesFactory.eINSTANCE.create(EtypesPackage.Literals.STRING_TO_STRING_MAP_ENTRY)));
  }

  /**
   * A specialized property descriptor for the {@link Annotation#getSource() annotation source} feature.
   * <p>
   * This implementation that is {@link AnnotationItemProviderAdapterFactory}-aware,
   * using its {@link AnnotationItemProviderAdapterFactory#getAssistant() assistant} when available.
   * It specializes {@link #createPropertyValueWrapper(Object, Object) property value wrapper creation} to provide assistant-driven nested property descriptors.
   * </p>
   *
   * @since 4.6
   */
  protected class SourcePropertyDescriptor extends ItemPropertyDescriptor
  {
    public SourcePropertyDescriptor()
    {
      super(getRootAdapterFactory(), getResourceLocator(), getString("_UI_Annotation_source_feature"), getString("_UI_Annotation_source_description"),
          EtypesPackage.Literals.ANNOTATION__SOURCE, true, false, true, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null);
    }

    /**
     * Creates a property value wrapper for the given object's property value.
     * <p>
     * This implementation creates a hierarchy of property descriptors if the {@link #getAssistant() assistant} is available,
     * if the assistant considers the annotation {@link org.eclipse.emf.ecore.util.BasicAnnotationValidator.Assistant#isValidLocation(Annotation) valid at this location},
     * and if the assistant returns {@link org.eclipse.emf.ecore.util.BasicAnnotationValidator.Assistant#getPropertyClasses(org.eclipse.emf.ecore.EModelElement) one or more modeled annotation classes}.
     * In that case, it {@link org.eclipse.emf.ecore.util.BasicAnnotationValidator.Assistant#createInstance(EClass, Annotation) creates} the modeled object of each class,
     * {@link AnnotationItemProviderAdapterFactory#isShowInstances(Annotation) optionally including} it in the tree,
     * and if the property descriptors are {@link org.eclipse.emf.ecore.provider.annotation.AnnotationItemProviderAdapterFactory.DecategorizingItemPropertyDescritorDecorator#getCategory() categorized},
     * it creates an additional tree nesting per category.
     * Otherwise it simply creates simply delegates to <code>super</code>.
     * <p>
     * @param object the object.
     * @param propertyValue the property value of that object.
     */
    @Override
    protected Object createPropertyValueWrapper(Object object, Object propertyValue)
    {
      BasicAnnotationValidator.Assistant assistant = getAssistant();
      if (assistant != null)
      {
        final Annotation annotation = (Annotation)object;
        if (assistant.isValidLocation(annotation))
        {
          List<EClass> propertyClasses = assistant.getPropertyClasses(annotation.getModelElement());
          if (!propertyClasses.isEmpty())
          {
            AnnotationItemProviderAdapterFactory.Group group = new AnnotationItemProviderAdapterFactory.Group(propertyValue);
            Map<String, Group> categories = new HashMap<>();
            AnnotationItemProviderAdapterFactory annotationItemProviderAdapterFactory = getAnnotationItemProviderAdapterFactory();
            boolean showInstances = annotationItemProviderAdapterFactory.isShowInstances(annotation);
            boolean onlyMisc = true;
            for (EClass propertyClass : propertyClasses)
            {
              EObject instance = assistant.createInstance(propertyClass, annotation);
              Group targetGroup = group;
              if (showInstances)
              {
                Group classGroup = new Group(instance);
                String groupName = annotationItemProviderAdapterFactory.getGroupName(instance);
                GroupPropertyDescriptor groupPropertyDescriptor = new GroupPropertyDescriptor(groupName, groupName, classGroup);
                group.add(groupPropertyDescriptor);
                targetGroup = classGroup;
                categories.clear();
                onlyMisc = true;
              }

              List<IItemPropertyDescriptor> propertyDescriptors = annotationItemProviderAdapterFactory.getPropertyDescriptors(instance, annotation,
                  annotationItemProviderAdapterFactory.getResourceLocator());
              for (IItemPropertyDescriptor propertyDescriptor : propertyDescriptors)
              {
                String category = null;
                if (propertyDescriptor instanceof AnnotationItemProviderAdapterFactory.DecategorizingItemPropertyDescritorDecorator)
                {
                  category = ((AnnotationItemProviderAdapterFactory.DecategorizingItemPropertyDescritorDecorator)propertyDescriptor).getCategory();
                }

                Group categoryGroup = categories.get(category);
                if (categoryGroup == null)
                {
                  categoryGroup = new Group(category);
                  categories.put(category, categoryGroup);
                  if (category == null)
                  {
                    category = EcoreEditPlugin.INSTANCE.getString("_UI_Misc_property_category");
                  }
                  else
                  {
                    onlyMisc = false;
                  }

                  targetGroup.add(new GroupPropertyDescriptor(category, category, categoryGroup));
                }

                categoryGroup.add(propertyDescriptor);
              }

              if (showInstances && onlyMisc)
              {
                List<IItemPropertyDescriptor> groupPropertyDescriptors = targetGroup.getPropertyDescriptors();
                groupPropertyDescriptors.clear();
                if (!categories.isEmpty())
                {
                  groupPropertyDescriptors.addAll(categories.values().iterator().next().getPropertyDescriptors());
                }
              }
            }

            if (!showInstances && onlyMisc)
            {
              List<IItemPropertyDescriptor> groupPropertyDescriptors = group.getPropertyDescriptors();
              groupPropertyDescriptors.clear();
              if (!categories.isEmpty())
              {
                groupPropertyDescriptors.addAll(categories.values().iterator().next().getPropertyDescriptors());
              }
            }

            return group;
          }
        }
      }

      return super.createPropertyValueWrapper(object, propertyValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(Object object, Object value)
    {
      Annotation annotation = (Annotation)object;
      String source = annotation.getSource();
      String strippedValue = stripToNull((String)value);
      if (strippedValue == null ? source != null : !strippedValue.equals(source))
      {
        super.setPropertyValue(object, strippedValue);
      }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation collects all the existing {@link Annotation#getSource() annotation sources} in the containing model,
     * adds to that the annotation source of any {@link org.eclipse.emf.cdo.etypes.AnnotationValidator.Registry registered} annotation validator
     * that returns <code>true</code> for {@link AnnotationValidator#isValidLocation(Annotation)},
     * removing the annotation source for any annotation validator that returns <code>false</code>.
     * </p>
     */
    @Override
    public Collection<?> getChoiceOfValues(Object object)
    {
      Annotation annotation = (Annotation)object;
      List<Object> result = new UniqueEList<>();

      for (String annotationSource : AnnotationValidator.Registry.INSTANCE.getAnnotationSources())
      {
        AnnotationValidator annotationValidator = AnnotationValidator.Registry.INSTANCE.getAnnotationValidator(annotationSource);
        if (annotationValidator.isValidLocation(annotation))
        {
          result.add(annotationSource);
        }
        else
        {
          result.remove(annotationSource);
        }
      }

      String source = annotation.getSource();
      if (source != null)
      {
        result.add(source);
      }

      return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is specialized to always return <code>true</code>.
     * </p>
     */
    @Override
    public boolean isChoiceArbitrary(Object object)
    {
      return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is specialized to do additional validation
     * checking to ensure that the source URI is {@link EtypesValidator#validateAnnotation_WellFormedSourceURI(Annotation, org.eclipse.emf.common.util.DiagnosticChain, Map) well-formed}.
     * </p>
     */
    @Override
    public ValueHandler getValueHandler(Object object)
    {
      return new DataTypeValueHandler((EDataType)feature.getEType())
      {
        @Override
        protected Diagnostic validate(EDataType eDataType, Object instance)
        {
          Annotation annotation = EtypesFactory.eINSTANCE.createAnnotation();
          annotation.setSource((String)instance);
          BasicDiagnostic diagnostic = new BasicDiagnostic();
          EtypesValidator.INSTANCE.validateAnnotation_WellFormedSourceURI(annotation, diagnostic, null);
          return diagnostic;
        }
      };
    }
  }

  /**
   * A specialized property descriptor for the {@link Annotation#getReferences() annotation references} feature.
   * Specialized {@link AnnotationItemProvider annotation item providers}
   * created by an {@link AnnotationItemProviderAdapterFactory annotation item provider adapter factory}
   * can override the {@link #getChoiceOfValues(Object) value choices} to provide more restricted choices.
   * But they won't generally need to do that because this implementation delegates to the {@link AnnotationItemProvider#getAssistant() assistant}.
   *
   * @since 4.6
   */
  protected class ReferencesPropertyDescriptor extends ItemPropertyDescriptor
  {
    public ReferencesPropertyDescriptor()
    {
      super(getRootAdapterFactory(), getResourceLocator(), getString("_UI_Annotation_references_feature"), getString("_UI_Annotation_references_description"),
          EtypesPackage.Literals.ANNOTATION__REFERENCES, true, false, true, null, null, null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is specialized to delegate to the {@link org.eclipse.emf.cdo.etypes.util.BasicAnnotationValidator.Assistant#getValidReferences(Annotation, Collection) assistant}.
     * </p>
     */
    @Override
    public Collection<?> getChoiceOfValues(Object object)
    {
      Collection<?> result = super.getChoiceOfValues(object);
      BasicAnnotationValidator.Assistant assistant = getAssistant();
      if (assistant != null)
      {
        result = assistant.getValidReferences((Annotation)object, result);
      }
      return result;
    }
  }

  private static final class GroupPropertyDescriptor implements IItemPropertyDescriptor
  {
    private String label;

    private String description;

    private Object itemPropertySource;

    public GroupPropertyDescriptor(String label, String description, IItemPropertySource itemPropertySource)
    {
      this.label = label;
      this.description = description;
      this.itemPropertySource = itemPropertySource;
    }

    @Override
    public void setPropertyValue(Object object, Object value)
    {
    }

    @Override
    public void resetPropertyValue(Object object)
    {
    }

    @Override
    public boolean isSortChoices(Object object)
    {
      return false;
    }

    @Override
    public boolean isPropertySet(Object object)
    {
      return false;
    }

    @Override
    public boolean isMultiLine(Object object)
    {
      return false;
    }

    @Override
    public boolean isMany(Object object)
    {
      return false;
    }

    @Override
    public boolean isCompatibleWith(Object object, Object anotherObject, IItemPropertyDescriptor anotherPropertyDescriptor)
    {
      return false;
    }

    @Override
    public Object getPropertyValue(Object object)
    {
      return itemPropertySource;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      return new IItemLabelProvider()
      {
        @Override
        public String getText(Object object)
        {
          return "";
        }

        @Override
        public Object getImage(Object object)
        {
          return ItemPropertyDescriptor.NO_VALUE_IMAGE;
        }
      };
    }

    @Override
    public String getId(Object object)
    {
      return label;
    }

    @Override
    public Object getHelpContextIds(Object object)
    {
      return null;
    }

    @Override
    public String[] getFilterFlags(Object object)
    {
      return null;
    }

    @Override
    public Object getFeature(Object object)
    {
      return label;
    }

    @Override
    public String getDisplayName(Object object)
    {
      return label;
    }

    @Override
    public String getDescription(Object object)
    {
      return description;
    }

    @Override
    public Collection<?> getChoiceOfValues(Object object)
    {
      return null;
    }

    @Override
    public String getCategory(Object object)
    {
      return null;
    }

    @Override
    public boolean canSetProperty(Object object)
    {
      return false;
    }
  }
}
