/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.impl;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import testmodel1.*;

import testmodel1.EmptyNode;
import testmodel1.EmptyRefNode;
import testmodel1.ExtendedNode;
import testmodel1.TestModel1Factory;
import testmodel1.TestModel1Package;
import testmodel1.TreeNode;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TestModel1FactoryImpl extends EFactoryImpl implements TestModel1Factory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static TestModel1Factory init()
  {
    try
    {
      TestModel1Factory theTestModel1Factory = (TestModel1Factory) EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/cdo/2006/TestModel1");
      if (theTestModel1Factory != null)
      {
        return theTestModel1Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new TestModel1FactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TestModel1FactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case TestModel1Package.TREE_NODE:
        return createTreeNode();
      case TestModel1Package.EXTENDED_NODE:
        return createExtendedNode();
      case TestModel1Package.EMPTY_NODE:
        return createEmptyNode();
      case TestModel1Package.EMPTY_REF_NODE:
        return createEmptyRefNode();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName()
            + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TreeNode createTreeNode()
  {
    TreeNodeImpl treeNode = new TreeNodeImpl();
    return treeNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExtendedNode createExtendedNode()
  {
    ExtendedNodeImpl extendedNode = new ExtendedNodeImpl();
    return extendedNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EmptyNode createEmptyNode()
  {
    EmptyNodeImpl emptyNode = new EmptyNodeImpl();
    return emptyNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EmptyRefNode createEmptyRefNode()
  {
    EmptyRefNodeImpl emptyRefNode = new EmptyRefNodeImpl();
    return emptyRefNode;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TestModel1Package getTestModel1Package()
  {
    return (TestModel1Package) getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  public static TestModel1Package getPackage()
  {
    return TestModel1Package.eINSTANCE;
  }

} //TestModel1FactoryImpl
