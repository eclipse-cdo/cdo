/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.impl;


import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import testmodel1.EmptyRefNode;
import testmodel1.TestModel1Package;
import testmodel1.TreeNode;

import java.util.Collection;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Empty Ref Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link testmodel1.impl.EmptyRefNodeImpl#getMoreReferences <em>More References</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EmptyRefNodeImpl extends TreeNodeImpl implements EmptyRefNode
{
  /**
   * The cached value of the '{@link #getMoreReferences() <em>More References</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMoreReferences()
   * @generated
   * @ordered
   */
  protected EList moreReferences = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EmptyRefNodeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EClass eStaticClass()
  {
    return TestModel1Package.Literals.EMPTY_REF_NODE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getMoreReferences()
  {
    cdoLoad();
    if (moreReferences == null)
    {
      moreReferences = new EObjectResolvingEList(TreeNode.class, this,
          TestModel1Package.EMPTY_REF_NODE__MORE_REFERENCES);
    }
    return moreReferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case TestModel1Package.EMPTY_REF_NODE__MORE_REFERENCES:
        return getMoreReferences();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case TestModel1Package.EMPTY_REF_NODE__MORE_REFERENCES:
        getMoreReferences().clear();
        getMoreReferences().addAll((Collection) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case TestModel1Package.EMPTY_REF_NODE__MORE_REFERENCES:
        getMoreReferences().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case TestModel1Package.EMPTY_REF_NODE__MORE_REFERENCES:
        return moreReferences != null && !moreReferences.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //EmptyRefNodeImpl