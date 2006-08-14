/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Extended Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link testmodel1.ExtendedNode#getBidiSource <em>Bidi Source</em>}</li>
 *   <li>{@link testmodel1.ExtendedNode#getBidiTarget <em>Bidi Target</em>}</li>
 *   <li>{@link testmodel1.ExtendedNode#getStringFeature2 <em>String Feature2</em>}</li>
 * </ul>
 * </p>
 *
 * @see testmodel1.TestModel1Package#getExtendedNode()
 * @model
 * @generated
 */
public interface ExtendedNode extends TreeNode
{
  /**
   * Returns the value of the '<em><b>Bidi Source</b></em>' reference list.
   * The list contents are of type {@link testmodel1.ExtendedNode}.
   * It is bidirectional and its opposite is '{@link testmodel1.ExtendedNode#getBidiTarget <em>Bidi Target</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Bidi Source</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Bidi Source</em>' reference list.
   * @see testmodel1.TestModel1Package#getExtendedNode_BidiSource()
   * @see testmodel1.ExtendedNode#getBidiTarget
   * @model type="testmodel1.ExtendedNode" opposite="bidiTarget"
   * @generated
   */
  EList getBidiSource();

  /**
   * Returns the value of the '<em><b>Bidi Target</b></em>' reference list.
   * The list contents are of type {@link testmodel1.ExtendedNode}.
   * It is bidirectional and its opposite is '{@link testmodel1.ExtendedNode#getBidiSource <em>Bidi Source</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Bidi Target</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Bidi Target</em>' reference list.
   * @see testmodel1.TestModel1Package#getExtendedNode_BidiTarget()
   * @see testmodel1.ExtendedNode#getBidiSource
   * @model type="testmodel1.ExtendedNode" opposite="bidiSource"
   * @generated
   */
  EList getBidiTarget();

  /**
   * Returns the value of the '<em><b>String Feature2</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String Feature2</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>String Feature2</em>' attribute.
   * @see #setStringFeature2(String)
   * @see testmodel1.TestModel1Package#getExtendedNode_StringFeature2()
   * @model
   * @generated
   */
  String getStringFeature2();

  /**
   * Sets the value of the '{@link testmodel1.ExtendedNode#getStringFeature2 <em>String Feature2</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>String Feature2</em>' attribute.
   * @see #getStringFeature2()
   * @generated
   */
  void setStringFeature2(String value);

} // ExtendedNode