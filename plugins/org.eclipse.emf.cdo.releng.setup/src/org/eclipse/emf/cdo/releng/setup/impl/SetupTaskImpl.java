/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.internal.setup.util.EMFUtil;
import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.CompoundSetupTask;
import org.eclipse.emf.cdo.releng.setup.ConfigurableItem;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTask.Sniffer.ResourceHandler;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.util.UIUtil;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getRequirements <em>Requirements</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getRestrictions <em>Restrictions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#isDisabled <em>Disabled</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getScope <em>Scope</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getExcludedTriggers <em>Excluded Triggers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupTaskImpl#getDocumentation <em>Documentation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class SetupTaskImpl extends MinimalEObjectImpl.Container implements SetupTask
{
  /**
   * The cached value of the '{@link #getRequirements() <em>Requirements</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRequirements()
   * @generated
   * @ordered
   */
  protected EList<SetupTask> requirements;

  /**
   * The cached value of the '{@link #getRestrictions() <em>Restrictions</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRestrictions()
   * @generated
   * @ordered
   */
  protected EList<ConfigurableItem> restrictions;

  /**
   * The default value of the '{@link #isDisabled() <em>Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isDisabled()
   * @generated
   * @ordered
   */
  protected static final boolean DISABLED_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isDisabled() <em>Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isDisabled()
   * @generated
   * @ordered
   */
  protected boolean disabled = DISABLED_EDEFAULT;

  /**
   * The default value of the '{@link #getScope() <em>Scope</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScope()
   * @generated
   * @ordered
   */
  protected static final SetupTaskScope SCOPE_EDEFAULT = SetupTaskScope.NONE;

  /**
   * The cached value of the '{@link #getExcludedTriggers() <em>Excluded Triggers</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExcludedTriggers()
   * @generated
   * @ordered
   */
  protected Set<Trigger> excludedTriggers;

  /**
   * The default value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDocumentation()
   * @generated
   * @ordered
   */
  protected static final String DOCUMENTATION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDocumentation()
   * @generated
   * @ordered
   */
  protected String documentation = DOCUMENTATION_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected SetupTaskImpl()
  {
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.SETUP_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<SetupTask> getRequirements()
  {
    if (requirements == null)
    {
      requirements = new EObjectResolvingEList<SetupTask>(SetupTask.class, this, SetupPackage.SETUP_TASK__REQUIREMENTS);
    }
    return requirements;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ConfigurableItem> getRestrictions()
  {
    if (restrictions == null)
    {
      restrictions = new EObjectResolvingEList<ConfigurableItem>(ConfigurableItem.class, this,
          SetupPackage.SETUP_TASK__RESTRICTIONS);
    }
    return restrictions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public SetupTaskScope getScope()
  {
    return getScope(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Set<Trigger> getExcludedTriggers()
  {
    return excludedTriggers == null ? Collections.<Trigger> emptySet() : excludedTriggers;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setExcludedTriggersGen(Set<Trigger> newExcludedTriggers)
  {
    Set<Trigger> oldExcludedTriggers = excludedTriggers;
    excludedTriggers = newExcludedTriggers;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS,
          oldExcludedTriggers, excludedTriggers));
    }
  }

  public void setExcludedTriggers(Set<Trigger> newExcludedTriggers)
  {
    setExcludedTriggersGen(newExcludedTriggers == null || newExcludedTriggers.isEmpty() ? null : newExcludedTriggers);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDocumentation()
  {
    return documentation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDocumentation(String newDocumentation)
  {
    String oldDocumentation = documentation;
    documentation = newDocumentation;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP_TASK__DOCUMENTATION, oldDocumentation,
          documentation));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public ScopeRoot getScopeRoot()
  {
    for (EObject container = eContainer(); container != null; container = container.eContainer())
    {
      if (container instanceof ScopeRoot)
      {
        return (ScopeRoot)container;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isDisabled()
  {
    return disabled;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDisabled(boolean newDisabled)
  {
    boolean oldDisabled = disabled;
    disabled = newDisabled;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP_TASK__DISABLED, oldDisabled, disabled));
    }
  }

  private SetupTaskScope getScope(EObject object)
  {
    if (object instanceof Configuration)
    {
      return SetupTaskScope.CONFIGURATION;
    }

    if (object instanceof Eclipse)
    {
      return SetupTaskScope.ECLIPSE;
    }

    if (object instanceof Project)
    {
      return SetupTaskScope.PROJECT;
    }

    if (object instanceof Branch)
    {
      return SetupTaskScope.BRANCH;
    }

    if (object instanceof Preferences)
    {
      return SetupTaskScope.USER;
    }

    EObject container = object.eContainer();
    if (container == null)
    {
      return SetupTaskScope.NONE;
    }

    return getScope(container);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean requires(SetupTask setupTask)
  {
    Set<SetupTask> visited = new HashSet<SetupTask>();
    return requires(setupTask, visited);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.ALL_TRIGGERS;
  }

  public int getPriority()
  {
    return DEFAULT_PRIORITY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public final Set<Trigger> getTriggers()
  {
    Set<Trigger> excludedTriggers = getExcludedTriggers();
    if (excludedTriggers == null || excludedTriggers.isEmpty())
    {
      return getValidTriggers();
    }

    Set<Trigger> result = new HashSet<Trigger>(getValidTriggers());
    result.removeAll(excludedTriggers);
    return Trigger.intern(result);
  }

  private boolean requires(SetupTask setupTask, Set<SetupTask> visited)
  {
    if (visited.add(this))
    {
      if (setupTask == this)
      {
        return true;
      }

      for (SetupTask requirement : getRequirements())
      {
        if (((SetupTaskImpl)requirement).requires(setupTask, visited))
        {
          return true;
        }
      }
    }

    return false;
  }

  protected final Object createToken(String value)
  {
    return new TypedStringToken(eClass(), value);
  }

  /**
   * Subclasses may override to indicate that this task overrides another task with the same token.
   *
   * @see #createToken(String)
   */
  public Object getOverrideToken()
  {
    return this;
  }

  public void overrideFor(SetupTask overriddenSetupTask)
  {
    getRequirements().addAll(overriddenSetupTask.getRequirements());
    getRestrictions().addAll(overriddenSetupTask.getRestrictions());
  }

  public void consolidate()
  {
  }

  /**
   * Subclasses may override to reset this task to its initial state.
   */
  public void dispose()
  {
  }

  public boolean needsBundlePool()
  {
    return false;
  }

  public boolean needsBundlePoolTP()
  {
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      return getRequirements();
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      return getRestrictions();
    case SetupPackage.SETUP_TASK__DISABLED:
      return isDisabled();
    case SetupPackage.SETUP_TASK__SCOPE:
      return getScope();
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      return getExcludedTriggers();
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      return getDocumentation();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      getRequirements().clear();
      getRequirements().addAll((Collection<? extends SetupTask>)newValue);
      return;
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      getRestrictions().clear();
      getRestrictions().addAll((Collection<? extends ConfigurableItem>)newValue);
      return;
    case SetupPackage.SETUP_TASK__DISABLED:
      setDisabled((Boolean)newValue);
      return;
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      setExcludedTriggers((Set<Trigger>)newValue);
      return;
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      setDocumentation((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      getRequirements().clear();
      return;
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      getRestrictions().clear();
      return;
    case SetupPackage.SETUP_TASK__DISABLED:
      setDisabled(DISABLED_EDEFAULT);
      return;
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      setExcludedTriggers((Set<Trigger>)null);
      return;
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      setDocumentation(DOCUMENTATION_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.SETUP_TASK__REQUIREMENTS:
      return requirements != null && !requirements.isEmpty();
    case SetupPackage.SETUP_TASK__RESTRICTIONS:
      return restrictions != null && !restrictions.isEmpty();
    case SetupPackage.SETUP_TASK__DISABLED:
      return disabled != DISABLED_EDEFAULT;
    case SetupPackage.SETUP_TASK__SCOPE:
      return getScope() != SCOPE_EDEFAULT;
    case SetupPackage.SETUP_TASK__EXCLUDED_TRIGGERS:
      return excludedTriggers != null;
    case SetupPackage.SETUP_TASK__DOCUMENTATION:
      return DOCUMENTATION_EDEFAULT == null ? documentation != null : !DOCUMENTATION_EDEFAULT.equals(documentation);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (disabled: ");
    result.append(disabled);
    result.append(", excludedTriggers: ");
    result.append(excludedTriggers);
    result.append(", documentation: ");
    result.append(documentation);
    result.append(')');
    return result.toString();
  }

  protected URI createResolvedURI(String uri)
  {
    if (uri == null)
    {
      return null;
    }

    URI result = URI.createURI(uri);
    if (result.isRelative() && result.hasRelativePath())
    {
      Resource resource = eResource();
      URI baseURI = resource.getURI();
      if (baseURI != null && baseURI.isHierarchical() && !baseURI.isRelative())
      {
        return result.resolve(baseURI);
      }
    }

    return result;
  }

  protected final void performUI(final SetupTaskContext context, final RunnableWithContext runnable) throws Exception
  {
    final Exception[] exception = { null };
    UIUtil.getDisplay().syncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          runnable.run(context);
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
      }
    });

    if (exception[0] != null)
    {
      throw exception[0];
    }
  }

  public MirrorRunnable mirror(MirrorContext context, File mirrorsDir, boolean includingLocals) throws Exception
  {
    return null;
  }

  public void collectSniffers(List<Sniffer> sniffers)
  {
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class BasicSniffer implements Sniffer
  {
    private Object icon;

    private String label;

    private String description;

    public BasicSniffer(Object icon, String label, String description)
    {
      this.icon = icon;
      this.label = label;
      this.description = description;
    }

    public BasicSniffer(EObject object, String description)
    {
      this.description = description;
      ComposedAdapterFactory adapterFactory = EMFUtil.createAdapterFactory();
      IItemLabelProvider labelProvider = (IItemLabelProvider)adapterFactory.adapt(object, IItemLabelProvider.class);

      icon = labelProvider.getImage(object);
      label = labelProvider.getText(object);
    }

    public BasicSniffer(EObject object)
    {
      this(object, null);
    }

    public BasicSniffer()
    {
    }

    public Object getIcon()
    {
      return icon;
    }

    public void setIcon(Object icon)
    {
      this.icon = icon;
    }

    public String getLabel()
    {
      return label;
    }

    public void setLabel(String label)
    {
      this.label = label;
    }

    public String getDescription()
    {
      return description;
    }

    public void setDescription(String description)
    {
      this.description = description;
    }

    public int getWork()
    {
      return 1;
    }

    public int getPriority()
    {
      return DEFAULT_PRIORITY;
    }

    public void retainDependencies(List<Sniffer> dependencies)
    {
      dependencies.clear();
    }

    @Override
    public String toString()
    {
      return label;
    }

    public static void retainType(Collection<?> c, Class<?> type)
    {
      for (Iterator<?> it = c.iterator(); it.hasNext();)
      {
        Object element = it.next();
        if (type.isInstance(element))
        {
          continue;
        }

        it.remove();
      }
    }

    public static Map<File, IPath> getSourcePaths(List<Sniffer> dependencies)
    {
      Map<File, IPath> sourcePaths = new HashMap<File, IPath>();
      for (Sniffer sniffer : dependencies)
      {
        sourcePaths.putAll(((SourcePathProvider)sniffer).getSourcePaths());
      }

      return sourcePaths;
    }

    public static CompoundSetupTask getCompound(SetupTaskContainer container, String name)
    {
      EList<SetupTask> children = container.getSetupTasks();
      for (SetupTask setupTask : children)
      {
        if (setupTask instanceof CompoundSetupTask)
        {
          CompoundSetupTask compound = (CompoundSetupTask)setupTask;
          if (ObjectUtil.equals(compound.getName(), name))
          {
            return compound;
          }
        }
      }

      CompoundSetupTask compound = SetupFactory.eINSTANCE.createCompoundSetupTask(name);
      children.add(compound);
      return compound;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class ResourceSniffer extends BasicSniffer implements ResourceHandler
  {
    private List<IResource> resources = new ArrayList<IResource>();

    public ResourceSniffer()
    {
    }

    public ResourceSniffer(EObject object, String description)
    {
      super(object, description);
    }

    public ResourceSniffer(EObject object)
    {
      super(object);
    }

    public ResourceSniffer(Object icon, String label, String description)
    {
      super(icon, label, description);
    }

    public void handleResource(IResource resource) throws Exception
    {
      if (filterResource(resource))
      {
        resources.add(resource);
      }
    }

    public final void sniff(SetupTaskContainer container, List<Sniffer> dependencies, IProgressMonitor monitor)
        throws Exception
    {
      sniff(container, dependencies, resources, monitor);
    }

    protected abstract boolean filterResource(IResource resource);

    protected abstract void sniff(SetupTaskContainer container, List<Sniffer> dependencies, List<IResource> resources,
        IProgressMonitor monitor) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  protected interface RunnableWithContext
  {
    public void run(SetupTaskContext context) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  protected static final class TypedStringToken
  {
    private final Object type;

    private final String value;

    public TypedStringToken(Object type, String value)
    {
      this.type = type;
      this.value = value;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (type == null ? 0 : type.hashCode());
      result = prime * result + (value == null ? 0 : value.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      TypedStringToken other = (TypedStringToken)obj;
      if (type == null)
      {
        if (other.type != null)
        {
          return false;
        }
      }
      else if (!type.equals(other.type))
      {
        return false;
      }

      if (value == null)
      {
        if (other.value != null)
        {
          return false;
        }
      }
      else if (!value.equals(other.value))
      {
        return false;
      }

      return true;
    }
  }
} // SetupTaskImpl
