package com.wanda.athena.rest.container;

public interface ContainerServer {
	enum ServerStatus {
		NONE, STARTING, RUNNING, STOPPING, DIED
	};

	public void start();

	public void stop();

	public ServerStatus status();
}
