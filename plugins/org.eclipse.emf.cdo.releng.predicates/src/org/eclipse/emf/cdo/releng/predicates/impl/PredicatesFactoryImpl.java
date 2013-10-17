/**
 */
package org.eclipse.emf.cdo.releng.predicates.impl;

import org.eclipse.emf.cdo.releng.predicates.AndPredicate;
import org.eclipse.emf.cdo.releng.predicates.BuilderPredicate;
import org.eclipse.emf.cdo.releng.predicates.NamePredicate;
import org.eclipse.emf.cdo.releng.predicates.NaturePredicate;
import org.eclipse.emf.cdo.releng.predicates.NotPredicate;
import org.eclipse.emf.cdo.releng.predicates.OrPredicate;
import org.eclipse.emf.cdo.releng.predicates.PredicatesFactory;
import org.eclipse.emf.cdo.releng.predicates.PredicatesPackage;
import org.eclipse.emf.cdo.releng.predicates.RepositoryPredicate;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.cdo.releng.predicates.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PredicatesFactoryImpl extends EFactoryImpl implements PredicatesFactory
{
  private static final IWorkspaceRoot WORKSPACE_ROOT = ResourcesPlugin.getWorkspace().getRoot();

  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static PredicatesFactory init()
  {
    try
    {
      PredicatesFactory thePredicatesFactory = (PredicatesFactory)EPackage.Registry.INSTANCE.getEFactory(PredicatesPackage.eNS_URI);
      if (thePredicatesFactory != null)
      {
        return thePredicatesFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new PredicatesFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PredicatesFactoryImpl()
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
      case PredicatesPackage.NAME_PREDICATE: return createNamePredicate();
      case PredicatesPackage.REPOSITORY_PREDICATE: return createRepositoryPredicate();
      case PredicatesPackage.AND_PREDICATE: return createAndPredicate();
      case PredicatesPackage.OR_PREDICATE: return createOrPredicate();
      case PredicatesPackage.NOT_PREDICATE: return createNotPredicate();
      case PredicatesPackage.NATURE_PREDICATE: return createNaturePredicate();
      case PredicatesPackage.BUILDER_PREDICATE: return createBuilderPredicate();
      case PredicatesPackage.FILE_PREDICATE: return createFilePredicate();
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
      case PredicatesPackage.PROJECT:
        return createProjectFromString(eDataType, initialValue);
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
      case PredicatesPackage.PROJECT:
        return convertProjectToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NamePredicate createNamePredicate()
  {
    NamePredicateImpl namePredicate = new NamePredicateImpl();
    return namePredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RepositoryPredicate createRepositoryPredicate()
  {
    RepositoryPredicateImpl repositoryPredicate = new RepositoryPredicateImpl();
    return repositoryPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AndPredicate createAndPredicate()
  {
    AndPredicateImpl andPredicate = new AndPredicateImpl();
    return andPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrPredicate createOrPredicate()
  {
    OrPredicateImpl orPredicate = new OrPredicateImpl();
    return orPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotPredicate createNotPredicate()
  {
    NotPredicateImpl notPredicate = new NotPredicateImpl();
    return notPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NaturePredicate createNaturePredicate()
  {
    NaturePredicateImpl naturePredicate = new NaturePredicateImpl();
    return naturePredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BuilderPredicate createBuilderPredicate()
  {
    BuilderPredicateImpl builderPredicate = new BuilderPredicateImpl();
    return builderPredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FilePredicate createFilePredicate()
  {
    FilePredicateImpl filePredicate = new FilePredicateImpl();
    return filePredicate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public IProject createProjectFromString(EDataType eDataType, String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }
    return WORKSPACE_ROOT.getProject(initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public String convertProjectToString(EDataType eDataType, Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }
    IProject project = (IProject)instanceValue;
    return project.getName();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PredicatesPackage getPredicatesPackage()
  {
    return (PredicatesPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static PredicatesPackage getPackage()
  {
    return PredicatesPackage.eINSTANCE;
  }

} // PredicatesFactoryImpl
