/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.server.admin.CDORepositoryConfigurationManager;
import org.eclipse.emf.cdo.server.internal.admin.RepositoryConfigurationManagerExtension;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.XMLConfiguration;
import org.eclipse.net4j.util.XMLUtil;
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.Map;

/**
 * Regression coverage for the shared XML configuration preparation lifecycle.
 *
 * @author Eike Stepper
 */
public class XMLConfigurationTest extends PlainTest
{
  public void testReaderConditionsParametersAndSnapshot() throws Exception
  {
    String xml = "<root xmlns:if=\"urn:org.eclipse.net4j:xml:if\" xmlns:unless=\"urn:org.eclipse.net4j:xml:unless\">" + //$NON-NLS-1$
        "<one if:true=\"@yes\" value=\"@value\"/>" + //$NON-NLS-1$
        "<two unless:true=\"@no\"/>" + //$NON-NLS-1$
        "<three if:blank=\"   \"/>" + //$NON-NLS-1$
        "<four unless:blank=\"filled\"/>" + //$NON-NLS-1$
        "<five if:set=\"present\"/>" + //$NON-NLS-1$
        "<six unless:set=\"\"/>" + //$NON-NLS-1$
        "<excluded if:true=\"false\"><child value=\"@value\"/></excluded>" + //$NON-NLS-1$
        "</root>"; //$NON-NLS-1$
    XMLConfiguration configuration = new XMLConfiguration(null);
    configuration.setParameter("@yes", "true"); //$NON-NLS-1$
    configuration.setParameter("@no", "false"); //$NON-NLS-1$
    configuration.setParameter("@value", "expanded"); //$NON-NLS-1$
    configuration.setParameter("present", ""); //$NON-NLS-1$
    configuration.prepare(new StringReader(xml));

    Document first = configuration.getDocument();
    assertTrue(XMLConfiguration.isExpanded(first));
    assertTrue(XMLConfiguration.isExpanded(first.getDocumentElement()));
    Element firstElement = (Element)first.getDocumentElement().getFirstChild();
    assertTrue(XMLConfiguration.isExpanded(firstElement.getAttributeNode("value"))); //$NON-NLS-1$
    assertEquals(6, first.getDocumentElement().getChildNodes().getLength());
    assertEquals(0, first.getElementsByTagName("excluded").getLength()); //$NON-NLS-1$
    assertEquals(0, first.getElementsByTagName("child").getLength()); //$NON-NLS-1$
    assertEquals("expanded", firstElement.getAttribute("value")); //$NON-NLS-1$
    first.getDocumentElement().setAttribute("mutated", "yes"); //$NON-NLS-1$
    assertFalse(configuration.getDocument().getDocumentElement().hasAttribute("mutated")); //$NON-NLS-1$
    assertEquals("true", configuration.getParameters().get("@yes")); //$NON-NLS-1$
    try
    {
      configuration.getParameters().put("@bad", "value"); //$NON-NLS-1$ //$NON-NLS-2$
      fail("Parameter snapshots must be immutable"); //$NON-NLS-1$
    }
    catch (UnsupportedOperationException expected)
    {
    }

    try
    {
      configuration.setParameter("@yes", "false"); //$NON-NLS-1$
      fail("Prepared parameters must be frozen"); //$NON-NLS-1$
    }
    catch (IllegalStateException expected)
    {
    }
  }

