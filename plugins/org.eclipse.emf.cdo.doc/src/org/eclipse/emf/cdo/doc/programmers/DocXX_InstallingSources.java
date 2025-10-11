package org.eclipse.emf.cdo.doc.programmers;

/**
 * Installing the Sources
 * <p>
 * For developing applications on top of CDO it is normally sufficient to have the CDO SDK installed in your Eclipse
 * <a href="https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Fconcepts%2Ftarget.htm">target platform</a>,
 * which is described in section {@link Doc03_PreparingWorkspace}.
 * <p>
 * However, if you want to work on CDO itself or want to analyze the commit history of CDO, you need to have the CDO sources available in your workspace.
 * This chapter guides you through the trivial process of installing a development environment for the CDO sources.
 * <p>
 * The installation is fully automated and will be performed by the
 * <a href="https://github.com/eclipse-oomph/oomph-website/blob/master/Eclipse_Installer.md">Eclipse Installer</a>.
 * Here are the four steps you need to follow:
 * <ol>
 *  <li>Download and install a Java Development Kit (JDK) if you don't have one already. Version 21 or greater is required.
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
 * The installer will download and install the required Eclipse IDE packages, CDO, and all its dependencies.
 * This may take a while depending on your internet connection speed.
 * Once the installation is complete, the installer will launch the new IDE.
 * Each time the IDE is started, it will check for updates and perform them automatically. Please refer to the
 * <a href="https://github.com/eclipse-oomph/oomph-website/blob/master/index.md">Eclipse Installer documentation</a> for details.
 *
 * @author Eike Stepper
 * @number 1000
 */
public class DocXX_InstallingSources
{
}
