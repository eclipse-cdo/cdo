/*
 * Copyright (c) 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.assembly.impl;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.assembly.AssemblyPackage;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Assembly</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyImpl#getSystemName <em>System Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyImpl#getModules <em>Modules</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AssemblyImpl extends ModelElementImpl implements Assembly
{
  /**
   * @since 1.1
   */
  public static final String PROP_ASSEMBLY = "org.eclipse.emf.cdo.lm.assembly.Assembly";

  /**
   * The default value of the '{@link #getSystemName() <em>System Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getSystemName()
   * @generated
   * @ordered
   */
  protected static final String SYSTEM_NAME_EDEFAULT = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected AssemblyImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return AssemblyPackage.Literals.ASSEMBLY;
  }

  @Override
  protected boolean emfToString()
  {
    return true;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getSystemName()
  {
    return (String)eDynamicGet(AssemblyPackage.ASSEMBLY__SYSTEM_NAME, AssemblyPackage.Literals.ASSEMBLY__SYSTEM_NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSystemName(String newSystemName)
  {
    eDynamicSet(AssemblyPackage.ASSEMBLY__SYSTEM_NAME, AssemblyPackage.Literals.ASSEMBLY__SYSTEM_NAME, newSystemName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<AssemblyModule> getModules()
  {
    return (EList<AssemblyModule>)eDynamicGet(AssemblyPackage.ASSEMBLY__MODULES, AssemblyPackage.Literals.ASSEMBLY__MODULES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY__ANNOTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getAnnotations()).basicAdd(otherEnd, msgs);
    case AssemblyPackage.ASSEMBLY__MODULES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getModules()).basicAdd(otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY__ANNOTATIONS:
      return ((InternalEList<?>)getAnnotations()).basicRemove(otherEnd, msgs);
    case AssemblyPackage.ASSEMBLY__MODULES:
      return ((InternalEList<?>)getModules()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY__ANNOTATIONS:
      return getAnnotations();
    case AssemblyPackage.ASSEMBLY__SYSTEM_NAME:
      return getSystemName();
    case AssemblyPackage.ASSEMBLY__MODULES:
      return getModules();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY__ANNOTATIONS:
      getAnnotations().clear();
      getAnnotations().addAll((Collection<? extends Annotation>)newValue);
      return;
    case AssemblyPackage.ASSEMBLY__SYSTEM_NAME:
      setSystemName((String)newValue);
      return;
    case AssemblyPackage.ASSEMBLY__MODULES:
      getModules().clear();
      getModules().addAll((Collection<? extends AssemblyModule>)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY__ANNOTATIONS:
      getAnnotations().clear();
      return;
    case AssemblyPackage.ASSEMBLY__SYSTEM_NAME:
      setSystemName(SYSTEM_NAME_EDEFAULT);
      return;
    case AssemblyPackage.ASSEMBLY__MODULES:
      getModules().clear();
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case AssemblyPackage.ASSEMBLY__ANNOTATIONS:
      return !getAnnotations().isEmpty();
    case AssemblyPackage.ASSEMBLY__SYSTEM_NAME:
      return SYSTEM_NAME_EDEFAULT == null ? getSystemName() != null : !SYSTEM_NAME_EDEFAULT.equals(getSystemName());
    case AssemblyPackage.ASSEMBLY__MODULES:
      return !getModules().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

  @Override
  public AssemblyModule getRootModule()
  {
    for (AssemblyModule module : getModules())
    {
      if (module.isRoot())
      {
        return module;
      }
    }

    throw new IllegalStateException("No root module: " + this);
  }

  @Override
  public AssemblyModule getModule(String moduleName)
  {
    EList<AssemblyModule> modules = getModules();
    return getModule(modules, moduleName);
  }

  @Override
  public void forEachDependency(Consumer<AssemblyModule> consumer)
  {
    for (AssemblyModule module : getModules())
    {
      if (!module.isRoot())
      {
        consumer.accept(module);
      }
    }
  }

  @Override
  public ModuleDefinition toModuleDefinition()
  {
    AssemblyModule rootModule = getRootModule();

    ModuleDefinition moduleDefinition = ModulesFactory.eINSTANCE.createModuleDefinition(rootModule.getName(), rootModule.getVersion());
    EList<DependencyDefinition> dependencies = moduleDefinition.getDependencies();

    for (AssemblyModule assemblyModule : getModules())
    {
      if (assemblyModule != rootModule)
      {
        String name = assemblyModule.getName();
        Version version = assemblyModule.getVersion();
        VersionRange versionRange = new VersionRange(version, true, version, true);

        DependencyDefinition dependencyDefinition = ModulesFactory.eINSTANCE.createDependencyDefinition();
        dependencyDefinition.setTargetName(name);
        dependencyDefinition.setVersionRange(versionRange);
        dependencies.add(dependencyDefinition);
      }
    }

    return moduleDefinition;
  }

  /**
   * @since 1.1
   */
  public void associateView(CDOView view)
  {
    view.properties().put(PROP_ASSEMBLY, this);
  }

  @Override
  public boolean compareTo(Assembly newAssembly, DeltaHandler handler)
  {
    boolean different = false;

    // Make a copy of this assembly's module.
    // The handler may modify the current module list.
    EList<AssemblyModule> oldModules = new BasicEList<>(getModules());

    for (AssemblyModule oldModule : oldModules)
    {
      AssemblyModule newModule = newAssembly.getModule(oldModule.getName());
      if (newModule == null)
      {
        different = true;
        handler.handleRemoval(oldModule);
      }
    }

    for (AssemblyModule newModule : newAssembly.getModules())
    {
      AssemblyModule oldModule = getModule(oldModules, newModule.getName());
      if (oldModule == null)
      {
        different = true;
        handler.handleAddition(newModule);
      }
      else
      {
        if (oldModule.isRoot() != newModule.isRoot() || !Objects.equals(oldModule.getVersion(), newModule.getVersion())
            || !Objects.equals(oldModule.getBranchPoint(), newModule.getBranchPoint()) || !equalLists(oldModule.getAnnotations(), newModule.getAnnotations()))
        {
          different = true;
          handler.handleModification(oldModule, newModule);
        }
      }
    }

    return different;
  }

  @SuppressWarnings("unchecked")
  private static boolean equalLists(List<? extends EObject> list1, List<? extends EObject> list2)
  {
    // An equality helper that ignores container references.
    EqualityHelper equalityHelper = new EqualityHelper()
    {
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean haveEqualReference(EObject eObject1, EObject eObject2, EReference reference)
      {
        if (reference.isContainer())
        {
          return true;
        }

        return super.haveEqualReference(eObject1, eObject2, reference);
      }

    };

    return equalityHelper.equals((List<EObject>)list1, (List<EObject>)list2);
  }

  private static AssemblyModule getModule(EList<AssemblyModule> modules, String moduleName)
  {
    for (AssemblyModule module : modules)
    {
      if (Objects.equals(module.getName(), moduleName))
      {
        return module;
      }
    }

    return null;
  }

} // AssemblyImpl
