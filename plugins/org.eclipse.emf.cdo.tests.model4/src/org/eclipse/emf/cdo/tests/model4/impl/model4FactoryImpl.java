/**
 * <copyright>
 * </copyright>
 *
 * $Id: model4FactoryImpl.java,v 1.1 2008-07-10 15:42:27 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class model4FactoryImpl extends EFactoryImpl implements model4Factory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static model4Factory init() {
		try {
			model4Factory themodel4Factory = (model4Factory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/emf/CDO/tests/model4/1.0.0"); 
			if (themodel4Factory != null) {
				return themodel4Factory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new model4FactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public model4FactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case model4Package.REF_SINGLE_CONTAINED: return (EObject)createRefSingleContained();
			case model4Package.SINGLE_CONTAINED_ELEMENT: return (EObject)createSingleContainedElement();
			case model4Package.REF_SINGLE_NON_CONTAINED: return (EObject)createRefSingleNonContained();
			case model4Package.SINGLE_NON_CONTAINED_ELEMENT: return (EObject)createSingleNonContainedElement();
			case model4Package.REF_MULTI_CONTAINED: return (EObject)createRefMultiContained();
			case model4Package.MULTI_CONTAINED_ELEMENT: return (EObject)createMultiContainedElement();
			case model4Package.REF_MULTI_NON_CONTAINED: return (EObject)createRefMultiNonContained();
			case model4Package.MULTI_NON_CONTAINED_ELEMENT: return (EObject)createMultiNonContainedElement();
			case model4Package.REF_SINGLE_CONTAINED_NPL: return (EObject)createRefSingleContainedNPL();
			case model4Package.REF_SINGLE_NON_CONTAINED_NPL: return (EObject)createRefSingleNonContainedNPL();
			case model4Package.REF_MULTI_CONTAINED_NPL: return (EObject)createRefMultiContainedNPL();
			case model4Package.REF_MULTI_NON_CONTAINED_NPL: return (EObject)createRefMultiNonContainedNPL();
			case model4Package.CONTAINED_ELEMENT_NO_OPPOSITE: return (EObject)createContainedElementNoOpposite();
			case model4Package.GEN_REF_SINGLE_CONTAINED: return (EObject)createGenRefSingleContained();
			case model4Package.GEN_REF_SINGLE_NON_CONTAINED: return (EObject)createGenRefSingleNonContained();
			case model4Package.GEN_REF_MULTI_CONTAINED: return (EObject)createGenRefMultiContained();
			case model4Package.GEN_REF_MULTI_NON_CONTAINED: return (EObject)createGenRefMultiNonContained();
			case model4Package.IMPL_SINGLE_REF_CONTAINER: return (EObject)createImplSingleRefContainer();
			case model4Package.IMPL_SINGLE_REF_CONTAINED_ELEMENT: return (EObject)createImplSingleRefContainedElement();
			case model4Package.IMPL_SINGLE_REF_NON_CONTAINER: return (EObject)createImplSingleRefNonContainer();
			case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT: return (EObject)createImplSingleRefNonContainedElement();
			case model4Package.IMPL_MULTI_REF_NON_CONTAINER: return (EObject)createImplMultiRefNonContainer();
			case model4Package.IMPL_MULTI_REF_NON_CONTAINED_ELEMENT: return (EObject)createImplMultiRefNonContainedElement();
			case model4Package.IMPL_MULTI_REF_CONTAINER: return (EObject)createImplMultiRefContainer();
			case model4Package.IMPL_MULTI_REF_CONTAINED_ELEMENT: return (EObject)createImplMultiRefContainedElement();
			case model4Package.IMPL_SINGLE_REF_CONTAINER_NPL: return (EObject)createImplSingleRefContainerNPL();
			case model4Package.IMPL_SINGLE_REF_NON_CONTAINER_NPL: return (EObject)createImplSingleRefNonContainerNPL();
			case model4Package.IMPL_MULTI_REF_CONTAINER_NPL: return (EObject)createImplMultiRefContainerNPL();
			case model4Package.IMPL_MULTI_REF_NON_CONTAINER_NPL: return (EObject)createImplMultiRefNonContainerNPL();
			case model4Package.IMPL_CONTAINED_ELEMENT_NPL: return (EObject)createImplContainedElementNPL();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefSingleContained createRefSingleContained() {
		RefSingleContainedImpl refSingleContained = new RefSingleContainedImpl();
		return refSingleContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SingleContainedElement createSingleContainedElement() {
		SingleContainedElementImpl singleContainedElement = new SingleContainedElementImpl();
		return singleContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefSingleNonContained createRefSingleNonContained() {
		RefSingleNonContainedImpl refSingleNonContained = new RefSingleNonContainedImpl();
		return refSingleNonContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SingleNonContainedElement createSingleNonContainedElement() {
		SingleNonContainedElementImpl singleNonContainedElement = new SingleNonContainedElementImpl();
		return singleNonContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefMultiContained createRefMultiContained() {
		RefMultiContainedImpl refMultiContained = new RefMultiContainedImpl();
		return refMultiContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MultiContainedElement createMultiContainedElement() {
		MultiContainedElementImpl multiContainedElement = new MultiContainedElementImpl();
		return multiContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefMultiNonContained createRefMultiNonContained() {
		RefMultiNonContainedImpl refMultiNonContained = new RefMultiNonContainedImpl();
		return refMultiNonContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MultiNonContainedElement createMultiNonContainedElement() {
		MultiNonContainedElementImpl multiNonContainedElement = new MultiNonContainedElementImpl();
		return multiNonContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefSingleContainedNPL createRefSingleContainedNPL() {
		RefSingleContainedNPLImpl refSingleContainedNPL = new RefSingleContainedNPLImpl();
		return refSingleContainedNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefSingleNonContainedNPL createRefSingleNonContainedNPL() {
		RefSingleNonContainedNPLImpl refSingleNonContainedNPL = new RefSingleNonContainedNPLImpl();
		return refSingleNonContainedNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefMultiContainedNPL createRefMultiContainedNPL() {
		RefMultiContainedNPLImpl refMultiContainedNPL = new RefMultiContainedNPLImpl();
		return refMultiContainedNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RefMultiNonContainedNPL createRefMultiNonContainedNPL() {
		RefMultiNonContainedNPLImpl refMultiNonContainedNPL = new RefMultiNonContainedNPLImpl();
		return refMultiNonContainedNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ContainedElementNoOpposite createContainedElementNoOpposite() {
		ContainedElementNoOppositeImpl containedElementNoOpposite = new ContainedElementNoOppositeImpl();
		return containedElementNoOpposite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenRefSingleContained createGenRefSingleContained() {
		GenRefSingleContainedImpl genRefSingleContained = new GenRefSingleContainedImpl();
		return genRefSingleContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenRefSingleNonContained createGenRefSingleNonContained() {
		GenRefSingleNonContainedImpl genRefSingleNonContained = new GenRefSingleNonContainedImpl();
		return genRefSingleNonContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenRefMultiContained createGenRefMultiContained() {
		GenRefMultiContainedImpl genRefMultiContained = new GenRefMultiContainedImpl();
		return genRefMultiContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GenRefMultiNonContained createGenRefMultiNonContained() {
		GenRefMultiNonContainedImpl genRefMultiNonContained = new GenRefMultiNonContainedImpl();
		return genRefMultiNonContained;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplSingleRefContainer createImplSingleRefContainer() {
		ImplSingleRefContainerImpl implSingleRefContainer = new ImplSingleRefContainerImpl();
		return implSingleRefContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplSingleRefContainedElement createImplSingleRefContainedElement() {
		ImplSingleRefContainedElementImpl implSingleRefContainedElement = new ImplSingleRefContainedElementImpl();
		return implSingleRefContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplSingleRefNonContainer createImplSingleRefNonContainer() {
		ImplSingleRefNonContainerImpl implSingleRefNonContainer = new ImplSingleRefNonContainerImpl();
		return implSingleRefNonContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplSingleRefNonContainedElement createImplSingleRefNonContainedElement() {
		ImplSingleRefNonContainedElementImpl implSingleRefNonContainedElement = new ImplSingleRefNonContainedElementImpl();
		return implSingleRefNonContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplMultiRefNonContainer createImplMultiRefNonContainer() {
		ImplMultiRefNonContainerImpl implMultiRefNonContainer = new ImplMultiRefNonContainerImpl();
		return implMultiRefNonContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplMultiRefNonContainedElement createImplMultiRefNonContainedElement() {
		ImplMultiRefNonContainedElementImpl implMultiRefNonContainedElement = new ImplMultiRefNonContainedElementImpl();
		return implMultiRefNonContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplMultiRefContainer createImplMultiRefContainer() {
		ImplMultiRefContainerImpl implMultiRefContainer = new ImplMultiRefContainerImpl();
		return implMultiRefContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplMultiRefContainedElement createImplMultiRefContainedElement() {
		ImplMultiRefContainedElementImpl implMultiRefContainedElement = new ImplMultiRefContainedElementImpl();
		return implMultiRefContainedElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplSingleRefContainerNPL createImplSingleRefContainerNPL() {
		ImplSingleRefContainerNPLImpl implSingleRefContainerNPL = new ImplSingleRefContainerNPLImpl();
		return implSingleRefContainerNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplSingleRefNonContainerNPL createImplSingleRefNonContainerNPL() {
		ImplSingleRefNonContainerNPLImpl implSingleRefNonContainerNPL = new ImplSingleRefNonContainerNPLImpl();
		return implSingleRefNonContainerNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplMultiRefContainerNPL createImplMultiRefContainerNPL() {
		ImplMultiRefContainerNPLImpl implMultiRefContainerNPL = new ImplMultiRefContainerNPLImpl();
		return implMultiRefContainerNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplMultiRefNonContainerNPL createImplMultiRefNonContainerNPL() {
		ImplMultiRefNonContainerNPLImpl implMultiRefNonContainerNPL = new ImplMultiRefNonContainerNPLImpl();
		return implMultiRefNonContainerNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplContainedElementNPL createImplContainedElementNPL() {
		ImplContainedElementNPLImpl implContainedElementNPL = new ImplContainedElementNPLImpl();
		return implContainedElementNPL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public model4Package getmodel4Package() {
		return (model4Package)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static model4Package getPackage() {
		return model4Package.eINSTANCE;
	}

} //model4FactoryImpl
