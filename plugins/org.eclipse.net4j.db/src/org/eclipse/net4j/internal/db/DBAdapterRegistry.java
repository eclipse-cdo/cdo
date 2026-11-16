/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBAdapter.Registry;
import org.eclipse.net4j.db.IDBAdapterID;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.internal.db.bundle.OM;
import org.eclipse.net4j.spi.db.DelegatingDBAdapter;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * Internal, thread-safe registry implementation for versioned database adapters.
 *
 * @author Eike Stepper
 */
public final class DBAdapterRegistry extends Container<Map.Entry<String, IDBAdapter>> implements Registry
{
  public static final DBAdapterRegistry INSTANCE = createInstance();

  /**
   * Values are either an eagerly registered adapter or a lazy descriptor.
   */
  private final Map<IDBAdapterID, IDBAdapterID> entries;

  private final Map<IDBAdapterID, DBAdapterDescriptor> descriptors;

  private volatile boolean autoCommit = true;

  public DBAdapterRegistry()
  {
    entries = new HashMap<>();
    descriptors = new HashMap<>();
  }

  public DBAdapterRegistry(int initialCapacity)
  {
    entries = new HashMap<>(initialCapacity);
    descriptors = new HashMap<>(initialCapacity);
  }

  public DBAdapterRegistry(int initialCapacity, float loadFactor)
  {
    entries = new HashMap<>(initialCapacity, loadFactor);
    descriptors = new HashMap<>(initialCapacity, loadFactor);
  }

  public DBAdapterRegistry(Map<? extends String, ? extends IDBAdapter> map)
  {
    entries = new HashMap<>(map.size());
    descriptors = new HashMap<>();
    putAll(map);
  }

  @Override
  public synchronized IDBAdapterID[] getAdapterIDs()
  {
    List<IDBAdapterID> result = new ArrayList<>(entries.size());

    for (DBAdapterID key : allKeys())
    {
      result.add(entries.get(key));
    }

    return result.toArray(new IDBAdapterID[0]);
  }

  @Override
  public synchronized IDBAdapterID[] getAdapterIDs(String adapterName)
  {
    List<IDBAdapterID> result = new ArrayList<>();

    for (DBAdapterID key : allKeys())
    {
      if (key.getName().equalsIgnoreCase(adapterName))
      {
        result.add(entries.get(key));
      }
    }

    return result.toArray(new IDBAdapterID[0]);
  }

