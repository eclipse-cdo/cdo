/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
  private static Map<String, List<Integer>> complaints = new HashMap<String, List<Integer>>();

  public static void main(String[] args) throws IOException
  {
    File folder = new File(args.length == 0 ? "/develop/ws/cdo" : args[0]);
    recurse(folder);

    ignore("base.impl.BaseFactoryImpl", 1);
    ignore("base.impl.BasePackageImpl", 2);
    ignore("base.util.BaseAdapterFactory", 2);
    ignore("base.util.BaseSwitch", 2);
    ignore("com.swtdesigner.ResourceManager", 48);
    ignore("com.swtdesigner.SWTResourceManager", 40);
    ignore("derived.impl.DerivedFactoryImpl", 1);
    ignore("derived.impl.DerivedPackageImpl", 2);
    ignore("derived.util.DerivedAdapterFactory", 2);
    ignore("derived.util.DerivedSwitch", 3);
    ignore("interface_.impl.InterfaceFactoryImpl", 1);
    ignore("interface_.impl.InterfacePackageImpl", 2);
    ignore("interface_.util.InterfaceAdapterFactory", 2);
    ignore("interface_.util.InterfaceSwitch", 2);
    ignore("org.eclipse.emf.cdo.defs.CDOTransactionDef", 1);
    ignore("org.eclipse.emf.cdo.defs.ResourceMode", 3);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOAuditDefImpl", 4);
    ignore("org.eclipse.emf.cdo.defs.impl.CDODefsFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOPackageRegistryDefImpl", 8);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOResourceDefImpl", 6);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOSessionDefImpl", 9);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOTransactionDefImpl", 1);
    ignore("org.eclipse.emf.cdo.defs.impl.CDOViewDefImpl", 6);
    ignore("org.eclipse.emf.cdo.defs.impl.EDynamicPackageDefImpl", 4);
    ignore("org.eclipse.emf.cdo.defs.impl.EPackageDefImpl", 4);
    ignore("org.eclipse.emf.cdo.defs.impl.FailOverStrategyDefImpl", 5);
    ignore("org.eclipse.emf.cdo.defs.impl.RetryFailOverStrategyDefImpl", 5);
    ignore("org.eclipse.emf.cdo.defs.util.CDODefsAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.defs.util.CDODefsSwitch", 1);
    ignore("org.eclipse.emf.cdo.defs.util.CDODefsUtil", 2);
    ignore("org.eclipse.emf.cdo.emodel.impl.EmodelFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.emodel.impl.EmodelPackageImpl", 1);
    ignore("org.eclipse.emf.cdo.emodel.provider.CDOAnnotationItemProvider", 3);
    ignore("org.eclipse.emf.cdo.emodel.provider.CDOModelElementItemProvider", 3);
    ignore("org.eclipse.emf.cdo.emodel.provider.CDONamedElementItemProvider", 2);
    ignore("org.eclipse.emf.cdo.emodel.util.EmodelAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.emodel.util.EmodelSwitch", 1);
    ignore("org.eclipse.emf.cdo.emodel.util.EmodelValidator", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.impl.model4interfacesFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.impl.model4interfacesPackageImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.util.model4interfacesAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.legacy.model4interfaces.util.model4interfacesSwitch", 15);
    ignore("org.eclipse.emf.cdo.tests.mango.ParameterPassing", 3);
    ignore("org.eclipse.emf.cdo.tests.mango.impl.MangoFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.mango.provider.ParameterItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.mango.provider.ValueItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.mango.provider.ValueListItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.mango.util.MangoAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.mango.util.MangoSwitch", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.VAT", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryCreateCommand", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProducts2CreateCommand", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProducts2ReorientCommand", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProductsCreateCommand", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CategoryProductsReorientCommand", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.CustomerCreateCommand", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.Model1ReorientConnectionViewCommand", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderAddressCreateCommand", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderDetailCreateCommand", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetails2CreateCommand", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetails2ReorientCommand", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetailsCreateCommand", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.OrderOrderDetailsReorientCommand", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.Product1CreateCommand", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.PurchaseOrderCreateCommand", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SalesOrderCreateCommand", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SalesOrderCustomerCreateCommand", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SalesOrderCustomerReorientCommand", 9);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.commands.SupplierCreateCommand", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.helpers.Model1BaseEditHelper", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryEditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryNameEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryProducts2EditPart", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CategoryProductsEditPart", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CompanyEditPart", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CustomerEditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.CustomerNameEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.Model1EditPartFactory", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderAddressEditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderAddressNameEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderDetailEditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderDetailPriceEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderOrderDetails2EditPart", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.OrderOrderDetailsEditPart", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.Product1EditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.Product1NameEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.PurchaseOrderDateEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.PurchaseOrderEditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SalesOrderCustomerEditPart", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SalesOrderEditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SalesOrderIdEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SupplierEditPart", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.parts.SupplierNameEditPart", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryCanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryItemSemanticEditPolicy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryProducts2ItemSemanticEditPolicy", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CategoryProductsItemSemanticEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CompanyCanonicalEditPolicy", 32);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CompanyItemSemanticEditPolicy", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CustomerCanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.CustomerItemSemanticEditPolicy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Model1BaseItemSemanticEditPolicy", 29);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Model1TextNonResizableEditPolicy", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Model1TextSelectionEditPolicy", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderAddressCanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderAddressItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderDetailCanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderDetailItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderOrderDetails2ItemSemanticEditPolicy", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.OrderOrderDetailsItemSemanticEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Product1CanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.Product1ItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.PurchaseOrderCanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.PurchaseOrderItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SalesOrderCanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SalesOrderCustomerItemSemanticEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SalesOrderItemSemanticEditPolicy", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SupplierCanonicalEditPolicy", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.edit.policies.SupplierItemSemanticEditPolicy", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1AbstractNavigatorItem", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1DomainNavigatorContentProvider", 14);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1DomainNavigatorItem", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1DomainNavigatorLabelProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorActionProvider", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorContentProvider", 44);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorGroup", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorItem", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorLabelProvider", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorLinkHelper", 10);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.navigator.Model1NavigatorSorter", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.parsers.AbstractParser", 14);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.parsers.CompositeParser", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.parsers.MessageFormatParser", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.DeleteElementAction", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.DiagramEditorContextMenuProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Messages", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1CreationWizard", 7);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1CreationWizardPage", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramActionBarContributor", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramEditor", 15);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramEditorPlugin", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramEditorUtil", 18);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramUpdateCommand", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DiagramUpdater", 25);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DocumentProvider", 56);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1DomainModelElementTester", 13);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1InitDiagramFileAction", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1LinkDescriptor", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1LoadResourceAction", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1MatchingStrategy", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1NewDiagramFileWizard", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1NodeDescriptor", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1PaletteFactory", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1UriEditorInputTester", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.Model1VisualIDRegistry", 39);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.part.ModelElementSelectionPage", 5);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramAppearancePreferencePage", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramConnectionsPreferencePage", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramGeneralPreferencePage", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramPreferenceInitializer", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramPrintingPreferencePage", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.preferences.DiagramRulersAndGridPreferencePage", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.ElementInitializers", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1EditPartProvider", 8);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ElementTypes", 12);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1IconProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ModelingAssistantProvider", 39);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ParserProvider", 16);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.providers.Model1ViewProvider", 25);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.sheet.Model1PropertySection", 12);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.sheet.Model1SheetLabelProvider", 6);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryNameViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryProducts2ViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryProductsViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CategoryViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CompanyViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CustomerNameViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.CustomerViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderAddressNameViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderAddressViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderDetailPriceViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderDetailViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderOrderDetails2ViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.OrderOrderDetailsViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.Product1NameViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.Product1ViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.PurchaseOrderDateViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.PurchaseOrderViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SalesOrderCustomerViewFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SalesOrderIdViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SalesOrderViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SupplierNameViewFactory", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.diagram.view.factories.SupplierViewFactory", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.Model1FactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.impl.OrderAddressImpl", 4);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.AddressItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.CategoryItemProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.CompanyItemProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.CustomerItemProvider", 1);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.OrderAddressItemProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.OrderDetailItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.OrderItemProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.Product1ItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.PurchaseOrderItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.SalesOrderItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.provider.SupplierItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.util.Model1AdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model1.util.Model1Switch", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.impl.Model2FactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model2.provider.SpecialPurchaseOrderItemProvider", 4);
    ignore("org.eclipse.emf.cdo.tests.model2.provider.TaskContainerItemProvider", 3);
    ignore("org.eclipse.emf.cdo.tests.model2.provider.TaskItemProvider", 2);
    ignore("org.eclipse.emf.cdo.tests.model2.util.Model2AdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model2.util.Model2Switch", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.impl.Model3FactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.impl.SubpackageFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.util.SubpackageAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.subpackage.util.SubpackageSwitch", 1);
    ignore("org.eclipse.emf.cdo.tests.model3.util.Model3AdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model3.util.Model3Switch", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.ImplContainedElementNPLImpl", 2);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.StringToEObjectImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.impl.model4FactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4.util.model4AdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model4.util.model4Switch", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.impl.model4interfacesFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.util.model4interfacesAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model4interfaces.util.model4interfacesSwitch", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.Doctor", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.Manager", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.impl.Model5FactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.tests.model5.util.Model5AdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.tests.model5.util.Model5Switch", 1);
    ignore("org.eclipse.emf.cdo.eresource.impl.EresourceFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl", 2);
    ignore("org.eclipse.emf.cdo.eresource.impl.EresourceFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.eresource.impl.EresourcePackageImpl", 2);
    ignore("org.eclipse.emf.cdo.eresource.provider.CDOResourceFolderItemProvider", 3);
    ignore("org.eclipse.emf.cdo.eresource.provider.CDOResourceItemProvider", 3);
    ignore("org.eclipse.emf.cdo.eresource.provider.CDOResourceNodeItemProvider", 2);
    ignore("org.eclipse.emf.cdo.eresource.provider.EresourceItemProviderAdapterFactory", 1);
    ignore("org.eclipse.emf.cdo.eresource.util.EresourceAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.eresource.util.EresourceSwitch", 6);
    ignore("org.eclipse.emf.cdo.internal.ui.editor.CDOActionBarContributor", 5);
    ignore("org.eclipse.emf.cdo.internal.ui.editor.CDOEditor", 11);
    ignore("org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl", 8);
    ignore("org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsFactoryImpl", 1);
    ignore("org.eclipse.emf.cdo.ui.defs.impl.EditorDefImpl", 4);
    ignore("org.eclipse.emf.cdo.ui.defs.util.CDOUIDefsAdapterFactory", 2);
    ignore("org.eclipse.emf.cdo.ui.defs.util.CDOUIDefsSwitch", 1);
    ignore("org.eclipse.net4j.defs.BufferPoolDef", 1);
    ignore("org.eclipse.net4j.defs.BufferProviderDef", 1);
    ignore("org.eclipse.net4j.defs.ClientProtocolFactoryDef", 1);
    ignore("org.eclipse.net4j.defs.ProtocolProviderDef", 1);
    ignore("org.eclipse.net4j.defs.ServerProtocolFactoryDef", 1);
    ignore("org.eclipse.net4j.defs.TCPSelectorDef", 1);
    ignore("org.eclipse.net4j.defs.impl.AcceptorDefImpl", 10);
    ignore("org.eclipse.net4j.defs.impl.ConnectorDefImpl", 10);
    ignore("org.eclipse.net4j.defs.impl.HTTPConnectorDefImpl", 4);
    ignore("org.eclipse.net4j.defs.impl.JVMAcceptorDefImpl", 4);
    ignore("org.eclipse.net4j.defs.impl.JVMConnectorDefImpl", 4);
    ignore("org.eclipse.net4j.defs.impl.Net4jDefsFactoryImpl", 1);
    ignore("org.eclipse.net4j.defs.impl.TCPAcceptorDefImpl", 5);
    ignore("org.eclipse.net4j.defs.impl.TCPConnectorDefImpl", 5);
    ignore("org.eclipse.net4j.defs.util.Net4jDefsAdapterFactory", 2);
    ignore("org.eclipse.net4j.defs.util.Net4jDefsSwitch", 1);
    ignore("org.eclipse.net4j.defs.util.Net4jDefsUtil", 1);
    ignore("org.eclipse.net4j.spi.db.DBSchema", 1);
    ignore("org.eclipse.net4j.ui.defs.impl.Net4JUIDefsFactoryImpl", 1);
    ignore("org.eclipse.net4j.ui.defs.util.Net4JUIDefsAdapterFactory", 2);
    ignore("org.eclipse.net4j.ui.defs.util.Net4JUIDefsSwitch", 1);
    ignore("org.eclipse.net4j.util.defs.DefException", 1);
    ignore("org.eclipse.net4j.util.defs.NegotiatorDef", 1);
    ignore("org.eclipse.net4j.util.defs.impl.ChallengeNegotiatorDefImpl", 7);
    ignore("org.eclipse.net4j.util.defs.impl.CredentialsProviderDefImpl", 4);
    ignore("org.eclipse.net4j.util.defs.impl.DefContainerImpl", 7);
    ignore("org.eclipse.net4j.util.defs.impl.DefImpl", 5);
    ignore("org.eclipse.net4j.util.defs.impl.Net4jUtilDefsFactoryImpl", 1);
    ignore("org.eclipse.net4j.util.defs.impl.PasswordCredentialsProviderDefImpl", 5);
    ignore("org.eclipse.net4j.util.defs.impl.RandomizerDefImpl", 7);
    ignore("org.eclipse.net4j.util.defs.impl.ResponseNegotiatorDefImpl", 5);
    ignore("org.eclipse.net4j.util.defs.impl.ThreadPoolDefImpl", 1);
    ignore("org.eclipse.net4j.util.defs.impl.UserImpl", 4);
    ignore("org.eclipse.net4j.util.defs.impl.UserManagerDefImpl", 5);
    ignore("org.eclipse.net4j.util.defs.util.Net4jUtilDefsAdapterFactory", 2);
    ignore("org.eclipse.net4j.util.defs.util.Net4jUtilDefsSwitch", 1);
    ignore("org.eclipse.net4j.util.io.ExtendedIOUtil", 1);
    ignore("org.eclipse.net4j.util.tests.defs.impl.DefsFactoryImpl", 1);
    ignore("org.eclipse.net4j.util.tests.defs.impl.DefsPackageImpl", 2);
    ignore("org.eclipse.net4j.util.tests.defs.impl.TestDefImpl", 5);
    ignore("org.eclipse.net4j.util.tests.defs.impl.TestDefsFactoryImpl", 1);
    ignore("org.eclipse.net4j.util.tests.defs.impl.TestDefsPackageImpl", 2);
    ignore("org.eclipse.net4j.util.tests.defs.util.DefsAdapterFactory", 2);
    ignore("org.eclipse.net4j.util.tests.defs.util.DefsSwitch", 3);
    ignore("org.eclipse.net4j.util.tests.defs.util.TestDefsAdapterFactory", 2);
    ignore("org.eclipse.net4j.util.tests.defs.util.TestDefsSwitch", 3);
    ignore("org.eclipse.net4j.util.ui.proposals.RegExContentProposalProvider", 10);
    ignore("org.eclipse.net4j.util.ui.proposals.RegExMessages", 2);
    ignore("org.gastro.business.OrderState", 3);
    ignore("org.gastro.business.impl.BusinessFactoryImpl", 1);
    ignore("org.gastro.business.provider.BusinessDayItemProvider", 3);
    ignore("org.gastro.business.provider.OrderDetailItemProvider", 2);
    ignore("org.gastro.business.provider.OrderItemProvider", 3);
    ignore("org.gastro.business.provider.WaiterItemProvider", 2);
    ignore("org.gastro.business.util.BusinessAdapterFactory", 2);
    ignore("org.gastro.business.util.BusinessSwitch", 1);
    ignore("org.gastro.inventory.impl.InventoryFactoryImpl", 1);
    ignore("org.gastro.inventory.provider.DepartmentItemProvider", 3);
    ignore("org.gastro.inventory.provider.EmployeeItemProvider", 2);
    ignore("org.gastro.inventory.provider.IngredientItemProvider", 2);
    ignore("org.gastro.inventory.provider.MenuCardItemProvider", 3);
    ignore("org.gastro.inventory.provider.OfferingItemProvider", 2);
    ignore("org.gastro.inventory.provider.ProductItemProvider", 2);
    ignore("org.gastro.inventory.provider.RecipeItemProvider", 3);
    ignore("org.gastro.inventory.provider.RestaurantItemProvider", 3);
    ignore("org.gastro.inventory.provider.SectionItemProvider", 3);
    ignore("org.gastro.inventory.provider.StationItemProvider", 2);
    ignore("org.gastro.inventory.provider.StockItemProvider", 3);
    ignore("org.gastro.inventory.provider.StockProductItemProvider", 2);
    ignore("org.gastro.inventory.provider.TableItemProvider", 2);
    ignore("org.gastro.inventory.util.InventoryAdapterFactory", 2);
    ignore("org.gastro.inventory.util.InventorySwitch", 1);
    ignore("reference.impl.ReferenceFactoryImpl", 1);
    ignore("reference.impl.ReferencePackageImpl", 2);
    ignore("reference.util.ReferenceAdapterFactory", 2);
    ignore("reference.util.ReferenceSwitch", 2);
    ignore("templates.MenuCardTemplate", 1);

    report();
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
    // System.out.println(file.getAbsolutePath());
    String name = file.getName();
    name = name.substring(0, name.length() - ".java".length());

    InputStream stream = new FileInputStream(file);
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    try
    {
      String packageName = null;
      String last = "";
      String line;
      int i = 0;
      while ((line = reader.readLine()) != null)
      {
        ++i;
        if (packageName == null)
        {
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
        // else if (last.equals("stmt"))
        // {
        // if (!type.equals("{"))
        // {
        // complain(name, i);
        // }
        // }
        else if (last.equals("empty"))
        {
          if (type.equals("empty"))
          {
            complain(name, i);
          }
        }

        last = type;
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

    for (Entry<String, List<Integer>> entry : list)
    {
      System.out.println("ignore(\"" + entry.getKey() + "\", " + entry.getValue().size() + ");");
    }
  }
}
