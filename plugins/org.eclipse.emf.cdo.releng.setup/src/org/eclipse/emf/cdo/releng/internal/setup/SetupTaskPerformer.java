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

import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressLogDialog;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.ConfigurableItem;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.util.OS;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogFilter;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class SetupTaskPerformer extends HashMap<Object, Object> implements SetupTaskContext
{
  public static final String RELENG_URL = System.getProperty("releng.url",
      "http://download.eclipse.org/modeling/emf/cdo/updates/integration").replace('\\', '/');

  private static boolean NEEDS_PATH_SEPARATOR_CONVERSION = File.separatorChar == '\\';

  private static final ComposedAdapterFactory ADAPTER_FACTORY = new ComposedAdapterFactory(
      EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());

  private static final Pattern STRING_EXPANSION_PATTERN = Pattern.compile("\\$\\{([^${}|]+)(\\|([^}]+))?}");

  private static final Map<String, StringFilter> STRING_FILTER_REGISTRY = new HashMap<String, StringFilter>();

  private static final long serialVersionUID = 1L;

  private static ProgressLog progress;

  private Trigger trigger;

  private File branchDir;

  private Setup setup;

  private Preferences preferences;

  private EList<SetupTask> triggeredSetupTasks;

  private boolean performing;

  private Set<String> restartReasons = new LinkedHashSet<String>();

  private List<String> logMessageBuffer;

  private PrintStream logStream;

  private ProgressLogFilter logFilter = new ProgressLogFilter();

  private URIConverter uriConverter = new ExtensibleURIConverterImpl();

  private List<ContextVariableTask> unresolvedVariables = new ArrayList<ContextVariableTask>();

  private List<EStructuralFeature.Setting> unresolvedSettings = new ArrayList<EStructuralFeature.Setting>();

  public SetupTaskPerformer(File branchDir)
  {
    trigger = Trigger.BOOTSTRAP;
    this.branchDir = branchDir;

    initialize();
  }

  public SetupTaskPerformer(boolean manual) throws Exception
  {
    trigger = manual ? Trigger.MANUAL : Trigger.STARTUP;

    IPath branchDirPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().removeLastSegments(1);
    branchDir = new File(branchDirPath.toOSString()).getCanonicalFile();

    initialize();
  }

  public void log(String line)
  {
    if (progress != null)
    {
      if (logMessageBuffer != null)
      {
        for (String value : logMessageBuffer)
        {
          doLog(value);
        }

        logMessageBuffer = null;
      }

      doLog(line);
    }
    else
    {
      if (logMessageBuffer == null)
      {
        logMessageBuffer = new ArrayList<String>();
      }

      logMessageBuffer.add(line);
    }
  }

  private void doLog(String line)
  {
    line = logFilter.filter(line);
    if (line == null)
    {
      return;
    }

    if (logStream != null)
    {
      try
      {
        logStream.println("[" + ProgressLogDialog.DATE_TIME.format(new Date()) + "] " + line);
        logStream.flush();
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    progress.log(line);
  }

  public void log(IStatus status)
  {
    log(ProgressLogDialog.toString(status));
  }

  public void task(SetupTask setupTask)
  {
    if (progress instanceof ProgressLogDialog)
    {
      ((ProgressLogDialog)progress).task(setupTask);
    }
  }

  public boolean isCancelled()
  {
    if (progress != null)
    {
      return progress.isCancelled();
    }

    return false;
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

  public void redirect(URI sourceURI, URI targetURI)
  {
    uriConverter.getURIMap().put(sourceURI, targetURI);
  }

  public URI redirect(URI uri)
  {
    return uriConverter.normalize(uri);
  }

  public OS getOS()
  {
    return OS.INSTANCE;
  }

  public String getP2ProfileName()
  {
    Branch branch = setup.getBranch();
    Project project = branch.getProject();

    String profileName = project.getName() + "_" + branch.getName();
    profileName = profileName.replace('.', '_');
    profileName = profileName.replace('-', '_');
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
    return new File(getInstallDir(), ".p2pool-ide");
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

  // TODO Is this Bucky-specific?
  public File getTargetPlatformDir()
  {
    return new File(branchDir, "tp");
  }

  public File getWorkspaceDir()
  {
    return new File(branchDir, "ws");
  }

  public Setup getSetup()
  {
    return setup;
  }

  public EList<SetupTask> getTriggeredSetupTasks()
  {
    return triggeredSetupTasks;
  }

  private void initTriggeredSetupTasks()
  {
    triggeredSetupTasks = setup.getSetupTasks(true, trigger);
    if (!triggeredSetupTasks.isEmpty())
    {

      Map<SetupTask, SetupTask> substitutions = getSubstitutions(triggeredSetupTasks);
      setup = copySetup(triggeredSetupTasks, substitutions);

      Set<String> keys = new HashSet<String>();
      for (SetupTask setupTask : triggeredSetupTasks)
      {
        if (setupTask instanceof ContextVariableTask)
        {
          ContextVariableTask contextVariableTask = (ContextVariableTask)setupTask;
          String name = contextVariableTask.getName();
          keys.add(name);
          String value = contextVariableTask.getValue();
          put(name, value);
        }
      }

      Map<String, Set<String>> variables = new HashMap<String, Set<String>>();
      for (Map.Entry<Object, Object> entry : entrySet())
      {
        Object entryKey = entry.getKey();
        if (keys.contains(entryKey))
        {
          Object entryValue = entry.getValue();
          if (entryKey instanceof String && entryValue != null)
          {
            String key = (String)entryKey;
            String value = entryValue.toString();
            variables.put(key, getVariables(value));
          }
        }
      }

      EList<Map.Entry<String, Set<String>>> orderedVariables = reorderVariables(variables);

      for (Map.Entry<String, Set<String>> entry : orderedVariables)
      {
        String key = entry.getKey();
        Object object = get(key);
        if (object != null)
        {
          String value = expandString(object.toString());
          put(key, value);
        }
      }

      reorder(triggeredSetupTasks);

      expandStrings(triggeredSetupTasks);

      for (Iterator<SetupTask> it = triggeredSetupTasks.iterator(); it.hasNext();)
      {
        SetupTask setupTask = it.next();
        if (setupTask instanceof ContextVariableTask)
        {
          ContextVariableTask contextVariableTask = (ContextVariableTask)setupTask;
          if (!contextVariableTask.isStringSubstitution())
          {
            it.remove();
          }
        }
      }
    }
  }

  public List<ContextVariableTask> getUnresolvedVariables()
  {
    return unresolvedVariables;
  }

  private void expandStrings(EList<SetupTask> orderedSetupTasks)
  {
    Set<String> keys = new HashSet<String>();
    for (SetupTask setupTask : orderedSetupTasks)
    {
      expand(keys, unresolvedSettings, setupTask);
      for (Iterator<EObject> it = setupTask.eAllContents(); it.hasNext();)
      {
        expand(keys, unresolvedSettings, it.next());
      }
    }

    if (!unresolvedSettings.isEmpty())
    {
      for (String key : keys)
      {
        for (SetupTask setupTask : orderedSetupTasks)
        {
          if (setupTask instanceof ContextVariableTask)
          {
            ContextVariableTask contextVariableTask = (ContextVariableTask)setupTask;
            if (key.equals(contextVariableTask.getName()))
            {
              unresolvedVariables.add(contextVariableTask);
            }
          }
        }
      }
    }
  }

  public void resolveSettings()
  {
    EList<SetupTask> setupTasks = preferences.getSetupTasks();
    for (ContextVariableTask contextVariableTask : unresolvedVariables)
    {
      put(contextVariableTask.getName(), contextVariableTask.getValue());
      ContextVariableTask userPreference = SetupFactory.eINSTANCE.createContextVariableTask();
      userPreference.setName(contextVariableTask.getName());
      userPreference.setValue(contextVariableTask.getValue());
      for (EObject container = contextVariableTask.eContainer(); container != null; container = container.eContainer())
      {
        if (container instanceof ConfigurableItem)
        {
          userPreference.getRestrictions().add((ConfigurableItem)container);
          break;
        }
      }

      setupTasks.add(userPreference);
    }

    for (EStructuralFeature.Setting setting : unresolvedSettings)
    {
      setting.set(expandString((String)setting.get(false)));
    }

  }

  private void expand(Set<String> keys, List<EStructuralFeature.Setting> unresolvedVariables, EObject eObject)
  {
    EClass eClass = eObject.eClass();
    for (EAttribute attribute : eClass.getEAllAttributes())
    {
      if (attribute.getEAttributeType().getInstanceClassName() == "java.lang.String"
          && attribute != SetupPackage.Literals.CONTEXT_VARIABLE_TASK__NAME)
      {
        String value = (String)eObject.eGet(attribute);
        if (value != null)
        {
          String newValue = expandString(value, keys);
          if (newValue == null)
          {
            unresolvedVariables.add(((InternalEObject)eObject).eSetting(attribute));
          }
          else if (!value.equals(newValue))
          {
            eObject.eSet(attribute, newValue);
          }
        }
      }
    }
  }

  public void perform() throws Exception
  {
    perform(triggeredSetupTasks);
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

  private void initialize()
  {
    ResourceSet resourceSet = EMFUtil.createResourceSet();

    URI uri = URI.createFileURI(branchDir.toString() + "/setup.xmi");
    Resource resource = EMFUtil.loadResourceSafely(resourceSet, uri);

    setup = (Setup)resource.getContents().get(0);
    preferences = setup.getPreferences();
    preferences.eResource().setTrackingModification(true);

    Branch branch = setup.getBranch();
    String branchName = branch.getName();

    Project project = branch.getProject();
    String projectLabel = project.getLabel();
    String projectName = project.getName();

    put("setup.git.prefix", preferences.getGitPrefix());
    put("setup.install.dir", getInstallDir());
    put("setup.project.dir", getProjectDir());
    put("setup.branch.dir", getBranchDir());
    put("setup.eclipse.dir", getEclipseDir());
    put("setup.tp.dir", getTargetPlatformDir());
    put("setup.ws.dir", getWorkspaceDir());
    put("setup.project.label", projectLabel);
    put("setup.project.name", projectName);
    put("setup.branch.name", branchName);
    put("releng.url", RELENG_URL);

    put("os", Platform.getOS());
    put("os.arch", Platform.getOSArch());
    put("ws", Platform.getWS());

    for (Map.Entry<Object, Object> entry : System.getProperties().entrySet())
    {
      put(entry.getKey(), entry.getValue());
    }

    try
    {
      File logFile = new File(getBranchDir(), "setup.log");
      logFile.getParentFile().mkdirs();

      FileOutputStream out = new FileOutputStream(logFile, true);
      logStream = new PrintStream(out);
    }
    catch (FileNotFoundException ex)
    {
      throw new RuntimeException(ex);
    }

    initTriggeredSetupTasks();
  }

  private EList<Map.Entry<String, Set<String>>> reorderVariables(final Map<String, Set<String>> variables)
  {
    EList<Map.Entry<String, Set<String>>> list = new BasicEList<Map.Entry<String, Set<String>>>(variables.entrySet());

    reorder(list, new DependencyProvider<Map.Entry<String, Set<String>>>()
    {
      public Collection<Map.Entry<String, Set<String>>> getDependencies(Map.Entry<String, Set<String>> variable)
      {
        Collection<Map.Entry<String, Set<String>>> result = new ArrayList<Map.Entry<String, Set<String>>>();
        for (String key : variable.getValue())
        {
          for (Map.Entry<String, Set<String>> entry : variables.entrySet())
          {
            if (entry.getKey().equals(key))
            {
              result.add(entry);
            }
          }
        }

        return result;
      }
    });

    return list;
  }

  private void reorder(EList<SetupTask> setupTasks)
  {
    reorder(setupTasks, new DependencyProvider<SetupTask>()
    {
      public Collection<SetupTask> getDependencies(SetupTask setupTask)
      {
        return setupTask.getRequirements();
      }
    });
  }

  public interface DependencyProvider<T>
  {
    Collection<? extends T> getDependencies(T value);
  }

  public static <T> void reorder(EList<T> values, DependencyProvider<T> dependencyProvider)
  {
    for (int i = 0, size = values.size(), count = 0; i < size; ++i)
    {
      T value = values.get(i);
      if (count == size)
      {
        throw new IllegalArgumentException("Circular dependencies " + value);
      }

      boolean changed = false;
      for (T dependency : dependencyProvider.getDependencies(value))
      {
        int index = values.indexOf(dependency);
        if (index > i)
        {
          values.move(i, index);
          changed = true;
        }
      }

      if (changed)
      {
        --i;
        ++count;
      }
      else
      {
        count = 0;
      }
    }
  }

  private void perform(EList<SetupTask> setupTasks) throws Exception
  {
    final EList<SetupTask> neededTasks = getNeededTasks(setupTasks);
    if (neededTasks.isEmpty())
    {
      return;
    }

    performing = true;

    if (Activator.SETUP_IDE && trigger != Trigger.MANUAL)
    {
      Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
      ProgressLogDialog.run(shell, new ProgressLogRunnable()
      {
        public Set<String> run(ProgressLog log) throws Exception
        {
          doPerform(neededTasks);
          return restartReasons;
        }
      }, Collections.singletonList(this));
    }
    else
    {
      doPerform(neededTasks);
    }
  }

  private void doPerform(EList<SetupTask> neededTasks) throws Exception
  {
    try
    {
      Branch branch = setup.getBranch();
      log("Setting up " + branch.getProject().getName() + " " + branch.getName());

      for (SetupTask neededTask : neededTasks)
      {
        task(neededTask);
        log("Performing setup task " + getLabel(neededTask));
        neededTask.perform(this);
        neededTask.dispose();
      }

      Resource preferencesResource = preferences.eResource();
      if (preferencesResource.isModified())
      {
        preferencesResource.save(null);
      }
    }
    finally
    {
      if (logStream != null)
      {
        logStream.println();
        logStream.println();
        logStream.println();
        logStream.println();
        IOUtil.closeSilent(logStream);
      }
    }
  }

  private Map<SetupTask, SetupTask> getSubstitutions(EList<SetupTask> setupTasks)
  {
    Map<Object, SetupTask> overrides = new HashMap<Object, SetupTask>();
    Map<SetupTask, SetupTask> substitutions = new HashMap<SetupTask, SetupTask>();

    for (SetupTask setupTask : setupTasks)
    {
      Object overrideToken = setupTask.getOverrideToken();
      SetupTask overriddenTask = overrides.put(overrideToken, setupTask);
      if (overriddenTask != null)
      {
        substitutions.put(overriddenTask, setupTask);
      }
    }

    // Shorten the paths through the substitutions map
    for (Map.Entry<SetupTask, SetupTask> entry : substitutions.entrySet())
    {
      SetupTask task = entry.getValue();

      for (;;)
      {
        SetupTask overridingTask = substitutions.get(task);
        if (overridingTask == null)
        {
          break;
        }

        entry.setValue(overridingTask);
      }
    }

    return substitutions;
  }

  private Setup copySetup(EList<SetupTask> setupTasks, Map<SetupTask, SetupTask> substitutions)
  {
    Set<EObject> roots = new LinkedHashSet<EObject>();
    roots.add(setup);

    for (EObject eObject : setup.eCrossReferences())
    {
      EObject rootContainer = EcoreUtil.getRootContainer(eObject);
      roots.add(rootContainer);
    }

    EcoreUtil.Copier copier = new EcoreUtil.Copier();
    Setup setup = (Setup)copier.copyAll(roots).iterator().next();

    for (Map.Entry<SetupTask, SetupTask> entry : substitutions.entrySet())
    {
      SetupTask overriddenTask = entry.getKey();
      SetupTask overridingTask = entry.getValue();

      EObject copy = copier.get(overridingTask);
      copier.put(overriddenTask, copy);
    }

    copier.copyReferences();

    for (ListIterator<SetupTask> it = setupTasks.listIterator(); it.hasNext();)
    {
      SetupTask setupTask = it.next();
      if (substitutions.containsKey(setupTask))
      {
        it.remove();
      }
      else
      {
        SetupTask copy = (SetupTask)copier.get(setupTask);
        it.set(copy);
      }
    }

    return setup;
  }

  private EList<SetupTask> getNeededTasks(EList<SetupTask> setupTasks) throws Exception
  {
    EList<SetupTask> result = new BasicEList<SetupTask>();

    for (Iterator<SetupTask> it = setupTasks.iterator(); it.hasNext();)
    {
      SetupTask setupTask = it.next();
      if (setupTask.isNeeded(this))
      {
        result.add(setupTask);
      }
      else
      {
        setupTask.dispose();
      }
    }

    return result;
  }

  private static String getLabel(SetupTask setupTask)
  {
    IItemLabelProvider labelProvider = (IItemLabelProvider)ADAPTER_FACTORY.adapt(setupTask, IItemLabelProvider.class);
    String type;
    try
    {
      Method getTypeTextMethod = ReflectUtil.getMethod(labelProvider.getClass(), "getTypeText", Object.class);
      getTypeTextMethod.setAccessible(true);
      type = getTypeTextMethod.invoke(labelProvider, setupTask).toString();
    }
    catch (Exception ex)
    {
      type = setupTask.eClass().getName();
    }
    String label = labelProvider.getText(setupTask);
    return label.startsWith(type) ? label : type + " " + label;
  }

  public static ProgressLog getProgress()
  {
    return progress;
  }

  public static void setProgress(ProgressLog progress)
  {
    SetupTaskPerformer.progress = progress;
  }

  static
  {
    STRING_FILTER_REGISTRY.put("uri", new StringFilter()
    {
      public String filter(String value)
      {
        return URI.createFileURI(value).toString();
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
