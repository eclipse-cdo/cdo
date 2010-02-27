package org.eclipse.emf.cdo.internal.ui.adapters;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CDOPropertySource<OBJECT> implements IPropertySource
{
  //  private static final IPropertyDescriptor[] descriptors = { new PropertyDescriptor("sessionID", "Session ID") }; //$NON-NLS-1$

  private List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();

  private OBJECT object;

  public CDOPropertySource(OBJECT object)
  {
    this.object = object;
  }

  public OBJECT getObject()
  {
    return object;
  }

  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
  }

  public PropertyDescriptor addPropertyDescriptor(String category, Object id, String displayName, String description)
  {
    PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
    descriptor.setCategory(category);
    descriptor.setDescription(description);

    descriptors.add(descriptor);
    return descriptor;
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
}
