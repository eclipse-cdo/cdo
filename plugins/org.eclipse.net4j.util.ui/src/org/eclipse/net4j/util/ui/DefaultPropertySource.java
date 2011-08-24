/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class DefaultPropertySource<RECEIVER> implements IPropertySource
{
  private List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();

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

  public PropertyDescriptor addDescriptor(String category, Object id, String displayName, String description)
  {
    PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
    descriptor.setCategory(category);
    descriptor.setDescription(description);

    descriptors.add(descriptor);
    return descriptor;
  }

  public void addDescriptors(IPropertyProvider<RECEIVER> provider)
  {
    for (Property<RECEIVER> property : provider.getProperties())
    {
      if (property.getLabel() != null)
      {
        descriptors.add(new DelegatingPropertyDescriptor<RECEIVER>(property));
      }
    }
  }

  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
  }

  public IPropertyDescriptor getPropertyDescriptor(Object id)
  {
    IPropertyDescriptor[] propertyDescriptors = getPropertyDescriptors();
    for (int i = 0; i < propertyDescriptors.length; i++)
    {
      IPropertyDescriptor propertyDescriptor = propertyDescriptors[i];
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

  public Object getPropertyValue(Object id)
  {
    Property<RECEIVER> property = getProperty(id);
    if (property != null)
    {
      return property.getValue(receiver);
    }

    return null;
  }

  public boolean isPropertySet(Object id)
  {
    return true;
  }

  public void resetPropertyValue(Object id)
  {
  }

  public void setPropertyValue(Object id, Object value)
  {
  }

  public Object getEditableValue()
  {
    return null;
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

    public String getCategory()
    {
      return property.getCategory();
    }

    public String getId()
    {
      return property.getName();
    }

    public String getDisplayName()
    {
      return property.getLabel();
    }

    public String getDescription()
    {
      return property.getDescription();
    }

    public boolean isCompatibleWith(IPropertyDescriptor anotherProperty)
    {
      return anotherProperty.getCategory().equals(getCategory()) && anotherProperty.getId().equals(getId());
    }

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

    public Object getHelpContextIds()
    {
      return null;
    }

    public String[] getFilterFlags()
    {
      return null;
    }

    public CellEditor createPropertyEditor(Composite parent)
    {
      return null;
    }
  }
}
