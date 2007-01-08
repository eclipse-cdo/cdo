/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1.impl;


import org.eclipse.emf.cdo.client.CDOPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import testmodel1.Author;
import testmodel1.Book;
import testmodel1.EmptyNode;
import testmodel1.EmptyRefNode;
import testmodel1.ExtendedNode;
import testmodel1.Root;
import testmodel1.TestModel1Factory;
import testmodel1.TestModel1Package;
import testmodel1.TreeNode;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TestModel1PackageImpl extends EPackageImpl implements TestModel1Package
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass treeNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass extendedNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass emptyNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass emptyRefNodeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass rootEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass authorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bookEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see testmodel1.TestModel1Package#eNS_URI
   * @see #init()
   * @generated
   */
  private TestModel1PackageImpl()
  {
    super(eNS_URI, TestModel1Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this
   * model, and for any others upon which it depends.  Simple
   * dependencies are satisfied by calling this method on all
   * dependent packages before doing anything else.  This method drives
   * initialization for interdependent packages directly, in parallel
   * with this package, itself.
   * <p>Of this package and its interdependencies, all packages which
   * have not yet been registered by their URI values are first created
   * and registered.  The packages are then initialized in two steps:
   * meta-model objects for all of the packages are created before any
   * are initialized, since one package's meta-model objects may refer to
   * those of another.
   * <p>Invocation of this method will not affect any packages that have
   * already been initialized.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static TestModel1Package init()
  {
    if (isInited)
      return (TestModel1Package) EPackage.Registry.INSTANCE.getEPackage(TestModel1Package.eNS_URI);

    // Obtain or create and register package
    TestModel1PackageImpl theTestModel1Package = (TestModel1PackageImpl) (EPackage.Registry.INSTANCE
        .getEPackage(eNS_URI) instanceof TestModel1PackageImpl ? EPackage.Registry.INSTANCE
        .getEPackage(eNS_URI) : new TestModel1PackageImpl());

    isInited = true;

    // Initialize simple dependencies
    CDOPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theTestModel1Package.createPackageContents();

    // Initialize created meta-data
    theTestModel1Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theTestModel1Package.freeze();

    return theTestModel1Package;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTreeNode()
  {
    return treeNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_Parent()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_Children()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_Parent2()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_Children2()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_References()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_Reference()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_SourceRef()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTreeNode_TargetRef()
  {
    return (EReference) treeNodeEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTreeNode_BooleanFeature()
  {
    return (EAttribute) treeNodeEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTreeNode_IntFeature()
  {
    return (EAttribute) treeNodeEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTreeNode_StringFeature()
  {
    return (EAttribute) treeNodeEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExtendedNode()
  {
    return extendedNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExtendedNode_BidiSource()
  {
    return (EReference) extendedNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExtendedNode_BidiTarget()
  {
    return (EReference) extendedNodeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getExtendedNode_StringFeature2()
  {
    return (EAttribute) extendedNodeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEmptyNode()
  {
    return emptyNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEmptyRefNode()
  {
    return emptyRefNodeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEmptyRefNode_MoreReferences()
  {
    return (EReference) emptyRefNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRoot()
  {
    return rootEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoot_Children()
  {
    return (EReference) rootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAuthor()
  {
    return authorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAuthor_Books()
  {
    return (EReference) authorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAuthor_Name()
  {
    return (EAttribute) authorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBook()
  {
    return bookEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBook_Author()
  {
    return (EReference) bookEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBook_Name()
  {
    return (EAttribute) bookEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TestModel1Factory getTestModel1Factory()
  {
    return (TestModel1Factory) getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    treeNodeEClass = createEClass(TREE_NODE);
    createEReference(treeNodeEClass, TREE_NODE__PARENT);
    createEReference(treeNodeEClass, TREE_NODE__CHILDREN);
    createEReference(treeNodeEClass, TREE_NODE__PARENT2);
    createEReference(treeNodeEClass, TREE_NODE__CHILDREN2);
    createEReference(treeNodeEClass, TREE_NODE__REFERENCES);
    createEReference(treeNodeEClass, TREE_NODE__REFERENCE);
    createEReference(treeNodeEClass, TREE_NODE__SOURCE_REF);
    createEReference(treeNodeEClass, TREE_NODE__TARGET_REF);
    createEAttribute(treeNodeEClass, TREE_NODE__BOOLEAN_FEATURE);
    createEAttribute(treeNodeEClass, TREE_NODE__INT_FEATURE);
    createEAttribute(treeNodeEClass, TREE_NODE__STRING_FEATURE);

    extendedNodeEClass = createEClass(EXTENDED_NODE);
    createEReference(extendedNodeEClass, EXTENDED_NODE__BIDI_SOURCE);
    createEReference(extendedNodeEClass, EXTENDED_NODE__BIDI_TARGET);
    createEAttribute(extendedNodeEClass, EXTENDED_NODE__STRING_FEATURE2);

    emptyNodeEClass = createEClass(EMPTY_NODE);

    emptyRefNodeEClass = createEClass(EMPTY_REF_NODE);
    createEReference(emptyRefNodeEClass, EMPTY_REF_NODE__MORE_REFERENCES);

    rootEClass = createEClass(ROOT);
    createEReference(rootEClass, ROOT__CHILDREN);

    authorEClass = createEClass(AUTHOR);
    createEReference(authorEClass, AUTHOR__BOOKS);
    createEAttribute(authorEClass, AUTHOR__NAME);

    bookEClass = createEClass(BOOK);
    createEReference(bookEClass, BOOK__AUTHOR);
    createEAttribute(bookEClass, BOOK__NAME);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    CDOPackage theCDOPackage = (CDOPackage) EPackage.Registry.INSTANCE
        .getEPackage(CDOPackage.eNS_URI);

    // Add supertypes to classes
    treeNodeEClass.getESuperTypes().add(theCDOPackage.getCDOPersistent());
    extendedNodeEClass.getESuperTypes().add(this.getTreeNode());
    emptyNodeEClass.getESuperTypes().add(this.getTreeNode());
    emptyRefNodeEClass.getESuperTypes().add(this.getTreeNode());
    rootEClass.getESuperTypes().add(theCDOPackage.getCDOPersistent());
    authorEClass.getESuperTypes().add(theCDOPackage.getCDOPersistent());
    bookEClass.getESuperTypes().add(theCDOPackage.getCDOPersistent());

    // Initialize classes and features; add operations and parameters
    initEClass(treeNodeEClass, TreeNode.class, "TreeNode", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTreeNode_Parent(), this.getTreeNode(), this.getTreeNode_Children(), "parent",
        null, 0, 1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTreeNode_Children(), this.getTreeNode(), this.getTreeNode_Parent(),
        "children", null, 0, -1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTreeNode_Parent2(), this.getTreeNode(), this.getTreeNode_Children2(),
        "parent2", null, 0, 1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTreeNode_Children2(), this.getTreeNode(), this.getTreeNode_Parent2(),
        "children2", null, 0, -1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTreeNode_References(), this.getTreeNode(), null, "references", null, 0, -1,
        TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTreeNode_Reference(), this.getTreeNode(), null, "reference", null, 0, 1,
        TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTreeNode_SourceRef(), this.getTreeNode(), this.getTreeNode_TargetRef(),
        "sourceRef", null, 0, 1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTreeNode_TargetRef(), this.getTreeNode(), this.getTreeNode_SourceRef(),
        "targetRef", null, 0, 1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTreeNode_BooleanFeature(), ecorePackage.getEBoolean(), "booleanFeature",
        null, 0, 1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTreeNode_IntFeature(), ecorePackage.getEInt(), "intFeature", null, 0, 1,
        TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTreeNode_StringFeature(), ecorePackage.getEString(), "stringFeature", null,
        0, 1, TreeNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID,
        IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(extendedNodeEClass, ExtendedNode.class, "ExtendedNode", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExtendedNode_BidiSource(), this.getExtendedNode(), this
        .getExtendedNode_BidiTarget(), "bidiSource", null, 0, -1, ExtendedNode.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExtendedNode_BidiTarget(), this.getExtendedNode(), this
        .getExtendedNode_BidiSource(), "bidiTarget", null, 0, -1, ExtendedNode.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
        !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getExtendedNode_StringFeature2(), ecorePackage.getEString(), "stringFeature2",
        null, 0, 1, ExtendedNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(emptyNodeEClass, EmptyNode.class, "EmptyNode", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);

    initEClass(emptyRefNodeEClass, EmptyRefNode.class, "EmptyRefNode", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEmptyRefNode_MoreReferences(), this.getTreeNode(), null, "moreReferences",
        null, 0, -1, EmptyRefNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(rootEClass, Root.class, "Root", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRoot_Children(), theCDOPackage.getCDOPersistent(), null, "children", null, 0,
        -1, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(authorEClass, Author.class, "Author", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAuthor_Books(), this.getBook(), this.getBook_Author(), "books", null, 0, -1,
        Author.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAuthor_Name(), ecorePackage.getEString(), "name", null, 0, 1, Author.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    initEClass(bookEClass, Book.class, "Book", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBook_Author(), this.getAuthor(), this.getAuthor_Books(), "author", null, 0,
        1, Book.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBook_Name(), ecorePackage.getEString(), "name", null, 0, 1, Book.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //TestModel1PackageImpl
