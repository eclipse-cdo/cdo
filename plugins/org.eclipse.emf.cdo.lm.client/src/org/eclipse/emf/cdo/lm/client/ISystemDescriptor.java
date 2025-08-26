/*
 * Copyright (c) 2022-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamSpec;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.util.LMMerger;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.properties.IPropertiesContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISystemDescriptor extends IPropertiesContainer, Comparable<ISystemDescriptor>
{
  public CDORepository getSystemRepository();

  public String getSystemName();

  public System getSystem();

  public String getError();

  public State getState();

  public boolean isOpen();

  public void open();

  public void close();

  public <E extends ModelElement, R> R modify(E element, Function<E, R> modifier, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  public <R> R modify(Function<System, R> modifier, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  /**
   * @since 1.2
   */
  public String[] getModuleNames();

  /**
   * @since 1.2
   */
  public CDORepository[] getModuleRepositories();

  public CDORepository getModuleRepository(String moduleName);

  public boolean withModuleSession(String moduleName, Consumer<CDOSession> consumer);

  /**
   * @since 1.2
   */
  public boolean withModuleSession(CDORepository moduleRepository, Consumer<CDOSession> consumer);

  public ModuleDefinition extractModuleDefinition(Baseline baseline);

  public ModuleDefinition extractModuleDefinition(FloatingBaseline baseline, long timeStamp);

  public ModuleDefinition extractModuleDefinition(CDOView view);

  /**
   * @since 1.4
   */
  public ResourceSet createModuleResourceSet(Baseline baseline) throws ResolutionException;

  /**
   * @since 1.5
   */
  public ResourceSet createModuleResourceSet(CDOSession moduleSession, CDOBranchPoint branchPoint) throws ResolutionException;

  /**
   * @since 1.5
   */
  public ResourceSet createModuleResourceSet(CDOSession moduleSession, CDOBranchPoint branchPoint, boolean readOnly) throws ResolutionException;

  /**
   * @since 1.3
   */
  public Map<String, CDOView> configureModuleResourceSet(CDOView view) throws ResolutionException;

  /**
   * @since 1.3
   */
  public Map<String, CDOView> configureModuleResourceSet(ResourceSet resourceSet, Assembly assembly);

  public Assembly resolve(ModuleDefinition rootDefinition, Baseline baseline, IProgressMonitor monitor) //
      throws ResolutionException;

  public Module createModule(String name, StreamSpec streamSpec, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  public Module createModule(String name, ModuleType type, StreamSpec streamSpec, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  public void deleteModule(Module module, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException, ModuleDeletionException;

  public Stream createStream(Module module, Drop base, StreamSpec streamSpec, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  public Drop createDrop(Stream stream, DropType dropType, long timeStamp, String label, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  public Change createChange(Stream stream, FixedBaseline base, String label, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  public void renameChange(Change change, String newLabel, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  public void deleteChange(Change change, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException, ChangeDeletionException;

  public Delivery createDelivery(Stream stream, Change change, LMMerger merger, IProgressMonitor monitor) //
      throws ConcurrentAccessException, CommitException;

  /**
   * @since 1.6
   */
  public Annotation attachFingerPrint(FixedBaseline fixedBaseline) //
      throws CommitException;

  /**
   * @since 1.6
   */
  public boolean verifyFingerPrint(FixedBaseline fixedBaseline);

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    Opening, Open, Closing, Closed
  }

  /**
   * @author Eike Stepper
   */
  public static final class ResolutionException extends Exception
  {
    private static final long serialVersionUID = 1L;

    private final Reason[] reasons;

    public ResolutionException(Reason[] reasons)
    {
      super(formatMessage(reasons));
      this.reasons = reasons;
    }

    public Reason[] getReasons()
    {
      return reasons;
    }

    private static String formatMessage(Reason[] reasons)
    {
      StringBuilder builder = new StringBuilder("The module definition could not be resolved");

      if (reasons != null && reasons.length != 0)
      {
        builder.append(':');

        for (Reason reason : reasons)
        {
          builder.append(StringUtil.NL);
          builder.append("   ");
          builder.append(reason);
        }
      }

      return builder.toString();
    }

    /**
     * @author Eike Stepper
     * @noextend This class is not intended to be subclassed by clients.
     */
    public static abstract class Reason
    {
      /**
       * @author Eike Stepper
       */
      public static final class Missing extends Reason
      {
        public final Module module;

        public final Dependency dependency;

        public Missing(Module module, Dependency dependency)
        {
          this.module = module;
          this.dependency = dependency;
        }

        public Module getModule()
        {
          return module;
        }

        public Dependency getDependency()
        {
          return dependency;
        }

        public boolean isEntryPoint()
        {
          return dependency == null;
        }

        @Override
        public String toString()
        {
          if (isEntryPoint())
          {
            return MessageFormat.format("You requested to install \"{0}\" but it could not be found", module);
          }

          return MessageFormat.format("Missing dependency: {0} requires \"{1}\" but it could not be found", module, dependency);
        }
      }

      /**
       * @author Eike Stepper
       */
      public static final class Conflicting extends Reason
      {
        public final Module[] modules;

        public Conflicting(Module[] modules)
        {
          this.modules = modules;
        }

        public Module[] getModules()
        {
          return modules;
        }

        @Override
        public String toString()
        {
          return MessageFormat.format("Only one of the following can be installed at once: {0}", Arrays.asList(modules));
        }
      }

      /**
       * @author Eike Stepper
       */
      public static final class Module
      {
        public final String name;

        public final Version version;

        public Module(String name, Version version)
        {
          this.name = name;
          this.version = version;
        }

        @Override
        public String toString()
        {
          return name + " " + version;
        }
      }

      /**
       * @author Eike Stepper
       */
      public static final class Dependency
      {
        public final String name;

        public final VersionRange versionRange;

        public Dependency(String name, VersionRange versionRange)
        {
          this.name = name;
          this.versionRange = versionRange;
        }

        @Override
        public String toString()
        {
          return name + " " + versionRange;
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ModuleDeletionException extends CDOException
  {
    private static final long serialVersionUID = 1L;

    public ModuleDeletionException(String deletedModuleName, List<CDOObjectReference> references)
    {
      super(createMessage(deletedModuleName, references));
    }

    private static String createMessage(String deletedModuleName, List<CDOObjectReference> references)
    {
      StringBuilder builder = new StringBuilder();

      for (CDOObjectReference reference : references)
      {
        CDOObject sourceObject = reference.getSourceObject();
        if (sourceObject instanceof Dependency)
        {
          Dependency dep = (Dependency)sourceObject;
          String dependencyName = dep.getModule().getName();

          StringUtil.appendSeparator(builder, ",\n");
          builder.append(dependencyName);
        }
      }

      return MessageFormat.format("The module '{0}' can not be deleted because the following modules depend on it:\n{1}", deletedModuleName, builder); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   * @since 1.1
   */
  public static final class ChangeDeletionException extends CDOException
  {
    private static final long serialVersionUID = 1L;

    public ChangeDeletionException(Change change)
    {
      super(createMessage(change));
    }

    private static String createMessage(Change change)
    {
      StringBuilder builder = new StringBuilder();

      for (Delivery delivery : change.getDeliveries())
      {
        StringUtil.appendSeparator(builder, ",\n");
        builder.append(delivery.getStream().getName());
      }

      return MessageFormat.format("The change '{0}' can not be deleted because deliveries to the following streams exist:\n{1}", change.getName(), builder); //$NON-NLS-1$
    }
  }
}
