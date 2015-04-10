package com.cloudsoftcorp.mule.modules.brooklyn.datasense;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.mule.api.annotations.MetaDataKeyRetriever;
import org.mule.api.annotations.MetaDataRetriever;
import org.mule.api.annotations.components.MetaDataCategory;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.builder.DefaultMetaDataBuilder;

import brooklyn.rest.domain.CatalogItemSummary;

import com.cloudsoftcorp.mule.modules.brooklyn.BrooklynConnector;

@MetaDataCategory
public  class ApplicationCatalogDatasense {

	
	@Inject
    private BrooklynConnector connector;
	
	public BrooklynConnector getConnector() {
		return connector;
	}

	public void setConnector(BrooklynConnector connector) {
		this.connector = connector;
	}

	@MetaDataRetriever
    public MetaData describeEntity(MetaDataKey entityKey) throws Exception {
        MetaDataModel authorModel =  new DefaultMetaDataBuilder().createPojo(Integer.class).build();
        return new DefaultMetaData(authorModel);	
        
	}
	
	protected List<CatalogItemSummary> getItemsFromCatalog() {
		return connector.getPoliciesCatalog("", "");
	}
	
	@MetaDataKeyRetriever
    public List<MetaDataKey> getEntities() throws Exception {
        List<MetaDataKey> entities = new ArrayList<MetaDataKey>();
        List<CatalogItemSummary> listCatalog = getItemsFromCatalog();
        for(CatalogItemSummary item: listCatalog){
        	entities.add(new DefaultMetaDataKey(item.getId(), item.getName()));
        }
        return entities;
    }
}