  public void testTreeExtensionHonorsExpandedDOMAndLegacyDOM() throws Exception
  {
    XMLConfiguration configuration = new XMLConfiguration(null);
    configuration.setParameter("@value", "prepared"); //$NON-NLS-1$
    configuration.prepare(new StringReader("<extension value=\"@value\"/>")); //$NON-NLS-1$
    Element preparedElement = configuration.getDocument().getDocumentElement();

    Map<String, String> substitutions = Map.of("prepared", "expanded-again"); //$NON-NLS-1$ //$NON-NLS-2$
    CapturingTreeExtension extension = new CapturingTreeExtension();
    extension.configureRepository(null, preparedElement, substitutions, null);
    assertEquals("prepared", extension.value); //$NON-NLS-1$

    Element legacyElement = XMLUtil.createDocumentBuilder().parse(new InputSource(new StringReader("<extension value=\"prepared\"/>"))) //$NON-NLS-1$
        .getDocumentElement();
    extension.configureRepository(null, legacyElement, substitutions, null);
    assertEquals("expanded-again", extension.value); //$NON-NLS-1$

    Element legacyStore = XMLUtil.createDocumentBuilder().parse(new InputSource(new StringReader("<store><property name=\"url\" value=\"prepared\"/></store>"))) //$NON-NLS-1$
        .getDocumentElement();
    assertEquals("expanded-again", RepositoryConfigurator.getProperties(legacyStore, 1, substitutions, null).get("url")); //$NON-NLS-1$
  }

  public void testDBStorePropertiesDoNotExpandPreparedAttributesAgain()
  {
    String outerProperty = "cdo.tests.xmlConfiguration.outer"; //$NON-NLS-1$
    String innerProperty = "cdo.tests.xmlConfiguration.inner"; //$NON-NLS-1$
    String oldOuter = System.getProperty(outerProperty);
    String oldInner = System.getProperty(innerProperty);
    System.setProperty(outerProperty, "$$$system_property(" + innerProperty + ")$$$"); //$NON-NLS-1$
    System.setProperty(innerProperty, "expanded-twice"); //$NON-NLS-1$
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    try
    {
      XMLConfiguration configuration = new XMLConfiguration(container);
      configuration.prepare(new StringReader("<store><property name=\"url\" value=\"$$$system_property(" + outerProperty + ")$$$\"/></store>")); //$NON-NLS-1$
      Element store = configuration.getDocument().getDocumentElement();
      Map<String, String> properties = RepositoryConfigurator.getProperties(store, 1, configuration.getParameters(), container);
      assertEquals("$$$system_property(" + innerProperty + ")$$$", properties.get("url")); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      throw new AssertionError(ex);
    }
    finally
    {
      LifecycleUtil.deactivate(container);
      restoreProperty(outerProperty, oldOuter);
      restoreProperty(innerProperty, oldInner);
    }
  }

  public void testAdminRepositoryOverrideIsUsedInBothConfigurationModes() throws Exception
  {
    File file = File.createTempFile("xml-configuration-admin", ".xml"); //$NON-NLS-1$
    try (FileWriter writer = new FileWriter(file))
    {
      writer.write("<root><repository name=\"legacy\"/></root>"); //$NON-NLS-1$
    }

    TestAdminExtension extension = new TestAdminExtension();
    try
    {
      XMLConfiguration configuration = new XMLConfiguration(null).prepare(new StringReader("<root><repository name=\"prepared\"/></root>")); //$NON-NLS-1$
      extension.start(configuration);
      assertEquals(1, extension.invocations);
      assertTrue(extension.expanded);

      extension.start(file);
      assertEquals(2, extension.invocations);
      assertFalse(extension.expanded);
    }
    finally
    {
      file.delete();
    }
  }

  public void testNestedAndCombinedConditions() throws Exception
  {
    String xml = "<root xmlns:if=\"urn:org.eclipse.net4j:xml:if\" xmlns:unless=\"urn:org.eclipse.net4j:xml:unless\">" + //$NON-NLS-1$
        "<outer if:set=\"available\"><inner if:true=\"true\" unless:blank=\"value\"/></outer>" + //$NON-NLS-1$
        "<excluded if:set=\"available\" if:true=\"false\"><inner/></excluded>" + //$NON-NLS-1$
        "</root>"; //$NON-NLS-1$
    XMLConfiguration configuration = new XMLConfiguration(null);
    configuration.setParameter("available", ""); //$NON-NLS-1$
    configuration.prepare(new StringReader(xml));

    Document document = configuration.getDocument();
    assertEquals(1, document.getElementsByTagName("inner").getLength()); //$NON-NLS-1$
    assertEquals(1, document.getElementsByTagName("outer").getLength()); //$NON-NLS-1$
  }

