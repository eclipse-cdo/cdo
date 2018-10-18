/**
 */
package org.eclipse.emf.cdo.evolution.provider;

import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionFactory;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.evolution.Evolution} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class EvolutionItemProvider extends ModelSetItemProvider
{
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EvolutionItemProvider(AdapterFactory adapterFactory)
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

      addUseEcorePackagePropertyDescriptor(object);
      addUseEresourcePackagePropertyDescriptor(object);
      addUseEtypesPackagePropertyDescriptor(object);
      addUniqueNamespacesPropertyDescriptor(object);
      addRootPackagesPropertyDescriptor(object);
      addAllPackagesPropertyDescriptor(object);
      addMissingPackagesPropertyDescriptor(object);
      addOrderedReleasesPropertyDescriptor(object);
      addInitialReleasePropertyDescriptor(object);
      addLatestReleasePropertyDescriptor(object);
      addNextReleaseVersionPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Use Ecore Package feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addUseEcorePackagePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_useEcorePackage_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_useEcorePackage_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__USE_ECORE_PACKAGE, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Use Eresource Package feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addUseEresourcePackagePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_useEresourcePackage_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_useEresourcePackage_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__USE_ERESOURCE_PACKAGE, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Use Etypes Package feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addUseEtypesPackagePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_useEtypesPackage_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_useEtypesPackage_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__USE_ETYPES_PACKAGE, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Unique Namespaces feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addUniqueNamespacesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_uniqueNamespaces_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_uniqueNamespaces_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__UNIQUE_NAMESPACES, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Root Packages feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addRootPackagesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_rootPackages_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_rootPackages_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__ROOT_PACKAGES, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the All Packages feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addAllPackagesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_allPackages_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_allPackages_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__ALL_PACKAGES, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Ordered Releases feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addOrderedReleasesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_orderedReleases_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_orderedReleases_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__ORDERED_RELEASES, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Initial Release feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addInitialReleasePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_initialRelease_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_initialRelease_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__INITIAL_RELEASE, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Latest Release feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addLatestReleasePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_latestRelease_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_latestRelease_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__LATEST_RELEASE, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Next Release Version feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addNextReleaseVersionPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_nextReleaseVersion_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_nextReleaseVersion_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__NEXT_RELEASE_VERSION, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Missing Packages feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addMissingPackagesPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Evolution_missingPackages_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Evolution_missingPackages_feature", "_UI_Evolution_type"),
        EvolutionPackage.Literals.EVOLUTION__MISSING_PACKAGES, false, false, false, null, null, null));
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Collection<? extends EStructuralFeature> getChildrenFeaturesGen(Object object)
  {
    if (childrenFeatures == null)
    {
      super.getChildrenFeatures(object);
      childrenFeatures.add(EvolutionPackage.Literals.EVOLUTION__MODELS);
      childrenFeatures.add(EvolutionPackage.Literals.EVOLUTION__ORDERED_RELEASES);
    }
    return childrenFeatures;
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
    @SuppressWarnings("unchecked")
    List<EStructuralFeature> childrenFeatures = (List<EStructuralFeature>)getChildrenFeaturesGen(object);

    childrenFeatures.remove(EvolutionPackage.Literals.EVOLUTION__MODELS);
    childrenFeatures.add(0, EvolutionPackage.Literals.EVOLUTION__MODELS);

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

  @Override
  protected boolean hasChildren(Object object, boolean optimized)
  {
    Evolution evolution = (Evolution)object;
    return !(evolution.getModels().isEmpty() && evolution.getReleases().isEmpty());
  }

  /**
   * This returns Evolution.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Evolution"));
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
    return new StyledString(getString("_UI_Evolution_type"));
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

    switch (notification.getFeatureID(Evolution.class))
    {
    case EvolutionPackage.EVOLUTION__USE_ECORE_PACKAGE:
    case EvolutionPackage.EVOLUTION__USE_ERESOURCE_PACKAGE:
    case EvolutionPackage.EVOLUTION__USE_ETYPES_PACKAGE:
    case EvolutionPackage.EVOLUTION__UNIQUE_NAMESPACES:
    case EvolutionPackage.EVOLUTION__RELEASES:
    case EvolutionPackage.EVOLUTION__NEXT_RELEASE_VERSION:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case EvolutionPackage.EVOLUTION__MODELS:
    case EvolutionPackage.EVOLUTION__ORDERED_RELEASES:
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

    newChildDescriptors.add(createChildParameter(EvolutionPackage.Literals.EVOLUTION__MODELS, EvolutionFactory.eINSTANCE.createModel()));
  }

}
