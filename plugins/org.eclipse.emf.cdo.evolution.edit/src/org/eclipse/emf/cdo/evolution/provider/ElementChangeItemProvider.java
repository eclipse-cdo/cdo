/**
 */
package org.eclipse.emf.cdo.evolution.provider;

import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.StyledString.Style;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.evolution.ElementChange} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ElementChangeItemProvider extends ChangeItemProvider
{
  private static final URI RED = URI.createURI("color://rgb/200/0/0"); //$NON-NLS-1$

  private static final URI GREEN = URI.createURI("color://rgb/0/160/0"); //$NON-NLS-1$

  static final Style REMOVAL_STYLER = Style.newBuilder().setForegroundColor(RED).toStyle();

  static final Style ADDITION_STYLER = Style.newBuilder().setForegroundColor(GREEN).toStyle();

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ElementChangeItemProvider(AdapterFactory adapterFactory)
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

      addOldElementPropertyDescriptor(object);
      addNewElementPropertyDescriptor(object);
      addKindPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Old Element feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addOldElementPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ElementChange_oldElement_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_ElementChange_oldElement_feature", "_UI_ElementChange_type"),
        EvolutionPackage.Literals.ELEMENT_CHANGE__OLD_ELEMENT, true, false, true, null, null, null));
  }

  /**
   * This adds a property descriptor for the New Element feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addNewElementPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_ElementChange_newElement_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_ElementChange_newElement_feature", "_UI_ElementChange_type"),
        EvolutionPackage.Literals.ELEMENT_CHANGE__NEW_ELEMENT, true, false, true, null, null, null));
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
        getString("_UI_ElementChange_kind_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_ElementChange_kind_feature", "_UI_ElementChange_type"),
        EvolutionPackage.Literals.ELEMENT_CHANGE__KIND, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
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

  @Override
  public Object getImage(Object object)
  {
    ElementChange elementChange = (ElementChange)object;
    EModelElement element = elementChange.getElement();

    if (element != null)
    {
      IItemLabelProvider labelProvider = (IItemLabelProvider)getRootAdapterFactory().adapt(element, IItemLabelProvider.class);
      if (labelProvider != null)
      {
        return labelProvider.getImage(element);
      }
    }

    return null;
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
    ElementChange elementChange = (ElementChange)object;
    EModelElement element = elementChange.getElement();

    StyledString styledLabel = new StyledString();
    ChangeKind kind = elementChange.getKind();

    IItemLabelProvider labelProvider = (IItemLabelProvider)getRootAdapterFactory().adapt(element, IItemLabelProvider.class);
    if (labelProvider != null)
    {
      String text = labelProvider.getText(element);
      if (kind == ChangeKind.ADDED || kind == ChangeKind.COPIED || kind == ChangeKind.MOVED)
      {
        styledLabel.append(text, ADDITION_STYLER);
      }
      else if (kind == ChangeKind.REMOVED)
      {
        styledLabel.append(text, REMOVAL_STYLER);
      }
      else
      {
        styledLabel.append(text);
      }
    }
    else
    {
      styledLabel.append(getString("_UI_ElementChange_type"));
    }

    String label = kind == null || kind == ChangeKind.NONE ? null : kind.toString();
    if (label != null)
    {
      styledLabel.append("  " + label, StyledString.Style.DECORATIONS_STYLER);
    }

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

    switch (notification.getFeatureID(ElementChange.class))
    {
    case EvolutionPackage.ELEMENT_CHANGE__KIND:
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

}
