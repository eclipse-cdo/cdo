/**
 */
package org.eclipse.emf.cdo.releng.workingsets.provider;

import org.eclipse.emf.cdo.releng.workingsets.InclusionPredicate;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSet;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
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
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.releng.workingsets.InclusionPredicate} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class InclusionPredicateItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InclusionPredicateItemProvider(AdapterFactory adapterFactory)
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

      addIncludedWorkingSetsPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Included Working Sets feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addIncludedWorkingSetsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory)
        .getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_InclusionPredicate_includedWorkingSets_feature"), getString(
            "_UI_PropertyDescriptor_description", "_UI_InclusionPredicate_includedWorkingSets_feature",
            "_UI_InclusionPredicate_type"), WorkingSetsPackage.Literals.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS,
        true, false, true, null, null, null)
    {
      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        Collection<?> result = super.getChoiceOfValues(object);
        ExclusionPredicateItemProvider.filterCircularWorkingSets((EObject)object, result);
        return result;
      }
    });
  }

  /**
   * This returns InclusionPredicate.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/InclusionPredicate"));
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
    InclusionPredicate inclusionPredicate = (InclusionPredicate)object;
    StringBuilder result = new StringBuilder();
    for (WorkingSet workingSet : inclusionPredicate.getIncludedWorkingSets())
    {
      if (result.length() != 0)
      {
        result.append(", ");
      }
      result.append(workingSet.getName());
    }
    return result.toString();
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

    switch (notification.getFeatureID(InclusionPredicate.class))
    {
    case WorkingSetsPackage.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, true));
      return;
    }
    super.notifyChanged(notification);
  }

  @Override
  protected Command factorAddCommand(EditingDomain domain, CommandParameter commandParameter)
  {
    if (commandParameter.getFeature() == null)
    {
      return new AddCommand(domain, commandParameter.getEOwner(),
          WorkingSetsPackage.Literals.INCLUSION_PREDICATE__INCLUDED_WORKING_SETS, commandParameter.getCollection());
    }

    return super.factorAddCommand(domain, commandParameter);
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
    return WorkingSetsEditPlugin.INSTANCE;
  }

}
