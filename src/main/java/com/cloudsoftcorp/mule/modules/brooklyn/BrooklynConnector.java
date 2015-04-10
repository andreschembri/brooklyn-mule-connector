

package com.cloudsoftcorp.mule.modules.brooklyn;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jboss.resteasy.client.core.BaseClientResponse;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.MetaDataKeyParam;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.CatalogItemSummary;
import brooklyn.rest.domain.EntitySummary;
import brooklyn.rest.domain.SensorSummary;
import brooklyn.rest.domain.TaskSummary;

import com.cloudsoftcorp.mule.module.workaround.EntitySummaryWorkAround;
import com.cloudsoftcorp.mule.modules.brooklyn.datasense.ApplicationCatalogDatasense;
import com.cloudsoftcorp.mule.modules.brooklyn.datasense.EntitiesCatalogDatasense;
import com.cloudsoftcorp.mule.modules.brooklyn.datasense.PolicyCatalogDatasense;
import com.cloudsoftcorp.mule.modules.brooklyn.strategy.ConnectorConnectionStrategy;

/**
 * Anypoint Connector for Brooklyn
 *
 * @author Andre Schembri
 */
@Connector(name = "brooklyn", friendlyName = "Brooklyn")
public class BrooklynConnector {

	@ConnectionStrategy
	ConnectorConnectionStrategy connectionStrategy;

	public ConnectorConnectionStrategy getConnectionStrategy() {
		return connectionStrategy;
	}

	public void setConnectionStrategy(ConnectorConnectionStrategy connectionStrategy) {
		this.connectionStrategy = connectionStrategy;
	}

	BrooklynApi brooklynApi;

