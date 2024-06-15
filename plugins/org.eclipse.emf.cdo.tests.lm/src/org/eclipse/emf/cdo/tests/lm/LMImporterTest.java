/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.lm.internal.client.LMImporter;
import org.eclipse.emf.cdo.lm.internal.client.LMImporter.ImportModule;
import org.eclipse.emf.cdo.lm.internal.client.LMImporter.ImportResolution;
import org.eclipse.emf.cdo.tests.lm.bundle.OM;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
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
    ImportModule base = createTestModule(importer, resourceSet, "base");
    ImportModule ext1 = createTestModule(importer, resourceSet, "ext1", "base");
    ImportModule ext2 = createTestModule(importer, resourceSet, "ext2", "ext1", "base");

    // Resolve the models to import and derive dependency information.
    ImportResolution resolution = importer.resolve(createXMLResourceSet());
    assertThat(resolution.getModuleInfos().get(0).getModule(), is(base));
    assertThat(resolution.getModuleInfos().get(1).getModule(), is(ext1));
    assertThat(resolution.getModuleInfos().get(2).getModule(), is(ext2));

    // Perform the module imports.
    ISystemDescriptor systemDescriptor = createSystemRepository();
    List<Module> modules = resolution.importModules(systemDescriptor);
    assertThat(modules.get(0).getName(), is(base.getName()));
    assertThat(modules.get(1).getName(), is(ext1.getName()));
    assertThat(modules.get(2).getName(), is(ext2.getName()));

    // Create a "base" module checkout.
    Stream baseStream = modules.get(0).getStreams().get(0);
    IAssemblyDescriptor baseDescriptor = IAssemblyManager.INSTANCE.createDescriptor("base assembly descriptor", baseStream, monitor());
    CDOCheckout baseCheckout = baseDescriptor.getCheckout();
    CDOView baseView = baseCheckout.openView(true);

    // Verify that the custom copy paths are respected.
    CDOResource baseResource = baseView.getResource("moved/renamed.xml"); // Use the custom path.
    Category baseCategory = (Category)baseResource.getContents().get(0);
    assertThat(baseCategory.getName(), is("base products"));

    // Verify that text resources are imported and their contents are modified.
    CDOTextResource baseText = baseView.getTextResource("manifest.txt");
    CDOClob baseClob = baseText.getContents();
    String baseString = baseClob.getString();
    assertThat(baseString, startsWith("Manifest-Version: 1.0"));
    assertThat(baseString, containsString("CDO.server.net4j"));
  }

  private ImportModule createTestModule(LMImporter importer, ResourceSet resourceSet, String name, String... dependencies) throws IOException
  {
    File xmlDir = getTestFolder("xml");
    URI xmlURI = URI.createFileURI(xmlDir.getAbsolutePath());

    URI rootURI = xmlURI.appendSegment(name);
    Resource resource = resourceSet.createResource(rootURI.appendSegment("model.xml"));

    Category category = Model1Factory.eINSTANCE.createCategory();
    category.setName(name + " products");
    resource.getContents().add(category);

    for (int i = 0; i < 3; i++)
    {
      Product1 product = Model1Factory.eINSTANCE.createProduct1();
      product.setName(name + " product " + i);
      category.getProducts().add(product);
    }

    for (String dependency : dependencies)
    {
      Resource dependencyResource = resourceSet.getResource(xmlURI.appendSegment(dependency).appendSegment("model.xml"), true);
      Category dependencyCategory = (Category)dependencyResource.getContents().get(0);
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
