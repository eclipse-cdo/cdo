/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.common;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.gmf.ui.creators.GMFFragmentCreator;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.DawnAcoreDiagramEditor;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnTest;
import org.eclipse.emf.cdo.dawn.tests.DawnTestPlatform;

import org.eclipse.emf.mwe.core.WorkflowEngine;
import org.eclipse.emf.mwe.core.monitor.NullProgressMonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
public class DawnCodeGenGMFFragmentTest extends AbstractDawnTest
{
  public void testCodeGeneration() throws Exception
  {
    Map<String, ?> slotMap = new HashMap<String, Object>();

    Map<String, String> properties = new HashMap<String, String>();
    File dawnGenFile = DawnTestPlatform.instance.getTestResource("/model/acore.dawngenmodel");

    String ouputFolder = DawnTestPlatform.instance.getTestFolder();

    properties.put("model", dawnGenFile.toURI().toString());
    properties.put("src-gen", ouputFolder);

    String workflowPath = getWorkflowPath("workflow/gmfFragmentGenerator.oaw");// FileLocator.toFileURL(workFlowURL).getFile();
    System.out.println(workflowPath);

    new WorkflowEngine().run(workflowPath, new NullProgressMonitor(), properties, slotMap);

    String outputFolder = DawnTestPlatform.instance.getTestFolder();
    String diagramFolder = ouputFolder + "/" + "src/org/eclipse/emf/cdo/dawn/examples/acore/diagram";

    assertEquals(true, new File(outputFolder + "/META-INF/MANIFEST.MF").exists());
    assertEquals(true, new File(outputFolder + "/fragment.xml").exists());
    assertEquals(true, new File(outputFolder + "/build.properties").exists());

    assertEquals(true, new File(diagramFolder + "/edit/").exists());
    assertEquals(true, new File(diagramFolder + "/edit/parts/DawnAcoreEditPartFactory.java").exists());
    assertEquals(true, new File(diagramFolder + "/edit/parts/DawnACoreRootEditPart.java").exists());
    assertEquals(true, new File(diagramFolder + "/edit/policies/DawnACoreRootCanonicalEditPolicy.java").exists());

    assertEquals(true, new File(diagramFolder + "/part/").exists());
    assertEquals(true, new File(diagramFolder + "/part/DawnAcoreCreationWizard.java").exists());
    assertEquals(true, new File(diagramFolder + "/part/DawnAcoreDiagramEditor.java").exists());
    assertEquals(true, new File(diagramFolder + "/part/DawnAcoreDiagramEditorUtil.java").exists());
    assertEquals(true, new File(diagramFolder + "/part/DawnAcoreDocumentProvider.java").exists());

    assertEquals(true, new File(diagramFolder + "/providers/").exists());
    assertEquals(true, new File(diagramFolder + "/providers/DawnAcoreEditPartProvider.java").exists());
    assertEquals(true, new File(diagramFolder + "/providers/DawnAcoreEditPolicyProvider.java").exists());

    basicCompare(new File(new URI(DawnTestPlatform.instance.getBundlePathForClass(DawnAcoreDiagramEditor.class) + ".dawn/src/")),
        new File(outputFolder + "/src"));
  }

  /**
   * check whether the generated folder contains the same files as the reference editor
   *
   * @throws IOException
   */
  private void basicCompare(File reference, File expected) throws IOException
  {
    if (reference.getName().endsWith("CVS"))
    {
      return;
    }
    msg(reference + " <---> " + expected);
    assertEquals(true, reference.exists());
    assertEquals(true, expected.exists());
    if (reference.isFile())
    {
      // assertEquals(true, FileCompare.compare(reference, expected));
      if (!FileCompare.compare(reference, expected))
      {
        fail("not equal: " + reference.getName());
      }
    }
    else if (reference.isDirectory())
    {
      for (File child : reference.listFiles())
      {
        basicCompare(child, new File(expected + "/" + child.getName()));
      }
    }
  }

  private String getWorkflowPath(String subPath)
  {
    URL resource = GMFFragmentCreator.class.getResource("");

    String packagePath = GMFFragmentCreator.class.getPackage().getName().replace(".", "/");
    System.out.println(packagePath);
    String path = resource.toString().replace(packagePath, "");
    return path + subPath;
  }

  private static class FileCompare
  {
    public static boolean compare(File reference, File expected) throws IOException
    {
      String referenceContent = FileCompare.getContent(reference);
      String compareContent = FileCompare.getContent(expected);

      msg("REF.) " + referenceContent);
      msg("EXP.) " + compareContent);

      return referenceContent.equals(compareContent);
    }

    private static String getContent(File file) throws java.io.IOException
    {
      StringBuffer buffer = new StringBuffer();
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = "";

      while ((line = reader.readLine()) != null)
      {
        if (isValid(line.trim()))
        {
          buffer.append(replaceWhiteSpace(line));
        }
      }

      reader.close();
      return buffer.toString();
    }

    private static Object replaceWhiteSpace(String line)
    {
      return line.replace(" ", "").replace("\t", "");
    }

    private static boolean isValid(String line)
    {
      if (line.contains("AcoreDiagramEditorPlugin.getInstance().logInfo") || line.equals("") || line.startsWith("*")
          || line.equals("import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;") | line.startsWith("/*")
          || line.startsWith("//") || line.contains("@"))
      {
        return false;
      }

      return true;
    }
  }
}
