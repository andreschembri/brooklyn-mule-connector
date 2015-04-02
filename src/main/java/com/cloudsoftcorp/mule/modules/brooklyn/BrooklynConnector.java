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
import org.mule.api.annotations.param.Optional;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.CatalogItemSummary;
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
	public List<SensorSummary> getSensors(@Default("#[message.payload.applicationId]") String applicationId,@Default("#[message.payload.entityId]") String entityId ){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getSensorApi().list(applicationId, entityId);
	}
	
	public SensorSummary getSensor(@Default("#[message.payload.applicationId]") String applicationId,@Default("#[message.payload.entityId]") String entityId, @Default("#[message.payload.sensorId]") String sensorId){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return (SensorSummary) brooklynApi.getSensorApi().get(applicationId, entityId, sensorId, false);
	}
	 
	public Object getSensorRawData(@Default("#[message.payload.applicationId]") String applicationId,@Default("#[message.payload.entityId]") String entityId, @Default("#[message.payload.sensorId]") String sensorId){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getSensorApi().get(applicationId, entityId, sensorId, true);
	}
	
	public void deleteSensor(@Default("#[message.payload.applicationId]") String applicationId,@Default("#[message.payload.entityId]") String entityId, @Default("#[message.payload.sensorId]") String sensorId){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		brooklynApi.getSensorApi().delete(applicationId, entityId, sensorId);
	}
	
	public void createSensor(@Default("#[message.payload.applicationId]") String applicationId,@Default("#[message.payload.entityId]") String entityId, @Default("#[message.payload.sensorId]") String sensorId, @Default("#[message.payload.sensorConfiguration]") Object sensorConfiguration){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		brooklynApi.getSensorApi().set(applicationId, entityId, sensorId, sensorConfiguration);
	}
	
	
	/*
	 * Catalog
	 */
	
	public List<CatalogItemSummary> getApplicationCatalog(@Default("#[message.payload.regularExpression]") String regularExpression, @Default("#[message.payload.fragement]") String fragement ){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getCatalogApi().listApplications(regularExpression, fragement);
	}
	
	public CatalogItemSummary getApplicationFromCatalog(@Default("#[message.payload.policyId]") String applicationId, @Default("#[message.payload.versionId]") String versionId) throws Exception{
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getCatalogApi().getApplication(applicationId, versionId);
	}
	
	public List<CatalogItemSummary> getPoliciesCatalog(@Default("#[message.payload.regularExpression]") String regularExpression, @Default("#[message.payload.fragement]") String fragement ){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getCatalogApi().listPolicies(regularExpression, fragement);
	}
	
	public CatalogItemSummary getPolicyFromCatalog(@Default("#[message.payload.policyId]") String policyId, @Default("#[message.payload.versionId]") String versionId) throws Exception{
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getCatalogApi().getPolicy(policyId, versionId);
	}
	
	public List<CatalogItemSummary> getEntitiesCatalog(@Default("#[message.payload.regularExpression]") String regularExpression, @Default("#[message.payload.fragement]") String fragement ){
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getCatalogApi().listEntities(regularExpression, fragement);
	}
	
	public CatalogItemSummary getEntityFromCatalog(@Default("#[message.payload.policyId]") String entityId, @Default("#[message.payload.versionId]") String versionId) throws Exception{
		BrooklynApi brooklynApi = connectionStrategy.getBrooklynAPI();
		return brooklynApi.getCatalogApi().getApplication(entityId, versionId);
	}
	

	
	
}