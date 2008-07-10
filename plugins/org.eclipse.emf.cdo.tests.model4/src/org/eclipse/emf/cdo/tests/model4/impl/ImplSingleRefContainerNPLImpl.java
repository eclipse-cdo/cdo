/**
 * <copyright>
 * </copyright>
 *
 * $Id: ImplSingleRefContainerNPLImpl.java,v 1.2 2008-07-10 15:57:40 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Single Ref Container NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.ImplSingleRefContainerNPLImpl#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ImplSingleRefContainerNPLImpl extends CDOObjectImpl implements ImplSingleRefContainerNPL
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ImplSingleRefContainerNPLImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return model4Package.Literals.IMPL_SINGLE_REF_CONTAINER_NPL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public IContainedElementNoParentLink getElement()
  {
    return (IContainedElementNoParentLink)eGet(model4interfacesPackage.Literals.ISINGLE_REF_CONTAINER_NPL__ELEMENT,
        true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setElement(IContainedElementNoParentLink newElement)
  {
    eSet(model4interfacesPackage.Literals.ISINGLE_REF_CONTAINER_NPL__ELEMENT, newElement);
  }

} // ImplSingleRefContainerNPLImpl
