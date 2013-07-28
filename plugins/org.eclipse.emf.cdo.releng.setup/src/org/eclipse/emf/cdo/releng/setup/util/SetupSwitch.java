/**
 */
package org.eclipse.emf.cdo.releng.setup.util;

import java.util.List;

import org.eclipse.emf.cdo.releng.setup.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage
 * @generated
 */
public class SetupSwitch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static SetupPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetupSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = SetupPackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    List<EClass> eSuperTypes = theEClass.getESuperTypes();
    return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case SetupPackage.PREFERENCES:
    {
      Preferences preferences = (Preferences)theEObject;
      T result = casePreferences(preferences);
      if (result == null)
        result = caseToolInstallation(preferences);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.LINK_LOCATION:
    {
      LinkLocation linkLocation = (LinkLocation)theEObject;
      T result = caseLinkLocation(linkLocation);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.TOOL_INSTALLATION:
    {
      ToolInstallation toolInstallation = (ToolInstallation)theEObject;
      T result = caseToolInstallation(toolInstallation);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.TOOL_PREFERENCE:
    {
      ToolPreference toolPreference = (ToolPreference)theEObject;
      T result = caseToolPreference(toolPreference);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.ECLIPSE_VERSION:
    {
      EclipseVersion eclipseVersion = (EclipseVersion)theEObject;
      T result = caseEclipseVersion(eclipseVersion);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.DIRECTOR_CALL:
    {
      DirectorCall directorCall = (DirectorCall)theEObject;
      T result = caseDirectorCall(directorCall);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.INSTALLABLE_UNIT:
    {
      InstallableUnit installableUnit = (InstallableUnit)theEObject;
      T result = caseInstallableUnit(installableUnit);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.P2_REPOSITORY:
    {
      P2Repository p2Repository = (P2Repository)theEObject;
      T result = caseP2Repository(p2Repository);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.CONFIGURATION:
    {
      Configuration configuration = (Configuration)theEObject;
      T result = caseConfiguration(configuration);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.PROJECT:
    {
      Project project = (Project)theEObject;
      T result = caseProject(project);
      if (result == null)
        result = caseToolInstallation(project);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.BRANCH:
    {
      Branch branch = (Branch)theEObject;
      T result = caseBranch(branch);
      if (result == null)
        result = caseToolInstallation(branch);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.API_BASELINE:
    {
      ApiBaseline apiBaseline = (ApiBaseline)theEObject;
      T result = caseApiBaseline(apiBaseline);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.GIT_CLONE:
    {
      GitClone gitClone = (GitClone)theEObject;
      T result = caseGitClone(gitClone);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case SetupPackage.SETUP:
    {
      Setup setup = (Setup)theEObject;
      T result = caseSetup(setup);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Preferences</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Preferences</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePreferences(Preferences object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Eclipse Version</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Eclipse Version</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEclipseVersion(EclipseVersion object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Director Call</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Director Call</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDirectorCall(DirectorCall object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Installable Unit</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Installable Unit</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseInstallableUnit(InstallableUnit object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>P2 Repository</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>P2 Repository</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseP2Repository(P2Repository object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Configuration</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Configuration</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConfiguration(Configuration object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Tool Installation</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Tool Installation</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseToolInstallation(ToolInstallation object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Project</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Project</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseProject(Project object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Branch</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Branch</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBranch(Branch object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Api Baseline</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Api Baseline</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseApiBaseline(ApiBaseline object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Git Clone</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Git Clone</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGitClone(GitClone object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Setup</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Setup</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSetup(Setup object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Tool Preference</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Tool Preference</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseToolPreference(ToolPreference object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Link Location</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Link Location</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLinkLocation(LinkLocation object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} //SetupSwitch
