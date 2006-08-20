/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.util;


import org.eclipse.emf.cdo.client.CDOPersistable;
import org.eclipse.emf.cdo.client.CDOPersistent;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import testmodel1.EmptyNode;
import testmodel1.ExtendedNode;
import testmodel1.TestModel1Package;
import testmodel1.TreeNode;

import java.util.List;


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
 * @see testmodel1.TestModel1Package
 * @generated
 */
public class TestModel1Switch
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static TestModel1Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TestModel1Switch()
  {
    if (modelPackage == null)
    {
      modelPackage = TestModel1Package.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public Object doSwitch(EObject theEObject)
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
  protected Object doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(
          (EClass) eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected Object doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case TestModel1Package.TREE_NODE:
      {
        TreeNode treeNode = (TreeNode) theEObject;
        Object result = caseTreeNode(treeNode);
        if (result == null) result = caseCDOPersistent(treeNode);
        if (result == null) result = caseCDOPersistable(treeNode);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case TestModel1Package.EXTENDED_NODE:
      {
        ExtendedNode extendedNode = (ExtendedNode) theEObject;
        Object result = caseExtendedNode(extendedNode);
        if (result == null) result = caseTreeNode(extendedNode);
        if (result == null) result = caseCDOPersistent(extendedNode);
        if (result == null) result = caseCDOPersistable(extendedNode);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case TestModel1Package.EMPTY_NODE:
      {
        EmptyNode emptyNode = (EmptyNode) theEObject;
        Object result = caseEmptyNode(emptyNode);
        if (result == null) result = caseTreeNode(emptyNode);
        if (result == null) result = caseCDOPersistent(emptyNode);
        if (result == null) result = caseCDOPersistable(emptyNode);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default:
        return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Tree Node</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Tree Node</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseTreeNode(TreeNode object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Extended Node</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Extended Node</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseExtendedNode(ExtendedNode object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Empty Node</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Empty Node</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseEmptyNode(EmptyNode object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Persistable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Persistable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseCDOPersistable(CDOPersistable object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Persistent</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Persistent</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseCDOPersistent(CDOPersistent object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public Object defaultCase(EObject object)
  {
    return null;
  }

} //TestModel1Switch
