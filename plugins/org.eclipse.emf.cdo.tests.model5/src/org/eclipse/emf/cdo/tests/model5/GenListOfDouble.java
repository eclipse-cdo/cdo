/**
 * <copyright>
 * </copyright>
 *
 * $Id: GenListOfDouble.java,v 1.1 2008-12-03 03:06:15 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model5;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Gen List Of Double</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.GenListOfDouble#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getGenListOfDouble()
 * @model
 * @generated
 */
public interface GenListOfDouble extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' attribute list. The list contents are of type
   * {@link java.lang.Double}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' attribute list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getGenListOfDouble_Elements()
   * @model
   * @generated
   */
  EList<Double> getElements();

} // GenListOfDouble
