/**
 */
package org.eclipse.emf.cdo.expressions.provider;

import org.eclipse.emf.cdo.expressions.ExpressionsFactory;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.ListValue;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemColorProvider;
import org.eclipse.emf.edit.provider.IItemFontProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemColorProvider;
import org.eclipse.emf.edit.provider.ITableItemFontProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.expressions.ListValue} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ListValueItemProvider extends ValueItemProvider implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource,
    ITableItemLabelProvider, ITableItemColorProvider, ITableItemFontProvider, IItemColorProvider, IItemFontProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ListValueItemProvider(AdapterFactory adapterFactory)
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

    }
    return itemPropertyDescriptors;
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS);
    }
    return childrenFeatures;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
   * This returns ListValue.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/ListValue"));
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
    return getString("_UI_ListValue_type");
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

    switch (notification.getFeatureID(ListValue.class))
    {
    case ExpressionsPackage.LIST_VALUE__ELEMENTS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
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

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createBooleanValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createByteValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createShortValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createIntValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createLongValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createFloatValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createDoubleValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createCharValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createStringValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createListValue()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createFunctionInvocation()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createMemberInvocation()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createMemberAccess()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createContextAccess()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createThis()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createContainedObject()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createLinkedObject()));

    newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.LIST_VALUE__ELEMENTS,
        ExpressionsFactory.eINSTANCE.createLinkedExpression()));
  }

}
