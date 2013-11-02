/**
 */
package org.eclipse.emf.cdo.releng.setup.provider;

import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SourceLocator;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.releng.setup.SourceLocator} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class SourceLocatorItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SourceLocatorItemProvider(AdapterFactory adapterFactory)
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

      addLocationPropertyDescriptor(object);
      addComponentNamePatternPropertyDescriptor(object);
      addComponentTypesPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Location feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addLocationPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors
        .add(createItemPropertyDescriptor(
            ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
            getResourceLocator(),
            getString("_UI_SourceLocator_location_feature"),
            getString("_UI_PropertyDescriptor_description", "_UI_SourceLocator_location_feature",
                "_UI_SourceLocator_type"), SetupPackage.Literals.SOURCE_LOCATOR__LOCATION, true, false, false,
            ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Component Name Pattern feature.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected void addComponentNamePatternPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
        getResourceLocator(),
        getString("_UI_SourceLocator_componentNamePattern_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_SourceLocator_componentNamePattern_feature",
            "_UI_SourceLocator_type"), SetupPackage.Literals.SOURCE_LOCATOR__COMPONENT_NAME_PATTERN, true, false,
        false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Component Types feature.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected void addComponentTypesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
        getResourceLocator(),
        getString("_UI_SourceLocator_componentTypes_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_SourceLocator_componentTypes_feature",
            "_UI_SourceLocator_type"), SetupPackage.Literals.SOURCE_LOCATOR__COMPONENT_TYPES, true, false, false,
        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
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
   * This returns SourceLocator.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/SourceLocator"));
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
    SourceLocator sourceLocator = (SourceLocator)object;
    StringBuilder result = new StringBuilder();
    result.append(sourceLocator.getLocation());
    result.append(" --> ");

    String componentNamePattern = sourceLocator.getComponentNamePattern();
    if (StringUtil.isEmpty(componentNamePattern))
    {
      componentNamePattern = ".*";
    }

    result.append(componentNamePattern);

    result.append(" (");
    int index = result.length();
    for (ComponentType componentType : sourceLocator.getComponentTypes())
    {
      if (result.length() != index)
      {
        result.append(',');
      }

      result.append(componentType.toString());
    }
    result.append(')');

    return result.toString();
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

    switch (notification.getFeatureID(SourceLocator.class))
    {
    case SetupPackage.SOURCE_LOCATOR__LOCATION:
    case SetupPackage.SOURCE_LOCATOR__COMPONENT_NAME_PATTERN:
    case SetupPackage.SOURCE_LOCATOR__COMPONENT_TYPES:
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
    return ((IChildCreationExtender)adapterFactory).getResourceLocator();
  }

}
