/**
 * <copyright>
 * </copyright>
 *
 * $Id: IMultiRefContainerNPL.java,v 1.3 2008-09-18 12:56:27 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>IMulti Ref Container NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefContainerNPL()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IMultiRefContainerNPL extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefContainerNPL_Elements()
   * @model containment="true"
   * @generated
   */
  EList<IContainedElementNoParentLink> getElements();

} // IMultiRefContainerNPL
