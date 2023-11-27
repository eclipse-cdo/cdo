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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.internal.server.LDAPUserAuthenticator.LDAPDN;
import org.eclipse.emf.cdo.internal.server.LDAPUserAuthenticator.LDAPEntry;
import org.eclipse.emf.cdo.internal.server.LDAPUserAuthenticator.LDAPUserInfo;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.IRepositoryProtector.RevisionAuthorizer;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserInfo;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.StringTester;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.20
 */
public class DefaultRevisionAuthorizer extends RevisionAuthorizer
{
  private CDOPermission permission;

  private Matcher matcher;

  public DefaultRevisionAuthorizer()
  {
  }

  public final CDOPermission getPermission()
  {
    return permission;
  }

  @InjectAttribute(name = "permission")
  public final void setPermission(CDOPermission permission)
  {
    checkInactive();
    this.permission = permission;
  }

  public final Matcher getMatcher()
  {
    return matcher;
  }

  @InjectElement(productGroup = Matcher.PRODUCT_GROUP)
  public final void setMatcher(Matcher matcher)
  {
    checkInactive();
    this.matcher = matcher;
  }

  @Override
  public CDOPermission authorizeRevision(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
      CDORevision revision)
  {
    if (matcher.matches(session, userInfo, securityContext, revisionProvider, revision))
    {
      return permission;
    }

    return null;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(permission, "permission"); //$NON-NLS-1$
    checkState(matcher, "matcher"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    matcher.setRevisionAuthorizer(this);
    matcher.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    matcher.deactivate();
    matcher.setRevisionAuthorizer(null);
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Matcher extends Lifecycle
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.revisionAuthorizerMatchers"; //$NON-NLS-1$

    private DefaultRevisionAuthorizer revisionAuthorizer;

    private String id;

    public Matcher()
    {
    }

    public final DefaultRevisionAuthorizer getRevisionAuthorizer()
    {
      return revisionAuthorizer;
    }

    public final void setRevisionAuthorizer(DefaultRevisionAuthorizer revisionAuthorizer)
    {
      checkInactive();
      this.revisionAuthorizer = revisionAuthorizer;
    }

    public final String getID()
    {
      return id;
    }

    @InjectAttribute(name = "id")
    public final void setID(String id)
    {
      checkInactive();
      this.id = id;
    }

    public Matcher findMatcher(String id)
    {
      if (id != null && id.equals(this.id))
      {
        return this;
      }

      return null;
    }

    public abstract boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
        CDORevision revision);

    @Override
    public String toString()
    {
      String str = getClass().getSimpleName();

      if (id != null)
      {
        str += "[" + id + "]";
      }

      return str;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class RefMatcher extends Matcher
  {
    private String ref;

    private Matcher target;

    public RefMatcher()
    {
    }

    public final String getRef()
    {
      return ref;
    }

    @InjectAttribute(name = "ref")
    public final void setRef(String ref)
    {
      checkInactive();
      this.ref = ref;
    }

    @Override
    public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
    {
      return target.matches(session, userInfo, securityContext, revisionProvider, revision);
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      checkState(ref, "ref"); //$NON-NLS-1$
    }

    @Override
    protected void doActivate() throws Exception
    {
      IRepositoryProtector protector = getRevisionAuthorizer().getRepositoryProtector();
      for (RevisionAuthorizer authorizer : protector.getRevisionAuthorizers())
      {
        if (authorizer instanceof DefaultRevisionAuthorizer)
        {
          Matcher matcher = ((DefaultRevisionAuthorizer)authorizer).getMatcher().findMatcher(ref);
          if (matcher != null)
          {
            target = matcher;
            break;
          }
        }
      }

      if (target == null)
      {
        throw new IllegalStateException("Matcher '" + ref + "' not found");
      }
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      target = null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class OperationMatcher extends Matcher
  {
    private final ConcurrentArray<Matcher> arguments = new ConcurrentArray<Matcher>()
    {
      @Override
      protected Matcher[] newArray(int length)
      {
        return new Matcher[length];
      }
    };

    public OperationMatcher()
    {
    }

    public final Matcher[] getArguments()
    {
      return arguments.get();
    }

    @InjectElement(productGroup = Matcher.PRODUCT_GROUP)
    public final void addArgument(Matcher argument)
    {
      checkInactive();
      arguments.add(argument);
    }

    public final void removeArgument(Matcher argument)
    {
      checkInactive();
      arguments.remove(argument);
    }

    @Override
    public Matcher findMatcher(String matcherID)
    {
      Matcher matcher = super.findMatcher(matcherID);
      if (matcher != null)
      {
        return matcher;
      }

      for (Matcher argument : getArguments())
      {
        matcher = argument.findMatcher(matcherID);
        if (matcher != null)
        {
          return matcher;
        }
      }

      return null;
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      checkState(arguments.get().length > 0, "arguments"); //$NON-NLS-1$
    }

    @Override
    protected void doActivate() throws Exception
    {
      DefaultRevisionAuthorizer revisionAuthorizer = getRevisionAuthorizer();
      for (Matcher argument : getArguments())
      {
        argument.setRevisionAuthorizer(revisionAuthorizer);
        argument.activate();
      }
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      for (Matcher argument : getArguments())
      {
        argument.deactivate();
        argument.setRevisionAuthorizer(null);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Not extends OperationMatcher
    {
      public Not()
      {
      }

      @Override
      public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
      {
        for (Matcher argument : getArguments())
        {
          if (!argument.matches(session, userInfo, securityContext, revisionProvider, revision))
          {
            return false;
          }
        }

        return true;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class And extends OperationMatcher
    {
      public And()
      {
      }

      @Override
      public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
      {
        for (Matcher argument : getArguments())
        {
          if (!argument.matches(session, userInfo, securityContext, revisionProvider, revision))
          {
            return false;
          }
        }

        return true;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Or extends OperationMatcher
    {
      public Or()
      {
      }

      @Override
      public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
      {
        for (Matcher argument : getArguments())
        {
          if (argument.matches(session, userInfo, securityContext, revisionProvider, revision))
          {
            return true;
          }
        }

        return false;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Xor extends OperationMatcher
    {
      public Xor()
      {
      }

      @Override
      public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
      {
        int count = 0;

        for (Matcher argument : getArguments())
        {
          if (argument.matches(session, userInfo, securityContext, revisionProvider, revision))
          {
            if (++count > 1)
            {
              return false;
            }
          }
        }

        return count == 1;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class ValueMatcher extends Matcher
  {
    private StringTester test;

    private String value;

    private boolean recursive;

    public ValueMatcher()
    {
    }

    public final StringTester getTest()
    {
      return test;
    }

    @InjectAttribute(name = "test", productGroup = StringTester.PRODUCT_GROUP)
    public final void setTest(StringTester test)
    {
      checkInactive();
      this.test = test;
    }

    public final String getValue()
    {
      return value;
    }

    @InjectAttribute(name = "value")
    public final void setValue(String value)
    {
      checkInactive();
      this.value = value;
    }

    public final boolean isRecursive()
    {
      return recursive;
    }

    public final void setRecursive(boolean recursive)
    {
      checkInactive();
      this.recursive = recursive;
    }

    @Override
    public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
    {
      Object extractedValue;

      try
      {
        extractedValue = extractValue(session, userInfo, securityContext, revisionProvider, revision);
      }
      catch (Exception ex)
      {
        return false;
      }

      if (extractedValue == null)
      {
        return false;
      }

      StringTester test = getTest();
      String value = getValue();

      return matchesValue(extractedValue, test, value);
    }

    protected boolean matchesValue(Object extractedValue, StringTester test, String value)
    {
      if (extractedValue instanceof Iterable<?>)
      {
        for (Object element : (Iterable<?>)extractedValue)
        {
          if (element != null && test.test(convertValue(element), value))
          {
            return true;
          }
        }

        return matchesRecursively(extractedValue, test, value);
      }

      if (test.test(convertValue(extractedValue), value))
      {
        return true;
      }

      return matchesRecursively(extractedValue, test, value);
    }

    protected boolean matchesRecursively(Object object, StringTester test, String value)
    {
      if (!recursive)
      {
        return false;
      }

      Object successor = computeSuccessor(object);
      if (successor == null)
      {
        return false;
      }

      if (successor instanceof Iterable<?>)
      {
        for (Object element : (Iterable<?>)successor)
        {
          if (element != null && matchesValue(element, test, value))
          {
            return true;
          }
        }

        return false;
      }

      return matchesValue(successor, test, value);
    }

    protected Object computeSuccessor(Object object)
    {
      return null;
    }

    protected String convertValue(Object object)
    {
      return object.toString();
    }

    protected abstract Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
        CDORevision revision) throws Exception;

    @Override
    protected void doBeforeActivate() throws Exception
    {
      if (test == null)
      {
        test = StringTester.EQ;
      }

      checkState(value, "value"); //$NON-NLS-1$
    }

    /**
     * @author Eike Stepper
     */
    public static class RepositoryUUID extends ValueMatcher
    {
      public RepositoryUUID()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return session.getRepository().getUUID();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RepositoryName extends ValueMatcher
    {
      public RepositoryName()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return session.getRepository().getName();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RepositoryPrimary extends ValueMatcher
    {
      public RepositoryPrimary()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return session.getRepository() == getRevisionAuthorizer().getRepositoryProtector().getRepository();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class UserID extends ValueMatcher
    {
      public UserID()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return userInfo.userID();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class UserAdministrator extends ValueMatcher
    {
      public UserAdministrator()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        String userID = userInfo.userID();
        UserAuthenticator authenticator = getRevisionAuthorizer().getRepositoryProtector().getUserAuthenticator();
        return authenticator.isAdministrator(userID);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class ContextBranchID extends ValueMatcher
    {
      public ContextBranchID()
      {
      }

      @InjectAttribute(name = "subBranches")
      public final void setSubBranches(boolean subBranches)
      {
        setRecursive(subBranches);
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return securityContext.getBranch();
      }

      @Override
      protected String convertValue(Object object)
      {
        return Integer.toString(((CDOBranch)object).getID());
      }

      @Override
      protected Object computeSuccessor(Object object)
      {
        CDOBranch branch = (CDOBranch)object;
        return branch.isMainBranch() ? null : branch.getBranch();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class ContextBranchName extends ContextBranchID
    {
      public ContextBranchName()
      {
      }

      @Override
      protected String convertValue(Object object)
      {
        return ((CDOBranch)object).getName();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class ContextBranchPath extends ContextBranchID
    {
      public ContextBranchPath()
      {
      }

      @Override
      protected String convertValue(Object object)
      {
        return ((CDOBranch)object).getPathName();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class ContextBranchMain extends ValueMatcher
    {
      public ContextBranchMain()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return securityContext.getBranch().isMainBranch();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class ContextTimeStamp extends ValueMatcher
    {
      public ContextTimeStamp()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return securityContext.getBranch().getTimeStamp();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class ContextHistorical extends ValueMatcher
    {
      public ContextHistorical()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return securityContext.getBranch().getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionID extends ValueMatcher
    {
      public RevisionID()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return revision.getID();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionInstanceOf extends RevisionClass
    {
      public RevisionInstanceOf()
      {
        setSubClasses(true);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionClass extends ValueMatcher
    {
      public RevisionClass()
      {
      }

      @InjectAttribute(name = "subClasses")
      public final void setSubClasses(boolean subClasses)
      {
        setRecursive(subClasses);
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return revision.getEClass();
      }

      @Override
      protected String convertValue(Object object)
      {
        return EcoreUtil.getURI((EClass)object).toString();
      }

      @Override
      protected Object computeSuccessor(Object object)
      {
        EList<EClass> superTypes = ((EClass)object).getESuperTypes();
        return superTypes.isEmpty() ? null : superTypes;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionClassName extends RevisionClass
    {
      public RevisionClassName()
      {
      }

      @Override
      protected String convertValue(Object object)
      {
        return ((EClass)object).getName();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionPackage extends ValueMatcher
    {
      public RevisionPackage()
      {
      }

      @InjectAttribute(name = "subPackages")
      public final void setSubPackages(boolean subPackages)
      {
        setRecursive(subPackages);
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return revision.getEClass().getEPackage();
      }

      @Override
      protected String convertValue(Object object)
      {
        return ((EPackage)object).getNsURI();
      }

      @Override
      protected Object computeSuccessor(Object object)
      {
        EObject container = ((EPackage)object).eContainer();
        return container instanceof EPackage ? container : null;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionPackageName extends RevisionPackage
    {
      public RevisionPackageName()
      {
      }

      @Override
      protected String convertValue(Object object)
      {
        return ((EPackage)object).getName();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionBranchID extends ValueMatcher
    {
      public RevisionBranchID()
      {
      }

      @InjectAttribute(name = "subBranches")
      public final void setSubBranches(boolean subBranches)
      {
        setRecursive(subBranches);
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return revision.getBranch();
      }

      @Override
      protected String convertValue(Object object)
      {
        return Integer.toString(((CDOBranch)object).getID());
      }

      @Override
      protected Object computeSuccessor(Object object)
      {
        CDOBranch branch = (CDOBranch)object;
        return branch.isMainBranch() ? null : branch.getBranch();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionBranchName extends RevisionBranchID
    {
      public RevisionBranchName()
      {
      }

      @Override
      protected String convertValue(Object object)
      {
        return ((CDOBranch)object).getName();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionBranchPath extends RevisionBranchID
    {
      public RevisionBranchPath()
      {
      }

      @Override
      protected String convertValue(Object object)
      {
        return ((CDOBranch)object).getPathName();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionBranchMain extends ValueMatcher
    {
      public RevisionBranchMain()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return revision.getBranch().isMainBranch();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionTimeStamp extends ValueMatcher
    {
      public RevisionTimeStamp()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return revision.getTimeStamp();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionRevised extends ValueMatcher
    {
      public RevisionRevised()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return revision.getRevised();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class LDAPUserDN extends ValueMatcher
    {
      public LDAPUserDN()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return ((LDAPUserInfo)userInfo).userDN();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class LDAPGroupDN extends ValueMatcher
    {
      public LDAPGroupDN()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        return ((LDAPUserInfo)userInfo).groupDNs();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class KeyValueMatcher extends ValueMatcher
  {
    private String key;

    public KeyValueMatcher()
    {
    }

    public final String getKey()
    {
      return key;
    }

    @InjectAttribute(name = "key")
    public final void setKey(String key)
    {
      checkInactive();
      this.key = key;
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      super.doBeforeActivate();
      checkState(key, "key"); //$NON-NLS-1$
    }

    /**
     * @author Eike Stepper
     */
    public static class SessionProperty extends KeyValueMatcher
    {
      public SessionProperty()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        IRegistry<String, Object> properties = session.properties();
        return properties.get(getKey());
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RepositoryProperty extends KeyValueMatcher
    {
      public RepositoryProperty()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        IRegistry<String, Object> properties = session.getRepository().properties();
        return properties.get(getKey());
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class RevisionFeature extends KeyValueMatcher
    {
      private RevisionInstanceOf instanceOfMatcher;

      public RevisionFeature()
      {
      }

      public final String getInstanceOf()
      {
        return instanceOfMatcher == null ? null : instanceOfMatcher.getValue();
      }

      @InjectAttribute(name = "instanceOf")
      public final void setInstanceOf(String instanceOf)
      {
        checkInactive();
        if (StringUtil.isEmpty(instanceOf))
        {
          instanceOfMatcher = null;
        }
        else
        {
          instanceOfMatcher = new RevisionInstanceOf();
          instanceOfMatcher.setValue(instanceOf);
        }
      }

      @Override
      public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
      {
        if (!super.matches(session, userInfo, securityContext, revisionProvider, revision))
        {
          return false;
        }

        if (instanceOfMatcher != null && !instanceOfMatcher.matches(session, userInfo, securityContext, revisionProvider, revision))
        {
          return false;
        }

        return true;
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        EStructuralFeature feature = revision.getEClass().getEStructuralFeature(getKey());
        if (feature == null)
        {
          return null;
        }

        return ((InternalCDORevision)revision).getValue(feature);
      }

      @Override
      protected void doActivate() throws Exception
      {
        super.doActivate();
        LifecycleUtil.activate(instanceOfMatcher);
      }

      @Override
      protected void doDeactivate() throws Exception
      {
        LifecycleUtil.deactivate(instanceOfMatcher);
        super.doDeactivate();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class LDAPUserAttribute extends KeyValueMatcher
    {
      private LDAPUserAuthenticator ldapAuthenticator;

      public LDAPUserAttribute()
      {
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision)
      {
        LDAPDN userDN = ((LDAPUserInfo)userInfo).userDN();

        LDAPEntry entry = ldapAuthenticator.ldapEntry(userDN);
        if (entry == null)
        {
          return null;
        }

        Attribute attribute = entry.attributes().get(getKey());
        if (attribute == null)
        {
          return null;
        }

        NamingEnumeration<?> enumeration;

        try
        {
          enumeration = attribute.getAll();
        }
        catch (NamingException ex)
        {
          OM.LOG.error(ex);
          return null;
        }

        Iterator<String> iterator = new Iterator<String>()
        {
          @Override
          public boolean hasNext()
          {
            try
            {
              return enumeration.hasMore();
            }
            catch (NamingException ex)
            {
              OM.LOG.error(ex);
              return false;
            }
          }

          @Override
          public String next()
          {
            try
            {
              Object next = enumeration.next();
              return next == null ? null : next.toString();
            }
            catch (NamingException ex)
            {
              OM.LOG.error(ex);
              return null;
            }
          }
        };

        return new Iterable<String>()
        {
          @Override
          public Iterator<String> iterator()
          {
            return iterator;
          }
        };
      }

      @Override
      protected void doActivate() throws Exception
      {
        ldapAuthenticator = (LDAPUserAuthenticator)getRevisionAuthorizer().getRepositoryProtector().getUserAuthenticator();
      }

      @Override
      protected void doDeactivate() throws Exception
      {
        ldapAuthenticator = null;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class LDAPGroupAttribute extends KeyValueMatcher
    {
      private LDAPUserAuthenticator ldapAuthenticator;

      public LDAPGroupAttribute()
      {
      }

      @Override
      public boolean matches(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider, CDORevision revision)
      {
        StringTester test = getTest();
        String value = getValue();
        String key = getKey();

        Set<LDAPDN> groupDNs = ((LDAPUserInfo)userInfo).groupDNs();

        for (LDAPDN groupDN : groupDNs)
        {
          LDAPEntry entry = ldapAuthenticator.ldapEntry(groupDN);
          if (entry != null)
          {
            Attribute attribute = entry.attributes().get(key);
            if (attribute != null)
            {
              try
              {
                NamingEnumeration<?> enumeration = attribute.getAll();
                while (enumeration.hasMore())
                {
                  Object extractedValue = enumeration.next();
                  if (extractedValue != null && test.test(extractedValue.toString(), value))
                  {
                    return true;
                  }
                }
              }
              catch (NamingException ex)
              {
                OM.LOG.error(ex);
              }
            }
          }
        }

        return false;
      }

      @Override
      protected Object extractValue(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
          CDORevision revision) throws Exception
      {
        throw new UnsupportedOperationException();
      }

      @Override
      protected void doActivate() throws Exception
      {
        ldapAuthenticator = (LDAPUserAuthenticator)getRevisionAuthorizer().getRepositoryProtector().getUserAuthenticator();
      }

      @Override
      protected void doDeactivate() throws Exception
      {
        ldapAuthenticator = null;
      }
    }
  }
}
