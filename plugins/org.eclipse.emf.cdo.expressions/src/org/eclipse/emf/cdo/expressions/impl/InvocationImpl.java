/**
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.Invocation;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Invocation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.InvocationImpl#getArguments <em>Arguments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.InvocationImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class InvocationImpl extends CDOObjectImpl implements Invocation
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InvocationImpl()
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
    return ExpressionsPackage.Literals.INVOCATION;
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
  @SuppressWarnings("unchecked")
  public EList<Expression> getArguments()
  {
    return (EList<Expression>)eDynamicGet(ExpressionsPackage.INVOCATION__ARGUMENTS,
        ExpressionsPackage.Literals.INVOCATION__ARGUMENTS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getName()
  {
    return (Expression)eDynamicGet(ExpressionsPackage.INVOCATION__NAME, ExpressionsPackage.Literals.INVOCATION__NAME,
        true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetName(Expression newName, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newName, ExpressionsPackage.INVOCATION__NAME, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(Expression newName)
  {
    eDynamicSet(ExpressionsPackage.INVOCATION__NAME, ExpressionsPackage.Literals.INVOCATION__NAME, newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public Object evaluate(EvaluationContext context)
  {
    String name = (String)getName().evaluate(context);

    EList<Object> arguments = new BasicEList<Object>();
    for (Expression argument : getArguments())
    {
      arguments.add(argument.evaluate(context));
    }

    try
    {
      return evaluate(context, name, arguments);
    }
    catch (InvocationTargetException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  protected abstract Object evaluate(EvaluationContext context, String name, EList<Object> arguments)
      throws InvocationTargetException;

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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      return ((InternalEList<?>)getArguments()).basicRemove(otherEnd, msgs);
    case ExpressionsPackage.INVOCATION__NAME:
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      return getArguments();
    case ExpressionsPackage.INVOCATION__NAME:
      return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      getArguments().clear();
      getArguments().addAll((Collection<? extends Expression>)newValue);
      return;
    case ExpressionsPackage.INVOCATION__NAME:
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      getArguments().clear();
      return;
    case ExpressionsPackage.INVOCATION__NAME:
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      return !getArguments().isEmpty();
    case ExpressionsPackage.INVOCATION__NAME:
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
    case ExpressionsPackage.INVOCATION___EVALUATE__EVALUATIONCONTEXT:
      return evaluate((EvaluationContext)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

} // InvocationImpl
