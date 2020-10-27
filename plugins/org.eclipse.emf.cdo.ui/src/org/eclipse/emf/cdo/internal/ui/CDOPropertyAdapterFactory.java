/*
 * Copyright (c) 2011-2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.edit.CDOItemProviderAdapter.CDOPropertyDescriptor;
import org.eclipse.emf.cdo.internal.common.RepositoryProperties;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.object.ObjectProperties;
import org.eclipse.emf.internal.cdo.session.SessionProperties;
import org.eclipse.emf.internal.cdo.view.ViewProperties;

import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.AbstractPropertyAdapterFactory;
import org.eclipse.net4j.util.ui.DefaultActionFilter;
import org.eclipse.net4j.util.ui.DefaultPropertySource;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOPropertyAdapterFactory extends AbstractPropertyAdapterFactory
{
  private static final IActionFilter REPOSITORY_ACTION_FILTER = new DefaultActionFilter<>(RepositoryProperties.INSTANCE);

  private static final IActionFilter SESSION_ACTION_FILTER = new DefaultActionFilter<>(SessionProperties.INSTANCE);

  private static final IActionFilter VIEW_ACTION_FILTER = new DefaultActionFilter<>(ViewProperties.INSTANCE);

  private static final IActionFilter OBJECT_ACTION_FILTER = new DefaultActionFilter<>(ObjectProperties.INSTANCE);

  public CDOPropertyAdapterFactory()
  {
  }

  @Override
  protected IPropertySource createPropertySource(Object object)
  {
    try
    {
      if (object instanceof CDOCommonRepository)
      {
        return new DefaultPropertySource<>((CDOCommonRepository)object, RepositoryProperties.INSTANCE);
      }

      if (object instanceof CDOSession)
      {
        return new DefaultPropertySource<>((CDOSession)object, SessionProperties.INSTANCE);
      }

      if (object instanceof CDOView)
      {
        return new DefaultPropertySource<>((CDOView)object, ViewProperties.INSTANCE);
      }

      if (object instanceof CDOElement)
      {
        object = ((CDOElement)object).getDelegate();
      }

      if (object instanceof EObject)
      {
        EObject eObject = (EObject)object;
        InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(eObject, false);
        if (cdoObject != null && !cdoObject.cdoInvalid())
        {
          InternalCDOView view = cdoObject.cdoView();
          if (view != null && view.isActive())
          {
            Map<String, Object> emfProperties = new HashMap<>();
            DefaultPropertySource<EObject> result = new DefaultPropertySource<EObject>(cdoObject, ObjectProperties.INSTANCE)
            {
              @Override
              public Object getPropertyValue(Object id)
              {
                try
                {
                  Object value = emfProperties.get(id);
                  if (value != null)
                  {
                    return value;
                  }

                  return super.getPropertyValue(id);
                }
                catch (Throwable ex)
                {
                  return null;
                }
              }
            };

            ComposedAdapterFactory adapterFactory = null;
            AdapterFactoryLabelProvider labelProvider = null;

            try
            {
              adapterFactory = CDOEditor.createAdapterFactory(false);
              IItemPropertySource propertySource = null;

              try
              {
                propertySource = (IItemPropertySource)adapterFactory.adapt(cdoObject.cdoInternalInstance(), IItemPropertySource.class);
              }
              catch (Exception ex)
              {
                //$FALL-THROUGH$
              }

              if (propertySource != null)
              {
                List<IItemPropertyDescriptor> propertyDescriptors = propertySource.getPropertyDescriptors(cdoObject);
                if (propertyDescriptors != null)
                {
                  labelProvider = new AdapterFactoryLabelProvider(adapterFactory);

                  for (IItemPropertyDescriptor propertyDescriptor : propertyDescriptors)
                  {
                    if (propertyDescriptor instanceof CDOPropertyDescriptor)
                    {
                      continue;
                    }

                    String category = getTypeText(adapterFactory, cdoObject);
                    String id = "___EMF___" + propertyDescriptor.getId(cdoObject);
                    String displayName = propertyDescriptor.getDisplayName(cdoObject);
                    String description = propertyDescriptor.getDescription(cdoObject);

                    PropertyDescriptor descriptor = result.addDescriptor(category, id, displayName, description);

                    Object value = propertyDescriptor.getPropertyValue(cdoObject);
                    emfProperties.put(id, value);

                    String text = labelProvider.getText(value);
                    descriptor.setLabelProvider(new LabelProvider()
                    {
                      @Override
                      public String getText(Object element)
                      {
                        return text;
                      }
                    });
                  }
                }
              }
            }
            finally
            {
              if (labelProvider != null)
              {
                labelProvider.dispose();
              }

              if (adapterFactory != null)
              {
                adapterFactory.dispose();
              }
            }

            return result;
          }
        }
      }
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }
    catch (Throwable ex)
    {
      if (LifecycleUtil.isActive(object))
      {
        OM.LOG.error(ex);
      }
    }

    return null;
  }

  @Override
  protected IActionFilter createActionFilter(Object object)
  {
    if (object instanceof CDOCommonRepository)
    {
      return REPOSITORY_ACTION_FILTER;
    }

    if (object instanceof CDOSession)
    {
      return SESSION_ACTION_FILTER;
    }

    if (object instanceof CDOView)
    {
      return VIEW_ACTION_FILTER;
    }

    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      if (CDOUtil.isCDOObject(eObject))
      {
        return OBJECT_ACTION_FILTER;
      }
    }

    return super.createActionFilter(object);
  }

  public static String getTypeText(ComposedAdapterFactory adapterFactory, EObject eObject)
  {
    String typeKey = eObject.eClass().getName();
    List<Adapter> originalAdapters = new ArrayList<>(eObject.eAdapters());

    try
    {
      return getResourceLocator(adapterFactory, eObject).getString("_UI_" + typeKey + "_type");
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }
    finally
    {
      eObject.eAdapters().retainAll(originalAdapters);
    }

    return typeKey;
  }

  private static ResourceLocator getResourceLocator(ComposedAdapterFactory adapterFactory, EObject eObject)
  {
    Object adapter = adapterFactory.getRootAdapterFactory().adapt(eObject, IItemLabelProvider.class);
    if (adapter instanceof ResourceLocator)
    {
      return (ResourceLocator)adapter;
    }

    return EMFEditPlugin.INSTANCE;
  }
}
