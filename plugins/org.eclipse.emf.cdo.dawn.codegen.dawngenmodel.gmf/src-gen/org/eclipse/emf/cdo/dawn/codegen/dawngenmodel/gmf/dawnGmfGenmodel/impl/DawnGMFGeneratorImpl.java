/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGMFGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.DawnGmfGenmodelPackage;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnFragmentGeneratorImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.gmf.codegen.gmfgen.GenEditorGenerator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Dawn GMF Generator</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnDocumentProviderClassName
 * <em>Dawn Document Provider Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnEditorUtilClassName
 * <em>Dawn Editor Util Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnCreationWizardClassName
 * <em>Dawn Creation Wizard Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnCanonicalEditingPolicyClassName
 * <em>Dawn Canonical Editing Policy Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnDiagramEditPartClassName
 * <em>Dawn Diagram Edit Part Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnEditPartFactoryClassName
 * <em>Dawn Edit Part Factory Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnEditPartProviderClassName
 * <em>Dawn Edit Part Provider Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getDawnEditPolicyProviderClassName
 * <em>Dawn Edit Policy Provider Class Name</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.dawnGmfGenmodel.impl.DawnGMFGeneratorImpl#getGMFGenEditorGenerator
 * <em>GMF Gen Editor Generator</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 * @author Martin Fluegge
 */
