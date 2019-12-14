/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin;

import org.eclipse.emf.cdo.ui.internal.admin.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public abstract class StoreType
{
  private static final List<StoreType> INSTANCES = Collections.unmodifiableList(Arrays.asList(initStoreTypes()));

  private final String id;

  private final String name;

  public StoreType(String id, String name)
  {
    this.id = id;
    this.name = name;
  }

  public static List<StoreType> getInstances()
  {
    return INSTANCES;
  }

  public String getID()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public abstract String getStoreTypeID();

  public String getStoreXML(Map<String, Object> storeProperties)
  {
    String template = getTemplate();
    return fillTemplate(template, storeProperties);
  }

  protected abstract String fillTemplate(String xmlTemplate, Map<String, Object> storeProperties);

  protected String replaceVariables(String template, Map<String, String> variables)
  {
    String result = template;
    for (Map.Entry<String, String> entry : variables.entrySet())
    {
      result = result.replace("{{" + entry.getKey() + "}}", entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return result;
  }

  private String getTemplate()
  {
    URL url = getClass().getResource(getStoreTypeID() + ".template.xml"); //$NON-NLS-1$
    return IOUtil.readText(url);
  }

  @Override
  public String toString()
  {
    return getName();
  }

  private static StoreType[] initStoreTypes()
  {
    // TODO: Make these contributable by store UI plug-ins
    return new StoreType[] { new Database("h2", Messages.StoreType_0, "org.h2.jdbcx.JdbcDataSource", "jdbc:h2:%s"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    };
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   */
  public static class Database extends StoreType
  {
    public static final String PROPERTY_PATH = "path"; //$NON-NLS-1$

    public static final String PROPERTY_CONNECTION_KEEP_ALIVE_PERIOD = "connectionKeepAlivePeriod"; //$NON-NLS-1$

    public static final String DEFAULT_CONNECTION_KEEP_ALIVE_PERIOD = "60"; //$NON-NLS-1$

    public static final String PROPERTY_READER_POOL_CAPACITY = "readerPoolCapacity"; //$NON-NLS-1$

    public static final String DEFAULT_READER_POOL_CAPACITY = "15"; //$NON-NLS-1$

    public static final String PROPERTY_WRITER_POOL_CAPACITY = "writerPoolCapacity"; //$NON-NLS-1$

    public static final String DEFAULT_WRITER_POOL_CAPACITY = "15"; //$NON-NLS-1$

    private final String adapter;

    private final String dataSourceClassName;

    private final String urlPattern;

    public Database(String id, String name, String dataSourceClassName, String urlPattern)
    {
      super("db." + id, name); //$NON-NLS-1$
      adapter = id;
      this.dataSourceClassName = dataSourceClassName;
      this.urlPattern = urlPattern;
    }

    @Override
    public String getStoreTypeID()
    {
      return "db"; //$NON-NLS-1$
    }

    public String getAdapter()
    {
      return adapter;
    }

    public String getDataSourceClassName()
    {
      return dataSourceClassName;
    }

    public String getDataSourceURL(String storePath)
    {
      return String.format(urlPattern, storePath);
    }

    @Override
    protected String fillTemplate(String xmlTemplate, Map<String, Object> storeProperties)
    {
      Map<String, String> variables = new java.util.HashMap<>();
      variables.put("adapter", getAdapter()); //$NON-NLS-1$
      variables.put("dataSource.class", getDataSourceClassName()); //$NON-NLS-1$
      variables.put("dataSource.url", getDataSourceURL((String)storeProperties.get(PROPERTY_PATH))); //$NON-NLS-1$
      variables.put("keepAlive", //$NON-NLS-1$
          defaultString(storeProperties, PROPERTY_CONNECTION_KEEP_ALIVE_PERIOD, DEFAULT_CONNECTION_KEEP_ALIVE_PERIOD));
      variables.put("readerPool", //$NON-NLS-1$
          defaultString(storeProperties, PROPERTY_READER_POOL_CAPACITY, DEFAULT_READER_POOL_CAPACITY));
      variables.put("writerPool", //$NON-NLS-1$
          defaultString(storeProperties, PROPERTY_WRITER_POOL_CAPACITY, DEFAULT_WRITER_POOL_CAPACITY));
      return replaceVariables(xmlTemplate, variables);
    }

    private String defaultString(Map<String, Object> storeProperties, String key, String defaultValue)
    {
      String value = (String)storeProperties.get(key);
      return StringUtil.isEmpty(value) ? defaultValue : value;
    }
  }
}
