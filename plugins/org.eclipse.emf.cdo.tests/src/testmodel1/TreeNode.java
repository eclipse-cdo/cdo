/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.cdo.client.CDOPersistent;

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tree Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link testmodel1.TreeNode#getParent <em>Parent</em>}</li>
 *   <li>{@link testmodel1.TreeNode#getChildren <em>Children</em>}</li>
 *   <li>{@link testmodel1.TreeNode#getParent2 <em>Parent2</em>}</li>
 *   <li>{@link testmodel1.TreeNode#getChildren2 <em>Children2</em>}</li>
 *   <li>{@link testmodel1.TreeNode#getReferences <em>References</em>}</li>
 *   <li>{@link testmodel1.TreeNode#isBooleanFeature <em>Boolean Feature</em>}</li>
 *   <li>{@link testmodel1.TreeNode#getIntFeature <em>Int Feature</em>}</li>
 *   <li>{@link testmodel1.TreeNode#getStringFeature <em>String Feature</em>}</li>
 * </ul>
 * </p>
 *
 * @see testmodel1.TestModel1Package#getTreeNode()
 * @model
 * @generated
 */
public interface TreeNode extends CDOPersistent
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link testmodel1.TreeNode#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(TreeNode)
   * @see testmodel1.TestModel1Package#getTreeNode_Parent()
   * @see testmodel1.TreeNode#getChildren
   * @model opposite="children"
   * @generated
   */
  TreeNode getParent();

  /**
   * Sets the value of the '{@link testmodel1.TreeNode#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(TreeNode value);

  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list.
   * The list contents are of type {@link testmodel1.TreeNode}.
   * It is bidirectional and its opposite is '{@link testmodel1.TreeNode#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see testmodel1.TestModel1Package#getTreeNode_Children()
   * @see testmodel1.TreeNode#getParent
   * @model type="testmodel1.TreeNode" opposite="parent" containment="true"
   * @generated
   */
  EList getChildren();

  /**
   * Returns the value of the '<em><b>Parent2</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link testmodel1.TreeNode#getChildren2 <em>Children2</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent2</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent2</em>' container reference.
   * @see #setParent2(TreeNode)
   * @see testmodel1.TestModel1Package#getTreeNode_Parent2()
   * @see testmodel1.TreeNode#getChildren2
   * @model opposite="children2"
   * @generated
   */
  TreeNode getParent2();

  /**
   * Sets the value of the '{@link testmodel1.TreeNode#getParent2 <em>Parent2</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent2</em>' container reference.
   * @see #getParent2()
   * @generated
   */
  void setParent2(TreeNode value);

  /**
   * Returns the value of the '<em><b>Children2</b></em>' containment reference list.
   * The list contents are of type {@link testmodel1.TreeNode}.
   * It is bidirectional and its opposite is '{@link testmodel1.TreeNode#getParent2 <em>Parent2</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children2</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Children2</em>' containment reference list.
   * @see testmodel1.TestModel1Package#getTreeNode_Children2()
   * @see testmodel1.TreeNode#getParent2
   * @model type="testmodel1.TreeNode" opposite="parent2" containment="true"
   * @generated
   */
  EList getChildren2();

  /**
   * Returns the value of the '<em><b>References</b></em>' reference list.
   * The list contents are of type {@link testmodel1.TreeNode}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>References</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>References</em>' reference list.
   * @see testmodel1.TestModel1Package#getTreeNode_References()
   * @model type="testmodel1.TreeNode"
   * @generated
   */
  EList getReferences();

  /**
   * Returns the value of the '<em><b>Boolean Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Boolean Feature</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Boolean Feature</em>' attribute.
   * @see #setBooleanFeature(boolean)
   * @see testmodel1.TestModel1Package#getTreeNode_BooleanFeature()
   * @model
   * @generated
   */
  boolean isBooleanFeature();

  /**
   * Sets the value of the '{@link testmodel1.TreeNode#isBooleanFeature <em>Boolean Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Boolean Feature</em>' attribute.
   * @see #isBooleanFeature()
   * @generated
   */
  void setBooleanFeature(boolean value);

  /**
   * Returns the value of the '<em><b>Int Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Int Feature</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Int Feature</em>' attribute.
   * @see #setIntFeature(int)
   * @see testmodel1.TestModel1Package#getTreeNode_IntFeature()
   * @model
   * @generated
   */
  int getIntFeature();

  /**
   * Sets the value of the '{@link testmodel1.TreeNode#getIntFeature <em>Int Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Int Feature</em>' attribute.
   * @see #getIntFeature()
   * @generated
   */
  void setIntFeature(int value);

  /**
   * Returns the value of the '<em><b>String Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String Feature</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>String Feature</em>' attribute.
   * @see #setStringFeature(String)
   * @see testmodel1.TestModel1Package#getTreeNode_StringFeature()
   * @model
   * @generated
   */
  String getStringFeature();

  /**
   * Sets the value of the '{@link testmodel1.TreeNode#getStringFeature <em>String Feature</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>String Feature</em>' attribute.
   * @see #getStringFeature()
   * @generated
   */
  void setStringFeature(String value);

} // TreeNode