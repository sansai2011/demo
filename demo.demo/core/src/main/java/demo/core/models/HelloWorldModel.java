/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package demo.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import demo.core.services.StringGenerator;
import lombok.Data;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Session;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

@Data
@Model(adaptables = Resource.class)
@Exporter(name = "jackson", extensions = "json", options = {
        @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true")
})
public class HelloWorldModel {

    @OSGiService
    ResourceResolverFactory resourceResolverFactory;

    @Inject
            @org.apache.sling.models.annotations.Optional
    private StringGenerator stringGenerator;

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @OSGiService
    private SlingSettingsService settings;
    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    private String message;

    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(currentResource))
                .map(Page::getPath).orElse("");

        message = "\tHello World!\n"
            + "\tThis is instance: " + settings.getSlingId() + "\n"
            + "\tResource type is: " + resourceType + "\n"
            + "\tCurrent page is: " + currentPagePath + "\n";
    }

    /*public String getMessage() {
        return message;
    }*/

   public ResourceResolver getServiceResolver() {
        ResourceResolver resourceResolver = null;
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "writeService");
       try {
           resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

       } catch (LoginException e) {
           e.printStackTrace();
       }
       Session session = resourceResolver.adaptTo(Session.class);
       session.getUserID();
       return resourceResolver;
   }

    public ResourceResolver getService() { return stringGenerator.getServiceResolver();}

}
