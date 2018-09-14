/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionFactory;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.FeaturePathMigration;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.ModelSetChange;
import org.eclipse.emf.cdo.evolution.PropertyChange;
import org.eclipse.emf.cdo.evolution.Release;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EvolutionFactoryImpl extends EFactoryImpl implements EvolutionFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static EvolutionFactory init()
  {
    try
    {
      EvolutionFactory theEvolutionFactory = (EvolutionFactory)EPackage.Registry.INSTANCE.getEFactory(EvolutionPackage.eNS_URI);
      if (theEvolutionFactory != null)
      {
        return theEvolutionFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new EvolutionFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EvolutionFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case EvolutionPackage.MODEL:
      return createModel();
    case EvolutionPackage.EVOLUTION:
      return createEvolution();
    case EvolutionPackage.RELEASE:
      return createRelease();
    case EvolutionPackage.MODEL_SET_CHANGE:
      return createModelSetChange();
    case EvolutionPackage.ELEMENT_CHANGE:
      return createElementChange();
    case EvolutionPackage.PROPERTY_CHANGE:
      return createPropertyChange();
    case EvolutionPackage.FEATURE_PATH_MIGRATION:
      return createFeaturePathMigration();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case EvolutionPackage.CHANGE_KIND:
      return createChangeKindFromString(eDataType, initialValue);
    case EvolutionPackage.URI:
      return createURIFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case EvolutionPackage.CHANGE_KIND:
      return convertChangeKindToString(eDataType, instanceValue);
    case EvolutionPackage.URI:
      return convertURIToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model createModel()
  {
    ModelImpl model = new ModelImpl();
    return model;
  }

  public Model createModel(URI uri)
  {
    ModelImpl model = new ModelImpl();
    model.setURI(uri);
    return model;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Evolution createEvolution()
  {
    EvolutionImpl evolution = new EvolutionImpl();
    return evolution;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Release createRelease()
  {
    ReleaseImpl release = new ReleaseImpl();
    return release;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelSetChange createModelSetChange()
  {
    ModelSetChangeImpl modelSetChange = new ModelSetChangeImpl();
    return modelSetChange;
  }

  public ModelSetChange createModelSetChange(ModelSet[] modelSetChain)
  {
    ModelSetChangeImpl modelSetChange = new ModelSetChangeImpl();
    modelSetChange.setModelSetChain(modelSetChain);
    return modelSetChange;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ElementChange createElementChange()
  {
    ElementChangeImpl elementChange = new ElementChangeImpl();
    return elementChange;
  }

  public ElementChange createElementChange(EModelElement oldElement, EModelElement newElement, ChangeKind kind)
  {
    ElementChangeImpl elementChange = new ElementChangeImpl();
    elementChange.setOldElement(oldElement);
    elementChange.setNewElement(newElement);
    elementChange.setKind(kind);
    return elementChange;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PropertyChange createPropertyChange()
  {
    PropertyChangeImpl propertyChange = new PropertyChangeImpl();
    return propertyChange;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FeaturePathMigration createFeaturePathMigration()
  {
    FeaturePathMigrationImpl featurePathMigration = new FeaturePathMigrationImpl();
    return featurePathMigration;
  }

  public PropertyChange createPropertyChange(EStructuralFeature feature, Object oldValue, Object newValue)
  {
    PropertyChangeImpl propertyChange = new PropertyChangeImpl();
    propertyChange.setFeature(feature);
    propertyChange.setOldValue(oldValue);
    propertyChange.setNewValue(newValue);
    return propertyChange;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ChangeKind createChangeKind(String literal)
  {
    ChangeKind result = ChangeKind.get(literal);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + EvolutionPackage.Literals.CHANGE_KIND.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ChangeKind createChangeKindFromString(EDataType eDataType, String initialValue)
  {
    return createChangeKind(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertChangeKind(ChangeKind instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertChangeKindToString(EDataType eDataType, Object instanceValue)
  {
    return convertChangeKind((ChangeKind)instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public URI createURI(String literal)
  {
    return literal == null ? null : URI.createURI(literal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public URI createURIFromString(EDataType eDataType, String initialValue)
  {
    return initialValue == null ? null : URI.createURI(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertURI(URI instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertURIToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EvolutionPackage getEvolutionPackage()
  {
    return (EvolutionPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static EvolutionPackage getPackage()
  {
    return EvolutionPackage.eINSTANCE;
  }

} // EvolutionFactoryImpl
