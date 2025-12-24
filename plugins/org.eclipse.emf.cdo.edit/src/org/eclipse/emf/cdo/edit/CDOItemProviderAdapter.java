/*
 * Copyright (c) 2007, 2009-2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.edit;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.edit.messages.Messages;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.object.ObjectProperties;

import org.eclipse.net4j.util.properties.Property;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

import java.util.Collection;
import java.util.List;

/**
 * Adds CDO-specific properties to {@link EObject objects}, such as {@link CDOObject#cdoID() ID},
 * {@link CDORevision#getVersion() version} and {@link CDOObject#cdoState() state}.
 *
 * @author Eike Stepper
 */
public class CDOItemProviderAdapter extends ItemProviderAdapter
{
  public CDOItemProviderAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean hasChildren(Object object)
  {
    Collection<? extends EStructuralFeature> anyChildrenFeatures = getChildrenFeatures(object);
    if (anyChildrenFeatures.isEmpty())
    {
      anyChildrenFeatures = getChildrenReferences(object);
    }

    EObject eObject = (EObject)object;
    for (EStructuralFeature feature : anyChildrenFeatures)
    {
      if (feature.isMany())
      {
        List<?> children = (List<?>)eObject.eGet(feature);
        if (!children.isEmpty())
        {
          return true;
        }
      }
      else
      {
        if (eObject.eIsSet(feature))
        {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @ADDED
   */
  @Override
  public Object getParent(Object object)
  {
    return CDOElement.getParentOf((EObject)object);
  }

  /**
   * @since 2.0
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);

      if (object instanceof EObject)
      {
        EObject eObject = (EObject)object;
        for (Property<EObject> property : ObjectProperties.INSTANCE.getProperties())
        {
          if (property.getLabel() != null)
          {
            addCDOPropertyDescriptor(eObject, property);
          }
        }
      }
    }

    return itemPropertyDescriptors;
  }

  /**
   * @since 4.4
   */
  protected void addCDOPropertyDescriptor(EObject object, Property<EObject> property)
  {
    itemPropertyDescriptors.add(new CDOPropertyDescriptor(property));
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addIDDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addVersionDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addStateDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addViewDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addContainerDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addDirectResourceDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addReadLockedDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addWriteLockedDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 4.3
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addWriteOptionDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addAdaptersDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addChangeSubscriptionPoliciesDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  protected void addURIDescriptor(Object object)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Base abstract to add CDO debug information to PropertiesView. Users wanting to add new information to the
   * Properties view can subclass this class instead of directly implementing {@link IItemPropertyDescriptor}.
   *
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class CDOPropertyDescriptor implements IItemPropertyDescriptor, IItemLabelProvider
  {
    /**
     * The filter id for expert properties
     */
    public static final String[] FILTER_ID_EXPERT = { "org.eclipse.ui.views.properties.expert" }; //$NON-NLS-1$

    /**
     * PropertiesView category for CDO
     */
    public static final String CDO_CATEGORY = Messages.getString("CDOItemProviderAdapter.0"); //$NON-NLS-1$

    private final Property<EObject> property;

    /**
     * @deprecated As of 4.4. use {@link #CDOPropertyDescriptor(Property)}.
     */
    @Deprecated
    public CDOPropertyDescriptor()
    {
      throw new UnsupportedOperationException();
    }

    /**
     * @since 4.4
     */
    public CDOPropertyDescriptor(Property<EObject> property)
    {
      this.property = property;
    }

    /**
     * @since 4.4
     */
    public final Property<EObject> getProperty()
    {
      return property;
    }

    /**
     * @since 4.4
     */
    @Override
    public String getId(Object object)
    {
      return "___CDO___" + property.getName();
    }

    /**
     * @since 4.4
     */
    @Override
    public Object getFeature(Object object)
    {
      return getId(object);
    }

    /**
     * @since 4.4
     */
    @Override
    public String getDisplayName(Object object)
    {
      return property.getLabel();
    }

    /**
     * @since 4.4
     */
    @Override
    public String getDescription(Object object)
    {
      return property.getDescription();
    }

    @Override
    public boolean isPropertySet(Object object)
    {
      return true;
    }

    /**
     * @since 4.4
     */
    @Override
    public Object getPropertyValue(Object object)
    {
      return property.getValue((EObject)object);
    }

    @Override
    public void setPropertyValue(Object object, Object value)
    {
      // Do nothing.
    }

    @Override
    public void resetPropertyValue(Object object)
    {
      // Do nothing.
    }

    @Override
    public boolean canSetProperty(Object object)
    {
      return false;
    }

    @Override
    public String getCategory(Object object)
    {
      return CDO_CATEGORY;
    }

    @Override
    public Collection<?> getChoiceOfValues(Object object)
    {
      return null;
    }

    @Override
    public Object getHelpContextIds(Object object)
    {
      return null;
    }

    @Override
    public boolean isCompatibleWith(Object object, Object anotherObject, IItemPropertyDescriptor anotherPropertyDescriptor)
    {
      return false;
    }

    @Override
    public boolean isMany(Object object)
    {
      return false;
    }

    @Override
    public boolean isMultiLine(Object object)
    {
      return false;
    }

    @Override
    public boolean isSortChoices(Object object)
    {
      return false;
    }

    @Override
    public String[] getFilterFlags(Object object)
    {
      return FILTER_ID_EXPERT;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      return this;
    }

    /**
     * @since 4.4
     */
    @Override
    public Object getImage(Object object)
    {
      return null;
    }

    /**
     * @since 4.4
     */
    @Override
    public String getText(Object object)
    {
      return object.toString();
    }

    /**
     * A default implementation of {@link IItemLabelProvider}.
     *
     * @author Eike Stepper
     * @deprecated As of 4.4 not used any more.
     */
    @Deprecated
    public static class DefaultLabelProvider implements IItemLabelProvider
    {
      @Deprecated
      public DefaultLabelProvider()
      {
      }

      @Deprecated
      @Override
      public Object getImage(Object object)
      {
        return null;
      }

      @Deprecated
      @Override
      public String getText(Object object)
      {
        return object.toString();
      }
    }
  }

  /**
   * Adds the {@link CDOID} of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class IDDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the version of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class VersionDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the {@link CDOState state} of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class StateDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the associate {@link CDOView view} of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class ViewDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the {@link EObject#eContainer() eContainer} of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class ContainerDescriptor extends CDOPropertyDescriptor
  {
    @Deprecated
    public ContainerDescriptor(AdapterFactory adapterFactory)
    {
    }
  }

  /**
   * Adds the {@link InternalEObject#eDirectResource() direct resource} of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class DirectResourceDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the {@link CDOLock read lock} of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class ReadLockedDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the {@link CDOLock write lock} of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class WriteLockedDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the {@link CDOLock write option} of a {@link CDOObject} to the Properties view.
   *
   * @author Eike Stepper
   * @since 4.3
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class WriteOptionDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the {@link EObject#eAdapters() eAdapters} list of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class AdaptersDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the change subscription policies list of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class ChangeSubscriptionPoliciesDescriptor extends CDOPropertyDescriptor
  {
  }

  /**
   * Adds the URI of a {@link CDOObject} to the Properties view.
   *
   * @author Victor Roldan Betancort
   * @since 2.0
   * @deprecated As of 4.4 not used any more.
   */
  @Deprecated
  public static class URIDescriptor extends CDOPropertyDescriptor
  {
  }
}
