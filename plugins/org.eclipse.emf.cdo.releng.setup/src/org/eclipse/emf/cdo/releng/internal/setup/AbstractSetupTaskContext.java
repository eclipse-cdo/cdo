/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
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
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.OS;
import org.eclipse.emf.cdo.releng.setup.util.SetupResource;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import java.io.File;
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
  private static boolean NEEDS_PATH_SEPARATOR_CONVERSION = File.separatorChar == '\\';

  private static final Pattern STRING_EXPANSION_PATTERN = Pattern.compile("\\$(\\{([^${}|]+)(\\|([^}]+))?}|\\$)");

  private static final Map<String, StringFilter> STRING_FILTER_REGISTRY = new HashMap<String, StringFilter>();

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
    if (resource.getToolVersion() > SetupTaskMigrator.TOOL_VERSION)
    {
      Runnable postInstall = new Runnable()
      {
        public void run()
        {

        }
      };

      Runnable restartHandler = new Runnable()
      {
        public void run()
        {

        }
      };

      if (UpdateUtil.update(UIUtil.getShell(), true, postInstall, restartHandler))
      {
        throw new UpdatingException();
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

    put(KEY_INSTALL_DIR, getInstallDir());
    put(KEY_P2_POOL_DIR, getP2PoolDir());
    put(KEY_P2_POOL_TP_DIR, getP2PoolTPDir());
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

    for (Map.Entry<Object, Object> entry : System.getProperties().entrySet())
    {
      put(entry.getKey(), entry.getValue());
    }
  }

  public Trigger getTrigger()
  {
    return trigger;
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
    return expandString(string, null);
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

  public String expandString(String string, Set<String> keys)
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
          if (keys != null)
          {
            unresolved = true;
            keys.add(key);
          }
          else
          {
            result.append(matcher.group());
          }
        }
        else
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

  public String getP2ProfileName()
  {
    String profileName = getBranchDir().toString();
    profileName = profileName.replace(':', '_');
    profileName = profileName.replace('/', '_');
    profileName = profileName.replace('\\', '_');
    return profileName;
  }

  public File getP2ProfileDir()
  {
    return new File(getP2AgentDir(), "org.eclipse.equinox.p2.engine/profileRegistry/" + getP2ProfileName() + ".profile");
  }

  public File getP2AgentDir()
  {
    return new File(getP2PoolDir(), "p2");
  }

  public File getP2PoolDir()
  {
    String bundlePoolFolder = preferences.getBundlePoolFolder();
    if (StringUtil.isEmpty(bundlePoolFolder))
    {
      return new File(getInstallDir(), ".p2pool-ide");
    }

    return new File(bundlePoolFolder);
  }

  public File getP2PoolTPDir()
  {
    String bundlePoolFolderTP = preferences.getBundlePoolFolderTP();
    if (StringUtil.isEmpty(bundlePoolFolderTP))
    {
      return new File(getInstallDir(), ".p2pool-tp");
    }

    return new File(bundlePoolFolderTP);
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

  protected String lookup(String key)
  {
    Object object = get(key);
    if (object != null)
    {
      return object.toString();
    }

    return null;
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

  public static void main(String[] args)
  {
    for (String value : new String[] { "$${foo}", "${foo|uri}", "${bar}", "$$", "{$$}", "${$foo}}", "${${foo}}",
        "${${poo}}", "$a$b" })
    {
      Set<String> keys = new HashSet<String>();
      AbstractSetupTaskContext context = new AbstractSetupTaskContext(Trigger.BOOTSTRAP)
      {
        private static final long serialVersionUID = 1L;

        {
          put("foo", "d:/stuff/junk");
          put("poo", "foo");
        }

        public void log(Throwable t)
        {
        }

        public void log(IStatus status)
        {
        }

        public void log(String line)
        {
        }

        public boolean isCancelled()
        {
          return false;
        }
      };

      String expandedString = context.expandString(value, keys);
      System.err.println("'" + value + "' -> '" + expandedString + "' -> '" + context.expandString(value) + "' -> "
          + context.getVariables(value) + " ->" + keys);

      System.err.println("  '" + value + "' -> '" + escape(value) + "' -> '" + context.expandString(escape(value)));
    }
  }
}