  public void testMalformedConditionsAreRejected()
  {
    assertInvalid("<root xmlns:if=\"urn:org.eclipse.net4j:xml:if\"><item if:maybe=\"x\"/></root>"); //$NON-NLS-1$
    assertInvalid("<root xmlns:if=\"urn:org.eclipse.net4j:xml:if\"><item if:true=\"perhaps\"/></root>"); //$NON-NLS-1$
    assertInvalid("<root xmlns:if=\"urn:org.eclipse.net4j:xml:if\" if:true=\"false\"><item/></root>"); //$NON-NLS-1$
  }

  public void testExcludedDocumentRootReportsConfigurationError()
  {
    try
    {
      new XMLConfiguration(null).prepare(new StringReader("<root xmlns:if=\"urn:org.eclipse.net4j:xml:if\" if:true=\"false\"/>")); //$NON-NLS-1$
      fail("An excluded document root must be rejected"); //$NON-NLS-1$
    }
    catch (IllegalArgumentException ex)
    {
      assertTrue(ex.getMessage().contains("root element")); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      throw new AssertionError(ex);
    }
  }

  public void testSetConditionsCheckPropertyExistence() throws Exception
  {
    String systemProperty = "cdo.tests.xmlConfiguration.set"; //$NON-NLS-1$
    String converterProperty = "cdo.tests.xmlConfiguration.set.converter"; //$NON-NLS-1$
    String oldSystemProperty = System.getProperty(systemProperty);
    String oldConverterProperty = System.getProperty(converterProperty);
    System.setProperty(systemProperty, "false"); //$NON-NLS-1$
    System.setProperty(converterProperty, ""); //$NON-NLS-1$
    String xml = "<root xmlns:if=\"urn:org.eclipse.net4j:xml:if\" xmlns:unless=\"urn:org.eclipse.net4j:xml:unless\">" + //$NON-NLS-1$
        "<empty if:set=\"@empty\"/><false if:set=\"@false\"/><value if:set=\"@value\"/>" + //$NON-NLS-1$
        "<missing if:set=\"@missing\"/><missingUnless unless:set=\"@missing\"/>" + //$NON-NLS-1$
        "<falseUnless unless:set=\"@false\"/><system if:set=\"" + systemProperty + "\"/>" + //$NON-NLS-1$
        "<converted if:set=\"$$$system_property(" + converterProperty + ")$$$\"/></root>"; //$NON-NLS-1$
    XMLConfiguration configuration = new XMLConfiguration(null);
    configuration.setParameter("@empty", ""); //$NON-NLS-1$
    configuration.setParameter("@false", "false"); //$NON-NLS-1$
    configuration.setParameter("@value", "configured"); //$NON-NLS-1$

    try
    {
      configuration.prepare(new StringReader(xml));
      Document document = configuration.getDocument();
      assertEquals(1, document.getElementsByTagName("empty").getLength()); //$NON-NLS-1$
      assertEquals(1, document.getElementsByTagName("false").getLength()); //$NON-NLS-1$
      assertEquals(1, document.getElementsByTagName("value").getLength()); //$NON-NLS-1$
      assertEquals(0, document.getElementsByTagName("missing").getLength()); //$NON-NLS-1$
      assertEquals(1, document.getElementsByTagName("missingUnless").getLength()); //$NON-NLS-1$
      assertEquals(0, document.getElementsByTagName("falseUnless").getLength()); //$NON-NLS-1$
      assertEquals(1, document.getElementsByTagName("system").getLength()); //$NON-NLS-1$
      assertEquals(1, document.getElementsByTagName("converted").getLength()); //$NON-NLS-1$
    }
    finally
    {
      restoreProperty(systemProperty, oldSystemProperty);
      restoreProperty(converterProperty, oldConverterProperty);
    }
  }

