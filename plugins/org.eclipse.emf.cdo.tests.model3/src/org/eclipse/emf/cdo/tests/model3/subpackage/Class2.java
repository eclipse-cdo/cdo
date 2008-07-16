/**
 * <copyright>
 * </copyright>
 *
 * $Id: Class2.java,v 1.2.4.2 2008-07-16 16:34:55 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3.subpackage;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.tests.model3.Class1;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object ' <em><b>Class2</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.subpackage.Class2#getClass1 <em> Class1</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage#getClass2()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Class2 extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Class1</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model3.Class1} . It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model3.Class1#getClass2 <em>Class2</em>} '. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Class1</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Class1</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage#getClass2_Class1()
   * @see org.eclipse.emf.cdo.tests.model3.Class1#getClass2
   * @model opposite="class2"
   * @generated
   */
  EList<Class1> getClass1();

} // Class2
