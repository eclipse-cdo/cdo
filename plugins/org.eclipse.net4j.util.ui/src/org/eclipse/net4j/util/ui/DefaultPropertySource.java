/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.properties.IPropertyProvider;
import org.eclipse.net4j.util.properties.Property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class DefaultPropertySource<RECEIVER> implements IPropertySource
{
  private Map<Object, IPropertyDescriptor> descriptors = new LinkedHashMap<>();

  private RECEIVER receiver;

  public DefaultPropertySource(RECEIVER receiver)
  {
    this.receiver = receiver;
  }

  public DefaultPropertySource(RECEIVER object, IPropertyProvider<RECEIVER> provider)
  {
    this(object);
    addDescriptors(provider);
  }

  public RECEIVER getReceiver()
  {
    return receiver;
  }

  /**
   * @since 3.5
   */
  public boolean addDescriptor(IPropertyDescriptor descriptor)
  {
    Object id = descriptor.getId();
    if (descriptors.containsKey(id))
    {
      return false;
    }

    descriptors.put(id, descriptor);
    return true;
  }

  public PropertyDescriptor addDescriptor(String category, Object id, String displayName, String description)
  {
    PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
    descriptor.setCategory(category);
    descriptor.setDescription(description);

    if (!addDescriptor(descriptor))
    {
      return null;
    }

    return descriptor;
  }

  public void addDescriptors(IPropertyProvider<RECEIVER> provider)
  {
    for (Property<RECEIVER> property : provider.getProperties())
    {
      if (property.getLabel() != null)
      {
        DelegatingPropertyDescriptor<RECEIVER> descriptor = new DelegatingPropertyDescriptor<>(property);
        addDescriptor(descriptor);
      }
    }
  }

  @Override
  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    return descriptors.values().toArray(new IPropertyDescriptor[descriptors.size()]);
  }

  public IPropertyDescriptor getPropertyDescriptor(Object id)
  {
    for (IPropertyDescriptor propertyDescriptor : descriptors.values())
    {
      if (propertyDescriptor.getId().equals(id))
      {
        return propertyDescriptor;
      }
    }

    return null;
  }

  public Property<RECEIVER> getProperty(Object id)
  {
    IPropertyDescriptor propertyDescriptor = getPropertyDescriptor(id);
    if (propertyDescriptor instanceof DelegatingPropertyDescriptor)
    {
      @SuppressWarnings("unchecked")
      DelegatingPropertyDescriptor<RECEIVER> delegating = (DelegatingPropertyDescriptor<RECEIVER>)propertyDescriptor;
      return delegating.getProperty();
    }

    return null;
  }

  @Override
  public Object getPropertyValue(Object id)
  {
    Property<RECEIVER> property = getProperty(id);
    if (property != null)
    {
      return property.getValue(receiver);
    }

    return null;
  }

  @Override
  public boolean isPropertySet(Object id)
  {
    return true;
  }

  @Override
  public void resetPropertyValue(Object id)
  {
  }

  @Override
  public void setPropertyValue(Object id, Object value)
  {
  }

  @Override
  public Object getEditableValue()
  {
    return null;
  }

  /**
   * @author Eike Stepper
   * @since 3.5
   */
  public static abstract class Augmented<RECEIVER, AUGMENTING_RECEIVER> extends DefaultPropertySource<RECEIVER>
  {
    private final Map<String, Object> propertyValues = new HashMap<>();

    public Augmented(RECEIVER object, IPropertyProvider<RECEIVER> provider, AUGMENTING_RECEIVER augmentingObject)
    {
      super(object, provider);
      if (augmentingObject != null)
      {
        IPropertySource augmentingPropertySource = createAugmentingPropertySource(augmentingObject);
        for (IPropertyDescriptor propertyDescriptor : augmentingPropertySource.getPropertyDescriptors())
        {
          if (propertyDescriptor instanceof DelegatingPropertyDescriptor)
          {
            @SuppressWarnings("unchecked")
            DelegatingPropertyDescriptor<AUGMENTING_RECEIVER> viewPropertyDescriptor = (DelegatingPropertyDescriptor<AUGMENTING_RECEIVER>)propertyDescriptor;

            Property<AUGMENTING_RECEIVER> property = viewPropertyDescriptor.getProperty();
            if (property != null)
            {
              String category = propertyDescriptor.getCategory();
              String id = "___VIEW___" + propertyDescriptor.getId();
              String displayName = propertyDescriptor.getDisplayName();
              String description = propertyDescriptor.getDescription();

              Object value = property.getValue(augmentingObject);
              propertyValues.put(id, value);

              addDescriptor(category, id, displayName, description);
            }
          }
        }
      }
    }

    @Override
    public Object getPropertyValue(Object id)
    {
      Object value = propertyValues.get(id);
      if (value != null)
      {
        return value;
      }

      return super.getPropertyValue(id);
    }

    protected abstract IPropertySource createAugmentingPropertySource(AUGMENTING_RECEIVER augmentingObject);
  }

  /**
   * @author Eike Stepper
   */
  public static final class DelegatingPropertyDescriptor<RECEIVER> implements IPropertyDescriptor
  {
    private final Property<RECEIVER> property;

    private ILabelProvider labelProvider;

    public DelegatingPropertyDescriptor(Property<RECEIVER> property)
    {
      this.property = property;
    }

    public Property<RECEIVER> getProperty()
    {
      return property;
    }

    @Override
    public String getCategory()
    {
      return property.getCategory();
    }

    @Override
    public String getId()
    {
      return property.getName();
    }

    @Override
    public String getDisplayName()
    {
      return property.getLabel();
    }

    @Override
    public String getDescription()
    {
      return property.getDescription();
    }

    @Override
    public boolean isCompatibleWith(IPropertyDescriptor anotherProperty)
    {
      return anotherProperty.getCategory().equals(getCategory()) && anotherProperty.getId().equals(getId());
    }

    @Override
    public ILabelProvider getLabelProvider()
    {
      if (labelProvider != null)
      {
        return labelProvider;
      }

      return new LabelProvider();
    }

    public void setLabelProvider(ILabelProvider labelProvider)
    {
      this.labelProvider = labelProvider;
    }

    @Override
    public Object getHelpContextIds()
    {
      return null;
    }

    @Override
    public String[] getFilterFlags()
    {
      return null;
    }

    @Override
    public CellEditor createPropertyEditor(Composite parent)
    {
      return null;
    }
  }
}
