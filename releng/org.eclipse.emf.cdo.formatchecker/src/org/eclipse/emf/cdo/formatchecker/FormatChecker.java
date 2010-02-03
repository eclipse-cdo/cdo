/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.formatchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class FormatChecker
{
  private static final String COPYRIGHT = "Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.";

  private static Map<String, List<Integer>> complaints = new HashMap<String, List<Integer>>();

  public static void main(String[] args) throws IOException
  {
    File folder = new File(args.length == 0 ? "/develop/ws/cdo" : args[0]);
    recurse(folder);
    ignores();
    report();
  }

  private static void ignores()
  {
    ignore("base.BaseClass", 1);
    ignore("base.BaseFactory", 1);
    ignore("base.BasePackage", 2);
    ignore("base.impl.BaseClassImpl", 1);
    ignore("base.impl.BaseFactoryImpl", 2);
    ignore("base.impl.BasePackageImpl", 1);
    ignore("base.util.BaseAdapterFactory", 3);
    ignore("base.util.BaseSwitch", 2);
    ignore("com.swtdesigner.ResourceManager", 2);
    ignore("com.swtdesigner.SWTResourceManager", 2);
    ignore("derived.DerivedClass", 1);
    ignore("derived.DerivedFactory", 1);
    ignore("derived.DerivedPackage", 2);
    ignore("derived.impl.DerivedClassImpl", 1);
    ignore("derived.impl.DerivedFactoryImpl", 2);
    ignore("derived.impl.DerivedPackageImpl", 1);
    ignore("derived.util.DerivedAdapterFactory", 3);
    ignore("derived.util.DerivedSwitch", 2);
    ignore("interface_.IInterface", 1);
    ignore("interface_.InterfaceFactory", 1);
    ignore("interface_.InterfacePackage", 2);
    ignore("interface_.impl.InterfaceFactoryImpl", 2);
    ignore("interface_.impl.InterfacePackageImpl", 1);
    ignore("interface_.util.InterfaceAdapterFactory", 3);
    ignore("interface_.util.InterfaceSwitch", 2);
    ignore("org.eclipse.emf.cdo.defs.CDOAuditDef", 1);
    ignore("org.eclipse.emf.cdo.defs.CDODefsFactory", 1);
    ignore("org.eclipse.emf.cdo.defs.CDODefsPackage", 2);
    ignore("org.eclipse.emf.cdo.defs.CDOPackageRegistryDef", 1);
    ignore("org.eclipse.emf.cdo.defs.CDOResourceDef", 1);
    ignore("org.eclipse.emf.cdo.defs.CDOSessionDef", 1);
    ignore("org.eclipse.emf.cdo.defs.CDOTransactionDef", 2);
    ignore("org.eclipse.emf.cdo.defs.CDOViewDef", 1);
    ignore("org.eclipse.emf.cdo.defs.EDynamicPackageDef", 1);
    ignore("org.eclipse.emf.cdo.defs.EPackageDef", 1);
    ignore("org.eclipse.emf.cdo.defs.FailOverStrategyDef", 1);
    ignore("org.eclipse.emf.cdo.defs.ResourceMode", 4);
    ignore("org.eclipse.emf.cdo.defs.RetryFailOverStrategyDef", 1);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOAuditDefImpl", 5);
    ignore("org.eclipse.emf.cdo.defs.impl.CDODefsFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.defs.impl.CDODefsPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOEagerPackageRegistryDefImpl", 1);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOPackageRegistryDefImpl", 9);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOResourceDefImpl", 8);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl", 10);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOTransactionDefImpl", 2);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOViewDefImpl", 6);
    ignore("org.eclipse.emf.cdo.defs.impl.EDynamicPackageDefImpl", 5);
    ignore("org.eclipse.emf.cdo.defs.impl.EPackageDefImpl", 5);
    ignore("org.eclipse.emf.cdo.defs.impl.FailOverStrategyDefImpl", 6);
    ignore("org.eclipse.emf.cdo.defs.impl.RetryFailOverStrategyDefImpl", 5);
    ignore("org.eclipse.emf.cdo.defs.util.CDODefsAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.defs.util.CDODefsSwitch", 2);
    ignore("org.eclipse.emf.cdo.defs.util.CDODefsUtil", 3);
    ignore("org.eclipse.emf.cdo.ecore.impl.CDOSuperAdapter", 1);
    ignore("org.eclipse.emf.cdo.ecore.impl.EAnnotationImpl", 5);
    ignore("org.eclipse.emf.cdo.ecore.impl.EAttributeImpl", 6);
    ignore("org.eclipse.emf.cdo.ecore.impl.EClassImpl", 28);
    ignore("org.eclipse.emf.cdo.ecore.impl.EClassifierImpl", 13);
    ignore("org.eclipse.emf.cdo.ecore.impl.EDataTypeImpl", 7);
    ignore("org.eclipse.emf.cdo.ecore.impl.EEnumImpl", 10);
    ignore("org.eclipse.emf.cdo.ecore.impl.EEnumLiteralImpl", 2);
    ignore("org.eclipse.emf.cdo.ecore.impl.EFactoryImpl", 18);
    ignore("org.eclipse.emf.cdo.ecore.impl.EGenericTypeImpl", 9);
    ignore("org.eclipse.emf.cdo.ecore.impl.EModelElementImpl", 11);
    ignore("org.eclipse.emf.cdo.ecore.impl.ENamedElementImpl", 5);
    ignore("org.eclipse.emf.cdo.ecore.impl.EOperationImpl", 6);
    ignore("org.eclipse.emf.cdo.ecore.impl.EPackageImpl", 14);
    ignore("org.eclipse.emf.cdo.ecore.impl.EParameterImpl", 4);
    ignore("org.eclipse.emf.cdo.ecore.impl.EReferenceImpl", 6);
    ignore("org.eclipse.emf.cdo.ecore.impl.EStringToStringMapEntryImpl", 5);
    ignore("org.eclipse.emf.cdo.ecore.impl.EStructuralFeatureImpl", 25);
    ignore("org.eclipse.emf.cdo.ecore.impl.ETypeParameterImpl", 4);
    ignore("org.eclipse.emf.cdo.ecore.impl.ETypedElementImpl", 4);
    ignore("org.eclipse.emf.cdo.ecore.impl.EcoreFactoryImpl", 9);
    ignore("org.eclipse.emf.cdo.ecore.impl.EcorePackageImpl", 3);
    ignore("org.eclipse.emf.cdo.emodel.CDOAnnotation", 2);
    ignore("org.eclipse.emf.cdo.emodel.CDOModelElement", 2);
    ignore("org.eclipse.emf.cdo.emodel.CDONamedElement", 2);
    ignore("org.eclipse.emf.cdo.emodel.EmodelFactory", 2);
    ignore("org.eclipse.emf.cdo.emodel.EmodelPackage", 3);
    ignore("org.eclipse.emf.cdo.emodel.impl.CDOAnnotationImpl", 2);
    ignore("org.eclipse.emf.cdo.emodel.impl.CDOModelElementImpl", 2);
    ignore("org.eclipse.emf.cdo.emodel.impl.CDONamedElementImpl", 2);
    ignore("org.eclipse.emf.cdo.emodel.impl.EmodelFactoryImpl", 3);
    ignore("org.eclipse.emf.cdo.emodel.impl.EmodelPackageImpl", 3);
    ignore("org.eclipse.emf.cdo.emodel.provider.CDOAnnotationItemProvider", 5);
    ignore("org.eclipse.emf.cdo.emodel.provider.CDOModelElementItemProvider", 6);
    ignore("org.eclipse.emf.cdo.emodel.provider.CDONamedElementItemProvider", 4);
    ignore("org.eclipse.emf.cdo.emodel.provider.EmodelEditPlugin", 2);
    ignore("org.eclipse.emf.cdo.emodel.provider.EmodelItemProviderAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.emodel.util.EmodelAdapterFactory", 4);
    ignore("org.eclipse.emf.cdo.emodel.util.EmodelSwitch", 3);
    ignore("org.eclipse.emf.cdo.emodel.util.EmodelValidator", 3);
    ignore("org.eclipse.emf.cdo.eresource.CDOResource", 1);
    ignore("org.eclipse.emf.cdo.eresource.CDOResourceFolder", 1);
    ignore("org.eclipse.emf.cdo.eresource.CDOResourceNode", 1);
    ignore("org.eclipse.emf.cdo.eresource.EresourceFactory", 1);
    ignore("org.eclipse.emf.cdo.eresource.EresourcePackage", 2);
    ignore("org.eclipse.emf.cdo.eresource.impl.EresourceFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl", 1);
    ignore("org.eclipse.emf.cdo.eresource.provider.CDOResourceFolderItemProvider", 5);
    ignore("org.eclipse.emf.cdo.eresource.provider.CDOResourceItemProvider", 4);
    ignore("org.eclipse.emf.cdo.eresource.provider.CDOResourceNodeItemProvider", 3);
    ignore("org.eclipse.emf.cdo.eresource.provider.EresourceEditPlugin", 1);
    ignore("org.eclipse.emf.cdo.eresource.provider.EresourceItemProviderAdapterFactory", 1);
    ignore("org.eclipse.emf.cdo.eresource.util.EresourceAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.eresource.util.EresourceSwitch", 2);
    ignore("org.eclipse.emf.cdo.examples.company.Address", 1);
    ignore("org.eclipse.emf.cdo.examples.company.Category", 1);
    ignore("org.eclipse.emf.cdo.examples.company.Company", 1);
    ignore("org.eclipse.emf.cdo.examples.company.CompanyFactory", 1);
    ignore("org.eclipse.emf.cdo.examples.company.CompanyPackage", 2);
    ignore("org.eclipse.emf.cdo.examples.company.Customer", 1);
    ignore("org.eclipse.emf.cdo.examples.company.Order", 1);
    ignore("org.eclipse.emf.cdo.examples.company.OrderAddress", 1);
    ignore("org.eclipse.emf.cdo.examples.company.OrderDetail", 1);
    ignore("org.eclipse.emf.cdo.examples.company.Product", 1);
    ignore("org.eclipse.emf.cdo.examples.company.PurchaseOrder", 1);
    ignore("org.eclipse.emf.cdo.examples.company.SalesOrder", 1);
    ignore("org.eclipse.emf.cdo.examples.company.Supplier", 1);
    ignore("org.eclipse.emf.cdo.examples.company.VAT", 4);
    ignore("org.eclipse.emf.cdo.examples.company.impl.AddressImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.CategoryImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.CompanyFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.examples.company.impl.CompanyImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.CompanyPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.CustomerImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.OrderAddressImpl", 5);
    ignore("org.eclipse.emf.cdo.examples.company.impl.OrderDetailImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.OrderImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.ProductImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.PurchaseOrderImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.SalesOrderImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.impl.SupplierImpl", 1);
    ignore("org.eclipse.emf.cdo.examples.company.provider.AddressItemProvider", 3);
    ignore("org.eclipse.emf.cdo.examples.company.provider.CategoryItemProvider", 4);
    ignore("org.eclipse.emf.cdo.examples.company.provider.CompanyEditPlugin", 1);
    ignore("org.eclipse.emf.cdo.examples.company.provider.CompanyItemProvider", 5);
    ignore("org.eclipse.emf.cdo.examples.company.provider.CompanyItemProviderAdapterFactory", 1);
    ignore("org.eclipse.emf.cdo.examples.company.provider.CustomerItemProvider", 2);
    ignore("org.eclipse.emf.cdo.examples.company.provider.OrderAddressItemProvider", 4);
    ignore("org.eclipse.emf.cdo.examples.company.provider.OrderDetailItemProvider", 3);
    ignore("org.eclipse.emf.cdo.examples.company.provider.OrderItemProvider", 5);
    ignore("org.eclipse.emf.cdo.examples.company.provider.ProductItemProvider", 3);
    ignore("org.eclipse.emf.cdo.examples.company.provider.PurchaseOrderItemProvider", 3);
    ignore("org.eclipse.emf.cdo.examples.company.provider.SalesOrderItemProvider", 3);
    ignore("org.eclipse.emf.cdo.examples.company.provider.SupplierItemProvider", 3);
    ignore("org.eclipse.emf.cdo.examples.company.util.CompanyAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.examples.company.util.CompanySwitch", 2);
    ignore("org.eclipse.emf.cdo.internal.ui.editor.CDOActionBarContributor", 5);
    ignore("org.eclipse.emf.cdo.internal.ui.editor.CDOEditor", 11);
    ignore("org.eclipse.emf.cdo.internal.ui.editor.PluginDelegator", 1);
    ignore("org.eclipse.emf.cdo.server.file.FileStore", 1);
    ignore("org.eclipse.emf.cdo.server.file.FileStoreAccessor", 1);
    ignore("org.eclipse.emf.cdo.server.hibernate.internal.teneo.CDOEntityMapper", 1);
    ignore("org.eclipse.emf.cdo.tests.defs.CDOSessionDefImplTest", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoParameterImpl", 5);
    ignore("org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoValueImpl", 5);
    ignore("org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoValueListImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.Model1Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.Model1Package", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.AddressImpl", 5);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.CategoryImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.CompanyImpl", 11);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.CustomerImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.Model1FactoryImpl", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.Model1PackageImpl", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.OrderAddressImpl", 24);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.OrderDetailImpl", 19);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.OrderImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.Product1Impl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.ProductToOrderImpl", 10);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.PurchaseOrderImpl", 14);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.SalesOrderImpl", 13);
    ignore("org.eclipse.emf.cdo.tests.legacy.model1.impl.SupplierImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.Model2Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.Model2Package", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.Model2FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.Model2PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.PersistentContainmentImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.SpecialPurchaseOrderImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.TaskContainerImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.TaskImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.TransientContainerImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.Unsettable1Impl", 5);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.impl.Unsettable2WithDefaultImpl", 5);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.util.Model2AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model2.util.Model2Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.Model3Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.Model3Package", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.impl.Class1Impl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.impl.MetaRefImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.impl.Model3PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.subpackage.SubpackageFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.subpackage.SubpackagePackage", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.subpackage.impl.Class2Impl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.subpackage.impl.SubpackageFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.subpackage.impl.SubpackagePackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.subpackage.util.SubpackageAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.subpackage.util.SubpackageSwitch", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.util.Model3AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model3.util.Model3Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ContainedElementNoOppositeImpl", 5);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.GenRefMapNonContainedImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.GenRefMultiContainedImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.GenRefMultiNUNonContainedImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.GenRefMultiNonContainedImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.GenRefSingleContainedImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.GenRefSingleNonContainedImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplContainedElementNPLImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplMultiRefContainedElementImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplMultiRefContainerImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplMultiRefContainerNPLImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplMultiRefNonContainedElementImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplMultiRefNonContainerImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplMultiRefNonContainerNPLImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplSingleRefContainedElementImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplSingleRefContainerImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplSingleRefContainerNPLImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplSingleRefNonContainedElementImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplSingleRefNonContainerImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.ImplSingleRefNonContainerNPLImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.MultiContainedElementImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.MultiNonContainedElementImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.MultiNonContainedUnsettableElementImpl", 10);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefMultiContainedImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefMultiContainedNPLImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefMultiNonContainedImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefMultiNonContainedNPLImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefMultiNonContainedUnsettableImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefSingleContainedImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefSingleContainedNPLImpl", 7);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefSingleNonContainedImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.RefSingleNonContainedNPLImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.SingleContainedElementImpl", 8);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.SingleNonContainedElementImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.StringToEObjectImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.model4FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.impl.model4PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.model4Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.model4Package", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.util.model4AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4.util.model4Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.impl.model4interfacesFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.impl.model4interfacesPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.model4interfacesFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.model4interfacesPackage", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.util.model4interfacesAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.util.model4interfacesSwitch", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.Model5Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.Model5Package", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.DoctorImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfBooleanImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfCharImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfDateImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfDoubleImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfFloatImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfIntImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfIntegerImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfLongImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfShortImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.GenListOfStringImpl", 6);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.ManagerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.Model5FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.Model5PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.impl.TestFeatureMapImpl", 9);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.util.Model5AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.legacy.model5.util.Model5Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.mango.MangoFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.MangoPackage", 2);
    ignore("org.eclipse.emf.cdo.tests.mango.MangoParameter", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.MangoValue", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.MangoValueList", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.ParameterPassing", 4);
    ignore("org.eclipse.emf.cdo.tests.mango.impl.MangoFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.mango.impl.MangoPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.impl.MangoParameterImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.impl.MangoValueImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.impl.MangoValueListImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.util.MangoAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.mango.util.MangoSwitch", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.Address", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.Category", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.Company", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.Customer", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.Model1Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.Model1Package", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.Order", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.OrderAddress", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.OrderDetail", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.Product1", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.PurchaseOrder", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.SalesOrder", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.Supplier", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.VAT", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryCreateCommand", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProducts2CreateCommand", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProducts2ReorientCommand", 10);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProductsCreateCommand", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProductsReorientCommand", 10);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CustomerCreateCommand", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.Model1ReorientConnectionViewCommand", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderAddressCreateCommand", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderDetailCreateCommand", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetails2CreateCommand", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetails2ReorientCommand", 10);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetailsCreateCommand", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetailsReorientCommand", 10);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.Product1CreateCommand", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.PurchaseOrderCreateCommand", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SalesOrderCreateCommand", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SalesOrderCustomerCreateCommand", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SalesOrderCustomerReorientCommand", 10);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SupplierCreateCommand", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.CategoryEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.CompanyEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.CustomerEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.Model1BaseEditHelper", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.OrderAddressEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.OrderDetailEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.Product1EditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.PurchaseOrderEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.SalesOrderEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.SupplierEditHelper", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryEditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryNameEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryProducts2EditPart", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryProductsEditPart", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CompanyEditPart", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CustomerEditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CustomerNameEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.Model1EditPartFactory", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderAddressEditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderAddressNameEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderDetailEditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderDetailPriceEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderOrderDetails2EditPart", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderOrderDetailsEditPart", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.Product1EditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.Product1NameEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.PurchaseOrderDateEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.PurchaseOrderEditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SalesOrderCustomerEditPart", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SalesOrderEditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SalesOrderIdEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SupplierEditPart", 19);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SupplierNameEditPart", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryCanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryProducts2ItemSemanticEditPolicy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryProductsItemSemanticEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CompanyCanonicalEditPolicy", 33);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CompanyItemSemanticEditPolicy", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CustomerCanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CustomerItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Model1BaseItemSemanticEditPolicy", 31);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Model1TextNonResizableEditPolicy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Model1TextSelectionEditPolicy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderAddressCanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderAddressItemSemanticEditPolicy", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderDetailCanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderDetailItemSemanticEditPolicy", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderOrderDetails2ItemSemanticEditPolicy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderOrderDetailsItemSemanticEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Product1CanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Product1ItemSemanticEditPolicy", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.PurchaseOrderCanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.PurchaseOrderItemSemanticEditPolicy", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SalesOrderCanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SalesOrderCustomerItemSemanticEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SalesOrderItemSemanticEditPolicy", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SupplierCanonicalEditPolicy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SupplierItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1AbstractNavigatorItem", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1DomainNavigatorContentProvider", 16);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1DomainNavigatorItem", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1DomainNavigatorLabelProvider", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorActionProvider", 10);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorContentProvider", 46);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorGroup", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorItem", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorLabelProvider", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorLinkHelper", 12);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorSorter", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.parsers.AbstractParser", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.parsers.CompositeParser", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.parsers.MessageFormatParser", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.DeleteElementAction", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.DiagramEditorContextMenuProvider", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Messages", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1CreationWizard", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1CreationWizardPage", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramActionBarContributor", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramEditor", 17);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramEditorPlugin", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramEditorUtil", 20);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramUpdateCommand", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramUpdater", 27);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DocumentProvider", 62);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DomainModelElementTester", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1InitDiagramFileAction", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1LinkDescriptor", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1LoadResourceAction", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1MatchingStrategy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1NewDiagramFileWizard", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1NodeDescriptor", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1PaletteFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1UriEditorInputTester", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1VisualIDRegistry", 41);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.ModelElementSelectionPage", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramAppearancePreferencePage", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramConnectionsPreferencePage", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramGeneralPreferencePage", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramPreferenceInitializer", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramPrintingPreferencePage", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramRulersAndGridPreferencePage", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.ElementInitializers", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1EditPartProvider", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ElementTypes", 14);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1IconProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ModelingAssistantProvider", 40);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ParserProvider", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ViewProvider", 26);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.sheet.Model1PropertySection", 14);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.sheet.Model1SheetLabelProvider", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryNameViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryProducts2ViewFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryProductsViewFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CompanyViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CustomerNameViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CustomerViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderAddressNameViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderAddressViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderDetailPriceViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderDetailViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderOrderDetails2ViewFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderOrderDetailsViewFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.Product1NameViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.Product1ViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.PurchaseOrderDateViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.PurchaseOrderViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SalesOrderCustomerViewFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SalesOrderIdViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SalesOrderViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SupplierNameViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SupplierViewFactory", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.AddressImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.CustomerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.Model1FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.OrderImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.Product1Impl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.ProductToOrderImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.util.Model1AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.util.Model1Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.model2.Model2Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.Model2Package", 2);
    ignore("org.eclipse.emf.cdo.tests.model2.PersistentContainment", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.Task", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.TaskContainer", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.TransientContainer", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.Unsettable1", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.Model2FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.PersistentContainmentImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.TaskImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.Unsettable1Impl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.Unsettable2WithDefaultImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.util.Model2AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model2.util.Model2Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.Class1", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.MetaRef", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.Model3Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.Model3Package", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.impl.Class1Impl", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.impl.MetaRefImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.impl.Model3FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.impl.Model3PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.Class2", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.impl.Class2Impl", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.impl.SubpackageFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.impl.SubpackagePackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.util.SubpackageAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.util.SubpackageSwitch", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.util.Model3AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model3.util.Model3Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.GenRefMultiContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.GenRefMultiNUNonContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.GenRefSingleContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.MultiContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefMultiContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefMultiNonContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefSingleContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefSingleNonContained", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.SingleContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ContainedElementNoOppositeImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.GenRefMapNonContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.GenRefMultiContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.GenRefMultiNUNonContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.GenRefMultiNonContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.GenRefSingleContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.GenRefSingleNonContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplContainedElementNPLImpl", 3);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefContainerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefContainerNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefNonContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefNonContainerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefNonContainerNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplSingleRefContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplSingleRefContainerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplSingleRefContainerNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplSingleRefNonContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplSingleRefNonContainerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplSingleRefNonContainerNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.MultiContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.MultiNonContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.MultiNonContainedUnsettableElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefMultiContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefMultiContainedNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefMultiNonContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefMultiNonContainedNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefMultiNonContainedUnsettableImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefSingleContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefSingleContainedNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefSingleNonContainedImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.RefSingleNonContainedNPLImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.SingleContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.SingleNonContainedElementImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.StringToEObjectImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.model4FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.model4PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.model4Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.model4Package", 2);
    ignore("org.eclipse.emf.cdo.tests.model4.util.model4AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model4.util.model4Switch", 2);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplContainedElementNPLValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplMultiRefContainerNPLValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplMultiRefContainerValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplMultiRefNonContainerNPLValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplMultiRefNonContainerValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplSingleRefContainerNPLValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplSingleRefContainerValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplSingleRefNonContainerNPLValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.validation.ImplSingleRefNonContainerValidator", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainerNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainer", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainerNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.INamedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainer", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainerNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainerNPL", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.impl.model4interfacesFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.impl.model4interfacesPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage", 2);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.util.model4interfacesAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.util.model4interfacesSwitch", 2);
    ignore("org.eclipse.emf.cdo.tests.model5.Doctor", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfBoolean", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfChar", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfDate", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfDouble", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfFloat", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfInt", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfInteger", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfLong", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfShort", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.GenListOfString", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.Manager", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.Model5Factory", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.Model5Package", 2);
    ignore("org.eclipse.emf.cdo.tests.model5.TestFeatureMap", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.DoctorImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfBooleanImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfCharImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfDateImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfDoubleImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfFloatImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfIntegerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfLongImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfShortImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.GenListOfStringImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.ManagerImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.Model5FactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.Model5PackageImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.util.Model5AdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model5.util.Model5Switch", 2);
    ignore("org.eclipse.emf.cdo.ui.defs.CDOEditorDef", 1);
    ignore("org.eclipse.emf.cdo.ui.defs.CDOUIDefsFactory", 1);
    ignore("org.eclipse.emf.cdo.ui.defs.CDOUIDefsPackage", 2);
    ignore("org.eclipse.emf.cdo.ui.defs.EditorDef", 1);
    ignore("org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl", 9);
    ignore("org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsFactoryImpl", 2);
    ignore("org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.ui.defs.impl.EditorDefImpl", 4);
    ignore("org.eclipse.emf.cdo.ui.defs.util.CDOUIDefsAdapterFactory", 3);
    ignore("org.eclipse.emf.cdo.ui.defs.util.CDOUIDefsSwitch", 2);
    ignore("org.eclipse.net4j.defs.AcceptorDef", 1);
    ignore("org.eclipse.net4j.defs.BufferPoolDef", 2);
    ignore("org.eclipse.net4j.defs.BufferProviderDef", 2);
    ignore("org.eclipse.net4j.defs.ClientProtocolFactoryDef", 2);
    ignore("org.eclipse.net4j.defs.ConnectorDef", 1);
    ignore("org.eclipse.net4j.defs.HTTPConnectorDef", 1);
    ignore("org.eclipse.net4j.defs.JVMAcceptorDef", 1);
    ignore("org.eclipse.net4j.defs.JVMConnectorDef", 1);
    ignore("org.eclipse.net4j.defs.Net4jDefsFactory", 1);
    ignore("org.eclipse.net4j.defs.Net4jDefsPackage", 2);
    ignore("org.eclipse.net4j.defs.ProtocolProviderDef", 2);
    ignore("org.eclipse.net4j.defs.ServerProtocolFactoryDef", 2);
    ignore("org.eclipse.net4j.defs.TCPAcceptorDef", 1);
    ignore("org.eclipse.net4j.defs.TCPConnectorDef", 1);
    ignore("org.eclipse.net4j.defs.TCPSelectorDef", 2);
    ignore("org.eclipse.net4j.defs.impl.AcceptorDefImpl", 11);
    ignore("org.eclipse.net4j.defs.impl.BufferProviderDefImpl", 1);
    ignore("org.eclipse.net4j.defs.impl.ClientProtocolFactoryDefImpl", 1);
    ignore("org.eclipse.net4j.defs.impl.ConnectorDefImpl", 12);
    ignore("org.eclipse.net4j.defs.impl.HTTPConnectorDefImpl", 4);
    ignore("org.eclipse.net4j.defs.impl.JVMAcceptorDefImpl", 4);
    ignore("org.eclipse.net4j.defs.impl.JVMConnectorDefImpl", 5);
    ignore("org.eclipse.net4j.defs.impl.Net4jDefsFactoryImpl", 2);
    ignore("org.eclipse.net4j.defs.impl.Net4jDefsPackageImpl", 1);
    ignore("org.eclipse.net4j.defs.impl.ProtocolProviderDefImpl", 1);
    ignore("org.eclipse.net4j.defs.impl.ServerProtocolFactoryDefImpl", 1);
    ignore("org.eclipse.net4j.defs.impl.TCPAcceptorDefImpl", 5);
    ignore("org.eclipse.net4j.defs.impl.TCPConnectorDefImpl", 5);
    ignore("org.eclipse.net4j.defs.util.Net4jDefsAdapterFactory", 3);
    ignore("org.eclipse.net4j.defs.util.Net4jDefsSwitch", 2);
    ignore("org.eclipse.net4j.defs.util.Net4jDefsUtil", 1);
    ignore("org.eclipse.net4j.ui.defs.Net4JUIDefsFactory", 1);
    ignore("org.eclipse.net4j.ui.defs.Net4JUIDefsPackage", 2);
    ignore("org.eclipse.net4j.ui.defs.impl.Net4JUIDefsFactoryImpl", 2);
    ignore("org.eclipse.net4j.ui.defs.impl.Net4JUIDefsPackageImpl", 1);
    ignore("org.eclipse.net4j.ui.defs.util.Net4JUIDefsAdapterFactory", 3);
    ignore("org.eclipse.net4j.ui.defs.util.Net4JUIDefsSwitch", 2);
    ignore("org.eclipse.net4j.util.defs.ChallengeNegotiatorDef", 1);
    ignore("org.eclipse.net4j.util.defs.CredentialsProviderDef", 1);
    ignore("org.eclipse.net4j.util.defs.DefContainer", 1);
    ignore("org.eclipse.net4j.util.defs.DefException", 2);
    ignore("org.eclipse.net4j.util.defs.NegotiatorDef", 2);
    ignore("org.eclipse.net4j.util.defs.Net4jUtilDefsFactory", 1);
    ignore("org.eclipse.net4j.util.defs.Net4jUtilDefsPackage", 2);
    ignore("org.eclipse.net4j.util.defs.PasswordCredentialsProviderDef", 1);
    ignore("org.eclipse.net4j.util.defs.RandomizerDef", 1);
    ignore("org.eclipse.net4j.util.defs.ResponseNegotiatorDef", 1);
    ignore("org.eclipse.net4j.util.defs.User", 1);
    ignore("org.eclipse.net4j.util.defs.UserManagerDef", 1);
    ignore("org.eclipse.net4j.util.defs.impl.ChallengeNegotiatorDefImpl", 8);
    ignore("org.eclipse.net4j.util.defs.impl.CredentialsProviderDefImpl", 5);
    ignore("org.eclipse.net4j.util.defs.impl.DefContainerImpl", 8);
    ignore("org.eclipse.net4j.util.defs.impl.DefImpl", 6);
    ignore("org.eclipse.net4j.util.defs.impl.ExecutorServiceDefImpl", 1);
    ignore("org.eclipse.net4j.util.defs.impl.NegotiatorDefImpl", 1);
    ignore("org.eclipse.net4j.util.defs.impl.Net4jUtilDefsFactoryImpl", 2);
    ignore("org.eclipse.net4j.util.defs.impl.Net4jUtilDefsPackageImpl", 1);
    ignore("org.eclipse.net4j.util.defs.impl.PasswordCredentialsProviderDefImpl", 6);
    ignore("org.eclipse.net4j.util.defs.impl.RandomizerDefImpl", 8);
    ignore("org.eclipse.net4j.util.defs.impl.ResponseNegotiatorDefImpl", 6);
    ignore("org.eclipse.net4j.util.defs.impl.ThreadPoolDefImpl", 2);
    ignore("org.eclipse.net4j.util.defs.impl.UserImpl", 5);
    ignore("org.eclipse.net4j.util.defs.impl.UserManagerDefImpl", 7);
    ignore("org.eclipse.net4j.util.defs.util.Net4jUtilDefsAdapterFactory", 3);
    ignore("org.eclipse.net4j.util.defs.util.Net4jUtilDefsSwitch", 2);
    ignore("org.eclipse.net4j.util.tests.defs.DefsFactory", 1);
    ignore("org.eclipse.net4j.util.tests.defs.DefsPackage", 2);
    ignore("org.eclipse.net4j.util.tests.defs.TestDef", 1);
    ignore("org.eclipse.net4j.util.tests.defs.TestDefsFactory", 1);
    ignore("org.eclipse.net4j.util.tests.defs.TestDefsPackage", 2);
    ignore("org.eclipse.net4j.util.tests.defs.impl.DefsFactoryImpl", 2);
    ignore("org.eclipse.net4j.util.tests.defs.impl.DefsPackageImpl", 3);
    ignore("org.eclipse.net4j.util.tests.defs.impl.TestDefImpl", 6);
    ignore("org.eclipse.net4j.util.tests.defs.impl.TestDefsFactoryImpl", 2);
    ignore("org.eclipse.net4j.util.tests.defs.impl.TestDefsPackageImpl", 1);
    ignore("org.eclipse.net4j.util.tests.defs.util.DefsAdapterFactory", 3);
    ignore("org.eclipse.net4j.util.tests.defs.util.DefsSwitch", 4);
    ignore("org.eclipse.net4j.util.tests.defs.util.TestDefsAdapterFactory", 3);
    ignore("org.eclipse.net4j.util.tests.defs.util.TestDefsSwitch", 2);
    ignore("org.eclipse.net4j.util.ui.proposals.RegExContentProposalProvider", 11);
    ignore("org.eclipse.net4j.util.ui.proposals.RegExMessages", 3);
    ignore("org.gastro.business.BusinessDay", 1);
    ignore("org.gastro.business.BusinessFactory", 1);
    ignore("org.gastro.business.BusinessPackage", 2);
    ignore("org.gastro.business.Order", 1);
    ignore("org.gastro.business.OrderDetail", 1);
    ignore("org.gastro.business.OrderState", 4);
    ignore("org.gastro.business.Waiter", 1);
    ignore("org.gastro.business.impl.BusinessDayImpl", 1);
    ignore("org.gastro.business.impl.BusinessFactoryImpl", 2);
    ignore("org.gastro.business.impl.BusinessPackageImpl", 1);
    ignore("org.gastro.business.impl.OrderImpl", 1);
    ignore("org.gastro.business.impl.WaiterImpl", 1);
    ignore("org.gastro.business.provider.BusinessDayItemProvider", 4);
    ignore("org.gastro.business.provider.BusinessEditPlugin", 1);
    ignore("org.gastro.business.provider.BusinessItemProviderAdapterFactory", 1);
    ignore("org.gastro.business.provider.OrderDetailItemProvider", 3);
    ignore("org.gastro.business.provider.OrderItemProvider", 4);
    ignore("org.gastro.business.provider.WaiterItemProvider", 3);
    ignore("org.gastro.business.util.BusinessAdapterFactory", 3);
    ignore("org.gastro.business.util.BusinessSwitch", 2);
    ignore("org.gastro.inventory.Department", 1);
    ignore("org.gastro.inventory.Employee", 1);
    ignore("org.gastro.inventory.Ingredient", 1);
    ignore("org.gastro.inventory.InventoryFactory", 1);
    ignore("org.gastro.inventory.InventoryPackage", 2);
    ignore("org.gastro.inventory.MenuCard", 1);
    ignore("org.gastro.inventory.Offering", 1);
    ignore("org.gastro.inventory.Recipe", 1);
    ignore("org.gastro.inventory.Restaurant", 1);
    ignore("org.gastro.inventory.Section", 1);
    ignore("org.gastro.inventory.Station", 1);
    ignore("org.gastro.inventory.Stock", 1);
    ignore("org.gastro.inventory.StockProduct", 1);
    ignore("org.gastro.inventory.Table", 1);
    ignore("org.gastro.inventory.impl.DepartmentImpl", 1);
    ignore("org.gastro.inventory.impl.EmployeeImpl", 1);
    ignore("org.gastro.inventory.impl.IngredientImpl", 1);
    ignore("org.gastro.inventory.impl.InventoryFactoryImpl", 2);
    ignore("org.gastro.inventory.impl.InventoryPackageImpl", 1);
    ignore("org.gastro.inventory.impl.MenuCardImpl", 1);
    ignore("org.gastro.inventory.impl.OfferingImpl", 1);
    ignore("org.gastro.inventory.impl.ProductImpl", 1);
    ignore("org.gastro.inventory.impl.RecipeImpl", 1);
    ignore("org.gastro.inventory.impl.RestaurantImpl", 1);
    ignore("org.gastro.inventory.impl.SectionImpl", 1);
    ignore("org.gastro.inventory.impl.StationImpl", 1);
    ignore("org.gastro.inventory.impl.StockImpl", 1);
    ignore("org.gastro.inventory.impl.StockProductImpl", 1);
    ignore("org.gastro.inventory.impl.TableImpl", 1);
    ignore("org.gastro.inventory.provider.DepartmentItemProvider", 5);
    ignore("org.gastro.inventory.provider.EmployeeItemProvider", 3);
    ignore("org.gastro.inventory.provider.IngredientItemProvider", 3);
    ignore("org.gastro.inventory.provider.InventoryEditPlugin", 1);
    ignore("org.gastro.inventory.provider.InventoryItemProviderAdapterFactory", 1);
    ignore("org.gastro.inventory.provider.MenuCardItemProvider", 4);
    ignore("org.gastro.inventory.provider.OfferingItemProvider", 3);
    ignore("org.gastro.inventory.provider.ProductItemProvider", 3);
    ignore("org.gastro.inventory.provider.RecipeItemProvider", 4);
    ignore("org.gastro.inventory.provider.RestaurantItemProvider", 4);
    ignore("org.gastro.inventory.provider.SectionItemProvider", 4);
    ignore("org.gastro.inventory.provider.StationItemProvider", 3);
    ignore("org.gastro.inventory.provider.StockItemProvider", 4);
    ignore("org.gastro.inventory.provider.StockProductItemProvider", 3);
    ignore("org.gastro.inventory.provider.TableItemProvider", 3);
    ignore("org.gastro.inventory.util.InventoryAdapterFactory", 3);
    ignore("org.gastro.inventory.util.InventorySwitch", 2);
    ignore("reference.Reference", 1);
    ignore("reference.ReferenceFactory", 1);
    ignore("reference.ReferencePackage", 2);
    ignore("reference.impl.ReferenceFactoryImpl", 2);
    ignore("reference.impl.ReferenceImpl", 1);
    ignore("reference.impl.ReferencePackageImpl", 1);
    ignore("reference.util.ReferenceAdapterFactory", 3);
    ignore("reference.util.ReferenceSwitch", 2);
    ignore("templates.MenuCardTemplate", 4);
  }

  private static void recurse(File folder) throws IOException
  {
    for (File file : folder.listFiles())
    {
      String name = file.getName();
      if (file.isDirectory())
      {
        if (!name.startsWith("."))
        {
          if (new File(file, ".project").exists() && !new File(file, "copyright.txt").exists())
          {
            continue;
          }

          recurse(file);
        }
      }
      else
      {
        if (name.endsWith(".java"))
        {
          processJava(file);
        }
      }
    }
  }

  private static void processJava(File file) throws IOException
  {
    String name = file.getName();
    name = name.substring(0, name.length() - ".java".length());

    InputStream stream = new FileInputStream(file);
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    try
    {
      int copyrightLine = -1;
      String packageName = null;
      String last = "";
      String line;
      int i = 0;
      while ((line = reader.readLine()) != null)
      {
        ++i;
        if (packageName == null)
        {
          if (copyrightLine == -1 && line.startsWith("/**"))
          {
            copyrightLine = 0;
            continue;
          }

          if (copyrightLine == 0 && line.startsWith(" * " + COPYRIGHT))
          {
            copyrightLine = i;
            continue;
          }

          if (line.startsWith("package "))
          {
            packageName = line.substring("package ".length()); // Remove prefix "package "
            packageName = packageName.substring(0, packageName.length() - 1); // Remove suffix ";"
            packageName = packageName.trim();
            name = packageName + "." + name;
            continue;
          }
        }

        String type = type(line);
        if (last.equals("{"))
        {
          if (type.equals("empty"))
          {
            complain(name, i);
          }
        }
        else if (last.equals("}"))
        {
          if (!(type.equals("empty") || type.equals("}") || type.equals("stmt")))
          {
            complain(name, i);
          }
        }
        else if (last.equals("empty"))
        {
          if (type.equals("empty") || type.equals("}"))
          {
            complain(name, i);
          }
        }

        last = type;
      }

      if (copyrightLine <= 0)
      {
        complain(name, 1);
      }
    }
    finally
    {
      stream.close();
    }
  }

  private static String type(String line)
  {
    line = line.trim();
    int i = line.indexOf("//");
    if (i != -1)
    {
      line = line.substring(0, i).trim();
      if (line.equals(""))
      {
        return "";
      }
    }

    if (line.equals(""))
    {
      return "empty";
    }

    if (line.equals("{"))
    {
      return "{";
    }

    if (line.startsWith("}"))
    {
      return "}";
    }

    if (line.equals("else") || line.equals("default") || line.startsWith("case ") || line.startsWith("else if (")
        || line.startsWith("catch (") || line.equals("finally"))
    {
      return "stmt";
    }

    return "";
  }

  private static void complain(String name, int i)
  {
    List<Integer> list = complaints.get(name);
    if (list == null)
    {
      list = new ArrayList<Integer>();
      complaints.put(name, list);
    }

    list.add(i);
  }

  private static void ignore(String name, int count)
  {
    List<Integer> list = complaints.get(name);
    int found = list == null ? 0 : list.size();
    if (found == count)
    {
      complaints.remove(name);
    }
  }

  private static void report()
  {
    if (complaints.isEmpty())
    {
      System.out.println("No violations found.");
      return;
    }

    List<Entry<String, List<Integer>>> list = new ArrayList<Entry<String, List<Integer>>>(complaints.entrySet());
    Collections.sort(list, new Comparator<Entry<String, List<Integer>>>()
    {
      public int compare(Entry<String, List<Integer>> o1, Entry<String, List<Integer>> o2)
      {
        return o1.getKey().compareTo(o2.getKey());
      }
    });

    for (Entry<String, List<Integer>> entry : list)
    {
      String name = entry.getKey();
      for (int i : entry.getValue())
      {
        int dot = name.lastIndexOf('.');
        String file = dot == -1 ? name : name.substring(dot + 1);
        System.err.println(name + ".$(" + file + ".java:" + i + ")");
      }
    }

    try
    {
      Thread.sleep(50);
    }
    catch (InterruptedException ex)
    {
      return;
    }

    System.out.println();
    System.out.println();
    System.out.println();
    for (Entry<String, List<Integer>> entry : list)
    {
      System.out.println("ignore(\"" + entry.getKey() + "\", " + entry.getValue().size() + ");");
    }
  }
}
