/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers;

/**
 * Preparing the Workspace
 * <p>
 * For an application that uses CDO, install the CDO and Net4j SDKs and compatible EMF bundles into
 * the PDE target platform used to compile and launch that application. The target platform is the
 * set of plug-ins against which PDE resolves the application; it is separate from the projects
 * checked out and open in the workspace. Application plug-in manifests should declare the bundles
 * they use. Database drivers and server-side extensions are needed only when the application uses
 * those components, not for every CDO client.
 * <p>
 * The following prerequisites must be installed into the
 * <a href="https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Fconcepts%2Ftarget.htm">target platform</a>
 * of your workspace:
 * <ul>
 * <li>The CDO SDK and the Net4j SDK are both available from the <a href="https://download.eclipse.org/modeling/emf/cdo/updates/downloads.html">CDO Downloads</a> page.
 *      There you find "Update Site" and  "Floating Update Site" buttons, which link to p2 repositories that contain the respective SDKs.
 * <li>The EMF SDK is available from the <a href="https://download.eclipse.org/modeling/emf/emf/updates/">EMF Updates</a> page.
 *      The <a href="https://download.eclipse.org/modeling/emf/emf/builds">EMF builds</a> page contains the necessary libraries and tools for working with EMF models.
 * <li>Depending on the features used, additional bundles may be required. For example, a server
 * deployment using a particular database needs that database adapter and driver; an ordinary client
 * does not need every server database bundle.
 * </ul>
 * <p>
 * Installing the SDKs in a target platform does not automatically add dependencies to an
 * application's bundle manifest or make them available in a non-PDE runtime. Declare the bundles
 * the application uses and include the corresponding runtime platform when deploying it.
 * <p>
 * Note: If you are using an IDE other than Eclipse, you will need to manually download and include the required libraries in your project's build path.
 * At the bottom of the CDO Downloads page, you find a link to <a href="https://download.eclipse.org/modeling/emf/cdo/updates">All Promoted Builds</a>,
 * where you can download <code>emf-cdo-{qualifier}-Dropins.zip</code> archives containing all necessary libraries.
 * <p>
 * Use a compatible set of CDO, Net4j, EMF, and Eclipse versions. The CDO downloads page identifies
 * associated repositories and dependencies. Do not infer compatibility from the fact that
 * individual bundles install or compile in isolation.
 * <p>
 * In the following chapter, you find information about using the Oomph Setup Engine to automate the setup of your Eclipse workspace.
 * <p>
 * In {@link DocXX_InstallingSources}, you find information about checking out the CDO sources from Git and importing them into your workspace.
 *
 * @author Eike Stepper
 * @number 3
 */
public class Doc03_PreparingWorkspace
{
  /**
   * Using the Oomph Setup Engine
   * <p>
   * The Oomph Setup Engine applies provisioning tasks to an Eclipse installation and workspace, such
   * as installing bundles, configuring preferences, and importing projects. It is useful for
   * repeatable development setups; it is not required for an application to use CDO. On the
   * <a href="https://github.com/eclipse-oomph/oomph-website/blob/master/index.md">Oomph Homepage</a>, you find information about
   * how to use the Eclipse Installer and how to author setup models.
   * <p>
   * A prerequisite for using the Oomph Setup Engine is to have the Oomph plugins installed in your Eclipse IDE. In many cases, the
   * Oomph plugins are already included in the Eclipse IDE packages available from the <a href="https://www.eclipse.org/downloads/">Eclipse
   * Downloads</a> page. If you are using a custom Eclipse installation, you can install the Oomph plugins by adding the
   * Oomph update site (<code>http://download.eclipse.org/oomph/updates/latest/</code>) to your Eclipse installation and installing the
   * "Oomph Setup" feature.
   * <p>
   * Once you have installed the Oomph plugins, you can add setup tasks to the <code>workspace.setup</code> file or the <code>installation.setup</code>
   * file of your Eclipse IDE. You can open them via the <i>Navigate</i> > <i>Open Setup</i> sub menu of the main menu.
   * <p>
   * For application development, a target definition needs the CDO SDK and its dependencies. For
   * developing CDO itself, the repository setup also imports CDO source projects into the workspace
   * and configures the build environment; the SDK target alone does not provide those projects. The
   * CDO build produces a setup macro for its target platform. To use it, find the appropriate
   * build of CDO on the <a href="https://download.eclipse.org/modeling/emf/cdo/updates/downloads.html">CDO Downloads</a> page. Each build is shown as follows:
   * <p>
   * <img src="DownloadDrop.png" alt="CDO Build">
   * <p>
   * The link to the generated setup macro is the last one in the list of links below the build description. It points to a file named
   * <code>tp-macro.setup</code>. You can reference this file from your setup file as follows:
   * <pre>
   * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
   * &lt;setup:MacroTask
   *     xmi:version="2.0"
   *     xmlns:xmi="http://www.omg.org/XMI"
   *     xmlns:setup="http://www.eclipse.org/oomph/setup/1.0"
   *     id="CDO-TP"
   *     macro="https://download.eclipse.org/modeling/emf/cdo/updates/integration/latest/tp-macro.setup#/"/&gt;
   * </pre>
   * <p>
   * You can copy the XML snippet above and paste it into your setup file. The Setup editor will automatically convert the XML snippet into a
   * MacroExpansion, fetch the referenced setup macro and expand it inline as a preview.
   * <p>
   * This example is a floating integration target: it follows the latest integration build as that
   * repository changes. Choose a release macro for release development, or pin a specific build
   * macro when repeatable dependency resolution is more important than tracking updates. The macro
   * configures the target platform; it does not replace project checkout/import setup.
   * You can then save the setup file and perform it via the <i>Perform Setup Tasks</i> action in the <i>Help</i> menu.
   * <p>
   * CDO publishes floating setup macros for the supported release and integration channels. Use the channel that matches
   * the repository version you intend to develop against:
   * <ul>
   * <li><a href="https://download.eclipse.org/modeling/emf/cdo/updates/releases/latest/tp-macro.setup">Latest Release</a> (R)</li>
   * <li><a href="https://download.eclipse.org/modeling/emf/cdo/updates/integration/latest/tp-macro.setup">Latest Integration Build</a></li>
   * </ul>
   *
   */
  public class UsingOomph
  {
  }
}
