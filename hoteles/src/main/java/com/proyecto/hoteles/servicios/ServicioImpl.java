package com.proyecto.hoteles.servicios;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.repositorios.HostRepository;


@Service
public class ServicioImpl{

	@Autowired
	private HostRepository hostRepository;
	

	public Huesped updateHostByFields(long id, Map<String, Object> fields){
		Optional<Huesped> optHost = hostRepository.findById(id);

		if(optHost.isPresent()){
			fields.forEach((key, value) -> {
				Field field = ReflectionUtils.findField(Huesped.class, key);
				field.setAccessible(true);
				ReflectionUtils.setField(field, optHost.get(), value);
			} );
			return hostRepository.save(optHost.get());
		}else{
			return null;
		}
 	} 
	
	/*@Override
	public List<Hotel> findAll() throws Exception {
		List<Hotel> list = this.modelRepository.findAll();
		return list;
	}
	
	@Override
	public Hotel findById(Long id) throws Exception {
		return this.modelRepository.findById(id).get();
	}

	@Override
	public Hotel save(Hotel entity) throws Exception {
		Service make = makeService.findById(entity.getMake().getId());
		entity.setMake(make);
		
		entity = this.modelRepository.save(entity);
		return entity;
	}

	@Override
	public Hotel update(Hotel entity) throws Exception {
		Service make = makeService.findById(entity.getMake().getId());
		entity.setMake(make);
		
		entity = this.modelRepository.save(entity);
		
		return entity;
	}

	@Override
	public void delete(Long id) throws Exception {
		this.modelRepository.deleteById(id);
	}
	
	public void softDelete(Long id) throws Exception {
		Hotel model = this.findById(id);
		model.setDeleted(true);
	}
	
	public List<Hotel> findBySearchCriteriaAndOrderCriteria(DynamicSearchDto dynamicSearch) throws Exception {
		GenericSpecification<Hotel> specification = new GenericSpecification();
		specification.setSearch(dynamicSearch.getListSearchCriteria());

		List<Hotel> result = (List<Hotel>) modelRepository.findAll((specification),
				getSort(dynamicSearch.getListOrderCriteria()));

		return result;
	}
	
	protected Sort getSort(List<OrderCriteria> ocl) throws RuntimeException {
		List<Order> orders = new ArrayList<Order>();
		ocl.forEach(oc -> {
			try {
				orders.add(new Order(Direction.fromString(oc.getValuesortOrder()), oc.getSortBy()));
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Error al ordenar");
			}
		});
		return Sort.by(orders);
	}
	
	public Page<Hotel> findPageBySearchCriteriaAndOrderCriteria(DynamicSearchPaginatorDto dynamicSearchPaginator) throws Exception {
		GenericSpecification<Hotel> specification = new GenericSpecification();
		specification.setSearch(dynamicSearchPaginator.getListSearchCriteria());

		Pageable paging = PageRequest.of(dynamicSearchPaginator.getPage().getPageIndex(), 
				dynamicSearchPaginator.getPage().getPageSize(),
				getSort(dynamicSearchPaginator.getListOrderCriteria()));
		
		Page<Hotel> pagedResult = modelRepository.findAll(specification, paging);

		return pagedResult;
		
	}*/
}

