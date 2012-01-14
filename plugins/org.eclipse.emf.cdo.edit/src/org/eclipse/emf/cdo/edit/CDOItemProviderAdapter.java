/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.internal.edit.messages.Messages;
import org.eclipse.emf.cdo.util.CDOUtil;
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
      addURIDescriptor(object);
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
   * @since 2.0
   */
  protected void addURIDescriptor(Object object)
  {
    itemPropertyDescriptors.add(new URIDescriptor());
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
    public static final String CDO_CATEGORY = Messages.getString("CDOItemProviderAdapter.0"); //$NON-NLS-1$

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
     * A default implementation of {@link IItemLabelProvider}.
     * 
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
  public static class IDDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.1"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.2"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.3"); //$NON-NLS-1$

    public IDDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      return CDOUtil.getCDOObject((EObject)object).cdoID();
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
  public static class VersionDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.4"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.5"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.6"); //$NON-NLS-1$

    public VersionDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return CDOUtil.getCDOObject((EObject)object).cdoRevision();
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
  public static class StateDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.7"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.8"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.9"); //$NON-NLS-1$

    public StateDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof CDOObject)
      {
        return CDOUtil.getCDOObject((EObject)object).cdoState();
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
  public static class ViewDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.10"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.11"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.12"); //$NON-NLS-1$

    public ViewDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof EObject)
      {
        return CDOUtil.getCDOObject((EObject)object).cdoView();
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
  public static class ContainerDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.13"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.14"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.15"); //$NON-NLS-1$

    private static AdapterFactory adapterFactory;

    public ContainerDescriptor(AdapterFactory adapterFactory)
    {
      ContainerDescriptor.adapterFactory = adapterFactory;
    }

    public Object getPropertyValue(Object object)
    {
      return CDOUtil.getCDOObject((EObject)object).eContainer();
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
          if (object != null)
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
  public static class DirectResourceDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.16"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.17"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.18"); //$NON-NLS-1$

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
  public static class ReadLockedDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.19"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.20"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.21"); //$NON-NLS-1$

    public ReadLockedDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof EObject)
      {
        return CDOUtil.getCDOObject((EObject)object).cdoReadLock();
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
  public static class WriteLockedDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.22"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.23"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.24"); //$NON-NLS-1$

    public WriteLockedDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof EObject)
      {
        return CDOUtil.getCDOObject((EObject)object).cdoWriteLock();
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
  public static class AdaptersDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.25"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.26"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.27"); //$NON-NLS-1$

    public AdaptersDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof EObject)
      {
        return CDOUtil.getCDOObject((EObject)object).eAdapters();
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
  public static class ChangeSubscriptionPoliciesDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.28"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.29"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.30"); //$NON-NLS-1$

    public ChangeSubscriptionPoliciesDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof EObject)
      {
        CDOView cdoView = CDOUtil.getCDOObject((EObject)object).cdoView();
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
              builder.append(", "); //$NON-NLS-1$
            }

            return builder.toString();
          }

          return null;
        }
      };
    }
  }

  /**
   * Adds the URI of a {@link CDOObject} to the Properties View.
   * 
   * @since 2.0
   * @author Victor Roldan Betancort
   */
  public static class URIDescriptor extends CDOPropertyDescriptor
  {
    private static final String FEATURE_ID = Messages.getString("CDOItemProviderAdapter.31"); //$NON-NLS-1$

    private static final String DISPLAY_NAME = Messages.getString("CDOItemProviderAdapter.32"); //$NON-NLS-1$

    private static final String DESCRIPTION = Messages.getString("CDOItemProviderAdapter.33"); //$NON-NLS-1$

    public URIDescriptor()
    {
    }

    public Object getPropertyValue(Object object)
    {
      if (object instanceof EObject)
      {
        CDOObject cdoObject = CDOUtil.getCDOObject((EObject)object);
        Resource resource = cdoObject.eResource();
        if (resource != null)
        {
          CDOID id = cdoObject.cdoID();
          String fragment = id != null ? id.toURIFragment() : resource.getURIFragment(cdoObject);
          return resource.getURI().appendFragment(fragment).toString();
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
          if (object instanceof String)
          {
            return (String)object;
          }

          return null;
        }
      };
    }
  }
}
