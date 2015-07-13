package com.robert.athena.rest.container;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

public abstract class AbstractContainerServer implements ContainerServer {
	protected final Logger log = Logger.getLogger(this.getClass());

	protected int port;
	protected String contextPath;
	protected String webappPath;

	protected int minThreads = -1;
	protected int maxThreads = -1;
	
	protected CountDownLatch latch = new CountDownLatch(1);

	protected ServerStatus serverStatus = ServerStatus.NONE;

	public AbstractContainerServer() {
		super();
	}

	public void start() {
		serverStatus = ServerStatus.STARTING;

		try {
			log.info("Starting container server.");

			doStart();
			serverStatus = ServerStatus.RUNNING;

			log.info("Started container server.");
		} catch (Exception e) {
			log.error("Failed to start container server.", e);

			exit();
		}

		doWait();
	}

	private void doWait() {
		while (serverStatus != ServerStatus.STOPPING) {
			try {
				latch.await();
			} catch (InterruptedException e) {
				log.error(
						"Container thread is interupted. Back to wait. This may be suspecious wakeup.",
						e);
			}
		}
	}

	protected abstract void doStart() throws Exception;

	public void stop() {
		serverStatus = ServerStatus.STOPPING;

		try {
			log.info("Stoping container server.");

			doStop();

			latch.countDown();
			serverStatus = ServerStatus.DIED;

			log.info("Stopped container server.");
		} catch (Exception e) {
			log.error("Failed to stop container server", e);

			exit();
		}
	}

	protected abstract void doStop() throws Exception;

	protected void exit() {
		// Print console to prompt the admin
		System.err.println("Something wrong. Please check logs.");
		System.exit(1);
	}

	public ServerStatus status() {
		return serverStatus;
	}

}