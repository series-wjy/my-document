package tacos.web.api;


import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import tacos.Taco;

public class TacoResourceAssembler
       extends RepresentationModelAssemblerSupport<Taco, TacoResource> {

  public TacoResourceAssembler() {
    super(DesignTacoController.class, TacoResource.class);
  }

  @Override
  public TacoResource toModel(Taco entity) {
    TacoResource resource = createModelWithId(entity.getId(), entity);
    return resource;
  }
}
