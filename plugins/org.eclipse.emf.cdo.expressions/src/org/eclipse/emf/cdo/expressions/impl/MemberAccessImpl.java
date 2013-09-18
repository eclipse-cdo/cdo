/**
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.MemberAccess;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Member Access</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.MemberAccessImpl#getObject <em>Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.MemberAccessImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MemberAccessImpl extends CDOObjectImpl implements MemberAccess
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MemberAccessImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ExpressionsPackage.Literals.MEMBER_ACCESS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getObject()
  {
    return (Expression)eDynamicGet(ExpressionsPackage.MEMBER_ACCESS__OBJECT,
        ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetObject(Expression newObject, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newObject, ExpressionsPackage.MEMBER_ACCESS__OBJECT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setObject(Expression newObject)
  {
    eDynamicSet(ExpressionsPackage.MEMBER_ACCESS__OBJECT, ExpressionsPackage.Literals.MEMBER_ACCESS__OBJECT, newObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getName()
  {
    return (Expression)eDynamicGet(ExpressionsPackage.MEMBER_ACCESS__NAME,
        ExpressionsPackage.Literals.MEMBER_ACCESS__NAME, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetName(Expression newName, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newName, ExpressionsPackage.MEMBER_ACCESS__NAME, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(Expression newName)
  {
    eDynamicSet(ExpressionsPackage.MEMBER_ACCESS__NAME, ExpressionsPackage.Literals.MEMBER_ACCESS__NAME, newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object evaluate(EvaluationContext context)
  {
    EObject object = (EObject)getObject().evaluate(context);
    String name = (String)getName().evaluate(context);

    EStructuralFeature feature = object.eClass().getEStructuralFeature(name);
    return object.eGet(feature);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      return basicSetObject(null, msgs);
    case ExpressionsPackage.MEMBER_ACCESS__NAME:
      return basicSetName(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      return getObject();
    case ExpressionsPackage.MEMBER_ACCESS__NAME:
      return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      setObject((Expression)newValue);
      return;
    case ExpressionsPackage.MEMBER_ACCESS__NAME:
      setName((Expression)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      setObject((Expression)null);
      return;
    case ExpressionsPackage.MEMBER_ACCESS__NAME:
      setName((Expression)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_ACCESS__OBJECT:
      return getObject() != null;
    case ExpressionsPackage.MEMBER_ACCESS__NAME:
      return getName() != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case ExpressionsPackage.MEMBER_ACCESS___EVALUATE__EVALUATIONCONTEXT:
      return evaluate((EvaluationContext)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

} // MemberAccessImpl
