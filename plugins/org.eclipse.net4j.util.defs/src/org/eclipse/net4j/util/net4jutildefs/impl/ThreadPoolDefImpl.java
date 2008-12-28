/**
 * <copyright>
 * </copyright>
 *
 * $Id: ThreadPoolDefImpl.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.util.net4jutildefs.impl;

import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.net4jutildefs.ThreadPoolDef;

import org.eclipse.emf.ecore.EClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Thread Pool Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ThreadPoolDefImpl extends ExecutorServiceDefImpl implements ThreadPoolDef {

	private static final String THREADGROUP_IDENTIFIER = "net4j";
	private ThreadGroup threadGroup;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ThreadPoolDefImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Net4jUtilDefsPackage.Literals.THREAD_POOL_DEF;
	}

	/**
	 * Gets a executor service instance. The current implementation does not
	 * reuse an instance created in a former call TODO: reuse instances
	 * 
	 * @return the instance
	 * 
	 */
	@Override
	protected Object createInstance() {
		ExecutorService executorService = Executors
				.newCachedThreadPool(new DaemonThreadFactory(
						THREADGROUP_IDENTIFIER));
		return executorService;
	}

	private static class DaemonThreadFactory implements ThreadFactory {
		private ThreadGroup threadGroup;

		public DaemonThreadFactory(String threadGroupIdentifier) {
			super();
			this.threadGroup = new ThreadGroup(threadGroupIdentifier);
		}

		public Thread newThread(Runnable r) {
			Thread thread = new Thread(threadGroup, r);
			thread.setDaemon(true);
			return thread;
		}
	}

} //ThreadPoolDefImpl
