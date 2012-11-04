/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.teneo;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.PersistenceOptions;
import org.eclipse.emf.teneo.annotations.mapper.EDataTypeAnnotator;
import org.eclipse.emf.teneo.annotations.mapper.EFeatureAnnotator;
import org.eclipse.emf.teneo.annotations.mapper.OneToManyReferenceAnnotator;
import org.eclipse.emf.teneo.annotations.xml.XmlPersistenceContentHandler;
import org.eclipse.emf.teneo.extension.ExtensionManager;
import org.eclipse.emf.teneo.extension.ExtensionManagerFactory;
import org.eclipse.emf.teneo.extension.ExtensionUtil;
import org.eclipse.emf.teneo.hibernate.mapper.ManyAttributeMapper;
import org.eclipse.emf.teneo.hibernate.mapper.MappingContext;
import org.eclipse.emf.teneo.hibernate.mapper.MappingUtil;
import org.eclipse.emf.teneo.hibernate.mapper.OneToManyMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Generates a CDO enabled mapping for Hibernate.
 * 
 * @author <a href="mtaal@elver.org">Martin Taal</a>
 * @since 3.0
 */
public class CDOMappingGenerator
{
  private static Map<String, String> extensions = new HashMap<String, String>();

  private ExtensionManager extensionManager = ExtensionManagerFactory.getInstance().create();

  public CDOMappingGenerator()
  {
  }

  public void registerCDOExtensions(ExtensionManager extensionManager)
  {
    MappingUtil.registerHbExtensions(extensionManager);

    // very strange but these can not go into the constructor...
    // get a class not found exception then
    addDefaultExtension(MappingContext.class, CDOMappingContext.class, extensionManager);
    addDefaultExtension(EFeatureAnnotator.class, CDOEFeatureAnnotator.class, extensionManager);
    addDefaultExtension(ManyAttributeMapper.class, CDOManyAttributeMapper.class, extensionManager);
    addDefaultExtension(XmlPersistenceContentHandler.class, CDOXmlPersistenceContentHandler.class, extensionManager);
    addDefaultExtension(EDataTypeAnnotator.class, CDOEDataTypeAnnotator.class, extensionManager);
    addDefaultExtension(OneToManyMapper.class, CDOOneToManyMapper.class, extensionManager);
    addDefaultExtension(OneToManyReferenceAnnotator.class, CDOOneToManyReferenceAnnotator.class, extensionManager);

    for (String key : extensions.keySet())
    {
      try
      {
        final Class<?> keyClass = Thread.currentThread().getContextClassLoader().loadClass(key);
        final Class<?> valueClass = Thread.currentThread().getContextClassLoader().loadClass(extensions.get(key));
        extensionManager.registerExtension(ExtensionUtil.createExtension(keyClass, valueClass, false));
      }
      catch (Exception e)
      {
        throw new WrappedException(e);
      }
    }
  }

  protected void addDefaultExtension(Class<?> extensionClass, Class<?> extendingClass, ExtensionManager extensionManager)
  {
    if (extensions.containsKey(extensionClass.getName()))
    {
      return;
    }

    extensionManager.registerExtension(ExtensionUtil.createExtension(extensionClass, extendingClass, false));
  }

  public Map<String, String> getExtensions()
  {
    return extensions;
  }

  public void putExtension(String extensionClassName, String extendingClassName)
  {
    extensions.put(extensionClassName, extendingClassName);
  }

  /**
   * Separate utility method, generates a hibernate mapping for a set of epackages and options. The hibernate.hbm.xml is
   * returned as a string. The mapping is not registered or used in any other way by Elver.
   */
  public String generateMapping(EPackage[] epackages, Properties props)
  {
    // set some default properties
    // never use hibernate optimistic locking
    props.put(PersistenceOptions.ALWAYS_VERSION, "false"); //$NON-NLS-1$

    if (!props.containsKey(PersistenceOptions.ID_COLUMN_NAME))
    {
      props.put(PersistenceOptions.ID_COLUMN_NAME, "idcol"); //$NON-NLS-1$
    }

    if (!props.containsKey(PersistenceOptions.VERSION_COLUMN_NAME))
    {
      props.put(PersistenceOptions.VERSION_COLUMN_NAME, "version"); //$NON-NLS-1$
    }

    props.setProperty(PersistenceOptions.FEATUREMAP_AS_COMPONENT, "true");
    props.put(PersistenceOptions.ALSO_MAP_AS_CLASS, "false"); //$NON-NLS-1$
    props.put(PersistenceOptions.EMAP_AS_TRUE_MAP, "false"); //$NON-NLS-1$
    registerCDOExtensions(extensionManager);
    return MappingUtil.generateMapping(epackages, props, extensionManager);
  }
}
