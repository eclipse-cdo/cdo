/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.provider;

import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the item provider adapter for a {@link org.eclipse.emf.cdo.releng.setup.SetupTask} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class SetupTaskItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
    IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
  private static final Map<Set<Trigger>, IItemLabelProvider> EXCLUSION_LABEL_PROVIDERS = new HashMap<Set<Trigger>, IItemLabelProvider>();

  static
  {
    for (Set<Trigger> validTriggers : Trigger.LITERALS.keySet())
    {
      final Map<Set<Trigger>, String> exclusionLabels = new HashMap<Set<Trigger>, String>();

      for (Set<Trigger> triggers : Trigger.LITERALS.keySet())
      {
        Set<Trigger> compliment = new LinkedHashSet<Trigger>(validTriggers);
        compliment.removeAll(triggers);
        exclusionLabels.put(triggers, Trigger.LITERALS.get(compliment));
      }

      EXCLUSION_LABEL_PROVIDERS.put(validTriggers, new IItemLabelProvider()
      {
        public String getText(Object object)
        {
          return exclusionLabels.get(object);
        }

        public Object getImage(Object object)
        {
          return null;
        }
      });
    }
  }

  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupTaskItemProvider(AdapterFactory adapterFactory)
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

      addRequirementsPropertyDescriptor(object);
      addRestrictionsPropertyDescriptor(object);
      addScopePropertyDescriptor(object);
      addExcludedTriggersPropertyDescriptor(object);
      addDocumentationPropertyDescriptor(object);
      addDisabledPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Requirements feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addRequirementsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory)
        .getRootAdapterFactory(), getResourceLocator(), getString("_UI_SetupTask_requirements_feature"), getString(
        "_UI_PropertyDescriptor_description", "_UI_SetupTask_requirements_feature", "_UI_SetupTask_type"),
        SetupPackage.Literals.SETUP_TASK__REQUIREMENTS, true, false, true, null, null, null)
    {
      private IItemLabelProvider labelProvider = new HierarchicalItemLabelProvider(itemDelegator);

      @Override
      public IItemLabelProvider getLabelProvider(Object object)
      {
        return labelProvider;
      }

      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        SetupTask setupTask = (SetupTask)object;
        Collection<?> result = new ArrayList<Object>(super.getChoiceOfValues(object));
        for (Iterator<?> it = result.iterator(); it.hasNext();)
        {
          Object value = it.next();
          if (value instanceof SetupTask && ((SetupTask)value).requires(setupTask))
          {
            // Remove items that would cause a circularity.
            it.remove();
          }
        }
        return result;
      }

    });
  }

  /**
   * This adds a property descriptor for the Restrictions feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addRestrictionsPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory)
        .getRootAdapterFactory(), getResourceLocator(), getString("_UI_SetupTask_restrictions_feature"), getString(
        "_UI_PropertyDescriptor_description", "_UI_SetupTask_restrictions_feature", "_UI_SetupTask_type"),
        SetupPackage.Literals.SETUP_TASK__RESTRICTIONS, true, false, true, null, null, null)
    {
      private IItemLabelProvider labelProvider = new HierarchicalItemLabelProvider(itemDelegator);

      @Override
      public IItemLabelProvider getLabelProvider(Object object)
      {
        return labelProvider;
      }
    });
  }

  /**
   * This adds a property descriptor for the Scope feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addScopePropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_SetupTask_scope_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_SetupTask_scope_feature", "_UI_SetupTask_type"),
        SetupPackage.Literals.SETUP_TASK__SCOPE, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null,
        null));
  }

  /**
   * This adds a property descriptor for the Excluded Triggers feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected void addExcludedTriggersPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory)
        .getRootAdapterFactory(), getResourceLocator(), getString("_UI_SetupTask_excludedTriggers_feature"), getString(
        "_UI_PropertyDescriptor_description", "_UI_SetupTask_excludedTriggers_feature", "_UI_SetupTask_type"),
        SetupPackage.Literals.SETUP_TASK__EXCLUDED_TRIGGERS, true, false, false,
        ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null)
    {
      @Override
      public IItemLabelProvider getLabelProvider(Object object)
      {
        final SetupTask setupTask = (SetupTask)object;
        return EXCLUSION_LABEL_PROVIDERS.get(setupTask.getValidTriggers());
      }

      @Override
      public String getDisplayName(Object object)
      {
        return "Triggers";
      }

      @Override
      public String getDescription(Object object)
      {
        return "The triggers for which the task is applicable";
      }

      @Override
      public Collection<?> getChoiceOfValues(Object object)
      {
        SetupTask setupTask = (SetupTask)object;
        Set<Trigger> validTriggers = setupTask.getValidTriggers();
        List<Set<Trigger>> result = new ArrayList<Set<Trigger>>(Trigger.LITERALS.keySet());
        for (Iterator<Set<Trigger>> it = result.iterator(); it.hasNext();)
        {
          if (!validTriggers.containsAll(it.next()))
          {
            it.remove();
          }
        }
        Collections.reverse(result);
        return result;
      }
    });
  }

  /**
   * This adds a property descriptor for the Documentation feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addDocumentationPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_SetupTask_documentation_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_SetupTask_documentation_feature", "_UI_SetupTask_type"),
        SetupPackage.Literals.SETUP_TASK__DOCUMENTATION, true, true, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
        null, null));
  }

  /**
   * This adds a property descriptor for the Disabled feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addDisabledPropertyDescriptor(Object object)
  {
    itemPropertyDescriptors.add(createItemPropertyDescriptor(
        ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(),
        getString("_UI_SetupTask_disabled_feature"),
        getString("_UI_PropertyDescriptor_description", "_UI_SetupTask_disabled_feature", "_UI_SetupTask_type"),
        SetupPackage.Literals.SETUP_TASK__DISABLED, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
        null, null));
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
    SetupTaskScope labelValue = ((SetupTask)object).getScope();
    String label = labelValue == null ? null : labelValue.toString();
    return label == null || label.length() == 0 ? getString("_UI_SetupTask_type") : getString("_UI_SetupTask_type")
        + " " + label;
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

    switch (notification.getFeatureID(SetupTask.class))
    {
    case SetupPackage.SETUP_TASK__SCOPE:
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
    case SetupPackage.SETUP_TASK__DISABLED:
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

  /**
   * @author Eike Stepper
   */
  private static class HierarchicalItemLabelProvider implements IItemLabelProvider
  {
    private AdapterFactoryItemDelegator itemDelegator;

    public HierarchicalItemLabelProvider(AdapterFactoryItemDelegator itemDelegator)
    {
      this.itemDelegator = itemDelegator;
    }

    public String getText(Object object)
    {
      StringBuilder builder = new StringBuilder(itemDelegator.getText(object));
      if (!(object instanceof Collection<?>))
      {
        int index = builder.length();
        object = itemDelegator.getParent(object);
        while (object != null && !(object instanceof Configuration) && !(object instanceof Resource))
        {
          if (builder.length() == index)
          {
            builder.insert(index, " (");
            index += 2;
          }
          else
          {
            builder.insert(index, " - ");
          }

          String text = itemDelegator.getText(object);
          builder.insert(index, text);

          object = itemDelegator.getParent(object);
        }

        if (builder.length() != index)
        {
          builder.append(")");
        }
      }

      return builder.toString();
    }

    public Object getImage(Object object)
    {
      return itemDelegator.getImage(object);
    }
  }
}
