/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.impl;


import org.eclipse.emf.cdo.client.impl.CDOPersistentImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import testmodel1.TestModel1Package;
import testmodel1.TreeNode;

import java.util.Collection;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tree Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getReferences <em>References</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#isBooleanFeature <em>Boolean Feature</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getIntFeature <em>Int Feature</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getStringFeature <em>String Feature</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TreeNodeImpl extends CDOPersistentImpl implements TreeNode
{
  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList children = null;

  /**
   * The cached value of the '{@link #getReferences() <em>References</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReferences()
   * @generated
   * @ordered
   */
  protected EList references = null;

  /**
   * The default value of the '{@link #isBooleanFeature() <em>Boolean Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isBooleanFeature()
   * @generated
   * @ordered
   */
  protected static final boolean BOOLEAN_FEATURE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isBooleanFeature() <em>Boolean Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isBooleanFeature()
   * @generated
   * @ordered
   */
  protected boolean booleanFeature = BOOLEAN_FEATURE_EDEFAULT;

  /**
   * The default value of the '{@link #getIntFeature() <em>Int Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIntFeature()
   * @generated
   * @ordered
   */
  protected static final int INT_FEATURE_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getIntFeature() <em>Int Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIntFeature()
   * @generated
   * @ordered
   */
  protected int intFeature = INT_FEATURE_EDEFAULT;

  /**
   * The default value of the '{@link #getStringFeature() <em>String Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStringFeature()
   * @generated
   * @ordered
   */
  protected static final String STRING_FEATURE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getStringFeature() <em>String Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStringFeature()
   * @generated
   * @ordered
   */
  protected String stringFeature = STRING_FEATURE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TreeNodeImpl()
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
    return TestModel1Package.Literals.TREE_NODE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TreeNode getParent()
  {
    cdoLoad();
    if (eContainerFeatureID != TestModel1Package.TREE_NODE__PARENT) return null;
    return (TreeNode) eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(TreeNode newParent, NotificationChain msgs)
  {
    cdoLoad();
    msgs = eBasicSetContainer((InternalEObject) newParent, TestModel1Package.TREE_NODE__PARENT,
        msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParent(TreeNode newParent)
  {

    if (newParent != eInternalContainer()
        || (eContainerFeatureID != TestModel1Package.TREE_NODE__PARENT && newParent != null))
    {
      if (EcoreUtil.isAncestor(this, newParent))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null) msgs = eBasicRemoveFromContainer(msgs);
      if (newParent != null)
        msgs = ((InternalEObject) newParent).eInverseAdd(this,
            TestModel1Package.TREE_NODE__CHILDREN, TreeNode.class, msgs);
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, TestModel1Package.TREE_NODE__PARENT,
          newParent, newParent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getChildren()
  {
    cdoLoad();
    if (children == null)
    {
      children = new EObjectContainmentWithInverseEList(TreeNode.class, this,
          TestModel1Package.TREE_NODE__CHILDREN, TestModel1Package.TREE_NODE__PARENT);
    }
    return children;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getReferences()
  {
    cdoLoad();
    if (references == null)
    {
      references = new EObjectResolvingEList(TreeNode.class, this,
          TestModel1Package.TREE_NODE__REFERENCES);
    }
    return references;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isBooleanFeature()
  {
    cdoLoad();
    return booleanFeature;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBooleanFeature(boolean newBooleanFeature)
  {
    cdoLoad();
    boolean oldBooleanFeature = booleanFeature;
    booleanFeature = newBooleanFeature;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          TestModel1Package.TREE_NODE__BOOLEAN_FEATURE, oldBooleanFeature, booleanFeature));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getIntFeature()
  {
    cdoLoad();
    return intFeature;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setIntFeature(int newIntFeature)
  {
    cdoLoad();
    int oldIntFeature = intFeature;
    intFeature = newIntFeature;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          TestModel1Package.TREE_NODE__INT_FEATURE, oldIntFeature, intFeature));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getStringFeature()
  {
    cdoLoad();
    return stringFeature;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStringFeature(String newStringFeature)
  {
    cdoLoad();
    String oldStringFeature = stringFeature;
    stringFeature = newStringFeature;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          TestModel1Package.TREE_NODE__STRING_FEATURE, oldStringFeature, stringFeature));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID,
      NotificationChain msgs)
  {
    switch (featureID)
    {
      case TestModel1Package.TREE_NODE__PARENT:
        if (eInternalContainer() != null) msgs = eBasicRemoveFromContainer(msgs);
        return basicSetParent((TreeNode) otherEnd, msgs);
      case TestModel1Package.TREE_NODE__CHILDREN:
        return ((InternalEList) getChildren()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
      NotificationChain msgs)
  {
    switch (featureID)
    {
      case TestModel1Package.TREE_NODE__PARENT:
        return basicSetParent(null, msgs);
      case TestModel1Package.TREE_NODE__CHILDREN:
        return ((InternalEList) getChildren()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID)
    {
      case TestModel1Package.TREE_NODE__PARENT:
        return eInternalContainer().eInverseRemove(this, TestModel1Package.TREE_NODE__CHILDREN,
            TreeNode.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
      case TestModel1Package.TREE_NODE__PARENT:
        return getParent();
      case TestModel1Package.TREE_NODE__CHILDREN:
        return getChildren();
      case TestModel1Package.TREE_NODE__REFERENCES:
        return getReferences();
      case TestModel1Package.TREE_NODE__BOOLEAN_FEATURE:
        return isBooleanFeature() ? Boolean.TRUE : Boolean.FALSE;
      case TestModel1Package.TREE_NODE__INT_FEATURE:
        return new Integer(getIntFeature());
      case TestModel1Package.TREE_NODE__STRING_FEATURE:
        return getStringFeature();
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
      case TestModel1Package.TREE_NODE__PARENT:
        setParent((TreeNode) newValue);
        return;
      case TestModel1Package.TREE_NODE__CHILDREN:
        getChildren().clear();
        getChildren().addAll((Collection) newValue);
        return;
      case TestModel1Package.TREE_NODE__REFERENCES:
        getReferences().clear();
        getReferences().addAll((Collection) newValue);
        return;
      case TestModel1Package.TREE_NODE__BOOLEAN_FEATURE:
        setBooleanFeature(((Boolean) newValue).booleanValue());
        return;
      case TestModel1Package.TREE_NODE__INT_FEATURE:
        setIntFeature(((Integer) newValue).intValue());
        return;
      case TestModel1Package.TREE_NODE__STRING_FEATURE:
        setStringFeature((String) newValue);
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
      case TestModel1Package.TREE_NODE__PARENT:
        setParent((TreeNode) null);
        return;
      case TestModel1Package.TREE_NODE__CHILDREN:
        getChildren().clear();
        return;
      case TestModel1Package.TREE_NODE__REFERENCES:
        getReferences().clear();
        return;
      case TestModel1Package.TREE_NODE__BOOLEAN_FEATURE:
        setBooleanFeature(BOOLEAN_FEATURE_EDEFAULT);
        return;
      case TestModel1Package.TREE_NODE__INT_FEATURE:
        setIntFeature(INT_FEATURE_EDEFAULT);
        return;
      case TestModel1Package.TREE_NODE__STRING_FEATURE:
        setStringFeature(STRING_FEATURE_EDEFAULT);
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
      case TestModel1Package.TREE_NODE__PARENT:
        return getParent() != null;
      case TestModel1Package.TREE_NODE__CHILDREN:
        return children != null && !children.isEmpty();
      case TestModel1Package.TREE_NODE__REFERENCES:
        return references != null && !references.isEmpty();
      case TestModel1Package.TREE_NODE__BOOLEAN_FEATURE:
        return booleanFeature != BOOLEAN_FEATURE_EDEFAULT;
      case TestModel1Package.TREE_NODE__INT_FEATURE:
        return intFeature != INT_FEATURE_EDEFAULT;
      case TestModel1Package.TREE_NODE__STRING_FEATURE:
        return STRING_FEATURE_EDEFAULT == null ? stringFeature != null : !STRING_FEATURE_EDEFAULT
            .equals(stringFeature);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (booleanFeature: ");
    result.append(booleanFeature);
    result.append(", intFeature: ");
    result.append(intFeature);
    result.append(", stringFeature: ");
    result.append(stringFeature);
    result.append(')');
    return result.toString();
  }

} //TreeNodeImpl