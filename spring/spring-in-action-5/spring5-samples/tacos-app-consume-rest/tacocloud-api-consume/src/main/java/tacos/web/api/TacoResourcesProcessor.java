package tacos.web.api;

import org.springframework.hateoas.*;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.stereotype.Component;
import tacos.Taco;

@Component
public class TacoResourcesProcessor {
        //implements ResourceProcessor<PagedModel<EntityModel<Taco>>> {

  private final EntityLinks entityLinks;
  
  public TacoResourcesProcessor(EntityLinks entityLinks) {
    this.entityLinks = entityLinks;
  }
  
  public PagedModel<EntityModel<Taco>> process(PagedModel<EntityModel<Taco>> resources) {
    resources
      .add(entityLinks
          .linkFor(Taco.class)
          .slash("recent")
          .withRel("recents"));
    
    return resources;
  }

}
