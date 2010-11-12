/**
 * <copyright>
 * </copyright>
 *
 * $Id: EtypesPackageImpl.java,v 1.3 2010-11-12 17:37:25 estepper Exp $
 */
package org.eclipse.emf.cdo.etypes.impl;

import org.eclipse.emf.cdo.common.model.lob.CDOBlob;
import org.eclipse.emf.cdo.common.model.lob.CDOClob;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>.
 * 
 * @since 4.0 <!-- end-user-doc -->
 * @generated
 */
public class EtypesPackageImpl extends EPackageImpl implements EtypesPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType blobEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EDataType clobEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.etypes.EtypesPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private EtypesPackageImpl()
  {
    super(eNS_URI, EtypesFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * <p>
   * This method is used to initialize {@link EtypesPackage#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static EtypesPackage init()
  {
    if (isInited)
      return (EtypesPackage)EPackage.Registry.INSTANCE.getEPackage(EtypesPackage.eNS_URI);

    // Obtain or create and register package
    EtypesPackageImpl theEtypesPackage = (EtypesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof EtypesPackageImpl ? EPackage.Registry.INSTANCE
        .get(eNS_URI) : new EtypesPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theEtypesPackage.createPackageContents();

    // Initialize created meta-data
    theEtypesPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theEtypesPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(EtypesPackage.eNS_URI, theEtypesPackage);
    return theEtypesPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getBlob()
  {
    return blobEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EDataType getClob()
  {
    return clobEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EtypesFactory getEtypesFactory()
  {
    return (EtypesFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
      return;
    isCreated = true;

    // Create data types
    blobEDataType = createEDataType(BLOB);
    clobEDataType = createEDataType(CLOB);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Initialize data types
    initEDataType(blobEDataType, CDOBlob.class, "Blob", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
    initEDataType(clobEDataType, CDOClob.class, "Clob", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

    // Create resource
    createResource(eNS_URI);
  }

} // EtypesPackageImpl
