/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package com.cloudsoftcorp.mule.modules.brooklyn;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.CatalogItemSummary;
import brooklyn.rest.domain.EntitySummary;
import brooklyn.rest.domain.SensorSummary;

import com.cloudsoftcorp.mule.module.workaround.EntitySummaryWorkAround;
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

	BrooklynApi brooklynApi;

	@PostConstruct
	public void init() {
		brooklynApi = connectionStrategy.getBrooklynAPI();
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
		return brooklynApi.getApplicationApi().createFromYaml(yaml).getStatus();
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
		return brooklynApi.getApplicationApi().list();
	}

	@Processor
	public List<EntitySummary> getApplicationDescendantEntities(
			@Default("#[mesage.payload.applicationId]") String applicationId,
			@Default("#[message.payload.regex]") String regex) {
		List<EntitySummary> listOfEntities = brooklynApi.getApplicationApi()
				.getDescendants(applicationId, regex);
		List<EntitySummary> serializableEntities = new ArrayList<EntitySummary>();
		for (EntitySummary entity : listOfEntities) {
			serializableEntities.add(new EntitySummaryWorkAround(entity));
		}
		return serializableEntities;
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
		return brooklynApi.getApplicationApi().get(applicationId);
	}

	@Processor
	public Integer deleteApplication(
			@Default("#[message,payload]") String applicationId) {
		return brooklynApi.getApplicationApi().delete(applicationId)
				.getStatus();
	}

	/*
	 * Entities
	 */

	@Processor
	public List<EntitySummary> getEntities(
			@Default("#[message.payload]") String applicationId) {
		return brooklynApi.getEntityApi().list(applicationId);
	}

	/*
	 * Entity Sensors
	 */
	@Processor
	public List<SensorSummary> getSensors(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId) {
		return brooklynApi.getSensorApi().list(applicationId, entityId);
	}

	@Processor
	public SensorSummary getSensor(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId,
			@Default("#[message.payload.sensorId]") String sensorId) {
		return (SensorSummary) brooklynApi.getSensorApi().get(applicationId,
				entityId, sensorId, false);
	}

	@Processor
	public Object getSensorRawData(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId,
			@Default("#[message.payload.sensorId]") String sensorId) {
		return brooklynApi.getSensorApi().get(applicationId, entityId,
				sensorId, true);
	}

	@Processor
	public void deleteSensor(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId,
			@Default("#[message.payload.sensorId]") String sensorId) {
		brooklynApi.getSensorApi().delete(applicationId, entityId, sensorId);
	}

	@Processor
	public void createSensor(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId,
			@Default("#[message.payload.sensorId]") String sensorId,
			@Default("#[message.payload.sensorConfiguration]") Object sensorConfiguration) {
		brooklynApi.getSensorApi().set(applicationId, entityId, sensorId,
				sensorConfiguration);
	}

	/*
	 * Catalog
	 */
	@Processor
	public List<CatalogItemSummary> getApplicationCatalog(
			@Default("#[message.payload.regularExpression]") String regularExpression,
			@Default("#[message.payload.fragement]") String fragement) {
		return brooklynApi.getCatalogApi().listApplications(regularExpression,
				fragement);
	}

	@Processor
	public CatalogItemSummary getApplicationFromCatalog(
			@Default("#[message.payload.policyId]") String applicationId,
			@Default("#[message.payload.versionId]") String versionId)
			throws Exception {
		return brooklynApi.getCatalogApi().getApplication(applicationId,
				versionId);
	}

	@Processor
	public List<CatalogItemSummary> getPoliciesCatalog(
			@Default("#[message.payload.regularExpression]") String regularExpression,
			@Default("#[message.payload.fragement]") String fragement) {
		return brooklynApi.getCatalogApi().listPolicies(regularExpression,
				fragement);
	}

	@Processor
	public CatalogItemSummary getPolicyFromCatalog(
			@Default("#[message.payload.policyId]") String policyId,
			@Default("#[message.payload.versionId]") String versionId)
			throws Exception {
		return brooklynApi.getCatalogApi().getPolicy(policyId, versionId);
	}

	@Processor
	public List<CatalogItemSummary> getEntitiesCatalog(
			@Default("#[message.payload.regularExpression]") String regularExpression,
			@Default("#[message.payload.fragement]") String fragement) {
		return brooklynApi.getCatalogApi().listEntities(regularExpression,
				fragement);
	}

	@Processor
	public CatalogItemSummary getEntityFromCatalog(
			@Default("#[message.payload.policyId]") String entityId,
			@Default("#[message.payload.versionId]") String versionId)
			throws Exception {
		return brooklynApi.getCatalogApi().getApplication(entityId, versionId);
	}

	@Processor
	public int addItemToCatalog(@Default("#[message.payload]") String yaml) {
		return brooklynApi.getCatalogApi().create(yaml).getStatus();
	}

	@Processor
	public void deleteEntry(
			@Default("#[message.payload.entryId]") String entryId,
			@Default("#[message.payload.versionId]") String versionId)
			throws Exception {
		brooklynApi.getCatalogApi().deleteEntity(entryId, versionId);
	}

}