  public void testFileSourceAndSecureParser()
  {
    File file = null;
    try
    {
      file = File.createTempFile("xml-configuration", ".xml"); //$NON-NLS-1$
      try (FileWriter writer = new FileWriter(file))
      {
        writer.write("<root/> "); //$NON-NLS-1$
      }

      XMLConfiguration configuration = new XMLConfiguration(null).prepare(file);
      assertEquals(file, configuration.getSourceFile());
      assertTrue(configuration.isPrepared());
    }
    catch (Exception ex)
    {
      throw new AssertionError(ex);
    }
    finally
    {
      if (file != null)
      {
        file.delete();
      }
    }

    try
    {
      new XMLConfiguration(null).prepare(new StringReader("<!DOCTYPE root [<!ENTITY x SYSTEM 'file:///etc/passwd'>]><root>&x;</root>")); //$NON-NLS-1$
      fail("External entity declarations must be rejected"); //$NON-NLS-1$
    }
    catch (Exception expected)
    {
    }
  }

  public void testStringConverterExpansion()
  {
    String property = "cdo.tests.xmlConfiguration.value"; //$NON-NLS-1$
    String oldValue = System.getProperty(property);
    System.setProperty(property, "converted"); //$NON-NLS-1$
    String nestedProperty = property + ".nested"; //$NON-NLS-1$
    String oldNestedValue = System.getProperty(nestedProperty);
    System.setProperty(nestedProperty, "$$$system_property(" + property + ")$$$"); //$NON-NLS-1$
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    try
    {
      XMLConfiguration configuration = new XMLConfiguration(container);
      configuration.prepare(new StringReader("<root value=\"$$$system_property(" + property + ")$$$\"/>")); //$NON-NLS-1$
      assertEquals("converted", configuration.getDocument().getDocumentElement().getAttribute("value")); //$NON-NLS-1$

      configuration = new XMLConfiguration(container);
      configuration.prepare(new StringReader("<root value=\"$$$system_property(" + nestedProperty + ")$$$\"/>")); //$NON-NLS-1$
      assertEquals("$$$system_property(" + property + ")$$$", configuration.getDocument().getDocumentElement().getAttribute("value")); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      throw new AssertionError(ex);
    }
    finally
    {
      LifecycleUtil.deactivate(container);
      if (oldValue == null)
      {
        System.clearProperty(property);
      }
      else
      {
        System.setProperty(property, oldValue);
      }

      if (oldNestedValue == null)
      {
        System.clearProperty(nestedProperty);
      }
      else
      {
        System.setProperty(nestedProperty, oldNestedValue);
      }
    }
  }

  private void assertInvalid(String xml)
  {
    try
    {
      new XMLConfiguration(null).prepare(new StringReader(xml));
      fail("Invalid conditions must fail preparation"); //$NON-NLS-1$
    }
    catch (IllegalArgumentException expected)
    {
    }
    catch (Exception ex)
    {
      throw new AssertionError(ex);
    }
  }

  private static void restoreProperty(String name, String value)
  {
    if (value == null)
    {
      System.clearProperty(name);
    }
    else
    {
      System.setProperty(name, value);
    }
  }

  /**
   * Captures the tree passed through the standard repository extension conversion path.
   *
   * @author Eike Stepper
   */
  private static final class CapturingTreeExtension extends RepositoryConfigurator.TreeExtension
  {
    private String value;

    public CapturingTreeExtension()
    {
    }

    @Override
    protected String configureRepository(InternalRepository repository, Tree config, Map<String, String> parameters, IManagedContainer container)
    {
      value = config.attribute("value"); //$NON-NLS-1$
      return null;
    }
  }

  /**
   * Records dispatch to the protected admin repository configuration hook.
   *
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  private static final class TestAdminExtension extends RepositoryConfigurationManagerExtension
  {
    private int invocations;

    private boolean expanded;

    public TestAdminExtension()
    {
    }

    @Override
    protected CDORepositoryConfigurationManager configureAdminRepository(IManagedContainer container, Element repositoryConfig)
    {
      ++invocations;
      expanded = XMLConfiguration.isExpanded(repositoryConfig);
      return null;
    }
  }
}
