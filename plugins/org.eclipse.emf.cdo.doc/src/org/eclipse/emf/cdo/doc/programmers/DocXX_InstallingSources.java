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
 * Installing the Sources
 * <p>
 * For developing applications on top of CDO it is normally sufficient to have the CDO SDK installed in your Eclipse
 * <a href="https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Fconcepts%2Ftarget.htm">target platform</a>,
 * which is described in section {@link Doc03_PreparingWorkspace}.
 * <p>
 * However, if you want to work on CDO itself or want to analyze the commit history of CDO, you need to have the CDO sources available in your workspace.
 * This chapter installs the CDO development workspace, not just the SDK used by an application.
 * The setup provisions an Eclipse installation and target platform, checks out/imports the CDO
 * projects, and configures the workspace for developing those projects. It is not necessary when
 * an application only needs CDO bundles; see {@link Doc03_PreparingWorkspace} for that case.
 * <p>
 * The installation is fully automated and will be performed by the
 * <a href="https://github.com/eclipse-oomph/oomph-website/blob/master/Eclipse_Installer.md">Eclipse Installer</a>.
 * Here are the steps you need to follow:
 * <ol>
 *  <li>Download and install a Java Development Kit (JDK) if you don't have one already. The current
 *      CDO development setup requires Java 21 or later.
 *      You can get it from <a href="https://adoptium.net/">Adoptium</a> or any other JDK provider of your choice.</li>
 *  <li>Download and run the <a href="https://github.com/eclipse-oomph/oomph-website/blob/master/Eclipse_Installer.md">Eclipse Installer</a>.
 *      You can use an existing Eclipse Installer installation if you have one.</li>
 *  <li>Drag <a href="https://raw.githubusercontent.com/eclipse-cdo/cdo/master/releng/org.eclipse.emf.cdo.releng/CDOConfiguration.setup">this link</a>
 *      and drop it on the installer's title area. Alternatively, copy the location of the that link and apply it to the Eclipse Installer either via
 *      the menu in the upper right in simple mode or via the left-most toolbar button to the upper right in advanced mode.</li>
 *  <li>Review and/or edit the variable values that the installer presents.</li>
 *  <li>Click the Next/Finish buttons until the installation starts.</li>
 * </ol>
 * <p>
 * {@image EclipseInstaller.png}
 * <p>
 * {@image EclipseInstaller2.png}
 * <p>
 * The installer will provision the Eclipse IDE, the CDO source projects, the target platform, and
 * the dependencies declared by the development setup. It does not merely install the CDO SDK into
 * an existing application's target platform.
 * This may take a while depending on your internet connection speed.
 * Once the installation is complete, the installer will launch the new IDE.
 * The setup configures automatic update behavior for the provisioned installation. Please refer to the
 * <a href="https://github.com/eclipse-oomph/oomph-website/blob/master/index.md">Eclipse Installer documentation</a> for details.
 *
 * @author Eike Stepper
 * @number 1000
 */
public class DocXX_InstallingSources
{
}
