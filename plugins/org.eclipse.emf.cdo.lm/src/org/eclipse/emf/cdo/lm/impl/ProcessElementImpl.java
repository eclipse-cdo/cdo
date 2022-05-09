/**
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.ProcessElement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Process Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ProcessElementImpl extends ModelElementImpl implements ProcessElement
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ProcessElementImpl()
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
    return LMPackage.Literals.PROCESS_ELEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.Process getProcess()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.System getSystem()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
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
    case LMPackage.PROCESS_ELEMENT___GET_PROCESS:
      return getProcess();
    case LMPackage.PROCESS_ELEMENT___GET_SYSTEM:
      return getSystem();
    }
    return super.eInvoke(operationID, arguments);
  }

} // ProcessElementImpl
