/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.provider;

import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.util.LMAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ChildCreationExtenderManager;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IItemStyledLabelProvider;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * @generated
 */
public class LMItemProviderAdapterFactory extends LMAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable, IChildCreationExtender
{
  /**
   * This keeps track of the root adapter factory that delegates to this adapter factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ComposedAdapterFactory parentAdapterFactory;

  /**
   * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected IChangeNotifier changeNotifier = new ChangeNotifier();

  /**
   * This helps manage the child creation extenders.
   * <!-- begin-user-doc -->
   * @since 1.1
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChildCreationExtenderManager childCreationExtenderManager = new ChildCreationExtenderManager(LMEditPlugin.INSTANCE, LMPackage.eNS_URI);

  /**
   * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  protected Collection<Object> supportedTypes = new ArrayList<>();

  /**
   * This constructs an instance.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public LMItemProviderAdapterFactory()
  {
    supportedTypes.add(IEditingDomainItemProvider.class);
    supportedTypes.add(IStructuredItemContentProvider.class);
    supportedTypes.add(ITreeItemContentProvider.class);
    supportedTypes.add(IItemLabelProvider.class);
    supportedTypes.add(IItemPropertySource.class);
    supportedTypes.add(IItemStyledLabelProvider.class);
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.System} instances.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected SystemItemProvider systemItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.System}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createSystemAdapter()
  {
    if (systemItemProvider == null)
    {
      systemItemProvider = new SystemItemProvider(this);
    }

    return systemItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.Process} instances.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected ProcessItemProvider processItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.Process}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createProcessAdapter()
  {
    if (processItemProvider == null)
    {
      processItemProvider = new ProcessItemProvider(this);
    }

    return processItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.DropType} instances.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected DropTypeItemProvider dropTypeItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.DropType}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createDropTypeAdapter()
  {
    if (dropTypeItemProvider == null)
    {
      dropTypeItemProvider = new DropTypeItemProvider(this);
    }

    return dropTypeItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.Module} instances.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected ModuleItemProvider moduleItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.Module}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createModuleAdapter()
  {
    if (moduleItemProvider == null)
    {
      moduleItemProvider = new ModuleItemProvider(this);
    }

    return moduleItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.emf.cdo.lm.Dependency} instances. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected DependencyItemProvider dependencyItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.Dependency}.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createDependencyAdapter()
  {
    if (dependencyItemProvider == null)
    {
      dependencyItemProvider = new DependencyItemProvider(this);
    }

    return dependencyItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all
   * {@link org.eclipse.emf.cdo.lm.ModuleType} instances. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ModuleTypeItemProvider moduleTypeItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.ModuleType}.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createModuleTypeAdapter()
  {
    if (moduleTypeItemProvider == null)
    {
      moduleTypeItemProvider = new ModuleTypeItemProvider(this);
    }

    return moduleTypeItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.Stream} instances.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected StreamItemProvider streamItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.Stream}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createStreamAdapter()
  {
    if (streamItemProvider == null)
    {
      streamItemProvider = new StreamItemProvider(this);
    }

    return streamItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.Change} instances.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected ChangeItemProvider changeItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.Change}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createChangeAdapter()
  {
    if (changeItemProvider == null)
    {
      changeItemProvider = new ChangeItemProvider(this);
    }

    return changeItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.Delivery} instances.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  protected DeliveryItemProvider deliveryItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.Delivery}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter createDeliveryAdapter()
  {
    if (deliveryItemProvider == null)
    {
      deliveryItemProvider = new DeliveryItemProvider(this);
    }

    return deliveryItemProvider;
  }

  /**
   * This keeps track of the one adapter used for all {@link org.eclipse.emf.cdo.lm.Drop} instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DropItemProvider dropItemProvider;

  /**
   * This creates an adapter for a {@link org.eclipse.emf.cdo.lm.Drop}. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Adapter createDropAdapter()
  {
    if (dropItemProvider == null)
    {
      dropItemProvider = new DropItemProvider(this);
    }

    return dropItemProvider;
  }

  /**
   * This returns the root adapter factory that contains this factory. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ComposeableAdapterFactory getRootAdapterFactory()
  {
    return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
  }

  /**
   * This sets the composed adapter factory that contains this factory. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory)
  {
    this.parentAdapterFactory = parentAdapterFactory;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object type)
  {
    return supportedTypes.contains(type) || super.isFactoryForType(type);
  }

  /**
   * This implementation substitutes the factory itself as the key for the adapter.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Adapter adapt(Notifier notifier, Object type)
  {
    return super.adapt(notifier, this);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object adapt(Object object, Object type)
  {
    if (isFactoryForType(type))
    {
      Object adapter = super.adapt(object, type);
      if (!(type instanceof Class<?>) || ((Class<?>)type).isInstance(adapter))
      {
        return adapter;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 1.1
   * <!-- end-user-doc -->
   * @generated
   */
  public List<IChildCreationExtender> getChildCreationExtenders()
  {
    return childCreationExtenderManager.getChildCreationExtenders();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain)
  {
    return childCreationExtenderManager.getNewChildDescriptors(object, editingDomain);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return childCreationExtenderManager;
  }

  /**
   * This adds a listener.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void addListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.addListener(notifyChangedListener);
  }

  /**
   * This removes a listener.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void removeListener(INotifyChangedListener notifyChangedListener)
  {
    changeNotifier.removeListener(notifyChangedListener);
  }

  /**
   * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void fireNotifyChanged(Notification notification)
  {
    changeNotifier.fireNotifyChanged(notification);

    if (parentAdapterFactory != null)
    {
      parentAdapterFactory.fireNotifyChanged(notification);
    }
  }

  /**
   * This disposes all of the item providers created by this factory. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void dispose()
  {
    if (systemItemProvider != null)
    {
      systemItemProvider.dispose();
    }
    if (processItemProvider != null)
    {
      processItemProvider.dispose();
    }
    if (moduleTypeItemProvider != null)
    {
      moduleTypeItemProvider.dispose();
    }
    if (dropTypeItemProvider != null)
    {
      dropTypeItemProvider.dispose();
    }
    if (moduleItemProvider != null)
    {
      moduleItemProvider.dispose();
    }
    if (streamItemProvider != null)
    {
      streamItemProvider.dispose();
    }
    if (changeItemProvider != null)
    {
      changeItemProvider.dispose();
    }
    if (deliveryItemProvider != null)
    {
      deliveryItemProvider.dispose();
    }
    if (dropItemProvider != null)
    {
      dropItemProvider.dispose();
    }
    if (dependencyItemProvider != null)
    {
      dependencyItemProvider.dispose();
    }
  }

}
