/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.cdo.client.CDOPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;


/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see testmodel1.TestModel1Factory
 * @model kind="package"
 * @generated
 */
public interface TestModel1Package extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "testmodel1";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/cdo/2006/TestModel1";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "testmodel1";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  TestModel1Package eINSTANCE = testmodel1.impl.TestModel1PackageImpl.init();

  /**
   * The meta object id for the '{@link testmodel1.impl.TreeNodeImpl <em>Tree Node</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see testmodel1.impl.TreeNodeImpl
   * @see testmodel1.impl.TestModel1PackageImpl#getTreeNode()
   * @generated
   */
  int TREE_NODE = 0;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_NODE__PARENT = CDOPackage.CDO_PERSISTENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_NODE__CHILDREN = CDOPackage.CDO_PERSISTENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>References</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_NODE__REFERENCES = CDOPackage.CDO_PERSISTENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Boolean Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_NODE__BOOLEAN_FEATURE = CDOPackage.CDO_PERSISTENT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Int Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_NODE__INT_FEATURE = CDOPackage.CDO_PERSISTENT_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>String Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_NODE__STRING_FEATURE = CDOPackage.CDO_PERSISTENT_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>Tree Node</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TREE_NODE_FEATURE_COUNT = CDOPackage.CDO_PERSISTENT_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link testmodel1.impl.ExtendedNodeImpl <em>Extended Node</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see testmodel1.impl.ExtendedNodeImpl
   * @see testmodel1.impl.TestModel1PackageImpl#getExtendedNode()
   * @generated
   */
  int EXTENDED_NODE = 1;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__PARENT = TREE_NODE__PARENT;

  /**
   * The feature id for the '<em><b>Children</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__CHILDREN = TREE_NODE__CHILDREN;

  /**
   * The feature id for the '<em><b>References</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__REFERENCES = TREE_NODE__REFERENCES;

  /**
   * The feature id for the '<em><b>Boolean Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__BOOLEAN_FEATURE = TREE_NODE__BOOLEAN_FEATURE;

  /**
   * The feature id for the '<em><b>Int Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__INT_FEATURE = TREE_NODE__INT_FEATURE;

  /**
   * The feature id for the '<em><b>String Feature</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__STRING_FEATURE = TREE_NODE__STRING_FEATURE;

  /**
   * The feature id for the '<em><b>Bidi Source</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__BIDI_SOURCE = TREE_NODE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Bidi Target</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__BIDI_TARGET = TREE_NODE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>String Feature2</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE__STRING_FEATURE2 = TREE_NODE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Extended Node</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXTENDED_NODE_FEATURE_COUNT = TREE_NODE_FEATURE_COUNT + 3;

  /**
   * Returns the meta object for class '{@link testmodel1.TreeNode <em>Tree Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Tree Node</em>'.
   * @see testmodel1.TreeNode
   * @generated
   */
  EClass getTreeNode();

  /**
   * Returns the meta object for the container reference '{@link testmodel1.TreeNode#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see testmodel1.TreeNode#getParent()
   * @see #getTreeNode()
   * @generated
   */
  EReference getTreeNode_Parent();

  /**
   * Returns the meta object for the containment reference list '{@link testmodel1.TreeNode#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Children</em>'.
   * @see testmodel1.TreeNode#getChildren()
   * @see #getTreeNode()
   * @generated
   */
  EReference getTreeNode_Children();

  /**
   * Returns the meta object for the reference list '{@link testmodel1.TreeNode#getReferences <em>References</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>References</em>'.
   * @see testmodel1.TreeNode#getReferences()
   * @see #getTreeNode()
   * @generated
   */
  EReference getTreeNode_References();

  /**
   * Returns the meta object for the attribute '{@link testmodel1.TreeNode#isBooleanFeature <em>Boolean Feature</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Boolean Feature</em>'.
   * @see testmodel1.TreeNode#isBooleanFeature()
   * @see #getTreeNode()
   * @generated
   */
  EAttribute getTreeNode_BooleanFeature();

  /**
   * Returns the meta object for the attribute '{@link testmodel1.TreeNode#getIntFeature <em>Int Feature</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Int Feature</em>'.
   * @see testmodel1.TreeNode#getIntFeature()
   * @see #getTreeNode()
   * @generated
   */
  EAttribute getTreeNode_IntFeature();

  /**
   * Returns the meta object for the attribute '{@link testmodel1.TreeNode#getStringFeature <em>String Feature</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>String Feature</em>'.
   * @see testmodel1.TreeNode#getStringFeature()
   * @see #getTreeNode()
   * @generated
   */
  EAttribute getTreeNode_StringFeature();

  /**
   * Returns the meta object for class '{@link testmodel1.ExtendedNode <em>Extended Node</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Extended Node</em>'.
   * @see testmodel1.ExtendedNode
   * @generated
   */
  EClass getExtendedNode();

  /**
   * Returns the meta object for the reference list '{@link testmodel1.ExtendedNode#getBidiSource <em>Bidi Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Bidi Source</em>'.
   * @see testmodel1.ExtendedNode#getBidiSource()
   * @see #getExtendedNode()
   * @generated
   */
  EReference getExtendedNode_BidiSource();

  /**
   * Returns the meta object for the reference list '{@link testmodel1.ExtendedNode#getBidiTarget <em>Bidi Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Bidi Target</em>'.
   * @see testmodel1.ExtendedNode#getBidiTarget()
   * @see #getExtendedNode()
   * @generated
   */
  EReference getExtendedNode_BidiTarget();

  /**
   * Returns the meta object for the attribute '{@link testmodel1.ExtendedNode#getStringFeature2 <em>String Feature2</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>String Feature2</em>'.
   * @see testmodel1.ExtendedNode#getStringFeature2()
   * @see #getExtendedNode()
   * @generated
   */
  EAttribute getExtendedNode_StringFeature2();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  TestModel1Factory getTestModel1Factory();


  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link testmodel1.impl.TreeNodeImpl <em>Tree Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see testmodel1.impl.TreeNodeImpl
     * @see testmodel1.impl.TestModel1PackageImpl#getTreeNode()
     * @generated
     */
    EClass TREE_NODE = eINSTANCE.getTreeNode();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TREE_NODE__PARENT = eINSTANCE.getTreeNode_Parent();

    /**
     * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TREE_NODE__CHILDREN = eINSTANCE.getTreeNode_Children();

    /**
     * The meta object literal for the '<em><b>References</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TREE_NODE__REFERENCES = eINSTANCE.getTreeNode_References();

    /**
     * The meta object literal for the '<em><b>Boolean Feature</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TREE_NODE__BOOLEAN_FEATURE = eINSTANCE.getTreeNode_BooleanFeature();

    /**
     * The meta object literal for the '<em><b>Int Feature</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TREE_NODE__INT_FEATURE = eINSTANCE.getTreeNode_IntFeature();

    /**
     * The meta object literal for the '<em><b>String Feature</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TREE_NODE__STRING_FEATURE = eINSTANCE.getTreeNode_StringFeature();

    /**
     * The meta object literal for the '{@link testmodel1.impl.ExtendedNodeImpl <em>Extended Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see testmodel1.impl.ExtendedNodeImpl
     * @see testmodel1.impl.TestModel1PackageImpl#getExtendedNode()
     * @generated
     */
    EClass EXTENDED_NODE = eINSTANCE.getExtendedNode();

    /**
     * The meta object literal for the '<em><b>Bidi Source</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXTENDED_NODE__BIDI_SOURCE = eINSTANCE.getExtendedNode_BidiSource();

    /**
     * The meta object literal for the '<em><b>Bidi Target</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXTENDED_NODE__BIDI_TARGET = eINSTANCE.getExtendedNode_BidiTarget();

    /**
     * The meta object literal for the '<em><b>String Feature2</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EXTENDED_NODE__STRING_FEATURE2 = eINSTANCE.getExtendedNode_StringFeature2();

  }

} //TestModel1Package
