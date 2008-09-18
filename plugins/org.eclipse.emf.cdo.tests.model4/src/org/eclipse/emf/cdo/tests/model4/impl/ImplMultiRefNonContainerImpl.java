/**
 * <copyright>
 * </copyright>
 *
 * $Id: ImplMultiRefNonContainerImpl.java,v 1.3 2008-09-18 12:56:49 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Multi Ref Non Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefNonContainerImpl#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ImplMultiRefNonContainerImpl extends CDOObjectImpl implements ImplMultiRefNonContainer
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ImplMultiRefNonContainerImpl()
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
    return model4Package.Literals.IMPL_MULTI_REF_NON_CONTAINER;
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
  @SuppressWarnings("unchecked")
  public EList<IMultiRefNonContainedElement> getElements()
  {
    return (EList<IMultiRefNonContainedElement>)eGet(
        model4interfacesPackage.Literals.IMULTI_REF_NON_CONTAINER__ELEMENTS, true);
  }

} // ImplMultiRefNonContainerImpl
