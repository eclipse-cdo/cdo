/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.product;

import org.eclipse.emf.cdo.releng.setup.DirectorCall;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.helper.Progress;
import org.eclipse.emf.cdo.releng.setup.helper.ProgressLog;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.internal.p2.director.app.DirectorApplication;
import org.eclipse.equinox.internal.p2.director.app.ILog;

/**
 * @author Eike Stepper
 */
public final class Director
{
  private DirectorCall directorCall;

  private String bundlePool;

  private Director(DirectorCall directorCall, String bundlePool)
  {
    this.directorCall = directorCall;
    this.bundlePool = bundlePool;
  }

  private Director(String bundlePool)
  {
    this(SetupFactory.eINSTANCE.createDirectorCall(), bundlePool);
  }

  private Director iu(String id)
  {
    InstallableUnit installableUnit = SetupFactory.eINSTANCE.createInstallableUnit();
    installableUnit.setId(id);
    directorCall.getInstallableUnits().add(installableUnit);
    return this;
  }

  public Director bundle(String id)
  {
    return iu(id);
  }

  public Director feature(String id)
  {
    return iu(id + ".feature.group");
  }

  public Director repository(String url)
  {
    P2Repository p2Repository = SetupFactory.eINSTANCE.createP2Repository();
    p2Repository.setUrl(url);
    directorCall.getP2Repositories().add(p2Repository);
    return this;
  }

  public void install(String destination)
  {
    String repositories = makeList(directorCall.getP2Repositories(), SetupPackage.Literals.P2_REPOSITORY__URL);
    String ius = makeList(directorCall.getInstallableUnits(), SetupPackage.Literals.INSTALLABLE_UNIT__ID);

    String os = Platform.getOS();
    String ws = Platform.getWS();
    String arch = Platform.getOSArch();

    String[] args = { "-destination", destination, "-repository", repositories, "-installIU", ius, "-profile",
        "SDKProfile", "-profileProperties", "org.eclipse.update.install.features=true", "-bundlepool", bundlePool,
        "-p2.os", os, "-p2.ws", ws, "-p2.arch", arch };

    DirectorApplication app = new DirectorApplication();
    app.setLog(new ILog()
    {
      public void log(String message)
      {
        ProgressLog log = Progress.log();
        if (log.isCancelled())
        {
          throw new OperationCanceledException();
        }

        log.addLine(message);
      }

      public void log(IStatus status)
      {
        log(status.getMessage());
      }

      public void close()
      {
      }
    });

    app.run(args);
  }

  private String makeList(EList<? extends EObject> objects, EAttribute attribute)
  {
    StringBuilder builder = new StringBuilder();
    for (EObject object : objects)
    {
      if (builder.length() > 0)
      {
        builder.append(',');
      }

      Object value = object.eGet(attribute);
      builder.append(value);
    }

    return builder.toString();
  }

  public static Director from(String bundlePool)
  {
    return new Director(bundlePool);
  }

  public static void install(String bundlePool, DirectorCall directorCall, String destination)
  {
    Director director = new Director(directorCall, bundlePool);
    director.install(destination);
  }
}
