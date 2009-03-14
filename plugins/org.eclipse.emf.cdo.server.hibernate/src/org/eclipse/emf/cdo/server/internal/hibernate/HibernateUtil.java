/**
 * Copyright (c) 2004 - 2009 Martin Taal and others. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.hibernate.Session;

import java.util.Map;
import java.util.Properties;

/**
 * @author Martin Taal
 */
public class HibernateUtil
{
  private static final String EXT_POINT = "mappingProviderFactories";

  private static HibernateUtil instance = new HibernateUtil();

  /**
   * @return the instance
   */
  public static HibernateUtil getInstance()
  {
    return instance;
  }

  /**
   * @param instance
   *          the instance to set
   */
  public static void setInstance(HibernateUtil instance)
  {
    HibernateUtil.instance = instance;
  }

  /**
   * @since 2.0
   */
  public IHibernateStore createStore(IHibernateMappingProvider mappingProvider)
  {
    HibernateStore store = new HibernateStore(mappingProvider);
    mappingProvider.setHibernateStore(store);
    return store;
  }

  /**
   * Can only be used when Eclipse is running. In standalone scenarios create the mapping strategy instance by directly
   * calling the constructor of the mapping strategy class.
   * 
   * @see #createFileMappingProvider(String...)
   * @since 2.0
   */
  public IHibernateMappingProvider.Factory createMappingProviderFactory(String type)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if ("mappingProviderFactory".equals(element.getName()))
      {
        String typeAttr = element.getAttribute("type");
        if (ObjectUtil.equals(typeAttr, type))
        {
          try
          {
            return (IHibernateMappingProvider.Factory)element.createExecutableExtension("class");
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public IHibernateMappingProvider createFileMappingProvider(String... locations)
  {
    return new FileHibernateMappingProvider(locations);
  }

  /**
   * @since 2.0
   */
  public Session getHibernateSession()
  {
    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentHibernateStoreAccessor();
    return accessor.getHibernateSession();
  }

  /** Converts from a Map<String, String> to a Properties */
  public Properties getPropertiesFromStore(IStore store)
  {
    final Properties props = new Properties();
    final Map<String, String> storeProps = store.getRepository().getProperties();
    for (String key : storeProps.keySet())
    {
      props.setProperty(key, storeProps.get(key));
    }

    return props;
  }

  public String getEntityName(CDORevision cdoRevision)
  {
    return cdoRevision.getEClass().getName();
  }

  /**
   * Translates a temporary cdoID into a hibernate ID, by finding the object it refers to in the CommitContext and then
   * returning or by persisting the object. Note assumes that the hibernate session and CommitContext are set in
   * HibernateThreadContext.
   */
  public CDOIDHibernate getCDOIDHibernate(CDOID cdoID)
  {
    final CDORevision cdoRevision = getCDORevision(cdoID);
    if (cdoRevision.getID() instanceof CDOIDHibernate)
    {
      return (CDOIDHibernate)cdoRevision.getID();
    }

    final Session session = getHibernateSession();
    if (!(cdoRevision.getID() instanceof CDOIDHibernate))
    {
      session.saveOrUpdate(cdoRevision);
    }
    if (!(cdoRevision.getID() instanceof CDOIDHibernate))
    {
      throw new IllegalStateException("CDORevision " + cdoRevision.getEClass().getName() + " " + cdoRevision.getID()
          + " does not have a hibernate cdoid after saving/updating it");
    }

    return (CDOIDHibernate)cdoRevision.getID();
  }

  public InternalCDORevision getCDORevision(Object target)
  {
    // if (target instanceof CDOObject)
    // {
    // return (InternalCDORevision)((CDOObject)target).cdoRevision();
    // }
    // else
    // {
    return (InternalCDORevision)target;
    // }
  }

  /**
   * Gets a current object, first checks the new and dirty objects from the commitcontent. Otherwise reads it from the
   * session.
   */
  public InternalCDORevision getCDORevision(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (HibernateThreadContext.isHibernateCommitContextSet())
    {
      final HibernateCommitContext hcc = HibernateThreadContext.getHibernateCommitContext();
      InternalCDORevision revision;
      if ((revision = hcc.getDirtyObject(id)) != null)
      {
        return revision;
      }

      if ((revision = hcc.getNewObject(id)) != null)
      {
        return revision;
      }

      // maybe the temp was already translated
      if (id instanceof CDOIDTemp)
      {
        final CDOID newID = hcc.getCommitContext().getIDMappings().get(id);
        if (newID != null)
        {
          return getCDORevision(newID);
        }
      }
    }

    if (!(id instanceof CDOIDHibernate))
    {
      throw new IllegalArgumentException("Passed cdoid is not an instance of CDOIDHibernate but a "
          + id.getClass().getName() + ": " + id);
    }

    final CDOIDHibernate cdoIDHibernate = (CDOIDHibernate)id;
    final Session session = getHibernateSession();
    return (InternalCDORevision)session.get(cdoIDHibernate.getEntityName(), cdoIDHibernate.getId());
  }

  public InternalCDORevision getCDORevisionNullable(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (HibernateThreadContext.isHibernateCommitContextSet())
    {
      final HibernateCommitContext hcc = HibernateThreadContext.getHibernateCommitContext();
      InternalCDORevision revision;
      if ((revision = hcc.getDirtyObject(id)) != null)
      {
        return revision;
      }

      if ((revision = hcc.getNewObject(id)) != null)
      {
        return revision;
      }

      // maybe the temp was already translated
      if (id instanceof CDOIDTemp)
      {
        final CDOID newID = hcc.getCommitContext().getIDMappings().get(id);
        if (newID != null)
        {
          return getCDORevision(newID);
        }
      }
    }

    if (!(id instanceof CDOIDHibernate))
    {
      return null;
    }

    final CDOIDHibernate cdoIDHibernate = (CDOIDHibernate)id;
    final Session session = getHibernateSession();
    return (InternalCDORevision)session.get(cdoIDHibernate.getEntityName(), cdoIDHibernate.getId());
  }
}