	public BrooklynApi getBrooklynApi() {
		return brooklynApi;
	}

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
	 * brooklyn:create-application}
	 *
	 * @return brooklyn.rest.domain.TaskSummary
	 */
	@Processor
	public TaskSummary createApplication(String yaml) {
		@SuppressWarnings("unchecked")
		BaseClientResponse<TaskSummary> response = (BaseClientResponse<TaskSummary>) brooklynApi.getApplicationApi().createFromYaml(yaml);
		return response.getEntity(brooklyn.rest.domain.TaskSummary.class);
	}

	/**
	 * Fetch list of applications, as ApplicationSummary objects
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-applications}
	 *
	 * @return java.util.List<ApplicationSummary>
	 */
	@Processor
	public List<ApplicationSummary> getApplications() {
		return brooklynApi.getApplicationApi().list();
	}

	/**
	 * Fetch entity info for all (or filtered) descendants
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-application-descendant-entities}
	 *
	 * @return java.util.List<EntitySummary>
	 */
	@Processor
	public List<EntitySummary> getApplicationDescendantEntities(
			@Default("#[mesage.payload.applicationId]") String applicationId,
			@Default("#[message.payload.regex]") String regex) {
		List<EntitySummary> listOfEntities = brooklynApi.getApplicationApi().getDescendants(applicationId, regex);
		
		return listOfEntities;
	}

	/**
	 * Fetch a specific application
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-application}
	 *
	 * @return brooklyn.rest.domain.ApplicationSummary
	 */
	@Processor
	public ApplicationSummary getApplication(
			@Default("#[message.payload]") String applicationId) {
		return brooklynApi.getApplicationApi().get(applicationId);
	}

	/**
	 * Delete a specified application
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:delete-application}
	 *
	 * @return brooklyn.rest.domain.TaskSummary
	 */
	@Processor
	public TaskSummary deleteApplication(
			@Default("#[message.payload]") String applicationId) {
		@SuppressWarnings("unchecked")
		BaseClientResponse<TaskSummary> response = (BaseClientResponse<TaskSummary>) brooklynApi.getApplicationApi().delete(applicationId);		
		return response.getEntity(TaskSummary.class);
	}

	/*
	 * Entities
	 */

	/**
	 * Fetch the list of entities for a given application
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-entities}
	 *
	 * @return java.util.List<EntitySummary>
	 */
	@Processor
	public List<EntitySummary> getEntities(
			@Default("#[message.payload]") String applicationId) {
		return brooklynApi.getEntityApi().list(applicationId);
	}

	/*
	 * Entity Sensors
	 */
	/**
	 * Fetch the sensor list for a specific application entity
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-sensors}
	 *
	 * @return java.util.List<SensorSummary>
	 */
	@Processor
	public List<SensorSummary> getSensors(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId) {
		return brooklynApi.getSensorApi().list(applicationId, entityId);
	}

	/**
	 * Fetch sensor value
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-sensor}
	 *
	 * @return java.util.List<SensorSummary>
	 */
	@Processor
	public SensorSummary getSensor(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId,
			@Default("#[message.payload.sensorId]") String sensorId) {
		return (SensorSummary) brooklynApi.getSensorApi().get(applicationId,
				entityId, sensorId, false);
	}
	
	/**
	 * Fetch sensor raw value
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-sensor-raw-data}
	 *
	 * @return java.lang.Object
	 */
	@Processor
	public Object getSensorRawData(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId,
			@Default("#[message.payload.sensorId]") String sensorId) {
		return brooklynApi.getSensorApi().get(applicationId, entityId,sensorId, true);
	}

	/**
	 * Manually clear a sensor value
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:delete-sensor}
	 *
	 */
	@Processor
	public void deleteSensor(
			@Default("#[message.payload.applicationId]") String applicationId,
			@Default("#[message.payload.entityId]") String entityId,
			@Default("#[message.payload.sensorId]") String sensorId) {
		brooklynApi.getSensorApi().delete(applicationId, entityId, sensorId);
	}

	/**
	 * Manually set a sensor value
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:create-sensor}
	 *
	 */
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
	
	/**
	 * Fetch a list of application templates optionally matching a query
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-application-catalog}
	 *
	 * @return java.util.List<CatalogItemSummary>
	 */
	@Processor
	public List<CatalogItemSummary> getApplicationCatalog(
			@Default("#[message.payload.regularExpression]") String regularExpression,
			@Default("#[message.payload.fragement]") String fragement) {
		return brooklynApi.getCatalogApi().listApplications(regularExpression,
				fragement);
	}

	/**
	 * Fetch an application's definition from the catalog
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-application-from-catalog}
	 *
	 * @return java.util.List<CatalogItemSummary>
	 */
	@Processor
	@MetaDataScope(ApplicationCatalogDatasense.class)
	public CatalogItemSummary getApplicationFromCatalog(
			@MetaDataKeyParam String applicationId)
			throws Exception {
		return brooklynApi.getCatalogApi().getApplication(applicationId);
	}

	/**
	 * List available policies optionally matching a query
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-policies-catalog}
	 *
	 * @return java.util.List<CatalogItemSummary>
	 */
	@Processor
	public List<CatalogItemSummary> getPoliciesCatalog(
			@Default("#[message.payload.regularExpression]") String regularExpression,
			@Default("#[message.payload.fragement]") String fragement) {
		return brooklynApi.getCatalogApi().listPolicies(regularExpression,fragement);
	}

	/**
	 * Fetch a policy's definition from the catalog
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-policy-from-catalog}
	 *
	 * @return brooklyn.rest.domain.CatalogItemSummary
	 */
	@MetaDataScope(PolicyCatalogDatasense.class)
	@Processor
	public CatalogItemSummary getPolicyFromCatalog(
			@MetaDataKeyParam String policyId)
			throws Exception {
		return brooklynApi.getCatalogApi().getPolicy(policyId);
	}

	/**
	 * List available entity types optionally matching a query
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-entities-catalog}
	 *
	 * @return java.util.List<CatalogItemSummary>
	 */
	@Processor
	public List<CatalogItemSummary> getEntitiesCatalog(
			@Default("#[message.payload.regularExpression]") String regularExpression,
			@Default("#[message.payload.fragement]") String fragement) {
		return brooklynApi.getCatalogApi().listEntities(regularExpression, fragement);
	}

	
	
	
	/**
	 * Fetch an entity's definition from the catalog
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:get-entity-from-catalog}
	 *
	 * @return brooklyn.rest.domain.CatalogItemSummary
	 */
	@MetaDataScope(EntitiesCatalogDatasense.class)
	@Processor 
	public CatalogItemSummary getEntityFromCatalog(
			 @MetaDataKeyParam String entityId)
			throws Exception {
		return brooklynApi.getCatalogApi().getEntity(entityId);
	}
	
	

	/**
	 * Add a catalog item (e.g. new entity or policy type) by uploading YAML descriptor
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:add-item-to-catalog}
	 *
	 * @return brooklyn.rest.domain.TaskSummary
	 */
	@Processor
	public TaskSummary addItemToCatalog(@Default("#[message.payload]") String yaml) {
		@SuppressWarnings("unchecked")
		BaseClientResponse<TaskSummary> response = (BaseClientResponse<TaskSummary>) brooklynApi.getCatalogApi().create(yaml);		
		return response.getEntity(TaskSummary.class);
	}

	/**
	 * Deletes an entity's definition from the catalog
	 *
	 * {@sample.xml ../../../doc/brooklyn-connector.xml.sample
	 * brooklyn:delete-catalog-entry}
	 *
	 * @return brooklyn.rest.domain.TaskSummary
	 */
	@Processor
	public void deleteCatalogEntry(
			@Default("#[message.payload.entryId]") String entryId)
			throws Exception {
		brooklynApi.getCatalogApi().deleteEntity(entryId);
	}

}