/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.util;


import org.eclipse.emf.cdo.client.CDOPersistable;
import org.eclipse.emf.cdo.client.CDOPersistent;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import testmodel1.*;
import testmodel1.EmptyNode;
import testmodel1.EmptyRefNode;
import testmodel1.ExtendedNode;
import testmodel1.TestModel1Package;
import testmodel1.TreeNode;


/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see testmodel1.TestModel1Package
 * @generated
 */
public class TestModel1AdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static TestModel1Package modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TestModel1AdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = TestModel1Package.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject) object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch the delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TestModel1Switch modelSwitch = new TestModel1Switch()
  {
    public Object caseTreeNode(TreeNode object)
    {
      return createTreeNodeAdapter();
    }

    public Object caseExtendedNode(ExtendedNode object)
    {
      return createExtendedNodeAdapter();
    }

    public Object caseEmptyNode(EmptyNode object)
    {
      return createEmptyNodeAdapter();
    }

    public Object caseEmptyRefNode(EmptyRefNode object)
    {
      return createEmptyRefNodeAdapter();
    }

    public Object caseCDOPersistable(CDOPersistable object)
    {
      return createCDOPersistableAdapter();
    }

    public Object caseCDOPersistent(CDOPersistent object)
    {
      return createCDOPersistentAdapter();
    }

    public Object defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  public Adapter createAdapter(Notifier target)
  {
    return (Adapter) modelSwitch.doSwitch((EObject) target);
  }

  /**
   * Creates a new adapter for an object of class '{@link testmodel1.TreeNode <em>Tree Node</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see testmodel1.TreeNode
   * @generated
   */
  public Adapter createTreeNodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link testmodel1.ExtendedNode <em>Extended Node</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see testmodel1.ExtendedNode
   * @generated
   */
  public Adapter createExtendedNodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link testmodel1.EmptyNode <em>Empty Node</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see testmodel1.EmptyNode
   * @generated
   */
  public Adapter createEmptyNodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link testmodel1.EmptyRefNode <em>Empty Ref Node</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see testmodel1.EmptyRefNode
   * @generated
   */
  public Adapter createEmptyRefNodeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.client.CDOPersistable <em>Persistable</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.client.CDOPersistable
   * @generated
   */
  public Adapter createCDOPersistableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.client.CDOPersistent <em>Persistent</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.client.CDOPersistent
   * @generated
   */
  public Adapter createCDOPersistentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //TestModel1AdapterFactory
