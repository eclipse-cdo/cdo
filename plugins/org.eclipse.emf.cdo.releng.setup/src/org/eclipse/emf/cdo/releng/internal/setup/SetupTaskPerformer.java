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

import org.eclipse.emf.cdo.releng.internal.setup.ui.ProgressDialog;
import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.preferences.PreferenceNode;
import org.eclipse.emf.cdo.releng.preferences.util.PreferencesUtil;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.ConfigurableItem;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.ContextVariableTask;
import org.eclipse.emf.cdo.releng.setup.EclipseIniTask;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.P2Task;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.ResourceCopyTask;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.VariableType;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLog;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLogFilter;
import org.eclipse.emf.cdo.releng.setup.log.ProgressLogRunnable;
import org.eclipse.emf.cdo.releng.setup.util.SetupResource;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.metadata.VersionRange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class SetupTaskPerformer extends AbstractSetupTaskContext
{
  private static final Pattern INSTALLABLE_UNIT_WITH_RANGE_PATTERN = Pattern.compile("([^\\[\\(]*)(.*)");

  private static final long serialVersionUID = 1L;

  private static ProgressLog progress;

  private boolean canceled;

  private EList<SetupTask> triggeredSetupTasks;

  private Map<EObject, EObject> copyMap;

  private EList<SetupTask> neededSetupTasks;

  private List<String> logMessageBuffer;

  private PrintStream logStream;

  private ProgressLogFilter logFilter = new ProgressLogFilter();

  private List<EStructuralFeature.Setting> unresolvedSettings = new ArrayList<EStructuralFeature.Setting>();

  private List<ContextVariableTask> unresolvedVariables = new ArrayList<ContextVariableTask>();

  private List<ContextVariableTask> resolvedVariables = new ArrayList<ContextVariableTask>();

  private Set<String> undeclaredVariables = new HashSet<String>();

  private ComposedAdapterFactory adapterFactory = EMFUtil.createAdapterFactory();

  public SetupTaskPerformer(Trigger trigger, String installFolder, Setup setup)
  {
    super(trigger, installFolder, setup);
    initTriggeredSetupTasks();
  }

  public SetupTaskPerformer(boolean manual) throws Exception
  {
    super(manual ? Trigger.MANUAL : Trigger.STARTUP, getCurrentBranchDir().getCanonicalFile());
    initTriggeredSetupTasks();
  }

  private void initTriggeredSetupTasks()
  {
    Setup setup = getSetup();
    if (setup == null)
    {
      triggeredSetupTasks = new BasicEList<SetupTask>();
    }
    else
    {
      Trigger trigger = getTrigger();

      EList<SetupTask> allPossibleSetupTasks = setup.getSetupTasks(true, null);
      Set<EClass> eClasses = new LinkedHashSet<EClass>();
      for (SetupTask possibleSetupTask : allPossibleSetupTasks)
      {
        EClass eClass = possibleSetupTask.eClass();
        if (eClasses.add(eClass))
        {
          eClasses.addAll(eClass.getEAllSuperTypes());
        }
      }

      EList<SetupTask> setupTasks = new BasicEList<SetupTask>();
      for (EClass eClass : eClasses)
      {
        for (EAnnotation eAnnotation : eClass.getEAnnotations())
        {
          String source = eAnnotation.getSource();
          if (source != null && source.startsWith("http://www.eclipse.org/CDO/releng/setup/enablement"))
          {
            String variableName = eAnnotation.getDetails().get("variableName");
            String p2RepositoryLocation = eAnnotation.getDetails().get("repository");
            put(variableName, p2RepositoryLocation);

            P2Task p2Task = SetupFactory.eINSTANCE.createP2Task();
            EList<InstallableUnit> installableUnits = p2Task.getInstallableUnits();
            for (String installableUnitSpecification : eAnnotation.getDetails().get("installableUnits").split("\\s"))
            {
              Matcher matcher = INSTALLABLE_UNIT_WITH_RANGE_PATTERN.matcher(installableUnitSpecification);
              if (matcher.matches())
              {
                InstallableUnit installableUnit = SetupFactory.eINSTANCE.createInstallableUnit();
                installableUnit.setID(matcher.group(1));
                String versionRange = matcher.group(2);
                if (!StringUtil.isEmpty(versionRange))
                {
                  installableUnit.setVersionRange(new VersionRange(versionRange));
                }

                installableUnits.add(installableUnit);
              }
            }

            P2Repository p2Repository = SetupFactory.eINSTANCE.createP2Repository();
            p2Repository.setURL("${" + variableName + "}");
            p2Task.getP2Repositories().add(p2Repository);

            setupTasks.add(p2Task);
          }
        }
      }

      setupTasks.addAll(setup.getSetupTasks(true, trigger));
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

        expandStrings(setupTasks);
        expandRequirements(setupTasks);
        propagateRestrictionsAndRequirements(setupTasks);
        reorderSetupTasks(setupTasks);

        for (Iterator<SetupTask> it = setupTasks.iterator(); it.hasNext();)
        {
          SetupTask setupTask = it.next();
          setupTask.consolidate();
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
  }

  public EList<SetupTask> getTriggeredSetupTasks()
  {
    return triggeredSetupTasks;
  }

  public EList<SetupTask> initNeededSetupTasks() throws Exception
  {
    if (neededSetupTasks == null)
    {
      neededSetupTasks = new BasicEList<SetupTask>();

      if (!undeclaredVariables.isEmpty())
      {
        throw new RuntimeException("Missing variables for " + undeclaredVariables);
      }

      if (!unresolvedVariables.isEmpty())
      {
        if (!ProgressDialog.promptUnresolvedVariables(UIUtil.getShell(), Collections.singletonList(this)))
        {
          return neededSetupTasks;
        }

        resolveSettings();
        unresolvedVariables.clear();
      }

      if (triggeredSetupTasks != null)
      {
        for (Iterator<SetupTask> it = triggeredSetupTasks.iterator(); it.hasNext();)
        {
          SetupTask setupTask = it.next();

          try
          {
            if (setupTask.isNeeded(this))
            {
              neededSetupTasks.add(setupTask);
            }
            else
            {
              setupTask.dispose();
            }
          }
          catch (NoClassDefFoundError ex)
          {
            // Don't perform tasks that can't load their enabling dependencies
            Activator.log(ex);
          }
        }
      }
    }

    return neededSetupTasks;
  }

  public EList<SetupTask> getNeededTasks()
  {
    return neededSetupTasks;
  }

  public Map<EObject, EObject> getCopyMap()
  {
    return copyMap;
  }

  public boolean isCanceled()
  {
    if (canceled)
    {
      return true;
    }

    if (progress != null)
    {
      return progress.isCanceled();
    }

    return false;
  }

  public void setCanceled(boolean canceled)
  {
    this.canceled = canceled;
  }

  public void task(SetupTask setupTask)
  {
    if (progress instanceof ProgressDialog)
    {
      ((ProgressDialog)progress).task(setupTask);
    }
  }

  public void log(Throwable t)
  {
    log(Activator.toString(t), false);
  }

  public void log(IStatus status)
  {
    log(Activator.toString(status), false);
  }

  public void log(String line)
  {
    log(line, true);
  }

  public void log(String line, boolean filter)
  {
    if (progress != null)
    {
      if (logMessageBuffer != null)
      {
        for (String value : logMessageBuffer)
        {
          doLog(value, filter);
        }

        logMessageBuffer = null;
      }

      doLog(line, filter);
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

  private void doLog(String line, boolean filter)
  {
    if (filter)
    {
      line = logFilter.filter(line);
    }

    if (line == null)
    {
      return;
    }

    try
    {
      PrintStream logStream = getLogStream();
      logStream.println("[" + ProgressDialog.DATE_TIME.format(new Date()) + "] " + line);
      logStream.flush();
    }
    catch (Exception ex)
    {
      Activator.log(ex);
    }

    progress.log(line, filter);
  }

  private PrintStream getLogStream()
  {
    if (logStream == null)
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
    }

    return logStream;
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

  private void expandStrings(EList<SetupTask> setupTasks)
  {
    Set<String> keys = new LinkedHashSet<String>();
    for (SetupTask setupTask : setupTasks)
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
        for (SetupTask setupTask : setupTasks)
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

  private void propagateRestrictionsAndRequirements(EList<SetupTask> setupTasks)
  {
    for (SetupTask setupTask : setupTasks)
    {
      EList<ConfigurableItem> restrictions = setupTask.getRestrictions();
      for (EObject eContainer = setupTask.eContainer(); eContainer instanceof SetupTask; eContainer = eContainer
          .eContainer())
      {
        restrictions.addAll(((SetupTask)eContainer).getRestrictions());
      }

      EList<SetupTask> requirements = setupTask.getRequirements();
      for (EObject eContainer = setupTask.eContainer(); eContainer instanceof SetupTask; eContainer = eContainer
          .eContainer())
      {
        requirements.addAll(((SetupTask)eContainer).getRequirements());
      }
    }
  }

  private void expandRequirements(EList<SetupTask> setupTasks)
  {
    for (SetupTask setupTask : setupTasks)
    {
      for (ListIterator<SetupTask> it = setupTask.getRequirements().listIterator(); it.hasNext();)
      {
        SetupTask requiredSetupTask = it.next();
        if (requiredSetupTask instanceof SetupTaskContainer)
        {
          it.remove();
          for (SetupTask expandedRequiredSetupTask : ((SetupTaskContainer)requiredSetupTask).getSetupTasks())
          {
            it.add(expandedRequiredSetupTask);
            it.previous();
          }
        }
      }
    }
  }

  private CompoundSetupTask findOrCreate(AdapterFactoryItemDelegator itemDelegator, ConfigurableItem configurableItem,
      EList<SetupTask> setupTasks)
  {
    EObject eContainer = configurableItem.eContainer();
    if (eContainer instanceof ConfigurableItem)
    {
      CompoundSetupTask compoundSetupTask = findOrCreate(itemDelegator, (ConfigurableItem)eContainer, setupTasks);
      setupTasks = compoundSetupTask.getSetupTasks();
    }

    CompoundSetupTask compoundSetupTask = find(configurableItem, setupTasks);
    if (compoundSetupTask == null)
    {
      compoundSetupTask = SetupFactory.eINSTANCE.createCompoundSetupTask();
      compoundSetupTask.setName(itemDelegator.getText(configurableItem));
      compoundSetupTask.getRestrictions().add(configurableItem);

      setupTasks.add(compoundSetupTask);
    }

    return compoundSetupTask;
  }

  private CompoundSetupTask find(ConfigurableItem configurableItem, EList<SetupTask> setupTasks)
  {
    LOOP: for (SetupTask setupTask : setupTasks)
    {
      if (setupTask instanceof CompoundSetupTask)
      {
        CompoundSetupTask compoundSetupTask = (CompoundSetupTask)setupTask;
        List<ConfigurableItem> restrictions = ((InternalEList<ConfigurableItem>)compoundSetupTask.getRestrictions())
            .basicList();
        URI uri = EcoreUtil.getURI(configurableItem);
        boolean found = false;
        for (ConfigurableItem restriction : restrictions)
        {
          URI otherURI = EcoreUtil.getURI(restriction);
          if (!otherURI.equals(uri))
          {
            continue LOOP;
          }

          found = true;
        }

        if (found)
        {
          return compoundSetupTask;
        }

        compoundSetupTask = find(configurableItem, compoundSetupTask.getSetupTasks());
        if (compoundSetupTask != null)
        {
          return compoundSetupTask;
        }
      }
    }

    return null;
  }

  public void resolveSettings()
  {
    Preferences preferences = getPreferences();

    // Load the current saved state of the preferences.
    SetupResource resource = EMFUtil.loadResourceSafely(EMFUtil.createResourceSet(), preferences.eResource().getURI());
    preferences = (Preferences)resource.getContents().get(0);

    AdapterFactoryItemDelegator itemDelegator = new AdapterFactoryItemDelegator(adapterFactory);
    EList<SetupTask> setupTasks = preferences.getSetupTasks();
    for (ContextVariableTask contextVariableTask : unresolvedVariables)
    {
      put(contextVariableTask.getName(), contextVariableTask.getValue());

      // Save passwords to the secure storage
      if (contextVariableTask.getType() == VariableType.PASSWORD)
      {
        saveSecurePreference(contextVariableTask.getName(), contextVariableTask.getValue());
      }
      else
      {
        EList<SetupTask> targetSetupTasks = setupTasks;
        for (EObject container = contextVariableTask.eContainer(); container != null; container = container
            .eContainer())
        {
          if (container instanceof ConfigurableItem)
          {
            targetSetupTasks = findOrCreate(itemDelegator, (ConfigurableItem)container, setupTasks).getSetupTasks();
            break;
          }
        }

        ContextVariableTask userPreference = SetupFactory.eINSTANCE.createContextVariableTask();
        userPreference.setName(contextVariableTask.getName());
        userPreference.setValue(contextVariableTask.getValue());

        targetSetupTasks.add(userPreference);
      }
    }

    try
    {
      resource.save(null);
    }
    catch (IOException ex)
    {
      log(ex);
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
    performTriggeredSetupTasks();

    if (getTrigger() == Trigger.BOOTSTRAP)
    {
      {
        EclipseIniTask ideEnvironmentVariableTask = SetupFactory.eINSTANCE.createEclipseIniTask();
        ideEnvironmentVariableTask.setVm(true);
        ideEnvironmentVariableTask.setValue("=true");
        ideEnvironmentVariableTask.setOption("-Dorg.eclipse.emf.cdo.releng.setup.ide");
        if (ideEnvironmentVariableTask.isNeeded(this))
        {
          ideEnvironmentVariableTask.perform(this);
        }
      }

      {
        EclipseIniTask relengURLEnvironmentVariableTask = SetupFactory.eINSTANCE.createEclipseIniTask();
        relengURLEnvironmentVariableTask.setVm(true);
        relengURLEnvironmentVariableTask.setValue("="
            + redirect(URI.createURI((String)get(SetupConstants.PROP_RELENG_URL))));
        relengURLEnvironmentVariableTask.setOption("-D" + SetupConstants.PROP_RELENG_URL);
        if (relengURLEnvironmentVariableTask.isNeeded(this))
        {
          relengURLEnvironmentVariableTask.perform(this);
        }
      }

      {
        EclipseIniTask setupURIEnvironmentVariableTask = SetupFactory.eINSTANCE.createEclipseIniTask();
        setupURIEnvironmentVariableTask.setVm(true);
        setupURIEnvironmentVariableTask.setValue("=" + redirect(EMFUtil.ECLIPSE_INDEX_URI));
        setupURIEnvironmentVariableTask.setOption("-D" + SetupConstants.PROP_SETUP_URI);
        if (setupURIEnvironmentVariableTask.isNeeded(this))
        {
          setupURIEnvironmentVariableTask.perform(this);
        }
      }

      {
        PreferenceNode configurationPreferences = PreferencesUtil.getRootPreferenceNode().getNode("configuration");
        if (configurationPreferences != null)
        {
          PreferenceNode networkPreferences = configurationPreferences.getNode("org.eclipse.core.net");
          if (networkPreferences != null)
          {
            ResourceCopyTask resourceCopyTask = SetupFactory.eINSTANCE.createResourceCopyTask();
            URI sourceLocation = URI.createFileURI(networkPreferences.getLocation());
            resourceCopyTask.setSourceURL(sourceLocation.toString());
            int segmentCount = sourceLocation.segmentCount();
            URI targetURI = URI.createFileURI(new File(getEclipseDir(), "configuration").toString())
                .appendSegment(sourceLocation.segment(segmentCount - 2))
                .appendSegment(sourceLocation.segment(segmentCount - 1));
            resourceCopyTask.setTargetURL(targetURI.toString());

            if (resourceCopyTask.isNeeded(this))
            {
              resourceCopyTask.perform(this);
            }
          }
        }
      }
    }
  }

  private void performTriggeredSetupTasks() throws Exception
  {
    initNeededSetupTasks();

    if (neededSetupTasks.isEmpty())
    {
      return;
    }

    if (!SetupConstants.SETUP_IDE || getTrigger() == Trigger.MANUAL)
    {
      performNeededSetupTasks();
    }
    else
    {
      ProgressDialog.run(UIUtil.getShell(), new ProgressLogRunnable()
      {
        public Set<String> run(ProgressLog log) throws Exception
        {
          performNeededSetupTasks();
          return getRestartReasons();
        }
      }, Collections.singletonList(this));
    }
  }

  public void performNeededSetupTasks() throws Exception
  {
    setPerforming(true);

    if (getTrigger() == Trigger.BOOTSTRAP)
    {
      doPerformNeededSetupTasks();
    }
    else
    {
      ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable()
      {
        public void run(IProgressMonitor monitor) throws CoreException
        {
          try
          {
            doPerformNeededSetupTasks();
          }
          catch (Throwable t)
          {
            Activator.coreException(t);
          }
        }
      }, null, IWorkspace.AVOID_UPDATE, null);
    }
  }

  private void doPerformNeededSetupTasks() throws Exception
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

      for (SetupTask neededTask : neededSetupTasks)
      {
        checkCancelation();

        task(neededTask);
        log("Performing setup task " + getLabel(neededTask));

        try
        {
          neededTask.perform(this);
          neededTask.dispose();
        }
        catch (NoClassDefFoundError ex)
        {
          log(ex);
        }
      }
    }
    catch (Exception ex)
    {
      log(ex);
      throw ex;
    }
    finally
    {
      // if (getTrigger() != Trigger.BOOTSTRAP)
      // {
      // ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
      // }

      if (autoBuilding != null)
      {
        restoreAutoBuilding(autoBuilding);
      }

      try
      {
        PrintStream logStream = getLogStream();
        logStream.println();
        logStream.println();
        logStream.println();
        logStream.println();
        IOUtil.closeSilent(logStream);
      }
      catch (Exception ex)
      {
        Activator.log(ex);
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
    Setup originalSetup = getSetup();
    Set<EObject> roots = new LinkedHashSet<EObject>();
    roots.add(originalSetup);

    for (EObject eObject : originalSetup.eCrossReferences())
    {
      EObject rootContainer = EcoreUtil.getRootContainer(eObject);
      roots.add(rootContainer);
    }

    EcoreUtil.Copier copier = new EcoreUtil.Copier()
    {
      private static final long serialVersionUID = 1L;

      @Override
      protected void copyContainment(EReference eReference, EObject eObject, EObject copyEObject)
      {
        // Don't copy projects.
        if (eReference != SetupPackage.Literals.CONFIGURATION__PROJECTS)
        {
          super.copyContainment(eReference, eObject, copyEObject);
        }
      }
    };

    Setup setup = (Setup)copier.copyAll(roots).iterator().next();

    Project originalProject = originalSetup.getBranch().getProject();
    Project project = (Project)copier.get(originalProject);
    if (project == null)
    {
      project = (Project)copier.copy(originalProject);
    }

    ((Configuration)copier.get(originalSetup.getEclipseVersion().getConfiguration())).getProjects().add(project);

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
      SetupTask originalOverriddenSetupTask = entry.getKey();
      SetupTask overriddenSetupTask = (SetupTask)originalCopier.get(originalOverriddenSetupTask);
      // For synthesized tasks, there is no copy, only the original.
      if (overriddenSetupTask == null)
      {
        overriddenSetupTask = originalOverriddenSetupTask;
      }

      SetupTask originalOverridingSetupTask = entry.getValue();
      SetupTask overridingSetupTask = (SetupTask)originalCopier.get(originalOverridingSetupTask);
      // For synthesized tasks, there is no copy, only the original.
      if (overridingSetupTask == null)
      {
        overridingSetupTask = originalOverridingSetupTask;
      }

      overridingSetupTask.overrideFor(overriddenSetupTask);
    }

    // For each original resource, ensure that the copied resource contains the either the corresponding copies or
    // a placeholder object.
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

  private EList<Map.Entry<String, Set<String>>> reorderVariables(final Map<String, Set<String>> variables)
  {
    EList<Map.Entry<String, Set<String>>> list = new BasicEList<Map.Entry<String, Set<String>>>(variables.entrySet());

    EMFUtil.reorder(list, new EMFUtil.DependencyProvider<Map.Entry<String, Set<String>>>()
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
    ECollections.sort(setupTasks, new Comparator<SetupTask>()
    {
      public int compare(SetupTask setupTask1, SetupTask setupTask2)
      {
        return setupTask1.getPriority() - setupTask2.getPriority();
      }
    });

    EMFUtil.reorder(setupTasks, new EMFUtil.DependencyProvider<SetupTask>()
    {
      public Collection<SetupTask> getDependencies(SetupTask setupTask)
      {
        return setupTask.getRequirements();
      }
    });
  }

  private String getLabel(SetupTask setupTask)
  {
    IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(setupTask, IItemLabelProvider.class);
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
}
