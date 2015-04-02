package com.cloudsoftcorp.mule.module.workaround;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import brooklyn.rest.domain.EntitySummary;

public class EntitySummaryWorkAround extends EntitySummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 744364167011233146L;

	public EntitySummaryWorkAround(String id, String name, String type,
			String catalogItemId, Map<String, URI> links) {
		super(id, name, type, catalogItemId, links);

	}
	
	public EntitySummaryWorkAround(EntitySummary entitySummary){
		super(entitySummary.getId(), entitySummary.getName(), entitySummary.getType(), entitySummary.getCatalogItemId(), entitySummary.getLinks());
	}

}
