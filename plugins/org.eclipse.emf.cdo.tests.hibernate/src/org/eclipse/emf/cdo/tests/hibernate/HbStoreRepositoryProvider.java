/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.teneo.TeneoHibernateMappingProvider;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HbStoreRepositoryProvider extends StoreRepositoryProvider
{
  private static HbStoreRepositoryProvider instance = new HbStoreRepositoryProvider();

  public static HbStoreRepositoryProvider getInstance()
  {
    return instance;
  }

  public static void setInstance(HbStoreRepositoryProvider instance)
  {
    HbStoreRepositoryProvider.instance = instance;
  }

  @Override
  public IRepository createRepository(String name, Map<String, String> testProperties)
  {
    setLogging();

    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.PROP_OVERRIDE_UUID, "f8188187-65de-4c8a-8e75-e0ce5949837a");
    props.put(Props.PROP_SUPPORTING_AUDITS, "false");
    props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
    props.put(Props.PROP_VERIFYING_REVISIONS, "false");
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
    addHibernateTeneoProperties(props);

    // override with the test properties
    props.putAll(testProperties);

    return CDOServerUtil.createRepository(name, createStore(), props);
  }

  private void addHibernateTeneoProperties(Map<String, String> props)
  {
    try
    {
      final Properties teneoProperties = new Properties();
      teneoProperties.load(getClass().getResourceAsStream("/teneo.properties"));
      for (Object key : teneoProperties.keySet())
      {
        props.put((String)key, teneoProperties.getProperty((String)key));
      }
    }
    catch (Exception e)
    {
      throw WrappedException.wrap(e);
    }
  }

  @Override
  public IStore createStore()
  {
    IHibernateMappingProvider mappingProvider = new TeneoHibernateMappingProvider();
    // return new HibernateStore(props, mappingProvider);
    // IHibernateMappingProvider mappingProvider = new HibernateFileMappingProvider("/mappings/product.hbm.xml");
    return new HibernateStore(mappingProvider);
  }

  protected void setLogging()
  {
    OMPlatform.INSTANCE.setDebugging(false);
    // OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    // OMPlatform.INSTANCE.addTraceHandler(new PrintTraceHandler(new PrintStream("trace.txt")));
    // OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
  }
}
