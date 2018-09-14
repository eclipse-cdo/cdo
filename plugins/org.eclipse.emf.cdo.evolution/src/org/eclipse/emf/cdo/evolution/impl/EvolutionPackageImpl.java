/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionFactory;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.FeaturePathMigration;
import org.eclipse.emf.cdo.evolution.Migration;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.ModelSetChange;
import org.eclipse.emf.cdo.evolution.PropertyChange;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.evolution.util.EvolutionValidator;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EvolutionPackageImpl extends EPackageImpl implements EvolutionPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass modelSetEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass modelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass evolutionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass releaseEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass changeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass modelSetChangeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass elementChangeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertyChangeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass migrationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass featurePathMigrationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum changeKindEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType uriEDataType = null;

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
   * @see org.eclipse.emf.cdo.evolution.EvolutionPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EvolutionPackageImpl()
  {
    super(eNS_URI, EvolutionFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link EvolutionPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EvolutionPackage init()
  {
    if (isInited)
    {
      return (EvolutionPackage)EPackage.Registry.INSTANCE.getEPackage(EvolutionPackage.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredEvolutionPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    EvolutionPackageImpl theEvolutionPackage = registeredEvolutionPackage instanceof EvolutionPackageImpl ? (EvolutionPackageImpl)registeredEvolutionPackage
        : new EvolutionPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theEvolutionPackage.createPackageContents();

    // Initialize created meta-data
    theEvolutionPackage.initializePackageContents();

    // Register package validator
    EValidator.Registry.INSTANCE.put(theEvolutionPackage, new EValidator.Descriptor()
    {
      public EValidator getEValidator()
      {
        return EvolutionValidator.INSTANCE;
      }
    });

    // Mark meta-data to indicate it can't be changed
    theEvolutionPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(EvolutionPackage.eNS_URI, theEvolutionPackage);
    return theEvolutionPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModelSet()
  {
    return modelSetEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModelSet_Change()
  {
    return (EReference)modelSetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModelSet_Migrations()
  {
    return (EReference)modelSetEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetEvolution()
  {
    return modelSetEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetVersion()
  {
    return modelSetEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetPreviousRelease()
  {
    return modelSetEClass.getEOperations().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetRootPackages()
  {
    return modelSetEClass.getEOperations().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetAllPackages()
  {
    return modelSetEClass.getEOperations().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__ContainsElement__EModelElement()
  {
    return modelSetEClass.getEOperations().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetElement__String()
  {
    return modelSetEClass.getEOperations().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetElementID__EModelElement()
  {
    return modelSetEClass.getEOperations().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetElementID__EModelElement_boolean()
  {
    return modelSetEClass.getEOperations().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__Compare__ModelSet()
  {
    return modelSetEClass.getEOperations().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getModelSet__GetMigration__String()
  {
    return modelSetEClass.getEOperations().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModel()
  {
    return modelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModel_Evolution()
  {
    return (EReference)modelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModel_URI()
  {
    return (EAttribute)modelEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModel_RootPackage()
  {
    return (EReference)modelEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModel_AllPackages()
  {
    return (EReference)modelEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModel_ReferencedPackages()
  {
    return (EReference)modelEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModel_MissingPackages()
  {
    return (EReference)modelEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEvolution()
  {
    return evolutionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEvolution_Models()
  {
    return (EReference)evolutionEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEvolution_UseEcorePackage()
  {
    return (EAttribute)evolutionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEvolution_UseEresourcePackage()
  {
    return (EAttribute)evolutionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEvolution_UseEtypesPackage()
  {
    return (EAttribute)evolutionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEvolution_UniqueNamespaces()
  {
    return (EAttribute)evolutionEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEvolution_RootPackages()
  {
    return (EReference)evolutionEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEvolution_AllPackages()
  {
    return (EReference)evolutionEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEvolution_Releases()
  {
    return (EReference)evolutionEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEvolution_OrderedReleases()
  {
    return (EReference)evolutionEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEvolution_LatestRelease()
  {
    return (EReference)evolutionEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEvolution_NextReleaseVersion()
  {
    return (EAttribute)evolutionEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEvolution_MissingPackages()
  {
    return (EReference)evolutionEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getEvolution__GetRelease__int()
  {
    return evolutionEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRelease()
  {
    return releaseEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelease_Evolution()
  {
    return (EReference)releaseEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelease_Date()
  {
    return (EAttribute)releaseEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelease_NextRelease()
  {
    return (EReference)releaseEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelease_PreviousRelease()
  {
    return (EReference)releaseEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRelease_Version()
  {
    return (EAttribute)releaseEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelease_RootPackages()
  {
    return (EReference)releaseEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRelease_AllPackages()
  {
    return (EReference)releaseEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getChange()
  {
    return changeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChange_Parent()
  {
    return (EReference)changeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChange_Children()
  {
    return (EReference)changeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getChange__GetOldModelSet()
  {
    return changeEClass.getEOperations().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getChange__GetNewModelSet()
  {
    return changeEClass.getEOperations().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getChange__GetModelSetChange()
  {
    return changeEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getChange__GetOldElementFor__EModelElement()
  {
    return changeEClass.getEOperations().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getChange__GetNewElementsFor__EModelElement()
  {
    return changeEClass.getEOperations().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModelSetChange()
  {
    return modelSetChangeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModelSetChange_OldModelSet()
  {
    return (EReference)modelSetChangeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModelSetChange_NewModelSet()
  {
    return (EReference)modelSetChangeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getElementChange()
  {
    return elementChangeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getElementChange_OldElement()
  {
    return (EReference)elementChangeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getElementChange_NewElement()
  {
    return (EReference)elementChangeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getElementChange_Kind()
  {
    return (EAttribute)elementChangeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EOperation getElementChange__GetElement()
  {
    return elementChangeEClass.getEOperations().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPropertyChange()
  {
    return propertyChangeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPropertyChange_Feature()
  {
    return (EReference)propertyChangeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPropertyChange_OldValue()
  {
    return (EAttribute)propertyChangeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPropertyChange_NewValue()
  {
    return (EAttribute)propertyChangeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPropertyChange_Kind()
  {
    return (EAttribute)propertyChangeEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMigration()
  {
    return migrationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMigration_ModelSet()
  {
    return (EReference)migrationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMigration_DiagnosticID()
  {
    return (EAttribute)migrationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFeaturePathMigration()
  {
    return featurePathMigrationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFeaturePathMigration_FromClass()
  {
    return (EReference)featurePathMigrationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFeaturePathMigration_ToClass()
  {
    return (EReference)featurePathMigrationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFeaturePathMigration_FeaturePath()
  {
    return (EReference)featurePathMigrationEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getChangeKind()
  {
    return changeKindEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EDataType getURI()
  {
    return uriEDataType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EvolutionFactory getEvolutionFactory()
  {
    return (EvolutionFactory)getEFactoryInstance();
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
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    modelSetEClass = createEClass(MODEL_SET);
    createEReference(modelSetEClass, MODEL_SET__CHANGE);
    createEReference(modelSetEClass, MODEL_SET__MIGRATIONS);
    createEOperation(modelSetEClass, MODEL_SET___GET_EVOLUTION);
    createEOperation(modelSetEClass, MODEL_SET___GET_VERSION);
    createEOperation(modelSetEClass, MODEL_SET___GET_PREVIOUS_RELEASE);
    createEOperation(modelSetEClass, MODEL_SET___GET_ROOT_PACKAGES);
    createEOperation(modelSetEClass, MODEL_SET___GET_ALL_PACKAGES);
    createEOperation(modelSetEClass, MODEL_SET___CONTAINS_ELEMENT__EMODELELEMENT);
    createEOperation(modelSetEClass, MODEL_SET___GET_ELEMENT__STRING);
    createEOperation(modelSetEClass, MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT);
    createEOperation(modelSetEClass, MODEL_SET___GET_ELEMENT_ID__EMODELELEMENT_BOOLEAN);
    createEOperation(modelSetEClass, MODEL_SET___COMPARE__MODELSET);
    createEOperation(modelSetEClass, MODEL_SET___GET_MIGRATION__STRING);

    modelEClass = createEClass(MODEL);
    createEReference(modelEClass, MODEL__EVOLUTION);
    createEAttribute(modelEClass, MODEL__URI);
    createEReference(modelEClass, MODEL__ROOT_PACKAGE);
    createEReference(modelEClass, MODEL__ALL_PACKAGES);
    createEReference(modelEClass, MODEL__REFERENCED_PACKAGES);
    createEReference(modelEClass, MODEL__MISSING_PACKAGES);

    evolutionEClass = createEClass(EVOLUTION);
    createEAttribute(evolutionEClass, EVOLUTION__USE_ECORE_PACKAGE);
    createEAttribute(evolutionEClass, EVOLUTION__USE_ERESOURCE_PACKAGE);
    createEAttribute(evolutionEClass, EVOLUTION__USE_ETYPES_PACKAGE);
    createEAttribute(evolutionEClass, EVOLUTION__UNIQUE_NAMESPACES);
    createEReference(evolutionEClass, EVOLUTION__MODELS);
    createEReference(evolutionEClass, EVOLUTION__ROOT_PACKAGES);
    createEReference(evolutionEClass, EVOLUTION__ALL_PACKAGES);
    createEReference(evolutionEClass, EVOLUTION__MISSING_PACKAGES);
    createEReference(evolutionEClass, EVOLUTION__RELEASES);
    createEReference(evolutionEClass, EVOLUTION__ORDERED_RELEASES);
    createEReference(evolutionEClass, EVOLUTION__LATEST_RELEASE);
    createEAttribute(evolutionEClass, EVOLUTION__NEXT_RELEASE_VERSION);
    createEOperation(evolutionEClass, EVOLUTION___GET_RELEASE__INT);

    releaseEClass = createEClass(RELEASE);
    createEReference(releaseEClass, RELEASE__EVOLUTION);
    createEAttribute(releaseEClass, RELEASE__VERSION);
    createEAttribute(releaseEClass, RELEASE__DATE);
    createEReference(releaseEClass, RELEASE__NEXT_RELEASE);
    createEReference(releaseEClass, RELEASE__PREVIOUS_RELEASE);
    createEReference(releaseEClass, RELEASE__ROOT_PACKAGES);
    createEReference(releaseEClass, RELEASE__ALL_PACKAGES);

    changeEClass = createEClass(CHANGE);
    createEReference(changeEClass, CHANGE__PARENT);
    createEReference(changeEClass, CHANGE__CHILDREN);
    createEOperation(changeEClass, CHANGE___GET_MODEL_SET_CHANGE);
    createEOperation(changeEClass, CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT);
    createEOperation(changeEClass, CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT);
    createEOperation(changeEClass, CHANGE___GET_OLD_MODEL_SET);
    createEOperation(changeEClass, CHANGE___GET_NEW_MODEL_SET);

    modelSetChangeEClass = createEClass(MODEL_SET_CHANGE);
    createEReference(modelSetChangeEClass, MODEL_SET_CHANGE__OLD_MODEL_SET);
    createEReference(modelSetChangeEClass, MODEL_SET_CHANGE__NEW_MODEL_SET);

    elementChangeEClass = createEClass(ELEMENT_CHANGE);
    createEReference(elementChangeEClass, ELEMENT_CHANGE__OLD_ELEMENT);
    createEReference(elementChangeEClass, ELEMENT_CHANGE__NEW_ELEMENT);
    createEAttribute(elementChangeEClass, ELEMENT_CHANGE__KIND);
    createEOperation(elementChangeEClass, ELEMENT_CHANGE___GET_ELEMENT);

    propertyChangeEClass = createEClass(PROPERTY_CHANGE);
    createEReference(propertyChangeEClass, PROPERTY_CHANGE__FEATURE);
    createEAttribute(propertyChangeEClass, PROPERTY_CHANGE__OLD_VALUE);
    createEAttribute(propertyChangeEClass, PROPERTY_CHANGE__NEW_VALUE);
    createEAttribute(propertyChangeEClass, PROPERTY_CHANGE__KIND);

    migrationEClass = createEClass(MIGRATION);
    createEReference(migrationEClass, MIGRATION__MODEL_SET);
    createEAttribute(migrationEClass, MIGRATION__DIAGNOSTIC_ID);

    featurePathMigrationEClass = createEClass(FEATURE_PATH_MIGRATION);
    createEReference(featurePathMigrationEClass, FEATURE_PATH_MIGRATION__FROM_CLASS);
    createEReference(featurePathMigrationEClass, FEATURE_PATH_MIGRATION__TO_CLASS);
    createEReference(featurePathMigrationEClass, FEATURE_PATH_MIGRATION__FEATURE_PATH);

    // Create enums
    changeKindEEnum = createEEnum(CHANGE_KIND);

    // Create data types
    uriEDataType = createEDataType(URI);
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
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    evolutionEClass.getESuperTypes().add(getModelSet());
    releaseEClass.getESuperTypes().add(getModelSet());
    modelSetChangeEClass.getESuperTypes().add(getChange());
    elementChangeEClass.getESuperTypes().add(getChange());
    propertyChangeEClass.getESuperTypes().add(getChange());
    featurePathMigrationEClass.getESuperTypes().add(getMigration());

    // Initialize classes, features, and operations; add parameters
    initEClass(modelSetEClass, ModelSet.class, "ModelSet", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModelSet_Change(), getModelSetChange(), null, "change", null, 0, 1, ModelSet.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getModelSet_Migrations(), getMigration(), getMigration_ModelSet(), "migrations", null, 0, -1, ModelSet.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEOperation(getModelSet__GetEvolution(), getEvolution(), "getEvolution", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getModelSet__GetVersion(), ecorePackage.getEInt(), "getVersion", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getModelSet__GetPreviousRelease(), getRelease(), "getPreviousRelease", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getModelSet__GetRootPackages(), ecorePackage.getEPackage(), "getRootPackages", 0, -1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getModelSet__GetAllPackages(), ecorePackage.getEPackage(), "getAllPackages", 0, -1, IS_UNIQUE, IS_ORDERED);

    EOperation op = initEOperation(getModelSet__ContainsElement__EModelElement(), theEcorePackage.getEBoolean(), "containsElement", 0, 1, IS_UNIQUE,
        IS_ORDERED);
    addEParameter(op, theEcorePackage.getEModelElement(), "modelElement", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getModelSet__GetElement__String(), null, "getElement", 0, 1, IS_UNIQUE, IS_ORDERED);
    ETypeParameter t1 = addETypeParameter(op, "T");
    EGenericType g1 = createEGenericType(theEcorePackage.getEModelElement());
    t1.getEBounds().add(g1);
    addEParameter(op, theEcorePackage.getEString(), "id", 0, 1, IS_UNIQUE, IS_ORDERED);
    g1 = createEGenericType(t1);
    initEOperation(op, g1);

    op = initEOperation(getModelSet__GetElementID__EModelElement(), theEcorePackage.getEString(), "getElementID", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, theEcorePackage.getEModelElement(), "modelElement", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getModelSet__GetElementID__EModelElement_boolean(), theEcorePackage.getEString(), "getElementID", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, theEcorePackage.getEModelElement(), "modelElement", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEBoolean(), "considerOldIDs", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getModelSet__Compare__ModelSet(), getModelSetChange(), "compare", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, getModelSet(), "other", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getModelSet__GetMigration__String(), getMigration(), "getMigration", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, ecorePackage.getEString(), "diagnosticID", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(modelEClass, Model.class, "Model", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModel_Evolution(), getEvolution(), getEvolution_Models(), "evolution", null, 1, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getModel_URI(), getURI(), "uRI", null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
        !IS_DERIVED, IS_ORDERED);
    initEReference(getModel_RootPackage(), theEcorePackage.getEPackage(), null, "rootPackage", null, 0, 1, Model.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getModel_AllPackages(), theEcorePackage.getEPackage(), null, "allPackages", null, 0, -1, Model.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getModel_ReferencedPackages(), theEcorePackage.getEPackage(), null, "referencedPackages", null, 0, -1, Model.class, IS_TRANSIENT,
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getModel_MissingPackages(), theEcorePackage.getEPackage(), null, "missingPackages", null, 0, -1, Model.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(evolutionEClass, Evolution.class, "Evolution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEvolution_UseEcorePackage(), ecorePackage.getEBoolean(), "useEcorePackage", "true", 0, 1, Evolution.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEvolution_UseEresourcePackage(), ecorePackage.getEBoolean(), "useEresourcePackage", null, 0, 1, Evolution.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEvolution_UseEtypesPackage(), ecorePackage.getEBoolean(), "useEtypesPackage", null, 0, 1, Evolution.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEvolution_UniqueNamespaces(), ecorePackage.getEBoolean(), "uniqueNamespaces", "true", 0, 1, Evolution.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEvolution_Models(), getModel(), getModel_Evolution(), "models", null, 0, -1, Evolution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEvolution_RootPackages(), theEcorePackage.getEPackage(), null, "rootPackages", null, 0, -1, Evolution.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getEvolution_AllPackages(), theEcorePackage.getEPackage(), null, "allPackages", null, 0, -1, Evolution.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getEvolution_MissingPackages(), theEcorePackage.getEPackage(), null, "missingPackages", null, 0, -1, Evolution.class, IS_TRANSIENT,
        IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getEvolution_Releases(), getRelease(), getRelease_Evolution(), "releases", null, 0, -1, Evolution.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEvolution_OrderedReleases(), getRelease(), null, "orderedReleases", null, 0, -1, Evolution.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getEvolution_LatestRelease(), getRelease(), null, "latestRelease", null, 0, 1, Evolution.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEAttribute(getEvolution_NextReleaseVersion(), theEcorePackage.getEInt(), "nextReleaseVersion", null, 0, 1, Evolution.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    op = initEOperation(getEvolution__GetRelease__int(), getRelease(), "getRelease", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, theEcorePackage.getEInt(), "version", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(releaseEClass, Release.class, "Release", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRelease_Evolution(), getEvolution(), getEvolution_Releases(), "evolution", null, 1, 1, Release.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRelease_Version(), ecorePackage.getEInt(), "version", null, 1, 1, Release.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getRelease_Date(), ecorePackage.getEDate(), "date", null, 1, 1, Release.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRelease_NextRelease(), getRelease(), null, "nextRelease", null, 0, 1, Release.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getRelease_PreviousRelease(), getRelease(), null, "previousRelease", null, 0, 1, Release.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getRelease_RootPackages(), theEcorePackage.getEPackage(), null, "rootPackages", null, 0, -1, Release.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRelease_AllPackages(), theEcorePackage.getEPackage(), null, "allPackages", null, 0, -1, Release.class, IS_TRANSIENT, IS_VOLATILE,
        !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(changeEClass, Change.class, "Change", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChange_Parent(), getChange(), getChange_Children(), "parent", null, 0, 1, Change.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getChange_Children(), getChange(), getChange_Parent(), "children", null, 0, -1, Change.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEOperation(getChange__GetModelSetChange(), getModelSetChange(), "getModelSetChange", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getChange__GetOldElementFor__EModelElement(), theEcorePackage.getEModelElement(), "getOldElementFor", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, theEcorePackage.getEModelElement(), "newElement", 0, 1, IS_UNIQUE, IS_ORDERED);

    op = initEOperation(getChange__GetNewElementsFor__EModelElement(), theEcorePackage.getEModelElement(), "getNewElementsFor", 0, -1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, theEcorePackage.getEModelElement(), "oldElement", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getChange__GetOldModelSet(), getModelSet(), "getOldModelSet", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEOperation(getChange__GetNewModelSet(), getModelSet(), "getNewModelSet", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(modelSetChangeEClass, ModelSetChange.class, "ModelSetChange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModelSetChange_OldModelSet(), getModelSet(), null, "oldModelSet", null, 0, 1, ModelSetChange.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModelSetChange_NewModelSet(), getModelSet(), null, "newModelSet", null, 0, 1, ModelSetChange.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(elementChangeEClass, ElementChange.class, "ElementChange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getElementChange_OldElement(), theEcorePackage.getEModelElement(), null, "oldElement", null, 0, 1, ElementChange.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getElementChange_NewElement(), theEcorePackage.getEModelElement(), null, "newElement", null, 0, 1, ElementChange.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getElementChange_Kind(), getChangeKind(), "kind", null, 0, 1, ElementChange.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEOperation(getElementChange__GetElement(), theEcorePackage.getEModelElement(), "getElement", 0, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(propertyChangeEClass, PropertyChange.class, "PropertyChange", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPropertyChange_Feature(), theEcorePackage.getEStructuralFeature(), null, "feature", null, 0, 1, PropertyChange.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPropertyChange_OldValue(), ecorePackage.getEJavaObject(), "oldValue", null, 0, 1, PropertyChange.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPropertyChange_NewValue(), ecorePackage.getEJavaObject(), "newValue", null, 0, 1, PropertyChange.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getPropertyChange_Kind(), getChangeKind(), "kind", null, 0, 1, PropertyChange.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

    initEClass(migrationEClass, Migration.class, "Migration", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMigration_ModelSet(), getModelSet(), getModelSet_Migrations(), "modelSet", null, 0, 1, Migration.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getMigration_DiagnosticID(), ecorePackage.getEString(), "diagnosticID", null, 0, 1, Migration.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(featurePathMigrationEClass, FeaturePathMigration.class, "FeaturePathMigration", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getFeaturePathMigration_FromClass(), ecorePackage.getEClass(), null, "fromClass", null, 1, 1, FeaturePathMigration.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFeaturePathMigration_ToClass(), ecorePackage.getEClass(), null, "toClass", null, 1, 1, FeaturePathMigration.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFeaturePathMigration_FeaturePath(), ecorePackage.getEReference(), null, "featurePath", null, 0, -1, FeaturePathMigration.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(changeKindEEnum, ChangeKind.class, "ChangeKind");
    addEEnumLiteral(changeKindEEnum, ChangeKind.NONE);
    addEEnumLiteral(changeKindEEnum, ChangeKind.CHANGED);
    addEEnumLiteral(changeKindEEnum, ChangeKind.REMOVED);
    addEEnumLiteral(changeKindEEnum, ChangeKind.ADDED);
    addEEnumLiteral(changeKindEEnum, ChangeKind.COPIED);
    addEEnumLiteral(changeKindEEnum, ChangeKind.MOVED);

    // Initialize data types
    initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "URI", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http://www.eclipse.org/emf/2002/Ecore
    createEcoreAnnotations();
    // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
    createExtendedMetaDataAnnotations();
  }

  /**
   * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/Ecore</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createEcoreAnnotations()
  {
    String source = "http://www.eclipse.org/emf/2002/Ecore";
    addAnnotation(modelEClass, source, new String[] { "constraints", "ModelLoaded IDs_Exist NamespaceReflectsChange" });
    addAnnotation(evolutionEClass, source, new String[] { "constraints", "NotEmpty PackagesUnique NoMissingPackages IDsUnique" });
    addAnnotation(elementChangeEClass, source, new String[] { "constraints", "FeaturePathIsKnown" });
    addAnnotation(migrationEClass, source, new String[] { "constraints", "NotObsolete" });
    addAnnotation(featurePathMigrationEClass, source, new String[] { "constraints", "NewFeatureReachable" });
  }

  /**
   * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createExtendedMetaDataAnnotations()
  {
    String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
    addAnnotation(getModel_URI(), source, new String[] { "kind", "attribute", "name", "uri" });
  }

} // EvolutionPackageImpl
