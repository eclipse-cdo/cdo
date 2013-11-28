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

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.EclipsePreferenceTask;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.ECFURIHandlerImpl;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.util.OS;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

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

  private static final Pattern STRING_EXPANSION_PATTERN = Pattern.compile("\\$\\{([^${}|]+)(\\|([^}]+))?}");

  private static final Map<String, StringFilter> STRING_FILTER_REGISTRY = new HashMap<String, StringFilter>();

  private static final long serialVersionUID = 1L;

  private Trigger trigger;

  private File branchDir;

  private Setup setup;

  private Preferences preferences;

  private boolean performing;

  private Set<String> restartReasons = new LinkedHashSet<String>();

  private URIConverter uriConverter = new ExtensibleURIConverterImpl();

  private AbstractSetupTaskContext(Trigger trigger)
  {
    this.trigger = trigger;

    uriConverter.getURIHandlers().add(4, new ECFURIHandlerImpl());
  }

  protected AbstractSetupTaskContext(Trigger trigger, Setup setup)
  {
    this(trigger);

    Branch branch = setup.getBranch();
    Project project = branch.getProject();

    String branchFolder = branch.getName().toLowerCase();
    String projectFolder = project.getName().toLowerCase();
    branchDir = new File("/${" + KEY_INSTALL_DIR + '}', projectFolder + "/" + branchFolder).getAbsoluteFile();

    initialize(setup, false);
  }

  protected AbstractSetupTaskContext(Trigger trigger, File branchDir)
  {
    this(trigger);

    this.branchDir = branchDir;

    ResourceSet resourceSet = EMFUtil.createResourceSet();

    URI uri = URI.createFileURI(branchDir.toString() + "/setup.xmi");
    Resource resource = EMFUtil.loadResourceSafely(resourceSet, uri);
    initialize((Setup)resource.getContents().get(0), true);
  }

  private void initialize(Setup setup, boolean considerNetworkPreferences)
  {
    this.setup = setup;
    preferences = setup.getPreferences();

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

      int prefixIndex = key.indexOf('/');
      if (prefixIndex != -1)
      {
        key = key.substring(0, prefixIndex);
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
        String filters = matcher.group(3);
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
    return uriConverter.normalize(uri);
  }

  public URIConverter getURIConverter()
  {
    return uriConverter;
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
    return new File(preferences.getBundlePoolFolder());
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
    return new File(branchDir, "eclipse");
  }

  public File getWorkspaceDir()
  {
    return new File(branchDir, "ws");
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
