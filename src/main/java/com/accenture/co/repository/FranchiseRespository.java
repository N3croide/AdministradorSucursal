package com.accenture.co.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.accenture.co.domain.model.Franchise;

@Repository
public interface FranchiseRespository extends ReactiveCrudRepository<Franchise, Long> {

}
