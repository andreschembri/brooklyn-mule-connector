/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package com.cloudsoftcorp.mule.modules.brooklyn;

import java.util.List;

import javax.ws.rs.core.Response;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Default;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.EntitySummary;
import brooklyn.rest.domain.SensorSummary;

import com.cloudsoftcorp.mule.modules.brooklyn.strategy.ConnectorConnectionStrategy;

/**
 * Anypoint Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name = "brooklyn", friendlyName = "Brooklyn")
public class BrooklynConnector {

	@ConnectionStrategy
	ConnectorConnectionStrategy connectionStrategy;

	
	public ConnectorConnectionStrategy getConnectionStrategy() {
		return connectionStrategy;
	}

	public void setConnectionStrategy(
			ConnectorConnectionStrategy connectionStrategy) {
		this.connectionStrategy = connectionStrategy;
	}
	
	
	
	
	
	/**
	* Application API 
	* 
	*/
	
	
	/**
	 * Create new application from policy
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-applications}
	 *
	 * @return jax-rs response object
	 */
	@Processor
	public Integer createApplication(String yaml) {
		BrooklynApi brooklynAPI = connectionStrategy.getBrooklynAPI();
		return brooklynAPI.getApplicationApi().createFromYaml(yaml).getStatus();
	}

	/**
	 * List all applications
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-applications}
	 *
	 * @return brooklyn.rest.domain.ApplicationSummary
	 */
	@Processor
	public List<ApplicationSummary> getApplications() {
		BrooklynApi brooklynAPI = connectionStrategy.getBrooklynAPI();
		return brooklynAPI.getApplicationApi().list();
	}

	/**
	 * List all applications
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-applications}
	 *
	 * @return brooklyn.rest.domain.ApplicationSummary
	 */
	@Processor
	public ApplicationSummary getApplication(
			@Default("#[message.payload]") String applicationId) {
		BrooklynApi brooklynAPI = connectionStrategy.getBrooklynAPI();
		return brooklynAPI.getApplicationApi().get(applicationId);
	}
	
	@Processor
	public Integer deleteApplication(@Default("#[message,payload]") String applicationId) {
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return  brooklynApi.getApplicationApi().delete(applicationId).getStatus();
	}

	
	
	/*
	 * Entities 
	 */
	
	@Processor
	public List<EntitySummary> getEntities(@Default("#[message.payload]") String applicationId){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getEntityApi().list(applicationId);
	} 
	
	
	/*
	 * Entity Sensors
	 */
	public List<SensorSummary> getSensorSummary(@Default("#[message.payload.applicationId]") String applicationId,@Default("#[message.payload.entityId]") String entityId ){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getSensorApi().list(applicationId, entityId);
	}
	 
}