  @Override
  public synchronized IDBAdapter[] getAdapters()
  {
    List<IDBAdapter> result = new ArrayList<>(entries.size());

    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter != null)
      {
        result.add(adapter);
      }
    }

    return result.toArray(new IDBAdapter[0]);
  }

  @Override
  public synchronized IDBAdapter[] getAdapters(String adapterName)
  {
    List<IDBAdapter> result = new ArrayList<>();

    for (DBAdapterID key : allKeys())
    {
      if (key.getName().equalsIgnoreCase(adapterName))
      {
        IDBAdapter adapter = materialize(key);
        if (adapter != null)
        {
          result.add(adapter);
        }
      }
    }

    return result.toArray(new IDBAdapter[0]);
  }

  @Override
  public synchronized IDBAdapter getAdapter(IDBAdapterID id)
  {
    return id == null ? null : materialize(DBAdapterID.copy(id));
  }

  @Override
  public synchronized IDBAdapter getAdapter(String adapterName, String version)
  {
    return materialize(new DBAdapterID(adapterName, version));
  }

  @Override
  public synchronized IDBAdapter getAdapter(String adapterName)
  {
    for (DBAdapterID key : allKeysDescending())
    {
      if (key.getName().equalsIgnoreCase(adapterName))
      {
        return materialize(key);
      }
    }

    return null;
  }

  @Override
  public IDBAdapter getAdapter(Connection connection)
  {
    Objects.requireNonNull(connection, "connection");

    if (connection instanceof IDBConnection)
    {
      IDBConnection dbConnection = (IDBConnection)connection;

      IDBDatabase database = dbConnection.getDatabase();
      if (database != null)
      {
        IDBAdapter adapter = database.getAdapter();
        if (adapter != null)
        {
          return adapter;
        }
      }
    }

    try
    {
      DatabaseMetaData metadata = connection.getMetaData();
      if (metadata == null)
      {
        return null;
      }

      String productName = metadata.getDatabaseProductName();
      if (productName == null || productName.trim().isEmpty())
      {
        return null;
      }

      Registration[] registrations = getRegistrations();
      List<Registration> productMatches = new ArrayList<>();

      boolean needsURL = false;
      boolean needsDriverName = false;

      for (Registration registration : registrations)
      {
        if (registration.matchesProduct(productName))
        {
          productMatches.add(registration);
          needsURL |= registration.urlPattern != null;
          needsDriverName |= registration.driverNamePattern != null;
        }
      }

      if (productMatches.isEmpty())
      {
        return null;
      }

      String url = needsURL ? metadata.getURL() : null;
      String driverName = needsDriverName ? metadata.getDriverName() : null;

      List<Registration> matches = new ArrayList<>();
      boolean hasDirectMatch = false;

      for (Registration registration : productMatches)
      {
        if (registration.matchesMetadata(url, driverName))
        {
          matches.add(registration);
          hasDirectMatch |= registration.id.getName().equalsIgnoreCase(productName.trim());
        }
      }

      Registration selected = null;

      for (Registration match : matches)
      {
        if (hasDirectMatch && !match.id.getName().equalsIgnoreCase(productName.trim()))
        {
          continue;
        }

        if (selected != null && !selected.id.getName().equalsIgnoreCase(match.id.getName()))
        {
          return null;
        }

        if (selected == null || DBAdapterID.copy(match.id).compareTo(DBAdapterID.copy(selected.id)) > 0)
        {
          selected = match;
        }
      }

      return selected == null ? null : getAdapter(selected.id);
    }
    catch (SQLException ex)
    {
      throw new DBException("Could not resolve database adapter from JDBC metadata", ex);
    }
  }

  @Override
  public synchronized IDBAdapter get(Object key)
  {
    if (key instanceof IDBAdapterID)
    {
      return getAdapter((IDBAdapterID)key);
    }

    return key instanceof String ? getAdapter((String)key) : null;
  }

  @Override
  public synchronized int size()
  {
    return entries.size();
  }

  @Override
  public synchronized boolean isEmpty()
  {
    return entries.isEmpty();
  }

  @Override
  public synchronized boolean containsKey(Object key)
  {
    if (key instanceof IDBAdapterID)
    {
      return entries.containsKey(DBAdapterID.copy((IDBAdapterID)key));
    }

    if (key instanceof String)
    {
      for (IDBAdapterID id : entries.keySet())
      {
        if (id.getName().equalsIgnoreCase((String)key))
        {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public synchronized boolean containsValue(Object value)
  {
    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter == value || value != null && value.equals(adapter))
      {
        return true;
      }
    }

    return false;
  }

  @Override
  public synchronized Set<String> keySet()
  {
    Set<String> result = new LinkedHashSet<>();

    for (DBAdapterID id : allKeys())
    {
      result.add(id.getName());
    }

    return Collections.unmodifiableSet(result);
  }

  @Override
  public synchronized Collection<IDBAdapter> values()
  {
    List<IDBAdapter> result = new ArrayList<>();

    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter != null)
      {
        result.add(adapter);
      }
    }

    return Collections.unmodifiableList(result);
  }

  @Override
  public synchronized Set<Map.Entry<String, IDBAdapter>> entrySet()
  {
    Set<Map.Entry<String, IDBAdapter>> result = new LinkedHashSet<>();

    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter != null)
      {
        result.add(entry(key, adapter));
      }
    }

    return Collections.unmodifiableSet(result);
  }

  @Override
  public synchronized IDBAdapter remove(Object key)
  {
    throw new UnsupportedOperationException("DB adapter deregistration is not supported");
  }

  @Override
  public synchronized void clear()
  {
    throw new UnsupportedOperationException("DB adapter deregistration is not supported");
  }

  @Override
  public synchronized Map.Entry<String, IDBAdapter>[] getElements()
  {
    @SuppressWarnings("unchecked")
    Map.Entry<String, IDBAdapter>[] result = entrySet().toArray(new Map.Entry[0]);
    return result;
  }

  @Override
  public boolean isAutoCommit()
  {
    return autoCommit;
  }

  @Override
  public void setAutoCommit(boolean on)
  {
    autoCommit = on;
  }

  @Override
  public void commit(boolean notifications)
  {
    // Do nothing.
  }

  @Override
  public void commit()
  {
    // Do nothing.
  }

  public synchronized IDBAdapter register(IDBAdapter adapter)
  {
    if (adapter == null)
    {
      throw new IllegalArgumentException("Adapter must not be null");
    }

    DBAdapterID id = DBAdapterID.copy(adapter);
    rejectDuplicate(id);
    entries.put(id, adapter);
    fireElementAddedEvent(entry(id, adapter));
    return adapter;
  }

  @Override
  public synchronized IDBAdapter put(String ignoredName, IDBAdapter adapter)
  {
    return register(adapter);
  }

  @Override
  public synchronized void putAll(Map<? extends String, ? extends IDBAdapter> map)
  {
    for (IDBAdapter adapter : map.values())
    {
      register(adapter);
    }
  }

  public synchronized DBAdapterDescriptor addDescriptor(DBAdapterDescriptor descriptor)
  {
    if (descriptor == null)
    {
      throw new IllegalArgumentException("Descriptor must not be null");
    }

    DBAdapterID id = DBAdapterID.copy(descriptor);
    rejectDuplicate(id);

    entries.put(id, descriptor);
    descriptors.put(id, descriptor);

    fireElementAddedEvent(entry(id, null));
    return descriptor;
  }

  /**
   * Kept for source compatibility; adapter deregistration is intentionally unsupported.
   */
  public DBAdapterDescriptor removeDescriptor(String adapterName)
  {
    throw new UnsupportedOperationException("DB adapter deregistration is not supported");
  }

  private Registration[] getRegistrations()
  {
    List<Map.Entry<IDBAdapterID, IDBAdapterID>> snapshot = new ArrayList<>();

    synchronized (this)
    {
      for (IDBAdapterID id : allKeys())
      {
        IDBAdapterID source = descriptors.get(id);
        if (source == null)
        {
          source = entries.get(id);
        }

        snapshot.add(new AbstractMap.SimpleImmutableEntry<>(DBAdapterID.copy(id), source));
      }
    }

    List<Registration> result = new ArrayList<>(snapshot.size());

    for (Map.Entry<IDBAdapterID, IDBAdapterID> entry : snapshot)
    {
      IDBAdapterID id = entry.getKey();
      IDBAdapterID value = entry.getValue();

      if (value instanceof DBAdapterDescriptor)
      {
        DBAdapterDescriptor descriptor = (DBAdapterDescriptor)value;
        result.add(new Registration(id, descriptor.getDatabaseProductNames(), descriptor.getDatabaseProductNamePattern(),
            descriptor.getDatabaseDriverNamePattern(), descriptor.getDatabaseURLPattern()));
      }
      else if (value instanceof IDBAdapter)
      {
        IDBAdapter adapter = (IDBAdapter)value;
        result.add(new Registration(id, adapter.getDatabaseProductNames(), adapter.getDatabaseProductNamePattern(), adapter.getDatabaseDriverNamePattern(),
            adapter.getDatabaseURLPattern()));
      }
    }

    return result.toArray(new Registration[0]);
  }

  private void rejectDuplicate(DBAdapterID id)
  {
    if (entries.containsKey(id))
    {
      throw new IllegalStateException("Duplicate DB adapter registration: " + id.getName() + " " + id.getVersion());
    }
  }

  private IDBAdapter materialize(DBAdapterID id)
  {
    IDBAdapterID value = entries.get(id);
    if (value instanceof IDBAdapter)
    {
      return (IDBAdapter)value;
    }

    if (value instanceof DBAdapterDescriptor)
    {
      DBAdapterDescriptor descriptor = (DBAdapterDescriptor)value;
      IDBAdapter adapter = descriptor.createDBAdapter();
      if (adapter == null)
      {
        return null;
      }

      if (!DBAdapterID.copy(adapter).equals(id))
      {
        String name = descriptor.getName();
        String version = descriptor.getVersion();
        OM.LOG.warn("DB adapter metadata differs from descriptor: " + name + " " + version);

        adapter = new DelegatingDBAdapter(adapter, name, version);
      }

      entries.put(id, adapter);
      return adapter;
    }

    return null;
  }

  private List<DBAdapterID> allKeys()
  {
    List<DBAdapterID> result = new ArrayList<>(entries.size());

    for (IDBAdapterID key : entries.keySet())
    {
      result.add((DBAdapterID)key);
    }

    Collections.sort(result);
    return result;
  }

  private List<DBAdapterID> allKeysDescending()
  {
    List<DBAdapterID> result = allKeys();
    Collections.reverse(result);
    return result;
  }

  private void initialize()
  {
    BiConsumer<DBAdapterDescriptor, Object> consumer = (descriptor, source) -> {
      try
      {
        addDescriptor(descriptor);
      }
      catch (IllegalArgumentException ex)
      {
        OM.LOG.error("Invalid DB adapter contribution " + descriptor + " from " + source, ex);
      }
    };

    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      OSGiDBAdapterDescriptor.collect(consumer);
    }
    else
    {
      StandaloneDBAdapterDescriptor.collect(consumer);
    }
  }

  private static String[] parseProductNames(String productNames)
  {
    return productNames == null || productNames.trim().isEmpty() ? null : productNames.split("\\s*,\\s*"); //$NON-NLS-1$
  }

  private static void validateRequiredAttributes(String name, String version, String className, Object source)
  {
    if (name == null || name.trim().isEmpty())
    {
      throw new IllegalArgumentException("DB adapter contribution from " + source + " is missing required attribute 'name'"); //$NON-NLS-1$
    }

    if (version == null || version.trim().isEmpty())
    {
      throw new IllegalArgumentException("DB adapter contribution '" + name + "' from " + source + " is missing required attribute 'version'"); //$NON-NLS-1$
    }

    if (className == null || className.trim().isEmpty())
    {
      throw new IllegalArgumentException("DB adapter contribution '" + name + "' from " + source + " is missing required attribute 'class'"); //$NON-NLS-1$
    }
  }

  private static Map.Entry<String, IDBAdapter> entry(IDBAdapterID id, IDBAdapter adapter)
  {
    return new AbstractMap.SimpleImmutableEntry<>(id.getName(), adapter);
  }

  private static DBAdapterRegistry createInstance()
  {
    DBAdapterRegistry registry = new DBAdapterRegistry();
    registry.initialize();
    return registry;
  }

  /**
   * @author Eike Stepper
   */
  private static final class OSGiDBAdapterDescriptor extends DBAdapterDescriptor
  {
    private final IConfigurationElement element;

    private OSGiDBAdapterDescriptor(IConfigurationElement element)
    {
      super( //
          attribute(element, "name"), //$NON-NLS-1$
          attribute(element, "version"), //$NON-NLS-1$
          parseProductNames(attribute(element, "productNames")), //$NON-NLS-1$
          attribute(element, "productNamePattern"), //$NON-NLS-1$
          attribute(element, "driverNamePattern"), //$NON-NLS-1$
          attribute(element, "urlPattern") //$NON-NLS-1$
      );

      validateRequiredAttributes(getName(), getVersion(), attribute(element, "class"), element); //$NON-NLS-1$
      this.element = element;
    }

    @Override
    public IDBAdapter createDBAdapter()
    {
      try
      {
        return (IDBAdapter)element.createExecutableExtension("class"); //$NON-NLS-1$
      }
      catch (CoreException ex)
      {
        OM.LOG.error(ex);
        return null;
      }
    }

    public static void collect(BiConsumer<DBAdapterDescriptor, Object> consumer)
    {
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, OM.EXT_POINT);

      for (IConfigurationElement element : elements)
      {
        if ("dbAdapter".equals(element.getName())) //$NON-NLS-1$
        {
          DBAdapterDescriptor descriptor;

          try
          {
            descriptor = new OSGiDBAdapterDescriptor(element);
          }
          catch (RuntimeException ex)
          {
            OM.LOG.error("Could not create DB adapter contribution from " + element, ex);
            continue;
          }

          consumer.accept(descriptor, element);
        }
      }
    }

    private static String attribute(IConfigurationElement element, String name)
    {
      String value = element.getAttribute(name);
      return value == null || value.trim().isEmpty() ? null : value;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class StandaloneDBAdapterDescriptor extends DBAdapterDescriptor
  {
    private final ClassLoader loader;

    private final URL resource;

    private final String className;

    private StandaloneDBAdapterDescriptor(ClassLoader loader, URL resource, Element element)
    {
      super( //
          attribute(element, "name"), //$NON-NLS-1$
          attribute(element, "version"), //$NON-NLS-1$
          parseProductNames(attribute(element, "productNames")), //$NON-NLS-1$
          attribute(element, "productNamePattern"), //$NON-NLS-1$
          attribute(element, "driverNamePattern"), //$NON-NLS-1$
          attribute(element, "urlPattern") //$NON-NLS-1$
      );

      this.loader = loader;
      this.resource = resource;
      className = attribute(element, "class"); //$NON-NLS-1$

      validateRequiredAttributes(getName(), getVersion(), className, resource);
    }

    @Override
    public IDBAdapter createDBAdapter()
    {
      try
      {
        Class<?> adapterClass = loadAdapterClass(className, loader);
        return (IDBAdapter)adapterClass.getDeclaredConstructor().newInstance();
      }
      catch (Exception | LinkageError ex)
      {
        OM.LOG.error("Could not create DB adapter " + getName() + " from " + resource, ex);
        return null;
      }
    }

    public static void collect(BiConsumer<DBAdapterDescriptor, Object> consumer)
    {
      Set<URL> discovered = new HashSet<>();

      ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
      if (contextLoader != null)
      {
        discoverResources(consumer, contextLoader, discovered);
      }

      ClassLoader registryLoader = DBAdapterRegistry.class.getClassLoader();
      if (registryLoader != contextLoader)
      {
        discoverResources(consumer, registryLoader, discovered);
      }

      discoverClasspathEntries(consumer, contextLoader == null ? registryLoader : contextLoader, discovered);
    }

    private static void discoverResources(BiConsumer<DBAdapterDescriptor, Object> consumer, ClassLoader loader, Set<URL> discovered)
    {
      try
      {
        Enumeration<URL> resources = loader.getResources("plugin.xml"); //$NON-NLS-1$

        while (resources.hasMoreElements())
        {
          URL resource = null;

          try
          {
            resource = resources.nextElement();
            resource = normalizeResource(resource);
            if (discovered.add(resource))
            {
              try (InputStream input = resource.openStream())
              {
                registerContributions(consumer, loader, resource, input);
              }
            }
          }
          catch (IllegalStateException ex)
          {
            throw ex;
          }
          catch (Exception ex)
          {
            OM.LOG.error("Could not read DB adapter contributions from " + resource, ex);
          }
        }
      }
      catch (IllegalStateException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        OM.LOG.error("Could not discover DB adapter plugin.xml resources", ex);
      }
    }

    private static void discoverClasspathEntries(BiConsumer<DBAdapterDescriptor, Object> consumer, ClassLoader loader, Set<URL> discovered)
    {
      String classpath = System.getProperty("java.class.path"); //$NON-NLS-1$
      if (classpath == null || classpath.trim().isEmpty())
      {
        return;
      }

      for (String entry : classpath.split(Pattern.quote(File.pathSeparator)))
      {
        File directory = new File(entry.trim());
        if (!directory.isDirectory())
        {
          continue;
        }

        File candidate = directory;
        for (int level = 0; level <= 2 && candidate != null; ++level, candidate = candidate.getParentFile())
        {
          File pluginXML = new File(candidate, "plugin.xml"); //$NON-NLS-1$
          if (pluginXML.isFile())
          {
            try
            {
              URL resource = normalizeResource(pluginXML.toURI().toURL());
              if (discovered.add(resource))
              {
                try (InputStream input = resource.openStream())
                {
                  registerContributions(consumer, loader, resource, input);
                }
                catch (IllegalStateException ex)
                {
                  throw ex;
                }
                catch (Exception ex)
                {
                  OM.LOG.error("Could not read DB adapter contributions from " + resource, ex);
                }
              }
            }
            catch (IllegalStateException ex)
            {
              throw ex;
            }
            catch (Exception ex)
            {
              OM.LOG.error("Could not locate DB adapter contributions from " + pluginXML, ex);
            }

            break;
          }
        }
      }
    }

    private static void registerContributions(BiConsumer<DBAdapterDescriptor, Object> consumer, ClassLoader loader, URL resource, InputStream input)
        throws Exception
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); //$NON-NLS-1$
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false); //$NON-NLS-1$
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false); //$NON-NLS-1$
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); //$NON-NLS-1$
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); //$NON-NLS-1$
      factory.setXIncludeAware(false);
      factory.setExpandEntityReferences(false);

      Document document = factory.newDocumentBuilder().parse(input);
      Element root = document.getDocumentElement();
      NodeList extensions = root.getElementsByTagName("extension"); //$NON-NLS-1$

      for (int i = 0; i < extensions.getLength(); ++i)
      {
        Element extension = (Element)extensions.item(i);
        if ("org.eclipse.net4j.db.dbAdapters".equals(extension.getAttribute("point"))) //$NON-NLS-1$
        {
          for (Node node = extension.getFirstChild(); node != null; node = node.getNextSibling())
          {
            if (node instanceof Element && "dbAdapter".equals(node.getNodeName())) //$NON-NLS-1$
            {
              DBAdapterDescriptor descriptor;

              try
              {
                descriptor = new StandaloneDBAdapterDescriptor(loader, resource, (Element)node);
              }
              catch (RuntimeException ex)
              {
                OM.LOG.error("Could not create DB adapter contribution from " + resource, ex);
                continue;
              }

              consumer.accept(descriptor, resource);
            }
          }
        }
      }
    }

    private static URL normalizeResource(URL resource) throws Exception
    {
      if ("file".equals(resource.getProtocol())) //$NON-NLS-1$
      {
        return new File(resource.toURI()).getCanonicalFile().toURI().toURL();
      }

      return resource.toURI().normalize().toURL();
    }

    private static Class<?> loadAdapterClass(String className, ClassLoader preferredLoader) throws ClassNotFoundException
    {
      Set<ClassLoader> loaders = new LinkedHashSet<>();
      if (preferredLoader != null)
      {
        loaders.add(preferredLoader);
      }

      ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
      if (contextLoader != null)
      {
        loaders.add(contextLoader);
      }

      ClassLoader registryLoader = DBAdapterRegistry.class.getClassLoader();
      if (registryLoader != null)
      {
        loaders.add(registryLoader);
      }

      ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
      if (systemLoader != null)
      {
        loaders.add(systemLoader);
      }

      ClassNotFoundException failure = null;
      for (ClassLoader loader : loaders)
      {
        try
        {
          Class<?> adapterClass = Class.forName(className, false, loader);
          if (IDBAdapter.class.isAssignableFrom(adapterClass))
          {
            return adapterClass;
          }
        }
        catch (ClassNotFoundException ex)
        {
          failure = ex;
        }
        catch (LinkageError ex)
        {
          failure = new ClassNotFoundException("Could not load DB adapter class " + className, ex); //$NON-NLS-1$
        }
      }

      if (failure != null)
      {
        throw failure;
      }

      throw new ClassNotFoundException("Could not load DB adapter class " + className + " from the available class loaders"); //$NON-NLS-1$
    }

    private static String attribute(Element element, String name)
    {
      String value = element.getAttribute(name);
      return value == null || value.trim().isEmpty() ? null : value;
    }
  }

  /**
   * A registration snapshot used while matching JDBC metadata.
   *
   * @author Eike Stepper
   */
  private static final class Registration
  {
    private final IDBAdapterID id;

    private final String[] productNames;

    private final String productNamePattern;

    private final String driverNamePattern;

    private final String urlPattern;

    public Registration(IDBAdapterID id, String[] productNames, String productNamePattern, String driverNamePattern, String urlPattern)
    {
      this.id = DBAdapterID.copy(id);
      this.productNames = productNames == null ? new String[0] : productNames.clone();
      this.productNamePattern = productNamePattern;
      this.driverNamePattern = driverNamePattern;
      this.urlPattern = urlPattern;
    }

    public boolean matchesProduct(String productName)
    {
      boolean productMatches = false;

      for (String candidate : productNames)
      {
        if (candidate != null && candidate.trim().equalsIgnoreCase(productName.trim()))
        {
          productMatches = true;
          break;
        }
      }

      if (!productMatches && productNamePattern != null)
      {
        productMatches = matchesPattern(productNamePattern, productName);
      }

      return productMatches;
    }

    public boolean matchesMetadata(String url, String driverName)
    {
      return matchesConstraint(urlPattern, url) && matchesConstraint(driverNamePattern, driverName);
    }

    private static boolean matchesConstraint(String pattern, String value)
    {
      return pattern == null || value != null && matchesPattern(pattern, value);
    }

    private static boolean matchesPattern(String pattern, String value)
    {
      return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(value.trim()).matches();
    }
  }
}
