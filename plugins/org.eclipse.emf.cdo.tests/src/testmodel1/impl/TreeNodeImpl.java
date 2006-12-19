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
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getParent2 <em>Parent2</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getChildren2 <em>Children2</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getReferences <em>References</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getSourceRef <em>Source Ref</em>}</li>
 *   <li>{@link testmodel1.impl.TreeNodeImpl#getTargetRef <em>Target Ref</em>}</li>
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
   * The cached value of the '{@link #getChildren2() <em>Children2</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildren2()
   * @generated
   * @ordered
   */
  protected EList children2 = null;

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
   * The cached value of the '{@link #getReference() <em>Reference</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReference()
   * @generated
   * @ordered
   */
  protected TreeNode reference = null;

  /**
   * The cached value of the '{@link #getSourceRef() <em>Source Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceRef()
   * @generated
   * @ordered
   */
  protected TreeNode sourceRef = null;

  /**
   * The cached value of the '{@link #getTargetRef() <em>Target Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetRef()
   * @generated
   * @ordered
   */
  protected TreeNode targetRef = null;

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
  public TreeNode getParent2()
  {
    cdoLoad();
    if (eContainerFeatureID != TestModel1Package.TREE_NODE__PARENT2) return null;
    return (TreeNode) eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent2(TreeNode newParent2, NotificationChain msgs)
  {
    cdoLoad();
    msgs = eBasicSetContainer((InternalEObject) newParent2, TestModel1Package.TREE_NODE__PARENT2,
        msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParent2(TreeNode newParent2)
  {

    if (newParent2 != eInternalContainer()
        || (eContainerFeatureID != TestModel1Package.TREE_NODE__PARENT2 && newParent2 != null))
    {
      if (EcoreUtil.isAncestor(this, newParent2))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null) msgs = eBasicRemoveFromContainer(msgs);
      if (newParent2 != null)
        msgs = ((InternalEObject) newParent2).eInverseAdd(this,
            TestModel1Package.TREE_NODE__CHILDREN2, TreeNode.class, msgs);
      msgs = basicSetParent2(newParent2, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, TestModel1Package.TREE_NODE__PARENT2,
          newParent2, newParent2));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList getChildren2()
  {
    cdoLoad();
    if (children2 == null)
    {
      children2 = new EObjectContainmentWithInverseEList(TreeNode.class, this,
          TestModel1Package.TREE_NODE__CHILDREN2, TestModel1Package.TREE_NODE__PARENT2);
    }
    return children2;
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
  public TreeNode getReference()
  {

    if (reference != null && reference.eIsProxy())
    {
      InternalEObject oldReference = (InternalEObject) reference;
      reference = (TreeNode) eResolveProxy(oldReference);
      if (reference != oldReference)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              TestModel1Package.TREE_NODE__REFERENCE, oldReference, reference));
      }
    }
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TreeNode basicGetReference()
  {
    cdoLoad();
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setReference(TreeNode newReference)
  {
    cdoLoad();
    TreeNode oldReference = reference;
    reference = newReference;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, TestModel1Package.TREE_NODE__REFERENCE,
          oldReference, reference));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TreeNode getSourceRef()
  {

    if (sourceRef != null && sourceRef.eIsProxy())
    {
      InternalEObject oldSourceRef = (InternalEObject) sourceRef;
      sourceRef = (TreeNode) eResolveProxy(oldSourceRef);
      if (sourceRef != oldSourceRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              TestModel1Package.TREE_NODE__SOURCE_REF, oldSourceRef, sourceRef));
      }
    }
    return sourceRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TreeNode basicGetSourceRef()
  {
    cdoLoad();
    return sourceRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSourceRef(TreeNode newSourceRef, NotificationChain msgs)
  {
    cdoLoad();
    TreeNode oldSourceRef = sourceRef;
    sourceRef = newSourceRef;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          TestModel1Package.TREE_NODE__SOURCE_REF, oldSourceRef, newSourceRef);
      if (msgs == null)
        msgs = notification;
      else
        msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSourceRef(TreeNode newSourceRef)
  {

    if (newSourceRef != sourceRef)
    {
      NotificationChain msgs = null;
      if (sourceRef != null)
        msgs = ((InternalEObject) sourceRef).eInverseRemove(this,
            TestModel1Package.TREE_NODE__TARGET_REF, TreeNode.class, msgs);
      if (newSourceRef != null)
        msgs = ((InternalEObject) newSourceRef).eInverseAdd(this,
            TestModel1Package.TREE_NODE__TARGET_REF, TreeNode.class, msgs);
      msgs = basicSetSourceRef(newSourceRef, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          TestModel1Package.TREE_NODE__SOURCE_REF, newSourceRef, newSourceRef));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TreeNode getTargetRef()
  {

    if (targetRef != null && targetRef.eIsProxy())
    {
      InternalEObject oldTargetRef = (InternalEObject) targetRef;
      targetRef = (TreeNode) eResolveProxy(oldTargetRef);
      if (targetRef != oldTargetRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              TestModel1Package.TREE_NODE__TARGET_REF, oldTargetRef, targetRef));
      }
    }
    return targetRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TreeNode basicGetTargetRef()
  {
    cdoLoad();
    return targetRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTargetRef(TreeNode newTargetRef, NotificationChain msgs)
  {
    cdoLoad();
    TreeNode oldTargetRef = targetRef;
    targetRef = newTargetRef;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          TestModel1Package.TREE_NODE__TARGET_REF, oldTargetRef, newTargetRef);
      if (msgs == null)
        msgs = notification;
      else
        msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTargetRef(TreeNode newTargetRef)
  {

    if (newTargetRef != targetRef)
    {
      NotificationChain msgs = null;
      if (targetRef != null)
        msgs = ((InternalEObject) targetRef).eInverseRemove(this,
            TestModel1Package.TREE_NODE__SOURCE_REF, TreeNode.class, msgs);
      if (newTargetRef != null)
        msgs = ((InternalEObject) newTargetRef).eInverseAdd(this,
            TestModel1Package.TREE_NODE__SOURCE_REF, TreeNode.class, msgs);
      msgs = basicSetTargetRef(newTargetRef, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          TestModel1Package.TREE_NODE__TARGET_REF, newTargetRef, newTargetRef));
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
      case TestModel1Package.TREE_NODE__PARENT2:
        if (eInternalContainer() != null) msgs = eBasicRemoveFromContainer(msgs);
        return basicSetParent2((TreeNode) otherEnd, msgs);
      case TestModel1Package.TREE_NODE__CHILDREN2:
        return ((InternalEList) getChildren2()).basicAdd(otherEnd, msgs);
      case TestModel1Package.TREE_NODE__SOURCE_REF:
        if (sourceRef != null)
          msgs = ((InternalEObject) sourceRef).eInverseRemove(this,
              TestModel1Package.TREE_NODE__TARGET_REF, TreeNode.class, msgs);
        return basicSetSourceRef((TreeNode) otherEnd, msgs);
      case TestModel1Package.TREE_NODE__TARGET_REF:
        if (targetRef != null)
          msgs = ((InternalEObject) targetRef).eInverseRemove(this,
              TestModel1Package.TREE_NODE__SOURCE_REF, TreeNode.class, msgs);
        return basicSetTargetRef((TreeNode) otherEnd, msgs);
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
      case TestModel1Package.TREE_NODE__PARENT2:
        return basicSetParent2(null, msgs);
      case TestModel1Package.TREE_NODE__CHILDREN2:
        return ((InternalEList) getChildren2()).basicRemove(otherEnd, msgs);
      case TestModel1Package.TREE_NODE__SOURCE_REF:
        return basicSetSourceRef(null, msgs);
      case TestModel1Package.TREE_NODE__TARGET_REF:
        return basicSetTargetRef(null, msgs);
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
      case TestModel1Package.TREE_NODE__PARENT2:
        return eInternalContainer().eInverseRemove(this, TestModel1Package.TREE_NODE__CHILDREN2,
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
      case TestModel1Package.TREE_NODE__PARENT2:
        return getParent2();
      case TestModel1Package.TREE_NODE__CHILDREN2:
        return getChildren2();
      case TestModel1Package.TREE_NODE__REFERENCES:
        return getReferences();
      case TestModel1Package.TREE_NODE__REFERENCE:
        if (resolve) return getReference();
        return basicGetReference();
      case TestModel1Package.TREE_NODE__SOURCE_REF:
        if (resolve) return getSourceRef();
        return basicGetSourceRef();
      case TestModel1Package.TREE_NODE__TARGET_REF:
        if (resolve) return getTargetRef();
        return basicGetTargetRef();
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
      case TestModel1Package.TREE_NODE__PARENT2:
        setParent2((TreeNode) newValue);
        return;
      case TestModel1Package.TREE_NODE__CHILDREN2:
        getChildren2().clear();
        getChildren2().addAll((Collection) newValue);
        return;
      case TestModel1Package.TREE_NODE__REFERENCES:
        getReferences().clear();
        getReferences().addAll((Collection) newValue);
        return;
      case TestModel1Package.TREE_NODE__REFERENCE:
        setReference((TreeNode) newValue);
        return;
      case TestModel1Package.TREE_NODE__SOURCE_REF:
        setSourceRef((TreeNode) newValue);
        return;
      case TestModel1Package.TREE_NODE__TARGET_REF:
        setTargetRef((TreeNode) newValue);
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
      case TestModel1Package.TREE_NODE__PARENT2:
        setParent2((TreeNode) null);
        return;
      case TestModel1Package.TREE_NODE__CHILDREN2:
        getChildren2().clear();
        return;
      case TestModel1Package.TREE_NODE__REFERENCES:
        getReferences().clear();
        return;
      case TestModel1Package.TREE_NODE__REFERENCE:
        setReference((TreeNode) null);
        return;
      case TestModel1Package.TREE_NODE__SOURCE_REF:
        setSourceRef((TreeNode) null);
        return;
      case TestModel1Package.TREE_NODE__TARGET_REF:
        setTargetRef((TreeNode) null);
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
      case TestModel1Package.TREE_NODE__PARENT2:
        return getParent2() != null;
      case TestModel1Package.TREE_NODE__CHILDREN2:
        return children2 != null && !children2.isEmpty();
      case TestModel1Package.TREE_NODE__REFERENCES:
        return references != null && !references.isEmpty();
      case TestModel1Package.TREE_NODE__REFERENCE:
        return reference != null;
      case TestModel1Package.TREE_NODE__SOURCE_REF:
        return sourceRef != null;
      case TestModel1Package.TREE_NODE__TARGET_REF:
        return targetRef != null;
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