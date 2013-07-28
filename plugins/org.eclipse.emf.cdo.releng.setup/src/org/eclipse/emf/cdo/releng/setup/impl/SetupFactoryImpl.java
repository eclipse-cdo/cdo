/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SetupFactoryImpl extends EFactoryImpl implements SetupFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SetupFactory init()
  {
    try
    {
      SetupFactory theSetupFactory = (SetupFactory)EPackage.Registry.INSTANCE.getEFactory(SetupPackage.eNS_URI);
      if (theSetupFactory != null)
      {
        return theSetupFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new SetupFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case SetupPackage.PREFERENCES:
      return createPreferences();
    case SetupPackage.LINK_LOCATION:
      return createLinkLocation();
    case SetupPackage.TOOL_PREFERENCE:
      return createToolPreference();
    case SetupPackage.ECLIPSE_VERSION:
      return createEclipseVersion();
    case SetupPackage.DIRECTOR_CALL:
      return createDirectorCall();
    case SetupPackage.INSTALLABLE_UNIT:
      return createInstallableUnit();
    case SetupPackage.P2_REPOSITORY:
      return createP2Repository();
    case SetupPackage.CONFIGURATION:
      return createConfiguration();
    case SetupPackage.PROJECT:
      return createProject();
    case SetupPackage.BRANCH:
      return createBranch();
    case SetupPackage.API_BASELINE:
      return createApiBaseline();
    case SetupPackage.GIT_CLONE:
      return createGitClone();
    case SetupPackage.SETUP:
      return createSetup();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case SetupPackage.URI:
      return createURIFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case SetupPackage.URI:
      return convertURIToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Configuration createConfiguration()
  {
    ConfigurationImpl configuration = new ConfigurationImpl();
    return configuration;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Project createProject()
  {
    ProjectImpl project = new ProjectImpl();
    return project;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Branch createBranch()
  {
    BranchImpl branch = new BranchImpl();
    return branch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EclipseVersion createEclipseVersion()
  {
    EclipseVersionImpl eclipseVersion = new EclipseVersionImpl();
    return eclipseVersion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DirectorCall createDirectorCall()
  {
    DirectorCallImpl directorCall = new DirectorCallImpl();
    return directorCall;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InstallableUnit createInstallableUnit()
  {
    InstallableUnitImpl installableUnit = new InstallableUnitImpl();
    return installableUnit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public P2Repository createP2Repository()
  {
    P2RepositoryImpl p2Repository = new P2RepositoryImpl();
    return p2Repository;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ApiBaseline createApiBaseline()
  {
    ApiBaselineImpl apiBaseline = new ApiBaselineImpl();
    return apiBaseline;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GitClone createGitClone()
  {
    GitCloneImpl gitClone = new GitCloneImpl();
    return gitClone;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Setup createSetup()
  {
    SetupImpl setup = new SetupImpl();
    return setup;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ToolPreference createToolPreference()
  {
    ToolPreferenceImpl toolPreference = new ToolPreferenceImpl();
    return toolPreference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LinkLocation createLinkLocation()
  {
    LinkLocationImpl linkLocation = new LinkLocationImpl();
    return linkLocation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Preferences createPreferences()
  {
    PreferencesImpl preferences = new PreferencesImpl();
    return preferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public URI createURIFromString(EDataType eDataType, String initialValue)
  {
    return (URI)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertURIToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupPackage getSetupPackage()
  {
    return (SetupPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static SetupPackage getPackage()
  {
    return SetupPackage.eINSTANCE;
  }

} // SetupFactoryImpl
