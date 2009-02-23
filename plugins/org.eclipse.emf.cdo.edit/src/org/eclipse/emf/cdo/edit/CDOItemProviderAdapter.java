/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.edit;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

import java.util.Collection;
import java.util.List;

/**
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
   * @since 2.0
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
  {
    if (itemPropertyDescriptors == null)
    {
      super.getPropertyDescriptors(object);
      addIDDescriptor(object);
      addVersionDescriptor(object);
      addStateDescriptor(object);
      addViewDescriptor(object);
      addContainerDescriptor(object);
      addDirectResourceDescriptor(object);
      addReadLockedDescriptor(object);
      addWriteLockedDescriptor(object);
      addAdaptersDescriptor(object);
      addChangeSubscriptionPoliciesDescriptor(object);
    }

    return itemPropertyDescriptors;
  }

  /**
   * @since 2.0
   */
  protected void addIDDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new IDDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addVersionDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new VersionDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addStateDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new StateDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addViewDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ViewDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addContainerDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ContainerDescriptor(adapterFactory));
  }

  /**
   * @since 2.0
   */
  protected void addDirectResourceDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new DirectResourceDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addReadLockedDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ReadLockedDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addWriteLockedDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new WriteLockedDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addAdaptersDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new AdaptersDescriptor());
  }

  /**
   * @since 2.0
   */
  protected void addChangeSubscriptionPoliciesDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new ChangeSubscriptionPoliciesDescriptor());
  }

  /**
   * Base abstract to add CDO debug information to PropertiesView. Users wanting to add new information to the
   * Properties View can subclass this class instead of directly implementing {@link IItemPropertyDescriptor}.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static abstract class CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    /**
     * The filter id for expert properties
     */
    public static final String[] FILTER_ID_EXPERT = { "org.eclipse.ui.views.properties.expert" }; //$NON-NLS-1$

    /**
     * PropertiesView category for CDO
     */
    public static final String CDO_CATEGORY = "CDO";

    public CDOPropertyDescriptor()
    {
    }

    public boolean canSetProperty(Object object)
    {
      return false;
    }

    public void setPropertyValue(Object object, Object value)
    {
    }

    public void resetPropertyValue(Object object)
    {
    }

    public boolean isPropertySet(Object object)
    {
      return false;
    }

    public String getCategory(Object object)
    {
      return CDO_CATEGORY;
    }

    public Collection<?> getChoiceOfValues(Object object)
    {
      return null;
    }

    public Object getHelpContextIds(Object object)
    {
      return null;
    }

    public boolean isCompatibleWith(Object object, Object anotherObject,
        IItemPropertyDescriptor anotherPropertyDescriptor)
    {
      return false;
    }

    public boolean isMany(Object object)
    {
      return false;
    }

    public boolean isMultiLine(Object object)
    {
      return false;
    }

    public boolean isSortChoices(Object object)
    {
      return false;
    }

    public String[] getFilterFlags(Object object)
    {
      return FILTER_ID_EXPERT;
    }

    public IItemLabelProvider getLabelProvider(Object object)
    {
      return new DefaultLabelProvider();
    }

    /**
     * @author Eike Stepper
     */
    public static class DefaultLabelProvider implements IItemLabelProvider
    {
      public DefaultLabelProvider()
      {
      }

      public Object getImage(Object object)
      {
        return null;
      }

      public String getText(Object object)
      {
        return object.toString();
      }
    }
  }

  /**
   * Adds the {@link CDOID} of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class IDDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "ID";

    private static final String DISPLAY_NAME = "ID";

    private static final String DESCRIPTION = "The CDOID uniquely identifies this object in the repository";

    public IDDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      return ((CDOObject)object).cdoID();
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }
  }

  /**
   * Adds the version of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class VersionDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "Version";

    private static final String DISPLAY_NAME = "Version";

    private static final String DESCRIPTION = "The version of this object";

    public VersionDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return ((CDOObject)object).cdoRevision();
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      return new DefaultLabelProvider()
      {
        @Override
        public String getText(Object object)
        {
          if (object instanceof CDORevision)
          {
            return String.valueOf(((CDORevision)object).getVersion());
          }

          return null;
        }
      };
    }
  }

  /**
   * Adds the {@link CDOState state} of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class StateDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "State";

    private static final String DISPLAY_NAME = "State";

    private static final String DESCRIPTION = "The local state of this object";

    public StateDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return ((CDOObject)object).cdoState();
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }
  }

  /**
   * Adds the associate {@link CDOView view} of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class ViewDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "View";

    private static final String DISPLAY_NAME = "View";

    private static final String DESCRIPTION = "The view of this object";

    public ViewDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return ((CDOObject)object).cdoView();
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }
  }

  /**
   * Adds the {@link EObject#eContainer() eContainer} of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class ContainerDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "Container";

    private static final String DISPLAY_NAME = "Container";

    private static final String DESCRIPTION = "The container of this object";

    private static AdapterFactory adapterFactory;

    public ContainerDescriptor(AdapterFactory adapterFactory)
    {
      ContainerDescriptor.adapterFactory = adapterFactory;
    }

    public Object getPropertyValue(Object object)
    {
      return ((CDOObject)object).eContainer();
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      // Needs a wrapped delegator to IItemLabelProvider since eContainer might be null for top level CDOObjects
      // and we can't return a null IItemLabelProvider
      return new IItemLabelProvider()
      {
        public Object getImage(Object object)
        {
          if (object != null)
          {
            return ((IItemLabelProvider)ContainerDescriptor.adapterFactory.adapt(object, IItemLabelProvider.class))
                .getImage(object);
          }

          return null;
        }

        public String getText(Object object)
        {
          if ((CDOObject)object != null)
          {
            return ((IItemLabelProvider)ContainerDescriptor.adapterFactory.adapt(object, IItemLabelProvider.class))
                .getText(object);
          }

          return null;
        }
      };
    }
  }

  /**
   * Adds the {@link InternalEObject#eDirectResource() direct resource} of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class DirectResourceDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "DirectResource";

    private static final String DISPLAY_NAME = "Direct Resource";

    private static final String DESCRIPTION = "The direct resource of this object";

    public DirectResourceDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof InternalEObject)
      {
        return ((InternalEObject)object).eDirectResource();
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      return new DefaultLabelProvider()
      {
        @Override
        public String getText(Object object)
        {
          if (object instanceof CDOResource)
          {
            return ((Resource)object).getURI().toString();
          }

          return null;
        }
      };
    }
  }

  /**
   * Adds the {@link CDOLock read lock} of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class ReadLockedDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "ReadLocked";

    private static final String DISPLAY_NAME = "Read Locked";

    private static final String DESCRIPTION = "Shows if this object is read-locked";

    public ReadLockedDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return ((CDOObject)object).cdoReadLock();
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      return new DefaultLabelProvider()
      {
        @Override
        public String getText(Object object)
        {
          if (object instanceof CDOLock)
          {
            return String.valueOf(((CDOLock)object).isLocked());
          }

          return null;
        }
      };
    }
  }

  /**
   * Adds the {@link CDOLock write lock} of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class WriteLockedDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "WriteLocked";

    private static final String DISPLAY_NAME = "Write Locked";

    private static final String DESCRIPTION = "Shows if this object is write-locked";

    public WriteLockedDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return ((CDOObject)object).cdoWriteLock();
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      return new DefaultLabelProvider()
      {
        @Override
        public String getText(Object object)
        {
          if (object instanceof CDOLock)
          {
            return String.valueOf(((CDOLock)object).isLocked());
          }

          return null;
        }
      };
    }
  }

  /**
   * Adds the {@link EObject#eAdapters() eAdapters} list of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class AdaptersDescriptor extends CDOPropertyDescriptor implements IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "Adapters";

    private static final String DISPLAY_NAME = "Adapters";

    private static final String DESCRIPTION = "The list of adapters attached to this object";

    public AdaptersDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return ((CDOObject)object).eAdapters();
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }
  }

  /**
   * Adds the change subscription policies list of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class ChangeSubscriptionPoliciesDescriptor extends CDOPropertyDescriptor implements
      IItemPropertyDescriptor
  {
    private static final String FEATURE_ID = "ChangeSubscriptionPolicies";

    private static final String DISPLAY_NAME = "Change Subscription Policies";

    private static final String DESCRIPTION = "The change subscription policies associated with the underlying view of this object";

    public ChangeSubscriptionPoliciesDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        CDOView cdoView = ((CDOObject)object).cdoView();
        if (cdoView != null)
        {
          return cdoView.options().getChangeSubscriptionPolicies();
        }
      }

      return null;
    }

    public String getDescription(Object object)
    {
      return DESCRIPTION;
    }

    public String getDisplayName(Object object)
    {
      return DISPLAY_NAME;
    }

    public Object getFeature(Object object)
    {
      return FEATURE_ID;
    }

    public String getId(Object object)
    {
      return FEATURE_ID;
    }

    @Override
    public IItemLabelProvider getLabelProvider(Object object)
    {
      return new DefaultLabelProvider()
      {
        @Override
        public String getText(Object object)
        {
          if (object instanceof CDOAdapterPolicy[])
          {
            StringBuilder builder = new StringBuilder();
            CDOAdapterPolicy[] policies = (CDOAdapterPolicy[])object;
            for (CDOAdapterPolicy policy : policies)
            {
              builder.append(policy.toString());
              builder.append(", ");
            }

            return builder.toString();
          }

          return null;
        }
      };
    }
  }
}
