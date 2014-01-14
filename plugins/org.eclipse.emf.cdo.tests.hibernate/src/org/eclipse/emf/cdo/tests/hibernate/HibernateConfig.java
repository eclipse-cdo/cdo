/*
 * Copyright (c) 2008-2012, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.hibernate.CDOHibernateUtil;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.teneo.TeneoUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.WrappedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class HibernateConfig extends RepositoryConfig
{
  public static final String STORE_NAME = "Hibernate";

  public static final HibernateConfig INSTANCE = new HibernateConfig(false);

  public static final HibernateConfig AUDIT_INSTANCE = new HibernateConfig(true);

  public static final String MAPPING_FILE = "mappingfile";

  private static final long serialVersionUID = 1L;

  private Map<String, String> additionalProperties = new HashMap<String, String>();

  public HibernateConfig(boolean supportAuditing)
  {
    super(STORE_NAME, supportAuditing, false, IDGenerationLocation.STORE);
  }

  @Override
  protected String getStoreName()
  {
    return STORE_NAME;
  }

  @Override
  protected void initRepositoryProperties(Map<String, String> props)
  {
    super.initRepositoryProperties(props);

    try
    {
      final Properties teneoProperties = new Properties();
      Map<String, String> additionalProperties = getAdditionalProperties();
      teneoProperties.load(getClass().getResourceAsStream("/app.properties"));
      teneoProperties.putAll(additionalProperties);
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

  public IStore createStore(String repoName)
  {
    // note the provider properties are read from the store/repository level
    IHibernateMappingProvider mappingProvider = TeneoUtil.createMappingProvider();
    return CDOHibernateUtil.createStore(mappingProvider);
  }

  public Map<String, String> getAdditionalProperties()
  {
    return additionalProperties;
  }
}
