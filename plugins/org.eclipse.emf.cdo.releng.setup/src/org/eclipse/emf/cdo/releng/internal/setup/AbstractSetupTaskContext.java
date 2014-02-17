/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Julian Enoch - Add support for secure context variables
 */
package org.eclipse.emf.cdo.releng.internal.setup;

import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.internal.setup.util.UpdateUtil;
import org.eclipse.emf.cdo.releng.internal.setup.util.UpdateUtil.UpdatingException;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.OS;
import org.eclipse.emf.cdo.releng.setup.util.SetupResource;
import org.eclipse.emf.cdo.releng.setup.util.SetupUtil;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public abstract class AbstractSetupTaskContext extends HashMap<Object, Object> implements SetupTaskContext
{
  private static final String[] FEATURE_TO_UPDATE = { "org.eclipse.emf.cdo.releng.setup.feature.group" };

  private static boolean NEEDS_PATH_SEPARATOR_CONVERSION = File.separatorChar == '\\';

  private static final Pattern STRING_EXPANSION_PATTERN = Pattern.compile("\\$(\\{([^${}|]+)(\\|([^}]+))?}|\\$)");

  private static final Map<String, StringFilter> STRING_FILTER_REGISTRY = new HashMap<String, StringFilter>();

  private static final String P2_AGENT_PATH = SetupUtil.getProperty("setup.p2.agent");

  private static final String P2_POOL_PATH = SetupUtil.getProperty("setup.p2.pool");

  private static final File P2_AGENT_DIR = P2_AGENT_PATH != null ? new File(P2_AGENT_PATH) : new File(
      SetupConstants.USER_HOME, ".p2");

  private static final File P2_POOL_DIR = P2_POOL_PATH != null ? new File(P2_POOL_PATH)
      : new File(P2_AGENT_DIR, "pool");

  private static final File STATE_DIR = new File(SetupConstants.USER_HOME, ".eclipse/org.eclipse.emf.cdo.releng.setup");

  protected static final String SECURE_STORAGE_NODE = "org.eclipse.emf.cdo.releng.setup";

  private static final long serialVersionUID = 1L;

  private Trigger trigger;

  private File branchDir;

  private Setup setup;

  private Setup originalSetup;

  private Preferences preferences;

  private boolean performing;

  private Set<String> restartReasons = new LinkedHashSet<String>();

  private ResourceSet resourceSet = EMFUtil.createResourceSet();

  private AbstractSetupTaskContext(Trigger trigger)
  {
    this.trigger = trigger;
  }

  protected AbstractSetupTaskContext(Trigger trigger, String installFolder, Setup setup)
  {
    this(trigger);

    Branch branch = setup.getBranch();
    Project project = branch.getProject();

    String branchFolder = branch.getName().toLowerCase();
    String projectFolder = project.getName().toLowerCase();
    branchDir = new File(installFolder, projectFolder + "/" + branchFolder).getAbsoluteFile();

    initialize(setup, false);
  }

  protected AbstractSetupTaskContext(Trigger trigger, File branchDir) throws UpdatingException
  {
    this(trigger);
    this.branchDir = branchDir;

    URI uri = getSetupURI(branchDir);
    SetupResource resource = EMFUtil.loadResourceSafely(resourceSet, uri);
    EcoreUtil.resolveAll(resource);

    for (Resource res : resourceSet.getResources())
    {
      if (res instanceof SetupResource)
      {
        SetupResource setupResource = (SetupResource)res;
        if (setupResource.getToolVersion() > SetupTaskMigrator.TOOL_VERSION)
        {
          Shell shell = UIUtil.getShell();
          if (UpdateUtil.update(shell, FEATURE_TO_UPDATE, true, false, null, null))
          {
            throw new UpdatingException();
          }

          break;
        }
      }
    }

    EList<EObject> contents = resource.getContents();
    if (!contents.isEmpty())
    {
      initialize((Setup)contents.get(0), true);
    }
  }

  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  private void initialize(Setup setup, boolean considerNetworkPreferences)
  {
    this.setup = setup;
    originalSetup = setup;
    preferences = originalSetup.getPreferences();

    // Apply network preferences early
    if (considerNetworkPreferences && getTrigger() != Trigger.BOOTSTRAP)
    {
      for (Iterator<EObject> it = preferences.eAllContents(); it.hasNext();)
      {
        EObject eObject = it.next();
        if (eObject instanceof EclipsePreferenceTask)
        {
          EclipsePreferenceTask preferenceTask = (EclipsePreferenceTask)eObject;
          String key = preferenceTask.getKey();
          if (key != null && key.startsWith("/configuration/org.eclipse.core.net/") && !key.contains("${"))
          {
            String value = preferenceTask.getValue();
            if (value != null && !value.contains("${"))
            {
              EclipsePreferenceTask preferenceTaskCopy = EcoreUtil.copy(preferenceTask);
              try
              {
                if (preferenceTaskCopy.isNeeded(this))
                {
                  preferenceTaskCopy.perform(this);
                }
              }
              catch (Exception ex)
              {
                // Ignore
              }
            }
          }
        }
      }
    }

    Branch branch = setup.getBranch();
    String branchName = branch.getName();

    Project project = branch.getProject();
    String projectName = project == null ? "<project>" : project.getName();
    String projectLabel = project == null ? null : project.getLabel();
    if (StringUtil.isEmpty(projectLabel))
    {
      projectLabel = projectName;
    }

    put(KEY_STATE_DIR, getStateDir());
    put(KEY_INSTALL_DIR, getInstallDir());
    put(KEY_P2_AGENT_DIR, getP2AgentDir());
    put(KEY_P2_POOL_DIR, getP2PoolDir());
    put(KEY_PROJECT_DIR, getProjectDir());
    put(KEY_BRANCH_DIR, getBranchDir());
    put(KEY_ECLIPSE_DIR, getEclipseDir());
    put(KEY_WS_DIR, getWorkspaceDir());
    put(KEY_PROJECT_NAME, projectName);
    put(KEY_PROJECT_LABEL, projectLabel);
    put(KEY_BRANCH_NAME, branchName);
    put(KEY_BRANCH_LABEL, projectLabel + " " + branchName);

    put(KEY_OS, Platform.getOS());
    put(KEY_ARCH, Platform.getOSArch());
    put(KEY_WS, Platform.getWS());

    put(PROP_RELENG_URL, RELENG_URL);

    for (Map.Entry<String, String> entry : System.getenv().entrySet())
    {
      put(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<Object, Object> entry : System.getProperties().entrySet())
    {
      put(entry.getKey(), entry.getValue());
    }
  }

  public Trigger getTrigger()
  {
    return trigger;
  }

  public void checkCancelation()
  {
    if (isCanceled())
    {
      throw new OperationCanceledException();
    }
  }

  public boolean isPerforming()
  {
    return performing;
  }

  public boolean isRestartNeeded()
  {
    return !restartReasons.isEmpty();
  }

  public void setRestartNeeded(String reason)
  {
    restartReasons.add(reason);
  }

  public Set<String> getRestartReasons()
  {
    return restartReasons;
  }

  public Preferences getPreferences()
  {
    return preferences;
  }

  public String expandString(String string)
  {
    return expandString(string, false);
  }

  public String expandString(String string, boolean secure)
  {
    return expandString(string, null, secure);
  }

  public Set<String> getVariables(String string)
  {
    if (string == null)
    {
      return null;
    }

    Set<String> result = new HashSet<String>();
    for (Matcher matcher = STRING_EXPANSION_PATTERN.matcher(string); matcher.find();)
    {
      String key = matcher.group(1);
      if (!"$".equals(key))
      {
        key = matcher.group(2);
        int prefixIndex = key.indexOf('/');
        if (prefixIndex != -1)
        {
          key = key.substring(0, prefixIndex);
        }
      }

      result.add(key);
    }

    return result;
  }

  protected String expandString(String string, Set<String> keys)
  {
    return expandString(string, keys, false);
  }

  private String expandString(String string, Set<String> keys, boolean secure)
  {
    if (string == null)
    {
      return null;
    }

    StringBuilder result = new StringBuilder();
    int previous = 0;
    boolean unresolved = false;
    for (Matcher matcher = STRING_EXPANSION_PATTERN.matcher(string); matcher.find();)
    {
      result.append(string.substring(previous, matcher.start()));
      String key = matcher.group(1);
      if ("$".equals(key))
      {
        result.append('$');
      }
      else
      {
        key = matcher.group(2);
        String suffix = "";

        int prefixIndex = key.indexOf('/');
        if (prefixIndex != -1)
        {
          suffix = key.substring(prefixIndex);
          key = key.substring(0, prefixIndex);
          if (NEEDS_PATH_SEPARATOR_CONVERSION)
          {
            suffix = suffix.replace('/', File.separatorChar);
          }
        }

        String value = lookup(key);
        if (value == null)
        {
          value = lookupSecurely(key); // If the value is in secure store, don't prompt for it

          if (value == null || !secure)
          {
            if (value == null && keys != null)
            {
              unresolved = true;
              keys.add(key);
            }
            else if (!unresolved)
            {
              result.append(matcher.group());
            }
          }

          if (!secure)
          {
            value = null;
          }
        }

        if (value != null)
        {
          String filters = matcher.group(4);
          if (filters != null)
          {
            for (String filterName : filters.split("\\|"))
            {
              value = filter(value, filterName);
            }
          }

          if (!unresolved)
          {
            result.append(value);
            result.append(suffix);
          }
        }
      }

      previous = matcher.end();
    }

    if (unresolved)
    {
      return null;
    }

    result.append(string.substring(previous));
    return result.toString();
  }

  public static void main(String[] args) throws Exception
  {
    class SecurePreferencesMock implements ISecurePreferences
    {
      private Map<String, SecurePreferencesMock> nodes = new HashMap<String, SecurePreferencesMock>();

      private Map<String, String> values = new HashMap<String, String>();

      public void removeNode()
      {
      }

      public void remove(String key)
      {
      }

      public void putLong(String key, long value, boolean encrypt) throws StorageException
      {
      }

      public void putInt(String key, int value, boolean encrypt) throws StorageException
      {
      }

      public void putFloat(String key, float value, boolean encrypt) throws StorageException
      {
      }

      public void putDouble(String key, double value, boolean encrypt) throws StorageException
      {
      }

      public void putByteArray(String key, byte[] value, boolean encrypt) throws StorageException
      {
      }

      public void putBoolean(String key, boolean value, boolean encrypt) throws StorageException
      {
      }

      public void put(String key, String value, boolean encrypt) throws StorageException
      {
        values.put(key, value);
      }

      public ISecurePreferences parent()
      {
        return null;
      }

      public boolean nodeExists(String pathName)
      {
        SecurePreferencesMock prefs = nodes.get(pathName);
        return prefs != null;
      }

      public ISecurePreferences node(String pathName)
      {
        SecurePreferencesMock prefs = nodes.get(pathName);
        if (prefs == null)
        {
          prefs = new SecurePreferencesMock();
          nodes.put(pathName, prefs);
        }
        return prefs;
      }

      public String name()
      {
        return null;
      }

      public String[] keys()
      {
        return null;
      }

      public boolean isEncrypted(String key) throws StorageException
      {
        return false;
      }

      public long getLong(String key, long def) throws StorageException
      {
        return 0;
      }

      public int getInt(String key, int def) throws StorageException
      {
        return 0;
      }

      public float getFloat(String key, float def) throws StorageException
      {
        return 0;
      }

      public double getDouble(String key, double def) throws StorageException
      {
        return 0;
      }

      public byte[] getByteArray(String key, byte[] def) throws StorageException
      {
        return null;
      }

      public boolean getBoolean(String key, boolean def) throws StorageException
      {
        return false;
      }

      public String get(String key, String def) throws StorageException
      {
        return values.get(key);
      }

      public void flush() throws IOException
      {
      }

      public void clear()
      {
      }

      public String[] childrenNames()
      {
        return null;
      }

      public String absolutePath()
      {
        return null;
      }
    }

    AbstractSetupTaskContext context = new AbstractSetupTaskContext(null)
    {
      private static final long serialVersionUID = 1L;

      @Override
      public ISecurePreferences getSecurePreferences()
      {
        return new SecurePreferencesMock();
      }

      public boolean isCanceled()
      {
        return false;
      }

      public void log(String line)
      {
      }

      public void log(String line, boolean filter)
      {
      }

      public void log(IStatus status)
      {
      }

      public void log(Throwable t)
      {
      }
    };

    context.put("user.id", "stepper");
    context.saveSecurePreference("foo\\secure.id", "stepper");

    Set<String> keys = new HashSet<String>();
    System.out.println(context.expandString("${bogus.id}", keys, false) + " --> " + keys);
    keys.clear();
    System.out.println(context.expandString("${bogus.id}", keys, true) + " --> " + keys);
    keys.clear();
    System.out.println(context.expandString("${user.id}", keys, false) + " --> " + keys);
    keys.clear();
    System.out.println(context.expandString("${user.id}", keys, true) + " --> " + keys);
    keys.clear();
    System.out.println(context.expandString("${foo\\secure.id}", keys, false) + " --> " + keys);
    keys.clear();
    System.out.println(context.expandString("${foo\\secure.id}", keys, true) + " --> " + keys);
  }

  public URI redirect(URI uri)
  {
    return resourceSet.getURIConverter().normalize(uri);
  }

  public URIConverter getURIConverter()
  {
    return resourceSet.getURIConverter();
  }

  public OS getOS()
  {
    return OS.INSTANCE;
  }

  public File getP2AgentDir()
  {
    return P2_AGENT_DIR;
  }

  public File getP2PoolDir()
  {
    return P2_POOL_DIR;
  }

  public File getP2ProfileDir()
  {
    return new File(getP2AgentDir(), "org.eclipse.equinox.p2.engine/profileRegistry/" + getP2ProfileName() + ".profile");
  }

  public String getP2ProfileName()
  {
    String profileName = getBranchDir().toString();
    return SetupUtil.encodePath(profileName);
  }

  public File getStateDir()
  {
    return STATE_DIR;
  }

  public File getInstallDir()
  {
    return getProjectDir().getParentFile();
  }

  public File getProjectDir()
  {
    return branchDir.getParentFile();
  }

  public File getBranchDir()
  {
    return branchDir;
  }

  public File getEclipseDir()
  {
    return new File(branchDir, getOS().getEclipseDir());
  }

  public File getWorkspaceDir()
  {
    return new File(branchDir, "ws");
  }

  public Setup getOriginalSetup()
  {
    return originalSetup;
  }

  public Setup getSetup()
  {
    return setup;
  }

  protected final void setSetup(Setup setup)
  {
    this.setup = setup;
  }

  protected final void setPerforming(boolean performing)
  {
    this.performing = performing;
  }

  @Override
  public Object get(Object key)
  {
    Object value = super.get(key);
    if (value == null && key instanceof String)
    {
      String name = (String)key;
      if (name.indexOf('.') != -1)
      {
        name = name.replace('.', '_');
        value = super.get(name);
      }
    }

    return value;
  }

  protected String lookup(String key)
  {
    Object object = get(key);
    if (object != null)
    {
      return object.toString();
    }

    return null;
  }

  protected String lookupSecurely(String key)
  {
    String newValue = null;

    ISecurePreferences securePreferences = getSecurePreferences();
    if (securePreferences.nodeExists(SECURE_STORAGE_NODE))
    {
      ISecurePreferences node = securePreferences.node(SECURE_STORAGE_NODE);

      try
      {
        newValue = node.get(key, null);
      }
      catch (StorageException ex)
      {
        log(ex);
      }
    }

    return newValue;
  }

  protected void saveSecurePreference(String name, String value)
  {
    ISecurePreferences securePreferences = getSecurePreferences();
    ISecurePreferences node = securePreferences.node(SECURE_STORAGE_NODE);

    try
    {
      node.put(name, value, true);
    }
    catch (StorageException ex)
    {
      log(ex);
    }
  }

  protected ISecurePreferences getSecurePreferences()
  {
    return SecurePreferencesFactory.getDefault();
  }

  protected String filter(String value, String filterName)
  {
    StringFilter filter = STRING_FILTER_REGISTRY.get(filterName);
    if (filter != null)
    {
      return filter.filter(value);
    }

    return value;
  }

  public static File getCurrentBranchDir()
  {
    return new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().removeLastSegments(1).toOSString());
  }

  public static Setup getCurrentSetup()
  {
    File branchDir = getCurrentBranchDir();
    URI setupURI = getSetupURI(branchDir);

    ResourceSet resourceSet = EMFUtil.createResourceSet();
    SetupResource setupResource = EMFUtil.loadResourceSafely(resourceSet, setupURI);
    return (Setup)setupResource.getContents().get(0);
  }

  public static URI getSetupURI(File branchFolder)
  {
    File setupFile = new File(branchFolder, "setup.xmi");
    return URI.createFileURI(setupFile.getAbsolutePath());
  }

  public static boolean existsCurrentSetup()
  {
    File branchDir = getCurrentBranchDir();
    URI uri = getSetupURI(branchDir);
    return URIConverter.INSTANCE.exists(uri, null);
  }

  public static String escape(String string)
  {
    if (string == null)
    {
      return null;
    }

    StringBuilder result = new StringBuilder();
    int previous = 0;
    for (Matcher matcher = STRING_EXPANSION_PATTERN.matcher(string); matcher.find();)
    {
      result.append(string.substring(previous, matcher.start()));
      result.append('$');
      String key = matcher.group();
      if ("$$".equals(key))
      {
        result.append("$$$");
      }
      else
      {
        result.append(key);
      }
      previous = matcher.end();
    }

    result.append(string.substring(previous));
    return result.toString();
  }

  static
  {
    STRING_FILTER_REGISTRY.put("uri", new StringFilter()
    {
      public String filter(String value)
      {
        return URI.decode(URI.createFileURI(value).toString());
      }
    });

    STRING_FILTER_REGISTRY.put("upper", new StringFilter()
    {
      public String filter(String value)
      {
        return value.toUpperCase();
      }
    });

    STRING_FILTER_REGISTRY.put("lower", new StringFilter()
    {
      public String filter(String value)
      {
        return value.toLowerCase();
      }
    });

    STRING_FILTER_REGISTRY.put("cap", new StringFilter()
    {
      public String filter(String value)
      {
        return StringUtil.cap(value);
      }
    });

    STRING_FILTER_REGISTRY.put("allcap", new StringFilter()
    {
      public String filter(String value)
      {
        return StringUtil.capAll(value);
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  public interface StringFilter
  {
    public String filter(String value);
  }
}