public class DawnGMFGeneratorImpl extends DawnFragmentGeneratorImpl implements DawnGMFGenerator
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * The default value of the '{@link #getDawnDocumentProviderClassName() <em>Dawn Document Provider Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnDocumentProviderClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_DOCUMENT_PROVIDER_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnDocumentProviderClassName() <em>Dawn Document Provider Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnDocumentProviderClassName()
   * @generated
   * @ordered
   */
  protected String dawnDocumentProviderClassName = DAWN_DOCUMENT_PROVIDER_CLASS_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDawnEditorUtilClassName() <em>Dawn Editor Util Class Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditorUtilClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_EDITOR_UTIL_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnEditorUtilClassName() <em>Dawn Editor Util Class Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditorUtilClassName()
   * @generated
   * @ordered
   */
  protected String dawnEditorUtilClassName = DAWN_EDITOR_UTIL_CLASS_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDawnCreationWizardClassName() <em>Dawn Creation Wizard Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnCreationWizardClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_CREATION_WIZARD_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnCreationWizardClassName() <em>Dawn Creation Wizard Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnCreationWizardClassName()
   * @generated
   * @ordered
   */
  protected String dawnCreationWizardClassName = DAWN_CREATION_WIZARD_CLASS_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDawnCanonicalEditingPolicyClassName()
   * <em>Dawn Canonical Editing Policy Class Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnCanonicalEditingPolicyClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnCanonicalEditingPolicyClassName()
   * <em>Dawn Canonical Editing Policy Class Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnCanonicalEditingPolicyClassName()
   * @generated
   * @ordered
   */
  protected String dawnCanonicalEditingPolicyClassName = DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDawnDiagramEditPartClassName() <em>Dawn Diagram Edit Part Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnDiagramEditPartClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_DIAGRAM_EDIT_PART_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnDiagramEditPartClassName() <em>Dawn Diagram Edit Part Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnDiagramEditPartClassName()
   * @generated
   * @ordered
   */
  protected String dawnDiagramEditPartClassName = DAWN_DIAGRAM_EDIT_PART_CLASS_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDawnEditPartFactoryClassName() <em>Dawn Edit Part Factory Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditPartFactoryClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_EDIT_PART_FACTORY_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnEditPartFactoryClassName() <em>Dawn Edit Part Factory Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditPartFactoryClassName()
   * @generated
   * @ordered
   */
  protected String dawnEditPartFactoryClassName = DAWN_EDIT_PART_FACTORY_CLASS_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDawnEditPartProviderClassName() <em>Dawn Edit Part Provider Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditPartProviderClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_EDIT_PART_PROVIDER_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnEditPartProviderClassName() <em>Dawn Edit Part Provider Class Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditPartProviderClassName()
   * @generated
   * @ordered
   */
  protected String dawnEditPartProviderClassName = DAWN_EDIT_PART_PROVIDER_CLASS_NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDawnEditPolicyProviderClassName()
   * <em>Dawn Edit Policy Provider Class Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditPolicyProviderClassName()
   * @generated
   * @ordered
   */
  protected static final String DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDawnEditPolicyProviderClassName()
   * <em>Dawn Edit Policy Provider Class Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDawnEditPolicyProviderClassName()
   * @generated
   * @ordered
   */
  protected String dawnEditPolicyProviderClassName = DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getGMFGenEditorGenerator() <em>GMF Gen Editor Generator</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getGMFGenEditorGenerator()
   * @generated
   * @ordered
   */
  protected GenEditorGenerator gmfGenEditorGenerator;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DawnGMFGeneratorImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return DawnGmfGenmodelPackage.Literals.DAWN_GMF_GENERATOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnDocumentProviderClassName()
  {
    return dawnDocumentProviderClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnDocumentProviderClassName(String newDawnDocumentProviderClassName)
  {
    String oldDawnDocumentProviderClassName = dawnDocumentProviderClassName;
    dawnDocumentProviderClassName = newDawnDocumentProviderClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME,
          oldDawnDocumentProviderClassName, dawnDocumentProviderClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnEditorUtilClassName()
  {
    return dawnEditorUtilClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnEditorUtilClassName(String newDawnEditorUtilClassName)
  {
    String oldDawnEditorUtilClassName = dawnEditorUtilClassName;
    dawnEditorUtilClassName = newDawnEditorUtilClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME, oldDawnEditorUtilClassName,
          dawnEditorUtilClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnCreationWizardClassName()
  {
    return dawnCreationWizardClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnCreationWizardClassName(String newDawnCreationWizardClassName)
  {
    String oldDawnCreationWizardClassName = dawnCreationWizardClassName;
    dawnCreationWizardClassName = newDawnCreationWizardClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME, oldDawnCreationWizardClassName,
          dawnCreationWizardClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnCanonicalEditingPolicyClassName()
  {
    return dawnCanonicalEditingPolicyClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnCanonicalEditingPolicyClassName(String newDawnCanonicalEditingPolicyClassName)
  {
    String oldDawnCanonicalEditingPolicyClassName = dawnCanonicalEditingPolicyClassName;
    dawnCanonicalEditingPolicyClassName = newDawnCanonicalEditingPolicyClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME,
          oldDawnCanonicalEditingPolicyClassName, dawnCanonicalEditingPolicyClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnDiagramEditPartClassName()
  {
    return dawnDiagramEditPartClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnDiagramEditPartClassName(String newDawnDiagramEditPartClassName)
  {
    String oldDawnDiagramEditPartClassName = dawnDiagramEditPartClassName;
    dawnDiagramEditPartClassName = newDawnDiagramEditPartClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME,
          oldDawnDiagramEditPartClassName, dawnDiagramEditPartClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnEditPartFactoryClassName()
  {
    return dawnEditPartFactoryClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnEditPartFactoryClassName(String newDawnEditPartFactoryClassName)
  {
    String oldDawnEditPartFactoryClassName = dawnEditPartFactoryClassName;
    dawnEditPartFactoryClassName = newDawnEditPartFactoryClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME,
          oldDawnEditPartFactoryClassName, dawnEditPartFactoryClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnEditPartProviderClassName()
  {
    return dawnEditPartProviderClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnEditPartProviderClassName(String newDawnEditPartProviderClassName)
  {
    String oldDawnEditPartProviderClassName = dawnEditPartProviderClassName;
    dawnEditPartProviderClassName = newDawnEditPartProviderClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME,
          oldDawnEditPartProviderClassName, dawnEditPartProviderClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDawnEditPolicyProviderClassName()
  {
    return dawnEditPolicyProviderClassName;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDawnEditPolicyProviderClassName(String newDawnEditPolicyProviderClassName)
  {
    String oldDawnEditPolicyProviderClassName = dawnEditPolicyProviderClassName;
    dawnEditPolicyProviderClassName = newDawnEditPolicyProviderClassName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME,
          oldDawnEditPolicyProviderClassName, dawnEditPolicyProviderClassName));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public GenEditorGenerator getGMFGenEditorGenerator()
  {
    if (gmfGenEditorGenerator != null && gmfGenEditorGenerator.eIsProxy())
    {
      InternalEObject oldGMFGenEditorGenerator = (InternalEObject)gmfGenEditorGenerator;
      gmfGenEditorGenerator = (GenEditorGenerator)eResolveProxy(oldGMFGenEditorGenerator);
      if (gmfGenEditorGenerator != oldGMFGenEditorGenerator)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE,
              DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR, oldGMFGenEditorGenerator,
              gmfGenEditorGenerator));
        }
      }
    }
    return gmfGenEditorGenerator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public GenEditorGenerator basicGetGMFGenEditorGenerator()
  {
    return gmfGenEditorGenerator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setGMFGenEditorGenerator(GenEditorGenerator newGMFGenEditorGenerator)
  {
    GenEditorGenerator oldGMFGenEditorGenerator = gmfGenEditorGenerator;
    gmfGenEditorGenerator = newGMFGenEditorGenerator;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET,
          DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR, oldGMFGenEditorGenerator,
          gmfGenEditorGenerator));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME:
      return getDawnDocumentProviderClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME:
      return getDawnEditorUtilClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME:
      return getDawnCreationWizardClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME:
      return getDawnCanonicalEditingPolicyClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME:
      return getDawnDiagramEditPartClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME:
      return getDawnEditPartFactoryClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME:
      return getDawnEditPartProviderClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME:
      return getDawnEditPolicyProviderClassName();
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR:
      if (resolve)
      {
        return getGMFGenEditorGenerator();
      }
      return basicGetGMFGenEditorGenerator();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME:
      setDawnDocumentProviderClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME:
      setDawnEditorUtilClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME:
      setDawnCreationWizardClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME:
      setDawnCanonicalEditingPolicyClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME:
      setDawnDiagramEditPartClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME:
      setDawnEditPartFactoryClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME:
      setDawnEditPartProviderClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME:
      setDawnEditPolicyProviderClassName((String)newValue);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR:
      setGMFGenEditorGenerator((GenEditorGenerator)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME:
      setDawnDocumentProviderClassName(DAWN_DOCUMENT_PROVIDER_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME:
      setDawnEditorUtilClassName(DAWN_EDITOR_UTIL_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME:
      setDawnCreationWizardClassName(DAWN_CREATION_WIZARD_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME:
      setDawnCanonicalEditingPolicyClassName(DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME:
      setDawnDiagramEditPartClassName(DAWN_DIAGRAM_EDIT_PART_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME:
      setDawnEditPartFactoryClassName(DAWN_EDIT_PART_FACTORY_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME:
      setDawnEditPartProviderClassName(DAWN_EDIT_PART_PROVIDER_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME:
      setDawnEditPolicyProviderClassName(DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME_EDEFAULT);
      return;
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR:
      setGMFGenEditorGenerator((GenEditorGenerator)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DOCUMENT_PROVIDER_CLASS_NAME:
      return DAWN_DOCUMENT_PROVIDER_CLASS_NAME_EDEFAULT == null ? dawnDocumentProviderClassName != null
          : !DAWN_DOCUMENT_PROVIDER_CLASS_NAME_EDEFAULT.equals(dawnDocumentProviderClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDITOR_UTIL_CLASS_NAME:
      return DAWN_EDITOR_UTIL_CLASS_NAME_EDEFAULT == null ? dawnEditorUtilClassName != null
          : !DAWN_EDITOR_UTIL_CLASS_NAME_EDEFAULT.equals(dawnEditorUtilClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CREATION_WIZARD_CLASS_NAME:
      return DAWN_CREATION_WIZARD_CLASS_NAME_EDEFAULT == null ? dawnCreationWizardClassName != null
          : !DAWN_CREATION_WIZARD_CLASS_NAME_EDEFAULT.equals(dawnCreationWizardClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME:
      return DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME_EDEFAULT == null ? dawnCanonicalEditingPolicyClassName != null
          : !DAWN_CANONICAL_EDITING_POLICY_CLASS_NAME_EDEFAULT.equals(dawnCanonicalEditingPolicyClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_DIAGRAM_EDIT_PART_CLASS_NAME:
      return DAWN_DIAGRAM_EDIT_PART_CLASS_NAME_EDEFAULT == null ? dawnDiagramEditPartClassName != null
          : !DAWN_DIAGRAM_EDIT_PART_CLASS_NAME_EDEFAULT.equals(dawnDiagramEditPartClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_FACTORY_CLASS_NAME:
      return DAWN_EDIT_PART_FACTORY_CLASS_NAME_EDEFAULT == null ? dawnEditPartFactoryClassName != null
          : !DAWN_EDIT_PART_FACTORY_CLASS_NAME_EDEFAULT.equals(dawnEditPartFactoryClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_PART_PROVIDER_CLASS_NAME:
      return DAWN_EDIT_PART_PROVIDER_CLASS_NAME_EDEFAULT == null ? dawnEditPartProviderClassName != null
          : !DAWN_EDIT_PART_PROVIDER_CLASS_NAME_EDEFAULT.equals(dawnEditPartProviderClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME:
      return DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME_EDEFAULT == null ? dawnEditPolicyProviderClassName != null
          : !DAWN_EDIT_POLICY_PROVIDER_CLASS_NAME_EDEFAULT.equals(dawnEditPolicyProviderClassName);
    case DawnGmfGenmodelPackage.DAWN_GMF_GENERATOR__GMF_GEN_EDITOR_GENERATOR:
      return gmfGenEditorGenerator != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (dawnDocumentProviderClassName: ");
    result.append(dawnDocumentProviderClassName);
    result.append(", dawnEditorUtilClassName: ");
    result.append(dawnEditorUtilClassName);
    result.append(", dawnCreationWizardClassName: ");
    result.append(dawnCreationWizardClassName);
    result.append(", dawnCanonicalEditingPolicyClassName: ");
    result.append(dawnCanonicalEditingPolicyClassName);
    result.append(", dawnDiagramEditPartClassName: ");
    result.append(dawnDiagramEditPartClassName);
    result.append(", dawnEditPartFactoryClassName: ");
    result.append(dawnEditPartFactoryClassName);
    result.append(", dawnEditPartProviderClassName: ");
    result.append(dawnEditPartProviderClassName);
    result.append(", dawnEditPolicyProviderClassName: ");
    result.append(dawnEditPolicyProviderClassName);
    result.append(')');
    return result.toString();
  }

} // DawnGMFGeneratorImpl
