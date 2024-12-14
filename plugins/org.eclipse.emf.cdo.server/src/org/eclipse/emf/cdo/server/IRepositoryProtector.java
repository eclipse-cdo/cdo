/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserInfo;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.security.AdministrationPredicate;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Protects a {@link #getRepository() repository} by authenticating users and, optionally, authorizing read and write operations.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 4.20
 */
public interface IRepositoryProtector extends IContainer<UserInfo>, IManagedContainerProvider
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositoryProtectors"; //$NON-NLS-1$

  public static final String DEFAULT_TYPE = "default"; //$NON-NLS-1$

  public boolean isFirstTime();

  public IRepository getRepository();

  public void setRepository(IRepository repository);

  public IRepository[] getSecondaryRepositories();

  public void addSecondaryRepository(IRepository repository);

  public void removeSecondaryRepository(IRepository repository);

  public UserAuthenticator getUserAuthenticator();

  public void setUserAuthenticator(UserAuthenticator userAuthenticator);

  public AuthorizationStrategy getAuthorizationStrategy();

  public void setAuthorizationStrategy(AuthorizationStrategy authorizationStrategy);

  public RevisionAuthorizer[] getRevisionAuthorizers();

  public void addRevisionAuthorizer(RevisionAuthorizer authorizer);

  public void removeRevisionAuthorizer(RevisionAuthorizer authorizer);

  public CommitHandler[] getCommitHandlers();

  public void addCommitHandler(CommitHandler handler);

  public void removeCommitHandler(CommitHandler handler);

  public UserInfo getUserInfo(String userID);

  /**
   * @author Eike Stepper
   */
  public static class UserInfo implements Comparable<UserInfo>
  {
    private final String userID;

    public UserInfo(String userID)
    {
      this.userID = Objects.requireNonNull(userID);
    }

    public final String userID()
    {
      return userID;
    }

    @Override
    public final int hashCode()
    {
      return Objects.hash(userID);
    }

    @Override
    public final boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (!(obj instanceof UserInfo))
      {
        return false;
      }

      UserInfo other = (UserInfo)obj;
      return Objects.equals(userID, other.userID);
    }

    public final boolean equalsStructurally(Object obj)
    {
      return equals(obj) && isStructurallyEqual((UserInfo)obj);
    }

    @Override
    public final int compareTo(UserInfo o)
    {
      return StringUtil.compare(userID, o.userID);
    }

    @Override
    public String toString()
    {
      return "UserInfo[" + userID + "]";
    }

    protected boolean isStructurallyEqual(UserInfo userInfo)
    {
      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UserInfoChangedEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private final UserInfo oldUserInfo;

    private final UserInfo newUserInfo;

    public UserInfoChangedEvent(IRepositoryProtector notifier, UserInfo oldUserInfo, UserInfo newUserInfo)
    {
      super(notifier);
      this.oldUserInfo = oldUserInfo;
      this.newUserInfo = newUserInfo;
    }

    @Override
    public IRepositoryProtector getSource()
    {
      return (IRepositoryProtector)super.getSource();
    }

    public UserInfo getOldUserInfo()
    {
      return oldUserInfo;
    }

    public UserInfo getNewUserInfo()
    {
      return newUserInfo;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Element extends Lifecycle implements IManagedContainerProvider
  {
    private IRepositoryProtector repositoryProtector;

    private Element()
    {
    }

    @Override
    public final IManagedContainer getContainer()
    {
      return repositoryProtector == null ? null : repositoryProtector.getContainer();
    }

    public final IRepositoryProtector getRepositoryProtector()
    {
      return repositoryProtector;
    }

    public final void setRepositoryProtector(IRepositoryProtector repositoryProtector)
    {
      checkInactive();
      this.repositoryProtector = repositoryProtector;
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      if (checkRepositoryProtector())
      {
        checkState(repositoryProtector, "repositoryProtector"); //$NON-NLS-1$
      }
    }

    protected boolean checkRepositoryProtector()
    {
      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class UserAuthenticator extends Element implements AdministrationPredicate
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositoryProtectorUserAuthenticators"; //$NON-NLS-1$

    public UserAuthenticator()
    {
    }

    public Class<? extends UserInfo> getUserInfoClass()
    {
      return UserInfo.class;
    }

    public abstract UserInfo authenticateUser(String userID, char[] password);

    @Override
    public boolean isAdministrator(String userID)
    {
      return false;
    }

    /**
     * @author Eike Stepper
     */
    public interface PasswordChangeSupport
    {
      public void updatePassword(String userID, char[] oldPassword, char[] newPassword) throws SecurityException;

      public void resetPassword(String adminID, char[] adminPassword, String userID, char[] newPassword) throws SecurityException;

      public void resetPassword(String userID, char[] newPassword) throws SecurityException;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class AuthorizationStrategy extends Element implements BiPredicate<CDOPermission, CDOPermission>
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositoryProtectorAuthorizationStrategies"; //$NON-NLS-1$

    public static final String DEFAULT_TYPE = "additive"; //$NON-NLS-1$

    /**
     * The {@link Additive additive} default authorization strategy.
     */
    public static final AuthorizationStrategy DEFAULT = new Additive();

    private CDOPermission initialPermission;

    private CDOPermission terminalPermission;

    private boolean authorizeAttach = true;

    private boolean authorizeModify = true;

    private boolean authorizeDetach = true;

    public AuthorizationStrategy()
    {
    }

    public final CDOPermission getInitialPermission()
    {
      return initialPermission;
    }

    @InjectAttribute(name = "initialPermission")
    public final void setInitialPermission(CDOPermission initialPermission)
    {
      checkInactive();
      this.initialPermission = initialPermission;
    }

    public final CDOPermission getTerminalPermission()
    {
      return terminalPermission;
    }

    @InjectAttribute(name = "terminalPermission")
    public final void setTerminalPermission(CDOPermission terminalPermission)
    {
      checkInactive();
      this.terminalPermission = terminalPermission;
    }

    public final boolean isAuthorizeAttach()
    {
      return authorizeAttach;
    }

    @InjectAttribute(name = "authorizeAttach")
    public final void setAuthorizeAttach(boolean authorizeAttach)
    {
      checkInactive();
      this.authorizeAttach = authorizeAttach;
    }

    public final boolean isAuthorizeModify()
    {
      return authorizeModify;
    }

    @InjectAttribute(name = "authorizeModify")
    public final void setAuthorizeModify(boolean authorizeModify)
    {
      checkInactive();
      this.authorizeModify = authorizeModify;
    }

    public final boolean isAuthorizeDetach()
    {
      return authorizeDetach;
    }

    @InjectAttribute(name = "authorizeDetach")
    public final void setAuthorizeDetach(boolean authorizeDetach)
    {
      checkInactive();
      this.authorizeDetach = authorizeDetach;
    }

    public final CDOPermission getCombinedPermission(CDOPermission currentPermission, CDOPermission newPermission)
    {
      return test(currentPermission, newPermission) ? newPermission : currentPermission;
    }

    /**
     * Returns <code>true</code> if the <code>newPermission</code> is supposed to replace the
     * <code>currentPermission</code>, <code>false</code> otherwise.
     */
    @Override
    public abstract boolean test(CDOPermission currentPermission, CDOPermission newPermission);

    @Override
    protected void doBeforeActivate() throws Exception
    {
      checkState(initialPermission, "initialPermission"); //$NON-NLS-1$
      checkState(terminalPermission, "terminalPermission"); //$NON-NLS-1$
    }

    /**
     * @author Eike Stepper
     * @since 4.23
     */
    public static class Constant extends AuthorizationStrategy
    {
      public Constant(CDOPermission permission)
      {
        setInitialPermission(permission);
        setTerminalPermission(permission);
      }

      @Override
      public boolean test(CDOPermission currentPermission, CDOPermission newPermission)
      {
        return false;
      }

      /**
       * @author Eike Stepper
       */
      public static class None extends Constant
      {
        public None()
        {
          super(CDOPermission.NONE);
        }
      }

      /**
       * @author Eike Stepper
       */
      public static class Read extends Constant
      {
        public Read()
        {
          super(CDOPermission.READ);
        }
      }

      /**
       * @author Eike Stepper
       */
      public static class Write extends Constant
      {
        public Write()
        {
          super(CDOPermission.WRITE);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Additive extends AuthorizationStrategy
    {
      public static final CDOPermission DEFAULT_INITIAL_PERMISSION = CDOPermission.NONE;

      public static final CDOPermission DEFAULT_TERMINAL_PERMISSION = CDOPermission.WRITE;

      public Additive()
      {
        setInitialPermission(DEFAULT_INITIAL_PERMISSION);
        setTerminalPermission(DEFAULT_TERMINAL_PERMISSION);
      }

      @Override
      public boolean test(CDOPermission currentPermission, CDOPermission newPermission)
      {
        return currentPermission.compareTo(newPermission) < 0;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Subtractive extends AuthorizationStrategy
    {
      public static final CDOPermission DEFAULT_INITIAL_PERMISSION = CDOPermission.WRITE;

      public static final CDOPermission DEFAULT_TERMINAL_PERMISSION = CDOPermission.NONE;

      public Subtractive()
      {
        setInitialPermission(DEFAULT_INITIAL_PERMISSION);
        setTerminalPermission(DEFAULT_TERMINAL_PERMISSION);
      }

      @Override
      public boolean test(CDOPermission currentPermission, CDOPermission newPermission)
      {
        return currentPermission.compareTo(newPermission) > 0;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class RevisionAuthorizer extends Element
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositoryProtectorRevisionAuthorizers"; //$NON-NLS-1$

    private Operation operation = Operation.COMBINE;

    private boolean disabled;

    public RevisionAuthorizer()
    {
    }

    public final Operation getOperation()
    {
      return operation;
    }

    @InjectAttribute(name = "operation")
    public final void setOperation(Operation operation)
    {
      checkInactive();
      this.operation = operation;
    }

    public final boolean isDisabled()
    {
      return disabled;
    }

    @InjectAttribute(name = "disabled")
    public final void setDisabled(boolean disabled)
    {
      checkInactive();
      this.disabled = disabled;
    }

    /**
     * @param securityContext Can be <code>null</code>, for example if authorization is caused by
     *          {@link CDORevisionManager#getRevisionByVersion(CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchVersion, int, boolean) getRevisionByVersion()}.
     * @param revisionProvider Can be <code>null</code>, for example if authorization is caused by
     *          {@link CDORevisionManager#getRevisionByVersion(CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchVersion, int, boolean) getRevisionByVersion()}.
     */
    public abstract CDOPermission authorizeRevision(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
        CDORevision revision);

    @Override
    protected void doBeforeActivate() throws Exception
    {
      super.doBeforeActivate();
      checkState(operation, "operation"); //$NON-NLS-1$
    }

    /**
     * @author Eike Stepper
     */
    public enum Operation
    {
      COMBINE, OVERRIDE, VETO;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class CommitHandler extends Element
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositoryProtectorCommitHandlers"; //$NON-NLS-1$

    public CommitHandler()
    {
    }

    /**
     * Called <b>before</b> the commit is security checked and passed to the repository.
     */
    public abstract void beforeCommit(CommitContext commitContext, UserInfo userInfo);

    /**
     * Called <b>after</b> the commit has succeeded.
     */
    public abstract void afterCommit(CommitContext commitContext, UserInfo userInfo);
  }
}
