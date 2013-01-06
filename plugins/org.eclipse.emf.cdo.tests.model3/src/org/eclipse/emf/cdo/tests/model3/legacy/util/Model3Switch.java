/**
 */
package org.eclipse.emf.cdo.tests.model3.legacy.util;

import java.util.List;

import org.eclipse.emf.cdo.tests.model3.*;

import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

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
 * @see org.eclipse.emf.cdo.tests.model3.legacy.Model3Package
 * @generated
 */
public class Model3Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static Model3Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model3Switch()
  {
    if (modelPackage == null)
    {
      modelPackage = Model3Package.eINSTANCE;
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
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
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
    case Model3Package.CLASS1:
    {
      Class1 class1 = (Class1)theEObject;
      T result = caseClass1(class1);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.META_REF:
    {
      MetaRef metaRef = (MetaRef)theEObject;
      T result = caseMetaRef(metaRef);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.POLYGON:
    {
      Polygon polygon = (Polygon)theEObject;
      T result = casePolygon(polygon);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.POLYGON_WITH_DUPLICATES:
    {
      PolygonWithDuplicates polygonWithDuplicates = (PolygonWithDuplicates)theEObject;
      T result = casePolygonWithDuplicates(polygonWithDuplicates);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.NODE_A:
    {
      NodeA nodeA = (NodeA)theEObject;
      T result = caseNodeA(nodeA);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.NODE_B:
    {
      NodeB nodeB = (NodeB)theEObject;
      T result = caseNodeB(nodeB);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.NODE_C:
    {
      NodeC nodeC = (NodeC)theEObject;
      T result = caseNodeC(nodeC);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.NODE_D:
    {
      NodeD nodeD = (NodeD)theEObject;
      T result = caseNodeD(nodeD);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.IMAGE:
    {
      Image image = (Image)theEObject;
      T result = caseImage(image);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.FILE:
    {
      File file = (File)theEObject;
      T result = caseFile(file);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.CLASS_WITH_ID_ATTRIBUTE:
    {
      ClassWithIDAttribute classWithIDAttribute = (ClassWithIDAttribute)theEObject;
      T result = caseClassWithIDAttribute(classWithIDAttribute);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.CLASS_WITH_JAVA_CLASS_ATTRIBUTE:
    {
      ClassWithJavaClassAttribute classWithJavaClassAttribute = (ClassWithJavaClassAttribute)theEObject;
      T result = caseClassWithJavaClassAttribute(classWithJavaClassAttribute);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model3Package.CLASS_WITH_JAVA_OBJECT_ATTRIBUTE:
    {
      ClassWithJavaObjectAttribute classWithJavaObjectAttribute = (ClassWithJavaObjectAttribute)theEObject;
      T result = caseClassWithJavaObjectAttribute(classWithJavaObjectAttribute);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Class1</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Class1</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseClass1(Class1 object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Meta Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Meta Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMetaRef(MetaRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Polygon</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Polygon</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePolygon(Polygon object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Polygon With Duplicates</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Polygon With Duplicates</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePolygonWithDuplicates(PolygonWithDuplicates object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Node A</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Node A</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNodeA(NodeA object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Node B</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Node B</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNodeB(NodeB object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Node C</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Node C</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNodeC(NodeC object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Node D</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Node D</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNodeD(NodeD object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Image</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Image</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImage(Image object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>File</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>File</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFile(File object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Class With ID Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Class With ID Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseClassWithIDAttribute(ClassWithIDAttribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Class With Java Class Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Class With Java Class Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseClassWithJavaClassAttribute(ClassWithJavaClassAttribute object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Class With Java Object Attribute</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Class With Java Object Attribute</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseClassWithJavaObjectAttribute(ClassWithJavaObjectAttribute object)
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

} //Model3Switch
