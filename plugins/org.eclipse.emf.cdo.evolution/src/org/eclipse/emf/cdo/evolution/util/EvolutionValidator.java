/**
 */
package org.eclipse.emf.cdo.evolution.util;

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.FeaturePathMigration;
import org.eclipse.emf.cdo.evolution.Migration;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.ModelSetChange;
import org.eclipse.emf.cdo.evolution.PropertyChange;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.evolution.impl.EvolutionPlugin;

import org.eclipse.net4j.util.collection.CollectionUtil;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.evolution.EvolutionPackage
 * @generated
 */
public class EvolutionValidator extends EObjectValidator
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final EvolutionValidator INSTANCE = new EvolutionValidator();

  /**
   * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.Diagnostic#getSource()
   * @see org.eclipse.emf.common.util.Diagnostic#getCode()
   * @generated
   */
  public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.cdo.evolution";

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

  public static final int CODE_NO_MODEL = GENERATED_DIAGNOSTIC_CODE_COUNT + 1;

  public static final int CODE_NO_URI = CODE_NO_MODEL + 1;

  public static final int CODE_NO_RESOURCE_SET = CODE_NO_URI + 1;

  public static final int CODE_RESOURCE_NOT_FOUND = CODE_NO_RESOURCE_SET + 1;

  public static final int CODE_LOAD_PROBLEM = CODE_RESOURCE_NOT_FOUND + 1;

  public static final int CODE_CONTENT_PROBLEM = CODE_LOAD_PROBLEM + 1;

  public static final int CODE_PACKAGE_MISSING = CODE_CONTENT_PROBLEM + 1;

  public static final int CODE_PACKAGE_NOT_UNIQUE = CODE_PACKAGE_MISSING + 1;

  public static final int CODE_NSURI_NOT_UNIQUE = CODE_PACKAGE_NOT_UNIQUE + 1;

  public static final int CODE_NSURI_NOT_CHANGED = CODE_NSURI_NOT_UNIQUE + 1;

  public static final int CODE_ID_ANNOTATION_MISSING = CODE_NSURI_NOT_CHANGED + 1;

  public static final int CODE_ID_WITHOUT_VALUE = CODE_ID_ANNOTATION_MISSING + 1;

  public static final int CODE_ID_NOT_UNIQUE = CODE_ID_WITHOUT_VALUE + 1;

  public static final int CODE_MIGRATION_UNUSED = CODE_ID_NOT_UNIQUE + 1;

  public static final int CODE_FEATURE_PATH_UNKNOWN = CODE_MIGRATION_UNUSED + 1;

  // Insert new codes above.

  public static final int CODE_UNCHANGED = CODE_FEATURE_PATH_UNKNOWN + 1;

  public static final int CODE_RELEASE = CODE_UNCHANGED + 1;

  /**
   * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  protected static final int DIAGNOSTIC_CODE_COUNT = CODE_RELEASE;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EvolutionValidator()
  {
    super();
  }

  @Override
  public boolean validate_EveryReferenceIsContained(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  @Override
  public boolean validate_EveryProxyResolves(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * Returns the package of this validator switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EPackage getEPackage()
  {
    return EvolutionPackage.eINSTANCE;
  }

  /**
   * Calls <code>validateXXX</code> for the corresponding classifier of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected boolean validateGen(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    switch (classifierID)
    {
    case EvolutionPackage.MODEL_SET:
      return validateModelSet((ModelSet)value, diagnostics, context);
    case EvolutionPackage.MODEL:
      return validateModel((Model)value, diagnostics, context);
    case EvolutionPackage.EVOLUTION:
      return validateEvolution((Evolution)value, diagnostics, context);
    case EvolutionPackage.RELEASE:
      return validateRelease((Release)value, diagnostics, context);
    case EvolutionPackage.CHANGE:
      return validateChange((Change)value, diagnostics, context);
    case EvolutionPackage.MODEL_SET_CHANGE:
      return validateModelSetChange((ModelSetChange)value, diagnostics, context);
    case EvolutionPackage.ELEMENT_CHANGE:
      return validateElementChange((ElementChange)value, diagnostics, context);
    case EvolutionPackage.PROPERTY_CHANGE:
      return validatePropertyChange((PropertyChange)value, diagnostics, context);
    case EvolutionPackage.MIGRATION:
      return validateMigration((Migration)value, diagnostics, context);
    case EvolutionPackage.FEATURE_PATH_MIGRATION:
      return validateFeaturePathMigration((FeaturePathMigration)value, diagnostics, context);
    case EvolutionPackage.CHANGE_KIND:
      return validateChangeKind((ChangeKind)value, diagnostics, context);
    case EvolutionPackage.URI:
      return validateURI((URI)value, diagnostics, context);
    default:
      return true;
    }
  }

  @Override
  protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (diagnostics == null)
    {
      return true;
    }

    if (context == null)
    {
      return true;
    }

    return validateGen(classifierID, value, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateModelSetGen(ModelSet modelSet, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(modelSet, diagnostics, context);
  }

  public boolean validateModelSet(ModelSet modelSet, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.RELEASE))
    {
      diagnostics.add(
          createDiagnostic(Diagnostic.INFO, DIAGNOSTIC_SOURCE, CODE_RELEASE, "_UI_Release_diagnostic", new Object[] {}, new Object[] { modelSet }, context));
      return true;
    }

    return validateModelSetGen(modelSet, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateModel(Model model, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(model, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(model, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateModel_ModelLoaded(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateModel_IDs_Exist(model, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateModel_NamespaceReflectsChange(model, diagnostics, context);
    }
    return result;
  }

  /**
   * Validates the ModelLoaded constraint of '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category MODEL_AVAILABILITY
   */
  public boolean validateModel_ModelLoaded(Model model, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.MODEL_AVAILABILITY))
    {
      switch (model.getStatus())
      {
      case NO_URI:
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_NO_URI, "_UI_NO_URI_diagnostic", new Object[] {},
            new Object[] { model, EvolutionPackage.Literals.MODEL__URI }, context));
        return false;

      case NO_RESOURCE_SET:
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_NO_RESOURCE_SET, "_UI_NO_RESOURCE_SET_diagnostic", new Object[] {},
            new Object[] { model }, context));
        return false;

      case RESOURCE_NOT_FOUND:
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_RESOURCE_NOT_FOUND, "_UI_RESOURCE_NOT_FOUND_diagnostic",
            new Object[] { model.getURI().toString() }, new Object[] { model, EvolutionPackage.Literals.MODEL__URI }, context));
        return false;

      case LOAD_PROBLEM:
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_RESOURCE_NOT_FOUND, "_UI_LOAD_PROBLEM_diagnostic",
            new Object[] { model.getURI().toString() }, new Object[] { model, EvolutionPackage.Literals.MODEL__URI }, context));
        return false;

      case CONTENT_PROBLEM:
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_CONTENT_PROBLEM, "_UI_CONTENT_PROBLEM_diagnostic",
            new Object[] { model.getURI().toString() }, new Object[] { model, EvolutionPackage.Literals.MODEL__URI }, context));
        return false;
      }
    }

    return true;
  }

  /**
   * Validates the IDs_Exist constraint of '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category IDENTITY_COMPLETENESS
   */
  public boolean validateModel_IDs_Exist(Model model, final DiagnosticChain diagnostics, final Map<Object, Object> context)
  {
    ValidationContext validationContext = ValidationContext.getFrom(context);
    if (validationContext != null && validationContext.getPhase() == ValidationPhase.IDENTITY_COMPLETENESS)
    {
      final Map<String, Object> identifiedElements = validationContext.getIdentifiedElements();
      EPackage rootPackage = model.getRootPackage();

      final boolean[] result = { true };
      ElementHandler.execute(rootPackage, new ElementRunnable()
      {
        public void run(EModelElement modelElement)
        {
          if (IDAnnotation.getFrom(modelElement, false) == null)
          {
            diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_ID_ANNOTATION_MISSING, "_UI_IDsExist_AnnotationMissing_diagnostic",
                new Object[] {}, new Object[] { modelElement, EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS }, context));
            result[0] = false;
          }
          else
          {
            String value = IDAnnotation.getValue(modelElement);
            if (value == null)
            {
              diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_ID_WITHOUT_VALUE, "_UI_IDsExist_NoValue_diagnostic", new Object[] {},
                  new Object[] { modelElement, EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS }, context));
              result[0] = false;
            }
            else
            {
              Object element = identifiedElements.get(value);
              if (element == null)
              {
                identifiedElements.put(value, modelElement);
              }
              else if (element instanceof List<?>)
              {
                @SuppressWarnings("unchecked")
                List<EModelElement> elements = (List<EModelElement>)element;
                elements.add(modelElement);
              }
              else
              {
                List<EModelElement> elements = new ArrayList<EModelElement>();
                elements.add((EModelElement)element);
                elements.add(modelElement);
                identifiedElements.put(value, elements);
              }
            }
          }
        }
      });

      return result[0];
    }
    return true;
  }

  /**
   * Validates the NamespaceReflectsChange constraint of '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category MODEL_UNIQUENESS
   */
  public boolean validateModel_NamespaceReflectsChange(Model model, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.MODEL_UNIQUENESS))
    {
      Evolution evolution = model.getEvolution();
      if (evolution.isUniqueNamespaces())
      {
        Map<String, Set<EPackage>> releasedPackages = evolution.getReleasedPackages();
        boolean result = true;

        for (EPackage ePackage : model.getAllPackages())
        {
          Set<EPackage> set = releasedPackages.get(ePackage.getNsURI());
          if (set != null)
          {
            for (EPackage releasedPackage : set)
            {
              EqualityHelper equalityHelper = new EqualityHelper()
              {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean haveEqualFeature(EObject eObject1, EObject eObject2, EStructuralFeature feature)
                {
                  if (feature == EcorePackage.Literals.EPACKAGE__ESUBPACKAGES)
                  {
                    // Compare packages without their subpackages.
                    return true;
                  }

                  return super.haveEqualFeature(eObject1, eObject2, feature);
                }

                @Override
                protected boolean haveEqualReference(EObject eObject, EObject releasedObject, EReference reference)
                {
                  boolean result = super.haveEqualReference(eObject, releasedObject, reference);
                  if (!result && reference == EcorePackage.Literals.EANNOTATION__DETAILS)
                  {
                    // As part of the release process (quick fix) the old values of ID annotations are deleted.
                    // Adjust the comparison to ignore this fact.
                    EMap<String, String> releasedDetails = ((EAnnotation)releasedObject).getDetails();
                    if (releasedDetails.containsKey(IDAnnotation.OLD_VALUE_KEY))
                    {
                      EList<Map.Entry<String, String>> replacementList = new BasicEList<Map.Entry<String, String>>(releasedDetails);
                      for (Iterator<Map.Entry<String, String>> it = replacementList.iterator(); it.hasNext();)
                      {
                        Map.Entry<String, String> entry = it.next();
                        if (IDAnnotation.OLD_VALUE_KEY.equals(entry.getKey()))
                        {
                          it.remove();

                          @SuppressWarnings("unchecked")
                          List<EObject> list1 = (List<EObject>)(Object)((EAnnotation)eObject).getDetails();

                          @SuppressWarnings("unchecked")
                          List<EObject> list2 = (List<EObject>)(Object)replacementList;

                          result = equals(list1, list2);
                          break;
                        }
                      }
                    }
                  }

                  return result;
                }
              };

              if (!equalityHelper.equals(ePackage, releasedPackage))
              {
                Release release = (Release)ElementHandler.getModelSet(releasedPackage);

                diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_NSURI_NOT_CHANGED, "_UI_NsUriMustChange_diagnostic",
                    new Object[] { ePackage.getNsURI(), release.getVersion() },
                    new Object[] { ePackage, EcorePackage.Literals.EPACKAGE__NS_URI, releasedPackage, evolution }, context));
                result = false;
                break;
              }
            }
          }
        }

        return result;
      }
    }

    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateEvolutionGen(Evolution evolution, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(evolution, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(evolution, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateEvolution_NotEmpty(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateEvolution_PackagesUnique(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateEvolution_NoMissingPackages(evolution, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateEvolution_IDsUnique(evolution, diagnostics, context);
    }
    return result;
  }

  public boolean validateEvolution(Evolution evolution, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.RELEASE))
    {
      ModelSetChange change = evolution.getChange();

      if (change == null || change.getChildren().isEmpty())
      {
        Release latestRelease = evolution.getLatestRelease();
        if (latestRelease == null)
        {
          diagnostics.add(createDiagnostic(Diagnostic.INFO, DIAGNOSTIC_SOURCE, CODE_UNCHANGED, "_UI_Unchanged_diagnostic", new Object[] {},
              new Object[] { evolution }, context));
        }
        else
        {
          diagnostics.add(createDiagnostic(Diagnostic.INFO, DIAGNOSTIC_SOURCE, CODE_UNCHANGED, "_UI_UnchangedSince_diagnostic",
              new Object[] { latestRelease.getVersion() }, new Object[] { evolution }, context));
        }
      }
      else
      {
        diagnostics.add(
            createDiagnostic(Diagnostic.INFO, DIAGNOSTIC_SOURCE, CODE_RELEASE, "_UI_Release_diagnostic", new Object[] {}, new Object[] { evolution }, context));
      }
      return true;
    }

    return validateEvolutionGen(evolution, diagnostics, context);
  }

  /**
   * Validates the NotEmpty constraint of '<em>Evolution</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category MODEL_AVAILABILITY
   */
  public boolean validateEvolution_NotEmpty(Evolution evolution, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.MODEL_AVAILABILITY))
    {
      if (evolution.getModels().isEmpty())
      {
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_NO_MODEL, "_UI_NoModel_diagnostic", new Object[] {},
            new Object[] { evolution }, context));
        return false;
      }
    }

    return true;
  }

  /**
   * Validates the PackagesUnique constraint of '<em>Evolution</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category MODEL_UNIQUENESS
   */
  public boolean validateEvolution_PackagesUnique(Evolution evolution, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.MODEL_UNIQUENESS))
    {
      boolean result = true;

      Map<URI, Set<Model>> models = new HashMap<URI, Set<Model>>();
      for (Model model : evolution.getModels())
      {
        CollectionUtil.add(models, model.getURI(), model);
      }

      for (Set<Model> set : models.values())
      {
        if (set.size() > 1)
        {
          for (Model model : set)
          {
            diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_PACKAGE_NOT_UNIQUE, "_UI_PackageNotUnique_diagnostic",
                new Object[] { model.getURI().toString() }, new Object[] { model, EvolutionPackage.Literals.MODEL__URI, model.getURI() }, context));
            result = false;
          }
        }
      }

      Map<String, Set<EPackage>> packages = new HashMap<String, Set<EPackage>>();
      for (EPackage ePackage : evolution.getAllPackages())
      {
        CollectionUtil.add(packages, ePackage.getNsURI(), ePackage);
      }

      for (Set<EPackage> set : packages.values())
      {
        if (set.size() > 1)
        {
          for (EPackage ePackage : set)
          {
            diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_NSURI_NOT_UNIQUE, "_UI_NsuriNotUnique_diagnostic",
                new Object[] { ePackage.getNsURI() }, new Object[] { ePackage, EcorePackage.Literals.EPACKAGE__NS_URI, ePackage.getNsURI() }, context));
            result = false;
          }
        }
      }

      return result;
    }

    return true;
  }

  /**
   * Validates the NoMissingPackages constraint of '<em>Evolution</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category MODEL_INTEGRITY
   */
  public boolean validateEvolution_NoMissingPackages(Evolution evolution, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.MODEL_INTEGRITY))
    {
      EList<EPackage> missingPackages = evolution.getMissingPackages();
      if (missingPackages.size() != 0)
      {
        for (EPackage missingPackage : missingPackages)
        {
          diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_PACKAGE_MISSING, "_UI_MissingPackage_diagnostic",
              new Object[] { missingPackage.getName() }, new Object[] { evolution, EvolutionPackage.Literals.EVOLUTION__MISSING_PACKAGES, missingPackage },
              context));
        }

        return false;
      }
    }

    return true;
  }

  /**
   * Validates the IDsUnique constraint of '<em>Evolution</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category IDENTITY_UNIQUENESS
   */
  public boolean validateEvolution_IDsUnique(Evolution evolution, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    // TODO Validate oldValue.
    // TODO Validate oldElement.eClass() == newElement.eClass().
    int xxx;

    ValidationContext validationContext = ValidationContext.getFrom(context);
    if (validationContext != null && validationContext.getPhase() == ValidationPhase.IDENTITY_UNIQUENESS)
    {
      boolean result = true;
      for (Map.Entry<String, Object> entry : validationContext.getIdentifiedElements().entrySet())
      {
        Object element = entry.getValue();
        if (element instanceof List<?>)
        {
          String id = entry.getKey();

          @SuppressWarnings("unchecked")
          List<EModelElement> elements = (List<EModelElement>)element;
          for (EModelElement modelElement : elements)
          {
            List<Object> data = new ArrayList<Object>();
            data.add(modelElement);
            data.add(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS);
            data.add(id);

            for (EModelElement conflictingElement : elements)
            {
              if (conflictingElement != modelElement)
              {
                data.add(conflictingElement);
              }
            }

            diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_ID_NOT_UNIQUE, "_UI_IDNotUnique_diagnostic", new Object[] { id },
                data.toArray(new Object[data.size()]), context));
            result = false;
          }
        }
      }

      return result;
    }

    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateRelease(Release release, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(release, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateChange(Change change, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(change, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateModelSetChange(ModelSetChange modelSetChange, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(modelSetChange, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateElementChange(ElementChange elementChange, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(elementChange, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(elementChange, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(elementChange, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(elementChange, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(elementChange, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(elementChange, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(elementChange, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(elementChange, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(elementChange, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateElementChange_FeaturePathIsKnown(elementChange, diagnostics, context);
    }
    return result;
  }

  /**
   * Validates the FeaturePathIsKnown constraint of '<em>Element Change</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category CHANGE_VALIDITY
   */
  public boolean validateElementChange_FeaturePathIsKnown(ElementChange elementChange, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    ValidationContext validationContext = ValidationContext.getFrom(context);
    if (validationContext != null && validationContext.getPhase() == ValidationPhase.CHANGE_VALIDITY)
    {
      ChangeKind kind = elementChange.getKind();
      if (kind == ChangeKind.COPIED || kind == ChangeKind.MOVED)
      {
        EModelElement newElement = elementChange.getNewElement();
        if (newElement instanceof EStructuralFeature)
        {
          EStructuralFeature newFeature = (EStructuralFeature)newElement;
          EStructuralFeature oldFeature = (EStructuralFeature)elementChange.getOldElement();

          EClass newContainingClass = newFeature.getEContainingClass();
          EClass oldContainingClass = oldFeature.getEContainingClass();
          if (elementChange.getOldElementFor(newContainingClass) != oldContainingClass)
          {
            BasicDiagnostic diagnostic = createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_FEATURE_PATH_UNKNOWN, "_UI_FeaturePathIsKnown_diagnostic",
                new Object[] { ElementHandler.getLabel(newFeature), kind.getName().toLowerCase(), ElementHandler.getLabel(oldFeature) },
                new Object[] { elementChange, newFeature }, context);

            Evolution evolution = validationContext.getEvolution();
            DiagnosticID diagnosticID = DiagnosticID.get(diagnostic);

            Migration migration = evolution.getMigration(diagnosticID.getValue());
            if (migration != null)
            {
              validationContext.getUsedMigrations().put(diagnosticID, migration);
              return true;
            }

            diagnostics.add(diagnostic);
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validatePropertyChange(PropertyChange propertyChange, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return validate_EveryDefaultConstraint(propertyChange, diagnostics, context);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateMigration(Migration migration, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(migration, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(migration, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(migration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(migration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(migration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(migration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(migration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(migration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(migration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateMigration_NotObsolete(migration, diagnostics, context);
    }
    return result;
  }

  /**
   * Validates the NotObsolete constraint of '<em>Migration</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category MIGRATION_OBSOLETENESS
   */
  public boolean validateMigration_NotObsolete(Migration migration, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    ValidationContext validationContext = ValidationContext.getFrom(context);
    if (validationContext != null && validationContext.getPhase() == ValidationPhase.MIGRATION_OBSOLETENESS)
    {
      String idValue = migration.getDiagnosticID();
      if (idValue != null && idValue.length() != 0)
      {
        DiagnosticID diagnosticID = new DiagnosticID(idValue);
        if (!validationContext.getUsedMigrations().containsKey(diagnosticID))
        {
          diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, CODE_MIGRATION_UNUSED, "_UI_MigrationUnused_diagnostic",
              new Object[] { migration }, new Object[] { migration, diagnosticID }, context));
          return false;
        }
      }
    }

    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateFeaturePathMigration(FeaturePathMigration featurePathMigration, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    if (!validate_NoCircularContainment(featurePathMigration, diagnostics, context))
    {
      return false;
    }
    boolean result = validate_EveryMultiplicityConforms(featurePathMigration, diagnostics, context);
    if (result || diagnostics != null)
    {
      result &= validate_EveryDataValueConforms(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryReferenceIsContained(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryBidirectionalReferenceIsPaired(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryProxyResolves(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_UniqueID(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryKeyUnique(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validate_EveryMapEntryUnique(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateMigration_NotObsolete(featurePathMigration, diagnostics, context);
    }
    if (result || diagnostics != null)
    {
      result &= validateFeaturePathMigration_NewFeatureReachable(featurePathMigration, diagnostics, context);
    }
    return result;
  }

  /**
   * Validates the NewFeatureReachable constraint of '<em>Feature Path Migration</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   * @category MIGRATION_VALIDITY
   */
  public boolean validateFeaturePathMigration_NewFeatureReachable(FeaturePathMigration featurePathMigration, DiagnosticChain diagnostics,
      Map<Object, Object> context)
  {
    if (ValidationContext.isPhase(context, ValidationPhase.MIGRATION_VALIDITY))
    {
    }
    // TODO implement the constraint
    // -> specify the condition that violates the constraint
    // -> verify the diagnostic details, including severity, code, and message
    // Ensure that you remove @generated or mark it @generated NOT
    if (false)
    {
      if (diagnostics != null)
      {
        diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0, "_UI_GenericConstraint_diagnostic",
            new Object[] { "NewFeatureReachable", getObjectLabel(featurePathMigration, context) }, new Object[] { featurePathMigration }, context));
      }
      return false;
    }
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateChangeKind(ChangeKind changeKind, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean validateURI(URI uri, DiagnosticChain diagnostics, Map<Object, Object> context)
  {
    return true;
  }

  /**
   * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator()
  {
    return EvolutionPlugin.INSTANCE;
  }

} // EvolutionValidator
