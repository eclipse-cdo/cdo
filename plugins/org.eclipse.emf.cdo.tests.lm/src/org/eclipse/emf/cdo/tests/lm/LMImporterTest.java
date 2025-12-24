/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lm;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.LMImporter;
import org.eclipse.emf.cdo.lm.client.LMImporter.ImportModule;
import org.eclipse.emf.cdo.lm.client.LMImporter.ImportResolution;
import org.eclipse.emf.cdo.tests.lm.bundle.OM;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class LMImporterTest extends AbstractLMTest
{
  public void testModuleImport() throws Exception
  {
    LMImporter importer = new LMImporter();

    // Build on-disk test model and configure 3 modules to import.
    ResourceSet resourceSet = createXMLResourceSet();
    Customer externalCustomer = createExternalCustomer(resourceSet);
    ImportModule mod0 = createTestModule(importer, resourceSet, externalCustomer, "mod0");
    ImportModule mod1 = createTestModule(importer, resourceSet, externalCustomer, "mod1", "mod0");
    ImportModule mod2 = createTestModule(importer, resourceSet, externalCustomer, "mod2", "mod1", "mod0");

    // Resolve the models to import and derive dependency information.
    ImportResolution resolution = importer.resolve(createXMLResourceSet());
    assertThat(resolution.getModuleInfos().get(0).getModule(), is(mod0));
    assertThat(resolution.getModuleInfos().get(1).getModule(), is(mod1));
    assertThat(resolution.getModuleInfos().get(2).getModule(), is(mod2));

    // Perform the module imports.
    ISystemDescriptor systemDescriptor = createSystemRepository();
    List<Module> modules = resolution.importModules(systemDescriptor);
    assertThat(modules.get(0).getName(), is(mod0.getName()));
    assertThat(modules.get(1).getName(), is(mod1.getName()));
    assertThat(modules.get(2).getName(), is(mod2.getName()));

    // Create a "base" module checkout.
    Stream mod0Stream = modules.get(0).getStreams().get(0);
    IAssemblyDescriptor mod0Descriptor = IAssemblyManager.INSTANCE.createDescriptor("base assembly descriptor", mod0Stream, monitor());
    CDOCheckout mod0Checkout = mod0Descriptor.getCheckout();
    CDOView mod0View = mod0Checkout.openView(true);

    // Verify that the custom copy paths are respected.
    CDOResource mod0Resource = mod0View.getResource("moved/renamed.xml"); // Use the custom path.
    Company mod0Company = (Company)mod0Resource.getContents().get(0);
    Category mod0Category = mod0Company.getCategories().get(0);
    assertThat(mod0Category.getName(), is("mod0 products"));

    // Verify that text resources are imported and their contents are modified.
    CDOTextResource baseText = mod0View.getTextResource("manifest.txt");
    CDOClob baseClob = baseText.getContents();
    String baseString = baseClob.getString();
    assertThat(baseString, startsWith("Manifest-Version: 1.0"));
    assertThat(baseString, containsString("CDO.server.net4j"));

    // Verify that the external reference is preserved.
    SalesOrder salesOrder = mod0Company.getSalesOrders().get(0);
    Customer customer = salesOrder.getCustomer();
    URI customerURI = EcoreUtil.getURI(customer);
    assertThat(customerURI.scheme(), is("file"));
  }

  private Customer createExternalCustomer(ResourceSet resourceSet) throws IOException
  {
    File xmlDir = getTestFolder("xml");
    URI xmlURI = URI.createFileURI(xmlDir.getAbsolutePath());

    Resource resource = resourceSet.createResource(xmlURI.appendSegment("external.xml"));

    Customer externalCustomer = Model1Factory.eINSTANCE.createCustomer();
    externalCustomer.setName("external customer");
    resource.getContents().add(externalCustomer);

    resource.save(null);
    return externalCustomer;
  }

  private ImportModule createTestModule(LMImporter importer, ResourceSet resourceSet, Customer externalCustomer, String name, String... dependencies)
      throws IOException
  {
    File xmlDir = getTestFolder("xml");
    URI xmlURI = URI.createFileURI(xmlDir.getAbsolutePath());

    URI rootURI = xmlURI.appendSegment(name);
    Resource resource = resourceSet.createResource(rootURI.appendSegment("model.xml"));

    Company company = Model1Factory.eINSTANCE.createCompany();
    company.setName(name + " company");
    resource.getContents().add(company);

    Category category = Model1Factory.eINSTANCE.createCategory();
    category.setName(name + " products");
    company.getCategories().add(category);

    for (int i = 0; i < 3; i++)
    {
      Product1 product = Model1Factory.eINSTANCE.createProduct1();
      product.setName(name + " product " + i);
      category.getProducts().add(product);
    }

    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    salesOrder.setCustomer(externalCustomer);
    company.getSalesOrders().add(salesOrder);

    for (String dependency : dependencies)
    {
      Resource dependencyResource = resourceSet.getResource(xmlURI.appendSegment(dependency).appendSegment("model.xml"), true);
      Company dependencyCompany = (Company)dependencyResource.getContents().get(0);
      Category dependencyCategory = dependencyCompany.getCategories().get(0);
      category.getTopProducts().addAll(dependencyCategory.getProducts());
    }

    resource.save(null);

    try (InputStream in = OM.BUNDLE.getInputStream("META-INF/MANIFEST.MF");
        FileOutputStream out = IOUtil.openOutputStream(rootURI.appendSegment("MANIFEST.MF").toFileString()))
    {
      IOUtil.copy(in, out);
    }

    ImportModule module = importer.addModule(name, rootURI);
    module.addResource("model.xml").setCopyPath("moved/renamed.xml");
    module.addText("MANIFEST.MF", "UTF-8").setCopyPath("manifest.txt") //
        .setStringContentsModifier(str -> str.replaceAll("org\\.eclipse\\.emf\\.cdo", "CDO"));

    return module;
  }

  private static ResourceSet createXMLResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl());
    return resourceSet;
  }
}
