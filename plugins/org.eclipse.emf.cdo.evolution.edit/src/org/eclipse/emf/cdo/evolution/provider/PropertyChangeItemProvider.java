/**
 */
package org.eclipse.emf.cdo.evolution.provider;

import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.PropertyChange;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.evolution.PropertyChange} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class PropertyChangeItemProvider extends ChangeItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PropertyChangeItemProvider(AdapterFactory adapterFactory)
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

      addFeaturePropertyDescriptor(object);
      addOldValuePropertyDescriptor(object);
      addNewValuePropertyDescriptor(object);
      addKindPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Feature feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addFeaturePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_PropertyChange_feature_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PropertyChange_feature_feature", "_UI_PropertyChange_type"),
        EvolutionPackage.Literals.PROPERTY_CHANGE__FEATURE, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the Old Value feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addOldValuePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_PropertyChange_oldValue_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PropertyChange_oldValue_feature", "_UI_PropertyChange_type"),
        EvolutionPackage.Literals.PROPERTY_CHANGE__OLD_VALUE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the New Value feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addNewValuePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_PropertyChange_newValue_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PropertyChange_newValue_feature", "_UI_PropertyChange_type"),
        EvolutionPackage.Literals.PROPERTY_CHANGE__NEW_VALUE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Kind feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addKindPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_PropertyChange_kind_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_PropertyChange_kind_feature", "_UI_PropertyChange_type"),
        EvolutionPackage.Literals.PROPERTY_CHANGE__KIND, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This returns PropertyChange.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/PropertyChange"));
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
   * This returns the label styled text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object getStyledText(Object object)
  {
    PropertyChange propertyChange = (PropertyChange)object;
    EStructuralFeature feature = propertyChange.getFeature();

    StyledString styledLabel = new StyledString();
    if (feature != null)
    {
      String text = null;

      EClass eClass = feature.getEContainingClass();
      IItemPropertySource propertySource = (IItemPropertySource)getRootAdapterFactory().adapt(eClass, IItemPropertySource.class);
      if (propertySource != null)
      {
        for (IItemPropertyDescriptor propertyDescriptor : propertySource.getPropertyDescriptors(eClass))
        {
          if (propertyDescriptor.getFeature(eClass) == feature)
          {
            text = propertyDescriptor.getDisplayName(eClass);
            break;
          }
        }
      }

      if (text == null)
      {
        text = feature.getName();
      }

      ChangeKind kind = propertyChange.getKind();
      if (kind == ChangeKind.ADDED || kind == ChangeKind.COPIED)
      {
        styledLabel.append(text, ElementChangeItemProvider.ADDITION_STYLER);
      }
      else if (kind == ChangeKind.REMOVED)
      {
        styledLabel.append(text, ElementChangeItemProvider.REMOVAL_STYLER);
      }
      else
      {
        styledLabel.append(text);
      }
    }
    else
    {
      // Can't really happen.
      styledLabel.append(getString("_UI_PropertyChange_type"), StyledString.Style.QUALIFIER_STYLER);
    }

    StringBuilder builder = new StringBuilder(" ");
    formatValue(propertyChange.getOldValue(), builder);
    builder.append(" \u279d ");
    formatValue(propertyChange.getNewValue(), builder);

    styledLabel.append(builder.toString(), StyledString.Style.DECORATIONS_STYLER);
    return styledLabel;
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

    switch (notification.getFeatureID(PropertyChange.class))
    {
    case EvolutionPackage.PROPERTY_CHANGE__OLD_VALUE:
    case EvolutionPackage.PROPERTY_CHANGE__NEW_VALUE:
    case EvolutionPackage.PROPERTY_CHANGE__KIND:
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

  private static void formatValue(Object value, StringBuilder builder)
  {
    if (value instanceof List<?>)
    {
      List<?> list = (List<?>)value;
      boolean first = true;
      for (Object element : list)
      {
        if (first)
        {
          first = false;
        }
        else
        {
          builder.append(", ");
        }

        formatValueSingle(element, builder);
      }
    }
    else
    {
      formatValueSingle(value, builder);
    }
  }

  private static void formatValueSingle(Object value, StringBuilder builder)
  {
    if (value instanceof ENamedElement)
    {
      builder.append(((ENamedElement)value).getName());
    }
    else
    {
      builder.append(String.valueOf(value));
    }
  }

}
