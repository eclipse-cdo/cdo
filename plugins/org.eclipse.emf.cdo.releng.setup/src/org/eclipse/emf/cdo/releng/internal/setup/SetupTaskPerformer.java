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

import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressDialog;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.ConfigurableItem;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogFilter;
import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLogRunnable;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class SetupTaskPerformer extends AbstractSetupTaskContext
{
  private static final long serialVersionUID = 1L;

  private static ProgressLog progress;

  private EList<SetupTask> triggeredSetupTasks;

  private Map<EObject, EObject> copyMap;

  private EList<SetupTask> neededSetupTasks;

  private List<String> logMessageBuffer;

  private PrintStream logStream;

  private ProgressLogFilter logFilter = new ProgressLogFilter();

  private List<EStructuralFeature.Setting> unresolvedSettings = new ArrayList<EStructuralFeature.Setting>();

  private List<ContextVariableTask> unresolvedVariables = new ArrayList<ContextVariableTask>();

  private List<ContextVariableTask> resolvedVariables = new ArrayList<ContextVariableTask>();

  private Set<String> undeclaredVariables = new HashSet<String>(); // TODO Should these be called "Unspecified"?

  public SetupTaskPerformer(Trigger trigger, Setup setup)
  {
    super(trigger, setup);
    initTriggeredSetupTasks();
  }

  public SetupTaskPerformer(File branchDir)
  {
    super(Trigger.BOOTSTRAP, branchDir);
    initialize();
  }

  public SetupTaskPerformer(boolean manual) throws Exception
  {
    super(manual ? Trigger.MANUAL : Trigger.STARTUP, new File(ResourcesPlugin.getWorkspace().getRoot().getLocation()
        .removeLastSegments(1).toOSString()).getCanonicalFile());
    initialize();
  }

  private void initialize()
  {
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

  public EList<SetupTask> getTriggeredSetupTasks()
  {
    return triggeredSetupTasks;
  }

  public Map<EObject, EObject> getCopyMap()
  {
    return copyMap;
  }

  private void initTriggeredSetupTasks()
  {
    Setup setup = getSetup();
    Trigger trigger = getTrigger();

    EList<SetupTask> setupTasks = setup.getSetupTasks(true, trigger);
    triggeredSetupTasks = setupTasks; // Debugging help

    if (!setupTasks.isEmpty())
    {
      Map<SetupTask, SetupTask> substitutions = getSubstitutions(setupTasks);
      setSetup(copySetup(setupTasks, substitutions));

      Set<String> keys = new HashSet<String>();
      for (SetupTask setupTask : setupTasks)
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

      reorderSetupTasks(setupTasks);
      expandStrings(setupTasks);

      for (Iterator<SetupTask> it = setupTasks.iterator(); it.hasNext();)
      {
        SetupTask setupTask = it.next();
        if (setupTask instanceof ContextVariableTask)
        {
          ContextVariableTask contextVariableTask = (ContextVariableTask)setupTask;
          if (!contextVariableTask.isStringSubstitution())
          {
            if (!unresolvedVariables.contains(contextVariableTask))
            {
              resolvedVariables.add(contextVariableTask);
            }

            it.remove();
          }
        }
      }
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

  public void task(SetupTask setupTask)
  {
    if (progress instanceof ProgressDialog)
    {
      ((ProgressDialog)progress).task(setupTask);
    }
  }

  public void log(IStatus status)
  {
    log(ProgressDialog.toString(status));
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
        logStream.println("[" + ProgressDialog.DATE_TIME.format(new Date()) + "] " + line);
        logStream.flush();
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    progress.log(line);
  }

  public List<ContextVariableTask> getUnresolvedVariables()
  {
    return unresolvedVariables;
  }

  public List<ContextVariableTask> getResolvedVariables()
  {
    return resolvedVariables;
  }

  public Set<String> getUndeclaredVariables()
  {
    return undeclaredVariables;
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
        boolean found = false;
        for (SetupTask setupTask : orderedSetupTasks)
        {
          if (setupTask instanceof ContextVariableTask)
          {
            ContextVariableTask contextVariableTask = (ContextVariableTask)setupTask;
            if (key.equals(contextVariableTask.getName()))
            {
              unresolvedVariables.add(contextVariableTask);
              found = true;
              break;
            }
          }
        }

        if (!found)
        {
          undeclaredVariables.add(key);
        }
      }
    }
  }

  public void resolveSettings()
  {
    EList<SetupTask> setupTasks = getPreferences().getSetupTasks();
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
      if (setting.getEStructuralFeature().isMany())
      {
        @SuppressWarnings("unchecked")
        List<String> values = (List<String>)setting.get(false);
        for (ListIterator<String> it = values.listIterator(); it.hasNext();)
        {
          it.set(expandString(it.next()));
        }
      }
      else
      {
        setting.set(expandString((String)setting.get(false)));
      }
    }
  }

  private void expand(Set<String> keys, List<EStructuralFeature.Setting> unresolvedVariables, EObject eObject)
  {
    EClass eClass = eObject.eClass();
    for (EAttribute attribute : eClass.getEAllAttributes())
    {
      if (attribute.isChangeable() && attribute.getEAttributeType().getInstanceClassName() == "java.lang.String"
          && attribute != SetupPackage.Literals.CONTEXT_VARIABLE_TASK__NAME)
      {
        if (attribute.isMany())
        {
          @SuppressWarnings("unchecked")
          List<String> values = (List<String>)eObject.eGet(attribute);
          List<String> newValues = new ArrayList<String>();
          boolean failed = false;
          for (String value : values)
          {
            String newValue = expandString(value, keys);
            if (newValue == null)
            {
              if (!failed)
              {
                unresolvedVariables.add(((InternalEObject)eObject).eSetting(attribute));
                failed = true;
              }
            }
            else
            {
              newValues.add(newValue);
            }
          }

          if (!failed)
          {
            eObject.eSet(attribute, newValues);
          }
        }
        else
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
  }

  public void perform() throws Exception
  {
    if (!undeclaredVariables.isEmpty())
    {
      throw new RuntimeException("Missing variables for " + undeclaredVariables);
    }

    perform(triggeredSetupTasks);
  }

  private void perform(EList<SetupTask> setupTasks) throws Exception
  {
    final EList<SetupTask> neededTasks = initNeededTasks(setupTasks);
    if (neededTasks.isEmpty())
    {
      return;
    }

    setPerforming(true);

    if (SetupConstants.SETUP_IDE && getTrigger() != Trigger.MANUAL)
    {
      // Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
      Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
      ProgressDialog.run(shell, new ProgressLogRunnable()
      {
        public Set<String> run(ProgressLog log) throws Exception
        {
          doPerform(neededTasks);
          return getRestartReasons();
        }
      }, Collections.singletonList(this));
    }
    else
    {
      doPerform(neededTasks);
    }
  }

  public static boolean disableAutoBuilding() throws CoreException
  {
    boolean autoBuilding = ResourcesPlugin.getWorkspace().isAutoBuilding();
    if (autoBuilding)
    {
      restoreAutoBuilding(false);
    }

    return autoBuilding;
  }

  public static void restoreAutoBuilding(boolean autoBuilding) throws CoreException
  {
    if (autoBuilding != ResourcesPlugin.getWorkspace().isAutoBuilding())
    {
      IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
      description.setAutoBuilding(autoBuilding);

      ResourcesPlugin.getWorkspace().setDescription(description);
    }
  }

  private void doPerform(final EList<SetupTask> neededTasks) throws Exception
  {
    if (getTrigger() == Trigger.BOOTSTRAP)
    {
      doPerformHelper(neededTasks);
    }
    else
    {
      ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
      {
        public void run(IProgressMonitor monitor) throws CoreException
        {
          try
          {
            doPerformHelper(neededTasks);
          }
          catch (Exception ex)
          {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex));
          }
        }
      }, null, IWorkspace.AVOID_UPDATE, null);
    }
  }

  private void doPerformHelper(EList<SetupTask> neededTasks) throws Exception
  {
    Boolean autoBuilding = null;
    try
    {
      if (getTrigger() != Trigger.BOOTSTRAP)
      {
        autoBuilding = disableAutoBuilding();
      }

      Branch branch = getSetup().getBranch();
      log("Setting up " + branch.getProject().getName() + " " + branch.getName());

      for (SetupTask neededTask : neededTasks)
      {
        task(neededTask);
        log("Performing setup task " + getLabel(neededTask));
        neededTask.perform(this);
        neededTask.dispose();
      }

      Resource preferencesResource = getPreferences().eResource();
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

      if (autoBuilding != null)
      {
        restoreAutoBuilding(autoBuilding);
      }
    }
  }

  private Map<SetupTask, SetupTask> getSubstitutions(EList<SetupTask> setupTasks)
  {
    Map<Object, SetupTask> overrides = new HashMap<Object, SetupTask>();
    Map<SetupTask, SetupTask> substitutions = new LinkedHashMap<SetupTask, SetupTask>();

    for (SetupTask setupTask : setupTasks)
    {
      Object overrideToken = setupTask.getOverrideToken();
      SetupTask overriddenTask = overrides.put(overrideToken, setupTask);
      if (overriddenTask != null)
      {
        substitutions.put(overriddenTask, setupTask);
      }
    }

    return substitutions;
  }

  private Setup copySetup(EList<SetupTask> setupTasks, Map<SetupTask, SetupTask> substitutions)
  {
    Setup setup = getSetup();
    Set<EObject> roots = new LinkedHashSet<EObject>();
    roots.add(setup);

    for (EObject eObject : setup.eCrossReferences())
    {
      EObject rootContainer = EcoreUtil.getRootContainer(eObject);
      roots.add(rootContainer);
    }

    EcoreUtil.Copier copier = new EcoreUtil.Copier();
    setup = (Setup)copier.copyAll(roots).iterator().next();

    // Shorten the paths through the substitutions map
    Map<SetupTask, SetupTask> directSubstitutions = new HashMap<SetupTask, SetupTask>(substitutions);
    for (Map.Entry<SetupTask, SetupTask> entry : directSubstitutions.entrySet())
    {
      SetupTask task = entry.getValue();

      for (;;)
      {
        SetupTask overridingTask = directSubstitutions.get(task);
        if (overridingTask == null)
        {
          break;
        }

        entry.setValue(overridingTask);
        task = overridingTask;
      }
    }

    HashMap<EObject, EObject> originalCopier = new HashMap<EObject, EObject>(copier);
    for (Map.Entry<SetupTask, SetupTask> entry : directSubstitutions.entrySet())
    {
      SetupTask overriddenTask = entry.getKey();
      SetupTask overridingTask = entry.getValue();

      EObject copy = copier.get(overridingTask);
      copier.put(overriddenTask, copy);
    }

    copyMap = copier;

    copier.copyReferences();

    // Determine all the copied objects for which the original object is directly contained in a resource.
    // For each such resource, create a copy of that resource.
    Map<Resource, Resource> resourceCopies = new HashMap<Resource, Resource>();
    @SuppressWarnings("unchecked")
    Set<InternalEObject> originals = (Set<InternalEObject>)(Set<?>)copier.keySet();
    for (InternalEObject original : originals)
    {
      Internal resource = original.eDirectResource();
      if (resource != null)
      {
        Resource newResource = resourceCopies.get(resource);
        if (newResource == null)
        {
          URI uri = resource.getURI();
          newResource = resource.getResourceSet().getResourceFactoryRegistry().getFactory(uri).createResource(uri);
          resourceCopies.put(resource, newResource);
        }
      }
    }

    // Perform override merging.
    for (Map.Entry<SetupTask, SetupTask> entry : substitutions.entrySet())
    {
      SetupTask overriddenSetupTask = (SetupTask)originalCopier.get(entry.getKey());
      SetupTask setupTask = (SetupTask)originalCopier.get(entry.getValue());
      setupTask.overrideFor(overriddenSetupTask);
    }

    // For each original resource, ensure that the copied resource contains the either the corresponding copies or
    // a placeholder object.
    //
    for (Map.Entry<Resource, Resource> entry : resourceCopies.entrySet())
    {
      Resource originalResource = entry.getKey();
      Resource copyResource = entry.getValue();
      EList<EObject> copyResourceContents = copyResource.getContents();
      for (EObject eObject : originalResource.getContents())
      {
        EObject copy = copier.get(eObject);
        if (copy == null)
        {
          copy = EcoreFactory.eINSTANCE.createEObject();
        }

        copyResourceContents.add(copy);
      }
    }

    for (ListIterator<SetupTask> it = setupTasks.listIterator(); it.hasNext();)
    {
      SetupTask setupTask = it.next();
      if (directSubstitutions.containsKey(setupTask))
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

  public EList<SetupTask> getNeededTasks()
  {
    return neededSetupTasks;
  }

  private EList<SetupTask> initNeededTasks(EList<SetupTask> setupTasks) throws Exception
  {
    neededSetupTasks = new BasicEList<SetupTask>();

    for (Iterator<SetupTask> it = setupTasks.iterator(); it.hasNext();)
    {
      SetupTask setupTask = it.next();
      if (setupTask.isNeeded(this))
      {
        neededSetupTasks.add(setupTask);
      }
      else
      {
        setupTask.dispose();
      }
    }

    return neededSetupTasks;
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

  private void reorderSetupTasks(EList<SetupTask> setupTasks)
  {
    reorder(setupTasks, new DependencyProvider<SetupTask>()
    {
      public Collection<SetupTask> getDependencies(SetupTask setupTask)
      {
        return setupTask.getRequirements();
      }
    });
  }

  private static String getLabel(SetupTask setupTask)
  {
    IItemLabelProvider labelProvider = (IItemLabelProvider)EMFUtil.ADAPTER_FACTORY.adapt(setupTask,
        IItemLabelProvider.class);
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
      // TODO Consider basing this on a provider that just returns a boolean based on "does v1 depend on v2".
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

  public interface DependencyProvider<T>
  {
    Collection<? extends T> getDependencies(T value);
  }
}
