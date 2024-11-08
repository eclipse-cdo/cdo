/**
 */
package org.eclipse.emf.cdo.lm.reviews.provider;

import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicStatus;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.StyledString.Style;
import org.eclipse.emf.edit.provider.ViewerNotification;

import java.util.Collection;
import java.util.List;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.lm.reviews.Topic} object.
 * <!-- begin-user-doc -->
 * @since 1.1
 * <!-- end-user-doc -->
 * @generated
 */
public class TopicItemProvider extends TopicContainerItemProvider
{
  public static final Style STYLE_UNRESOLVED = Style.newBuilder().setForegroundColor(URI.createURI("color://rgb/220/40/40")).toStyle();

  public static final Style STYLE_RESOLVED = Style.newBuilder().setForegroundColor(URI.createURI("color://rgb/20/180/20")).toStyle();

  private ItemPropertyDescriptor statusPropertyDescriptor;

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TopicItemProvider(AdapterFactory adapterFactory)
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

      addIdPropertyDescriptor(object);
      addTextPropertyDescriptor(object);
      addAuthorPropertyDescriptor(object);
      addCreationTimePropertyDescriptor(object);
      addEditTimePropertyDescriptor(object);
      addHeadingPropertyDescriptor(object);
      addModelReferencePropertyDescriptor(object);
      addStatusPropertyDescriptor(object);
      addParentHeadingPropertyDescriptor(object);
      addPreviousHeadingPropertyDescriptor(object);
      addNextHeadingPropertyDescriptor(object);
      addOutlineNumberPropertyDescriptor(object);
      addParentIndexPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Id feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addIdPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Authorable_id_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Authorable_id_feature", "_UI_Authorable_type"),
        ReviewsPackage.Literals.AUTHORABLE__ID, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Text feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTextPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Authorable_text_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Authorable_text_feature", "_UI_Authorable_type"),
        ReviewsPackage.Literals.AUTHORABLE__TEXT, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Heading feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addHeadingPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_heading_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_heading_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__HEADING, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Model Reference feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addModelReferencePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_modelReference_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_modelReference_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__MODEL_REFERENCE, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Status feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addStatusPropertyDescriptor(Object object)
  {
    // Remember the statusPropertyDescriptor for getStyledText() below.
    statusPropertyDescriptor = createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_status_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_status_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__STATUS, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null);
    itemPropertyDescriptors.add(statusPropertyDescriptor);
  }

  /**
   * This adds a property descriptor for the Author feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addAuthorPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Authorable_author_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Authorable_author_feature", "_UI_Authorable_type"),
        ReviewsPackage.Literals.AUTHORABLE__AUTHOR, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Creation Time feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addCreationTimePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Authorable_creationTime_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_Authorable_creationTime_feature", "_UI_Authorable_type"),
        ReviewsPackage.Literals.AUTHORABLE__CREATION_TIME, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Edit Time feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addEditTimePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Authorable_editTime_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Authorable_editTime_feature", "_UI_Authorable_type"),
        ReviewsPackage.Literals.AUTHORABLE__EDIT_TIME, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Parent Heading feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addParentHeadingPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_parentHeading_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_parentHeading_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__PARENT_HEADING, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Previous Heading feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addPreviousHeadingPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_previousHeading_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_previousHeading_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__PREVIOUS_HEADING, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Next Heading feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addNextHeadingPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_nextHeading_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_nextHeading_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__NEXT_HEADING, false, false, false, null, null, null));
  }

  /**
   * This adds a property descriptor for the Outline Number feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addOutlineNumberPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_outlineNumber_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_outlineNumber_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__OUTLINE_NUMBER, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
  }

  /**
   * This adds a property descriptor for the Parent Index feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addParentIndexPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_Topic_parentIndex_feature"), getString("_UI_PropertyDescriptor_description", "_UI_Topic_parentIndex_feature", "_UI_Topic_type"),
        ReviewsPackage.Literals.TOPIC__PARENT_INDEX, false, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
  }

  /**
   * This returns Topic.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object)
  {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Topic"));
  }

  @Override
  protected Object overlayImage(Object object, Object image)
  {
    image = super.overlayImage(object, image);
    return TopicContainerItemProvider.overlayTopicContainerImage((Topic)object, image);
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
    Topic topic = (Topic)object;
    StyledString styledString = new StyledString();

    if (topic.isHeading())
    {
      styledString.append(topic.getOutlineNumber(), StyledString.Style.QUALIFIER_STYLER).append(" ");
    }

    styledString.append(topic.getText());

    Style style = null;
    TopicStatus status = topic.getStatus();

    if (status == TopicStatus.UNRESOLVED)
    {
      style = STYLE_UNRESOLVED;
    }
    else if (status == TopicStatus.RESOLVED)
    {
      style = STYLE_RESOLVED;
    }

    if (style != null)
    {
      getPropertyDescriptors(topic); // Ensure that the descriptors are initialized.

      String statusLabel = statusPropertyDescriptor.getLabelProvider(topic).getText(status);
      styledString.append("  ").append("[" + statusLabel + "]", style);
    }

    return styledString;
  }

  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * @since 1.1
   * @deprecated Only here to show the unmodified code created by the generator in contrast to the
   * hand-modified {@link #notifyChanged(Notification)}. <b>Do not attempt to call notifyChangedGen()
   * from notifyChanged() as that would lead to StackOverflowError between TopicContainerItemProvider
   * and TopicItemProvider!</b>
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  public void notifyChangedGen(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(Topic.class))
    {
    case ReviewsPackage.TOPIC__ID:
    case ReviewsPackage.TOPIC__TEXT:
    case ReviewsPackage.TOPIC__AUTHOR:
    case ReviewsPackage.TOPIC__CREATION_TIME:
    case ReviewsPackage.TOPIC__EDIT_TIME:
    case ReviewsPackage.TOPIC__HEADING:
    case ReviewsPackage.TOPIC__MODEL_REFERENCE:
    case ReviewsPackage.TOPIC__STATUS:
    case ReviewsPackage.TOPIC__OUTLINE_NUMBER:
    case ReviewsPackage.TOPIC__PARENT_INDEX:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    }
    super.notifyChanged(notification);
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    updateChildren(notification);

    switch (notification.getFeatureID(Topic.class))
    {
    case ReviewsPackage.TOPIC__TEXT:
    case ReviewsPackage.TOPIC__HEADING:
    case ReviewsPackage.TOPIC__MODEL_REFERENCE:
    case ReviewsPackage.TOPIC__AUTHOR:
    case ReviewsPackage.TOPIC__OUTLINE_NUMBER:
    case ReviewsPackage.TOPIC__PARENT_INDEX:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      return;
    case ReviewsPackage.TOPIC__STATUS:
      fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
      TopicContainerItemProvider.propagateNotification(this, notification, notification.getNotifier());
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
