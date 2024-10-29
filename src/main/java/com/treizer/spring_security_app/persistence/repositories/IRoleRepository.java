package com.treizer.spring_security_app.persistence.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.treizer.spring_security_app.persistence.entities.RoleEntity;

@Repository
public interface IRoleRepository extends CrudRepository<RoleEntity, Long> {

    public List<RoleEntity> findRoleEntitiesByRoleNameIn(List<String> roles);

}
