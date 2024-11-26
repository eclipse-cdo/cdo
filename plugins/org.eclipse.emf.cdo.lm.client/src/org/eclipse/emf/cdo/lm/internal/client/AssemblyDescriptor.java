/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.Assembly.DeltaHandler;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.internal.client.LMResourceSetConfigurer.CheckoutResult.BranchPointDelta;
import org.eclipse.emf.cdo.lm.internal.client.bundle.OM;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewCommitInfoListener;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class AssemblyDescriptor extends Container<AssemblyModule> implements IAssemblyDescriptor
{
  private final IListener checkoutViewListener = new CDOViewCommitInfoListener()
  {
    @Override
    public void notifyCommitInfo(CDOCommitInfo commitInfo)
    {
      commitInfo.forEachRevisionDelta(revisionDelta -> {
        if (revisionDelta.getEClass().getEPackage() == ModulesPackage.eINSTANCE)
        {
          moduleDefinitionChanged();
        }
      });
    }
  };

  private final CDOCheckout checkout;

  private final Set<LMResourceSetConfigurer.CheckoutResult> lmResourceSetConfigurerResults = new LinkedHashSet<>();

  private final ISystemDescriptor systemDescriptor;

  private final CDOID moduleID;

  private final String moduleName;

  private final Baseline baseline;

  private final Adapter assemblyAdapter = new AssemblyAdapter(this);

  private final Assembly assembly;

  private volatile Assembly updateAssembly;

  private volatile UpdateState updateState;

  private volatile Updates availableUpdates;

  private volatile List<String> resolutionErrors;

  private String name;

  public AssemblyDescriptor(CDOCheckout checkout, String systemName, CDOID baselineID)
  {
    this.checkout = checkout;

    name = checkout.getLabel();
    systemDescriptor = ISystemManager.INSTANCE.getDescriptor(systemName);
    systemDescriptor.open();

    System system = systemDescriptor.getSystem();
    baseline = (Baseline)system.cdoView().getObject(baselineID);

    assembly = AssemblyManager.loadAssembly(checkout, false);
    addAssemblyAdapter(assembly);

    moduleID = baseline.getModule().cdoID();
    moduleName = assembly.getRootModule().getName();

    updateAssembly = AssemblyManager.loadAssembly(checkout, true);
    if (updateAssembly == null)
    {
      resolutionErrors = AssemblyManager.loadErrors(checkout);
      updateState = UpdateState.NoUpdatesAvailable;
    }
    else
    {
      addAssemblyAdapter(updateAssembly);
      updateState = UpdateState.UpdatesAvailable;
    }

    CDOBranchPointRef branchPointRef = baseline.getBranchPoint();
    CDOBranchPointRef existingBranchPointRef = new CDOBranchPointRef(checkout.getBranchPoint());
    if (!existingBranchPointRef.equals(branchPointRef))
    {
      checkout.setBranchPoint(branchPointRef);
    }

    checkout.getView().addListener(checkoutViewListener);
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public CDOCheckout getCheckout()
  {
    return checkout;
  }

  @Override
  public Baseline getBaseline()
  {
    return baseline;
  }

  @Override
  public Baseline getBaseline(AssemblyModule module)
  {
    System system = systemDescriptor.getSystem();
    if (system != null)
    {
      String annotation = CDOUtil.getAnnotation(module, //
          LMPackage.ANNOTATION_SOURCE, //
          LMPackage.ANNOTATION_DETAIL_BASELINE_ID);
      if (!StringUtil.isEmpty(annotation))
      {
        CDOID baselineID = CDOIDUtil.read(annotation);
        CDOView systemView = system.cdoView();
        return (Baseline)systemView.getObject(baselineID);
      }
    }

    return null;
  }

  @Override
  public Assembly getAssembly()
  {
    return assembly;
  }

  @Override
  public String getModuleName()
  {
    return moduleName;
  }

  @Override
  public AssemblyModule getModule(String name)
  {
    return assembly.getModule(name);
  }

  @Override
  public AssemblyModule[] getModules(boolean withNewModules)
  {
    EList<AssemblyModule> modules = assembly.getModules();

    if (withNewModules)
    {
      Updates updates = availableUpdates;
      if (updates != null)
      {
        Collection<AssemblyModule> newModules = updates.getAdditions().values();
        if (!newModules.isEmpty())
        {
          modules = new BasicEList<>(modules);
          modules.addAll(newModules);
        }
      }
    }

    return modules.toArray(new AssemblyModule[modules.size()]);
  }

  @Override
  public ISystemDescriptor getSystemDescriptor()
  {
    return systemDescriptor;
  }

  @Override
  public AssemblyModule[] getElements()
  {
    EList<AssemblyModule> modules = assembly.getModules();
    AssemblyModule[] elements = modules.toArray(new AssemblyModule[modules.size()]);
    Arrays.sort(elements, null);
    return elements;
  }

  @Override
  public boolean isEmpty()
  {
    EList<AssemblyModule> modules = assembly.getModules();
    return modules.isEmpty();
  }

  public void addResourceSet(LMResourceSetConfigurer.CheckoutResult lmResourceSetConfigurerResult)
  {
    synchronized (lmResourceSetConfigurerResults)
    {
      lmResourceSetConfigurerResults.add(lmResourceSetConfigurerResult);
    }
  }

  public void removeResourceSet(LMResourceSetConfigurer.CheckoutResult lmResourceSetConfigurerResult)
  {
    synchronized (lmResourceSetConfigurerResults)
    {
      lmResourceSetConfigurerResults.remove(lmResourceSetConfigurerResult);
    }
  }

  public LMResourceSetConfigurer.CheckoutResult[] getLMResourceSetConfigurations()
  {
    synchronized (lmResourceSetConfigurerResults)
    {
      return lmResourceSetConfigurerResults.toArray(new LMResourceSetConfigurer.CheckoutResult[lmResourceSetConfigurerResults.size()]);
    }
  }

  @Override
  public UpdateState getUpdateState()
  {
    return updateState;
  }

  private void setUpdateState(UpdateState updateState)
  {
    IEvent event = null;

    synchronized (this)
    {
      UpdateState oldUpdateState = this.updateState;
      if (oldUpdateState != updateState)
      {
        this.updateState = updateState;
        event = new UpdateStateChangedEventImpl(this, oldUpdateState, updateState);
      }
    }

    if (event != null)
    {
      fireEvent(event);
    }
  }

  @Override
  public boolean hasUpdatesAvailable()
  {
    return updateState == UpdateState.UpdatesAvailable;
  }

  @Override
  public Updates getAvailableUpdates()
  {
    return availableUpdates;
  }

  @Override
  public List<String> getResolutionErrors()
  {
    return resolutionErrors;
  }

  public void checkForUpdates(IProgressMonitor monitor) throws Exception
  {
    ConcurrencyUtil.checkCancelation(monitor);

    ModuleDefinition moduleDefinition = systemDescriptor.extractModuleDefinition(baseline);
    ConcurrencyUtil.checkCancelation(monitor);

    removeAssemblyAdapter(updateAssembly);
    ConcurrencyUtil.checkCancelation(monitor);

    Assembly newAssembly;

    Updates oldAvailableUpdates = availableUpdates;
    Updates newAvailableUpdates;

    availableUpdates = null;
    resolutionErrors = null;

    try
    {
      newAssembly = systemDescriptor.resolve(moduleDefinition, baseline, monitor);
      ConcurrencyUtil.checkCancelation(monitor);
    }
    catch (ResolutionException ex)
    {
      AssemblyManager.deleteAssembly(checkout, true);
      resolutionErrors = AssemblyManager.saveErrors(checkout, ex.getReasons());
      updateAssembly = null;

      setUpdateState(UpdateState.NoUpdatesAvailable);
      fireEvent(new AvailableUpdatesChangedEventImpl(this, oldAvailableUpdates, null));

      throw ex;
    }

    try
    {
      EList<AssemblyModule> modules = new BasicEList<>(assembly.getModules());
      EList<AssemblyModule> newModules = new BasicEList<>(newAssembly.getModules());

      modules.sort(null);
      newModules.sort(null);
      ConcurrencyUtil.checkCancelation(monitor);

      if (EcoreUtil.equals(newModules, modules))
      {
        AssemblyManager.deleteAssembly(checkout, true);
        ConcurrencyUtil.checkCancelation(monitor);

        updateAssembly = null;
        newAvailableUpdates = null;
      }
      else
      {
        newAssembly.sortModules();
        AssemblyManager.saveAssembly(checkout, newAssembly, true);
        ConcurrencyUtil.checkCancelation(monitor);

        updateAssembly = newAssembly;
        newAvailableUpdates = new UpdatesImpl();

        UpdatesImpl updates = (UpdatesImpl)newAvailableUpdates;
        assembly.compareTo(updateAssembly, new DeltaHandler()
        {
          @Override
          public void handleAddition(AssemblyModule newModule)
          {
            updates.additions.put(newModule.getName(), newModule);
          }

          @Override
          public void handleRemoval(AssemblyModule oldModule)
          {
            updates.removals.add(oldModule.getName());
          }

          @Override
          public void handleModification(AssemblyModule oldModule, AssemblyModule newModule)
          {
            updates.modifications.put(oldModule.getName(), newModule);
          }
        });

        if (newAvailableUpdates.isEmpty())
        {
          newAvailableUpdates = null;
        }
      }

      ConcurrencyUtil.checkCancelation(monitor);
    }
    finally
    {
      addAssemblyAdapter(updateAssembly);
      ConcurrencyUtil.checkCancelation(monitor);
    }

    availableUpdates = newAvailableUpdates;
    setUpdateState(newAvailableUpdates == null ? UpdateState.NoUpdatesAvailable : UpdateState.UpdatesAvailable);
    ConcurrencyUtil.checkCancelation(monitor);

    if (!Objects.equals(newAvailableUpdates, oldAvailableUpdates))
    {
      fireEvent(new AvailableUpdatesChangedEventImpl(this, oldAvailableUpdates, newAvailableUpdates));
      ConcurrencyUtil.checkCancelation(monitor);
    }
  }

  @Override
  public void update() throws Exception
  {
    if (!hasUpdatesAvailable())
    {
      return;
    }

    ContainerEvent<AssemblyModule> containerEvent = new ContainerEvent<>(this);
    List<BranchPointDelta> branchPointDeltas = new ArrayList<>();
    EcoreUtil.Copier copier = new EcoreUtil.Copier();

    assembly.compareTo(updateAssembly, new DeltaHandler()
    {
      @Override
      public void handleAddition(AssemblyModule newModule)
      {
        AssemblyModule copy = (AssemblyModule)copier.copy(newModule);
        assembly.getModules().add(copy);
        containerEvent.addDelta(copy, IContainerDelta.Kind.ADDED);
        branchPointDeltas.add(new BranchPointDelta(newModule, null, newModule.getBranchPoint()));
      }

      @Override
      public void handleRemoval(AssemblyModule oldModule)
      {
        assembly.getModules().remove(oldModule);
        containerEvent.addDelta(oldModule, IContainerDelta.Kind.REMOVED);
        branchPointDeltas.add(new BranchPointDelta(oldModule, oldModule.getBranchPoint(), null));
      }

      @Override
      public void handleModification(AssemblyModule oldModule, AssemblyModule newModule)
      {
        oldModule.setRoot(newModule.isRoot());
        oldModule.setVersion(newModule.getVersion());
        oldModule.setBranchPoint(newModule.getBranchPoint());

        EList<Annotation> oldAnnotations = oldModule.getAnnotations();
        oldAnnotations.clear();

        EList<Annotation> newAnnotations = newModule.getAnnotations();
        oldAnnotations.addAll(copier.copyAll(newAnnotations));

        containerEvent.addDelta(oldModule, IContainerDelta.Kind.REMOVED);
        containerEvent.addDelta(oldModule, IContainerDelta.Kind.ADDED);
        branchPointDeltas.add(new BranchPointDelta(oldModule, //
            oldModule.getBranchPoint(), //
            newModule.getBranchPoint()));
      }
    });

    copier.copyReferences();

    assembly.sortModules();
    AssemblyManager.saveAssembly(checkout, assembly, false);
    AssemblyManager.deleteAssembly(checkout, true);

    Updates oldAvailableUpdates = availableUpdates;
    availableUpdates = null;
    updateAssembly = null;

    setUpdateState(UpdateState.NoUpdatesAvailable);

    fireEvent(new AvailableUpdatesChangedEventImpl(this, oldAvailableUpdates, null));
    fireEvent(containerEvent);

    for (LMResourceSetConfigurer.CheckoutResult lmResourceSetConfigurerResult : lmResourceSetConfigurerResults)
    {
      try
      {
        lmResourceSetConfigurerResult.reconfigure(branchPointDeltas);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  public void checkoutChanged()
  {
    boolean changed = false;
    String newName = checkout.getLabel();
    String oldName;

    synchronized (this)
    {
      oldName = name;
      if (!Objects.equals(newName, oldName))
      {
        name = newName;
        changed = true;
      }
    }

    if (changed)
    {
      fireEvent(new NameChangedEventImpl(this, oldName, newName));
    }
  }

  public void moduleDefinitionChanged()
  {
    AssemblyManager.INSTANCE.scheduleUpdateCheck(this);
  }

  public void moduleDeleted(CDOID deletedModuleID)
  {
    if (deletedModuleID == moduleID)
    {
      OM.LOG.info("Deleting checkout '" + checkout + "' because module '" + moduleName + "' was deleted");
      checkout.delete(true);
    }
    else
    {
      AssemblyManager.INSTANCE.scheduleUpdateCheck(this);
    }
  }

  public void baselineAdded(Baseline newBaseline)
  {
    AssemblyManager.INSTANCE.scheduleUpdateCheck(this);
  }

  @Override
  public String toString()
  {
    return name;
  }

  private void addAssemblyAdapter(Assembly assembly)
  {
    if (assembly != null)
    {
      assembly.eAdapters().add(assemblyAdapter);
    }
  }

  private void removeAssemblyAdapter(Assembly assembly)
  {
    if (assembly != null)
    {
      assembly.eAdapters().remove(assemblyAdapter);
    }
  }

  public static AssemblyDescriptor get(EObject object)
  {
    if (object != null)
    {
      for (Adapter adapter : object.eAdapters())
      {
        if (adapter instanceof AssemblyAdapter)
        {
          return ((AssemblyAdapter)adapter).getAssemblyDescriptor();
        }
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class AssemblyAdapter extends EContentAdapter
  {
    private final AssemblyDescriptor assemblyDescriptor;

    public AssemblyAdapter(AssemblyDescriptor assemblyDescriptor)
    {
      this.assemblyDescriptor = assemblyDescriptor;
    }

    public AssemblyDescriptor getAssemblyDescriptor()
    {
      return assemblyDescriptor;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class NameChangedEventImpl extends Event implements NameChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final String oldName;

    private final String newName;

    public NameChangedEventImpl(AssemblyDescriptor assemblyDescriptor, String oldName, String newName)
    {
      super(assemblyDescriptor);
      this.oldName = oldName;
      this.newName = newName;
    }

    @Override
    public AssemblyDescriptor getDescriptor()
    {
      return (AssemblyDescriptor)getSource();
    }

    @Override
    public String getOldName()
    {
      return oldName;
    }

    @Override
    public String getNewName()
    {
      return newName;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class UpdateStateChangedEventImpl extends Event implements UpdateStateChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final UpdateState oldUpdateState;

    private final UpdateState newUpdateState;

    public UpdateStateChangedEventImpl(AssemblyDescriptor assemblyDescriptor, UpdateState oldUpdateState, UpdateState newUpdateState)
    {
      super(assemblyDescriptor);
      this.oldUpdateState = oldUpdateState;
      this.newUpdateState = newUpdateState;
    }

    @Override
    public AssemblyDescriptor getDescriptor()
    {
      return (AssemblyDescriptor)getSource();
    }

    @Override
    public UpdateState getOldUpdateState()
    {
      return oldUpdateState;
    }

    @Override
    public UpdateState getNewUpdateState()
    {
      return newUpdateState;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AvailableUpdatesChangedEventImpl extends Event implements AvailableUpdatesChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final Updates oldAvailableUpdates;

    private final Updates newAvailableUpdates;

    public AvailableUpdatesChangedEventImpl(AssemblyDescriptor assemblyDescriptor, //
        Updates oldAvailableUpdates, //
        Updates newAvailableUpdates)
    {
      super(assemblyDescriptor);
      this.oldAvailableUpdates = oldAvailableUpdates;
      this.newAvailableUpdates = newAvailableUpdates;
    }

    @Override
    public AssemblyDescriptor getDescriptor()
    {
      return (AssemblyDescriptor)getSource();
    }

    @Override
    public Updates getOldAvailableUpdates()
    {
      return oldAvailableUpdates;
    }

    @Override
    public Updates getNewAvailableUpdates()
    {
      return newAvailableUpdates;
    }

    @Override
    protected String formatEventName()
    {
      return AvailableUpdatesChangedEvent.class.getSimpleName();
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "checkout=" + getDescriptor().getName() + ", old=" + oldAvailableUpdates + ", new=" + newAvailableUpdates;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class UpdatesImpl implements Updates
  {
    private final Map<String, AssemblyModule> additions = new HashMap<>();

    private final Map<String, AssemblyModule> modifications = new HashMap<>();

    private final Set<String> removals = new HashSet<>();

    public UpdatesImpl()
    {
    }

    @Override
    public boolean isEmpty()
    {
      return additions.isEmpty() && modifications.isEmpty() && removals.isEmpty();
    }

    @Override
    public Map<String, AssemblyModule> getAdditions()
    {
      return Collections.unmodifiableMap(additions);
    }

    @Override
    public Map<String, AssemblyModule> getModifications()
    {
      return Collections.unmodifiableMap(modifications);
    }

    @Override
    public Set<String> getRemovals()
    {
      return Collections.unmodifiableSet(removals);
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(additions, modifications, removals);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (!(obj instanceof UpdatesImpl))
      {
        return false;
      }

      UpdatesImpl other = (UpdatesImpl)obj;
      return Objects.equals(additions, other.additions) && //
          Objects.equals(modifications, other.modifications) && //
          Objects.equals(removals, other.removals);
    }

    @Override
    public String toString()
    {
      return "Update[additions=" + additions.keySet() + //
          ", removals=" + removals + //
          ", modifications=" + modifications;
    }
  }
}
